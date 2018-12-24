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
import com.zrp200.lustrouspixeldungeon.messages.Messages;

import java.text.DecimalFormat;

public class RingOfTenacity extends Ring {
	static {
		bonusScaling = 0.85f;
		buffClass = Tenacity.class;
	}

	@Override
	protected RingBuff buff() {
		return new Tenacity();
	}

	public static float damageMultiplier(Char t ){
		//(HT - HP)/HT = heroes current % missing health.
		return (float)Math.pow(bonusScaling, getBonus( t )*((float)(t.HT - t.HP)/t.HT));
	}

	public class Tenacity extends RingBuff {
	}
}

