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

package com.zrp200.lustrouspixeldungeon.levels.rooms.secret;

import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Hunger;
import com.zrp200.lustrouspixeldungeon.items.food.ChargrilledMeat;
import com.zrp200.lustrouspixeldungeon.items.food.Food;
import com.zrp200.lustrouspixeldungeon.items.food.FrozenCarpaccio;
import com.zrp200.lustrouspixeldungeon.items.food.Pasty;
import com.zrp200.lustrouspixeldungeon.levels.Level;
import com.zrp200.lustrouspixeldungeon.levels.Terrain;
import com.zrp200.lustrouspixeldungeon.levels.painters.Painter;
import com.zrp200.lustrouspixeldungeon.plants.BlandfruitBush;

public class SecretLarderRoom extends SecretRoom {
	
	@Override
	public int minHeight() {
		return 6;
	}
	
	@Override
	public int minWidth() {
		return 6;
	}
	
	@Override
	public void paint(Level level) {
		Painter.fill(level, this, Terrain.WALL);
		Painter.fill(level, this, 1, Terrain.EMPTY_SP);
		
		Point c = center();
		
		Painter.fill(level, c.x-1, c.y-1, 3, 3, Terrain.WATER);
		Painter.set(level, c, Terrain.GRASS);

		level.plant(new BlandfruitBush.Seed(), level.pointToCell(c));
		
		int extraFood = (int)(Hunger.STARVING - Hunger.HUNGRY) * (1 + Dungeon.depth / 5);

		while (extraFood > 0){
			Food food = Random.oneOf(new Pasty(), new Food(), new ChargrilledMeat(), new FrozenCarpaccio());
			if(food.energy > extraFood) continue;
			extraFood -= food.energy;
			int foodPos;
			do {
				foodPos = level.pointToCell(random());
			} while (level.map[foodPos] != Terrain.EMPTY_SP || level.heaps.get(foodPos) != null);
			level.drop(food, foodPos);
		}
		
		entrance().set(Door.Type.HIDDEN);
	}
	
	
}
