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

package com.zrp200.lustrouspixeldungeon.actors.mobs;

import com.watabou.noosa.Camera;
import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.blobs.Fire;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Burning;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Chill;
import com.zrp200.lustrouspixeldungeon.effects.particles.FlameParticle;
import com.zrp200.lustrouspixeldungeon.effects.particles.SparkParticle;
import com.zrp200.lustrouspixeldungeon.items.Generator;
import com.zrp200.lustrouspixeldungeon.items.Heap;
import com.zrp200.lustrouspixeldungeon.items.Item;
import com.zrp200.lustrouspixeldungeon.items.potions.Potion;
import com.zrp200.lustrouspixeldungeon.items.potions.PotionOfFrost;
import com.zrp200.lustrouspixeldungeon.items.potions.PotionOfLiquidFlame;
import com.zrp200.lustrouspixeldungeon.items.wands.Wand;
import com.zrp200.lustrouspixeldungeon.items.wands.WandOfFireblast;
import com.zrp200.lustrouspixeldungeon.items.wands.WandOfFrost;
import com.zrp200.lustrouspixeldungeon.items.wands.WandOfLightning;
import com.zrp200.lustrouspixeldungeon.items.wands.WandOfMagicMissile;
import com.zrp200.lustrouspixeldungeon.mechanics.Ballistica;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.sprites.CharSprite;
import com.zrp200.lustrouspixeldungeon.sprites.ShamanSprite;
import com.zrp200.lustrouspixeldungeon.utils.GLog;

import java.util.HashMap;

public abstract class Shaman extends Mob {
    private final static HashMap<Class<?extends Shaman>, Float> probs = new HashMap<Class<? extends Shaman>,Float>() {
    	{
			put(  Shaman.MagicMissile.class,    6f   ); // 6/13, down from 50%
			put(  Shaman.Lightning.class,       4f   ); // 4/13, up from 30%
			put(  Shaman.Frost.class,           2f   ); // 2/13, up from 10%
			put(  Shaman.Firebolt.class,        1f   ); // 1/13, down from 10%

		}
	};

	public static Class<?extends Mob> random() {
        return Random.chances(probs);
	}
	private static final float TIME_TO_ZAP = 1f;
	{
		HP = HT = 18;
		defenseSkill = 8;

		damageRoll[0] = 2; damageRoll[1] = 8; // 2-8 damage.

		EXP = 6;
		maxLvl = 14;
		armor = 4;

		lootChance = 0.33f;
	}

	Class<?extends Wand> wandLoot = null;
	Class<?extends Potion> potionLoot = null;

	protected Magic magic;

	protected int minZapDmg=4, maxZapDmg=10; // typically zaps do 4-10 damage.

	public static class Magic {} // for distinguishing between magic and not.

	protected void applyZap(int damage) {
		enemy.damage(damage, magic);
		if (enemy == Dungeon.hero && !enemy.isAlive()) {
			Dungeon.fail(getClass());
			GLog.n(Messages.get(this, "zap_kill"));
		}
	}

	private boolean doZap() {
		boolean visible = fieldOfView[pos] || fieldOfView[enemy.pos];
		if (visible) {
			( (ShamanSprite) sprite ).zapEnemy();
		}

		spend( TIME_TO_ZAP );
		return !visible;
	}

	public void onZapComplete(boolean next) {
		if (hit(this, enemy, true))
			applyZap( Random.NormalIntRange(minZapDmg, maxZapDmg) );
		else
			enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
		if(next) next();
	}
	public final void onZapComplete() { // this is just a way to avoid having to pass an argument
		onZapComplete(true);
	}

	private final static float // 30% of drops are potions, 5% are wands, and the rest are scrolls.
            POTION_DROP = .3f,
            WAND_DROP = .05f;
	@Override
	protected Item createLoot() {
	    float seed = Random.Float();

	    if      (seed < WAND_DROP   && wandLoot != null)    loot = wandLoot;
	    else if (seed < POTION_DROP && potionLoot != null)  loot = potionLoot;
	    else                                                loot = Generator.Category.SCROLL;

	    return super.createLoot();
	}

