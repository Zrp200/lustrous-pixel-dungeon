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
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.FlavourBuff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Paralysis;
import com.zrp200.lustrouspixeldungeon.effects.Speck;
import com.zrp200.lustrouspixeldungeon.items.weapon.Weapon;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSprite;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSprite.Glowing;

public class Stunning extends Weapon.Enchantment {
	
	private static ItemSprite.Glowing YELLOW = new ItemSprite.Glowing( 0xCCAA44 );
	
	@Override
	public int proc( Weapon weapon, Char attacker, Char defender, int damage ) {
		// lvl 0 - 13%
		// lvl 1 - 22%
		// lvl 2 - 30%
		final int level = Math.max( 0, weapon.level() );
		
		if (Random.Int( level + 8 ) >= 7) {
			new FlavourBuff() {
				{actPriority = VFX_PRIO;}
				public boolean act() {
					Buff.prolong( target, Paralysis.class, Random.Float( 1, 1.5f + level ) );
					target.sprite.emitter().burst(Speck.factory(Speck.LIGHT), 12 );
					return super.act();
				}
			}.attachTo(defender);
		}

		return damage;
	}
	
	@Override
	public Glowing glowing() {
		return YELLOW;
	}
}
