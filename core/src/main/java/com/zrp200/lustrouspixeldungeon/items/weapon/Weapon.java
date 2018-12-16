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

package com.zrp200.lustrouspixeldungeon.items.weapon;

import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Badges;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.LustrousPixelDungeon;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.MagicImmune;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.effects.ItemChange;
import com.zrp200.lustrouspixeldungeon.items.Item;
import com.zrp200.lustrouspixeldungeon.items.KindOfWeapon;
import com.zrp200.lustrouspixeldungeon.items.rings.RingOfFuror;
import com.zrp200.lustrouspixeldungeon.items.weapon.curses.Annoying;
import com.zrp200.lustrouspixeldungeon.items.weapon.curses.Displacing;
import com.zrp200.lustrouspixeldungeon.items.weapon.curses.Elastic;
import com.zrp200.lustrouspixeldungeon.items.weapon.curses.Exhausting;
import com.zrp200.lustrouspixeldungeon.items.weapon.curses.Fragile;
import com.zrp200.lustrouspixeldungeon.items.weapon.curses.Friendly;
import com.zrp200.lustrouspixeldungeon.items.weapon.curses.Malevolent;
import com.zrp200.lustrouspixeldungeon.items.weapon.curses.Sacrificial;
import com.zrp200.lustrouspixeldungeon.items.weapon.curses.Wayward;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Blazing;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Chilling;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Dazzling;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Eldritch;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Grim;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Lucky;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Projecting;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Shocking;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Stunning;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Unstable;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Vampiric;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Venomous;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Vorpal;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSprite;
import com.zrp200.lustrouspixeldungeon.utils.GLog;

import java.util.ArrayList;
import java.util.Arrays;

abstract public class Weapon extends KindOfWeapon {

	private static final int HITS_TO_KNOW    = 20;

	public float    ACC = 1f;	// Accuracy modifier
	public float	DLY	= 1f;	// Speed modifier
	public int      RCH = 1;    // Reach modifier (only applies to melee hits)

	public enum Augment {
		SPEED   (0.7f, 0.6667f),
		DAMAGE  (1.5f, 1.6667f),
		NONE	(1.0f, 1.0000f);

		private float damageFactor;
		private float delayFactor;

		Augment(float dmg, float dly){
			damageFactor = dmg;
			delayFactor = dly;
		}

		public int damageFactor(int dmg){
			return Math.round(dmg * damageFactor);
		}

		public float delayFactor(float dly){
			return dly * delayFactor;
		}
	}
	
	public Augment augment = Augment.NONE;

	private int hitsToKnow = HITS_TO_KNOW;
	
	public Enchantment enchantment;
	public boolean enchantKnown = false;

	@Override
	public Item identify() {
		enchantKnown = true;
		return super.identify();
	}

	@Override
	public boolean doEquip(Hero hero) {
		boolean enchantUnknown = !enchantKnown;
		enchantKnown = true;
		if(enchantUnknown && enchantment != null) ItemChange.show(hero,this); // make it obvious
		return super.doEquip(hero);
	}

	@Override
	public int proc( Char attacker, Char defender, int damage ) {
		
		if (enchantment != null && attacker.buff(MagicImmune.class) == null) {
			damage = enchantment.proc( this, attacker, defender, damage );
		}
		
		if (!levelKnown && attacker == Dungeon.hero) {
			if (--hitsToKnow <= 0) {
				identify();
				GLog.i( Messages.get(Weapon.class, "identify") );
				Badges.validateItemLevelAquired( this );
			}
		}

		return damage;
	}

	private static final String
			UNFAMILIRIARITY	= "unfamiliarity",
			ENCHANTMENT		= "enchantment",
			AUGMENT			= "augment",
			ENCHANTMENT_KNOWN = "enchant known";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( UNFAMILIRIARITY, hitsToKnow );
		bundle.put( ENCHANTMENT, enchantment );
		bundle.put( AUGMENT, augment );
		bundle.put( ENCHANTMENT_KNOWN, enchantKnown);
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		hitsToKnow = bundle.getInt( UNFAMILIRIARITY );
		enchantment = (Enchantment)bundle.get( ENCHANTMENT );
		enchantKnown = bundle.getBoolean(ENCHANTMENT_KNOWN);
		
