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

import com.watabou.noosa.audio.Sample;
import com.zrp200.lustrouspixeldungeon.Assets;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Actor;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.actors.mobs.Mob;
import com.zrp200.lustrouspixeldungeon.effects.CellEmitter;
import com.zrp200.lustrouspixeldungeon.effects.Speck;
import com.zrp200.lustrouspixeldungeon.items.Heap;
import com.zrp200.lustrouspixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.utils.GLog;

public class TeleportationTrap extends Trap {

	{
		color = TEAL;
		shape = DOTS;
	}

	@Override
	public void activate() {

		CellEmitter.get(pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
		Sample.INSTANCE.play( Assets.SND_TELEPORT );

		Char ch = Actor.findChar( pos);
		if (ch != null) {
			if (ch instanceof Hero) {
				ScrollOfTeleportation.teleportHero((Hero) ch);
			} else {
				int count = 10;
				int pos;
				do {
					pos = Dungeon.level.randomRespawnCell();
					if (count-- <= 0) {
						break;
					}
				} while (pos == -1);
				
				if (pos == -1 || Dungeon.bossLevel()) {
					
					GLog.w(Messages.get(ScrollOfTeleportation.class, "no_tele"));
					
				} else {
					
					ch.pos = pos;
					if (ch instanceof Mob && ((Mob) ch).state == ((Mob) ch).HUNTING) {
						((Mob) ch).state = ((Mob) ch).WANDERING;
					}
					ch.sprite.place(ch.pos);
					ch.sprite.visible = Dungeon.level.heroFOV[pos];
					
				}
			}
		}

		Heap heap = Dungeon.level.heaps.get(this.pos);
		int respawn = Dungeon.level.randomRespawnCell();
		while (heap != null && respawn != -1) {
			if (heap.type == Heap.Type.FOR_SALE) return;
			if (heap.type == Heap.Type.HEAP) {
				Dungeon.level.drop(heap.pickUp(), respawn);
				heap = Dungeon.level.heaps.get(this.pos);
				respawn = Dungeon.level.randomRespawnCell();
			}
			else {
				Dungeon.level.heaps.remove(this.pos);
				heap.pos = respawn;
				Dungeon.level.heaps.put(respawn, heap);
				heap.sprite.place(respawn);
				return;
			}
		}
	}
}
