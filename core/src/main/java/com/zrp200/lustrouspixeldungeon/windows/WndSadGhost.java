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

package com.zrp200.lustrouspixeldungeon.windows;

import com.zrp200.lustrouspixeldungeon.Challenges;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.mobs.npcs.Ghost;
import com.zrp200.lustrouspixeldungeon.items.Item;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.scenes.PixelScene;
import com.zrp200.lustrouspixeldungeon.ui.RedButton;
import com.zrp200.lustrouspixeldungeon.ui.RenderedTextMultiline;
import com.zrp200.lustrouspixeldungeon.ui.Window;
import com.zrp200.lustrouspixeldungeon.utils.GLog;

public class WndSadGhost extends Window {

	private static final int WIDTH		= 120;
	private static final int BTN_HEIGHT	= 20;
	private static final float GAP		= 2;
	
	public WndSadGhost( final Ghost ghost, final Ghost.Quest quest ) {

		IconTitle titlebar = new IconTitle();
		titlebar.label(Messages.get(this, quest.name + "_title"));
		titlebar.icon(quest.icon);

		titlebar.setRect( 0, 0, WIDTH, 0 );
		add( titlebar );

		RenderedTextMultiline message = PixelScene.renderMultiline( Messages.get(this, quest.name)
				+ Messages.get(this, "give_item"), 6 );
		message.maxWidth(WIDTH);
		message.setPos(0, titlebar.bottom() + GAP);
		add( message );
		
		RedButton btnWeapon = new RedButton( Messages.get(this, "weapon") ) {
			@Override
			protected void onClick() {
				selectReward( ghost, Ghost.Quest.weapon );
			}
		};

		btnWeapon.setRect( 0, message.top() + message.height() + GAP, WIDTH, BTN_HEIGHT );
		add( btnWeapon );

		if (!Dungeon.isChallenged( Challenges.NO_ARMOR )) {
			RedButton btnArmor = new RedButton( Messages.get(this, "armor") ) {
				@Override
				protected void onClick() {
					selectReward(ghost, Ghost.Quest.armor);
				}
			};
			btnArmor.setRect(0, btnWeapon.bottom() + GAP, WIDTH, BTN_HEIGHT);
			add(btnArmor);

			resize(WIDTH, (int) btnArmor.bottom());
		} else {
			resize(WIDTH, (int) btnWeapon.bottom());
		}
	}
	
	private void selectReward( Ghost ghost, Item reward ) {
		
		hide();
		
		if (reward == null) return;
		
		reward.identify();
		if (reward.doPickUp( Dungeon.hero )) {
			GLog.i( Messages.get(Dungeon.hero, "you_now_have", reward.name()) );
		} else {
			Dungeon.level.drop( reward, ghost.pos ).sprite.drop();
		}
		
		ghost.yell( Messages.get(this, "farewell") );
		ghost.die( null );
		
		Ghost.Quest.complete();
	}
}
