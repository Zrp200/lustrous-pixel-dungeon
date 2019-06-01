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
import com.zrp200.lustrouspixeldungeon.actors.hero.Belongings;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.items.artifacts.Artifact;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.ui.BuffIndicator;

//TODO this may be very powerful, consider balancing
public class ArtifactRecharge extends ActiveBuff {
	
	{
		type = buffType.POSITIVE;
		startGrey = 0; // interferes with tinting for some reason
	}

	@Override
	public boolean act() {
		
		if (target instanceof Hero){
			Belongings b = ((Hero) target).belongings;
			
			if (b.misc1 instanceof Artifact){
				((Artifact)b.misc1).charge((Hero)target);
			}
			if (b.misc2 instanceof Artifact){
				((Artifact)b.misc2).charge((Hero)target);
			}
		}

		return super.act();
	}
	
	@Override
	public int icon() {
		return BuffIndicator.RECHARGING;
	}
	
	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(0, 1f, 0);
	}
	
	@Override
	public String toString() {
		return Messages.get(this, "name");
	}

    @Override
    protected String dispTurns(float input) {
        return super.dispTurns(input+1);
    }
}
