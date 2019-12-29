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

package com.zrp200.lustrouspixeldungeon.items.weapon.missiles;

import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Challenges;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Actor;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.PinCushion;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.actors.hero.HeroClass;
import com.zrp200.lustrouspixeldungeon.items.Generator;
import com.zrp200.lustrouspixeldungeon.items.Heap;
import com.zrp200.lustrouspixeldungeon.items.Item;
import com.zrp200.lustrouspixeldungeon.items.bags.Bag;
import com.zrp200.lustrouspixeldungeon.items.bags.MagicalHolster;
import com.zrp200.lustrouspixeldungeon.items.rings.RingOfSharpshooting;
import com.zrp200.lustrouspixeldungeon.items.weapon.Weapon;
import com.zrp200.lustrouspixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.utils.GLog;

import java.util.ArrayList;

abstract public class MissileWeapon extends Weapon {

	{
		stackable = true;
		levelKnown = true;
		
		bones = true;

		value = 6;

		defaultAction = AC_THROW;
		usesTargeting = true;
	}

	boolean sticky = true;

	protected static final float MAX_DURABILITY = 100;
	protected float durability = MAX_DURABILITY;
	protected float durabilityCap = MAX_DURABILITY;
	protected float baseUses = 10;

	public boolean holster() {
		return Dungeon.hero != null && Dungeon.hero.belongings.getItem(MagicalHolster.class) != null;
	}
	
	//used to reduce durability from the source weapon stack, rather than the one being thrown.
	protected MissileWeapon parent;

	@Override
	public int min() {
		return Math.max(
				0,
				min( level() + RingOfSharpshooting.levelDamageBonus(Dungeon.hero) )
		);
	}

	@Override
	public int minBase() {
		return 2*tier;
	}
	public int minScale() {
		return tier == 1 ? 1 : 2;
	}

	@Override
	public int max() {
		return Math.max(0, max( level() + RingOfSharpshooting.levelDamageBonus(Dungeon.hero) ));
	}

	@Override
	public int maxBase() {
		return 5*tier;
	}

	@Override
	public int maxScale() {
		return tier == 1 ? 2 : tier;
	}

	public int STRReq(int lvl){
		lvl = Math.max(0, lvl);
		//strength req decreases at +1,+3,+6,+10,etc.
		return (7 + tier * 2) - (int)(Math.sqrt(8 * lvl + 1) - 1)/2;
	}


	protected float
			rangeBoost = 1.5f,
			adjacentPenalty = 0.5f;

	@Override
	public float accuracyFactor(Char owner) {
		Hero user = (Hero)owner;
		return super.accuracyFactor(owner) * (Dungeon.level.adjacent(owner.pos,user.enemy().pos) ? adjacentPenalty : rangeBoost);
	}

	@SuppressWarnings("ConstantConditions")
	private abstract class EnhanceCallback implements Callback {
		MissileWeapon toEnhance;
		{
			toEnhance = (MissileWeapon) detach(curUser.belongings.backpack);
			toEnhance.parent = null;
			toEnhance.durability = MAX_DURABILITY;
		}
	}

	private MissileWeapon enhance(EnhanceCallback operation) {
		bundleRestoring = true;
		operation.call();
		bundleRestoring = false;
		MissileWeapon enhanced = operation.toEnhance;
		//try to put the upgraded into inventory, if it didn't already merge
		if(enhanced == this) {
			MissileWeapon similar = (MissileWeapon) curUser.belongings.getSimilar(this);
			if (similar != null) {
				similar.merge(this);
				return similar;
			}
		}
		if( !enhanced.collect() ) enhanced.drop(Dungeon.hero.pos);
		updateQuickslot();
		return enhanced;
	}

