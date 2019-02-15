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

package com.zrp200.lustrouspixeldungeon.journal;

import com.watabou.utils.Bundle;
import com.zrp200.lustrouspixeldungeon.Badges;
import com.zrp200.lustrouspixeldungeon.items.Generator.Category;
import com.zrp200.lustrouspixeldungeon.items.Item;
import com.zrp200.lustrouspixeldungeon.items.armor.ClothArmor;
import com.zrp200.lustrouspixeldungeon.items.armor.HuntressArmor;
import com.zrp200.lustrouspixeldungeon.items.armor.LeatherArmor;
import com.zrp200.lustrouspixeldungeon.items.armor.MageArmor;
import com.zrp200.lustrouspixeldungeon.items.armor.MailArmor;
import com.zrp200.lustrouspixeldungeon.items.armor.PlateArmor;
import com.zrp200.lustrouspixeldungeon.items.armor.RogueArmor;
import com.zrp200.lustrouspixeldungeon.items.armor.ScaleArmor;
import com.zrp200.lustrouspixeldungeon.items.armor.WarriorArmor;
import com.zrp200.lustrouspixeldungeon.items.wands.WandOfCorrosion;
import com.zrp200.lustrouspixeldungeon.items.weapon.melee.Cord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import static com.zrp200.lustrouspixeldungeon.items.Generator.Category.*;

public enum Catalog {
	
	WEAPONS (WEP_T1, WEP_T2, WEP_T3, WEP_T4, WEP_T5) {
	    @Override
        public boolean allSeen() {
			boolean cordSeen = seen.get(Cord.class);
			seen.put(Cord.class,true); // temporarily force it to be true
			boolean result = super.allSeen();
			seen.put(Cord.class,cordSeen);
			return result;
		} },

	ARMOR ( ClothArmor.class, LeatherArmor.class, MailArmor.class, ScaleArmor.class, PlateArmor.class,
			WarriorArmor.class, MageArmor.class, RogueArmor.class, HuntressArmor.class),

	WANDS       (WAND),
	RINGS       (RING),
	ARTIFACTS   (ARTIFACT),
	POTIONS	    (POTION),
	SCROLLS     (SCROLL);

	Catalog(Class<?extends Item>... itemClasses) {
		for(Class<?extends Item> itemClass : itemClasses) {
			seen.put(itemClass,false);
		}
	}
	Catalog(Category... categories) {
	    for(Category category : categories) for(Class<?extends Item> itemClass : (Class<?extends Item>[])category.classes) {
	        seen.put(itemClass,false);
        }
    }
	
	protected LinkedHashMap<Class<? extends Item>, Boolean> seen = new LinkedHashMap<>(); // needs to be public in order for
	
	public Collection<Class<? extends Item>> items(){
		return seen.keySet();
	}

	public boolean allSeen(){
		for (Class<?extends Item> item : items()){
			if (!seen.get(item)){
				return false;
			}
		}
		return true;
	}
	
	public static LinkedHashMap<Catalog, Badges.Badge> catalogBadges = new LinkedHashMap<>();
	static {
		catalogBadges.put(WEAPONS, Badges.Badge.ALL_WEAPONS_IDENTIFIED);
		catalogBadges.put(ARMOR, Badges.Badge.ALL_ARMOR_IDENTIFIED);
		catalogBadges.put(WANDS, Badges.Badge.ALL_WANDS_IDENTIFIED);
		catalogBadges.put(RINGS, Badges.Badge.ALL_RINGS_IDENTIFIED);
		catalogBadges.put(ARTIFACTS, Badges.Badge.ALL_ARTIFACTS_IDENTIFIED);
		catalogBadges.put(POTIONS, Badges.Badge.ALL_POTIONS_IDENTIFIED);
		catalogBadges.put(SCROLLS, Badges.Badge.ALL_SCROLLS_IDENTIFIED);
	}
	
	public static boolean isSeen(Class<? extends Item> itemClass){
		for (Catalog cat : values()) {
			if (cat.seen.containsKey(itemClass)) {
				return cat.seen.get(itemClass);
			}
		}
		return false;
	}
	
	public static void setSeen(Class<? extends Item> itemClass){
		for (Catalog cat : values()) {
			if (cat.seen.containsKey(itemClass) && !cat.seen.get(itemClass)) {
				cat.seen.put(itemClass, true);
				Journal.saveNeeded = true;
			}
		}
		Badges.validateItemsIdentified();
	}
	
	private static final String CATALOGS = "catalogs";
	
	public static void store( Bundle bundle ){
		
		Badges.loadGlobal();
		
		ArrayList<String> seen = new ArrayList<>();
		
		//if we have identified all items of a set, we use the badge to keep track instead.
		if (!Badges.isUnlocked(Badges.Badge.ALL_ITEMS_IDENTIFIED)) {
			for (Catalog cat : values()) {
				if (!Badges.isUnlocked(catalogBadges.get(cat))) {
					for (Class<? extends Item> item : cat.items()) {
						if (cat.seen.get(item)) seen.add(item.getSimpleName());
					}
				}
			}
		}
		
		bundle.put( CATALOGS, seen.toArray(new String[0]) );
		
	}
	
	public static void restore( Bundle bundle ){
		
		Badges.loadGlobal();

		//catalog-specific badge logic
		for (Catalog cat : values()){
			if (Badges.isUnlocked(catalogBadges.get(cat)) || Badges.isUnlocked(Badges.Badge.ALL_ITEMS_IDENTIFIED) ){
				for (Class<? extends Item> item : cat.items()) cat.seen.put(item, true);
			}
		}
		
		//general save/load
		if (bundle.contains(CATALOGS)) {
			List<String> seen = Arrays.asList(bundle.getStringArray(CATALOGS));
			
			//pre-0.6.3 saves
			//TODO should adjust this to tie into the bundling system's class array
			if (seen.contains("WandOfVenom")){
				WANDS.seen.put(WandOfCorrosion.class, true);
			}
			
			for (Catalog cat : values()) {
				for (Class<? extends Item> item : cat.items()) {
					if (seen.contains(item.getSimpleName())) {
						cat.seen.put(item, true);
					}
				}
			}
		}
	}
	
}
