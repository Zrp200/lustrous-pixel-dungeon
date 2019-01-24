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

import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Actor;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.levels.traps.DisarmingTrap;
import com.zrp200.lustrouspixeldungeon.levels.traps.ExplosiveTrap;
import com.zrp200.lustrouspixeldungeon.levels.traps.TeleportationTrap;
import com.zrp200.lustrouspixeldungeon.levels.traps.Trap;
import com.zrp200.lustrouspixeldungeon.mechanics.Ballistica;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSpriteSheet;
import com.zrp200.lustrouspixeldungeon.sprites.MissileSprite;

import java.util.ArrayList;

public class Boomerang extends MissileWeapon {

	{
		image = ItemSpriteSheet.BOOMERANG;

		tier = 3;
		sticky = false;

		baseUses = 8;
	}

	@Override
	public int min(int lvl) {
		int min;
		tier--; // one tier lower
		try {  min = super.min(lvl); } finally {  tier++; }
		return min;
	}

	@Override
	public int max(int lvl) { // 10 base, scaling by 2 each level.
		int value;
		tier--;
		try { value = super.max(lvl); } finally { tier++; }
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
								|| trapAtCell instanceof ExplosiveTrap && isDestroyable() )
				) {
					super.onThrowComplete(cell);
					return; // it's not coming back.
				} else Dungeon.level.press(cell, null,true);
			}
			// TODO implement smooth return
		}
		Actor.add(new Return(cell));
		parent = null;
		rangedHit = false;
	}
	class Return extends Actor {
		{ actPriority = HERO_PRIO+1; }

		ArrayList<Integer> path;
		int distancePerTurn = 3, // hopefully soon this can be played around with a bit more...
			distanceThisTurn, lastVisible, curPos;

		MissileSprite sprite() {
			return (MissileSprite) curUser.sprite.parent.recycle(MissileSprite.class);
		}
		void progress() {
			distanceThisTurn--;
			curPos = path.remove(0);
		}

		private Return(int from) {
			distanceThisTurn = distancePerTurn
					= Math.max(1,Math.round( distancePerTurn / speedFactor(curUser) ) );
			Ballistica trajectory = new Ballistica(from,curUser.pos,Ballistica.STOP_TARGET);
			path = new ArrayList<>(trajectory.subPath(0,trajectory.path.indexOf(trajectory.collisionPos)));
			lastVisible = path.get(0);
		}

		@Override
		protected boolean act() {
			if(lastVisible == curUser.pos) { // you basically just dived for it.
				if(!collect()) drop(curUser.pos);
				remove(Return.this);
				return true;
			}
			if( path.isEmpty() ) { // it's come full "circle"
				drop( lastVisible );
				remove(this);
				return true;
			}
			if(distanceThisTurn<=0) {
				distanceThisTurn = Math.min(distancePerTurn, path.size() );
				spend(TICK);
				return true;
			}
			while(distanceThisTurn>0 && !path.isEmpty()) {
				progress();
				final Char occupant = findChar(curPos);
				if (occupant != null) {
					sprite().reset(lastVisible, occupant.sprite, Boomerang.this, new Callback() {
						@Override
						public void call() {
							if (occupant == curUser) {
								collect();
								remove(Return.this);
							} else if(lastVisible != occupant.pos) {
								curUser.shoot(occupant,Boomerang.this);
								if (durability <= 0) remove(Return.this);
							}
							next();
						}
					});
					return false; // it'll just call itself again I think
				}
			}
			sprite().reset(
					lastVisible,
					lastVisible = curPos,
					Boomerang.this,
					new Callback() { @Override public void call() { next(); } } );
			return false;
		}

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);

			bundle.put("curPos",curPos);
			bundle.put("lastVisible",lastVisible);
			bundle.put("distancePerTurn",distancePerTurn);
			bundle.put("distanceThisTurn",distanceThisTurn);

			int[] path = new int[this.path.size()];
			for(int i=0;i<path.length;i++) path[i] = this.path.get(i);
			bundle.put("path",path);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);

			path = new ArrayList<>();
			for (int pos : bundle.getIntArray("path")) path.add(pos);

			lastVisible = bundle.getInt("lastVisible");
			curPos = bundle.getInt("curPos");
			distanceThisTurn = bundle.getInt("distanceThisTurn");
			distancePerTurn = bundle.getInt("distancePerTurn");
		}
	}
}