	@Override
	public MissileWeapon upgrade(final boolean enchant) {
		return bundleRestoring ? (MissileWeapon)super.upgrade(enchant) : enhance(new EnhanceCallback() {
			@Override
			public void call() {
				toEnhance.upgrade(enchant);
			}
		});
	}
	@Override
	public Weapon enchant(final Enchantment ench, final boolean visible) {
		return bundleRestoring ? super.enchant(ench, visible) : enhance(new EnhanceCallback() {
			@Override
			public void call() {
				if(toEnhance.quantity == 1 && toEnhance != MissileWeapon.this) {// this lets us enchant two.
					if(quantity == 1) {
						toEnhance = (MissileWeapon) merge(toEnhance);
						detachAll(curUser.belongings.backpack);
					} else toEnhance.merge(detach(curUser.belongings.backpack));
					toEnhance.durability = MAX_DURABILITY;
				}
				toEnhance.enchant(ench,visible);
			}
		});
	}

	@Override
	public MissileWeapon transmute(boolean dry) {
		MissileWeapon result = (MissileWeapon)Generator.random( Generator.misTiers[tier-1] )
				.emulate(this)
				.quantity(1);
		result.durability = MAX_DURABILITY;
		return Challenges.isItemBlocked(result) || result.getClass() == getClass()
				? transmute(dry)
				: result;
	}

	@Override
	public void augment(final Augment augment) { // if I ever decide to let missile weapons be augmented.
		if(bundleRestoring) super.augment(augment);
		else enhance(new EnhanceCallback() {
			@Override
			public void call() {
				toEnhance.augment(augment);
			}
		});
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.remove( AC_EQUIP );
		return actions;
	}
	
	@Override
	public boolean collect(Bag container) {
		detach();
		return super.collect(container);
	}

	private PinCushion embed = null;
	public boolean stickTo(Char enemy) {
		if(enemy != null) {
			PinCushion p = Buff.affect(enemy, PinCushion.class);
			return stickTo(p);
		}
		return false;
	}
	public boolean stickTo(PinCushion p) {
		if (p != null && sticky && durability > 0) {
			embed = p; // important that this goes first.
			p.stick(this);
			return true;
		}
		return false;
	}
	public boolean isBreaking() {
		return durability <= 0;
	}

	public boolean detach() {
		Char holder = detachEmbed();
		if(Dungeon.hero != null && Dungeon.hero.belongings.contains(this)) {
			detachAll(Dungeon.hero.belongings.backpack);
			holder = Dungeon.hero;
		} else if(holder == null) return false;
		drop(holder.pos);
		return true;
	}

	protected Char detachEmbed() {
	    if(embed == null) return null;
	    Char holder = embed.target;
	    embed = null;
	    return holder;
    }

	public boolean attachedTo(PinCushion p) { return embed != null && embed.equals(p); }
	public boolean isStuck() { return embed != null; }
	
	@Override
	public Item random() {
		if (!stackable) return this;
		
		//2: 66.67% (2/3)
		//3: 26.67% (4/15)
		//4: 6.67%  (1/15)
		quantity = 2;
		if (Random.Int(3) == 0) {
			quantity++;
			if (Random.Int(5) == 0) {
				quantity++;
			}
		}
		return this;
	}
	
	@Override
	public float castDelay(Char user, int dst) {
		return speedFactor( user );
	}

	protected void useDurability() {
		// this deducts durability from the correct source
		MissileWeapon weapon = parent == null ? this : parent;
		float dpu = weapon.durabilityPerUse();
		weapon.durability -= dpu;
		if (parent != null && parent.isBreaking() ){
			durability = 0;
			parent.durability = MAX_DURABILITY;
		}

		// this alerts the player if a weapon is about to break
		boolean willBreak = weapon.durability > 0 && weapon.durability <= weapon.durabilityPerUse();
		if(willBreak && dpu < 100) {
			if (level() <= 0) GLog.w(Messages.get(this, "about_to_break"));
			else GLog.n(Messages.get(this, "about_to_break"));
		}
	}

	@SuppressWarnings("WeakerAccess")
	protected float durabilityScaling = 3f;
	protected float enchantDurability = 1.5f;
	
