/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2019 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.zrp200.lustrouspixeldungeon.items.weapon.missiles;

import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.zrp200.lustrouspixeldungeon.Assets;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Actor;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.hero.HeroAction;
import com.zrp200.lustrouspixeldungeon.items.Heap;
import com.zrp200.lustrouspixeldungeon.items.Item;
import com.zrp200.lustrouspixeldungeon.items.bags.Bag;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Projecting;
import com.zrp200.lustrouspixeldungeon.levels.traps.DisarmingTrap;
import com.zrp200.lustrouspixeldungeon.levels.traps.DisintegrationTrap;
import com.zrp200.lustrouspixeldungeon.levels.traps.ExplosiveTrap;
import com.zrp200.lustrouspixeldungeon.levels.traps.TeleportationTrap;
import com.zrp200.lustrouspixeldungeon.levels.traps.Trap;
import com.zrp200.lustrouspixeldungeon.mechanics.Ballistica;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSpriteSheet;
import com.zrp200.lustrouspixeldungeon.sprites.MissileSprite;
import com.zrp200.lustrouspixeldungeon.utils.BArray;

import java.util.ArrayList;

import static com.zrp200.lustrouspixeldungeon.Dungeon.depth;
import static com.zrp200.lustrouspixeldungeon.Dungeon.hero;

public class Boomerang extends MissileWeapon {

	{
		image = ItemSpriteSheet.BOOMERANG;

		tier = 4;
		sticky = false;

		baseUses = 7.5f;
	}

	private Boomerang.Returning returning;
	public Returning returning() {
		return returning;
	}
	public boolean isReturning() {
		return returning != null && returning.isActive();
	}

	private void findParent() {
		if(hero != null) {
			parent = (MissileWeapon) hero.belongings.getSimilar(this);
		}
	}

	@Override
	public boolean collect(Bag container) {
		if(super.collect(container)) {
			terminateFlight();
			return true;
		}
		return false;
	}

	@Override
	public synchronized Heap drop(int pos) {
		terminateFlight();
		return super.drop(pos);
	}

	protected void terminateFlight() {
		if( isReturning() ) {
			returning.detach();
		}
	}

	@Override
	public int maxBase() { // 16 base, down from 20
		return Math.round(super.maxBase()*.8f);
	}

	@Override
	public int maxScale() { // +2/+3 instead of +2/+4
		return Math.max(1,super.maxScale()-1);
	}

	@Override
    public int proc(Char attacker, Char defender, int damage) {
        findParent();
		damage = super.proc(attacker, defender, damage);
        if( isBreaking() ) terminateFlight();
        return damage;
    }

    @Override
	public boolean canSurpriseAttack() {
		return !isReturning();
	}

	@Override
	protected void afterThrow(int cell) {
		if( cell == curUser.pos || isReturning() || isBreaking() ) {
			super.afterThrow(cell);
			return;
		}

		Trap trapAtCell = Dungeon.level.traps.get(cell);
		// TODO implement smooth return
		if(!rangedHit && Actor.findChar(cell) == null && !Dungeon.level.pit[cell])
			if (trapAtCell != null && (trapAtCell instanceof TeleportationTrap || trapAtCell instanceof DisarmingTrap
					|| (trapAtCell instanceof ExplosiveTrap || trapAtCell instanceof DisintegrationTrap)
					&& isDestroyable())) {
				super.afterThrow(cell);
				return; // it's not coming back.
			} else Dungeon.level.press(cell, null, true);
		returnFrom(cell);
	}

	@Override
	public String info() {
		if( isReturning() ) findParent();
		return super.info();
	}

	public Returning returnFrom(int cell) {
	    terminateFlight();
	    detachEmbed();
	    return returning = Buff.append(curUser, Returning.class).set(this,cell);
    }

	@SuppressWarnings("WeakerAccess")
    protected static float returningAcc = .95f;
    @Override
    public float accuracyFactor(Char owner) {
        if( isReturning() ) {
            float rangeBoost = this.rangeBoost, adjacentPenalty = this.adjacentPenalty;
            this.rangeBoost = this.adjacentPenalty = returningAcc; // we don't care about range.

			float accuracyFactor = super.accuracyFactor(owner);

			this.rangeBoost = rangeBoost;
            this.adjacentPenalty = adjacentPenalty;
            return accuracyFactor;
        }
        return super.accuracyFactor(owner);
    }

	public static class Returning extends Buff {

    	public static final double VELOCITY = 0.6;

    	{
			actPriority = BLOB_PRIO; // between hero and mob
		}

		int depthOfOrigin = depth;
		public int pos;
		private int lastPos; public int lastPos() { return lastPos; }

		protected void setPosTo(int newPos) {
			lastPos = pos;
			pos = newPos;
		}


		private static final int PATH_PARAMS = Ballistica.STOP_TARGET | Ballistica.STOP_TERRAIN;
		private int returnDist; // this affects speed of boomerang
		private ArrayList<Integer> path;

		// this finds the return path.
		// if the path is valid in and of itself, returns that. otherwise, tries to autoaim in a 5x5 radius while prioritizing the true target cell
		@SuppressWarnings("UnusedReturnValue")
		private ArrayList<Integer> findPath(int from, int to) {
			// because I hate myself and figure that this is more efficient than looping.
			path = null;
			PathFinder.buildDistanceMap(to, BArray.not(new boolean[PathFinder.distance.length],null), 2);
			pathFinding:
			for( ArrayList<Integer> distance : PathFinder.sortedMap() ) for(int cell : distance) {
				Ballistica trajectory = new Ballistica(from, cell,
						!boomerang.hasEnchant(Projecting.class,Dungeon.hero) ? PATH_PARAMS : Ballistica.STOP_TARGET);
				if(path == null // this makes sure that we don't return null if we don't find anything
						|| trajectory.collisionPos == to) { // this resets the path if there is indeed a better option.
					returnDist = trajectory.dist;
					path = new ArrayList<>( trajectory.subPath(0, returnDist) );
				}
				if(trajectory.collisionPos == to) break pathFinding;
			}
			return path;
		}

