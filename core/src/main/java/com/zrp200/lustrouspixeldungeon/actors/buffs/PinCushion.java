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

package com.zrp200.lustrouspixeldungeon.actors.buffs;

import com.watabou.utils.Bundle;
import com.zrp200.lustrouspixeldungeon.items.weapon.missiles.MissileWeapon;

import java.util.ArrayList;
import java.util.Collection;

public class PinCushion extends Buff {

	private ArrayList<MissileWeapon> items = new ArrayList<>();

	public void stick(MissileWeapon projectile){
		if( !projectile.attachedTo(this) ) {
			projectile.stickTo(target); // this is actually a recursive call. you can call whatever's easier.
			return;
		}

		for (MissileWeapon item : items){
			if (item.isSimilar(projectile)){
				item.merge(projectile);
				return;
			}
		}

		items.add(projectile);
	}

	@Override
	public void detach() {
		for (MissileWeapon item : items) item.detach();
		super.detach();
	}

	private static final String ITEMS = "items";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( ITEMS , items );
	}

	@SuppressWarnings("unchecked")
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle( bundle );
		items = new ArrayList<>((Collection<MissileWeapon>)(Collection<?>)bundle.getCollection( ITEMS ));
		for(MissileWeapon weapon : items)
			stick(weapon); // this updates the items as well while removing the need for them to store their embed
	}
}
