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

package com.zrp200.lustrouspixeldungeon.items.food;

import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Burning;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Hunger;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Paralysis;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Poison;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Roots;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Slow;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSpriteSheet;
import com.zrp200.lustrouspixeldungeon.utils.GLog;

public class MysteryMeat extends Meat {

	{
		image = ItemSpriteSheet.MEAT;
		energy = Hunger.HUNGRY/2f;
	}

	public void effect(Hero hero){
		switch (Random.Int( 5 )) {
			case 0:
				GLog.w( Messages.get(MysteryMeat.class, "hot") );
				Buff.affect( hero, Burning.class ).reignite();
				break;
			case 1:
				GLog.w( Messages.get(MysteryMeat.class, "legs") );
				Buff.prolong( hero, Roots.class, Paralysis.DURATION );
				break;
			case 2:
				GLog.w( Messages.get(MysteryMeat.class, "not_well") );
				Buff.prolong( hero, Poison.class, hero.HT / 5f );
				break;
			case 3:
				GLog.w( Messages.get(MysteryMeat.class, "stuffed") );
				Buff.prolong( hero, Slow.class, Slow.DURATION );
				break;
		}
	}
}