		private static final float
				MIN_SPEED = 1,
				TURNS_TO_RETURN = 3f;

		float distancePerTurn;
		private Sprite sprite;
		public Boomerang boomerang;
		boolean willHover;
		boolean hasMoved = true;

		Returning set(Boomerang boomerang, int from) { // use this to set up stuff
			this.boomerang = boomerang;
			findPath(from, target.pos);
			distancePerTurn = Math.max(MIN_SPEED, Dungeon.level.trueDistance(from, hero.pos)/TURNS_TO_RETURN); // don't care if we stop early in the case of speed.
			dest = path.get(path.size()-1);
			willHover = path.size()-1 == returnDist;
			pos = lastPos = path.remove(0);
			hasMoved = false;
			refreshSprite();
			return this;
		}

		public Sprite sprite() {
			if(sprite == null) sprite = new Sprite(); // you'll never get a null from this
			try { target.sprite.parent.add(sprite); } catch (NullPointerException ignored) { /* just give up and move on. it'll get added eventually. */}
			return sprite;
		}
		public void refreshSprite() {
			sprite().reset(pos,pos,boomerang,null);
		}

		private int dest;

		public boolean atDest() {
			return dest == pos;
		}

		@Override
		public void detach() {
			willHover = false;
			boomerang.returning = null;
			sprite().killAndErase();
			super.detach();
			next();
		}

		public int nextPos() {
			if(path != null && !path.isEmpty()) return path.get(0);
			else return pos;
		}

		void instantPickUp() {
			if(target == null) return;
			detach();
			if ( boomerang.collect() ) {
				Sample.INSTANCE.play( Assets.SND_ITEM );
			} else {
				boomerang.drop(pos);
			}
		}

		public Chase heroChase() {
			return new Chase();
		}

		public boolean isActive() {
			return depth == depthOfOrigin;
		}

		boolean killIfNeeded() {
			if(pos == target.pos) {
				instantPickUp();
				return true;
			}
			if( path.isEmpty() ) {
				if(willHover) {
					spend(TICK*1.5f); // hover for a bit
					willHover = false;
				} else boomerang.drop(pos);
				return true;
			}

			return false;
		}

		@Override
		public boolean act() {
			if(!isActive()) { // wait
				spend(TICK);
				return true;
			}
			if( killIfNeeded() ) return true;
			spend(1f/distancePerTurn);

			final Char ch1=findChar(pos);
			if(hasMoved)
				onCharCollision(ch1);
			else
				hasMoved = true;

			final int dest = path.remove(0);
			sprite().reset(dest, new Callback() {
				@Override
				public void call() {
					setPosTo(dest);
					Char ch2 = findChar(pos);
					if(ch1 != ch2) // don't wanna hit the same guy twice
						onCharCollision( ch2 );
					next();
				}
			});
			return false;
		}
		public void onCharCollision(Char ch) {
			if ( ch != null && !killIfNeeded() )
				curUser.shoot(ch, boomerang);
		}

		private static String
				POS = "pos", LAST_POS = "LASTPOS",
				DPT = "distancePerTurn", PATH = "path",
				RANG = "boomerang", DOO = "depthOfOrigin", HOVER="willHover";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);

			bundle.put(POS,pos);
			bundle.put(LAST_POS,lastPos);
			bundle.put(DPT,distancePerTurn);

			int[] path = new int[this.path.size()];
			for(int i=0;i<path.length;i++) path[i] = this.path.get(i);
			bundle.put(PATH,path);

			bundle.put(RANG,boomerang);
			bundle.put(DOO,depthOfOrigin);
			bundle.put(HOVER, willHover);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);

			path = new ArrayList<>();
			for (int pos : bundle.getIntArray(PATH)) path.add(pos);

			pos = bundle.getInt(POS);
			lastPos = bundle.contains(LAST_POS) ? bundle.getInt(LAST_POS) : pos;
			distancePerTurn = bundle.getInt(DPT);
			boomerang = (Boomerang) bundle.get(RANG);
			if(boomerang != null) boomerang.returning = this;
			depthOfOrigin = bundle.getInt(DOO);
			willHover = bundle.getBoolean(HOVER);
		}

		class Sprite extends MissileSprite { // yay multiple inheritance

			Sprite() {
                try {
                    drySetup(worldToCamera(pos),worldToCamera(pos),boomerang);
                } catch(NullPointerException ignored) {}
            }

			public void reset(int to, Callback listener) { reset(pos,to,boomerang,listener); }
			public void reset(Visual to, Callback listener) { reset(pos,to,boomerang,listener); }

			boolean angleSet = false;

			protected void setupAngle(PointF d) {
				if(!angleSet) super.setupAngle(d);
				angleSet = true;
			}

			@Override
			protected void setup(PointF from, PointF to, Item item, Callback listener) {
				super.setup(from, to, item, listener);
				angularSpeed *= VELOCITY;
			}

			@Override
			public synchronized void onComplete(Tweener tweener) {
				super.onComplete(tweener);
				revive(); // this unkills the sprite.
			}
		}
		public class Chase extends HeroAction { // this allows it to be stored.
			// >no multiple inheritance
			public HeroAction getAction() {
				if( Dungeon.hero.buffs( Returning.class ).contains( Returning.this )) return new HeroAction.Move(pos);
				if( Dungeon.level.containsItem(boomerang) ) return new HeroAction.PickUp(pos);
				return null;
			}

		}
	}
}
