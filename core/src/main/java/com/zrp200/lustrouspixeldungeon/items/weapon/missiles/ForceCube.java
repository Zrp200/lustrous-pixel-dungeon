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

package com.zrp200.lustrouspixeldungeon.items.weapon.missiles;

import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Assets;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Actor;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.items.wands.WandOfBlastWave;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSpriteSheet;
import com.zrp200.lustrouspixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public class ForceCube extends MissileWeapon {
	
	{
		image = ItemSpriteSheet.FORCE_CUBE;
		
		tier = 5;
		baseUses = 5;
		
		sticky = false;
	}

	public final ArrayList<Char> targets = new ArrayList<Char>() {
		@Override
		public boolean add(Char ch) {
			if(ch == null) return false; // this prevents null values.
			return super.add(ch);
		}
	};
	public int pos;
	@Override
	protected void onThrow(int cell) {
		Dungeon.level.press(cell, null, true);

		targets.clear();
		pos = cell; // this is useful exclusively for elastic procs.
		
		for (int i : PathFinder.NEIGHBOURS8){
			Dungeon.level.press(cell + i, null, true);
			targets.add(Actor.findChar(cell + i));
		}
		Random.shuffle(targets); // this enforces a randomized order
		targets.add( Actor.findChar(cell) ); // last one is always in center, for elastic logic.
		
		for (Char target : targets){
			curUser.shoot(target, this);
			if (target == Dungeon.hero && !target.isAlive()){
				Dungeon.fail(getClass());
				GLog.n(Messages.get(this, "ondeath"));
			}
		}
		
		useDurability();
		WandOfBlastWave.BlastWave.blast(cell);
		Sample.INSTANCE.play( Assets.SND_BLAST );
		onThrowComplete(cell);
	}
}
