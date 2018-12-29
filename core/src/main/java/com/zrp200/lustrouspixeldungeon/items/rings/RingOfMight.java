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

package com.zrp200.lustrouspixeldungeon.items.rings;


import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.items.Item;

public class RingOfMight extends Ring {
	private static final float BONUS_SCALING = 1.035f;

	@Override
	protected String statsInfo() {
		return statsInfo(BONUS_SCALING);
	}

	@Override
	protected RingBuff buff() {
		return new Might();
	}

	@Override
	public boolean doEquip(Hero hero) {
		if (super.doEquip(hero)){
			hero.updateHT( false );
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean doUnequip(Hero hero, boolean collect, boolean single) {
		if (super.doUnequip(hero, collect, single)){
			hero.updateHT( false );
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Item upgrade() {
		super.upgrade();
		updateTargetHT();
		return this;
	}

	@Override
	public void level(int value) {
		super.level(value);
		updateTargetHT();
	}
	
	private void updateTargetHT(){
		if (buff != null && buff.target instanceof Hero){
			((Hero) buff.target).updateHT( false );
		}
	}

	
	public static int strengthBonus( Char target ){
		return getBonus(target,Might.class);
	}
	
	public static float HTMultiplier( Char target ){
		return (float)Math.pow(BONUS_SCALING,getBonus(target,Might.class));
	}

	public class Might extends RingBuff {
	}
}

