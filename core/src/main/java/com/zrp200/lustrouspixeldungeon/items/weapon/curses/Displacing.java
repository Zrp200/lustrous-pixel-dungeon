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

package com.zrp200.lustrouspixeldungeon.items.weapon.curses;

import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.actors.mobs.Mob;
import com.zrp200.lustrouspixeldungeon.effects.CellEmitter;
import com.zrp200.lustrouspixeldungeon.effects.Speck;
import com.zrp200.lustrouspixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.zrp200.lustrouspixeldungeon.items.weapon.Weapon;

public class Displacing extends WeaponCurse {

	public static boolean teleportChar(Char defender) { // logic externalized for Chaotic and Displacement
		if(defender instanceof Hero) {
			ScrollOfTeleportation.teleportHero((Hero) defender);
			return true;
		}
		if (defender.properties().contains(Char.Property.IMMOVABLE)) {
			return false;
		}

		int count = 10;
		int newPos;
		do {
			newPos = Dungeon.level.randomRespawnCell();
			if (count-- <= 0) {
				break;
			}
		} while (newPos == -1);

		if (newPos == -1 || Dungeon.bossLevel()) {
			return false;
		}

		if (Dungeon.level.heroFOV[defender.pos]) {
			CellEmitter.get( defender.pos ).start( Speck.factory( Speck.LIGHT ), 0.2f, 3 );
		}

		defender.pos = newPos;
		if (defender instanceof Mob && ((Mob) defender).state == ((Mob) defender).HUNTING){
			((Mob) defender).state = ((Mob) defender).WANDERING;
		}
		defender.sprite.place( defender.pos );
		defender.sprite.visible = Dungeon.level.heroFOV[defender.pos];
		return true;
	}

	@Override
	public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		return Random.Int(12) == 0 && teleportChar(defender) ? 0 : damage;
	}
}
