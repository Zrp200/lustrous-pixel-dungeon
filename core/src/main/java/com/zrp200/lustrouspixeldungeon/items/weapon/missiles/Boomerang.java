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
import com.watabou.utils.PointF;
import com.zrp200.lustrouspixeldungeon.Assets;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Actor;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.actors.hero.HeroAction;
import com.zrp200.lustrouspixeldungeon.actors.hero.HeroSubClass;
import com.zrp200.lustrouspixeldungeon.items.Heap;
import com.zrp200.lustrouspixeldungeon.items.Item;
import com.zrp200.lustrouspixeldungeon.items.bags.Bag;
import com.zrp200.lustrouspixeldungeon.levels.traps.DisarmingTrap;
import com.zrp200.lustrouspixeldungeon.levels.traps.DisintegrationTrap;
import com.zrp200.lustrouspixeldungeon.levels.traps.ExplosiveTrap;
import com.zrp200.lustrouspixeldungeon.levels.traps.TeleportationTrap;
import com.zrp200.lustrouspixeldungeon.levels.traps.Trap;
import com.zrp200.lustrouspixeldungeon.mechanics.Ballistica;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSpriteSheet;
import com.zrp200.lustrouspixeldungeon.sprites.MissileSprite;

import java.util.ArrayList;

import static com.zrp200.lustrouspixeldungeon.Dungeon.depth;

public class Boomerang extends MissileWeapon {

	{
		image = ItemSpriteSheet.BOOMERANG;

		tier = 3;
		sticky = false;

		baseUses = 7.5f;

		value = 7;
	}

	private Boomerang.Returning returning;
	public Returning returning() {
		return returning;
	}
	public boolean isReturning() {
		return returning != null && returning.isActive();
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

	private void terminateFlight() {
		if(returning != null) {
			returning.detach();
		}
	}

	@Override
	public int min(int lvl) {
		int min;
		try {
			tier--; // one tier lower
			min = super.min(lvl);
		} finally {
			tier++;
		}
		return min;
	}

	@Override
	public int max(int lvl) { // 10 base, scaling by 2 each level.
		int value;
		try {
			tier--;
			value = super.max(lvl);
		} finally {
			tier++;
		}
		return value;
	}

	@Override
	public boolean canSurpriseAttack() {
		return !isReturning();
	}

	protected void onThrowComplete(final int cell) {
		if(cell == curUser.pos) {
			super.onThrowComplete(cell);
			return;
		}

		Trap trapAtCell = Dungeon.level.traps.get(cell);
		// TODO implement smooth return
		if(!rangedHit && Actor.findChar(cell) == null && !Dungeon.level.pit[cell])
			if (trapAtCell != null && (trapAtCell instanceof TeleportationTrap || trapAtCell instanceof DisarmingTrap
					|| (trapAtCell instanceof ExplosiveTrap || trapAtCell instanceof DisintegrationTrap)
					&& isDestroyable())) {
				super.onThrowComplete(cell);
				return; // it's not coming back.
			} else Dungeon.level.press(cell, null, true);
		returning = Buff.append(curUser,Returning.class).set(this,cell);

		parent = null;
		rangedHit = false;
	}

	public static class Returning extends Buff {
		{
			actPriority = BLOB_PRIO; // between hero and mob
		}

		int depthOfOrigin = depth;
		public int pos;
		private int lastPos;
		public int lastPos() { return lastPos; }

		protected void setPosTo(int newPos) {
			lastPos = pos;
			pos = newPos;
		}

		ArrayList<Integer> path;

		private static final float
				MIN_SPEED = 1,
				TURNS_TO_RETURN = 3f;

		float distancePerTurn;
		private Sprite sprite;
		public Boomerang boomerang;
		boolean moving;

		Returning set(Boomerang boomerang, int from) { // use this to set up stuff
			this.boomerang = boomerang;
			Ballistica trajectory = new Ballistica(from,target.pos,Ballistica.STOP_TARGET);
			distancePerTurn = Math.max(MIN_SPEED, trajectory.dist/TURNS_TO_RETURN);
			path = new ArrayList<>(trajectory.subPath(0,trajectory.path.indexOf(trajectory.collisionPos)));
			path.add(path.get(path.size()-1)); // should create a short "hover" I think.
			pos = lastPos = path.remove(0);
			moving = true;
			sprite();
			return this;
		}

		@Override
		public void detach() {
			moving = false;
			boomerang.returning = null;
			super.detach();
		}

		public int nextPos() {
			if(path != null && !path.isEmpty()) return path.get(0);
			else return pos;
		}

		public Sprite sprite() {
			if(sprite == null) sprite = new Sprite(); // you'll never get a null from this
			try { target.sprite.parent.add(sprite); } catch (NullPointerException ignored) { /* just give up and move on. */}
			return sprite;
		}
		public void refreshSprite() {
			sprite().reset(pos,pos,boomerang,null);
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

		@Override
		protected void onRemove() {
			sprite().killAndErase();
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
				if(moving) {
					spend(TICK); // hover for a bit
					moving = false;
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
			final Char ch1;
			if(pos != lastPos) // don't wanna hit the guy we just attacked.
				onCharCollision(ch1=findChar(pos));
			else
				ch1 = null;
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
			if (ch == null || killIfNeeded()) {
				return;
			}

			float 	rangeBoost      = boomerang.rangeBoost,
					adjacentPenalty = boomerang.adjacentPenalty;

			HeroSubClass subClass = ((Hero)target).subClass;
			try {
				((Hero) target).subClass = HeroSubClass.NONE; // nope.
				boomerang.rangeBoost = boomerang.adjacentPenalty = 0.95f; // nullify these, and apply an additional 5% penalty

				if (curUser.shoot(ch, boomerang)) {
					boomerang.useDurability();
					if (boomerang.durability <= 0) Returning.this.detach();
				}

			} finally {
				boomerang.rangeBoost      = rangeBoost;
				boomerang.adjacentPenalty = adjacentPenalty;
				((Hero)target).subClass   = subClass;
			}
		}

		private static String
				POS = "pos", LAST_POS = "LASTPOS",
				DPT = "distancePerTurn", PATH = "path",
				RANG = "boomerang", DOO = "depthOfOrigin";

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
			depthOfOrigin = bundle.getInt(DOO);
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
				angularSpeed *= 0.75;
			}

			@Override
			public synchronized void onComplete(Tweener tweener) {
				super.onComplete(tweener);
				revive();
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
