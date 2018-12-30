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
import com.watabou.utils.Callback;
import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.blobs.Blizzard;
import com.zrp200.lustrouspixeldungeon.actors.blobs.Blob;
import com.zrp200.lustrouspixeldungeon.actors.blobs.Fire;
import com.zrp200.lustrouspixeldungeon.actors.blobs.Freezing;
import com.zrp200.lustrouspixeldungeon.actors.blobs.Inferno;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Burning;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Chill;
import com.zrp200.lustrouspixeldungeon.effects.particles.FlameParticle;
import com.zrp200.lustrouspixeldungeon.effects.particles.SparkParticle;
import com.zrp200.lustrouspixeldungeon.items.Generator;
import com.zrp200.lustrouspixeldungeon.items.Heap;
import com.zrp200.lustrouspixeldungeon.items.wands.WandOfFireblast;
import com.zrp200.lustrouspixeldungeon.items.wands.WandOfFrost;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Blazing;
import com.zrp200.lustrouspixeldungeon.mechanics.Ballistica;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.scenes.GameScene;
import com.zrp200.lustrouspixeldungeon.sprites.CharSprite;
import com.zrp200.lustrouspixeldungeon.sprites.ShamanSprite;
import com.zrp200.lustrouspixeldungeon.utils.GLog;

import java.util.HashMap;

public abstract class Shaman extends Mob implements Callback {
    private final static HashMap<Class<?extends Shaman>, Float> probs = new HashMap<Class<? extends Shaman>,Float>() {
    	{
			put(  Shaman.MagicMissile.class,    5f   );
			put(  Shaman.Lightning.class,       3f   );
			put(  Shaman.Frost.class,           2f   );
			put(  Shaman.Firebolt.class,        1f   );

		}
	};

	public static Class<?extends Mob> random() {
        return Random.chances(probs);
	}
	private static final float TIME_TO_ZAP = 1f;
	{

		HP = HT = 18;
		defenseSkill = 8;

		EXP = 6;
		maxLvl = 14;

		loot = Generator.Category.SCROLL;
		lootChance = 0.33f;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(2, 8);
	}

	@Override
	public int attackSkill(Char target) {
		return 11;
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 4);
	}

	@Override
	protected boolean canAttack(Char enemy) {
		return new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}

	void applyZap(int damage) {
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
	public void onZapComplete() {
		if (hit(this, enemy, true))
			applyZap();
		else
			enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
		next();
	}

	@Override
	public void call() {
		next();
	}

	public static class Lightning extends Shaman {
		{
			spriteClass = ShamanSprite.Lightning.class;
			properties.add(Property.ELECTRIC);
		}
		protected void applyZap() {
			int damage = Random.NormalIntRange(6, 12);
			if (Dungeon.level.water[enemy.pos] && !enemy.flying)
				damage *= 1.25f;
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
		}
		private boolean zapping;
		protected void applyZap() {
			enemy.sprite.burst(0xFFFFFFFF,2);
			applyZap( Random.NormalIntRange(4,12) );
		}
		public void onZapComplete() {
			zapping = true; // this boosts its accuracy temporarily
			try { super.onZapComplete(); } finally {
				zapping = false;  // deactivate said boost
			}
		}

		@Override
		public int attackSkill(Char target) {
			return Math.round( super.attackSkill(target) * (zapping ? 1.5f : 1f) );
		}
	}
	public static class Firebolt extends Shaman {
        {
            spriteClass = ShamanSprite.Firebolt.class;

            resistances.add(Fire.class);
            resistances.add(Burning.class);
            resistances.add(Inferno.class);
            resistances.add(Blazing.class);
            resistances.add(WandOfFireblast.class);
            resistances.add(Shaman.Firebolt.class);
        }

		@Override
		public void onZapComplete() {
			super.onZapComplete();
			GameScene.add( Blob.seed( enemy.pos , 1, Fire.class ) );
		}

		protected void applyZap() {
            enemy.sprite.centerEmitter().burst(FlameParticle.FACTORY, 3);
            applyZap( Random.NormalIntRange(6,12) );
			Buff.affect( enemy, Burning.class ).reignite( enemy );
        }
    }
    public static class Frost extends Shaman {
		{
			spriteClass = ShamanSprite.Frost.class;

			resistances.add(Chill.class);
			resistances.add(Shaman.Frost.class);
			resistances.add(WandOfFrost.class);
			resistances.add(Blizzard.class);
			resistances.add(com.zrp200.lustrouspixeldungeon.actors.buffs.Frost.class);
			resistances.add(Freezing.class);
		}
		protected void applyZap() {
			enemy.sprite.burst( 0xFF99CCFF, 3 );
			applyZap( Random.NormalIntRange(6,10));
			Buff.prolong( enemy, Chill.class, Random.Float(1,5) );
			Heap heap = Dungeon.level.heaps.get(enemy.pos);
			if(heap != null) heap.freeze();
		}
	}
}