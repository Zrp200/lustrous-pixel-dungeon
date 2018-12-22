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

import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Invisibility;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.items.KindOfWeapon;
import com.zrp200.lustrouspixeldungeon.items.rings.RingOfFuror;
import com.zrp200.lustrouspixeldungeon.ui.BuffIndicator;

public class MirrorImage extends HeroImage {

	protected void remove() {
		die(null);
		sprite.killAndErase();
	}
	
	public void duplicate( Hero hero ) {
		super.duplicate(hero);
		Buff.affect(this, MirrorInvis.class, Short.MAX_VALUE);
	}

	protected float attackDelay() {
		float modifier = weapon() != null ? weapon().speedFactor(hero) : RingOfFuror.attackDelayMultiplier(hero) * 0.5f;
		return modifier * super.attackDelay();
	}

	private KindOfWeapon weapon() {
		return hero.belongings.weapon;
	}
	
	@Override
	public int damageRoll() {
		int damage;
		if (weapon() != null){
			damage = weapon().damageRoll(this);
		} else {
			damage = hero.damageRoll();//handles ring of force
		}
		return (damage+1)/2; //half hero damage, rounded up
	}

	@Override
	protected boolean canAttack(Char enemy) {
		return super.canAttack(enemy) ||
				(weapon() != null && weapon().canReach(this, enemy.pos));
	}
	
	@Override
	public int drRoll() {
		if (hero != null && weapon() != null){
			return Random.NormalIntRange(0, weapon().defenseFactor(this)/2);
		} else {
			return 0;
		}
	}
	
	@Override
	public int attackProc( Char enemy, int damage ) {
		damage = super.attackProc( enemy, damage );

		MirrorInvis buff = buff(MirrorInvis.class);
		if (buff != null){
			buff.detach();
		}
		if (weapon() != null){
			return weapon().proc( this, enemy, damage );
		} else {
			return damage;
		}
	}
	
	public static class MirrorInvis extends Invisibility {
		
		{
			announced = false;
		}
		
		@Override
		public int icon() {
			return BuffIndicator.NONE;
		}
	}
}