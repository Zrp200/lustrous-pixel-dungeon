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

package com.zrp200.lustrouspixeldungeon.scenes;

import com.watabou.input.PointerEvent;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.RenderedText;
import com.watabou.utils.DeviceCompat;
import com.zrp200.lustrouspixeldungeon.Assets;
import com.zrp200.lustrouspixeldungeon.LustSettings;
import com.zrp200.lustrouspixeldungeon.LustrousPixelDungeon;
import com.zrp200.lustrouspixeldungeon.effects.Flare;
import com.zrp200.lustrouspixeldungeon.ui.Archs;
import com.zrp200.lustrouspixeldungeon.ui.ExitButton;
import com.zrp200.lustrouspixeldungeon.ui.Icons;
import com.zrp200.lustrouspixeldungeon.ui.RenderedTextMultiline;
import com.zrp200.lustrouspixeldungeon.ui.Window;

public class AboutScene extends PixelScene {

	private static final String TITLE_LPD = "Lustrous Pixel Dungeon";

	private static final String TXT_LPD = "Code: Zrp200 \"Palkia\"\n" +
			"Graphics: s0i5l3a1s, videogamer1002" + (
					LustSettings.landscape() ? "\n\nAdditional Thanks to PD Suggestion Workshop and Pixel Dungeon discords!" : "");

	private static final String TTL_SHPX = "Shattered Pixel Dungeon";

	private static final String TXT_SHPX =
			"Design, Code, & Graphics: Evan";

	private static final String LNK_SHPX = "ShatteredPixel.com";

	private static final String TTL_WATA = "Pixel Dungeon";

	private static final String TXT_WATA =
			"Code & Graphics: Watabou\n" +
			"Music: Cube_Code";
	
	private static final String LNK_WATA = "pixeldungeon.watabou.ru";

	@Override
	public void create() {
		super.create();

		final float
				colWidth = Camera.main.width / (LustSettings.landscape() ? 3 : 1),
				colTop = (Camera.main.height / 2) - (LustSettings.landscape() ? 30 : 100),
				offset = LustSettings.landscape() ? colWidth : 0;

		Image zrp = new Image(Assets.ZRP200);
		zrp.x = (colWidth - zrp.width()) / 2;
		zrp.y = colTop;
		align(zrp);
		add( zrp );
		new Flare( 7, 36 ).color( 0x9b57b6, true ).show( zrp, 0 ).angularSpeed = 20;

		RenderedText lustTitle = renderText( TITLE_LPD, 8);
		add(lustTitle);
		lustTitle.x = (colWidth - lustTitle.width()) / 2;
		lustTitle.y = zrp.y + zrp.height + 5;
		align(lustTitle);

		RenderedTextMultiline lust = renderMultiline( TXT_LPD, 8 );
		lust.maxWidth((int)Math.min(colWidth, 120));
		add( lust );

		lust.setPos((colWidth - lust.width()) / 2, lustTitle.y + lustTitle.height() + 10);
		align(lust);


		Image shpx = Icons.SHPX.get();
		shpx.x = (colWidth - shpx.width()) / 2 + offset;
		shpx.y = LustSettings.landscape() ?
				colTop :
				lust.top() + lust.height() + 15;
		align(shpx);
		add( shpx );

		new Flare( 7, 64 ).color( 0x225511, true ).show( shpx, 0 ).angularSpeed = +20;

		RenderedText shpxtitle = renderText( TTL_SHPX, 8 );
		shpxtitle.hardlight( Window.SHPX_COLOR );
		align(shpxtitle);
		add( shpxtitle );

		shpxtitle.x = (colWidth - shpxtitle.width()) / 2 + offset;
		shpxtitle.y = shpx.y + shpx.height + 5;
		align(shpxtitle);

		PointerArea shpxhotArea = new PointerArea( shpxtitle.x, shpxtitle.y, shpxtitle.width(), shpxtitle.height() ) {
			@Override
			protected void onClick( PointerEvent touch ) {
				DeviceCompat.openURI( "https://" + LNK_SHPX );
			}
		};
		add(shpxhotArea);

		RenderedTextMultiline shpxtext = renderMultiline( TXT_SHPX, 8 );
		shpxtext.maxWidth((int)Math.min(colWidth, 120));
		add( shpxtext );

		shpxtext.setPos((colWidth - shpxtext.width()) / 2 + offset, shpxtitle.y + shpxtitle.height() + 10);
		align(shpxtext);

		Image wata = Icons.WATA.get();
		wata.x = offset*2 + (colWidth - wata.width()) / 2;
		wata.y = LustSettings.landscape() ?
						colTop:
						shpxtext.top() + wata.height + 15;
		align(wata);
		add( wata );

		new Flare( 7, 64 ).color( 0x112233, true ).show( wata, 0 ).angularSpeed = +20;

		RenderedText wataTitle = renderText( TTL_WATA, 8 );
		wataTitle.hardlight(Window.TITLE_COLOR);
		add( wataTitle );

		wataTitle.x = (colWidth - wataTitle.width()) / 2 + offset*2;
		wataTitle.y = wata.y + wata.height() + 10;
		align(wataTitle);

		PointerArea hotArea = new PointerArea( wataTitle.x, wataTitle.y, wataTitle.width(), wataTitle.height() ) {
			@Override
			protected void onClick( PointerEvent touch ) {
				DeviceCompat.openURI( "https://" + LNK_WATA );
			}
		};
		add( hotArea );

		RenderedTextMultiline wataText = renderMultiline( TXT_WATA, 8 );
		wataText.maxWidth((int)Math.min(colWidth, 120));
		add( wataText );

		wataText.setPos(offset * 2 + (colWidth - wataText.width()) / 2, wataTitle.y + wataTitle.height() + 10);
		align(wataText);
		
		Archs archs = new Archs();
		archs.setSize( Camera.main.width, Camera.main.height );
		addToBack( archs );

		ExitButton btnExit = new ExitButton();
		btnExit.setPos( Camera.main.width - btnExit.width(), 0 );
		add( btnExit );

		fadeIn();
	}
	
	@Override
	protected void onBackPressed() {
		LustrousPixelDungeon.switchNoFade(TitleScene.class);
	}
}
