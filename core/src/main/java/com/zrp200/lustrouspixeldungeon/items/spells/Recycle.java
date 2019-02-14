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

package com.zrp200.lustrouspixeldungeon.items.spells;

import com.zrp200.lustrouspixeldungeon.Challenges;
import com.zrp200.lustrouspixeldungeon.LustrousPixelDungeon;
import com.zrp200.lustrouspixeldungeon.effects.ItemChange;
import com.zrp200.lustrouspixeldungeon.items.Generator;
import com.zrp200.lustrouspixeldungeon.items.Item;
import com.zrp200.lustrouspixeldungeon.items.potions.Potion;
import com.zrp200.lustrouspixeldungeon.items.potions.brews.Brew;
import com.zrp200.lustrouspixeldungeon.items.potions.elixirs.Elixir;
import com.zrp200.lustrouspixeldungeon.items.potions.exotic.ExoticPotion;
import com.zrp200.lustrouspixeldungeon.items.scrolls.Scroll;
import com.zrp200.lustrouspixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.zrp200.lustrouspixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.zrp200.lustrouspixeldungeon.items.scrolls.exotic.ScrollOfDivination;
import com.zrp200.lustrouspixeldungeon.items.stones.Runestone;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.plants.Plant;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSpriteSheet;
import com.zrp200.lustrouspixeldungeon.utils.GLog;
import com.zrp200.lustrouspixeldungeon.windows.WndBag;

public class Recycle extends InventorySpell {
	
	{
		image = ItemSpriteSheet.RECYCLE;
		mode = WndBag.Mode.RECYCLABLE;

		value = (50+50)/8f; //prices of ingredients, divided by output quantity;
	}
	
	@Override
	protected void onItemSelected(Item item) {
		Item result;
		do {
			if (item instanceof Potion) {
				result = Generator.random(Generator.Category.POTION);
				if (item instanceof ExoticPotion){
					try {
						result = ExoticPotion.regToExo.get(result.getClass()).newInstance();
					} catch ( Exception e ){
						LustrousPixelDungeon.reportException(e);
						result = item;
					}
				}
			} else if (item instanceof Scroll) {
				result = Generator.random(Generator.Category.SCROLL);
				if (item instanceof ExoticScroll){
					try {
						result = ExoticScroll.regToExo.get(result.getClass()).newInstance();
					} catch ( Exception e ){
						LustrousPixelDungeon.reportException(e);
						result = item;
					}
				}
			} else if (item instanceof Plant.Seed) {
				result = Generator.random(Generator.Category.SEED);
			} else {
				result = Generator.random(Generator.Category.STONE);
			}
		} while (result.getClass() == item.getClass() || Challenges.isItemBlocked(result));
		
		item.detach(curUser.belongings.backpack);
		GLog.p(Messages.get(this, "recycled", result.name()));
		result.collect();
		ItemChange.show(curUser,result);
	}
	
	public static boolean isRecyclable(Item item){
		return (item instanceof Potion && !(item instanceof Elixir || item instanceof Brew)) ||
				item instanceof Scroll ||
				item instanceof Plant.Seed ||
				item instanceof Runestone;
	}
	
	public static class Recipe extends com.zrp200.lustrouspixeldungeon.items.Recipe.SimpleRecipe {
		
		{
			inputs =  new Class[]{ScrollOfTransmutation.class, ScrollOfDivination.class};
			inQuantity = new int[]{1, 1};
			
			cost = 6;
			
			output = Recycle.class;
			outQuantity = 8;
		}
		
	}
}
