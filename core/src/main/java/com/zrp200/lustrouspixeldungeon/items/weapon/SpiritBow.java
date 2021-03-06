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

import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.zrp200.lustrouspixeldungeon.Assets;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Actor;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.effects.Splash;
import com.zrp200.lustrouspixeldungeon.items.Heap;
import com.zrp200.lustrouspixeldungeon.items.rings.RingOfFuror;
import com.zrp200.lustrouspixeldungeon.items.rings.RingOfSharpshooting;
import com.zrp200.lustrouspixeldungeon.items.weapon.missiles.MissileWeapon;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.scenes.CellSelector;
import com.zrp200.lustrouspixeldungeon.scenes.GameScene;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSpriteSheet;
import com.zrp200.lustrouspixeldungeon.sprites.MissileSprite;
import com.zrp200.lustrouspixeldungeon.ui.QuickSlotButton;

import java.util.ArrayList;

import static com.zrp200.lustrouspixeldungeon.items.weapon.Weapon.Augment.NONE;
import static com.zrp200.lustrouspixeldungeon.items.weapon.Weapon.Augment.SPEED;


public class SpiritBow extends Weapon {
	
	private static final String AC_SHOOT		= "SHOOT";
	
	{
		image = ItemSpriteSheet.SPIRIT_BOW;
		
		defaultAction = AC_SHOOT;
		usesTargeting = true;
		
		unique = true;
		bones = false;

		enchantKnown = true;
	}
	
