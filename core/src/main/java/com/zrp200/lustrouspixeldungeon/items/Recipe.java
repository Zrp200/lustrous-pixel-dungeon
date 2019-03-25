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

package com.zrp200.lustrouspixeldungeon.items;

import com.zrp200.lustrouspixeldungeon.LustrousPixelDungeon;
import com.zrp200.lustrouspixeldungeon.items.artifacts.AlchemistsToolkit;
import com.zrp200.lustrouspixeldungeon.items.bombs.Bomb;
import com.zrp200.lustrouspixeldungeon.items.food.Blandfruit;
import com.zrp200.lustrouspixeldungeon.items.food.MeatPie;
import com.zrp200.lustrouspixeldungeon.items.food.StewedMeat;
import com.zrp200.lustrouspixeldungeon.items.potions.AlchemicalCatalyst;
import com.zrp200.lustrouspixeldungeon.items.potions.Potion;
import com.zrp200.lustrouspixeldungeon.items.potions.brews.BlizzardBrew;
import com.zrp200.lustrouspixeldungeon.items.potions.brews.CausticBrew;
import com.zrp200.lustrouspixeldungeon.items.potions.brews.FrigidBrew;
import com.zrp200.lustrouspixeldungeon.items.potions.brews.FrostfireBrew;
import com.zrp200.lustrouspixeldungeon.items.potions.brews.InfernalBrew;
import com.zrp200.lustrouspixeldungeon.items.potions.brews.ShockingBrew;
import com.zrp200.lustrouspixeldungeon.items.potions.brews.WickedBrew;
import com.zrp200.lustrouspixeldungeon.items.potions.elixirs.ElixirOfAquaticRejuvenation;
import com.zrp200.lustrouspixeldungeon.items.potions.elixirs.ElixirOfDragonsBlood;
import com.zrp200.lustrouspixeldungeon.items.potions.elixirs.ElixirOfHoneyedHealing;
import com.zrp200.lustrouspixeldungeon.items.potions.elixirs.ElixirOfIcyTouch;
import com.zrp200.lustrouspixeldungeon.items.potions.elixirs.ElixirOfMight;
import com.zrp200.lustrouspixeldungeon.items.potions.elixirs.ElixirOfRestoration;
import com.zrp200.lustrouspixeldungeon.items.potions.elixirs.ElixirOfToxicEssence;
import com.zrp200.lustrouspixeldungeon.items.potions.elixirs.ElixirOfVitality;
import com.zrp200.lustrouspixeldungeon.items.potions.exotic.ExoticPotion;
import com.zrp200.lustrouspixeldungeon.items.scrolls.Scroll;
import com.zrp200.lustrouspixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.zrp200.lustrouspixeldungeon.items.spells.Alchemize;
import com.zrp200.lustrouspixeldungeon.items.spells.AquaBlast;
import com.zrp200.lustrouspixeldungeon.items.spells.ArcaneCatalyst;
import com.zrp200.lustrouspixeldungeon.items.spells.BeaconOfReturning;
import com.zrp200.lustrouspixeldungeon.items.spells.CurseInfusion;
import com.zrp200.lustrouspixeldungeon.items.spells.FeatherFall;
import com.zrp200.lustrouspixeldungeon.items.spells.MagicalInfusion;
import com.zrp200.lustrouspixeldungeon.items.spells.MagicalPorter;
import com.zrp200.lustrouspixeldungeon.items.spells.PhaseShift;
import com.zrp200.lustrouspixeldungeon.items.spells.ReclaimTrap;
import com.zrp200.lustrouspixeldungeon.items.spells.Recycle;
import com.zrp200.lustrouspixeldungeon.items.wands.Wand;
import com.zrp200.lustrouspixeldungeon.items.weapon.missiles.darts.Dart;
import com.zrp200.lustrouspixeldungeon.items.weapon.missiles.darts.TippedDart;

import java.util.ArrayList;

@SuppressWarnings("StaticInitializerReferencesSubClass")
public abstract class Recipe {
	
	public abstract boolean testIngredients(ArrayList<Item> ingredients);
	
	public abstract int cost(ArrayList<Item> ingredients);
	
	public abstract Item brew(ArrayList<Item> ingredients);
	
	public abstract Item sampleOutput(ArrayList<Item> ingredients);
	
	//subclass for the common situation of a recipe with static inputs and outputs
	public static abstract class SimpleRecipe extends Recipe {
		
		//*** These elements must be filled in by subclasses
		protected Class<?extends Item>[] inputs; //each class should be unique
		protected int[] inQuantity;
		
		protected int cost;
		
		protected Class<?extends Item> output;
		protected int outQuantity;
		//***
		
