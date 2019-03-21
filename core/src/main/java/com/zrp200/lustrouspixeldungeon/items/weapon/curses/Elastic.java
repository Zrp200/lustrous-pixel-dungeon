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

package com.zrp200.lustrouspixeldungeon.items.weapon.curses;

import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.items.wands.WandOfBlastWave;
import com.zrp200.lustrouspixeldungeon.items.weapon.Weapon;
import com.zrp200.lustrouspixeldungeon.items.weapon.missiles.Boomerang;
import com.zrp200.lustrouspixeldungeon.mechanics.Ballistica;

public class Elastic extends WeaponCurse {
	@Override
	public int proc(Weapon weapon, Char attacker, Char defender, int damage ) {
		int origin = defender.pos;
		if(weapon instanceof Boomerang) {
			Boomerang boomerang = (Boomerang) weapon;
			if(boomerang.isReturning()) origin = boomerang.returning().lastPos();
		}
		int oppositeDefender = defender.pos + (defender.pos - origin);
		Ballistica trajectory = new Ballistica(defender.pos, oppositeDefender, Ballistica.MAGIC_BOLT);
		WandOfBlastWave.throwChar(defender, trajectory, 2);
		
		return damage/6;
	}
}
