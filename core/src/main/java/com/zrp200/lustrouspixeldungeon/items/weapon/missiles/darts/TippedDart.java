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

package com.zrp200.lustrouspixeldungeon.items.weapon.missiles.darts;

import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.LustrousPixelDungeon;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.hero.HeroSubClass;
import com.zrp200.lustrouspixeldungeon.items.Generator;
import com.zrp200.lustrouspixeldungeon.items.Heap;
import com.zrp200.lustrouspixeldungeon.items.Item;
import com.zrp200.lustrouspixeldungeon.items.Recipe;
import com.zrp200.lustrouspixeldungeon.plants.Blindweed;
import com.zrp200.lustrouspixeldungeon.plants.Dreamfoil;
import com.zrp200.lustrouspixeldungeon.plants.Earthroot;
import com.zrp200.lustrouspixeldungeon.plants.Fadeleaf;
import com.zrp200.lustrouspixeldungeon.plants.Firebloom;
import com.zrp200.lustrouspixeldungeon.plants.Icecap;
import com.zrp200.lustrouspixeldungeon.plants.Plant;
import com.zrp200.lustrouspixeldungeon.plants.Rotberry;
import com.zrp200.lustrouspixeldungeon.plants.Sorrowmoss;
import com.zrp200.lustrouspixeldungeon.plants.Starflower;
import com.zrp200.lustrouspixeldungeon.plants.Stormvine;
import com.zrp200.lustrouspixeldungeon.plants.Sungrass;
import com.zrp200.lustrouspixeldungeon.plants.Swiftthistle;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class TippedDart extends Dart {
	
	{
		tier = 2;

		//so that slightly more than 1.5x durability is needed for 2 uses
		baseUses = 0.65f;

		durabilityScaling = 1.8f;
		enchantDurability = 1.25f;

	}
	
	//exact same damage as regular darts, despite being higher tier.


	private Dart untip() {
		Dart d = new Dart();
		d.quantity(quantity);
		d.level(level());
		d.enchantment = enchantment;
		return d;
	}

	@Override
	protected float durabilityPerUse() {
		float use = super.durabilityPerUse();

		if (Dungeon.hero.subClass == HeroSubClass.WARDEN){
			use /= 2f;
		}

		return use;
	}

	@Override
	protected void onRangedHit(Char enemy, int cell) {
		super.onRangedHit( enemy, cell);

		//need to spawn a dart
		if (durability <= 0){
			//attempt to stick the dart to the enemy, just drop it if we can't.
			Dart d = untip();
			d.quantity(1);
			if( !d.stickTo(enemy) ) d.drop(enemy.pos);
		}
	}

	private static HashMap<Class<?extends Plant.Seed>, Class<?extends TippedDart>> types = new HashMap<>();
	static {
		types.put(Blindweed.Seed.class,     BlindingDart.class);
		types.put(Dreamfoil.Seed.class,     SleepDart.class);
		types.put(Earthroot.Seed.class,     ParalyticDart.class);
		types.put(Fadeleaf.Seed.class,      DisplacingDart.class);
		types.put(Firebloom.Seed.class,     IncendiaryDart.class);
		types.put(Icecap.Seed.class,        ChillingDart.class);
		types.put(Rotberry.Seed.class,      RotDart.class);
		types.put(Sorrowmoss.Seed.class,    PoisonDart.class);
		types.put(Starflower.Seed.class,    HolyDart.class);
		types.put(Stormvine.Seed.class,     ShockingDart.class);
		types.put(Sungrass.Seed.class,      HealingDart.class);
		types.put(Swiftthistle.Seed.class,  AdrenalineDart.class);
	}
	
	public static TippedDart randomTipped(){
		Plant.Seed s;
		do{
			s = (Plant.Seed) Generator.random(Generator.Category.SEED);
		} while (!types.containsKey(s.getClass()));
		
		try{
			return (TippedDart) types.get(s.getClass()).newInstance().quantity(2);
		} catch (Exception e) {
			LustrousPixelDungeon.reportException(e);
			return null;
		}
		
	}
	
	public static class TipDart extends Recipe{
		
		@Override
		//also sorts ingredients if it can
		public boolean testIngredients(ArrayList<Item> ingredients) {
			if (ingredients.size() != 2) return false;
			
			if (ingredients.get(0).getClass() == Dart.class){
				if (!(ingredients.get(1) instanceof Plant.Seed)){
					return false;
				}
			} else if (ingredients.get(0) instanceof Plant.Seed){
				if (ingredients.get(1).getClass() == Dart.class){
					Item temp = ingredients.get(0);
					ingredients.set(0, ingredients.get(1));
					ingredients.set(1, temp);
				} else {
					return false;
				}
			} else {
				return false;
			}
			
			Plant.Seed seed = (Plant.Seed) ingredients.get(1);

            return ingredients.get(0).quantity() >= 1
                    && seed.quantity() >= 1
                    && types.containsKey(seed.getClass());

        }
		
		@Override
		public int cost(ArrayList<Item> ingredients) {
			return 0;
		}
		
		@Override
		public Item brew(ArrayList<Item> ingredients) {
			Item output = sampleOutput(ingredients);
			if(output == null) return null;
			int produced = output.quantity();
			
			ingredients.get(0).quantity(ingredients.get(0).quantity() - produced);
			ingredients.get(1).quantity(ingredients.get(1).quantity() - 1);
			
			return output;
			
		}
		
		@Override
		public Item sampleOutput(ArrayList<Item> ingredients) {
			if (!testIngredients(ingredients)) return null;
			Dart darts = (Dart) ingredients.get(0);
			int upgradeLevel = darts.level();
			Enchantment enchantment = darts.enchantment;
			
			try{
				int produced = Math.min(2, ingredients.get(0).quantity());
				TippedDart output = (TippedDart) (types.get(ingredients.get(1).getClass()).newInstance().quantity( produced ));
				output.level(upgradeLevel);
				output.enchantment = enchantment;
				return output;
			} catch (Exception e) {
				LustrousPixelDungeon.reportException(e);
				return null;
			}
		}
	}
	public static class UntipDart extends Recipe{
			@Override
			public boolean testIngredients(ArrayList<Item> ingredients) {
				for(Item ingredient1 : ingredients) for (Item ingredient2 : ingredients) {
					if (!(ingredient1 instanceof TippedDart && ingredient2 instanceof TippedDart))
						return false;
					Dart[] darts = {(Dart) ingredient1, (Dart) ingredient2};
					if (darts[0].level() != darts[1].level()) return false;
					if (	!(		darts[0].enchantment == darts[1].enchantment
											|| darts[0].enchantment.getClass().equals(darts[1].enchantment.getClass())
							)
						) return false;
				}
				return !ingredients.isEmpty();
			}

			@Override
			public int cost(ArrayList<Item> ingredients) {
				return 0;
			}

			@Override
			public Item brew(ArrayList<Item> ingredients) {
				Item output = sampleOutput(ingredients);
				if(output == null) return null;
				for(Item ingredient : ingredients) {
					ingredient.quantity(0);
				}
				return output;

			}

			@Override
			public Item sampleOutput(ArrayList<Item> ingredients) {
				if (!testIngredients(ingredients)) return null;

				try{
					int produced = 0;
					for(Item ingredient : ingredients) produced += ingredient.quantity();
					return ((TippedDart) ingredients.get(0)).untip().quantity(produced);
				} catch (Exception e) {
					LustrousPixelDungeon.reportException(e);
					return null;
				}
			}
	}
}
