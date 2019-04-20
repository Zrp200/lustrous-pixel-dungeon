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
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.items.Heap;
import com.zrp200.lustrouspixeldungeon.items.Item;
import com.zrp200.lustrouspixeldungeon.journal.Notes;
import com.zrp200.lustrouspixeldungeon.levels.Level;
import com.zrp200.lustrouspixeldungeon.levels.Terrain;
import com.zrp200.lustrouspixeldungeon.scenes.GameScene;

public abstract class WellWater extends Blob {

	private boolean seen = false;
	@Override
	protected void evolve() {
		seen = false;
		applyToBlobArea(new EvolveCallBack() {
			@Override
			protected void call() {
				off[cell] = cur[cell];
				volume += off[cell];
				if (off[cell] > 0 && Dungeon.level.visited[cell]) {
					seen = true;
				}
			}
		});

		if (seen){
			Notes.add(record());
		} else {
			Notes.remove(record());
		}
	}
	
	protected boolean affect( int pos ) {
		
		Heap heap;
		
		if (pos == Dungeon.hero.pos && affectHero( Dungeon.hero )) {
			
			cur[pos] = 0;
			return true;
			
		} else if ((heap = Dungeon.level.heaps.get( pos )) != null) {
			
			Item oldItem = heap.peek();
			Item newItem = affectItem( oldItem, pos );
			
			if (newItem != null) {

				if (newItem != oldItem) {
					if (oldItem.quantity() > 1) {

						oldItem.quantity( oldItem.quantity() - 1 );
						heap.drop( newItem );

					} else {
						heap.replace( oldItem, newItem );
					}
				}

				heap.sprite.link();
				cur[pos] = 0;
				
				return true;
				
			} else {
				
				int newPlace;
				do {
					newPlace = pos + PathFinder.NEIGHBOURS8[Random.Int( 8 )];
				} while (!Dungeon.level.passable[newPlace] && !Dungeon.level.avoid[newPlace]);
				Dungeon.level.drop( heap.pickUp(), newPlace ).sprite.drop( pos );
				
				return false;
				
			}
			
		} else {
			
			return false;
			
		}
	}
	
	protected abstract boolean affectHero( Hero hero );
	
	protected abstract Item affectItem( Item item, int pos );
	
	protected abstract Notes.Landmark record();
	
	public static void affectCell( int cell ) {
		
		Class<?>[] waters = {WaterOfHealth.class, WaterOfAwareness.class, WaterOfTransmutation.class};
		
		for (Class<?>waterClass : waters) {
			WellWater water = (WellWater)Dungeon.level.blobs.get( waterClass );
			if (water != null &&
				water.volume > 0 &&
				water.cur[cell] > 0 &&
				water.affect( cell )) {
				
				Level.set( cell, Terrain.EMPTY_WELL );
				GameScene.updateMap( cell );
				
				return;
			}
		}
	}
}
