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

package com.zrp200.lustrouspixeldungeon.items.scrolls;

import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.effects.ItemChange;
import com.zrp200.lustrouspixeldungeon.items.EquipableItem;
import com.zrp200.lustrouspixeldungeon.items.Item;
import com.zrp200.lustrouspixeldungeon.journal.Catalog;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.utils.GLog;
import com.zrp200.lustrouspixeldungeon.windows.WndBag;

public class ScrollOfTransmutation extends InventoryScroll {
	
	{
		initials = 10;
		mode = WndBag.Mode.TRANMSUTABLE;

		value = 50;
	}
	
	public static boolean canTransmute(Item item){
		return item != null && item.transmute(true) != null;
	}
	
	@Override
	protected void onItemSelected(Item item) {

		Item result = canTransmute(item) ? item.transmute() : null;

		if (result == null) {
			//This shouldn't ever trigger
			GLog.n(Messages.get(this, "nothing"));
			curItem.collect(curUser.belongings.backpack);
		} else {
			if (item.isEquipped(curUser)) {
				item.cursed = false; //to allow it to be unequipped
				((EquipableItem) item).doUnequip(curUser, false);
				((EquipableItem) result).doEquip(curUser);
			} else {
				item.detach(curUser.belongings.backpack);
				if (!result.collect()) {
					result.drop(curUser.pos);
				}
			}
			if (result.isIdentified()) {
				Catalog.setSeen(result.getClass());
			}
			//GLog.p( Messages.get(this, "morph") );
			ItemChange.show(Dungeon.hero, result);
		}
	}

	@Override
	public void empoweredRead() {
		//does nothing, this shouldn't happen
	}
}
