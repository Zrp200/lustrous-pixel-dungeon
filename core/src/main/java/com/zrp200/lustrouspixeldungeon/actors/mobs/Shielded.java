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

package com.zrp200.lustrouspixeldungeon.actors.mobs;

import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Berserk;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.items.BrokenSeal;
import com.zrp200.lustrouspixeldungeon.items.armor.PlateArmor;
import com.zrp200.lustrouspixeldungeon.sprites.ShieldedSprite;

public class Shielded extends Brute {

	public Shielded() {
		spriteClass = ShieldedSprite.class;
		EXP = 9; // from being rare
		defenseSkill = 20;

		Buff.affect(this, BrokenSeal.WarriorShield.class).setArmor(new PlateArmor());

	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 10);
	}
	public int defenseProc(Char enemy, int damage) {
		if(damage > 0) Buff.affect(this, Berserk.class).damage(damage); // Let's make this interesting shall we?
		return super.defenseProc(enemy,damage);
	}
	
}
