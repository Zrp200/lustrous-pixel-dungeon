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

import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.mobs.Mob;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.ui.BuffIndicator;

public class Terror extends ActiveBuff {

	public int object = 0;

	Mob target; // now a mob instance

	private static final String OBJECT    = "object";

	{
		type = buffType.NEGATIVE;
	}

	@Override
	public boolean act() {
		boolean result = super.act();
		if( target.buffs().contains(this) ) target.state = target.FLEEING;
		return result;
	}

	@Override
	public void detach() {
		target.state = target.HUNTING;
		super.detach();
	}

	@Override
	public boolean attachTo(Char target) {
		if(target instanceof Mob && super.attachTo(target) ) {
			this.target = (Mob) super.target;
			for(Amok buff : this.target.buffs(Amok.class))
				buff.detach();
			this.target.state = this.target.FLEEING; // right away.
			return true;
		}
		return false;
	}

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		bundle.put(OBJECT, object);
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		object = bundle.getInt( OBJECT );
	}

	@Override
	public int icon() {
		return BuffIndicator.TERROR;
	}

	public void recover() {
		left -= 5f;
		if (left <= 0){
			detach();
		}
	}
}
