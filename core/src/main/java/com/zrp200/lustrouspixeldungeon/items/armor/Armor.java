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

package com.zrp200.lustrouspixeldungeon.items.armor;

import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Badges;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.LustrousPixelDungeon;
import com.zrp200.lustrouspixeldungeon.actors.Actor;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.MagicImmune;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Momentum;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.effects.ItemChange;
import com.zrp200.lustrouspixeldungeon.effects.Speck;
import com.zrp200.lustrouspixeldungeon.items.BrokenSeal;
import com.zrp200.lustrouspixeldungeon.items.EquipableItem;
import com.zrp200.lustrouspixeldungeon.items.Item;
import com.zrp200.lustrouspixeldungeon.items.armor.curses.AntiEntropy;
import com.zrp200.lustrouspixeldungeon.items.armor.curses.Bulk;
import com.zrp200.lustrouspixeldungeon.items.armor.curses.Corrosion;
import com.zrp200.lustrouspixeldungeon.items.armor.curses.Displacement;
import com.zrp200.lustrouspixeldungeon.items.armor.curses.Metabolism;
import com.zrp200.lustrouspixeldungeon.items.armor.curses.Multiplicity;
import com.zrp200.lustrouspixeldungeon.items.armor.curses.Overgrowth;
import com.zrp200.lustrouspixeldungeon.items.armor.curses.Stench;
import com.zrp200.lustrouspixeldungeon.items.armor.curses.Volatility;
import com.zrp200.lustrouspixeldungeon.items.armor.glyphs.Affection;
import com.zrp200.lustrouspixeldungeon.items.armor.glyphs.AntiMagic;
import com.zrp200.lustrouspixeldungeon.items.armor.glyphs.Brimstone;
import com.zrp200.lustrouspixeldungeon.items.armor.glyphs.Camouflage;
import com.zrp200.lustrouspixeldungeon.items.armor.glyphs.Entanglement;
import com.zrp200.lustrouspixeldungeon.items.armor.glyphs.Flow;
import com.zrp200.lustrouspixeldungeon.items.armor.glyphs.HolyProvidence;
import com.zrp200.lustrouspixeldungeon.items.armor.glyphs.Obfuscation;
import com.zrp200.lustrouspixeldungeon.items.armor.glyphs.Potential;
import com.zrp200.lustrouspixeldungeon.items.armor.glyphs.Repulsion;
import com.zrp200.lustrouspixeldungeon.items.armor.glyphs.Stone;
import com.zrp200.lustrouspixeldungeon.items.armor.glyphs.Swiftness;
import com.zrp200.lustrouspixeldungeon.items.armor.glyphs.Thorns;
import com.zrp200.lustrouspixeldungeon.items.armor.glyphs.Viscosity;
import com.zrp200.lustrouspixeldungeon.levels.Terrain;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.sprites.HeroSprite;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSprite;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSpriteSheet;
import com.zrp200.lustrouspixeldungeon.utils.GLog;

import java.util.ArrayList;
import java.util.Arrays;

public class Armor extends EquipableItem {

	private static final int HITS_TO_KNOW    = 10;

	protected static final String AC_DETACH       = "DETACH";

	public boolean glyphKnown = false;

	@Override
	public Item identify() {
		if(glyph != null && !glyphKnown) revealGlyph();
		glyphKnown = true;
		return super.identify();
	}

	public void revealGlyph() {
		glyphKnown = true;
		ItemChange.show(Dungeon.hero,this);
	}

	public enum Augment {
		EVASION (1.5f , -1f),
		DEFENSE (-1.5f, 1f),
		NONE	(0f   ,  0f);
		
		private float evasionFactor;
		private float defenceFactor;
		
		Augment(float eva, float df){
			evasionFactor = eva;
			defenceFactor = df;
		}
		
		public int evasionFactor(int level){
			return Math.round((2 + level) * evasionFactor);
		}
		
