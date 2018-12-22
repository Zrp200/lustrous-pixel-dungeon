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

package com.zrp200.lustrouspixeldungeon.items.scrolls;

import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Challenges;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.LustrousPixelDungeon;
import com.zrp200.lustrouspixeldungeon.effects.ItemChange;
import com.zrp200.lustrouspixeldungeon.items.EquipableItem;
import com.zrp200.lustrouspixeldungeon.items.Generator;
import com.zrp200.lustrouspixeldungeon.items.Item;
import com.zrp200.lustrouspixeldungeon.items.Stylus;
import com.zrp200.lustrouspixeldungeon.items.artifacts.Artifact;
import com.zrp200.lustrouspixeldungeon.items.potions.Potion;
import com.zrp200.lustrouspixeldungeon.items.potions.brews.Brew;
import com.zrp200.lustrouspixeldungeon.items.potions.elixirs.Elixir;
import com.zrp200.lustrouspixeldungeon.items.potions.exotic.ExoticPotion;
import com.zrp200.lustrouspixeldungeon.items.rings.Ring;
import com.zrp200.lustrouspixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.zrp200.lustrouspixeldungeon.items.stones.Runestone;
import com.zrp200.lustrouspixeldungeon.items.stones.StoneOfEnchantment;
import com.zrp200.lustrouspixeldungeon.items.wands.Wand;
import com.zrp200.lustrouspixeldungeon.items.weapon.Weapon;
import com.zrp200.lustrouspixeldungeon.items.weapon.melee.MagesStaff;
import com.zrp200.lustrouspixeldungeon.items.weapon.melee.MeleeWeapon;
import com.zrp200.lustrouspixeldungeon.journal.Catalog;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.plants.Plant;
import com.zrp200.lustrouspixeldungeon.utils.GLog;
import com.zrp200.lustrouspixeldungeon.windows.WndBag;

public class ScrollOfTransmutation extends InventoryScroll {
	
	{
		initials = 10;
		mode = WndBag.Mode.TRANMSUTABLE;
		
		bones = true;
		unique = true;
	}
	
	public static boolean canTransmute(Item item){
		return item instanceof MeleeWeapon ||
				(item instanceof Potion && !(item instanceof Elixir || item instanceof Brew)) ||
				item instanceof Scroll ||
				item instanceof Ring ||
				item instanceof Wand ||
				item instanceof Plant.Seed ||
				item instanceof Runestone ||
				item instanceof Artifact
                || item instanceof Stylus;
	}
	
	@Override
	protected void onItemSelected(Item item) {
		
		Item result;
		
		if (item instanceof MagesStaff) {
			result = changeStaff( (MagesStaff)item );
		} else if (item instanceof MeleeWeapon) {
			result = changeWeapon( (MeleeWeapon)item );
		} else if (item instanceof Scroll) {
			result = changeScroll( (Scroll)item );
		} else if (item instanceof Potion) {
			result = changePotion( (Potion)item );
		} else if (item instanceof Ring) {
			result = changeRing( (Ring)item );
		} else if (item instanceof Wand) {
			result = changeWand( (Wand)item );
		} else if (item instanceof Plant.Seed) {
			result = changeSeed((Plant.Seed) item);
		} else if (item instanceof Runestone) {
			result = changeStone((Runestone) item);
		} else if (item instanceof Artifact) {
			result = changeArtifact((Artifact) item);
		} else if(item instanceof Stylus) {
			result = new StoneOfEnchantment();
		} else {
			result = null;
		}
		
		if (result == null){
			//This shouldn't ever trigger
			GLog.n( Messages.get(this, "nothing") );
			curItem.collect( curUser.belongings.backpack );
		} else {
			if (item.isEquipped(Dungeon.hero)){
				item.cursed = false; //to allow it to be unequipped
				((EquipableItem)item).doUnequip(Dungeon.hero, false);
				((EquipableItem)result).doEquip(Dungeon.hero);
			} else {
				item.detach(Dungeon.hero.belongings.backpack);
				if (!result.collect()){
					Dungeon.level.drop(result, curUser.pos).sprite.drop();
				}
			}
			if (result.isIdentified()){
				Catalog.setSeen(result.getClass());
			}
			//GLog.p( Messages.get(this, "morph") );
			ItemChange.show(Dungeon.hero,result);
		}
		
	}
	
