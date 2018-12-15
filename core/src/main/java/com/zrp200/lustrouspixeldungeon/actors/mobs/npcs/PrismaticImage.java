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

package com.zrp200.lustrouspixeldungeon.actors.mobs.npcs;

import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Assets;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.PrismaticGuard;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.actors.mobs.Mob;
import com.zrp200.lustrouspixeldungeon.effects.CellEmitter;
import com.zrp200.lustrouspixeldungeon.effects.Speck;
import com.zrp200.lustrouspixeldungeon.sprites.CharSprite;
import com.zrp200.lustrouspixeldungeon.sprites.PrismaticSprite;

public class PrismaticImage extends HeroImage {
	
	{
		spriteClass = PrismaticSprite.class;
		
		HP = HT = 8;
		
		WANDERING = new Wandering();
	}
	
	private int deathTimer = -1;
	
	@Override
	protected boolean act() {
		
		if (!isAlive()){
			deathTimer--;
			
			if (deathTimer > 0) {
				sprite.alpha((deathTimer + 3) / 8f);
				spend(TICK);
			} else remove();
			return true;
		}
		
		if (deathTimer != -1){
			if (paralysed == 0) sprite.remove(CharSprite.State.PARALYSED);
			deathTimer = -1;
			sprite.resetColor();
		}
		
		return super.act();
	}

	@Override
	protected void remove() {
		destroy();
		sprite.die();
	}

	@Override
	public void die(Object cause) {
		if (deathTimer == -1) {
			deathTimer = 5;
			sprite.add(CharSprite.State.PARALYSED);
		}
	}

	private static final String TIMER	= "timer";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( TIMER, deathTimer );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		deathTimer = bundle.getInt( TIMER );
	}
	
	public void duplicate( Hero hero, int HP ) {
		this.HP = HP;
		HT = PrismaticGuard.maxHP( hero );
		super.duplicate(hero);
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 1 + hero.lvl/8, 4 + hero.lvl/2 );
	}
	
	@Override
	public int drRoll() {
		if (hero != null){
			return hero.drRoll();
		} else {
			return 0;
		}
	}

	@Override
	public float stealth() {
		return (hero != null)
			? hero.stealth()
			: super.stealth();
	}

	@Override
	public int defenseProc(Char enemy, int damage) {
		damage = super.defenseProc(enemy, damage);
		if (hero.belongings.armor != null){
			return hero.belongings.armor.proc( enemy, this, damage );
		} else {
			return damage;
		}
	}
	
	@Override
	public float speed() {
		if (hero.belongings.armor != null){
			return hero.belongings.armor.speedFactor(this, super.speed());
		}
		return super.speed();
	}
	
	private class Wandering extends Mob.Wandering{
		
		@Override
		public boolean act(boolean justAlerted) {
			if (!enemyInFOV()){
				Buff.affect(hero, PrismaticGuard.class).set( HP );
				destroy();
				CellEmitter.get(pos).start( Speck.factory(Speck.LIGHT), 0.2f, 3 );
				sprite.die();
				Sample.INSTANCE.play( Assets.SND_TELEPORT );
				return true;
			} else {
				return super.act(justAlerted);
			}
		}
		
	}
	
}
