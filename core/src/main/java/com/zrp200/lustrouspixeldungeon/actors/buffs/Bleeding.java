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

import com.watabou.utils.PointF;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.effects.Splash;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.ui.BuffIndicator;
import com.zrp200.lustrouspixeldungeon.utils.GLog;

import static com.watabou.utils.Random.NormalFloat;

public class Bleeding extends ActiveBuff {

	{
		type = buffType.NEGATIVE;
		startGrey = Dungeon.hero != null ? Dungeon.hero.HP/10f : 3;
		turnReduction = 0; // handled manually
		isDamaging = true;
	}
	
	@Override
	public int icon() {
		return BuffIndicator.BLEEDING;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", Math.round(left));
	}

	@Override
	public boolean act() {
		if (target.isAlive()) {

			left = NormalFloat(left / 2f, left);
			int dmg = Math.round(left);

			if (dmg > 0) {
				target.damage( dmg, this );
				if (target.sprite.visible) {
					Splash.at( target.sprite.center(), -PointF.PI / 2, PointF.PI / 6,
							target.sprite.blood(), Math.min( 10 * dmg / target.HT, 10 ) );
				}
				
				if (target == Dungeon.hero && !target.isAlive()) {
					Dungeon.fail( getClass() );
					GLog.n( Messages.get(this, "ondeath") );
				}
				startGrey = target.HP/10f;
				super.act();
			} else {
				detach();
			}
			
		} else {
			
			detach();
			
		}
		
		return true;
	}

	@Override
	public String heroMessage() {
		return Messages.get(this, "heromsg");
	}

}
