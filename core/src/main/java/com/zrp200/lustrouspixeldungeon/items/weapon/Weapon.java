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
import com.zrp200.lustrouspixeldungeon.actors.mobs.Mob;
import com.zrp200.lustrouspixeldungeon.effects.ItemChange;
import com.zrp200.lustrouspixeldungeon.items.Item;
import com.zrp200.lustrouspixeldungeon.items.KindOfWeapon;
import com.zrp200.lustrouspixeldungeon.items.rings.RingOfFuror;
import com.zrp200.lustrouspixeldungeon.items.weapon.curses.Annoying;
import com.zrp200.lustrouspixeldungeon.items.weapon.curses.Chaotic;
import com.zrp200.lustrouspixeldungeon.items.weapon.curses.Displacing;
import com.zrp200.lustrouspixeldungeon.items.weapon.curses.Exhausting;
import com.zrp200.lustrouspixeldungeon.items.weapon.curses.Friendly;
import com.zrp200.lustrouspixeldungeon.items.weapon.curses.Necromantic;
import com.zrp200.lustrouspixeldungeon.items.weapon.curses.Polarized;
import com.zrp200.lustrouspixeldungeon.items.weapon.curses.Sacrificial;
import com.zrp200.lustrouspixeldungeon.items.weapon.curses.Wayward;
import com.zrp200.lustrouspixeldungeon.items.weapon.curses.WeaponCurse;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Blazing;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Blooming;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Chilling;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Dazzling;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Elastic;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Grim;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Lucky;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Precise;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Projecting;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Shocking;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Swift;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Unstable;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Vampiric;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSprite;
import com.zrp200.lustrouspixeldungeon.utils.GLog;

import java.util.ArrayList;
import java.util.Arrays;

abstract public class Weapon extends KindOfWeapon {

	private static final int HITS_TO_KNOW    = 20;

	protected float 	ACC = 1f;	// Accuracy modifier
	protected float 	DLY	= 1f;	// Speed modifier
	protected int   	RCH = 1;    // Reach modifier (only applies to melee hits)

	public int tier;

	@Override
	public boolean isEnchantable() {
		return true;
	}

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
	
	public Enchantment enchantment;
	public boolean enchantKnown = false;

	protected float surpriseToMax = 0f;

	protected Char findEnemy(Char owner) {
		return owner instanceof Hero ? ((Hero)owner).enemy() : null;
	}

	@Override
	public int damageRoll(Char owner) {
		if (findEnemy(owner) instanceof Mob && ((Mob) findEnemy(owner)).surprisedBy(owner)) {
			//deals % toward max to max on surprise, instead of min to max.
			int diff = max() - min();
			int damage = augment.damageFactor(Random.NormalIntRange(
					min() + Math.round(diff*surpriseToMax),
					max()));
			int exStr = owner instanceof Hero ? ((Hero) owner).STR() - STRReq() : 0;
			if (exStr > 0) {
				damage += Random.IntRange(0, exStr);
			}
			return damage;
		}
		return super.damageRoll(owner);
	}

	@Override
	public Item identify() {
		enchantKnown = true;
		return super.identify();
	}

    @Override
    public Weapon clone() {
        return (Weapon)super.clone();
    }

    @Override
	public boolean isIdentified() {
		return super.isIdentified() && enchantKnown;
	}

	public void revealEnchant() {
		enchantKnown = true;
		ItemChange.show(Dungeon.hero,this);
	}

	@Override
	public boolean doEquip(Hero hero) {
		if( hasHiddenEnchant() ) revealEnchant(); // make it obvious
		return super.doEquip(hero);
	}

	protected final boolean hasHiddenEnchant() {
		return hasEnchant() && !enchantKnown;
	}

	private static final int USES_TO_ID = 20;
	private int usesLeftToID = USES_TO_ID;
	private float availableUsesToID = USES_TO_ID/2f;

	@Override
	public int proc( Char attacker, Char defender, int damage ) {
		if (hasEnchant(attacker)) {
			damage = enchantment.proc( this, attacker, defender, damage );
		}

		if (attacker == Dungeon.hero) {
			processHit();
		}

		return damage;
	}

	public void processHit() {
        if (!levelKnown && availableUsesToID >= 1) {
            availableUsesToID--;
            usesLeftToID--;
            if (usesLeftToID <= 0) {
                identify();
                GLog.p( Messages.get(Weapon.class, "identify") );
                Badges.validateItemLevelAquired( this );
            }
        }
	}

