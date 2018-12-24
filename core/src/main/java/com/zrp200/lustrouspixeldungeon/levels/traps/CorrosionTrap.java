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

package com.zrp200.lustrouspixeldungeon.levels.traps;

import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.blobs.Blob;
import com.zrp200.lustrouspixeldungeon.actors.blobs.CorrosiveGas;
import com.zrp200.lustrouspixeldungeon.scenes.GameScene;

public class CorrosionTrap extends BlobTrap {

	{
		color = GREY;
		blobClass = CorrosiveGas.class;
	}

	@Override
	public void activate() {

		CorrosiveGas corrosiveGas = (CorrosiveGas) seedBlob();

		corrosiveGas.setStrength(1+Dungeon.depth/4);

		GameScene.add(corrosiveGas);

	}

	@Override
	protected int blobAmount() {
		return 80+5*Dungeon.depth;
	}
}
