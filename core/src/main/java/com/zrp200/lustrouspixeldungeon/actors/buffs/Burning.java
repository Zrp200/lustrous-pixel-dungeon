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

package com.zrp200.lustrouspixeldungeon.actors.buffs;

import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Badges;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.blobs.Blob;
import com.zrp200.lustrouspixeldungeon.actors.blobs.Fire;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.actors.mobs.Thief;
import com.zrp200.lustrouspixeldungeon.effects.particles.ElmoParticle;
import com.zrp200.lustrouspixeldungeon.items.Heap;
import com.zrp200.lustrouspixeldungeon.items.Item;
import com.zrp200.lustrouspixeldungeon.items.food.ChargrilledMeat;
import com.zrp200.lustrouspixeldungeon.items.food.MysteryMeat;
import com.zrp200.lustrouspixeldungeon.items.scrolls.Scroll;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.scenes.GameScene;
import com.zrp200.lustrouspixeldungeon.sprites.CharSprite;
import com.zrp200.lustrouspixeldungeon.ui.BuffIndicator;
import com.zrp200.lustrouspixeldungeon.utils.GLog;

import java.util.ArrayList;

public class Burning extends ActiveBuff implements Hero.Doom {
	
	public static final float DURATION = 8f;

	//for tracking burning of hero items
	private int burnIncrement = 0;

	// you shouldn't really be doing anything other than this
	public static Burning reignite(Char target, float duration) {
		return Buff.prolong(target, Burning.class, duration);
	}

	// shortcut for just adding burning with standard duration
	public static Burning reignite(Char target) {
		return Buff.prolong(target, Burning.class, DURATION);
	}


	private static final String BURN	= "burnIncrement";

	{
		type = buffType.NEGATIVE;
		startGrey = 2;
		fx = CharSprite.State.BURNING;

		isDamaging = true;
	}

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( BURN, burnIncrement );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
		burnIncrement = bundle.getInt( BURN );
	}

	public static int damageRoll() {
		return Random.NormalIntRange(1, 3+Dungeon.depth/4);
	}

	@Override
	public boolean act() {
		
		if (target.isAlive() && !target.isImmune(getClass())) {
			
			int damage = damageRoll();
			Buff.detach( target, Chill.class);

			if (target instanceof Hero) {
				
				Hero hero = (Hero)target;

				hero.damage( damage, this );
				burnIncrement++;

				//at 4+ turns, there is a (turns-3)/3 chance an item burns
				if (Random.Int(3) < (burnIncrement - 3)){
					burnIncrement = 0;

					ArrayList<Item> burnable = new ArrayList<>();
					//does not reach inside of containers
					for (Item i : hero.belongings.backpack.items){
						if ((i instanceof Scroll && i.isDestroyable())
								|| i instanceof MysteryMeat){
							burnable.add(i);
						}
					}

					if (!burnable.isEmpty()){
						Item toBurn = Random.element(burnable).detach(hero.belongings.backpack);
						GLog.w( Messages.get(this, "burnsup", Messages.capitalize(toBurn.toString())) );
						if (toBurn.getClass() == MysteryMeat.class){
							ChargrilledMeat steak = new ChargrilledMeat();
							if (!steak.collect( hero.belongings.backpack )) {
								Dungeon.level.drop( steak, hero.pos ).sprite.drop();
							}
						}
						Heap.burnFX( hero.pos );
					}
				}
				
			} else {
				target.damage( damage, this );
			}

			if (target instanceof Thief) {

				Item item = ((Thief) target).item;

				if (item instanceof Scroll && item.isDestroyable()) {
					target.sprite.emitter().burst( ElmoParticle.FACTORY, 6 );
					((Thief)target).item = null;
				} else if (item instanceof MysteryMeat) {
					target.sprite.emitter().burst( ElmoParticle.FACTORY, 6 );
					((Thief)target).item = new ChargrilledMeat();
				}

			}

		} else {

			detach();
		}
		
		if (Dungeon.level.flamable[target.pos] && Blob.volumeAt(target.pos, Fire.class) == 0) {
			GameScene.add( Blob.seed( target.pos, 4, Fire.class ) );
		}
		
		super.act();
		
		if (Dungeon.level.water[target.pos] && !target.flying) {
			detach();
		}
		
		return true;
	}
	
	public void reignite() {
		prolong(target,getClass(),DURATION);
	}
	
	@Override
	public int icon() {
		return BuffIndicator.FIRE;
	}

	@Override
	public void onDeath() {
		
		Badges.validateDeathFromFire();
		
		Dungeon.fail( getClass() );
		GLog.n( Messages.get(this, "ondeath") );
	}
}
