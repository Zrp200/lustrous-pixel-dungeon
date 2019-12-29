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

package com.zrp200.lustrouspixeldungeon.items.weapon.missiles.darts;

import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.LustrousPixelDungeon;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.actors.hero.HeroSubClass;
import com.zrp200.lustrouspixeldungeon.items.Generator;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.plants.Blindweed;
import com.zrp200.lustrouspixeldungeon.plants.Dreamfoil;
import com.zrp200.lustrouspixeldungeon.plants.Earthroot;
import com.zrp200.lustrouspixeldungeon.plants.Fadeleaf;
import com.zrp200.lustrouspixeldungeon.plants.Firebloom;
import com.zrp200.lustrouspixeldungeon.plants.Icecap;
import com.zrp200.lustrouspixeldungeon.plants.Plant;
import com.zrp200.lustrouspixeldungeon.plants.Rotberry;
import com.zrp200.lustrouspixeldungeon.plants.Sorrowmoss;
import com.zrp200.lustrouspixeldungeon.plants.Starflower;
import com.zrp200.lustrouspixeldungeon.plants.Stormvine;
import com.zrp200.lustrouspixeldungeon.plants.Sungrass;
import com.zrp200.lustrouspixeldungeon.plants.Swiftthistle;
import com.zrp200.lustrouspixeldungeon.scenes.GameScene;
import com.zrp200.lustrouspixeldungeon.windows.WndOptions;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class TippedDart extends Dart {
	
	{
		tier = 2;

		//so that slightly more than 1.5x durability is needed for 2 uses
		baseUses = 0.65f;

		durabilityScaling = 1.8f;
		enchantDurability = 1f; // no boost
        value = 8;
	}
	
	private static final String AC_CLEAN = "CLEAN";
	
	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions( hero );
		actions.remove( AC_TIP );
		actions.add( AC_CLEAN );
		return actions;
	}
	
	@Override
	public void execute(final Hero hero, String action) {
		if (action.equals( AC_CLEAN )){
			
			GameScene.show(new WndOptions(Messages.get(this, "clean_title"),
					Messages.get(this, "clean_desc"),
					Messages.get(this, "clean_all"),
					Messages.get(this, "clean_one"),
					Messages.get(this, "cancel")){
				@Override
				protected void onSelect(int index) {
					if (index == 0){
						detachAll(hero.belongings.backpack);
						new Dart().quantity(quantity).collect();
						
						hero.spend( 1f );
						hero.busy();
						hero.sprite.operate(hero.pos);
					} else if (index == 1){
						detach(hero.belongings.backpack);
						if (!new Dart().collect()) Dungeon.level.drop(new Dart(), hero.pos).sprite.drop();
						
						hero.spend( 1f );
						hero.busy();
						hero.sprite.operate(hero.pos);
					}
				}
			});
			
		}
		super.execute(hero, action);
	}
	
	//exact same damage as regular darts, despite being higher tier.


	private Dart untip() {
		Dart d = (Dart) new Dart().emulate(this);
		d.reset();
		return d;
	}

	@Override
	protected float durabilityPerUse() {
		float use = super.durabilityPerUse();

		if (Dungeon.hero.subClass == HeroSubClass.WARDEN){
			use /= 2f;
		}

		return use;
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		damage = super.proc(attacker, defender, damage);
		//need to spawn a dart
		if (durability <= 0){
			//attempt to stick the dart to the enemy, just drop it if we can't.
			Dart d = untip();
			d.quantity(1);
			if( !d.stickTo(defender) ) d.drop(defender.pos);
		}
		return damage;
	}

	private static HashMap<Class<?extends Plant.Seed>, Class<?extends TippedDart>> types = new HashMap<>();
	static {
		types.put(Blindweed.Seed.class,     BlindingDart.class);
		types.put(Dreamfoil.Seed.class,     SleepDart.class);
		types.put(Earthroot.Seed.class,     ParalyticDart.class);
		types.put(Fadeleaf.Seed.class,      DisplacingDart.class);
		types.put(Firebloom.Seed.class,     IncendiaryDart.class);
		types.put(Icecap.Seed.class,        ChillingDart.class);
		types.put(Rotberry.Seed.class,      RotDart.class);
		types.put(Sorrowmoss.Seed.class,    PoisonDart.class);
		types.put(Starflower.Seed.class,    HolyDart.class);
		types.put(Stormvine.Seed.class,     ShockingDart.class);
		types.put(Sungrass.Seed.class,      HealingDart.class);
		types.put(Swiftthistle.Seed.class,  AdrenalineDart.class);
	}
	
	public static TippedDart getTipped( Plant.Seed s, int quantity ){
		try {
			return (TippedDart) types.get(s.getClass()).newInstance().quantity(quantity);
		} catch (Exception e){
			LustrousPixelDungeon.reportException(e);
			return null;
		}
	}

	public static TippedDart randomTipped( int quantity ){
		Plant.Seed s;
		do{
			s = (Plant.Seed) Generator.random(Generator.Category.SEED);
		} while (!types.containsKey(s.getClass()));

		return getTipped(s, quantity );

	}
	
}
