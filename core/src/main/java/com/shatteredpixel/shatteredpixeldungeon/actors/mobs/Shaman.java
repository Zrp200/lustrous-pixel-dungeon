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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFireblast;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blazing;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ShamanSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public abstract class Shaman extends Mob implements Callback {
	public static Class<?extends Mob> random() {
		switch(Random.Int(3)) {
			case 0:
				return Shaman.Firebolt.class;
			default:
				return Shaman.Lightning.class;
		}
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
			int damage = Random.NormalIntRange(4, 12);
			if (Dungeon.level.water[enemy.pos] && !enemy.flying)
				damage *= 1.5f;
			enemy.sprite.centerEmitter().burst( SparkParticle.FACTORY, 3 );
			enemy.sprite.flash();
			if(enemy == Dungeon.hero) Camera.main.shake( 2, 0.3f );
			super.applyZap(damage);
		}
	}
	public static class Firebolt extends Shaman {
        {
            spriteClass = ShamanSprite.Firebolt.class;

            resistances.add(Burning.class);
            resistances.add(Blazing.class);
            resistances.add(WandOfFireblast.class);
            resistances.add(Shaman.Firebolt.class);
        }
        protected void applyZap() {
            enemy.sprite.centerEmitter().burst(FlameParticle.FACTORY, 3);
			Buff.affect( enemy, Burning.class ).reignite( enemy );
            GameScene.add( Blob.seed( enemy.pos , 1, Fire.class ) );
            super.applyZap( Random.NormalIntRange(3,9) );
        }
    }
}