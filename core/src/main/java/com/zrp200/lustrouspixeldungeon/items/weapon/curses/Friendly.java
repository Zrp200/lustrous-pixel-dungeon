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

import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Charm;
import com.zrp200.lustrouspixeldungeon.effects.Speck;
import com.zrp200.lustrouspixeldungeon.items.weapon.Weapon;

public class Friendly extends WeaponCurse {
	
	@Override
	public int proc(Weapon weapon, Char attacker, Char defender, int damage ) {
		
		if (Random.Int(10) == 0){
			
			int base = Random.IntRange(3, 5);
			
			Buff.affect( attacker, Charm.class, base + 10 ).object = defender.id();
			
			//5 turns will be reduced by the attack, so effectively lasts for base turns
			Buff.affect( defender, Charm.class, base + 5 ).object = attacker.id();
			
		}
		
		return damage;
	}

}
