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

package com.zrp200.lustrouspixeldungeon.items.stones;

import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.zrp200.lustrouspixeldungeon.Assets;
import com.zrp200.lustrouspixeldungeon.actors.Actor;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.FlavourBuff;
import com.zrp200.lustrouspixeldungeon.effects.CellEmitter;
import com.zrp200.lustrouspixeldungeon.effects.Speck;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSpriteSheet;

public class StoneOfAggression extends Runestone {
	
	{
		image = ItemSpriteSheet.STONE_AGGRESSION;
	}
	
	@Override
	protected void activate(int cell) {
		
		CellEmitter.center(cell).start( Speck.factory( Speck.SCREAM ), 0.3f, 3 );
		Sample.INSTANCE.play( Assets.SND_READ );
		
		for (int i : PathFinder.NEIGHBOURS9){
			
			Char ch = Actor.findChar( cell + i );
			
			if (ch != null && ch.alignment == Char.Alignment.ENEMY){
				Buff.prolong(ch, Aggression.class, Aggression.DURATION).object = curUser.id();
			}
		}
	
	}
	
	public static class Aggression extends FlavourBuff {
		
		public static final float DURATION = 10f;
		
		public int object = 0;
		
		private static final String OBJECT    = "object";
		
		{
			type = buffType.NEGATIVE;
			announced = true;
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
		public String toString() {
			return Messages.get(this, "name");
		}
		
	}
	
}
