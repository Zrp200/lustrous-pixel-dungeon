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

package com.zrp200.lustrouspixeldungeon.items.rings;

import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Badges;
import com.zrp200.lustrouspixeldungeon.Challenges;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.items.Generator;
import com.zrp200.lustrouspixeldungeon.items.Item;
import com.zrp200.lustrouspixeldungeon.items.ItemStatusHandler;
import com.zrp200.lustrouspixeldungeon.items.KindofMisc;
import com.zrp200.lustrouspixeldungeon.journal.Catalog;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSpriteSheet;
import com.zrp200.lustrouspixeldungeon.utils.GLog;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public abstract class Ring extends KindofMisc {

	private static final int TICKS_TO_KNOW    = 200;

	private static final float UPGRADE_CURSE_REMOVAL = .8f;
	
	protected Buff buff=buff();
	
	private static final Class<?>[] rings = {
		RingOfAccuracy.class,	RingOfEvasion.class,	RingOfElements.class,
		RingOfForce.class,   	RingOfFuror.class,  	RingOfHaste.class,
		RingOfEnergy.class,  	RingOfMight.class,  	RingOfSharpshooting.class,
		RingOfTenacity.class,	RingOfWealth.class
	};

	private static final HashMap<String, Integer> gems = new HashMap<String, Integer>() {
		{
			put("garnet",ItemSpriteSheet.RING_GARNET);
			put("ruby",ItemSpriteSheet.RING_RUBY);
			put("topaz",ItemSpriteSheet.RING_TOPAZ);
			put("emerald",ItemSpriteSheet.RING_EMERALD);
			put("onyx",ItemSpriteSheet.RING_ONYX);
			put("opal",ItemSpriteSheet.RING_OPAL);
			put("tourmaline",ItemSpriteSheet.RING_TOURMALINE);
			put("sapphire",ItemSpriteSheet.RING_SAPPHIRE);
			put("amethyst",ItemSpriteSheet.RING_AMETHYST);
			put("quartz",ItemSpriteSheet.RING_QUARTZ);
			put("agate",ItemSpriteSheet.RING_AGATE);
			put("diamond",ItemSpriteSheet.RING_DIAMOND);
		}
	};
	
	private static ItemStatusHandler<Ring> handler;
	
	private String gem;
	
	private int ticksToKnow = TICKS_TO_KNOW;
	
	@SuppressWarnings("unchecked")
	public static void initGems() {
		handler = new ItemStatusHandler<>( (Class<? extends Ring>[])rings, gems );
	}

	@Override
	public Ring transmute(boolean dry) {
		Ring n = (Ring) Generator.random( Generator.Category.RING );
		return (Challenges.isItemBlocked(n) || n.getClass() == getClass()) ? transmute(dry) : (Ring)n.emulate(this);
	}

	public static void save(Bundle bundle ) {
		handler.save( bundle );
	}

	public static void saveSelectively( Bundle bundle, ArrayList<Item> items ) {
		handler.saveSelectively( bundle, items );
	}
	
	@SuppressWarnings("unchecked")
	public static void restore( Bundle bundle ) {
		handler = new ItemStatusHandler<>( (Class<? extends Ring>[])rings, gems, bundle );
	}
	
	public Ring() {
		super();
		reset();
	}

	//anonymous rings are always IDed, do not affect ID status,
	//and their sprite is replaced by a placeholder if they are not known,
	//useful for items that appear in UIs, or which are only spawned for their effects
	private boolean anonymous = false;
	public void anonymize(){
		if (!isKnown()) image = ItemSpriteSheet.RING_HOLDER;
		anonymous = true;
	}
	
	public void reset() {
		super.reset();
		if (handler != null && handler.contains(this)){
			image = handler.image(this);
			gem = handler.label(this);
		}
	}
	
	public void activate( Char ch ) {
		buff = buff();
		buff.attachTo( ch );
	}

	@Override
	public boolean doUnequip( Hero hero, boolean collect, boolean single ) {
		if (super.doUnequip( hero, collect, single )) {

			hero.remove( buff );
			buff = null;

			return true;

		} else {

			return false;

		}
	}
	
	public boolean isKnown() {
		return anonymous || (handler != null && handler.isKnown( this ));
	}
	
	public void setKnown() {
		if (!anonymous) {
			if (!isKnown()) {
				handler.know(this);
			}

			if (Dungeon.hero.isAlive()) {
				Catalog.setSeen(getClass());
			}
		}
	}
	
	@Override
	public String name() {
		return isKnown() ? super.name() : Messages.get(Ring.class, gem);
	}
	
	@Override
	public String info(){
		
		String desc = isKnown() ? super.desc() : Messages.get(this, "unknown_desc");
		
		if (cursed && isEquipped( Dungeon.hero )) {
			desc += "\n\n" + Messages.get(Ring.class, "cursed_worn");
			
		} else if (cursed && cursedKnown) {
			desc += "\n\n" + Messages.get(Ring.class, "curse_known");
			
		} else if (!isIdentified() && cursedKnown){
			desc += "\n\n" + Messages.get(Ring.class, "not_cursed");
			
		}
		
		if (isKnown()) {
			desc += "\n\n" + statsInfo();
		}
		
		return desc;
	}

	String statsInfo() { // the "default" method

		StringBuilder message = new StringBuilder();
		message.append(Messages.get(this,"stats")).append(" ");
		if( !isIdentified() ) message.append("typically ");

		String effect1 = Messages.get(this,"effect1"); // this goes first if available; this is the "flat" boost

		if( !(effect1.equals( "" ) ) )
			message.append( effect1 ).append( " by _" )
					.append( effect1Bonus() )
					.append("_ and ");

		message.append( Messages.get(this, "effect2") )
				.append(" by _").append( effect2Bonus() ).append( "._" );
		return message.toString();
	}

	protected String effect1Bonus() { // typically the flast boost, but some rings are different.
		return String.valueOf( (int)Math.round( visualSoloBonus() ) );
	}

	abstract protected String effect2Bonus();
	final String visualMultiplier(float bonusScaling) {
		float multiplier = (float)Math.pow(bonusScaling, visualSoloBonus() );
		return new DecimalFormat( "#.#" + (multiplier < 1 ? "#" : "") )
				.format(
				100 * (bonusScaling > 1 ? multiplier - 1f : 1f - multiplier)
		) +
				"%";
	}

	protected float visualSoloBonus() {
		return visiblyCursed() ? Math.min(0,visiblyUpgraded() - 2) : soloBonus( visiblyUpgraded() );
	}

	@Override
	public Item upgrade() {
		super.upgrade();
		
		if (Random.Float() > Math.pow(UPGRADE_CURSE_REMOVAL, level())) {
			cursed = false;
		}
		
		return this;
	}
	
	@Override
	public boolean isIdentified() {
		return super.isIdentified() && isKnown();
	}
	
	@Override
	public Item identify() {
		setKnown();
		return super.identify();
	}
	
	@Override
	public Item random() {
		//+0: 66.67% (2/3)
		//+1: 26.67% (4/15)
		//+2: 6.67%  (1/15)
		int n = 0;
		if (Random.Int(3) == 0) {
			n++;
			if (Random.Int(5) == 0){
				n++;
			}
		}
		level(n);
		
		//30% chance to be cursed
		if (Random.Float() < 0.3f) {
			cursed = true;
		}
		
		return this;
	}
	
	public static HashSet<Class<? extends Ring>> getKnown() {
		return handler.known();
	}
	
	public static HashSet<Class<? extends Ring>> getUnknown() {
		return handler.unknown();
	}
	
	public static boolean allKnown() {
		return handler.known().size() == rings.length - 2;
	}
	
	@Override
	public int price() {
		int price = 75;
		if ( visiblyCursed() ) {
			price /= 2;
		}
		if (levelKnown) {
			if (level() > 0) {
				price *= (level() + 1);
			} else if (level() < 0) {
				price /= (1 - level());
			}
		}
		if (price < 1) {
			price = 1;
		}
		return price;
	}
	
	protected abstract RingBuff buff();

	private static final String UNFAMILIRIARITY    = "unfamiliarity";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( UNFAMILIRIARITY, ticksToKnow );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		if ((ticksToKnow = bundle.getInt( UNFAMILIRIARITY )) == 0) {
			ticksToKnow = TICKS_TO_KNOW;
		}
		
		//pre-0.6.1 saves
		if (level() < 0){
			upgrade(-level());
		}
	}

	public static int getBonus(Char target, Class<?extends RingBuff> type){
		int bonus = 0;
		if(target != null) for ( RingBuff buff : target.buffs(type) )
			bonus += buff.level();
		return bonus;
	}

	int soloBonus(){ return soloBonus(Ring.this.level()); }
	private int soloBonus(float level) { return (int)(cursed ? Math.min( 0, level-2 ) : level+1); } // adjusts for curses and such

	public class RingBuff extends Buff {
		@Override
		public boolean act() {
			if (!isIdentified() && --ticksToKnow <= 0) {
				identify();
				GLog.w( Messages.get(Ring.class, "identify", Ring.this.toString()) );
				Badges.validateItemLevelAquired( Ring.this );
			}
			spend( TICK );
			return true;
		}
		public int level(){ return Ring.this.soloBonus(); }
	}
}
