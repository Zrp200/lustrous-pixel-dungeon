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

package com.zrp200.lustrouspixeldungeon.items.armor.curses;

import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.LustrousPixelDungeon;
import com.zrp200.lustrouspixeldungeon.actors.Actor;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.PinCushion;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.actors.mobs.Mimic;
import com.zrp200.lustrouspixeldungeon.actors.mobs.Mob;
import com.zrp200.lustrouspixeldungeon.actors.mobs.Statue;
import com.zrp200.lustrouspixeldungeon.actors.mobs.Thief;
import com.zrp200.lustrouspixeldungeon.actors.mobs.npcs.MirrorImage;
import com.zrp200.lustrouspixeldungeon.items.armor.Armor;
import com.zrp200.lustrouspixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.zrp200.lustrouspixeldungeon.scenes.GameScene;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSprite;

import java.util.ArrayList;

public class Multiplicity extends Armor.Glyph {

	private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );

	@Override
	public int proc(Armor armor, Char attacker, Char defender, int damage) {

		if (Random.Int(20) == 0){
			ArrayList<Integer> spawnPoints = new ArrayList<>();

			for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
				int p = defender.pos + PathFinder.NEIGHBOURS8[i];
				if (Actor.findChar( p ) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
					spawnPoints.add( p );
				}
			}

			if (spawnPoints.size() > 0) {

				Mob m = null;
				if (Random.Int(2) == 0 && defender instanceof Hero){
					m = new MirrorImage();
					((MirrorImage)m).duplicate( (Hero)defender );

				} else {
					//FIXME should probably have a mob property for this
					if (attacker.properties().contains(Char.Property.BOSS) || attacker.properties().contains(Char.Property.MINIBOSS)
							|| attacker instanceof Mimic || attacker instanceof Statue){
						m = Dungeon.level.createMob();
					} else {
						try {
							Actor.fixTime();
							
							m = (Mob)attacker.getClass().newInstance();
							Bundle store = new Bundle();
							attacker.storeInBundle(store);
							m.restoreFromBundle(store);
							m.HP = m.HT;
							if (m.buff(PinCushion.class) != null){
								m.remove(m.buff(PinCushion.class));
							}

							//If a thief has stolen an item, that item is not duplicated.
							if (m instanceof Thief){
								((Thief) m).item = null;
							}

						} catch (Exception e) {
							LustrousPixelDungeon.reportException(e);
							m = null;
						}
					}

				}

				if (m != null) {
					GameScene.add(m);
					ScrollOfTeleportation.appear(m, Random.element(spawnPoints));
				}

			}
		}

		return damage;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return BLACK;
	}

	@Override
	public boolean curse() {
		return true;
	}
}
