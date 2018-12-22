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

import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.items.weapon.Weapon;
import com.zrp200.lustrouspixeldungeon.messages.Messages;

public class MeleeWeapon extends Weapon {
	
	public int tier;

	public int minScale() {return 1;}
	public int maxScale() {return tier+1;}
	public int minBase() {return tier;}
	public int maxBase() {return (tier+1)*5;}

	@Override
	public int min(int lvl) {
		return  minBase() +  //base
				lvl*minScale();    //level scaling
	}

	@Override
	public int max(int lvl) {
		return  maxBase() +    //base
				lvl*(maxScale());   //level scaling
	}

	public int STRReq(int lvl){
		lvl = Math.max(0, lvl);
		//strength req decreases at +1,+3,+6,+10,etc.
		return (8 + tier * 2) - (int)(Math.sqrt(8 * lvl + 1) - 1)/2;
	}
	
	@Override
	public int damageRoll(Char owner) {
		int damage = augment.damageFactor(super.damageRoll( owner ));

		if (owner instanceof Hero) {
			int exStr = ((Hero)owner).STR() - STRReq();
			if (exStr > 0) {
				damage += Random.IntRange( 0, exStr );
			}
		}
		
		return damage;
	}

	public int defenseFactor(Char owner, int level) {
		return 0;
	}
	public int defenseFactor(Char owner) {
		return defenseFactor(owner, level());
	}

	public String baseInfo() {
		String info = desc();
		int encumbrance = STRReq(levelKnown ? level() : 0) - Dungeon.hero.STR();

		if (levelKnown) {
			info += "\n\n" + Messages.get(MeleeWeapon.class, "stats_known", tier, augment.damageFactor(min()), augment.damageFactor(max()), STRReq());
			if (encumbrance > 0) {
				info += " " + Messages.get(Weapon.class, "too_heavy");
			} else if (encumbrance < 0){
				info += " " + Messages.get(Weapon.class, "excess_str", -encumbrance);
			}
		} else {
			info += "\n\n" + Messages.get(MeleeWeapon.class, "stats_unknown", tier, min(0), max(0), STRReq(0));
			if (encumbrance > 0) {
				info += " " + Messages.get(MeleeWeapon.class, "probably_too_heavy");
			}
		}
		if (!statsInfo().equals(""))
			info += "\n\n" + statsInfo();
		return info;
	}
	@Override
	public String info() {
		String info = baseInfo();
		switch (augment) {
			case SPEED:
				info += "\n\n" + Messages.get(Weapon.class, "faster");
				break;
			case DAMAGE:
				info += "\n\n" + Messages.get(Weapon.class, "stronger");
				break;
			case NONE:
		}

		if ( isVisiblyEnchanted() ){
			info += "\n\n" + Messages.get(Weapon.class, "enchanted", enchantment.name());
			info += " " + Messages.get(enchantment, "desc");
		}

		if (cursed && isEquipped( Dungeon.hero )) {
			info += "\n\n" + Messages.get(Weapon.class, "cursed_worn");
		} else if ( visiblyCursed() ) {
			info += "\n\n" + Messages.get(Weapon.class, "cursed");
		} else if (!isIdentified() && cursedKnown){
			info += "\n\n" + Messages.get(Weapon.class, "not_cursed");
		}
		
		return info;
	}
	
	public String statsInfo(){
		return Messages.get(this, "stats_desc");
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

}
