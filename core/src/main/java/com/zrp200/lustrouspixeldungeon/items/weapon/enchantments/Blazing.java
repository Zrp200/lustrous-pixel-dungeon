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

package com.zrp200.lustrouspixeldungeon.items.weapon.enchantments;

import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Burning;
import com.zrp200.lustrouspixeldungeon.effects.particles.FlameParticle;
import com.zrp200.lustrouspixeldungeon.items.weapon.Weapon;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSprite;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSprite.Glowing;

import static com.zrp200.lustrouspixeldungeon.Dungeon.depth;

public class Blazing extends Weapon.Enchantment {

	public static ItemSprite.Glowing GLOW = new ItemSprite.Glowing( 0xFF4400 ); // Orange
	
	@Override
	public int proc( Weapon weapon, Char attacker, Char defender, int damage ) {
		// lvl 0 - 20% (10% reignite)
		// lvl 1 - 33% (17% reignite)
		// lvl 2 - 50% (25% reignite)

		if (Random.Int( weapon.level() + 3 ) >= 2) {
			proc(defender);
		}

		return damage;
	}

	public static void proc(Char target) {
		if ( (target.buff(Burning.class) != null || target.isImmune(Burning.class) ) && !target.isImmune(Blazing.class))
			target.damage( Math.round(Burning.damageRoll() * 0.67f), new Blazing() );
		Burning.reignite(target);
		target.sprite.emitter().burst( FlameParticle.FACTORY, depth/4+1 );
	}
	
	@Override
	public Glowing glowing() {
		return GLOW;
	}
}
