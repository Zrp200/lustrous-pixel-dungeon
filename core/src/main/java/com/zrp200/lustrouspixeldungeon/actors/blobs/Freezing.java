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

import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Actor;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Chill;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Frost;
import com.zrp200.lustrouspixeldungeon.effects.BlobEmitter;
import com.zrp200.lustrouspixeldungeon.effects.CellEmitter;
import com.zrp200.lustrouspixeldungeon.effects.particles.SnowParticle;
import com.zrp200.lustrouspixeldungeon.items.Heap;
import com.zrp200.lustrouspixeldungeon.messages.Messages;

public class Freezing extends Blob {
	
	@Override
	protected void evolve() {
		final Fire fire = (Fire)Dungeon.level.blobs.get( Fire.class );
		
		applyToBlobArea(new EvolveCallBack() {
			@SuppressWarnings("ConstantConditions")
			@Override
			protected void call() {
				if (cur[cell] <= 0) {
					off[cell] = 0;
				} else if (volumeAt(cell,Fire.class) > 0){
					fire.clear(cell);
					off[cell] = cur[cell] = 0;
				} else {
					Freezing.freeze(cell);
					off[cell] = cur[cell] - 1;
					volume += off[cell];
				}
			}
		});
	}
	
	public static void freeze( int cell ){
		Char ch = Actor.findChar( cell );
		if (ch != null && !ch.isImmune(Freezing.class)) {
			if (ch.buff(Frost.class) != null){
				Buff.affect(ch, Frost.class, 2f);
			} else {
				Buff.affect(ch, Chill.class, Dungeon.level.water[cell] ? 5f : 3f);
				Chill chill = ch.buff(Chill.class);
				if (chill != null && chill.cooldown() >= 10f){
					Buff.affect(ch, Frost.class, 5f);
				}
			}
		}
		
		Heap heap = Dungeon.level.heaps.get( cell );
		if (heap != null) heap.freeze();
	}
	
	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );
		emitter.start( SnowParticle.FACTORY, 0.05f, 0 );
	}
	
	@Override
	public String tileDesc() {
		return Messages.get(this, "desc");
	}
	
	//legacy functionality from before this was a proper blob. Returns true if this cell is visible
	public static boolean affect( int cell, Fire fire ) {
		
		Char ch = Actor.findChar( cell );
		if (ch != null) {
			if (Dungeon.level.water[ch.pos]){
				Buff.prolong(ch, Frost.class, Frost.duration(ch) * Random.Float(5f, 7.5f));
			} else {
				Buff.prolong(ch, Frost.class, Frost.duration(ch) * Random.Float(1.0f, 1.5f));
			}
		}
		
		if (fire != null) {
			fire.clear( cell );
		}
		
		Heap heap = Dungeon.level.heaps.get( cell );
		if (heap != null) {
			heap.freeze();
		}
		
		if (Dungeon.level.heroFOV[cell]) {
			CellEmitter.get( cell ).start( SnowParticle.FACTORY, 0.2f, 6 );
			return true;
		} else {
			return false;
		}
	}
}
