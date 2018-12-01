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

package com.zrp200.lustrouspixeldungeon.windows;

import com.zrp200.lustrouspixeldungeon.actors.hero.HeroSubClass;
import com.zrp200.lustrouspixeldungeon.items.TomeOfMastery;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.scenes.PixelScene;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSprite;
import com.zrp200.lustrouspixeldungeon.ui.RedButton;
import com.zrp200.lustrouspixeldungeon.ui.RenderedTextMultiline;
import com.zrp200.lustrouspixeldungeon.ui.Window;

public class WndChooseWay extends Window {
	
	private static final int WIDTH		= 120;
	private static final int BTN_HEIGHT	= 18;
	private static final float GAP		= 2;
	private static final float CHOICE_WIDTH = (WIDTH - GAP)/2;
	
	public WndChooseWay( final TomeOfMastery tome, final HeroSubClass... subclasses ) {

		super();

		IconTitle titlebar = new IconTitle();
		titlebar.icon(new ItemSprite(tome.image(), null));
		titlebar.label(tome.name());
		titlebar.setRect(0, 0, WIDTH, 0);
		add(titlebar);

		RenderedTextMultiline hl = PixelScene.renderMultiline(6);
		StringBuilder subclass_desc = new StringBuilder();
		for (HeroSubClass way : subclasses) subclass_desc.append(way.desc() + "\n\n");
		hl.text(subclass_desc + Messages.get(this, "message"), WIDTH);
		hl.setPos(titlebar.left(), titlebar.bottom() + GAP);
		add(hl);
		RedButton[] btnWay = new RedButton[subclasses.length];
		for (int i = 0; i < subclasses.length; i++) {
			final HeroSubClass subclass = subclasses[i];
			btnWay[i] = new RedButton(subclasses[i].title().toUpperCase()) {
				@Override
				protected void onClick() {
					hide();
					tome.choose(subclass);
				}
			};
			btnWay[i].setRect(
					i%2==1 ? btnWay[i-1].right() : 0,
					i>0 ? btnWay[i-1].top() + (BTN_HEIGHT + GAP) * (i/2) : hl.bottom() + GAP, // this should force two buttons per line
					/*i+1==subclasses.length && i%2==0 ? WIDTH :*/ CHOICE_WIDTH,
					BTN_HEIGHT
			);
			add(btnWay[i]);
		}
		
		RedButton btnCancel = new RedButton( Messages.get(this, "cancel") ) {
			@Override protected void onClick() {  hide(); }
		};
		RedButton lastBtn = btnWay[btnWay.length - 1];
		if(subclasses.length%2==1) 	btnCancel.setRect(lastBtn.right(), lastBtn.top(), CHOICE_WIDTH, BTN_HEIGHT);
		else 						btnCancel.setRect( 0, btnWay[btnWay.length-1].bottom() + GAP, WIDTH, BTN_HEIGHT );
		add( btnCancel );
		resize( WIDTH, (int)btnCancel.bottom() );
	}
}