	public String baseInfo() {
		String info = desc();
		int encumbrance = STRReq(levelKnown ? level() : 0) - Dungeon.hero.STR();

		if (levelKnown) {
			info += "\n\n" + Messages.get(this, "stats_known", tier, augment.damageFactor(min()), augment.damageFactor(max()), STRReq());
			if (encumbrance > 0) {
				info += " " + Messages.get(this, "too_heavy");
			} else if (encumbrance < 0){
				info += " " + Messages.get(this, "excess_str", -encumbrance);
			}
		} else {
			info += "\n\n" + Messages.get(this, "stats_unknown", tier, min(0), max(0), STRReq(0));
			if (encumbrance > 0) {
				info += " " + Messages.get(this, "probably_too_heavy");
			}
		}
		if (!statsInfo().equals(""))
			info += "\n\n" + statsInfo();
		return info;
	}

    @Override
	public String info() {
		String info = baseInfo();
		switch (augment) {
			case SPEED:
				info += "\n\n" + Messages.get(Weapon.class, "faster");
				break;
			case DAMAGE:
				info += "\n\n" + Messages.get(Weapon.class, "stronger");
				break;
			case NONE:
		}

		if ( isVisiblyEnchanted() ){
			info += "\n\n" + Messages.get(Weapon.class, "enchanted", enchantment.name());
			info += " " + Messages.get(enchantment, "desc");
		}

		if (cursed && isEquipped( Dungeon.hero )) {
			info += "\n\n" + Messages.get(Weapon.class, "cursed_worn");
		} else if ( visiblyCursed() ) {
			info += "\n\n" + Messages.get(Weapon.class, "cursed");
		} else if (!isIdentified() && cursedKnown){
			info += "\n\n" + Messages.get(Weapon.class, "not_cursed");
		}

		return info;
	}

	protected String statsInfo(){
		return Messages.get(this, "stats_desc");
	}

	@Override
	public Item virtual() {
		Weapon placeholder = (Weapon)super.virtual();
		placeholder.enchantment = enchantment;
		return placeholder;
	}

	private static final String
			ENCHANTMENT		= "enchantment",
			AUGMENT			= "augment",
			ENCHANTMENT_KNOWN = "enchant known",
            USES_LEFT_TO_ID = "uses_left_to_id",
            AVAILABLE_USES  = "available_uses";
	
	public void onHeroGainExp( float levelPercent, Hero hero ){
		if (!levelKnown && isEquipped(hero) && availableUsesToID <= USES_TO_ID/2f) {
			//gains enough uses to ID over 0.5 levels
			availableUsesToID = Math.min(USES_TO_ID/2f, availableUsesToID + levelPercent * USES_TO_ID);
		}
	}

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
        bundle.put( USES_LEFT_TO_ID, usesLeftToID );
        bundle.put( AVAILABLE_USES, availableUsesToID );
		bundle.put( ENCHANTMENT, enchantment );
		bundle.put( AUGMENT, augment );
		bundle.put( ENCHANTMENT_KNOWN, enchantKnown);
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		enchantment = (Enchantment)bundle.get( ENCHANTMENT );
		enchantKnown = bundle.getBoolean(ENCHANTMENT_KNOWN);
		usesLeftToID = bundle.getInt( USES_LEFT_TO_ID );
		availableUsesToID = bundle.getInt( AVAILABLE_USES );
		augment = bundle.getEnum(AUGMENT, Augment.class);
		if(augment == null) augment = Augment.NONE;
		
