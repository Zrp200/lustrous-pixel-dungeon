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

package com.zrp200.lustrouspixeldungeon.items.stones;

import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Point;
import com.zrp200.lustrouspixeldungeon.Assets;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.effects.CheckedCell;
import com.zrp200.lustrouspixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.zrp200.lustrouspixeldungeon.mechanics.ShadowCaster;
import com.zrp200.lustrouspixeldungeon.scenes.GameScene;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSpriteSheet;

public class StoneOfClairvoyance extends Runestone {
	
	private static final int DIST = 8;
	
	{
		image = ItemSpriteSheet.STONE_CLAIRVOYANCE;
		value = 40/3f;
	}
	
	@Override
	protected void activate(final int cell) {
		Point c = Dungeon.level.cellToPoint(cell);
		
		int[] rounding = ShadowCaster.rounding[DIST];
		
		int left, right;
		int curr;
		boolean noticed = false;
		for (int y = Math.max(0, c.y - DIST); y <= Math.min(Dungeon.level.height()-1, c.y + DIST); y++) {
			if (rounding[Math.abs(c.y - y)] < Math.abs(c.y - y)) {
				left = c.x - rounding[Math.abs(c.y - y)];
			} else {
				left = DIST;
				while (rounding[left] < rounding[Math.abs(c.y - y)]){
					left--;
				}
				left = c.x - left;
			}
			right = Math.min(Dungeon.level.width()-1, c.x + c.x - left);
			left = Math.max(0, left);
			for (curr = left + y * Dungeon.level.width(); curr <= right + y * Dungeon.level.width(); curr++){
				
				curUser.sprite.parent.addToBack( new CheckedCell( curr ) );
				Dungeon.level.mapped[curr] = true;

				if (Dungeon.level.secret[curr]) {
					Dungeon.level.discover(curr);

					if (Dungeon.level.heroFOV[curr]) {
						GameScene.discoverTile(curr, Dungeon.level.map[curr]);
						ScrollOfMagicMapping.discover(curr);
						noticed = true;
					}
				}

			}
		}
		
		if (noticed) {
			Sample.INSTANCE.play( Assets.SND_SECRET );
		}
		
		Sample.INSTANCE.play( Assets.SND_TELEPORT );
		GameScene.updateFog();


	}
	
}