		public int defenseFactor(int level){
			return Math.round((2 + level) * defenceFactor);
		}
	}
	
	public Augment augment = Augment.NONE;
	public Glyph glyph;
	private BrokenSeal seal;
	
	public int tier;
	
	private int hitsToKnow = HITS_TO_KNOW;
	
	public Armor( int tier ) {
		this.tier = tier;
	}

	private static final String UNFAMILIRIARITY	= "unfamiliarity";
	private static final String GLYPH			= "glyph";
	private static final String SEAL            = "seal";
	private static final String AUGMENT			= "augment";
	private static final String GLYPH_KNOWN 	= "glyph known";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( UNFAMILIRIARITY, hitsToKnow );
		bundle.put( GLYPH, glyph );
		bundle.put( SEAL, seal);
		bundle.put( AUGMENT, augment);
		bundle.put( GLYPH_KNOWN,glyphKnown);
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
		hitsToKnow = bundle.getInt( UNFAMILIRIARITY );
		inscribe((Glyph) bundle.get(GLYPH));
		seal = (BrokenSeal)bundle.get(SEAL);
		glyphKnown=bundle.getBoolean(GLYPH_KNOWN);
		//pre-0.6.5 saves
		if (bundle.contains(AUGMENT)) augment = bundle.getEnum(AUGMENT, Augment.class);
	}

	@Override
	public void reset() {
		super.reset();
		//armor can be kept in bones between runs, the seal cannot.
		seal = null;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (seal != null)actions.add(AC_DETACH);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {

		super.execute(hero, action);

		if (action.equals(AC_DETACH) && seal != null){
			BrokenSeal.WarriorShield sealBuff = hero.buff(BrokenSeal.WarriorShield.class);
			if (sealBuff != null) sealBuff.setArmor(null);

			if (seal.level() > 0){
				degrade();
			}
			GLog.i( Messages.get(Armor.class, "detach_seal") );
			hero.sprite.operate(hero.pos);
			if (!seal.collect()){
				Dungeon.level.drop(seal, hero.pos);
			}
			seal = null;
		}
	}

	@Override
	public boolean doEquip( Hero hero ) {
		
		detach(hero.belongings.backpack);

		if (hero.belongings.armor == null || hero.belongings.armor.doUnequip( hero, true, false )) {
			
			hero.belongings.armor = this;

			if (cursed) {
				equipCursed( hero );
				GLog.n( Messages.get(Armor.class, "equip_cursed") );
			}
			if( !glyphKnown && glyph != null ) {// don't want it to always happen you know. Too much could get irritating
				revealGlyph(); // just to make obvious that it's enchanted
			}
			glyphKnown = cursedKnown = true;
			
			((HeroSprite)hero.sprite).updateArmor();
			activate(hero);

			hero.spendAndNext( time2equip( hero ) );
			return true;
			
		} else {
			
			collect( hero.belongings.backpack );
			return false;
			
		}
	}

	@Override
	public void activate(Char ch) {
		if (seal != null) Buff.affect(ch, BrokenSeal.WarriorShield.class).setArmor(this);
	}

	public void affixSeal(BrokenSeal seal){
		this.seal = seal;
		if (seal.level() > 0){
			//doesn't trigger upgrading logic such as affecting curses/glyphs
			level(level()+1);
			Badges.validateItemLevelAquired(this);
		}
		if (isEquipped(Dungeon.hero)){
			Buff.affect(Dungeon.hero, BrokenSeal.WarriorShield.class).setArmor(this);
		}
	}

	public BrokenSeal checkSeal(){
		return seal;
	}

	@Override
	protected float time2equip( Hero hero ) {
		return 2 / hero.speed();
	}

	@Override
	public boolean doUnequip( Hero hero, boolean collect, boolean single ) {
		if (super.doUnequip( hero, collect, single )) {

			hero.belongings.armor = null;
			((HeroSprite)hero.sprite).updateArmor();

			BrokenSeal.WarriorShield sealBuff = hero.buff(BrokenSeal.WarriorShield.class);
			if (sealBuff != null) sealBuff.setArmor(null);

			return true;

		} else {

			return false;

		}
	}
	
	@Override
	public boolean isEquipped( Hero hero ) {
		return hero.belongings.armor == this;
	}

	public final int DRMax(){
		return DRMax(level());
	}

	public int DRMax(int lvl){
		int max = tier * (2 + lvl) + augment.defenseFactor(lvl);
		if (lvl > max){
			return ((lvl - max)+1)/2;
		} else {
			return max;
		}
	}

	public final int DRMin(){
		return DRMin(level());
	}

	public int DRMin(int lvl){
		int max = DRMax(lvl);
		if (lvl >= max){
			return (lvl - max);
		} else {
			return lvl;
		}
	}
	
	public float evasionFactor( Char owner, float evasion ){
		
		if (hasGlyph(Stone.class, owner) && !((Stone)glyph).testingEvasion()){
			return 0.25f;
		}
		
		if (owner instanceof Hero){
			int aEnc = STRReq() - ((Hero) owner).STR();
			if (aEnc > 0) evasion /= Math.pow(1.5, aEnc);
			
			Momentum momentum = owner.buff(Momentum.class);
			if (momentum != null){
				evasion += momentum.evasionBonus(Math.max(0, -aEnc));
			}
		}
		
		return evasion + augment.evasionFactor(level());
	}
	
	public float speedFactor( Char owner, float speed ){
		
		if (owner instanceof Hero) {
			int aEnc = STRReq() - ((Hero) owner).STR();
			if (aEnc > 0) speed /= Math.pow(1.2, aEnc);
		}
		
		if (hasGlyph(Swiftness.class, owner)) {
			boolean enemyNear = false;
			for (Char ch : Actor.chars()){
				if (Dungeon.level.adjacent(ch.pos, owner.pos) && owner.alignment != ch.alignment){
					enemyNear = true;
					break;
				}
			}
			if (!enemyNear) speed *= (1.2f + 0.04f * level());
		} else if (hasGlyph(Flow.class, owner) && Dungeon.level.water[owner.pos]){
			speed *= (1.5f + 0.1f * level());
		}
		
		if (hasGlyph(Bulk.class, owner) &&
				(Dungeon.level.map[owner.pos] == Terrain.DOOR
						|| Dungeon.level.map[owner.pos] == Terrain.OPEN_DOOR )) {
			speed /= 3f;
		}
		
		return speed;
		
	}
	
	public float stealthFactor( Char owner, float stealth ){
		
		if (hasGlyph(Obfuscation.class, owner)){
			stealth += 1 + level()/3f;
		}
		
		return stealth;
	}

	@Override
	public Item upgrade() {
		return upgrade( false );
	}
	
	public Item upgrade( boolean inscribe ) {
		if (inscribe && glyph == null){
			inscribe( Glyph.random() );
		} else if (!inscribe && Random.Float() > Math.pow(0.9, level())){
			inscribe(null,false);
		}

		cursed = false;

		if (seal != null && seal.level() == 0)
			seal.upgrade();

		return super.upgrade();
	}
	
	public int proc( Char attacker, Char defender, int damage ) {
		
		if (glyph != null && defender.buff(MagicImmune.class) == null) {
			damage = glyph.proc( this, attacker, defender, damage );
		}
		
		if (!levelKnown && defender instanceof Hero) {
			if (--hitsToKnow <= 0) {
				identify();
				GLog.w( Messages.get(Armor.class, "identify") );
				Badges.validateItemLevelAquired( this );
			}
		}
		
		return damage;
	}


	@Override
	public String name() {
		return glyph != null && glyphKnown ? glyph.name( super.name() ) : super.name();
	}
	
	@Override
	public String info() {
		String info = desc();
		
		if (levelKnown) {
			info += "\n\n" + Messages.get(Armor.class, "curr_absorb", DRMin(), DRMax(), STRReq());
			
			if (STRReq() > Dungeon.hero.STR()) {
				info += " " + Messages.get(Armor.class, "too_heavy");
			}
		} else {
			info += "\n\n" + Messages.get(Armor.class, "avg_absorb", DRMin(0), DRMax(0), STRReq(0));

			if (STRReq(0) > Dungeon.hero.STR()) {
				info += " " + Messages.get(Armor.class, "probably_too_heavy");
			}
		}

		switch (augment) {
			case EVASION:
				info += "\n\n" + Messages.get(Armor.class, "evasion");
				break;
			case DEFENSE:
				info += "\n\n" + Messages.get(Armor.class, "defense");
				break;
			case NONE:
		}
		
		if ( visiblyInscribed() ) {
			info += "\n\n" +  Messages.get(Armor.class, "inscribed", glyph.name());
			info += " " + glyph.desc();
		}
		
		if (cursed && isEquipped( Dungeon.hero )) {
			info += "\n\n" + Messages.get(Armor.class, "cursed_worn");
		} else if ( visiblyCursed() ) {
			info += "\n\n" + Messages.get(Armor.class, "cursed");
		} else if (seal != null) {
			info += "\n\n" + Messages.get(Armor.class, "seal_attached");
		} else if (!isIdentified() && cursedKnown){
			info += "\n\n" + Messages.get(Armor.class, "not_cursed");
		}
		
		return info;
	}

	@Override
	public Emitter emitter() {
		if (seal == null) return super.emitter();
		Emitter emitter = new Emitter();
		emitter.pos(ItemSpriteSheet.film.width(image)/2f + 2f, ItemSpriteSheet.film.height(image)/3f);
		emitter.fillTarget = false;
		emitter.pour(Speck.factory( Speck.RED_LIGHT ), 0.6f);
		return emitter;
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
		//20% chance to be inscribed (+33%)
		float effectRoll = Random.Float();
		if (effectRoll < 0.3f) {
			inscribe(Glyph.randomCurse(), false);
			cursed = true;
		} else if (effectRoll >= 0.8f){
			inscribe(false);
		}

		return this;
	}

	public int STRReq(){
		return STRReq(level());
	}

	public int STRReq(int lvl){
		lvl = Math.max(0, lvl);

		//strength req decreases at +1,+3,+6,+10,etc.
		return (8 + Math.round(tier * 2)) - (int)(Math.sqrt(8 * lvl + 1) - 1)/2;
	}
	
	@Override
	public int price() {
		if (seal != null) return 0;

		int price = 20 * tier;
		if ( visiblyInscribed() ) {
			price *= Math.pow(1.5, hasGoodGlyph() ? 1 : -1);
		}
		if (cursedKnown) {
			if( cursed )
				price *= 0.75;
			else if( !levelKnown )
				price *= 1.25; // because you can say for certain it isn't cursed.
		}
		if (levelKnown && level() > 0) {
			price *= (level() + 1);
		}
		if (price < 1) {
			price = 1;
		}
		return price;
	}

	public Armor inscribe( Glyph glyph, boolean visible) {
		this.glyph = glyph;
		if(visible) revealGlyph();
		return this;
	}

	public Armor inscribe( Glyph glyph ) {
		return inscribe(glyph,true);
	}

	public Armor inscribe(boolean visible) {

		Class<? extends Glyph> oldGlyphClass = glyph != null ? glyph.getClass() : null;
		Glyph gl = Glyph.random( oldGlyphClass );

		return inscribe( gl, visible );
	}
	public Armor inscribe() {
		return inscribe(true);
	}

	public boolean hasGlyph(Class<?extends Glyph> type, Char owner) {
		return glyph != null && glyph.getClass() == type && owner.buff(MagicImmune.class) == null;
	}

	//these are not used to process specific glyph effects, so magic immune doesn't affect them
	public boolean hasGoodGlyph(){
		return glyph != null && !glyph.curse();
	}
	public boolean hasCurseGlyph(){
		return glyph != null && glyph.curse();
	}
	public boolean visiblyInscribed() { return glyph != null && glyphKnown; }

	@Override
	public ItemSprite.Glowing glowing() {
		return glyph != null && glyphKnown ? glyph.glowing() : null;
	}
	
	public static abstract class Glyph implements Bundlable {
		
		private static final Class<?>[] common = new Class<?>[]{
				Obfuscation.class, Swiftness.class, Viscosity.class, Potential.class
		};
		
		private static final Class<?>[] uncommon = new Class<?>[]{
				Brimstone.class, Stone.class, Entanglement.class,
				Repulsion.class, Camouflage.class, Flow.class
		};
		
		private static final Class<?>[] rare = new Class<?>[]{
				Affection.class, AntiMagic.class, Thorns.class, HolyProvidence.class
		};
		
		private static final float[] typeChances = new float[]{
				50, //12.5% each
				40, //6.67% each
				10  //2.5% each
		};

		private static final Class<?>[] curses = new Class<?>[]{
				AntiEntropy.class, Corrosion.class, Displacement.class, Metabolism.class,
				Multiplicity.class, Stench.class, Overgrowth.class, Bulk.class, Volatility.class
		};
		
		public abstract int proc( Armor armor, Char attacker, Char defender, int damage );
		
		public String name() {
			if (!curse())
				return name( Messages.get(this, "glyph") );
			else
				return name( Messages.get(Item.class, "curse"));
		}
		
		public String name( String armorName ) {
			return Messages.get(this, "name", armorName);
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
		public static Glyph random( Class<? extends Glyph> ... toIgnore ) {
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
		public static Glyph randomCommon( Class<? extends Glyph> ... toIgnore ){
			try {
				ArrayList<Class<?>> glyphs = new ArrayList<>(Arrays.asList(common));
				glyphs.removeAll(Arrays.asList(toIgnore));
				if (glyphs.isEmpty()) {
					return random();
				} else {
					return (Glyph) Random.element(glyphs).newInstance();
				}
			} catch (Exception e) {
				LustrousPixelDungeon.reportException(e);
				return null;
			}
		}
		
		@SuppressWarnings("unchecked")
		public static Glyph randomUncommon( Class<? extends Glyph> ... toIgnore ){
			try {
				ArrayList<Class<?>> glyphs = new ArrayList<>(Arrays.asList(uncommon));
				glyphs.removeAll(Arrays.asList(toIgnore));
				if (glyphs.isEmpty()) {
					return random();
				} else {
					return (Glyph) Random.element(glyphs).newInstance();
				}
			} catch (Exception e) {
				LustrousPixelDungeon.reportException(e);
				return null;
			}
		}
		
		@SuppressWarnings("unchecked")
		public static Glyph randomRare( Class<? extends Glyph> ... toIgnore ){
			try {
				ArrayList<Class<?>> glyphs = new ArrayList<>(Arrays.asList(rare));
				glyphs.removeAll(Arrays.asList(toIgnore));
				if (glyphs.isEmpty()) {
					return random();
				} else {
					return (Glyph) Random.element(glyphs).newInstance();
				}
			} catch (Exception e) {
				LustrousPixelDungeon.reportException(e);
				return null;
			}
		}
		
		@SuppressWarnings("unchecked")
		public static Glyph randomCurse( Class<? extends Glyph> ... toIgnore ){
			try {
				ArrayList<Class<?>> glyphs = new ArrayList<>(Arrays.asList(curses));
				glyphs.removeAll(Arrays.asList(toIgnore));
				if (glyphs.isEmpty()) {
					return random();
				} else {
					return (Glyph) Random.element(glyphs).newInstance();
				}
			} catch (Exception e) {
				LustrousPixelDungeon.reportException(e);
				return null;
			}
		}
		
	}
}
