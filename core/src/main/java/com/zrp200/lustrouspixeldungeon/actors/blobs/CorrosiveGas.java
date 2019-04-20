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

import com.watabou.utils.Bundle;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Corrosion;
import com.zrp200.lustrouspixeldungeon.effects.BlobEmitter;
import com.zrp200.lustrouspixeldungeon.effects.Speck;
import com.zrp200.lustrouspixeldungeon.messages.Messages;

public class CorrosiveGas extends Gas {

	//FIXME should have strength per-cell
	private int strength = 0;

	@Override
	protected void evolve() {
		super.evolve();
		if(volume == 0) {
			strength = 0;
		}
	}

	@Override
	protected void affectChar(Char ch) {
		if(volume == 0 || strength == 0) {
			strength = 0;
			return;
		}
		Buff.prolong(ch, Corrosion.class, 2f).setDamage(strength);
	}

	public CorrosiveGas setStrength(int str){
		strength = Math.max(strength, str);
		return this;
	}

	private static final String STRENGTH = "strength";

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		strength = bundle.getInt( STRENGTH );
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( STRENGTH, strength );
	}

	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );

		emitter.pour( Speck.factory(Speck.CORROSION), 0.4f );
	}

	@Override
	public String tileDesc() {
		return Messages.get(this, "desc");
	}
}
