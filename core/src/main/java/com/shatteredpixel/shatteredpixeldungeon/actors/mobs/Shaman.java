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
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ShamanSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public abstract class Shaman extends Mob implements Callback {
	public static Class<?extends Mob> random() {
		return Shaman.LightningShaman.class;
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

	abstract protected void applyZap(Char enemy);

	protected boolean doZap(Char enemy) {
		boolean visible = fieldOfView[pos] || fieldOfView[enemy.pos];
		if (visible) {
			sprite.zap( enemy.pos );
		}

		spend( TIME_TO_ZAP );

		if (hit( this, enemy, true )) {
			applyZap(enemy);
			if (enemy == Dungeon.hero && !enemy.isAlive()) {
					Dungeon.fail( getClass() );
					GLog.n( Messages.get(this, "zap_kill") );
			}
		} else
			enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
		return !visible;
	}
	@Override
	protected boolean doAttack( Char enemy ) {
		if (Dungeon.level.distance( pos, enemy.pos ) <= 1)
			return super.doAttack( enemy );
		else return doZap(enemy);
	}
	
	@Override
	public void call() {
		next();
	}
	public static class LightningShaman extends Shaman {
		{
			spriteClass = ShamanSprite.LightningShaman.class;
			properties.add(Property.ELECTRIC);
		}
		protected void applyZap(Char enemy ) {
			int damage = Random.NormalIntRange(4, 12);
			if (Dungeon.level.water[enemy.pos] && !enemy.flying)
				damage *= 1.5f;
			enemy.damage(damage, this);
			enemy.sprite.centerEmitter().burst( SparkParticle.FACTORY, 3 );
			enemy.sprite.flash();
			if(enemy == Dungeon.hero) Camera.main.shake( 2, 0.3f );
		}
	}
	//public static class FireboltShaman extends Shaman {}
}
