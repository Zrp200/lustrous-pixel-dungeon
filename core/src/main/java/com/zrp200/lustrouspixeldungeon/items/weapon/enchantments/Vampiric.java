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
import com.zrp200.lustrouspixeldungeon.effects.Speck;
import com.zrp200.lustrouspixeldungeon.items.weapon.Weapon;
import com.zrp200.lustrouspixeldungeon.sprites.CharSprite;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSprite;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSprite.Glowing;

public class Vampiric extends Weapon.Enchantment {

	private static ItemSprite.Glowing RED = new ItemSprite.Glowing( 0x660022 );
	
	@Override
	public int proc( Weapon weapon, Char attacker, Char defender, int damage ) {
		//heals for up to 30% of damage dealt, based on missing HP, ultimately normally distributed
		float 	missingPercent = (attacker.HT - attacker.HP) / (float)attacker.HT,
				maxHeal = (.025f + missingPercent * .125f)*2*damage; // min max heal is .025%, consistent with shattered.
		float heal = Random.NormalFloat(maxHeal);
		if(heal % 1 > Random.Float()) heal++; // more likely to round up the closer it is. 1.1 has a 10% chance to round up to 2, lol.
		int healAmt = Math.min( (int)heal, attacker.HT - attacker.HP );

		if (healAmt > 0 && attacker.isAlive()) {
			attacker.HP += healAmt;
			attacker.sprite.emitter().start( Speck.factory( Speck.HEALING ), 0.4f, 1 );
			attacker.sprite.showStatus( CharSprite.POSITIVE, Integer.toString( healAmt ) );
		}

		return damage;
	}
	
	@Override
	public Glowing glowing() {
		return RED;
	}
}
