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
			returning = null;
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

	protected void onThrowComplete(final int cell) {
		if(cell == curUser.pos) {
			super.onThrowComplete(cell);
			return;
		}

		Trap trapAtCell = Dungeon.level.traps.get(cell);
		if(!rangedHit && Actor.findChar(cell) == null) {
			if (!Dungeon.level.pit[cell]) {
				if(trapAtCell != null && (
						trapAtCell instanceof TeleportationTrap
								|| trapAtCell instanceof DisarmingTrap
								|| (trapAtCell instanceof ExplosiveTrap || trapAtCell instanceof DisintegrationTrap) && isDestroyable() )
				) {
					super.onThrowComplete(cell);
					return; // it's not coming back.
				} else Dungeon.level.press(cell, null,true);
			}
			// TODO implement smooth return
		}
		returning = Buff.append(curUser,Returning.class).initialize(this,cell);

		parent = null;
		rangedHit = false;
	}

	public static class Returning extends Buff {
		{
			actPriority = (HERO_PRIO + BLOB_PRIO)/2;
		}

		int depthOfOrigin = depth;
		public int pos;
		ArrayList<Integer> path;

		private static final float
				MIN_SPEED = 1.5f,
				TURNS_TO_RETURN = 3f;

		float distancePerTurn;
		private Sprite sprite;
		public Boomerang boomerang;

		Returning initialize(Boomerang boomerang, int from) { // use this to set up stuff
			this.boomerang = boomerang;
			Ballistica trajectory = new Ballistica(from,target.pos,Ballistica.STOP_TARGET);
			distancePerTurn = Math.max(MIN_SPEED, trajectory.dist/TURNS_TO_RETURN);
			path = new ArrayList<>(trajectory.subPath(0,trajectory.path.indexOf(trajectory.collisionPos)));
			pos = path.remove(0);
			sprite();
			return this;
		}

		public Sprite sprite() {
			if(sprite == null) sprite = new Sprite(); // you'll never get a null from this
			try { target.sprite.parent.add(sprite); } catch (NullPointerException ignored) {}
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
				boomerang.drop(pos);
				return true;
			}

			return false;
		}

		@Override
		public boolean act() {
			if(!isActive()) { // wait
				spend(1);
				return true;
			}
			if( killIfNeeded() ) return true;
			spend(1f/distancePerTurn);

			final int dest = path.remove(0);
			sprite().reset(dest, new Callback() {
				@Override
				public void call() {
					pos = dest;
					onCharCollision( findChar(pos) );
					next();
				}
			});
			return false;
		}
		public void onCharCollision(Char ch) {
			if (ch != null && !killIfNeeded()) {

				float 	rangeBoost      = boomerang.rangeBoost,
						adjacentPenalty = boomerang.adjacentPenalty;

				HeroSubClass subClass = ((Hero)target).subClass;
				try {
					((Hero) target).subClass = HeroSubClass.NONE; // nope.
					boomerang.rangeBoost = boomerang.adjacentPenalty = 1f; // nullify these

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
		}

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);

			bundle.put("pos",pos);
			bundle.put("distancePerTurn",distancePerTurn);

			int[] path = new int[this.path.size()];
			for(int i=0;i<path.length;i++) path[i] = this.path.get(i);
			bundle.put("path",path);

			bundle.put("boomerang",boomerang);
			bundle.put("depthOfOrigin",depthOfOrigin);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);

			path = new ArrayList<>();
			for (int pos : bundle.getIntArray("path")) path.add(pos);

			pos = bundle.getInt("pos");
			distancePerTurn = bundle.getInt("distancePerTurn");
			boomerang = (Boomerang) bundle.get("boomerang");
			depthOfOrigin = bundle.getInt("depthOfOrigin");
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
				revive(); // 80% sure this will work
			}
		}
		public class Chase extends HeroAction { // this allows it to be stored.
			// >no multiple inheritance
			public Returning getBuff() {
				return Returning.this;
			}
			public HeroAction getAction() {
				if( curUser.buffs(Returning.this.getClass() ).contains(Returning.this)) return new HeroAction.Move(pos);
				if( Dungeon.level.containsItem(boomerang) ) return new HeroAction.PickUp(pos);
				return null;
			}

		}
	}
}
