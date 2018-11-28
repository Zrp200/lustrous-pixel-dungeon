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

package com.zrp200.lustrouspixeldungeon.items.stones;

import com.watabou.noosa.Image;
import com.zrp200.lustrouspixeldungeon.Assets;
import com.zrp200.lustrouspixeldungeon.effects.Identification;
import com.zrp200.lustrouspixeldungeon.items.Item;
import com.zrp200.lustrouspixeldungeon.items.potions.Potion;
import com.zrp200.lustrouspixeldungeon.items.potions.PotionOfExperience;
import com.zrp200.lustrouspixeldungeon.items.potions.PotionOfFrost;
import com.zrp200.lustrouspixeldungeon.items.potions.PotionOfHaste;
import com.zrp200.lustrouspixeldungeon.items.potions.PotionOfHealing;
import com.zrp200.lustrouspixeldungeon.items.potions.PotionOfInvisibility;
import com.zrp200.lustrouspixeldungeon.items.potions.PotionOfLevitation;
import com.zrp200.lustrouspixeldungeon.items.potions.PotionOfLiquidFlame;
import com.zrp200.lustrouspixeldungeon.items.potions.PotionOfMindVision;
import com.zrp200.lustrouspixeldungeon.items.potions.PotionOfParalyticGas;
import com.zrp200.lustrouspixeldungeon.items.potions.PotionOfPurity;
import com.zrp200.lustrouspixeldungeon.items.potions.PotionOfStrength;
import com.zrp200.lustrouspixeldungeon.items.potions.PotionOfToxicGas;
import com.zrp200.lustrouspixeldungeon.items.potions.exotic.ExoticPotion;
import com.zrp200.lustrouspixeldungeon.items.scrolls.Scroll;
import com.zrp200.lustrouspixeldungeon.items.scrolls.ScrollOfIdentify;
import com.zrp200.lustrouspixeldungeon.items.scrolls.ScrollOfLullaby;
import com.zrp200.lustrouspixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.zrp200.lustrouspixeldungeon.items.scrolls.ScrollOfMirrorImage;
import com.zrp200.lustrouspixeldungeon.items.scrolls.ScrollOfRage;
import com.zrp200.lustrouspixeldungeon.items.scrolls.ScrollOfRecharging;
import com.zrp200.lustrouspixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.zrp200.lustrouspixeldungeon.items.scrolls.ScrollOfRetribution;
import com.zrp200.lustrouspixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.zrp200.lustrouspixeldungeon.items.scrolls.ScrollOfTerror;
import com.zrp200.lustrouspixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.zrp200.lustrouspixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.zrp200.lustrouspixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.scenes.GameScene;
import com.zrp200.lustrouspixeldungeon.scenes.PixelScene;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSprite;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSpriteSheet;
import com.zrp200.lustrouspixeldungeon.ui.IconButton;
import com.zrp200.lustrouspixeldungeon.ui.RedButton;
import com.zrp200.lustrouspixeldungeon.ui.RenderedTextMultiline;
import com.zrp200.lustrouspixeldungeon.ui.Window;
import com.zrp200.lustrouspixeldungeon.utils.GLog;
import com.zrp200.lustrouspixeldungeon.windows.IconTitle;
import com.zrp200.lustrouspixeldungeon.windows.WndBag;

import java.util.HashSet;

public class StoneOfIntuition extends InventoryStone {
	
	
	{
		mode = WndBag.Mode.UNIDED_POTION_OR_SCROLL;
		image = ItemSpriteSheet.STONE_INTUITION;
	}
	
	@Override
	protected void onItemSelected(Item item) {
		
		GameScene.show( new WndGuess(item));
		
	}
	
	//in order of their consumable icon
	public static Class[] potions = new Class[]{
			PotionOfExperience.class,
			PotionOfFrost.class,
			PotionOfHaste.class,
			PotionOfHealing.class,
			PotionOfInvisibility.class,
			PotionOfLevitation.class,
			PotionOfLiquidFlame.class,
			PotionOfMindVision.class,
			PotionOfParalyticGas.class,
			PotionOfPurity.class,
			PotionOfStrength.class,
			PotionOfToxicGas.class
	};
	
	public static Class[] scrolls = new Class[]{
			ScrollOfIdentify.class,
			ScrollOfLullaby.class,
			ScrollOfMagicMapping.class,
			ScrollOfMirrorImage.class,
			ScrollOfRetribution.class,
			ScrollOfRage.class,
			ScrollOfRecharging.class,
			ScrollOfRemoveCurse.class,
			ScrollOfTeleportation.class,
			ScrollOfTerror.class,
			ScrollOfTransmutation.class,
			ScrollOfUpgrade.class
	};
	
