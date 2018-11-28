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

package com.zrp200.lustrouspixeldungeon.levels.features;

import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Barkskin;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.actors.hero.HeroClass;
import com.zrp200.lustrouspixeldungeon.actors.hero.HeroSubClass;
import com.zrp200.lustrouspixeldungeon.effects.CellEmitter;
import com.zrp200.lustrouspixeldungeon.effects.particles.LeafParticle;
import com.zrp200.lustrouspixeldungeon.items.Dewdrop;
import com.zrp200.lustrouspixeldungeon.items.Generator;
import com.zrp200.lustrouspixeldungeon.items.Item;
import com.zrp200.lustrouspixeldungeon.items.armor.glyphs.Camouflage;
import com.zrp200.lustrouspixeldungeon.items.artifacts.SandalsOfNature;
import com.zrp200.lustrouspixeldungeon.levels.Level;
import com.zrp200.lustrouspixeldungeon.levels.Terrain;
import com.zrp200.lustrouspixeldungeon.plants.BlandfruitBush;
import com.zrp200.lustrouspixeldungeon.scenes.GameScene;

public class HighGrass {

	public static void trample( Level level, int pos, Char ch ) {
		
		if (ch instanceof Hero && ((Hero) ch).heroClass == HeroClass.HUNTRESS){
			//Level.set(pos, Terrain.FURROWED_GRASS);
			Level.set(pos, Terrain.GRASS);
		} else {
			Level.set(pos, Terrain.GRASS);
		}
		GameScene.updateMap( pos );
		
		int naturalismLevel = 0;

		if (ch != null) {
			SandalsOfNature.Naturalism naturalism = ch.buff( SandalsOfNature.Naturalism.class );
			if (naturalism != null) {
				if (!naturalism.isCursed()) {
					naturalismLevel = naturalism.itemLevel() + 1;
					naturalism.charge();
				} else {
					naturalismLevel = -1;
				}
			}
		}

		if (naturalismLevel >= 0) {
			// Seed, scales from 1/20 to 1/4
			if (Random.Int(20 - (naturalismLevel * 4)) == 0) {
				Item seed = Generator.random(Generator.Category.SEED);

				if (seed instanceof BlandfruitBush.Seed) {
					if (Random.Int(3) - Dungeon.LimitedDrops.BLANDFRUIT_SEED.count >= 0) {
						level.drop(seed, pos).sprite.drop();
						Dungeon.LimitedDrops.BLANDFRUIT_SEED.count++;
					}
				} else
					level.drop(seed, pos).sprite.drop();
			}

			// Dew, scales from 1/6 to 1/3
			if (Random.Int(24 - naturalismLevel*3) <= 3) {
				level.drop(new Dewdrop(), pos).sprite.drop();
			}
		}

		int leaves = 4;
		

		if (ch instanceof Hero) {
			Hero hero = (Hero)ch;

			// Barkskin
			if (hero.subClass == HeroSubClass.WARDEN) {
				Buff.affect(ch, Barkskin.class).set(ch.HT / 3, 1);
				leaves += 4;
			}

			//Camouflage
			//FIXME doesn't work with sad ghost
			if (hero.belongings.armor != null && hero.belongings.armor.hasGlyph(Camouflage.class, hero)){
				Buff.affect(hero, Camouflage.Camo.class).set(3 + hero.belongings.armor.level());
				leaves += 4;
			}
		}
		
		CellEmitter.get( pos ).burst( LeafParticle.LEVEL_SPECIFIC, leaves );
		if (Dungeon.level.heroFOV[pos]) Dungeon.observe();
	}
}