	public static MagesStaff changeStaff( MagesStaff staff ){
		Class<?extends Wand> wandClass = staff.wandClass();
		
		if (wandClass == null){
			return null;
		} else {
			Wand n;
			do {
				n = (Wand) Generator.random(Generator.Category.WAND);
			} while (Challenges.isItemBlocked(n) || n.getClass() == wandClass);
			n.level(0);
			staff.imbueWand(n, null);
		}
		
		return staff;
	}
	
	public static Weapon changeWeapon(MeleeWeapon w ) {
		
		Weapon n;
		Generator.Category c = Generator.wepTiers[w.tier-1];
		
		do {
			try {
				n = (MeleeWeapon)c.classes[Random.chances(c.probs)].newInstance();
			} catch (Exception e) {
				LustrousPixelDungeon.reportException(e);
				return null;
			}
		} while (Challenges.isItemBlocked(n) || n.getClass() == w.getClass());
		
		int level = w.level();
		if (level > 0) {
			n.upgrade( level );
		} else if (level < 0) {
			n.degrade( -level );
		}
		
		n.enchantment = w.enchantment;
		n.levelKnown = w.levelKnown;
		n.cursedKnown = w.cursedKnown;
		n.cursed = w.cursed;
		n.augment = w.augment;
		
		return n;
		
	}
	
	public static Ring changeRing( Ring r ) {
		Ring n;
		do {
			n = (Ring)Generator.random( Generator.Category.RING );
		} while (Challenges.isItemBlocked(n) || n.getClass() == r.getClass());
		
		n.level(0);
		
		int level = r.level();
		if (level > 0) {
			n.upgrade( level );
		} else if (level < 0) {
			n.degrade( -level );
		}
		
		n.levelKnown = r.levelKnown;
		n.cursedKnown = r.cursedKnown;
		n.cursed = r.cursed;
		
		return n;
	}
	
	public static Artifact changeArtifact( Artifact a ) {
		Artifact n = Generator.randomArtifact();
		
		if (n != null && !Challenges.isItemBlocked(n)){
			n.cursedKnown = a.cursedKnown;
			n.cursed = a.cursed;
			n.levelKnown = a.levelKnown;
			n.transferUpgrade(a.visiblyUpgraded());
			return n;
		}
		
		return null;
	}
	
	public static Wand changeWand( Wand w ) {
		
		Wand n;
		do {
			n = (Wand)Generator.random( Generator.Category.WAND );
		} while ( Challenges.isItemBlocked(n) || n.getClass() == w.getClass());
		
		n.level( 0 );
		n.upgrade( w.level() );
		
		n.levelKnown = w.levelKnown;
		n.cursedKnown = w.cursedKnown;
		n.cursed = w.cursed;
		
		return n;
	}
	
	public static Plant.Seed changeSeed( Plant.Seed s ) {
		
		Plant.Seed n;
		
		do {
			n = (Plant.Seed)Generator.random( Generator.Category.SEED );
		} while (n.getClass() == s.getClass());
		
		return n;
	}

	private Runestone changeStone( Runestone r ) {

		Runestone n;

		do {
			n = (Runestone) Generator.random( Generator.Category.STONE );
		} while (n.getClass() == r.getClass());

		return n;
	}
	
	@SuppressWarnings({"SuspiciousMethodCalls", "ConstantConditions"})
	public static Scroll changeScroll(Scroll s ) {
		try {
			if (s instanceof ExoticScroll) {
				return ExoticScroll.exoToReg.get(s.getClass()).newInstance();
			} else {
				return ExoticScroll.regToExo.get(s.getClass()).newInstance();
			}
		} catch ( Exception e ){
			LustrousPixelDungeon.reportException(e);
			return null;
		}
	}
	
	public static Potion changePotion( Potion p ) {
		try {
			if (p instanceof ExoticPotion) {
				return ExoticPotion.exoToReg.get(p.getClass()).newInstance();
			} else {
				return ExoticPotion.regToExo.get(p.getClass()).newInstance();
			}
		} catch ( Exception e ){
			LustrousPixelDungeon.reportException(e);
			return null;
		}
	}
	
	@Override
	public void empoweredRead() {
		//does nothing, this shouldn't happen
	}
	
	@Override
	public int price() {
		return isKnown() ? 50 * quantity : super.price();
	}
}