	static Class curGuess = null;
	
	public class WndGuess extends Window {
		
		private static final int WIDTH = 120;
		private static final int BTN_SIZE = 20;
		
		public WndGuess(final Item item){
			
			IconTitle titlebar = new IconTitle();
			titlebar.icon( new ItemSprite(ItemSpriteSheet.STONE_INTUITION, null) );
			titlebar.label( Messages.get(StoneOfIntuition.class, "name") );
			titlebar.setRect( 0, 0, WIDTH, 0 );
			add( titlebar );
			
			RenderedTextMultiline text = PixelScene.renderMultiline(6);
			text.text( Messages.get(this, "text") );
			text.setPos(0, titlebar.bottom());
			text.maxWidth( WIDTH );
			add(text);
			
			final RedButton guess = new RedButton(""){
				@Override
				protected void onClick() {
					super.onClick();
					useAnimation();
					if (item.getClass() == curGuess){
						item.identify();
						GLog.p( Messages.get(WndGuess.class, "correct") );
						curUser.sprite.parent.add( new Identification( curUser.sprite.center().offset( 0, -16 ) ) );
					} else {
						GLog.n( Messages.get(WndGuess.class, "incorrect") );
					}
					curGuess = null;
					hide();
				}
			};
			guess.visible = false;
			guess.icon( new ItemSprite(item) );
			guess.enable(false);
			guess.setRect(0, 80, WIDTH, 20);
			add(guess);
			
			float left;
			float top = text.bottom() + 5;
			int rows;
			int placed = 0;
			
			HashSet<Class<?extends Item>> unIDed = new HashSet<>();
			final Class[] all;
			
			final int row;
			if (item.isIdentified()){
				hide();
				return;
			} else if (item instanceof Potion){
				unIDed.addAll(Potion.getUnknown());
				all = potions.clone();
				if (item instanceof ExoticPotion){
					row = 8;
					for (int i = 0; i < all.length; i++){
						all[i] = ExoticPotion.regToExo.get(all[i]);
					}
					HashSet<Class<?extends Item>> exoUID = new HashSet<>();
					for (Class<?extends Item> i : unIDed){
						exoUID.add(ExoticPotion.regToExo.get(i));
					}
					unIDed = exoUID;
				} else {
					row = 0;
				}
			} else if (item instanceof Scroll){
				unIDed.addAll(Scroll.getUnknown());
				all = scrolls.clone();
				if (item instanceof ExoticScroll){
					row = 24;
					for (int i = 0; i < all.length; i++){
						all[i] = ExoticScroll.regToExo.get(all[i]);
					}
					HashSet<Class<?extends Item>> exoUID = new HashSet<>();
					for (Class<?extends Item> i : unIDed){
						exoUID.add(ExoticScroll.regToExo.get(i));
					}
					unIDed = exoUID;
				} else {
					row = 16;
				}
			} else {
				hide();
				return;
			}
			
			if (unIDed.size() < 6){
				rows = 1;
				top += BTN_SIZE/2f;
				left = (WIDTH - BTN_SIZE*unIDed.size())/2f;
			} else {
				rows = 2;
				left = (WIDTH - BTN_SIZE*((unIDed.size()+1)/2))/2f;
			}
			
			for (int i = 0; i < all.length; i++){
				if (!unIDed.contains(all[i])) {
					continue;
				}
				
				final int j = i;
				IconButton btn = new IconButton(){
					@Override
					protected void onClick() {
						curGuess = all[j];
						guess.visible = true;
						guess.text( Messages.get(curGuess, "name") );
						guess.enable(true);
						super.onClick();
					}
				};
				Image im = new Image(Assets.CONS_ICONS, 7*i, row, 7, 8);
				im.scale.set(2f);
				btn.icon(im);
				btn.setRect(left + placed*BTN_SIZE, top, BTN_SIZE, BTN_SIZE);
				add(btn);
				
				placed++;
				if (rows == 2 && placed == ((unIDed.size()+1)/2)){
					placed = 0;
					if (unIDed.size() % 2 == 1){
						left += BTN_SIZE/2f;
					}
					top += BTN_SIZE;
				}
			}
			
			resize(WIDTH, 100);
			
		}
		
		
		@Override
		public void onBackPressed() {
			super.onBackPressed();
			new StoneOfIntuition().collect();
		}
	}
}
