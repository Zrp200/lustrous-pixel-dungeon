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

import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.blobs.Blob;
import com.zrp200.lustrouspixeldungeon.actors.blobs.Fire;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Blazing;
import com.zrp200.lustrouspixeldungeon.levels.Level;
import com.zrp200.lustrouspixeldungeon.levels.Terrain;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.scenes.GameScene;
import com.zrp200.lustrouspixeldungeon.sprites.CharSprite;
import com.zrp200.lustrouspixeldungeon.ui.BuffIndicator;

public class FireImbue extends ActiveBuff {
	
	{
		type = buffType.POSITIVE;
		fx = CharSprite.State.FIRE_IMBUE;
		startGrey = 7.5f;
	}

	public static final float DURATION	= 50f;

    @Override
	public boolean act() {
    	int pos = target.pos;
    	int terrain = Dungeon.level.map[pos];

    	if(!target.flying) { // why else do fire elementals not set everything aflame?
			// setting grass afire by moving is both ridiculously overpowered and extremely unfun.
			// be mindful to not throw scrolls into doors.
			if ( (terrain == Terrain.GRASS || terrain == Terrain.FURROWED_GRASS) && Blob.volumeAt(pos, Fire.class) <= 0 ) {
					Level.set(pos, Terrain.EMBERS);
					GameScene.updateMap(pos);
			} else Fire.ignite(pos);
    	}
		return super.act();
	}

	public void proc(Char enemy){
		if (Random.Float(3+Dungeon.depth/4f) > 2) { // essentially blazing with weapon level depth/4. +0 at depth 1-3, +6 at depth 26
			Blazing.proc(enemy); // because blazing is good and fun.
		}
    }

	@Override
	public int icon() {
		return BuffIndicator.FIRE;
	}

	@Override
	public String toString() {
		return Messages.get(this, "name");
	}

	{
		resistances.addAll( Char.Property.FIERY.resistances() );
		immunities.addAll( Char.Property.FIERY.immunities() );
	}
}