		//pre-0.6.5 saves
		if (bundle.contains( "imbue" )){
			String imbue = bundle.getString( "imbue" );
			if (imbue.equals( "LIGHT" ))        augment = Augment.SPEED;
			else if (imbue.equals( "HEAVY" ))   augment = Augment.DAMAGE;
			else                                augment = Augment.NONE;
		} else {
			augment = bundle.getEnum(AUGMENT, Augment.class);
		}
	}
	
	@Override
	public float accuracyFactor( Char owner ) {
		
		int encumbrance = 0;
		
		if( owner instanceof Hero ){
			encumbrance = STRReq() - ((Hero)owner).STR();
		}

		if (hasEnchant(Wayward.class, owner) || hasEnchant(Malevolent.class, owner) && ((Malevolent) enchantment).randomCurse() instanceof Wayward )
			encumbrance = Math.max(2, encumbrance+2);

		float ACC = this.ACC;

		return encumbrance > 0 ? (float)(ACC / Math.pow( 1.5, encumbrance )) : ACC;
	}
	
	@Override
	public float speedFactor( Char owner ) {

		int encumbrance = 0;
		if (owner instanceof Hero) {
			encumbrance = STRReq() - ((Hero)owner).STR();
		}

		float DLY = augment.delayFactor(this.DLY);

		DLY = RingOfFuror.modifyAttackDelay(DLY, owner);

		return (encumbrance > 0 ? (float)(DLY * Math.pow( 1.2, encumbrance )) : DLY);
	}

	@Override
	public int reachFactor(Char owner) {
		return hasEnchant(Projecting.class, owner) ? RCH+1 : RCH;
	}

	public int STRReq(){
		return STRReq(level());
	}

	public abstract int STRReq(int lvl);
	
	@Override
	public Item upgrade() {
		return upgrade(false);
	}
	
	public Item upgrade(boolean enchant ) {
		if (enchant && (enchantment == null || enchantment.curse())){
			enchant( Enchantment.random() );
		} else if (!enchant && Random.Float() > Math.pow(0.9, level())){
			enchant(null);
		}
		
		cursed = false;
		
		return super.upgrade();
	}
	
	@Override
	public String name() {
		return isVisiblyEnchanted() ? enchantment.name( super.name() ) : super.name();
	}
	
	@Override
	public Item random() {
		//+0: 75% (3/4)
		//+1: 20% (4/20)
		//+2: 5%  (1/20)
		int n = 0;
		if (Random.Int(4) == 0) {
			n++;
			if (Random.Int(5) == 0) {
				n++;
			}
		}
		level(n);
		
		//30% chance to be cursed
		//15% chance to be enchanted (+50%)
		float effectRoll = Random.Float();
		if (effectRoll < 0.3f) {
			enchant(Enchantment.randomCurse());
			cursed = true;
		} else if (effectRoll >= 0.85f){
			enchant();
		}
		enchantKnown = false;

		return this;
	}
	
	public Weapon enchant( Enchantment ench ) {
		enchantment = ench;
		enchantKnown = true; // easier just to manually turn this off.
		return this;
	}

	public Weapon enchant() {

		Class<? extends Enchantment> oldEnchantment = enchantment != null ? enchantment.getClass() : null;
		Enchantment ench = Enchantment.random( oldEnchantment );

		return enchant( ench );
	}

	public boolean hasEnchant(Class<?extends Enchantment> type, Char owner) {
		return enchantment != null && enchantment.getClass() == type && owner.buff(MagicImmune.class) == null;
	}
	
	//these are not used to process specific enchant effects, so magic immune doesn't affect them
	public boolean hasGoodEnchant(){
		return enchantment != null && !enchantment.curse();
	}
	public boolean hasCurseEnchant(){
		return enchantment != null && enchantment.curse();
	}
	public boolean isVisiblyEnchanted() { return enchantment != null && enchantKnown; }

	@Override
	public ItemSprite.Glowing glowing() {
		return isVisiblyEnchanted() ? enchantment.glowing() : null;
	}

	public static abstract class Enchantment implements Bundlable {
		
		private static final Class<?>[] common = new Class<?>[]{
				Blazing.class, Venomous.class, Vorpal.class, Shocking.class};
		
		private static final Class<?>[] uncommon = new Class<?>[]{
				Chilling.class, Eldritch.class, Lucky.class,
				Projecting.class, Unstable.class, Dazzling.class};
		
		private static final Class<?>[] rare = new Class<?>[]{
				Grim.class, Stunning.class, Vampiric.class};
		
		private static final float[] typeChances = new float[]{
				50, //12.5% each
				40, //6.67% each
				10  //3.33% each
		};
		
		@SuppressWarnings("unchecked")
		protected static final Class<?extends Weapon.Enchantment>[] curses = new Class[] {
				Annoying.class, Displacing.class, Exhausting.class, Fragile.class,
				Sacrificial.class, Wayward.class, Elastic.class, Friendly.class,
				Malevolent.class
		};


		public abstract int proc( Weapon weapon, Char attacker, Char defender, int damage );

		public String name() {
			if (!curse())
				return name( Messages.get(this, "enchant"));
			else
				return name( Messages.get(Item.class, "curse"));
		}

		public String name( String weaponName ) {
			return Messages.get(this, "name", weaponName);
		}

		public String desc() {
			return Messages.get(this, "desc");
		}

		public boolean curse() {
			return false;
		}

		@Override
		public void restoreFromBundle( Bundle bundle ) {
		}

		@Override
		public void storeInBundle( Bundle bundle ) {
		}
		
		public abstract ItemSprite.Glowing glowing();
		
		@SuppressWarnings("unchecked")
		public static Enchantment random( Class<? extends Enchantment> ... toIgnore ) {
			switch(Random.chances(typeChances)){
				case 0: default:
					return randomCommon( toIgnore );
				case 1:
					return randomUncommon( toIgnore );
				case 2:
					return randomRare( toIgnore );
			}
		}
		
		@SuppressWarnings("unchecked")
		public static Enchantment randomCommon( Class<? extends Enchantment> ... toIgnore ) {
			try {
				ArrayList<Class<?>> enchants = new ArrayList<>(Arrays.asList(common));
				enchants.removeAll(Arrays.asList(toIgnore));
				if (enchants.isEmpty()) {
					return random();
				} else {
					return (Enchantment) Random.element(enchants).newInstance();
				}
			} catch (Exception e) {
				LustrousPixelDungeon.reportException(e);
				return null;
			}
		}
		
		@SuppressWarnings("unchecked")
		public static Enchantment randomUncommon( Class<? extends Enchantment> ... toIgnore ) {
			try {
				ArrayList<Class<?>> enchants = new ArrayList<>(Arrays.asList(uncommon));
				enchants.removeAll(Arrays.asList(toIgnore));
				if (enchants.isEmpty()) {
					return random();
				} else {
					return (Enchantment) Random.element(enchants).newInstance();
				}
			} catch (Exception e) {
				LustrousPixelDungeon.reportException(e);
				return null;
			}
		}
		
		@SuppressWarnings("unchecked")
		public static Enchantment randomRare( Class<? extends Enchantment> ... toIgnore ) {
			try {
				ArrayList<Class<?>> enchants = new ArrayList<>(Arrays.asList(rare));
				enchants.removeAll(Arrays.asList(toIgnore));
				if (enchants.isEmpty()) {
					return random();
				} else {
					return (Enchantment) Random.element(enchants).newInstance();
				}
			} catch (Exception e) {
				LustrousPixelDungeon.reportException(e);
				return null;
			}
		}

		@SuppressWarnings("unchecked")
		public static Enchantment randomCurse( Class<? extends Enchantment> ... toIgnore ){
			try {
				ArrayList<Class<?extends Enchantment>> enchants = new ArrayList<>(Arrays.asList(curses));
				enchants.removeAll(Arrays.asList(toIgnore));
				if (enchants.isEmpty()) {
					return random();
				} else {
					return Random.element(enchants).newInstance();
				}
			} catch (Exception e) {
				LustrousPixelDungeon.reportException(e);
				return null;
			}
		}
		
	}
}
