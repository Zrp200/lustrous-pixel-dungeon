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

import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;

public class RingOfTenacity extends Ring {
	private static final float BONUS_SCALING = 0.85f;

	@Override
	protected RingBuff buff() {
		return new Tenacity();
	}

	@Override
	protected String effect2Bonus() {
		return visualMultiplier(BONUS_SCALING);
	}

	public static float damageMultiplier(Char t ){
		//(HT - HP)/HT = heroes current % missing health.
		return (float)Math.pow(BONUS_SCALING,getBonus( t, Tenacity.class )*((float)(t.HT - t.HP)/t.HT));
	}

	@Override
	protected float visualSoloBonus() {
		float level = super.visualSoloBonus();
		Hero hero = Dungeon.hero;
		if(hero != null) level *= (hero.HT - hero.HP) / hero.HT;
		return level;
	}

	public class Tenacity extends RingBuff {
	}
}

