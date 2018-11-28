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

package com.zrp200.lustrouspixeldungeon.items.potions.brews;

import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.zrp200.lustrouspixeldungeon.Assets;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Actor;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.blobs.Blob;
import com.zrp200.lustrouspixeldungeon.actors.blobs.Fire;
import com.zrp200.lustrouspixeldungeon.actors.blobs.Freezing;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Roots;
import com.zrp200.lustrouspixeldungeon.items.potions.PotionOfLiquidFlame;
import com.zrp200.lustrouspixeldungeon.items.potions.exotic.PotionOfSnapFreeze;
import com.zrp200.lustrouspixeldungeon.scenes.GameScene;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSpriteSheet;

public class FrostfireBrew extends Brew {
	
	{
		image = ItemSpriteSheet.BREW_FROSTFIRE;
	}
	
	@Override
	public void shatter(int cell) {
		if (Dungeon.level.heroFOV[cell]) {
			setKnown();
			
			splash( cell );
			Sample.INSTANCE.play( Assets.SND_SHATTER );
		}
		
		Fire fire = (Fire)Dungeon.level.blobs.get( Fire.class );
		
		for (int offset : PathFinder.NEIGHBOURS9){
			if (!Dungeon.level.solid[cell+offset]) {
				
				Freezing.affect( cell + offset, fire );
				
				Char ch = Actor.findChar( cell + offset);
				if (ch != null){
					Buff.affect(ch, Roots.class, 10f);
				}
				GameScene.add(Blob.seed(cell + offset, 10, Fire.class));
				
			}
		}
	}
	
	@Override
	public int price() {
		//prices of ingredients
		return quantity * (50 + 30);
	}
	
	public static class Recipe extends com.zrp200.lustrouspixeldungeon.items.Recipe.SimpleRecipe {
		
		{
			inputs =  new Class[]{PotionOfSnapFreeze.class, PotionOfLiquidFlame.class};
			inQuantity = new int[]{1, 1};
			
			cost = 8;
			
			output = FrostfireBrew.class;
			outQuantity = 1;
		}
		
	}
}
