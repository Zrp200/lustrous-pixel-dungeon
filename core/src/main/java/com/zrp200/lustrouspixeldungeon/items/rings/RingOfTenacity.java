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
	private static final float BONUS_SCALING = 0.8f;

	@Override
	protected RingBuff buff() {
		return new Tenacity();
	}

	@Override
	protected String effect2Bonus() {
		return visualMultiplier(BONUS_SCALING);
	}

	@Override
	protected String effect1Bonus() {
		String result;
		Hero h = curUser; // idk I like curUser
		curUser = Dungeon.hero;
		int hp = curUser.HP;
		try {
			curUser.HP = 0; // ;)
			result = effect2Bonus();
		} finally {
			curUser.HP = hp;
			curUser = h;
		}
		return "up to " + result + ",";
	}

	public static float damageMultiplier(Char target ){
		//(HT - HP)/HT = heroes current % missing health.
		return (float)Math.pow(BONUS_SCALING,getBonus( target, Tenacity.class )*((float)(target.HT - target.HP)/target.HT));
	}

	@Override
	protected float visualSoloBonus() {
		float level = super.visualSoloBonus();
		Hero hero = Dungeon.hero;
		if(hero != null) level *= (float)(hero.HT - hero.HP) / hero.HT;
		return level;
	}

	private class Tenacity extends RingBuff { }
}

