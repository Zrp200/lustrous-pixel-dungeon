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

package com.zrp200.lustrouspixeldungeon.actors.hero;

import com.watabou.utils.Bundle;
import com.zrp200.lustrouspixeldungeon.Assets;
import com.zrp200.lustrouspixeldungeon.Badges;
import com.zrp200.lustrouspixeldungeon.Challenges;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.items.BrokenSeal;
import com.zrp200.lustrouspixeldungeon.items.Item;
import com.zrp200.lustrouspixeldungeon.items.armor.Armor;
import com.zrp200.lustrouspixeldungeon.items.armor.ClothArmor;
import com.zrp200.lustrouspixeldungeon.items.artifacts.CloakOfShadows;
import com.zrp200.lustrouspixeldungeon.items.bags.MagicalHolster;
import com.zrp200.lustrouspixeldungeon.items.bags.PotionBandolier;
import com.zrp200.lustrouspixeldungeon.items.bags.ScrollHolder;
import com.zrp200.lustrouspixeldungeon.items.bags.VelvetPouch;
import com.zrp200.lustrouspixeldungeon.items.food.Food;
import com.zrp200.lustrouspixeldungeon.items.food.SmallRation;
import com.zrp200.lustrouspixeldungeon.items.potions.PotionOfHealing;
import com.zrp200.lustrouspixeldungeon.items.potions.PotionOfInvisibility;
import com.zrp200.lustrouspixeldungeon.items.potions.PotionOfLiquidFlame;
import com.zrp200.lustrouspixeldungeon.items.potions.PotionOfMindVision;
import com.zrp200.lustrouspixeldungeon.items.scrolls.ScrollOfIdentify;
import com.zrp200.lustrouspixeldungeon.items.scrolls.ScrollOfLullaby;
import com.zrp200.lustrouspixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.zrp200.lustrouspixeldungeon.items.scrolls.ScrollOfRage;
import com.zrp200.lustrouspixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.zrp200.lustrouspixeldungeon.items.wands.WandOfMagicMissile;
import com.zrp200.lustrouspixeldungeon.items.weapon.SpiritBow;
import com.zrp200.lustrouspixeldungeon.items.weapon.melee.Dagger;
import com.zrp200.lustrouspixeldungeon.items.weapon.melee.Gloves;
import com.zrp200.lustrouspixeldungeon.items.weapon.melee.MagesStaff;
import com.zrp200.lustrouspixeldungeon.items.weapon.melee.WornShortsword;
import com.zrp200.lustrouspixeldungeon.items.weapon.missiles.ThrowingKnife;
import com.zrp200.lustrouspixeldungeon.items.weapon.missiles.ThrowingStone;
import com.zrp200.lustrouspixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.zrp200.lustrouspixeldungeon.messages.Messages;

public enum HeroClass {

	WARRIOR("warrior", HeroSubClass.BERSERKER, HeroSubClass.GLADIATOR ) {
		@Override
		public void initHero(Hero hero) {
			super.initHero(hero);
			(hero.belongings.weapon = new WornShortsword()).identify();
			ThrowingStone stones = new ThrowingStone();
			stones.identify().quantity(3).collect();
			Dungeon.quickslot.setSlot(0, stones);

			if (hero.belongings.armor != null){
				hero.belongings.armor.affixSeal(new BrokenSeal());
			}

			new PotionBandolier().collect();

			new PotionOfHealing().identify();
			new ScrollOfRage().identify();
		}

		@Override
		public Badges.Badge masteryBadge() {
			return Badges.Badge.MASTERY_WARRIOR;
		}
	},
	MAGE( "mage", HeroSubClass.BATTLEMAGE, HeroSubClass.WARLOCK ) {
		@Override
		public void initHero(Hero hero) {
			super.initHero(hero);
			MagesStaff staff;

			staff = new MagesStaff(new WandOfMagicMissile());

			(hero.belongings.weapon = staff).identify();
			staff.activate(hero);

			Dungeon.quickslot.setSlot(0, staff);

			new ScrollHolder().collect();

			new ScrollOfUpgrade().identify();
			new PotionOfLiquidFlame().identify();
		}

		@Override
		public Badges.Badge masteryBadge() {
			return Badges.Badge.MASTERY_MAGE;
		}
	},
	ROGUE( "rogue", HeroSubClass.ASSASSIN, HeroSubClass.FREERUNNER ) {
		@Override
		public void initHero(Hero hero) {
			super.initHero(hero);
			(hero.belongings.weapon = new Dagger()).identify();

			CloakOfShadows cloak = new CloakOfShadows();
			(hero.belongings.misc1 = cloak).identify();
			hero.belongings.misc1.activate( hero );

			ThrowingKnife knives = new ThrowingKnife();
			knives.quantity(3).collect();

			Dungeon.quickslot.setSlot(0, cloak);
			Dungeon.quickslot.setSlot(1, knives);

			new VelvetPouch().collect();

			new ScrollOfMagicMapping().identify();
			new PotionOfInvisibility().identify();
		}

		@Override
		public Badges.Badge masteryBadge() {
			return Badges.Badge.MASTERY_ROGUE;
		}
	},
	HUNTRESS( "huntress", HeroSubClass.SNIPER, HeroSubClass.WARDEN ) {
		@Override
		public void initHero(Hero hero) {
			super.initHero(hero);

			(hero.belongings.weapon = new Gloves()).identify();
			SpiritBow bow = new SpiritBow();
			bow.identify().collect();

			Dungeon.quickslot.setSlot(0, bow);

			new MagicalHolster().collect();

			new PotionOfMindVision().identify();
			new ScrollOfLullaby().identify();
		}

		@Override
		public Badges.Badge masteryBadge() {
			return Badges.Badge.MASTERY_HUNTRESS;
		}
	};

	private String title;
	private HeroSubClass[] subClasses;

	HeroClass( String title, HeroSubClass...subClasses ) {
		this.title = title;
		this.subClasses = subClasses;
	}

	public void initHero( Hero hero ) {
		hero.heroClass = this;

		Item i = new ClothArmor().identify();

		if (!Challenges.isItemBlocked(i)) hero.belongings.armor = (Armor)i;
		i = new Food();
		if (!Challenges.isItemBlocked(i)) i.collect();

		if (Dungeon.isChallenged(Challenges.NO_FOOD))
			new SmallRation().collect();

		new ScrollOfIdentify().identify();
	}

	public abstract Badges.Badge masteryBadge();
	
	public String title() {
		return Messages.get(HeroClass.class, title);
	}
	
	public HeroSubClass[] subClasses() {
		return subClasses;
	}
	
	public String spritesheet() {
		switch (this) {
			case WARRIOR: default:
				return Assets.WARRIOR;
			case MAGE:
				return Assets.MAGE;
			case ROGUE:
				return Assets.ROGUE;
			case HUNTRESS:
				return Assets.HUNTRESS;
		}
	}
	
	public String[] perks() {
		String[] result = new String[5];
		for(int i = 0; i < 5; i++)
		    result[i] = Messages.get(HeroClass.class, title + "_perk" + (i+1) );
		return result;
	}

	private static final String CLASS	= "class";
	
	public void storeInBundle( Bundle bundle ) {
		bundle.put( CLASS, toString() );
	}
	
	public static HeroClass restoreInBundle( Bundle bundle ) {
		String value = bundle.getString( CLASS );
		return value.length() > 0 ? valueOf( value ) : ROGUE;
	}
}
