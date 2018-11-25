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

package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.food.*;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.*;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;

import static com.shatteredpixel.shatteredpixeldungeon.Challenges.NO_FOOD;
import static com.shatteredpixel.shatteredpixeldungeon.Challenges.NO_HERBALISM;

public class RingOfWealth extends Ring {
	
	private float triesToDrop = 0;
	
	@Override
	protected RingBuff buff( ) {
		return new Wealth();
	}
	
	public static float dropChanceMultiplier( Char target ){
		return (float)Math.pow(1.2, getBonus(target, Wealth.class)); // +4.3% compared to shattered
	}
	
	public static ArrayList<Item> tryRareDrop(Char target, int tries ){
		if (getBonus(target, Wealth.class) <= 0) return null;
		
		HashSet<Wealth> buffs = target.buffs(Wealth.class);
		float triesToDrop = -1;
		
		//find the largest count (if they aren't synced yet)
		for (Wealth w : buffs){
			if (w.triesToDrop() > triesToDrop){
				triesToDrop = w.triesToDrop();
			}
		}
		
		//reset (if needed), decrement, and store counts
		if (triesToDrop <= 0) triesToDrop += Random.NormalIntRange(15, 60); // 20% faster than Shattered
		triesToDrop -= dropProgression( target, tries );
		for (Wealth w : buffs){
			w.triesToDrop(triesToDrop);
		}
		
		//now handle reward logic
		if (triesToDrop <= 0){
			return generateRareDrop();
		} else {
			return null;
		}
		
	}
	
	//TODO this is a start, but i'm sure this could be made more interesting...
	private static ArrayList<Item> generateRareDrop(){
		float roll = Random.Float();
		ArrayList<Item> items = new ArrayList<>();
		if (roll < 0.6f){ // 60% chance
			items.add( Generator.random() ); // let's make this......interesting ;)
		} else if (roll < 0.9f){ // 30% chance
			switch (Random.Int(5) - ((Dungeon.isChallenged(NO_HERBALISM)) ? 1 : 0)){
				case 0:
					for(int i = 0; i < 5; i++)
						items.add(	Generator.random(
								Random.Int(2) == 0
									? Generator.Category.SEED
									: Generator.Category.STONE
								)
						);
					break;
				case 1:
					items.add(Generator.random(Random.Int(2) == 0 ? Generator.Category.POTION : Generator.Category.SCROLL ));
					items.add(Generator.random(Random.Int(2) == 0 ? Generator.Category.POTION : Generator.Category.SCROLL ));
					items.add(Generator.random(Random.Int(2) == 0 ? Generator.Category.POTION : Generator.Category.SCROLL ));
					break;
				case 2:
					items.add(new Bomb().random());
					items.add(new Honeypot());
					break;
				case 3:
					if(Dungeon.isChallenged(NO_FOOD))
						items.add(new SmallRation()); // Yeah no 3 rations for you.
					else
						switch(Random.Int(8)) {
							case 0:
								items.add(new MeatPie());
								break;
							case 1:
								Blandfruit blandfruit = new Blandfruit();
								if(Random.Int(3) == 0)
									blandfruit.cook((Plant.Seed) Generator.random(Generator.Category.SEED));
								items.add(blandfruit);
								break;
							case 2:
								items.add( new Pasty().quantity( Random.Int(1,2) ) );
								break;
							case 3:
								items.add( new Food().quantity( Random.Int(1,3) ) );
								break;
							case 4:
								items.add( new FrozenCarpaccio().quantity( Random.Int(1,4) ) );
								break;
							case 5:
								items.add( new ChargrilledMeat().quantity( Random.Int(1,5) ) ); // Stronger than small ration because alchemy
								break;
							case 6:
								items.add( new MysteryMeat().quantity( Random.Int(1,6) ) );
								break;
							case 7:
								items.add( new SmallRation().quantity( Random.Int(1,6) ) );
								break;
						}
				case 4:
					items.add(new Dewdrop().quantity(Random.NormalIntRange(5,15))); // Dew
                    break;
			}
		} else if (roll < 0.99f){
			switch(2) {
				case 0:
					Gold g = new Gold();
					g.random();
					g.quantity(g.quantity() * 5);
					items.add(g);
					break;
				case 1:
					Item item;
					do {
						item = Generator.random();
					} while (!(
							item.isUpgradable() ||
									item instanceof Artifact ||
									item instanceof MissileWeapon
					));
					if(Random.Int(2) == 0) { // Prize-ify
						int floorset = Dungeon.depth / 5;
						if (item instanceof MissileWeapon) {
							item = Generator.randomMissile(floorset + 1);
							items.add(item);
							break; // Nothing after this point affects it.
						}
						if (item instanceof MeleeWeapon)
							item = Generator.randomWeapon(floorset + 1);
						if (item instanceof Armor) item = Generator.randomArmor(floorset + 1);
						item.cursed = false;
						item.cursedKnown = true;
						if (item.isUpgradable() && Random.Int(2) == 0) item.upgrade();
					}
					items.add(item);
					break;
			}
		}
		else {
			switch(Random.Int(5)) {
				case 0:
					items.add(new Ankh());
					break;
				case 1:
					items.add(new StoneOfEnchantment());
					break;
				case 2:
					items.add(new ScrollOfTransmutation());
					break;
				case 3:
					items.add(new Dewdrop().quantity(20)); // A full heal.
					break;
				case 4:
					Item g = new Gold().random();
					g.quantity(g.quantity() * 10); // You won the lottery!
					items.add(g);
					break;
			}
		}
		for(int i = 0; i < items.size(); i++) if(Random.Int(5) == 0) { // 20% chance to get an exotic instead O_O
			Item item = items.get(i);
			if(item instanceof Scroll) item = ScrollOfTransmutation.changeScroll( (Scroll) item );
			else if(item instanceof Potion) item = ScrollOfTransmutation.changePotion( (Potion) item );
			items.set(i, item);
		}
		return items;
	}
	
	private static float dropProgression( Char target, int tries ){
		return tries * (float)Math.pow(1.2f, getBonus(target, Wealth.class) - 1 );
	}

	public class Wealth extends RingBuff {
		
		private void triesToDrop( float val){
			triesToDrop = val;
		}
		
		private float triesToDrop(){
			return triesToDrop;
		}
		
		
	}
}
