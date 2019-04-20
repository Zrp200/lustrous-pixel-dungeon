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
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Burning;
import com.zrp200.lustrouspixeldungeon.effects.particles.FlameParticle;
import com.zrp200.lustrouspixeldungeon.items.weapon.Weapon;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSprite;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSprite.Glowing;

import static com.zrp200.lustrouspixeldungeon.Dungeon.depth;
import static com.zrp200.lustrouspixeldungeon.Dungeon.level;

public class Blazing extends Weapon.Enchantment {

	private static ItemSprite.Glowing GLOW = new ItemSprite.Glowing( 0xFF4400 ); // Orange
	
	@Override
	public int proc( Weapon weapon, Char attacker, Char defender, int damage ) {
		// lvl 0 - 20% (10% ignite)
		// lvl 1 - 33% (17% ignite)
		// lvl 2 - 50% (25% ignite)

		if (Random.Int( weapon.level() + 5 ) >= 4) {
			boolean alreadyBurning = defender.buff(Burning.class) != null,
					onFlamableTile = level.flamable[defender.pos];

			float damageMultiplier = !alreadyBurning ? 2/3f : 1/2f; // proportion of burning damage applied, more for initial ignition
			float igniteChance = onFlamableTile ? 2/3f : 1/2f;

			float burnDuration = Burning.DURATION;
			if(!onFlamableTile) burnDuration /= 2;

			if(alreadyBurning || Random.Float() < igniteChance) { // always ignites if target is already burning
				Buff.prolong(defender, Burning.class, burnDuration);
				if(!alreadyBurning) damageMultiplier = 0; // no damage for initial ignition.
			}

			defender.sprite.emitter().burst( FlameParticle.FACTORY, depth/4+1);

			int blazeDamage = (int)Math.ceil(Burning.damageRoll() * damageMultiplier); // round up
			if(blazeDamage > 0 && !defender.isImmune( getClass() ) )
				defender.damage(blazeDamage, this);
		}

		return damage;

	}
	
	@Override
	public Glowing glowing() {
		return GLOW;
	}
}
