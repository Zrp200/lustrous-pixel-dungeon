/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2018 Evan Debenham
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

package com.zrp200.lustrouspixeldungeon.levels.rooms.special;

import com.watabou.utils.Point;
import com.zrp200.lustrouspixeldungeon.Challenges;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.items.Generator;
import com.zrp200.lustrouspixeldungeon.items.Gold;
import com.zrp200.lustrouspixeldungeon.items.Heap;
import com.zrp200.lustrouspixeldungeon.items.Item;
import com.zrp200.lustrouspixeldungeon.items.armor.Armor;
import com.zrp200.lustrouspixeldungeon.items.keys.IronKey;
import com.zrp200.lustrouspixeldungeon.levels.Level;
import com.zrp200.lustrouspixeldungeon.levels.Terrain;
import com.zrp200.lustrouspixeldungeon.levels.painters.Painter;

public class CryptRoom extends SpecialRoom {

	public void paint( Level level ) {
		
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1, Terrain.EMPTY );

		Point c = center();
		int cx = c.x;
		int cy = c.y;
		
		Door entrance = entrance();
		
		entrance.set( Door.Type.LOCKED );
		level.addItemToSpawn( new IronKey( Dungeon.depth ) );
		
		if (entrance.x == left) {
			Painter.set( level, new Point( right-1, top+1 ), Terrain.STATUE );
			Painter.set( level, new Point( right-1, bottom-1 ), Terrain.STATUE );
			cx = right - 2;
		} else if (entrance.x == right) {
			Painter.set( level, new Point( left+1, top+1 ), Terrain.STATUE );
			Painter.set( level, new Point( left+1, bottom-1 ), Terrain.STATUE );
			cx = left + 2;
		} else if (entrance.y == top) {
			Painter.set( level, new Point( left+1, bottom-1 ), Terrain.STATUE );
			Painter.set( level, new Point( right-1, bottom-1 ), Terrain.STATUE );
			cy = bottom - 2;
		} else if (entrance.y == bottom) {
			Painter.set( level, new Point( left+1, top+1 ), Terrain.STATUE );
			Painter.set( level, new Point( right-1, top+1 ), Terrain.STATUE );
			cy = top + 2;
		}
		
		level.drop( prize( level ), cx + cy * level.width() ).type = Heap.Type.TOMB;
	}
	
	private static Item prize( Level level ) {
		
		//1 floor set higher than normal
		Armor prize = Generator.randomArmor( (Dungeon.depth / 5) + 1);
		
		if (Challenges.isItemBlocked(prize)){
			return new Gold().random();
		}

		//if it isn't already cursed, give it a free upgrade
		if (!prize.cursed){
			prize.upgrade();
			//curse the armor, unless it has a glyph
			if (!prize.hasGoodGlyph()){
				prize.inscribe(Armor.Glyph.randomCurse());
			}
		}
		prize.cursed = prize.cursedKnown = true;
		prize.glyphKnown = false;
		
		return prize;
	}
}
