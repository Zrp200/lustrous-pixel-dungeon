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
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Blindness;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Slow;
import com.zrp200.lustrouspixeldungeon.effects.Speck;
import com.zrp200.lustrouspixeldungeon.items.weapon.Weapon;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSprite;

public class Dazzling extends Weapon.Enchantment {

	public static final ItemSprite.Glowing COLOR = new ItemSprite.Glowing( 0xFFFF66 ); // light yellow

	@Override
	public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		// lvl 0 - 12.5%
		// lvl 1 - 22.2%
		// lvl 2 - 30%
		int level = Math.max( 0, weapon.level() );

		if (Random.Int( level + 8 ) >= 7) {

			Buff.prolong( defender, Blindness.class, Random.Float( 1f, 2f + level ) );
			Buff.prolong( defender, Slow.class, Random.Float( 1f, 1.5f + level/2f ) );
			defender.sprite.emitter().burst(Speck.factory(Speck.LIGHT), 6 );

		}

		return damage;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return COLOR;
	}

}