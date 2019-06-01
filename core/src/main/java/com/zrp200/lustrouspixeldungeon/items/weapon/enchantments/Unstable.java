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

package com.zrp200.lustrouspixeldungeon.items.weapon.enchantments;

import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.LustrousPixelDungeon;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.items.weapon.Weapon;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSprite;

import java.util.ArrayList;

public class Unstable extends Weapon.Enchantment {

	private static ItemSprite.Glowing GREY = new ItemSprite.Glowing( 0x999999 );

	@SuppressWarnings("unchecked")
	private static final ArrayList<Class<?extends Weapon.Enchantment>> enchantList =
			new ArrayList<Class<?extends Weapon.Enchantment>>() {
		{
			Class[][] enchantCategories = {common, uncommon, rare};
			for(Class[] enchantCategory : enchantCategories)
				for(Class enchantClass : enchantCategory)
					add(enchantClass);

			remove(Projecting.class); // no on-hit effect.
			remove(Unstable.class); // obvious reasons
		}
	};

	public static Weapon.Enchantment randomEnchantment() {
		//noinspection ConstantConditions
		Class<?extends Weapon.Enchantment> enchantClass = (Class<? extends Weapon.Enchantment>) Random.oneOf(enchantList.toArray());
		try {
			return enchantClass.newInstance();
		} catch (Exception e) {
			LustrousPixelDungeon.reportException(e);
			return randomEnchantment();
		}
	}
	
	public static boolean justRolledPrecise;

	@Override
	public int proc( Weapon weapon, Char attacker, Char defender, int damage ) {
		if (justRolledPrecise){
			justRolledPrecise = false;
			return damage;
		}
		return randomEnchantment().proc( weapon, attacker, defender, damage );
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return GREY;
	}
}
