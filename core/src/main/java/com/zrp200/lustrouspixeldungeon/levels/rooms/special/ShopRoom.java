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

package com.zrp200.lustrouspixeldungeon.levels.rooms.special;

import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Challenges;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.LustrousPixelDungeon;
import com.zrp200.lustrouspixeldungeon.actors.hero.Belongings;
import com.zrp200.lustrouspixeldungeon.actors.mobs.Mob;
import com.zrp200.lustrouspixeldungeon.actors.mobs.npcs.Shopkeeper;
import com.zrp200.lustrouspixeldungeon.items.Ankh;
import com.zrp200.lustrouspixeldungeon.items.Generator;
import com.zrp200.lustrouspixeldungeon.items.Heap;
import com.zrp200.lustrouspixeldungeon.items.Honeypot;
import com.zrp200.lustrouspixeldungeon.items.Item;
import com.zrp200.lustrouspixeldungeon.items.MerchantsBeacon;
import com.zrp200.lustrouspixeldungeon.items.Stylus;
import com.zrp200.lustrouspixeldungeon.items.Torch;
import com.zrp200.lustrouspixeldungeon.items.armor.Armor;
import com.zrp200.lustrouspixeldungeon.items.artifacts.TimekeepersHourglass;
import com.zrp200.lustrouspixeldungeon.items.bags.Bag;
import com.zrp200.lustrouspixeldungeon.items.bags.MagicalHolster;
import com.zrp200.lustrouspixeldungeon.items.bags.PotionBandolier;
import com.zrp200.lustrouspixeldungeon.items.bags.ScrollHolder;
import com.zrp200.lustrouspixeldungeon.items.bags.VelvetPouch;
import com.zrp200.lustrouspixeldungeon.items.bombs.Bomb;
import com.zrp200.lustrouspixeldungeon.items.food.SmallRation;
import com.zrp200.lustrouspixeldungeon.items.potions.Potion;
import com.zrp200.lustrouspixeldungeon.items.potions.PotionOfHealing;
import com.zrp200.lustrouspixeldungeon.items.scrolls.Scroll;
import com.zrp200.lustrouspixeldungeon.items.scrolls.ScrollOfIdentify;
import com.zrp200.lustrouspixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.zrp200.lustrouspixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.zrp200.lustrouspixeldungeon.items.spells.Spell;
import com.zrp200.lustrouspixeldungeon.items.stones.Runestone;
import com.zrp200.lustrouspixeldungeon.items.stones.StoneOfAugmentation;
import com.zrp200.lustrouspixeldungeon.items.wands.Wand;
import com.zrp200.lustrouspixeldungeon.items.weapon.missiles.MissileWeapon;
import com.zrp200.lustrouspixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.zrp200.lustrouspixeldungeon.levels.Level;
import com.zrp200.lustrouspixeldungeon.levels.Terrain;
import com.zrp200.lustrouspixeldungeon.levels.painters.Painter;
import com.zrp200.lustrouspixeldungeon.plants.Plant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ShopRoom extends SpecialRoom {

	private ArrayList<Item> itemsToSpawn = generateItems();
	
	@Override
	public int minWidth() {
		return Math.max(7, (int)(Math.sqrt(itemsToSpawn.size())+3));
	}
	
	@Override
	public int minHeight() {
		return Math.max(7, (int)(Math.sqrt(itemsToSpawn.size())+3));
	}
	
	public void paint( Level level ) {
		
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1, Terrain.EMPTY_SP );

		placeShopkeeper( level );

		placeItems( level );
		
		for (Door door : connected.values()) {
			door.set( Door.Type.REGULAR );
		}

	}

	protected void placeShopkeeper( Level level ) {

		int pos = level.pointToCell(center());

		Mob shopkeeper = new Shopkeeper();
		shopkeeper.pos = pos;
		level.mobs.add( shopkeeper );

	}

	protected void placeItems( Level level ){

		Point itemPlacement = new Point(entrance());
		if (itemPlacement.y == top){
			itemPlacement.y++;
		} else if (itemPlacement.y == bottom) {
			itemPlacement.y--;
		} else if (itemPlacement.x == left){
			itemPlacement.x++;
		} else {
			itemPlacement.x--;
		}

		for (Item item : itemsToSpawn) {

			if (itemPlacement.x == left+1 && itemPlacement.y != top+1){
				itemPlacement.y--;
			} else if (itemPlacement.y == top+1 && itemPlacement.x != right-1){
				itemPlacement.x++;
			} else if (itemPlacement.x == right-1 && itemPlacement.y != bottom-1){
				itemPlacement.y++;
			} else {
				itemPlacement.x--;
			}

			int cell = level.pointToCell(itemPlacement);

			if (level.heaps.get( cell ) != null) {
				do {
					cell = level.pointToCell(random());
				} while (level.heaps.get( cell ) != null || level.findMob( cell ) != null);
			}

			level.drop( item, cell ).type = Heap.Type.FOR_SALE;
		}

	}
	
	@SuppressWarnings({"ConstantConditions"})
	private static ArrayList<Item> generateItems() {
		final int tier = Math.min( 4, Math.max( 0, Dungeon.depth/5 ) );

		@SuppressWarnings("unchecked")
		ArrayList<Item> itemsToSpawn = new ArrayList() {
			{
				try {
					add( Generator.random( Generator.wepTiers[tier] ).getClass().newInstance().identify() );
					add( ( (Armor) Generator.Category.ARMOR.classes[tier].newInstance() ).identify() );
				} catch (Exception e) {
					LustrousPixelDungeon.reportException(e);
				}

				add(Generator.random(Generator.misTiers[tier]).quantity(2));
				add(TippedDart.randomTipped());
				add(new MerchantsBeacon());

				add(ChooseBag(Dungeon.hero.belongings));

				if (tier >= 4 || Dungeon.isChallenged(Challenges.DARKNESS)) {
					add( new Torch() );
					add( new Torch() );
					add( new Torch() );
				}


				add(new PotionOfHealing());
				for (int i = 0; i < 3; i++)
					add(Generator.random(Generator.Category.POTION));

				add(new ScrollOfIdentify());
				add(new ScrollOfRemoveCurse());
				add(new ScrollOfMagicMapping());
				add(Generator.random(Generator.Category.SCROLL));

				for (int i = 0; i < 2; i++)
					add(Random.Int(2) == 0 ?
							Generator.random(Generator.Category.POTION) :
							Generator.random(Generator.Category.SCROLL));

				add(new SmallRation());
				add(new SmallRation());

				switch (Random.Int(4)) {
					case 0:
						add(new Bomb());
						break;
					case 1:
					case 2:
						add(new Bomb.DoubleBomb());
						break;
					case 3:
						add(new Honeypot());
						break;
				}

				add(new Ankh());
				add(new StoneOfAugmentation());

				TimekeepersHourglass hourglass = Dungeon.hero.belongings.getItem(TimekeepersHourglass.class);
				if (hourglass != null) {
					int bags = 0;
					//creates the given float percent of the remaining bags to be dropped.
					//this way players who get the hourglass late can still max it, usually.
					switch (Dungeon.depth) {
						case 6:
							bags = (int) Math.ceil((5 - hourglass.sandBags) * 0.2f);
							break;
						case 11:
							bags = (int) Math.ceil((5 - hourglass.sandBags) * 0.25f);
							break;
						case 16:
							bags = (int) Math.ceil((5 - hourglass.sandBags) * 0.50f);
							break;
						case 21:
							bags = (int) Math.ceil((5 - hourglass.sandBags) * 0.80f);
							break;
					}

					for (int i = 1; i <= bags; i++) {
						add(new TimekeepersHourglass.sandBag());
						hourglass.sandBags++;
					}
				}
				Generator.Category[] rareCategories = new Generator.Category[]{
						Generator.Category.RING, Generator.Category.WAND, Generator.Category.ARTIFACT
				};
				if (Random.Int(10) == 0) {
					Item rare = Generator.random(Random.oneOf(rareCategories)).identify();
					rare.level(0);
					rare.cursed = false;
					add(rare);
				}
				if (Random.Int(10) >= 3) add(new Stylus());

				//hard limit is 63 items + 1 shopkeeper, as shops can't be bigger than 8x8=64 internally
				if (size() > 63)
					throw new RuntimeException("Shop attempted to carry more than 63 items!");
			}
		};

		Random.shuffle(itemsToSpawn);
		return itemsToSpawn;
	}

	@SuppressWarnings("ConstantConditions")
    protected static Bag ChooseBag(Belongings pack) {

        //0=pouch, 1=holder, 2=bandolier, 3=holster
        HashMap<Bag, Integer> bagItems = new HashMap<Bag, Integer>() {
            {
            	for(Class<?extends Bag> bagClass : Arrays.asList(
            			VelvetPouch.class, PotionBandolier.class,
						ScrollHolder.class,MagicalHolster.class ) )
            		add(bagClass);
            }

            void add(Class<?extends Bag> bagClass) {
				try { put(bagClass.newInstance(),0); } catch (Exception e) {
					LustrousPixelDungeon.reportException(e);
				}
			}

		};

        //count up items in the main bag
        for (Item item : pack.backpack.items) {
            for (Bag bag : bagItems.keySet()) {
                if ( item.getClass() == bag.getClass() ) {
                	bagItems.put(bag, -2);
                	bag.acquire();
				}
                if (bagItems.get(bag) == -2) continue;
                if (bag.grab(item)) bagItems.put(bag, bagItems.get(bag) + 1);
            }
        }
        Bag bestBag = null;
        int bestValue = -3;
        for (Bag bag : bagItems.keySet()) {
        	if( bag.dropped() ) bagItems.put(bag, Math.min(bagItems.get(bag),-1)); // a bit of padding hopefully for if shit goes wrong.
            int value = bagItems.get(bag);
            if (value > bestValue) {
                bestBag = bag;
                bestValue = value;
            }
        }
        return bestBag;
    }
}