		//gets a simple list of items based on inputs
		public ArrayList<Item> getIngredients() {
			ArrayList<Item> result = new ArrayList<>();
			try {
				for (int i = 0; i < inputs.length; i++) {
					Item ingredient = inputs[i].newInstance();
					ingredient.quantity(inQuantity[i]);
					result.add(ingredient);
				}
			} catch (Exception e){
				LustrousPixelDungeon.reportException( e );
				return null;
			}
			return result;
		}
		
		@Override
		public final boolean testIngredients(ArrayList<Item> ingredients) {
			
			int[] needed = inQuantity.clone();
			
			//TODO is this right?
			for (Item ingredient : ingredients){
				if (!ingredient.isIdentified()) return false;
				for (int i = 0; i < inputs.length; i++){
					if (ingredient.getClass() == inputs[i]){
						needed[i] -= ingredient.quantity();
						break;
					}
				}
			}
			
			for (int i : needed){
				if (i > 0){
					return false;
				}
			}
			
			return true;
		}
		
		public final int cost(ArrayList<Item> ingredients){
			return cost;
		}
		
		@Override
		public final Item brew(ArrayList<Item> ingredients) {
			if (!testIngredients(ingredients)) return null;
			
			int[] needed = inQuantity.clone();
			
			for (Item ingredient : ingredients){
				for (int i = 0; i < inputs.length; i++) {
					if (ingredient.getClass() == inputs[i] && needed[i] > 0) {
						if (needed[i] <= ingredient.quantity()) {
							ingredient.quantity(ingredient.quantity() - needed[i]);
							needed[i] = 0;
						} else {
							needed[i] -= ingredient.quantity();
							ingredient.quantity(0);
						}
					}
				}
			}
			
			//sample output and real output are identical in this case.
			return sampleOutput(null);
		}
		
		//ingredients are ignored, as output doesn't vary
		public final Item sampleOutput(ArrayList<Item> ingredients){
			try {
				Item result = output.newInstance();
				result.quantity(outQuantity);
				return result;
			} catch (Exception e) {
				LustrousPixelDungeon.reportException( e );
				return null;
			}
		}
	}
	
	
	//*******
	// Static members
	//*******
	
	private static Recipe[] oneIngredientRecipes = new Recipe[]{
		new AlchemistsToolkit.upgradeKit(),
		new Scroll.ScrollToStone(),
		new StewedMeat.oneMeat(),
		new TippedDart.UntipDart()
	};
	
	private static Recipe[] twoIngredientRecipes = new Recipe[]{
		new Blandfruit.CookFruit(),
		new TippedDart.TipDart(),
		new TippedDart.UntipDart(),
		new Bomb.EnhanceBomb(),
		new AlchemicalCatalyst.Recipe(),
		new ArcaneCatalyst.Recipe(),
		new ElixirOfAquaticRejuvenation.Recipe(),
		new ElixirOfDragonsBlood.Recipe(),
		new ElixirOfIcyTouch.Recipe(),
		new ElixirOfMight.Recipe(),
		new ElixirOfHoneyedHealing.Recipe(),
		new ElixirOfToxicEssence.Recipe(),
		new BlizzardBrew.Recipe(),
		new InfernalBrew.Recipe(),
		new ShockingBrew.Recipe(),
		new CausticBrew.Recipe(),
		new Alchemize.Recipe(),
		new AquaBlast.Recipe(),
		new BeaconOfReturning.Recipe(),
		new CurseInfusion.Recipe(),
		new FeatherFall.Recipe(),
		new MagicalInfusion.Recipe(),
		new MagicalPorter.Recipe(),
		new PhaseShift.Recipe(),
		new ReclaimTrap.Recipe(),
		new Recycle.Recipe(),
		new StewedMeat.twoMeat()
	};
	
	private static Recipe[] threeIngredientRecipes = new Recipe[]{
		new Potion.SeedToPotion(),
		new ExoticPotion.PotionToExotic(),
		new ExoticScroll.ScrollToExotic(),
		new StewedMeat.threeMeat(),
		new TippedDart.UntipDart(),
		new MeatPie.Recipe()
	};
	
	public static Recipe findRecipe(ArrayList<Item> ingredients){
		
		if (ingredients.size() == 1){
			for (Recipe recipe : oneIngredientRecipes){
				if (recipe.testIngredients(ingredients)){
					return recipe;
				}
			}
			
		} else if (ingredients.size() == 2){
			for (Recipe recipe : twoIngredientRecipes){
				if (recipe.testIngredients(ingredients)){
					return recipe;
				}
			}
			
		} else if (ingredients.size() == 3){
			for (Recipe recipe : threeIngredientRecipes){
				if (recipe.testIngredients(ingredients)){
					return recipe;
				}
			}
		}
		
		return null;
	}
	
	public static boolean usableInRecipe(Item item){
		return !item.cursed
				&& (!(item instanceof EquipableItem) || item instanceof Dart || item instanceof AlchemistsToolkit)
				&& !(item instanceof Wand);
	}
}