	public boolean sniperSpecial = false;
	
	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_EQUIP);
		actions.add(AC_SHOOT);
		return actions;
	}
	
	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (action.equals(AC_SHOOT)) {
			GameScene.selectCell( shooter );
		}
	}
	
	@Override
	public String info() {
		String info = desc();

		info += "\n\n" + Messages.get( SpiritBow.class, "stats",
				Math.round(augment.damageFactor(min())),
				Math.round(augment.damageFactor(max())),
				STRReq());

		if (STRReq() > Dungeon.hero.STR()) {
			info += " " + Messages.get(Weapon.class, "too_heavy");
		} else if (Dungeon.hero.STR() > STRReq()){
			info += " " + Messages.get(Weapon.class, "excess_str", Dungeon.hero.STR() - STRReq());
		}

		switch (augment) {
			case SPEED:
				info += "\n\n" + Messages.get(Weapon.class, "faster");
				break;
			case DAMAGE:
				info += "\n\n" + Messages.get(Weapon.class, "stronger");
				break;
			case NONE:
		}

		if (isVisiblyEnchanted()){
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

		info += "\n\n" + Messages.get(MissileWeapon.class, "distance");

		return info;
	}
	
	@Override
	public int STRReq(int lvl) {
		lvl = Math.max(0, lvl);
		//strength req decreases at +1,+3,+6,+10,etc.
		return 10 - (int)(Math.sqrt(8 * lvl + 1) - 1)/2;
	}

	public int min() {
		return Math.max( 0, min(level() + RingOfSharpshooting.levelDamageBonus(Dungeon.hero) ) );
	}

	@Override
	public int max() {
		return max(RingOfSharpshooting.levelDamageBonus(Dungeon.hero)) + (int)(maxScale()*trueLevel());
	}

	@Override
	public int minBase() {
		return 1;
	}
	public int maxBase() {
		return 6;
	}

	public int minScale() {
		return 1;
	}
	public int maxScale() {
		return 2;
	}

	private int targetPos;
	
	@Override
	public int damageRoll(Char owner) {
		float damage = super.damageRoll(owner);
		
		if (sniperSpecial){
			switch (augment){
				case NONE:
					damage *= 0.667f;
					break;
				case SPEED:
					damage /= 2f;
					break;
				case DAMAGE:
					int distance = Dungeon.level.distance(owner.pos, targetPos) - 1;
					damage *= 0.1f *( 10 + distance ); // +10% for each space of distance.
					break;
			}
		}
		
		return Math.round(damage);
	}
	
	@Override
	public float speedFactor(Char owner) {
		if (sniperSpecial){
			switch (augment){
				case NONE: default:
					return 0f;
				case SPEED:
					return 1f * RingOfFuror.attackDelayMultiplier(owner);
				case DAMAGE:
					return 2f * RingOfFuror.attackDelayMultiplier(owner);
			}
		} else {
			return super.speedFactor(owner);
		}
	}
	
	@Override
	public int level() {
		// don't care about halves.
		return (int) trueLevel();
	}

	private double trueLevel() { // for max calcs, rounds to nearest 0.5
		return Math.round((Dungeon.hero == null ? 0 : Dungeon.hero.lvl/5d)*2)/2d
				+ super.level();
	}
	
	//for fetching upgrades from a boomerang from pre-0.7.1
	public int spentUpgrades() {
		return super.level() - (curseInfusionBonus ? 1 : 0);
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	public SpiritArrow knockArrow(){
		return new SpiritArrow();
	}
	
	public class SpiritArrow extends MissileWeapon {
		
		{
			image = ItemSpriteSheet.SPIRIT_ARROW;
			enchantment = SpiritBow.this.enchantment;
			augment = SpiritBow.this.augment;
			enchantKnown = true; // for fun visual effects
		}
		private boolean hit = false;

		@Override
		public int damageRoll(Char owner) {
			return SpiritBow.this.damageRoll(owner);
		}

		@Override
		public int level() {
			return SpiritBow.this.level();
		}

		public SpiritBow getBow() { // for one specific use
			return SpiritBow.this;
		}
		
		@Override
		public float speedFactor(Char user) {
			return SpiritBow.this.speedFactor(user);
		}

        @Override
        public Heap drop(int pos) { // this'll prevent weird stuff from happening!
			if( !hit && Dungeon.level.heroFOV[pos] ) Splash.at( pos, 0xCC99FFFF, 1 );
			return Dungeon.level.drop(null,pos);
        }

		@Override
		public boolean stickTo(Char enemy) {
			return hit = true;
		}

		@Override
		public int STRReq(int lvl) {
			return SpiritBow.this.STRReq(lvl);
		}

		@Override
		protected void afterThrow(int cell) {
			if (sniperSpecial && augment == NONE) sniperSpecial = false;
			super.afterThrow(cell);
		}

		int flurryCount = -1;

		@Override
		public void cast(final Hero user, final int dst) {
			final int cell = throwPos( user, dst );
			targetPos = cell;
			if (sniperSpecial && augment == SPEED){
				if (flurryCount == -1) flurryCount = 3;
				
				final Char enemy = Actor.findChar( cell );
				
				if (enemy == null){
					user.spendAndNext(castDelay(user, dst));
					sniperSpecial = false;
					flurryCount = -1;
					return;
				}
				QuickSlotButton.target(enemy);
				
				final boolean last = flurryCount == 1;
				
				user.busy();
				
				Sample.INSTANCE.play( Assets.SND_MISS, 0.6f, 0.6f, 1.5f );
				
				((MissileSprite) user.sprite.parent.recycle(MissileSprite.class)).
						reset(user.sprite,
								cell,
								this,
								new Callback() {
									@Override
									public void call() {
										if (enemy.isAlive()) {
											curUser = user;
											onThrow(cell);
										}
										
										if (last) {
											user.spendAndNext(castDelay(user, dst));
											sniperSpecial = false;
											flurryCount = -1;
										}
									}
								});
				
				user.sprite.zap(cell, new Callback() {
					@Override
					public void call() {
						flurryCount--;
						if (flurryCount > 0){
							cast(user, dst);
						}
					}
				});
				
			} else {
				super.cast(user, dst);
			}
		}
	}
	
	private CellSelector.Listener shooter = new CellSelector.Listener() {
		@Override
		public void onSelect( Integer target ) {
			if (target != null) {
				knockArrow().cast(curUser, target);
			}
		}
		@Override
		public String prompt() {
			return Messages.get(SpiritBow.class, "prompt");
		}
	};
}
