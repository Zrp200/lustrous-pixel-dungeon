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

import com.zrp200.lustrouspixeldungeon.effects.BlobEmitter;
import com.zrp200.lustrouspixeldungeon.effects.Speck;
import com.zrp200.lustrouspixeldungeon.levels.Level;
import com.zrp200.lustrouspixeldungeon.levels.Terrain;

import static com.zrp200.lustrouspixeldungeon.Dungeon.level;

public class SmokeScreen extends Blob {
	
	@Override
	protected void evolve() {
		super.evolve();
		applyToBlobArea(1, new EvolveCallBack() {
			@Override
			protected void call() {
				level.losBlocking[cell] = off[cell] > 0 || (Terrain.flags[level.map[cell]] & Terrain.LOS_BLOCKING) != 0;
			}
		});
	}
	
	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );
		emitter.pour( Speck.factory( Speck.SMOKE ), 0.1f );
	}
	
	@Override
	public void clear(int cell, int amount) {
		super.clear(cell, amount);
		Level l = level;
		l.losBlocking[cell] = cur[cell] > 0 || (Terrain.flags[l.map[cell]] & Terrain.LOS_BLOCKING) != 0;
	}
	
	@Override
	public void fullyClear() {
		super.fullyClear();
		level.buildFlagMaps();
	}
	
}
