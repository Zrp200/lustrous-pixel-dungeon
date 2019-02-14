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

import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.actors.mobs.Piranha;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSpriteSheet;

public class FishingSpear extends MissileWeapon {
	
	{
		image = ItemSpriteSheet.FISHING_SPEAR;
		
		tier = 2;
	}

	@Override
	public float accuracyFactor(Char owner) {
		boolean modify = owner instanceof Hero && ((Hero)owner).enemy() instanceof Piranha;
		float acc = super.accuracyFactor(owner);
		if(modify) acc *= 1.5;
		return acc;
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (defender instanceof Piranha){
			damage = Math.max(damage, defender.HP/2);
		}
		return super.proc(attacker, defender, damage);
	}
}
