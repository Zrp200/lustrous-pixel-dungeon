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

public abstract class Shaman extends Mob implements Callback {
	public static Class<?extends Mob> random() {
		if(Random.Int(2) == 0) return Shaman.MagicMissile.class;
		if(Random.Int(2) == 0) return Shaman.Lightning.class;
		return Random.Int(2) == 0
				? Shaman.Firebolt.class
				: Shaman.Frost.class;
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

	protected void applyZap(int damage) {
		enemy.damage(damage, this);
		if (enemy == Dungeon.hero && !enemy.isAlive()) {
			Dungeon.fail(getClass());
			GLog.n(Messages.get(this, "zap_kill"));
		}
	}
	protected abstract void applyZap();

	protected boolean doZap() {
		boolean visible = fieldOfView[pos] || fieldOfView[enemy.pos];
		if (visible) {
			sprite.zap( enemy.pos );
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
	public String description() {
	    return super.description() + "\n\n" + Messages.get(this, "variant_desc");
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
			int damage = Random.NormalIntRange(5, 11);
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
		}
		protected void applyZap() {
			enemy.sprite.burst(0xFFFFFFFF,2);
			applyZap(Random.NormalIntRange(4,10));
		}
		public void onZapComplete() { // a temporary solution to what I want at the moment, hoping to get a bit more elegant later
			if (!hit(this, enemy, false)) // if it won't hit without the magic boost
				super.onZapComplete(); // try again with it.
			else applyZap();
			next();
		}
	}
	public static class Firebolt extends Shaman {
        {
            spriteClass = ShamanSprite.Firebolt.class;

            resistances.add(Burning.class);
            resistances.add(Inferno.class);
            resistances.add(Blazing.class);
            resistances.add(WandOfFireblast.class);
            resistances.add(Shaman.Firebolt.class);
        }
        protected void applyZap() {
            enemy.sprite.centerEmitter().burst(FlameParticle.FACTORY, 3);
			GameScene.add( Blob.seed( enemy.pos , 1, Fire.class ) );
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
			applyZap( Random.NormalIntRange(4,8));
			Buff.prolong( enemy, Chill.class, Random.Float(1,5) );
			Heap heap = Dungeon.level.heaps.get(enemy.pos);
			if(heap != null) heap.freeze();
		}
	}
}