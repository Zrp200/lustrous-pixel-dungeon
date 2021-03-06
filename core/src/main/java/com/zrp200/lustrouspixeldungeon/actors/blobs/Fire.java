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

import android.util.SparseIntArray;

import com.zrp200.lustrouspixeldungeon.actors.Actor;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Burning;
import com.zrp200.lustrouspixeldungeon.effects.BlobEmitter;
import com.zrp200.lustrouspixeldungeon.effects.particles.FlameParticle;
import com.zrp200.lustrouspixeldungeon.items.Heap;
import com.zrp200.lustrouspixeldungeon.levels.Level;
import com.zrp200.lustrouspixeldungeon.levels.Terrain;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.plants.Plant;
import com.zrp200.lustrouspixeldungeon.scenes.GameScene;

import static com.zrp200.lustrouspixeldungeon.Dungeon.level;

public class Fire extends Blob {

	@Override
	protected void evolve() {
		applyToBlobArea(new EvolveCallBack() {
			@Override
			protected void call() {
				Regrowth regrowth = (Regrowth) level.blobs.get(Regrowth.class);
				int fire;

				final Freezing freeze = (Freezing) level.blobs.get( Freezing.class );
				boolean cellFlammable = canIgnite(cell);
				if (cur[cell] > 0) { // tile is already on fire; do fire things
					if (volumeAt(cell, Freezing.class) > 0){
						//noinspection ConstantConditions
						freeze.clear(cell);
						off[cell] = cur[cell] = 0;
						return;
					}
					burn( cell );
					fire = cur[cell] - 1;

					if ( fire <= 0 && cellFlammable ) { // TODO make destruction independent from actual fire duration
						if (volumeAt(cell,Regrowth.class) <= 0) {
							level.destroy(cell);
							observe = true;
							GameScene.updateMap(cell);
						} else { // use regrowth as fuel
							fire = 1;
							//noinspection ConstantConditions
							regrowth.clear(cell,1);
						}
					}
					else if(fire <= terrainIgniteAmounts.get(Terrain.GRASS) && (level.map[cell] == Terrain.HIGH_GRASS || level.map[cell] == Terrain.FURROWED_GRASS)) {
						Level.set(cell,Terrain.GRASS);
						observe = true;
						GameScene.updateMap(cell);

					}
				} else if (volumeAt(cell,Freezing.class) <= 0) { // see if we reignite the cell
					if (cellFlammable
							&& (cell > 0 && cur[cell-1] > 0
							|| cur[cell+1] > 0
							|| cur[cell- level.width()] > 0
							|| cur[cell+ level.width()] > 0)) { // ignition
						fire = 4;
						burn(cell);
						area.union(x, y);
					} else {
						fire = 0;
					}
				}
				else fire = 0;
				volume += (off[cell] = fire);
			}
		});
	}
	
	public static void burn( int pos ) {
		Char ch = Actor.findChar( pos );
		if (ch != null && !ch.isImmune(Fire.class)) {
			Burning.reignite(ch);
		}

		burnTerrain(pos);
	}

	public static void burnTerrain(int pos) {
		Heap heap = level.heaps.get( pos );
		if (heap != null) {
			heap.burn();
		}

		Plant plant = level.plants.get( pos );
		if (plant != null){
			plant.wither();
		}
	}

	private static final SparseIntArray terrainIgniteAmounts = new SparseIntArray() {
		{
		    // these are flammable terrains.
			put(Terrain.GRASS,2); // amusingly identical to its actual value
			put(Terrain.FURROWED_GRASS, 5);
			put(Terrain.HIGH_GRASS, 5);
			put(Terrain.OPEN_DOOR, 4);
			put(Terrain.DOOR, 4);
			put(Terrain.BARRICADE, 5);
			put(Terrain.BOOKSHELF, 5);
		}
	};

	public static boolean canIgnite(int pos) {
	    return terrainIgniteAmounts.get( level.map[pos] ) > 0 || level.flamable[pos] || volumeAt(pos, Regrowth.class) > 0;
    }
	@Override
	public void seed(Level level, int cell, int amount) {
		if(volumeAt(cell, Fire.class) <= 0)
			amount = Math.max(terrainIgniteAmounts.get( level.map[cell], canIgnite(cell) ? 4 : 0), amount);

		if( amount > 0 ) // you can't seed 0 units of fire, sorry.
			super.seed(level, cell, amount);
	}

	// I like shortcuts.
	public static Fire ignite(int cell, int amount) {
		return seed(cell,amount,Fire.class);
	}
	public static void ignite(int cell) {
	    ignite(cell, 0);
    }

	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );
		emitter.pour( FlameParticle.FACTORY, 0.03f );
	}
	
	@Override
	public String tileDesc() {
		return Messages.get(this, "desc");
	}
}
