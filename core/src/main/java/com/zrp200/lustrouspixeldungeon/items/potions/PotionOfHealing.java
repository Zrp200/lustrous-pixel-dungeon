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

package com.zrp200.lustrouspixeldungeon.items.potions;

import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Bleeding;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Cripple;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Healing;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Poison;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Weakness;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.utils.GLog;

public class PotionOfHealing extends Potion {

	{
		initials = 3;

		bones = true;
	}
	
	@Override
	public void apply( Hero hero ) {
		setKnown();
		//starts out healing 30 hp, equalizes with hero health total at level 11
		Buff.affect( hero, Healing.class ).setHeal((int)(0.8f*hero.HT + 14), 0.25f, 0);
		cure( hero );
		GLog.p( Messages.get(this, "heal") );
	}
	
	public static void cure( Char ch ) {
		Buff.detach( ch, Poison.class );
		Buff.detach( ch, Cripple.class );
		Buff.detach( ch, Weakness.class );
		Buff.detach( ch, Bleeding.class );
		
	}

	@Override
	public int price() {
		return isKnown() ? 30 * quantity : super.price();
	}
}
