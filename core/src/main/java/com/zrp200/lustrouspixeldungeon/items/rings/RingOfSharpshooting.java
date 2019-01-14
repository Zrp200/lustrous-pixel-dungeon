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

public class RingOfSharpshooting extends Ring {
	private static final float BONUS_SCALING = 1.2f;

	@Override
	protected String effect2Bonus() {
		return visualMultiplier(BONUS_SCALING);
	}

	@Override
	protected RingBuff buff() {
		return new Aim();
	}

	public static int levelDamageBonus(Char target ){
		return Math.round( getBonus(target, Aim.class) );
	}
	
	public static float durabilityMultiplier( Char target ){
		return (float)Math.pow(BONUS_SCALING,getBonus(target, Aim.class));
	}

	private class Aim extends RingBuff {
	}
}
