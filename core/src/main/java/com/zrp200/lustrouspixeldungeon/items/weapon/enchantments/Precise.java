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

package com.zrp200.lustrouspixeldungeon.items.weapon.enchantments;

import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.items.weapon.Weapon;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSprite;

public class Precise extends Weapon.Enchantment {
	
	private static ItemSprite.Glowing WHITE = new ItemSprite.Glowing( 0xFFFFFF );
	
	@Override
	public int proc( Weapon weapon, Char attacker, Char defender, int damage) {
		return damage;
	}
	
	//called from attackSkill in Hero, Statue, and GhostHero
	public static boolean rollToGuaranteeHit( Weapon weapon, Char owner ){
		// lvl 0 - 13%
		// lvl 1 - 22%
		// lvl 2 - 30%
		int level = Math.max( 0, weapon.level() );
		boolean unstable = false;
		if (weapon.hasEnchant(Precise.class, owner)
				|| ((unstable = weapon.hasEnchant(Unstable.class, owner)
				&& Random.oneOf(Unstable.randomEnchants) == Precise.class) &&
				(Random.Int(level + 8) >= 7))) {
			if(unstable) Unstable.justRolledPrecise = true;
			return true;
		}
		
		return false;
	}
	
	@Override
	public ItemSprite.Glowing glowing() {
		return WHITE;
	}
}
