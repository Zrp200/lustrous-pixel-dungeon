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

package com.zrp200.lustrouspixeldungeon.actors.mobs;

import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Amok;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Sleep;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Terror;
import com.zrp200.lustrouspixeldungeon.actors.mobs.npcs.Imp;
import com.zrp200.lustrouspixeldungeon.sprites.GolemSprite;

public class Golem extends Mob {
	
	{
		spriteClass = GolemSprite.class;
		
		HP = HT = 85;
		defenseSkill = 18;
		
		EXP = 12;
		maxLvl = 22;
		
		properties.add(Property.INORGANIC);
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 25, 40 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 28;
	}
	
	@Override
	public float attackDelay() {
		return super.attackDelay() * 1.5f;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 12);
	}
	
	@Override
	public void rollToDropLoot() {
		Imp.Quest.process( this );
		
		super.rollToDropLoot();
	}

	@Override
	public float resist(Class effect) {
		float resist = super.resist(effect);
		if(effect == Amok.class) resist /= 4; // Amok is 0.25x effective, up from 0x
		return resist;
	}

	{
		resistances.add( Terror.class );

		immunities.add( Sleep.class );
	}
}
