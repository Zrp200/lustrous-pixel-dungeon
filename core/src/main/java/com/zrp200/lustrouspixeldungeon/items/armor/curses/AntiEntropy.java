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

package com.zrp200.lustrouspixeldungeon.items.armor.curses;

import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Burning;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Frost;
import com.zrp200.lustrouspixeldungeon.effects.CellEmitter;
import com.zrp200.lustrouspixeldungeon.effects.particles.FlameParticle;
import com.zrp200.lustrouspixeldungeon.effects.particles.SnowParticle;
import com.zrp200.lustrouspixeldungeon.items.armor.Armor;
import com.zrp200.lustrouspixeldungeon.items.armor.Armor.Glyph;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSprite;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSprite.Glowing;

public class AntiEntropy extends Glyph {

	private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );
	
	@Override
	public int proc( Armor armor, Char attacker, Char defender, int damage) {

		if (Random.Int( 8 ) == 0) {

			if (Dungeon.level.adjacent( attacker.pos, defender.pos )) {
				Buff.prolong(attacker, Frost.class, Frost.duration(attacker) * Random.Float(0.5f, 1f));
				CellEmitter.get(attacker.pos).start(SnowParticle.FACTORY, 0.2f, 6);
			}
			
			Buff.affect( defender, Burning.class ).reignite();
			defender.sprite.emitter().burst( FlameParticle.FACTORY, 5 );

		}
		
		return damage;
	}

	@Override
	public Glowing glowing() {
		return BLACK;
	}

	@Override
	public boolean curse() {
		return true;
	}
}