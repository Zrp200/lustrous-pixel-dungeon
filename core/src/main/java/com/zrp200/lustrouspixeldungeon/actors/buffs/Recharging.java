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
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.effects.SpellSprite;
import com.zrp200.lustrouspixeldungeon.effects.particles.EnergyParticle;
import com.zrp200.lustrouspixeldungeon.items.scrolls.ScrollOfRecharging;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.ui.BuffIndicator;

public class Recharging extends FlavourBuff {
	
	{
		type = buffType.POSITIVE;
	}

	@Override
	public int icon() {
		return BuffIndicator.RECHARGING;
	}

	@Override
	public void tintIcon(Image icon) {
		FlavourBuff.greyIcon(icon, 5f, cooldown());
	}

	public static void showVFX(Char target) { // the lazy person in me HATES this
		SpellSprite.show( target, SpellSprite.CHARGE );
		ScrollOfRecharging.charge(target);
	}
	
	@Override
	public String toString() {
		return Messages.get(this, "name");
	}

	//want to process partial turns for this buff, and not count it when it's expiring.
	//firstly, if this buff has half a turn left, should give out half the benefit.
	//secondly, recall that buffs execute in random order, so this can cause a problem where we can't simply check
	//if this buff is still attached, must instead directly check its remaining time, and act accordingly.
	//otherwise this causes inconsistent behaviour where this may detach before, or after, a wand charger acts.
	public float remainder() {
		return Math.min(1f, this.cooldown());
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns());
	}
}
