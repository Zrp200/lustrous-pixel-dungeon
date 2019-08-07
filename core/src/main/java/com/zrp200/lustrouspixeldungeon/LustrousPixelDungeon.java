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

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.watabou.noosa.Game;
import com.watabou.noosa.RenderedText;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.DeviceCompat;
import com.zrp200.lustrouspixeldungeon.scenes.PixelScene;
import com.zrp200.lustrouspixeldungeon.scenes.WelcomeScene;

import javax.microedition.khronos.opengles.GL10;

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
	
	public LustrousPixelDungeon() {
		super(WelcomeScene.class);
	}

	
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate(savedInstanceState);

		updateSystemUI();
		LustSettings.landscape ( LustSettings.landscape() );
		
		Music.INSTANCE.enable( LustSettings.music() );
		Music.INSTANCE.volume( LustSettings.musicVol()/10f );
		Sample.INSTANCE.enable( LustSettings.soundFx() );
		Sample.INSTANCE.volume( LustSettings.SFXVol()/10f );
		
		Music.setMuteListener();

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

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if (scene instanceof PixelScene){
			((PixelScene) scene).saveWindows();
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onWindowFocusChanged( boolean hasFocus ) {
		super.onWindowFocusChanged( hasFocus );
		if (hasFocus) updateSystemUI();
	}

	@Override
	@SuppressWarnings("deprecation")
	public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
		super.onMultiWindowModeChanged(isInMultiWindowMode);
		updateSystemUI();
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
	public void onSurfaceChanged( GL10 gl, int width, int height ) {

		if (scene instanceof PixelScene &&
				(height != Game.height || width != Game.width)) {
			((PixelScene) scene).saveWindows();
		}

		super.onSurfaceChanged( gl, width, height );

		updateDisplaySize();

	}

	public void updateDisplaySize(){
		boolean landscape = LustSettings.landscape();

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				instance.setRequestedOrientation(landscape ?
						ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE :
						ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
			} else {
				instance.setRequestedOrientation(landscape ?
						ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE :
						ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
		
		if (view.getMeasuredWidth() == 0 || view.getMeasuredHeight() == 0)
			return;

		dispWidth = view.getMeasuredWidth();
		dispHeight = view.getMeasuredHeight();

		float dispRatio = dispWidth / (float)dispHeight;

		float renderWidth = dispRatio > 1 ? PixelScene.MIN_WIDTH_L : PixelScene.MIN_WIDTH_P;
		float renderHeight = dispRatio > 1 ? PixelScene.MIN_HEIGHT_L : PixelScene.MIN_HEIGHT_P;

		//force power saver in this case as all devices must run at at least 2x scale.
		if (dispWidth < renderWidth*2 || dispHeight < renderHeight*2)
			LustSettings.put( LustSettings.KEY_POWER_SAVER, true );

		if (LustSettings.powerSaver()){

			int maxZoom = (int)Math.min(dispWidth/renderWidth, dispHeight/renderHeight);

			renderWidth *= Math.max( 2, Math.round(1f + maxZoom*0.4f));
			renderHeight *= Math.max( 2, Math.round(1f + maxZoom*0.4f));

			if (dispRatio > renderWidth / renderHeight){
				renderWidth = renderHeight * dispRatio;
			} else {
				renderHeight = renderWidth / dispRatio;
			}

			final int finalW = Math.round(renderWidth);
			final int finalH = Math.round(renderHeight);
			if (finalW != width || finalH != height){

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						view.getHolder().setFixedSize(finalW, finalH);
					}
				});

			}
		} else {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					view.getHolder().setSizeFromLayout();
				}
			});
		}
	}

	public static void updateSystemUI() {

		boolean fullscreen = Build.VERSION.SDK_INT < Build.VERSION_CODES.N
								|| !instance.isInMultiWindowMode();

		if (fullscreen){
			instance.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		} else {
			instance.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		}

		if (DeviceCompat.supportsFullScreen()){
			if (fullscreen && LustSettings.fullscreen()) {
				instance.getWindow().getDecorView().setSystemUiVisibility(
						View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
						View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
						View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
						View.SYSTEM_UI_FLAG_HIDE_NAVIGATION );
			} else {
				instance.getWindow().getDecorView().setSystemUiVisibility(
						View.SYSTEM_UI_FLAG_LAYOUT_STABLE );
			}
		}

	}
	
}