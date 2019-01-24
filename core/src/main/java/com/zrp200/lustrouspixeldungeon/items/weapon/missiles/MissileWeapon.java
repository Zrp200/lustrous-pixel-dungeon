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
import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Actor;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.PinCushion;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.actors.hero.HeroClass;
import com.zrp200.lustrouspixeldungeon.items.Heap;
import com.zrp200.lustrouspixeldungeon.items.Item;
import com.zrp200.lustrouspixeldungeon.items.bags.Bag;
import com.zrp200.lustrouspixeldungeon.items.bags.MagicalHolster;
import com.zrp200.lustrouspixeldungeon.items.rings.RingOfSharpshooting;
import com.zrp200.lustrouspixeldungeon.items.weapon.Weapon;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Projecting;
import com.zrp200.lustrouspixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.utils.GLog;

import java.util.ArrayList;

abstract public class MissileWeapon extends Weapon {

	{
		stackable = true;
		levelKnown = true;

		basePrice = 6;

		bones = true;

		defaultAction = AC_THROW;
		usesTargeting = true;
	}
	
	protected boolean sticky = true;

	private static final float MAX_DURABILITY = 100;
	protected float durability = MAX_DURABILITY;
	protected float durabilityCap = MAX_DURABILITY;
	protected float baseUses = 10;

	public boolean holster;
	
	//used to reduce durability from the source weapon stack, rather than the one being thrown.
	protected MissileWeapon parent;

	@Override
	public int min() {
		return Math.max(0, min( level() + RingOfSharpshooting.levelDamageBonus(Dungeon.hero) ));
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

	@Override
	//FIXME some logic here assumes the items are in the player's inventory. Might need to adjust
	public Item upgrade() {
		if (!bundleRestoring) {
			if (quantity > 1) {
				MissileWeapon upgraded = (MissileWeapon) split(1);
				upgraded.parent = null;

				upgraded = (MissileWeapon) upgraded.upgrade();

				//try to put the upgraded into inventory, if it didn't already merge
				if (upgraded.quantity() == 1 && !upgraded.collect()) {
					upgraded.drop(Dungeon.hero.pos);
				}
				updateQuickslot();
				return upgraded;
			} else {
				durability = MAX_DURABILITY;
				super.upgrade();

				Item similar = Dungeon.hero.belongings.getSimilar(this);
				if (similar != null){
					detach(Dungeon.hero.belongings.backpack);
					return similar.merge(this);
				}
				updateQuickslot();
				return this;
			}

		} else {
			return super.upgrade();
		}
	}

	@Override
	public Weapon enchant(Enchantment ench, boolean visible) {
		if (!bundleRestoring) {
			if (quantity > 1) {
				MissileWeapon enchanted = (MissileWeapon) split(1);
				enchanted.parent = null;

				enchanted = (MissileWeapon) enchanted.enchant(ench,visible);

				//try to put the upgraded into inventory, if it didn't already merge
				if (enchanted.quantity() == 1 && !enchanted.collect()) {
					enchanted.drop(Dungeon.hero.pos);
				}
				updateQuickslot();
				return enchanted;
			} else {
				durability = MAX_DURABILITY;
				super.enchant(ench, visible);
				Item similar = (Dungeon.hero != null) ? Dungeon.hero.belongings.getSimilar(this) : null;
				if (similar != null){
					detach(Dungeon.hero.belongings.backpack);
					return (Weapon) similar.merge(this);
				}
				updateQuickslot();
				return this;
			}
		} else {
			return super.enchant(ench,enchantKnown);
		}
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.remove( AC_EQUIP );
		return actions;
	}
	
	@Override
	public boolean collect(Bag container) {
		if (container instanceof MagicalHolster) holster = true;
		detach();
		return super.collect(container);
	}
	
	@Override
	public int throwPos(Hero user, int dst) {
		if (hasEnchant(Projecting.class, user)
				&& !Dungeon.level.solid[dst] && Dungeon.level.distance(user.pos, dst) <= 4){
			return dst;
		} else {
			return super.throwPos(user, dst);
		}
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		rangedHit = true;
		useDurability();
		stickTo(defender);
		return super.proc(attacker, defender, damage);
	}

	private PinCushion embed = null;
	public boolean stickTo(Char enemy) {
		if(enemy != null && durability > 0 && sticky && enemy.isAlive()) {
			PinCushion p = Buff.affect(enemy, PinCushion.class);
			if (p.target == enemy){
				embed = p; // important that this goes first.
				p.stick(this);
				return true;
			}
		}
		return false;
	}

	public void detach() {
		if(curUser != null && curUser.belongings.contains(this)) {
			detachAll(curUser.belongings.backpack);
			drop(curUser.pos);
		}
		else if(embed != null) {
			int dropPos = embed.target.pos;
			embed = null;
			drop(dropPos);
		}
	}

	public boolean attachedTo(PinCushion p) { return embed.equals(p); }
	
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

	public void useDurability() {
		if (parent != null){
			if ( parent.durability <= parent.durabilityPerUse()){
				durability = 0;
				parent.durability = MAX_DURABILITY;
			} else {
				durability = parent.durability -= parent.durabilityPerUse();
			}
		} else {
			durability -= durabilityPerUse();
		}
		MissileWeapon weapon = parent == null ? this : parent;
		boolean willBreak = weapon.durability > 0 && weapon.durability <= weapon.durabilityPerUse();
		if(willBreak) {
			if (level() <= 0) GLog.w(Messages.get(this, "about_to_break"));
			else GLog.n(Messages.get(this, "about_to_break"));
		}
	}

	@SuppressWarnings("WeakerAccess")
	protected float durabilityScaling = 3f;
	protected float enchantDurability = durabilityScaling/2;
	
	protected float durabilityPerUse(){
		float usages = baseUses * (float)(Math.pow(durabilityScaling, level()));
		usages *= (enchantment != null ? enchantDurability : 1);
		
		if (Dungeon.hero.heroClass == HeroClass.HUNTRESS)   usages *= 1.5f;
		if (holster)                                        usages *= MagicalHolster.HOLSTER_DURABILITY_FACTOR;
		
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
		durability = Math.min(((MissileWeapon)other).durability,durability);
		while (durability <= 0){
			quantity--;
			durability += MAX_DURABILITY;
		}
		return this;
	}
	
	@Override
	public Item split(int amount) {
		bundleRestoring = true;
		Item split = super.split(amount);
		bundleRestoring = false;

		//unless the thrown weapon will break, split off a max durability item and
		//have it reduce the durability of the main stack. Cleaner to the player this way
		if (split != null){
			MissileWeapon m = (MissileWeapon)split;
			m.durability = MAX_DURABILITY;
			m.parent = this;
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
		
		if (durabilityPerUse() > 0){
			info += " " + Messages.get(this, "uses_left",
					(int)Math.ceil(durability/durabilityPerUse()),
					(int)Math.ceil(MAX_DURABILITY/durabilityPerUse()));
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
	protected void onThrow(int cell) {
		Char enemy = Actor.findChar(cell);
		if(Actor.findChar(cell) != null && curUser.pos != cell) curUser.shoot(enemy, this);
		super.onThrow(cell);
	}

	@Override
	public Heap drop(int pos) {
		if((embed != null && embed.target.isAlive()) || durability <= 0) return Dungeon.level.drop(null,pos);
		return super.drop(pos);
	}

	@Override
	protected void onThrowComplete(int cell) {
		parent = null;
		rangedHit = false;
		super.onThrowComplete(cell);
	}
}
