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

package com.zrp200.lustrouspixeldungeon;

import com.watabou.noosa.Game;
import com.watabou.noosa.RenderedText;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PlatformSupport;
import com.zrp200.lustrouspixeldungeon.scenes.PixelScene;
import com.zrp200.lustrouspixeldungeon.scenes.WelcomeScene;

public class LustrousPixelDungeon extends Game {

	public enum Version {
		v012("0.1.2",423),
		v011("0.1.1",418),
		v010("0.1.0",416),
		v001("0.0.1",407);

		public static final Version latest = values()[0]; // the current version
		public final String name;
		public final int versionCode;
		Version(String name, int code) {
			this.name = "v" + name;
			this.versionCode = code;
		}
	}
	
	public LustrousPixelDungeon( PlatformSupport platform ) {
		super( sceneClass == null ? WelcomeScene.class : sceneClass, platform );
	}
	
	@Override
	public void create() {
		super.create();

		updateSystemUI();
		
		Music.INSTANCE.enable( LustSettings.music() );
		Music.INSTANCE.volume( LustSettings.musicVol()/10f );
		Sample.INSTANCE.enable( LustSettings.soundFx() );
		Sample.INSTANCE.volume( LustSettings.SFXVol()/10f );

		Sample.INSTANCE.load(
				Assets.SND_CLICK,	Assets.SND_BADGE,	Assets.SND_GOLD,

				Assets.SND_STEP, 	Assets.SND_WATER,	Assets.SND_OPEN,	Assets.SND_UNLOCK,
				Assets.SND_ITEM, 	Assets.SND_DEWDROP,	Assets.SND_HIT, 	Assets.SND_MISS,

				Assets.SND_DESCEND,	Assets.SND_EAT, 	Assets.SND_READ, 	Assets.SND_LULLABY,
				Assets.SND_DRINK, 	Assets.SND_SHATTER,	Assets.SND_ZAP, 	Assets.SND_LIGHTNING,
				Assets.SND_LEVELUP, Assets.SND_DEATH, 	Assets.SND_CHALLENGE,
				Assets.SND_CURSED, 	Assets.SND_EVOKE, 	Assets.SND_TRAP, 	Assets.SND_TOMB,
				Assets.SND_ALERT, 	Assets.SND_MELD, 	Assets.SND_BOSS, 	Assets.SND_BLAST,
				Assets.SND_PLANT, 	Assets.SND_RAY, 	Assets.SND_BEACON, 	Assets.SND_TELEPORT,
				Assets.SND_CHARMS, 	Assets.SND_MASTERY,	Assets.SND_PUFF, 	Assets.SND_ROCKS,
				Assets.SND_BURNING, Assets.SND_FALLING, Assets.SND_GHOST, 	Assets.SND_SECRET,
				Assets.SND_BONES, 	Assets.SND_BEE, 	Assets.SND_DEGRADE,	Assets.SND_MIMIC
		);

		RenderedText.setFont( LustSettings.systemFont() ? null : "pixelfont.ttf");
	}

	public static void switchNoFade(Class<? extends PixelScene> c){
		switchNoFade(c, null);
	}

	public static void switchNoFade(Class<? extends PixelScene> c, SceneChangeCallback callback) {
		PixelScene.noFade = true;
		switchScene( c, callback );
	}

	public static void seamlessResetScene(SceneChangeCallback callback) {
		if (scene() instanceof PixelScene){
			((PixelScene) scene()).saveWindows();
			switchNoFade((Class<? extends PixelScene>) sceneClass, callback );
		} else {
			resetScene();
		}
	}

	public static void seamlessResetScene(){
		seamlessResetScene(null);
	}

	@Override
	protected void switchScene() {
		super.switchScene();
		if (scene instanceof PixelScene){
			((PixelScene) scene).restoreWindows();
		}
	}
	
	@Override
	public void resize( int width, int height ) {
		if (scene instanceof PixelScene &&
				(height != Game.height || width != Game.width)) {
			((PixelScene) scene).saveWindows();
		}

		super.resize( width, height );

		updateDisplaySize();

	}

	public void updateDisplaySize(){
		platform.updateDisplaySize();
	}

	public static void updateSystemUI() {
		platform.updateSystemUI();
	}
}