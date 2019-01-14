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
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.ui.BuffIndicator;

public class AdrenalineSurge extends FlavourBuff {
	
	{
		type = buffType.POSITIVE;
		announced = true;
	}
	
	private int boost=1;
	private float interval;
	
	public void set(int boost, float interval){
		this.boost = Math.max(this.boost,boost);
		this.interval = Math.max(this.interval,interval);
		spend(this.interval - cooldown() );
	}
	
	public int boost(){
		return boost;
	}

	@Override
	public void tintIcon(Image icon) {
		greyIcon(icon,Math.max(5,interval*0.375f), cooldown() );
	}

	@Override
	public boolean act() {
		boost --;
		if (boost > 0){
			spend( interval );
		} else {
			detach();
		}
		return true;
	}
	
	@Override
	public int icon() {
		return BuffIndicator.FURY;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", boost, dispTurns(cooldown()+1));
	}
	
	private static final String BOOST	    = "boost";
	private static final String INTERVAL	    = "interval";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( BOOST, boost );
		bundle.put( INTERVAL, interval );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		boost = bundle.getInt( BOOST );
		//pre-0.7.1
		if (bundle.contains(INTERVAL)) {
			interval = bundle.getFloat(INTERVAL);
		} else {
			interval = 800f;
		}
	}
}
