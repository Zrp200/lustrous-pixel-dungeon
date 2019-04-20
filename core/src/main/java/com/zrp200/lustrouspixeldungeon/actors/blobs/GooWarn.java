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

package com.zrp200.lustrouspixeldungeon.actors.blobs;



import com.zrp200.lustrouspixeldungeon.effects.BlobEmitter;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.sprites.GooSprite;

public class GooWarn extends Blob {

	//cosmetic blob, used to warn noobs that goo's pump up should, infact, be avoided.

	{
		//this one needs to act after the Goo
		actPriority = MOB_PRIO - 1;
	}

	protected int pos;

	@Override
	protected void evolve() {
		applyToBlobArea(1, new EvolveCallBack() {
			@Override
			protected void call() {
				off[cell] = Math.max(cur[cell] - 1,0);
				volume += off[cell];
			}
		});
	}

	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );
		emitter.pour(GooSprite.GooParticle.FACTORY, 0.03f );
	}

	@Override
	public String tileDesc() {
		return Messages.get(this, "desc");
	}
}

