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
import com.zrp200.lustrouspixeldungeon.actors.Char;

public abstract class ShieldBuff extends Buff {
	
	private int shielding;

	public void requestUpdate() {
		target.needsShieldUpdate = true;
	}
	
	@Override
	public boolean attachTo(Char target) {
		if (super.attachTo(target)) {
			requestUpdate();
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void detach() {
		requestUpdate();
		super.detach();
	}
	
	public int shielding(){
		return shielding;
	}
	
	public void setShield( int shield ) {
		this.shielding = shield;
		target.needsShieldUpdate = true;
	}
	
	public void incShield(){
		incShield(1);
	}
	
	public void incShield( int amt ){
		shielding += amt;
		target.needsShieldUpdate = true;
	}
	
	public void decShield(){
		decShield(1);
	}
	
	public void decShield( int amt ){
		shielding -= amt;
		target.needsShieldUpdate = true;
	}
	
	//returns the amount of damage leftover
	public int absorbDamage( int dmg ){
		if (shielding >= dmg){
			shielding -= dmg;
			dmg = 0;
		} else {
			dmg -= shielding;
			shielding = 0;
		}
		if (shielding == 0){
			detach();
		}
		target.needsShieldUpdate = true;
		return dmg;
	}
	
	private static final String SHIELDING = "shielding";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( SHIELDING, shielding);
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		shielding = bundle.getInt( SHIELDING );
	}
	
}