	protected float durabilityPerUse(){
		float usages = baseUses * (float)(Math.pow(durabilityScaling, level()));
		usages *= (enchantment != null ? enchantDurability : 1);
		
		if (Dungeon.hero.heroClass == HeroClass.HUNTRESS)   usages *= 1.5f;
		if ( holster() )                                    usages *= MagicalHolster.HOLSTER_DURABILITY_FACTOR;
		
		usages *= RingOfSharpshooting.durabilityMultiplier( Dungeon.hero );
		
		//at 100 uses, items just last forever.
		if (usages >= durabilityCap) return 0;

		//add a tiny amount to account for rounding error for calculations like 1/3
		return (MAX_DURABILITY/usages) + 0.001f;
	}
	
	@Override
	public void reset() {
		super.reset();
		durability = MAX_DURABILITY;
	}
	
	@Override
	public Item merge(Item other) {
        super.merge(other);
        if (isSimilar(other)) {
            durability += ((MissileWeapon)other).durability;
            durability -= MAX_DURABILITY;
        }
        while(durability <= 0) {
        	quantity--;
        	durability += MAX_DURABILITY;
		}
        Dungeon.hero.belongings.trim();
        updateQuickslot();
        return this;
	}
	
	@Override
	public MissileWeapon split(int amount) {
		boolean bundleState = bundleRestoring;
		bundleRestoring = true;
		MissileWeapon split = (MissileWeapon)super.split(amount);
		bundleRestoring = bundleState;
		//unless the thrown weapon will break, split off a max durability item and
		//have it reduce the durability of the main stack. Cleaner to the player this way
		if (split != null){
			split.durability = MAX_DURABILITY;
			split.parent = this;
		}
		
		return split;
	}
	
	@Override
	public boolean doPickUp(Hero hero) {
		parent = null;
		return super.doPickUp(hero);
	}
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	@Override
	public String info() {
		String info = super.info();
		info += "\n\n" + Messages.get(MissileWeapon.class, "distance");
		
		info += "\n\n" + Messages.get(this, "durability");

		MissileWeapon wep = parent != null ? parent : this;
		if (wep.durabilityPerUse() > 0){
			info += " " + Messages.get(this, "uses_left",
					(int)Math.ceil(wep.durability/wep.durabilityPerUse()),
					(int)Math.ceil(MAX_DURABILITY/wep.durabilityPerUse()));
		} else {
			info += " " + Messages.get(this, "unlimited_uses");
		}
		return info;
	}
	
	private static final String DURABILITY = "durability";
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(DURABILITY, durability);
	}
	
	private static boolean bundleRestoring = false;
	@Override
	public void restoreFromBundle(Bundle bundle) {
		bundleRestoring = true;
		super.restoreFromBundle(bundle);
		bundleRestoring = false;
		//compatibility with pre-0.6.3 saves
		if (bundle.contains(DURABILITY)) {
			durability = bundle.getInt(DURABILITY);
		} else {
			durability = 100;
			//reduces quantity roughly in line with new durability system
			if (!(this instanceof TippedDart)){
				quantity = (int)Math.ceil(quantity/5f);
			}
		}
	}
	protected boolean rangedHit = false;
    @Override
    public int proc(Char attacker, Char defender, int damage) {
        rangedHit = true;
        useDurability();
        if(hasHiddenEnchant()) revealEnchant();
        stickTo(defender);
        return super.proc(attacker, defender, damage);
    }

	@Override
	protected void onThrow(int cell) {
		Char enemy = Actor.findChar(cell);
		if(enemy != null && enemy != curUser) curUser.shoot(enemy, this);
		super.onThrow(cell);
	}
    public void onMiss(Char enemy) { }

	@Override
	protected void afterThrow(int cell) { // this should be overridden to do stuff after striking the target.
		super.afterThrow(cell);
		rangedHit = false;
	}

	@Override
	public Heap drop(int pos) {
		if((embed != null && embed.target.isAlive()) || durability <= 0) {
			return Dungeon.level.drop(null,pos);
		}
		embed = null;
		parent = null;
		return super.drop(pos);
	}
}
