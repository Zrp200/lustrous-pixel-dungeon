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

package com.zrp200.lustrouspixeldungeon.actors.blobs;

import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Actor;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Paralysis;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.effects.BlobEmitter;
import com.zrp200.lustrouspixeldungeon.effects.particles.SparkParticle;
import com.zrp200.lustrouspixeldungeon.items.Heap;
import com.zrp200.lustrouspixeldungeon.items.Item;
import com.zrp200.lustrouspixeldungeon.items.wands.Wand;
import com.zrp200.lustrouspixeldungeon.items.weapon.melee.MagesStaff;

public class Electricity extends Blob {
	private static final float CHARGE_BONUS = 1/3f;
	
	{
		//acts after mobs, to give them a chance to resist paralysis
		actPriority = MOB_PRIO - 1;
	}
	
	private boolean[] water;
	
	@Override
	protected void evolve() {
		
		water = Dungeon.level.water;
		
		//spread first..
		applyToBlobArea(new EvolveCallBack() {
			@Override
			protected void call() {
				if (cur[cell] > 0) spreadFromCell(cell, cur[cell]);
			}
		});
		
		//..then decrement/shock
		applyToBlobArea(new EvolveCallBack() {
			@Override
			protected void call() {
				if (cur[cell] <= 0) {
					off[cell] = 0;
					return;
				}

				Char ch = Actor.findChar( cell );
				if (ch != null && !ch.isImmune(this.getClass())) {
					Buff.prolong( ch, Paralysis.class, 1f);
					if (cur[cell] % 2 == 1) {
						ch.damage(Math.round(Random.Float(2 + Dungeon.depth / 5f)), this);
					}
					if(ch instanceof Hero) ((Hero) ch).belongings.charge(Wand.Charger.CHARGE_BUFF_BONUS);
				}

				Heap h = Dungeon.level.heaps.get( cell );
				if (h != null){
					Item toShock = h.peek();
					if (toShock instanceof Wand){
						((Wand) toShock).gainCharge(CHARGE_BONUS);
					} else if (toShock instanceof MagesStaff){
						((MagesStaff) toShock).gainCharge(CHARGE_BONUS);
					}
				}

				off[cell] = cur[cell] - 1;
				volume += off[cell];
			}
		});
	}
	
	private void spreadFromCell( int cell, int power ){
		if (cur[cell] == 0) {
			area.union(cell % Dungeon.level.width(), cell / Dungeon.level.width());
		}
		cur[cell] = Math.max(cur[cell], power);
		
		for (int c : PathFinder.NEIGHBOURS4){
			if (water[cell + c] && cur[cell + c] < power){
				spreadFromCell(cell + c, power);
			}
		}
	}
	
	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );
		emitter.start( SparkParticle.FACTORY, 0.05f, 0 );
	}
	
}

