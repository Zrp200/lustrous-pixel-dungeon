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
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Burning;
import com.zrp200.lustrouspixeldungeon.effects.particles.FlameParticle;
import com.zrp200.lustrouspixeldungeon.items.weapon.Weapon;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSprite;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSprite.Glowing;

import static com.zrp200.lustrouspixeldungeon.Dungeon.depth;

public class Blazing extends Weapon.Enchantment {

	private static ItemSprite.Glowing ORANGE = new ItemSprite.Glowing( 0xFF4400 );
	
	@Override
	public int proc( Weapon weapon, Char attacker, Char defender, int damage ) {
		// lvl 0 - 20% (10% ignite)
		// lvl 1 - 33% (17% ignite)
		// lvl 2 - 50% (25% ignite)

		if (Random.Int( weapon.level() + 5 ) >= 4) {
			int intensity = depth/4+1;
			float duration, igniteChance;
			float damageMultiplier = 2/3f;

			boolean alreadyBurning = defender.buff(Burning.class) != null;
			if(alreadyBurning) {
				igniteChance = 1f;
				damageMultiplier = 0.5f; // already burning, fine to rein in power slightly
				duration = Burning.DURATION / 2; // 4 turns
			} else if(Dungeon.level.flamable[defender.pos]) {
				igniteChance = 2/3f;
				duration = Burning.DURATION;
			} else {
				duration = Burning.DURATION * 0.75f;
				igniteChance = 0.5f;
			}

			if(Random.Float() < igniteChance) {
				Buff.affect(defender, Burning.class).set(duration);
				if(!alreadyBurning) damageMultiplier = 0; // no damage for initial ignition.
			}
			defender.sprite.emitter().burst( FlameParticle.FACTORY, intensity);
			int blazeDamage = (int)Math.ceil(Burning.damageRoll() * damageMultiplier);// round up
			if(blazeDamage > 0) defender.damage(blazeDamage, this);
		}

		return damage;

	}
	
	@Override
	public Glowing glowing() {
		return ORANGE;
	}
}
