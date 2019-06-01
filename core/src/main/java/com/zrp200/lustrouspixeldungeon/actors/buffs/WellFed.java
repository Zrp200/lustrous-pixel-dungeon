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

package com.zrp200.lustrouspixeldungeon.actors.buffs;

import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.ui.BuffIndicator;

public class WellFed extends ActiveBuff {
	
	{
		type = buffType.POSITIVE;
		startGrey = 150f;
	}

	@Override
	public boolean act() {
		super.act();
		int left = Math.round(this.left);
		if (left != 0 && left % 18 == 0) {
			target.HP = Math.min(++target.HP,target.HT);
		}
		return true;
	}

	public void reset(){
		//heals one HP every 10 turns for 450 turns
		prolong(Hunger.STARVING);
	}
	
	@Override
	public int icon() {
		return BuffIndicator.WELL_FED;
	}
	
	@Override
	public String desc() {
		return Messages.get(this, "desc", Math.round(left+1));
	}
}