		//pre-0.7.2 saves
		if (bundle.contains( "unfamiliarity" )){
			usesLeftToID = bundle.getInt( "unfamiliarity" );
			availableUsesToID = USES_TO_ID/2f;
		}
	}
	
	@Override
	public void reset() {
		super.reset();
		usesLeftToID = USES_TO_ID;
		availableUsesToID = USES_TO_ID/2f;
	}
	
	@Override
	public float accuracyFactor( Char owner ) {
		
		int encumbrance = 0;
		
		if( owner instanceof Hero ){
			encumbrance = STRReq() - ((Hero)owner).STR();
		}
		if (hasEnchant(Wayward.class, owner) || hasEnchant(Chaotic.class, owner) && ((Chaotic) enchantment).randomCurse() instanceof Wayward )
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

		DLY *= RingOfFuror.attackDelayMultiplier(owner);

		return (encumbrance > 0 ? (float)(DLY * Math.pow( 1.2, encumbrance )) : DLY);
	}

	@Override
	public int reachFactor(Char owner) {
		return hasEnchant(Projecting.class, owner) ? RCH+1 : RCH;
	}

	@Override
	public int throwPos(Hero user, int dst, boolean assist) {
		if (hasEnchant(Projecting.class, user)
				&& !Dungeon.level.solid[dst] && Dungeon.level.distance(user.pos, dst) <= 5)
			return dst;
		return super.throwPos(user, dst, assist);
	}
	public int STRReq(){
		return STRReq(level());
	}

	public abstract int STRReq(int lvl);
	
	@Override
	public Item upgrade() {
		return upgrade(false);
	}

	@SuppressWarnings("unchecked")
	public Item upgrade(boolean enchant ) {
		if ( enchant && !hasEnchant() )
			enchant(Enchantment.random());
		else if(hasCurseEnchant() && Random.Int(3) == 0)
		    enchant(null);
		else if (hasGoodEnchant() && level() >= 4 && Random.Float(10) < Math.pow(2, level()-4))
			enchant(null);
		
		cursed = false;
		
		return super.upgrade();
	}
	public void augment(Augment augment) {
		this.augment = augment;
	}
	
	@Override
	public String name() {
		return isVisiblyEnchanted() && !hasCustomName() ? enchantment.name( super.name() ) : super.name();
	}
	@Override
	public int price() {
		int price = super.price()*tier;

		if ( isVisiblyEnchanted() ) {
			if( hasGoodEnchant() ) price *= 1.5;
			else price /= 1.5;
		}

		if (cursedKnown) {
			if(cursed) price *= enchantKnown ? 0.75 : 0.667;
			else if( !levelKnown ) price *= 1.25; // prize items usually.
		}

		if(visiblyUpgraded() > 0) price *= visiblyUpgraded();

		return Math.max(price, 1);
	}


	@SuppressWarnings("unchecked")
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
			enchant( Enchantment.randomCurse(), false );
			cursed = true;
		} else if (effectRoll >= 0.85f){
			enchant( false );
		}

		return this;
	}

	public Weapon enchant( Enchantment ench, boolean visible ) {
		enchantment = ench;
		if(visible) revealEnchant();
		updateQuickslot();
		return this;
	}

	public final Weapon enchant( Enchantment ench ) {
		return enchant(ench,true);
	}

	@SuppressWarnings("unchecked")
	public final Weapon enchant(boolean visible) {

		Class<? extends Enchantment> oldEnchantment = hasEnchant() ? enchantment.getClass() : null;
		Enchantment ench = Enchantment.random( oldEnchantment );
		return enchant( ench, visible );
	}

	public final Weapon enchant() {
		return enchant(true);
	}

	public boolean hasEnchant() { // shortcut
		return enchantment != null;
	}
	public boolean hasEnchant(Char owner) { // checks if enchant is active for a character
		return hasEnchant() && owner.buff(MagicImmune.class) == null;
	}
	public boolean hasEnchant(Class<?extends Enchantment> type) { // checks for a specific type of enchant
		return hasEnchant() && enchantment.getClass() == type || type == null && !hasEnchant();
	}
	public boolean hasEnchant(Class<?extends Enchantment> type, Char owner) { // combines everything
		return hasEnchant(type) && hasEnchant(owner);
	}

	@Override
	public boolean isSimilar(Item item) {
		if(!(item instanceof Weapon)) return false;
		Weapon w = (Weapon) item;
		return super.isSimilar(item)
				&& hasEnchant(w.hasEnchant() ? w.enchantment.getClass() : null)
				&& augment == w.augment;
	}
	//these are not used to process specific enchant effects, so magic immune doesn't affect them
	public boolean hasGoodEnchant(){
		return hasEnchant() && !hasCurseEnchant();
	}
	public boolean hasCurseEnchant(){ return enchantment instanceof WeaponCurse; }
	protected boolean isVisiblyEnchanted() { return hasEnchant() && enchantKnown; }

	@Override
	public ItemSprite.Glowing glowing() {
		return isVisiblyEnchanted() ? enchantment.glowing() : null;
	}

	public static abstract class Enchantment implements Bundlable {

		protected static final Class<?>[] common = new Class<?>[]{
				Precise.class, Chilling.class, Shocking.class, Blooming.class};
		protected static final Class<?>[] uncommon = new Class<?>[]{
				Blazing.class, Elastic.class, Projecting.class,
				Unstable.class, Lucky.class, Swift.class};
		protected static final Class<?>[] rare = new Class<?>[]{
				Grim.class, Vampiric.class, Dazzling.class};

		private static final float[] typeChances = new float[]{
				50, //12.5% each
				40, //6.67% each
				10  //3.33% each
		};

		@SuppressWarnings("unchecked")
		protected static final Class<?extends Weapon.Enchantment>[] curses = new Class[] {
				Annoying.class,		Displacing.class, 	Exhausting.class,
				Sacrificial.class,	Polarized.class, 	Friendly.class,
				Chaotic.class, 		Necromantic.class
		};


		public abstract int proc( Weapon weapon, Char attacker, Char defender, int damage );

		public String name() {
			return name( Messages.get(this, "enchant"));
		}

		public String name( String weaponName ) {
			return Messages.get(this, "name") + " " + weaponName;
		}

		public String desc() {
			return Messages.get(this, "desc");
		}

		public boolean curse() {
			return false;
		}

		@Override
		public void restoreFromBundle( Bundle bundle ) { }

		@Override
		public void storeInBundle( Bundle bundle ) { }
		
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

		@SuppressWarnings({"unchecked", "ConstantConditions"})
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
		public static Enchantment randomRare(Class<? extends Enchantment>... toIgnore) {
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

		@SuppressWarnings({"unchecked", "ConstantConditions"})
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
