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

package com.zrp200.lustrouspixeldungeon.sprites;

import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.zrp200.lustrouspixeldungeon.Assets;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.mobs.Shaman;
import com.zrp200.lustrouspixeldungeon.effects.MagicMissile;

public class ShamanSprite extends MobSprite {
	protected int boltType;

	public ShamanSprite(String textureFile) {
		texture(textureFile);

		TextureFilm frames = new TextureFilm(textureFile, 12, 15);

		idle = new Animation(2, true);
		idle.frames(frames, 0, 0, 0, 1, 0, 0, 1, 1);

		run = new Animation(12, true);
		run.frames(frames, 4, 5, 6, 7);

		attack = new Animation(12, false);
		attack.frames(frames, 2, 3, 0);

		zap = attack.clone();

		die = new Animation(12, false);
		die.frames(frames, 8, 9, 10);

		play(idle);
	}

	public void zapEnemy() {
		Char enemy = ((Shaman) ch).getEnemy();
		zap(enemy.pos);
		MagicMissile.boltFromChar(parent,
				boltType,
				this,
				enemy.pos,
				new Callback() {
					@Override
					public void call() {
						((Shaman) ch).onZapComplete();
					}
				});
		Sample.INSTANCE.play(Assets.SND_ZAP);
	}

	public static class Lightning extends ShamanSprite {
		{ textureFile = Assets.LSHAMAN; }

		public Lightning() {super(Assets.LSHAMAN);}
		public void zapEnemy() {
			Shaman shaman = (Shaman) ch;
			Char enemy = shaman.getEnemy();
			parent.add(new com.zrp200.lustrouspixeldungeon.effects.Lightning(shaman.pos, enemy.sprite.destinationCenter(), new Callback() {
				@Override
				public void call() {
					ch.next();
				}
			}));
			zap(enemy.pos);
			shaman.onZapComplete();
		}
	}

	public static class Firebolt extends ShamanSprite {
		public Firebolt(){
			super(Assets.FIRESHAMAN);
			this.boltType = MagicMissile.FIRE;
		}
	}
	public static class MM extends ShamanSprite {
		public MM(){
			super(Assets.SHAMAN);
			boltType = MagicMissile.MAGIC_MISSILE;
		}
	}
	public static class Frost extends ShamanSprite {
		public Frost() {
			super(Assets.SHAMAN_F);
			boltType = MagicMissile.FROST;
		}
	}
}
