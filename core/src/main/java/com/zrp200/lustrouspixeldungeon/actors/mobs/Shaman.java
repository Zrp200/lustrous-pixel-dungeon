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
import com.zrp200.lustrouspixeldungeon.LustrousPixelDungeon;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.blobs.Blob;
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
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Blazing;
import com.zrp200.lustrouspixeldungeon.mechanics.Ballistica;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.scenes.GameScene;
import com.zrp200.lustrouspixeldungeon.sprites.CharSprite;
import com.zrp200.lustrouspixeldungeon.sprites.ShamanSprite;
import com.zrp200.lustrouspixeldungeon.utils.GLog;

import java.util.HashMap;

import static com.watabou.utils.Random.NormalIntRange;

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

		damageRoll[0] = 2;
		damageRoll[1] = 8;

		EXP = 6;
		maxLvl = 14;
		armor = 4;

		loot = Generator.Category.SCROLL; // default
		lootChance = 0.33f;
	}

	Class<?extends Wand> wandLoot = null;
	Class<?extends Potion> potionLoot = null;

	@Override
	protected Item createLoot() {
		Item loot = super.createLoot();
		try {
			int seed = Random.Int(20);
			if (seed == 0 && wandLoot != null)
				loot = wandLoot.newInstance();
			else if (seed < 8 && potionLoot != null)
				loot = potionLoot.newInstance();
		} catch (Exception e) {
			LustrousPixelDungeon.reportException(e);
		}
		return loot;
	}

	@Override
	public int attackSkill(Char target) {
		return 11;
	}

	@Override
	protected boolean canAttack(Char enemy) {
		return new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}

	@SuppressWarnings("WeakerAccess")
	final protected void applyZap(int damage) {
		enemy.damage(damage, this, true);
		if (enemy == Dungeon.hero && !enemy.isAlive()) {
			Dungeon.fail(getClass());
			GLog.n(Messages.get(this, "zap_kill"));
		}
	}
	protected abstract void applyZap();

	private boolean doZap() {
		boolean visible = fieldOfView[pos] || fieldOfView[enemy.pos];
		if (visible) {
			( (ShamanSprite) sprite ).zapEnemy();
		}

		spend( TIME_TO_ZAP );
		return !visible;
	}
	@Override
	protected boolean doAttack(Char enemy) {
		if (Dungeon.level.distance( pos, enemy.pos ) <= 1)
			return super.doAttack( enemy );
		else return doZap();
	}
	public void onZapComplete(boolean next) {
		if (hit(this, enemy, true))
			applyZap();
		else
			enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
		if(next) next();
	}
	public final void onZapComplete() { // this is just a way to avoid having to pass an argument
		onZapComplete(true);
	}

	public static class Lightning extends Shaman {
		{
			spriteClass = ShamanSprite.Lightning.class;
			properties.add(Property.ELECTRIC);
			wandLoot = WandOfLightning.class;
		}
		protected void applyZap() {
			int damage = NormalIntRange(4, 12);
			if (Dungeon.level.water[enemy.pos] && !enemy.flying)
				damage *= 1.5f;
			enemy.sprite.centerEmitter().burst( SparkParticle.FACTORY, 3 );
			enemy.sprite.flash();
			if(enemy == Dungeon.hero)
				Camera.main.shake( 2, 0.3f );
			applyZap(damage);
		}
	}
	public static class MagicMissile extends Shaman {
		{
			spriteClass = ShamanSprite.MM.class;
			wandLoot = WandOfMagicMissile.class;
		}

		private boolean zapping;
		protected void applyZap() {
			enemy.sprite.burst(0xFFFFFFFF,2);
			applyZap( NormalIntRange(4,10) );
		}

		public void onZapComplete(boolean next) {
			zapping = true; // this boosts its accuracy temporarily
			try { // we have to override this manually
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
        }

		@Override
		public void onZapComplete(boolean next) {
			GameScene.add( Blob.seed( enemy.pos , 1, Fire.class ) );
        	super.onZapComplete(next);
		}

		protected void applyZap() {
            enemy.sprite.centerEmitter().burst(FlameParticle.FACTORY, 3);
            applyZap( NormalIntRange(4,12) );
			Burning.reignite(enemy,6);
        }

		@Override
		public void damage(int dmg, Object src, boolean magic) {
        	if(src instanceof Firebolt && magic) dmg /= 2; // magic resist.
			super.damage(dmg, src, magic);
		}

		{
			resistances.add(Burning.class);
			resistances.add(Blazing.class);
			resistances.add(WandOfFireblast.class);
		}
	}
    public static class Frost extends Shaman {
		{
			spriteClass = ShamanSprite.Frost.class;

			wandLoot = WandOfFrost.class;
			potionLoot = PotionOfFrost.class;
		}
		protected void applyZap() {
			enemy.sprite.burst( 0xFF99CCFF, 3 );
			applyZap( NormalIntRange(4,10) );
			Chill chill = enemy.buff(Chill.class);
			float extension = Random.Float(1,2);
			if(chill != null && chill.cooldown() + extension > 6) extension = 6-chill.cooldown();
			Buff.affect(enemy,Chill.class,extension);
			Heap heap = Dungeon.level.heaps.get(enemy.pos);
			if(heap != null) heap.freeze();
		}

        @Override
        public void damage(int dmg, Object src, boolean magic) {
            if(src instanceof Frost && magic) dmg/=2;
		    super.damage(dmg, src, magic);
        }
    }
	{
		resistances.add(Chill.class);
		resistances.add(WandOfFrost.class);
		resistances.add(com.zrp200.lustrouspixeldungeon.actors.buffs.Frost.class);
	}
}