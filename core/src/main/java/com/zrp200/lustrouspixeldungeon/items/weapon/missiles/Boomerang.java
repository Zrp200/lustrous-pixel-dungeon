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

package com.zrp200.lustrouspixeldungeon.items.weapon.missiles;

import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Actor;
import com.zrp200.lustrouspixeldungeon.levels.Terrain;
import com.zrp200.lustrouspixeldungeon.levels.traps.DisarmingTrap;
import com.zrp200.lustrouspixeldungeon.levels.traps.ExplosiveTrap;
import com.zrp200.lustrouspixeldungeon.levels.traps.TeleportationTrap;
import com.zrp200.lustrouspixeldungeon.levels.traps.Trap;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSpriteSheet;
import com.zrp200.lustrouspixeldungeon.sprites.MissileSprite;

public class Boomerang extends MissileWeapon {

	{
		image = ItemSpriteSheet.BOOMERANG;

		tier = 3;
		sticky = false;

		baseUses = 8;
		durabilityScaling = 1.625f; // down from 3
		enchantDurability = 1.3125f; // down from 1.5
	}

	@Override
	public int minScale() { return 1; }

	@Override
	public int minBase() {
		int min;
		tier--; // one tier lower
		try {min = super.minBase();} finally { tier++; }
		return min;
	}

	@Override
	public int max(int lvl) { // 10 base, scaling by 2 each level.
		int value;
		tier--;
		try { value = super.max(lvl); } finally { tier++; }
		return value;
	}

	protected void onThrowComplete(int cell) {
		MissileSprite sprite = ((MissileSprite) curUser.sprite.parent.recycle(MissileSprite.class));
		Trap trapAtCell = Dungeon.level.traps.get(cell);
		if(!trapAtCell.active) trapAtCell = null;
		if(!rangedHit && Actor.findChar(cell) == null) {
			if (!Dungeon.level.pit[cell] && (trapAtCell != null || Dungeon.level.map[cell] == Terrain.DOOR)) {
				drop(cell); // quickly.
				if (trapAtCell instanceof TeleportationTrap || trapAtCell instanceof DisarmingTrap)
					return;
				if (trapAtCell instanceof ExplosiveTrap && isDestroyable()) return;
			} else {

			}
		}
		sprite.reset(cell, curUser.sprite, curItem, null);
		if (!collect(curUser.belongings.backpack)) {
			drop(curUser.pos);
		}
	}
}
