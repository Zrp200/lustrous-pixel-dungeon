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

package com.zrp200.lustrouspixeldungeon.items.bags;

import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.items.Item;
import com.zrp200.lustrouspixeldungeon.items.bombs.Bomb;
import com.zrp200.lustrouspixeldungeon.items.wands.Wand;
import com.zrp200.lustrouspixeldungeon.items.weapon.missiles.MissileWeapon;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSpriteSheet;

public class MagicalHolster extends Bag {

	{
		image = ItemSpriteSheet.HOLSTER;

		limitedDrop = Dungeon.LimitedDrops.MAGICAL_HOLSTER;
	}

	public static final float HOLSTER_SCALE_FACTOR = 0.85f;
	public static final float HOLSTER_DURABILITY_FACTOR = 1.2f;
	
	@Override
	public boolean grab( Item item ) {
		return item instanceof Wand || item instanceof MissileWeapon || item instanceof Bomb;
	}
	
	@Override
	public boolean collect( Bag container ) {
		if (super.collect( container )) {
			if (owner != null) for (Item item : items) if (item instanceof Wand)
				( (Wand) item ).charge(owner, HOLSTER_SCALE_FACTOR);
			return true;
		}
		return false;
	}

	@Override
	public void onDetach( ) {
		super.onDetach();
		for (Item item : items) if (item instanceof Wand)
			( (Wand) item ).stopCharging();
	}
	
	@Override
	public int price() {
		return 60;
	}

}
