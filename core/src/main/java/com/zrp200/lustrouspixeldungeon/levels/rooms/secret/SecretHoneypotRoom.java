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

package com.zrp200.lustrouspixeldungeon.levels.rooms.secret;

import com.watabou.utils.Point;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.mobs.Bee;
import com.zrp200.lustrouspixeldungeon.items.Honeypot;
import com.zrp200.lustrouspixeldungeon.items.Item;
import com.zrp200.lustrouspixeldungeon.items.bombs.Bomb;
import com.zrp200.lustrouspixeldungeon.levels.Level;
import com.zrp200.lustrouspixeldungeon.levels.Terrain;
import com.zrp200.lustrouspixeldungeon.levels.painters.Painter;

public class SecretHoneypotRoom extends SecretRoom {
	
	@Override
	public void paint(Level level) {
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill(level, this, 1, Terrain.EMPTY );
		
		Point brokenPotPos = center();
		
		brokenPotPos.x = (brokenPotPos.x + entrance().x) / 2;
		brokenPotPos.y = (brokenPotPos.y + entrance().y) / 2;
		
		Honeypot.ShatteredPot pot = new Honeypot.ShatteredPot();
		level.drop(pot, level.pointToCell(brokenPotPos));
		
		Bee bee = new Bee();
		bee.spawn( Dungeon.depth );
		bee.HP = bee.HT;
		bee.pos = level.pointToCell(brokenPotPos);
		level.mobs.add( bee );
		
		bee.setPotInfo(level.pointToCell(brokenPotPos), null);
		
		placeItem(new Honeypot(), level);
		
		placeItem( new Bomb().random(), level);
		
		entrance().set(Door.Type.HIDDEN);
	}
	
	private void placeItem(Item item, Level level){
		int itemPos;
		do {
			itemPos = level.pointToCell(random());
		} while (level.heaps.get(itemPos) != null);
		
		level.drop(item, itemPos);
	}
}
