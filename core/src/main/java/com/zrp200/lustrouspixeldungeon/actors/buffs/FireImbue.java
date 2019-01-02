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

package com.zrp200.lustrouspixeldungeon.actors.buffs;

import com.watabou.noosa.Image;
import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.effects.particles.FlameParticle;
import com.zrp200.lustrouspixeldungeon.levels.Level;
import com.zrp200.lustrouspixeldungeon.levels.Terrain;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.scenes.GameScene;
import com.zrp200.lustrouspixeldungeon.ui.BuffIndicator;

public class FireImbue extends ActiveBuff {
	
	{
		type = buffType.POSITIVE;
		announced = true;
	}

	public static final float DURATION	= 50f;

    @Override
	public boolean act() {
		if (Dungeon.level.map[target.pos] == Terrain.GRASS) {
			Level.set(target.pos, Terrain.EMBERS);
			GameScene.updateMap(target.pos);
		}

		return super.act();
	}

	public void proc(Char enemy){
		if (Random.Int(2) == 0)
			Buff.affect( enemy, Burning.class ).reignite( enemy );

		enemy.sprite.emitter().burst( FlameParticle.FACTORY, 2 );
	}

	@Override
	public int icon() {
		return BuffIndicator.FIRE;
	}
	
	@Override
	public void tintIcon(Image icon) {
		FlavourBuff.greyIcon(icon, 5f, left);
	}

	@Override
	public String toString() {
		return Messages.get(this, "name");
	}

	{
		immunities.add( Burning.class );
	}
}
