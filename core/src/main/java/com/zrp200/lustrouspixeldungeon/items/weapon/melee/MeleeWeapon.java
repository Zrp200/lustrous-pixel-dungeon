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

package com.zrp200.lustrouspixeldungeon.items.weapon.melee;

import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.items.weapon.Weapon;

public class MeleeWeapon extends Weapon {

	public int minScale() 	{ return 1;          }
	public int maxScale() 	{ return tier+1;     }
	public int minBase()  	{ return tier;       }
	public int maxBase()  	{ return (tier+1)*5; }

	public int STRReq(int lvl){
		lvl = Math.max(0, lvl);
		//strength req decreases at +1,+3,+6,+10,etc.
		return (8 + tier * 2) - (int)(Math.sqrt(8 * lvl + 1) - 1)/2;
	}

	public int defenseFactor(Char owner, int level) {
		return 0;
	}
	public int defenseFactor(Char owner) {
		return defenseFactor(owner, level());
	}

	@Override
	public int price() {
		int price = 20 * tier;
		if ( isVisiblyEnchanted() ) {
			if( hasGoodEnchant() ) price *= 1.5;
			else price /= 1.5;
		}

		if (cursedKnown) {
			if(cursed) price *= enchantKnown ? 0.75 : 0.667;
			else if( !levelKnown ) price *= 1.25; // prize items usually.
		}
		if (levelKnown && level() > 0) {
			price *= (level() + 1);
		}
		if (price < 1) {
			price = 1;
		}
		return price;
	}

	public static class Uncommon extends MeleeWeapon { // because this is used so often
		@Override
		public int maxBase() {
			return 4*(tier+1);
		}
	}

}
