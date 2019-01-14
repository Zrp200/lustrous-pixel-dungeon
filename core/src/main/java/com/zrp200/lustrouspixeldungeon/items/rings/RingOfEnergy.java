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

package com.zrp200.lustrouspixeldungeon.items.rings;

import com.zrp200.lustrouspixeldungeon.actors.Char;

public class RingOfEnergy extends Ring {
	private static final float WAND_BONUS_SCALING = 1.225f;
	private static final float ARTIFACT_BONUS_SCALING = 1.09f;

	@Override
	protected String effect1Bonus() {
		return visualMultiplier(WAND_BONUS_SCALING); // two different multipliers
	}

	@Override
	protected String effect2Bonus() {
		return visualMultiplier(ARTIFACT_BONUS_SCALING);
	}

	@Override
	protected RingBuff buff( ) {
		return new Energy();
	}
	
	public static float wandChargeMultiplier( Char target ){
		return (float)Math.pow(WAND_BONUS_SCALING,getBonus(target,Energy.class));
	}

	public static float artifactChargeMultiplier(Char target) {
		return (float)Math.pow(ARTIFACT_BONUS_SCALING,getBonus(target,Energy.class));
	}
	
	public class Energy extends RingBuff {
	}
}
