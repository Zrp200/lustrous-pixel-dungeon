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

package com.zrp200.lustrouspixeldungeon.items.scrolls.exotic;

import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Assets;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Invisibility;
import com.zrp200.lustrouspixeldungeon.actors.mobs.Mob;
import com.zrp200.lustrouspixeldungeon.actors.mobs.npcs.Sheep;
import com.zrp200.lustrouspixeldungeon.effects.CellEmitter;
import com.zrp200.lustrouspixeldungeon.effects.Flare;
import com.zrp200.lustrouspixeldungeon.effects.Speck;
import com.zrp200.lustrouspixeldungeon.scenes.GameScene;
import com.zrp200.lustrouspixeldungeon.ui.TargetHealthIndicator;

public class ScrollOfPolymorph extends ExoticScroll {
	
	{
		initials = 10;
		isAOE = true;
	}
	
	@Override
	public void doRead() {
		
		new Flare( 5, 32 ).color( 0xFFFFFF, true ).show( curUser.sprite, 2f );
		Sample.INSTANCE.play( Assets.SND_READ );
		Invisibility.dispel();
		
		for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
			if (mob.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[mob.pos]) {
				if (!mob.properties().contains(Char.Property.BOSS)
						&& !mob.properties().contains(Char.Property.MINIBOSS)){
					Sheep sheep = new Sheep();
					sheep.lifespan = 10;
					sheep.pos = mob.pos;
					
					//awards half exp for each sheep-ified mob
					//50% chance to round up, 50% to round down
					if (mob.EXP % 2 == 1) mob.EXP += Random.Int(2);
					mob.EXP /= 2;
					
					mob.destroy();
					mob.sprite.killAndErase();
					Dungeon.level.mobs.remove(mob);
					TargetHealthIndicator.instance.target(null);
					GameScene.add(sheep);
					CellEmitter.get(sheep.pos).burst(Speck.factory(Speck.WOOL), 4);
				}
			}
		}
		setKnown();
		
		readAnimation();
		
	}
	
}
