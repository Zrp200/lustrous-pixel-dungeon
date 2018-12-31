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
import com.zrp200.lustrouspixeldungeon.actors.blobs.Electricity;
import com.zrp200.lustrouspixeldungeon.actors.blobs.ToxicGas;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Burning;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Charm;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Chill;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Corrosion;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Frost;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Ooze;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Paralysis;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Poison;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Weakness;
import com.zrp200.lustrouspixeldungeon.actors.mobs.Eye;
import com.zrp200.lustrouspixeldungeon.actors.mobs.Shaman;
import com.zrp200.lustrouspixeldungeon.actors.mobs.Warlock;
import com.zrp200.lustrouspixeldungeon.actors.mobs.Yog;
import com.zrp200.lustrouspixeldungeon.levels.traps.DisintegrationTrap;
import com.zrp200.lustrouspixeldungeon.levels.traps.GrimTrap;

import java.util.HashSet;

public class RingOfElements extends Ring {
	private static final float BONUS_SCALING = 0.84f;

	@Override
	protected RingBuff buff() {
		return new Resistance();
	}

	@SuppressWarnings("unchecked")
	private static final HashSet<Class> RESISTS = new HashSet() {
		{
			add(Burning.class);	add(Charm.class);    	add(Chill.class);
			add(Frost.class);  	add(Ooze.class);     	add(Paralysis.class);
			add(Poison.class);	add(Corrosion.class);	add(Weakness.class);

			add(DisintegrationTrap.class);	add(GrimTrap.class);

			add(ToxicGas.class);	add(Electricity.class);

			add(Shaman.class);	add(Warlock.class);	add(Eye.class);	add(Yog.BurningFist.class);
		}
	};

	@Override
	protected String statsInfo() {
		return statsInfo(BONUS_SCALING);
	}

	public static float resist(Char target, Class effect ){
		if (getBonus(target, Resistance.class) == 0) return 1f;
		
		for (Class c : RESISTS){
			if (c.isAssignableFrom(effect)){
				return (float)Math.pow(BONUS_SCALING,getBonus(target,Resistance.class));
			}
		}
		return 1f;
	}
	
	private class Resistance extends RingBuff { }
}