	@Override
	public int attackSkill(Char target) {
		return 11;
	}

	@Override
	protected boolean canAttack(Char enemy) {
		return new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}

	@Override
	protected boolean doAttack(Char enemy) {
		if (Dungeon.level.distance( pos, enemy.pos ) <= 1)
			return super.doAttack( enemy );
		else return doZap();
	}


	public static class Lightning extends Shaman {
		{
			spriteClass = ShamanSprite.Lightning.class;
			properties.add(Property.ELECTRIC);
			wandLoot = WandOfLightning.class;

			maxZapDmg = 12; // 4-12, up from 4-10.
			magic = new LightningBolt();
		}
		public static class LightningBolt extends Magic {}

		protected void applyZap(int damage) {
			if (Dungeon.level.water[enemy.pos] && !enemy.flying)
				damage *= 4/3f;
			enemy.sprite.centerEmitter().burst( SparkParticle.FACTORY, 3 );
			enemy.sprite.flash();
			if(enemy == Dungeon.hero)
				Camera.main.shake( 2, 0.3f );
			super.applyZap(damage);
		}
	}
	public static class MagicMissile extends Shaman {
		{
			spriteClass = ShamanSprite.MM.class;
			wandLoot = WandOfMagicMissile.class;
			magic = new MagicBolt();
		}
		public static class MagicBolt extends Magic { }

		private boolean zapping;
		protected void applyZap(int damage) {
			enemy.sprite.burst(0xFFFFFFFF,2);
			super.applyZap(damage);
		}

		public void onZapComplete(boolean next) {
			zapping = true; // this boosts its accuracy temporarily
			try { // this is the whole reason onZapComplete takes an argument.
				super.onZapComplete(false);
			} finally {
				zapping = false;  // deactivate said boost
			}
			if(next) next();
		}

		@Override
		public int attackSkill(Char target) {
			return Math.round( super.attackSkill(target) * (zapping ? 1.5f : 1f) );
		}
	}
	public static class Firebolt extends Shaman {
        {
            spriteClass = ShamanSprite.Firebolt.class;

            wandLoot 	= WandOfFireblast.class;
            potionLoot 	= PotionOfLiquidFlame.class;

			maxZapDmg = 12; // up from 10
			magic = new BoltOfFire();
        }
        public static class BoltOfFire extends Magic {}

		@Override
		public void onZapComplete(boolean next) {
        	super.onZapComplete(next);
			Fire.ignite(enemy.pos); // firebolts start fires. Flammable tiles catch fire and scrolls get annihilated.
            Fire.burnTerrain(enemy.pos);
        }

		protected void applyZap(int damage) {
            enemy.sprite.centerEmitter().burst(FlameParticle.FACTORY, 3);
            super.applyZap(damage);
			Burning.reignite(enemy,6);
        }

		{
            // very nearly fiery, but not quite.
            resistances.addAll( Property.FIERY.resistances() );
            resistances.addAll( Property.FIERY.immunities()  );
		}
	}
    public static class Frost extends Shaman {
		{
			spriteClass = ShamanSprite.Frost.class;

			wandLoot = WandOfFrost.class;
			potionLoot = PotionOfFrost.class;

			magic = new FrostBolt();
		}

		public static class FrostBolt extends Magic { }

		protected void applyZap(int damage) {
			enemy.sprite.burst(0xFF99CCFF, 3);
			super.applyZap(damage);
			Chill chill = enemy.buff(Chill.class);
			float extension = Random.Float(1.5f, 2.5f);

			if (chill != null && chill.cooldown() + extension > 6)
				Buff.prolong(enemy, Chill.class, 6);
			else Buff.affect(enemy, Chill.class, extension);

			Heap heap = Dungeon.level.heaps.get(enemy.pos);
			if (heap != null) heap.freeze();
		}

		{
			resistances.add(Chill.class);
			resistances.add(Frost.FrostBolt.class);
			resistances.add(WandOfFrost.class);
			resistances.add(com.zrp200.lustrouspixeldungeon.actors.buffs.Frost.class);
		}
	}
}