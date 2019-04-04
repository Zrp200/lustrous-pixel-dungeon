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

import com.zrp200.lustrouspixeldungeon.actors.Actor;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Roots;
import com.zrp200.lustrouspixeldungeon.effects.BlobEmitter;
import com.zrp200.lustrouspixeldungeon.effects.particles.LeafParticle;
import com.zrp200.lustrouspixeldungeon.levels.Level;
import com.zrp200.lustrouspixeldungeon.levels.Terrain;
import com.zrp200.lustrouspixeldungeon.scenes.GameScene;

import static com.zrp200.lustrouspixeldungeon.Dungeon.level;

public class Regrowth extends Blob {
	
	@Override
	protected void evolve() {
		super.evolve();
		if(volume <= 0) return;
		applyToBlobArea(new EvolveCallBack() {
			@Override
			protected void call() {
				if(volume <= 0) return;
				if (cur[cell] > 0 || off[cell] > 0) {
					int c = level.map[cell];
					int c1 = c;
					boolean burning = volumeAt(cell,Fire.class) > 0;
					if (c == Terrain.EMPTY || c == Terrain.EMBERS || c == Terrain.EMPTY_DECO) {
						c1 = (cur[cell] > 9 && !burning)
								? Terrain.HIGH_GRASS : Terrain.GRASS;
					} else if ((c == Terrain.GRASS || c == Terrain.FURROWED_GRASS)
							&& cur[cell] > 9 && level.plants.get(cell) == null && !burning) {
						c1 = Terrain.HIGH_GRASS;
					}

					if (c1 != c) {
						Level.set( cell, c1 );
						GameScene.updateMap( cell );
						observe = true;
					}
					Char ch = Actor.findChar( cell );
					if (ch != null
							&& !ch.isImmune(this.getClass())
							&& (off[cell] > 1)) {
						Buff.prolong( ch, Roots.class, TICK );
					}
				}
			}
		});
	}

	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );
		
		emitter.start( LeafParticle.LEVEL_SPECIFIC, 0.2f, 0 );
	}
}
