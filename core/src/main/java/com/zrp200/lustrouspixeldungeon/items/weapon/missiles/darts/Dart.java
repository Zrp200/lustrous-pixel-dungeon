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

package com.zrp200.lustrouspixeldungeon.items.weapon.missiles.darts;

import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.MagicImmune;
import com.zrp200.lustrouspixeldungeon.items.weapon.melee.Crossbow;
import com.zrp200.lustrouspixeldungeon.items.weapon.missiles.MissileWeapon;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSpriteSheet;

public class Dart extends MissileWeapon {

	{
		image = ItemSpriteSheet.DART;

		tier = 1;

		//infinite, even with penalties
		baseUses = 1000;
	}

	@Override
	public int minBase() {
		return bow != null ? 4+bow.level() : 1;
	}

	@Override
	public int maxBase() {
		return bow != null ? 12+3*bow.level() : 2;
	}

	@Override
	public int minScale() {
		return 1;
	}

	@Override
	public int maxScale() {
		return 2;
	}

	private static Crossbow bow;
	
	private void updateCrossbow(){
		if (Dungeon.hero.belongings.weapon instanceof Crossbow){
			bow = (Crossbow) Dungeon.hero.belongings.weapon;
		} else {
			bow = null;
		}
	}
	
	@Override
	public boolean hasEnchant(Class<? extends Enchantment> type, Char owner) {
		if (bow != null && bow.hasEnchant(type, owner)){
			return true;
		} else {
			return super.hasEnchant(type, owner);
		}
	}
	
	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (bow != null && bow.enchantment != null && attacker.buff(MagicImmune.class) == null){
			damage = bow.enchantment.proc(bow, attacker, defender, damage);
		}
		return super.proc(attacker, defender, damage);
	}
	
	@Override
	protected void onThrow(int cell) {
		updateCrossbow();
		super.onThrow(cell);
	}
	
	@Override
	public String info() {
		updateCrossbow();
		return super.info();
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
}
