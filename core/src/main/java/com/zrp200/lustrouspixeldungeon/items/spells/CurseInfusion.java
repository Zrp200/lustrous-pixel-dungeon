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

import com.watabou.noosa.audio.Sample;
import com.zrp200.lustrouspixeldungeon.Assets;
import com.zrp200.lustrouspixeldungeon.effects.CellEmitter;
import com.zrp200.lustrouspixeldungeon.effects.particles.ShadowParticle;
import com.zrp200.lustrouspixeldungeon.items.Item;
import com.zrp200.lustrouspixeldungeon.items.armor.Armor;
import com.zrp200.lustrouspixeldungeon.items.quest.MetalShard;
import com.zrp200.lustrouspixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.zrp200.lustrouspixeldungeon.items.weapon.Weapon;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSpriteSheet;
import com.zrp200.lustrouspixeldungeon.windows.WndBag;

public class CurseInfusion extends InventorySpell {
	
	{
		image = ItemSpriteSheet.CURSE_INFUSE;
		mode = WndBag.Mode.CURSABLE;
	}
	
	@Override
	protected void onItemSelected(Item item) {
		
		CellEmitter.get(curUser.pos).burst(ShadowParticle.UP, 5);
		Sample.INSTANCE.play(Assets.SND_CURSED);
		
		item.cursed = true;
		if (item instanceof Weapon && item.isEnchantable() ) {
			Weapon w = (Weapon) item;
			Class<? extends Weapon.Enchantment> curr = null;
			if (w.hasEnchant()) {
				w.enchant(Weapon.Enchantment.randomCurse(w.enchantment.getClass()));
			} else {
				w.enchant(Weapon.Enchantment.randomCurse(curr));
			}
		} else if (item instanceof Armor){
			Armor a = (Armor) item;
			if (a.glyph != null){
				a.inscribe(Armor.Glyph.randomCurse(a.glyph.getClass()));
			} else {
				a.inscribe(Armor.Glyph.randomCurse());
			}
		}
	}
	
	@Override
	public int price() {
		//prices of ingredients, divided by output quantity
		return Math.round(quantity * ((30 + 100) / 4f));
	}
	
	public static class Recipe extends com.zrp200.lustrouspixeldungeon.items.Recipe.SimpleRecipe {
		
		{
			inputs =  new Class[]{ScrollOfRemoveCurse.class, MetalShard.class};
			inQuantity = new int[]{1, 1};
			
			cost = 1;
			
			output = CurseInfusion.class;
			outQuantity = 4;
		}
		
	}
}
