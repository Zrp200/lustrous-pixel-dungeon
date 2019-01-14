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

package com.zrp200.lustrouspixeldungeon.items.weapon.missiles.darts;

import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.FlavourBuff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Paralysis;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSpriteSheet;

public class ParalyticDart extends TippedDart {
	
	{
		image = ItemSpriteSheet.PARALYTIC_DART;
	}
	
	@Override
	public int proc( Char attacker, Char defender, int damage ) {
		new FlavourBuff() {
			{ actPriority = VFX_PRIO; }
			public boolean act() {
				Buff.prolong( target, Paralysis.class, 5f );
				return super.act();
			}
		}.attachTo(defender);
		return super.proc( attacker, defender, damage );
	}
	
}
