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

import com.watabou.input.Touchscreen;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.RenderedText;
import com.watabou.noosa.TouchArea;
import com.watabou.noosa.ui.Component;
import com.zrp200.lustrouspixeldungeon.Assets;
import com.zrp200.lustrouspixeldungeon.Chrome;
import com.zrp200.lustrouspixeldungeon.LustrousPixelDungeon;
import com.zrp200.lustrouspixeldungeon.actors.hero.HeroClass;
import com.zrp200.lustrouspixeldungeon.items.Ankh;
import com.zrp200.lustrouspixeldungeon.items.Item;
import com.zrp200.lustrouspixeldungeon.items.armor.curses.Volatility;
import com.zrp200.lustrouspixeldungeon.items.bags.MagicalHolster;
import com.zrp200.lustrouspixeldungeon.items.bombs.TeleportationBomb;
import com.zrp200.lustrouspixeldungeon.items.rings.RingOfWealth;
import com.zrp200.lustrouspixeldungeon.items.wands.WandOfCorruption;
import com.zrp200.lustrouspixeldungeon.items.weapon.curses.Elastic;
import com.zrp200.lustrouspixeldungeon.items.weapon.melee.Knuckles;
import com.zrp200.lustrouspixeldungeon.items.weapon.melee.Quarterstaff;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.sprites.CharSprite;
import com.zrp200.lustrouspixeldungeon.sprites.HeroSprite;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSprite;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSpriteSheet;
import com.zrp200.lustrouspixeldungeon.sprites.ShamanSprite;
import com.zrp200.lustrouspixeldungeon.ui.Archs;
import com.zrp200.lustrouspixeldungeon.ui.ExitButton;
import com.zrp200.lustrouspixeldungeon.ui.Icons;
import com.zrp200.lustrouspixeldungeon.ui.RenderedTextMultiline;
import com.zrp200.lustrouspixeldungeon.ui.ScrollPane;
import com.zrp200.lustrouspixeldungeon.ui.Window;
import com.zrp200.lustrouspixeldungeon.windows.WndTitledMessage;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

//TODO: update this class with relevant info as new versions come out.
public class ChangesScene extends PixelScene {
	private ChangeInfo addSection(String title, boolean isMajor, int color) {
		ChangeInfo changes = new ChangeInfo(title,isMajor,isMajor ? "" : null);
		changes.hardlight( color );
		infos.add(changes);
		return changes;
	}
	public enum Version {
		SHPD070("Shattered v0.7.0", 10,18,2018),
		LUST000("Lustrous v0.0.0", 12,1,2018);

		private String name;
		private Date releaseDate;

		Version(String name, int releaseMonth, int releaseDay, int releaseYear) {
			GregorianCalendar calender = new GregorianCalendar();
			calender.set(releaseYear,releaseMonth-1,releaseDay);

			this.name = name;
			this.releaseDate = calender.getTime();
		}
	}
	private static ChangeButton addDeveloperCommentary(Version release, String commentary,Version...eventsToCompare) {
		StringBuilder message = new StringBuilder();
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
		if(release != null) message.append("_-_ Released on " + dateFormat.format(release.releaseDate) + "\n");
		if(eventsToCompare != null) for(Version event : eventsToCompare)
			message.append(
					"_-_ " +
					( release.releaseDate.getTime() - event.releaseDate.getTime() ) / 86400000 +
					" days after " + event.name + "\n"
			);
		message.append("\n");
		message.append(commentary == null ? "Dev commentary will be added here in the future." : commentary);
		return new ChangeButton(
				new Image(Assets.ZRP200),
				"Developer Commentary",
				message.toString()
		);
	}
	private final ArrayList<ChangeInfo> infos = new ArrayList<>();

	@Override
	public void create() {
		super.create();

		int w = Camera.main.width;
		int h = Camera.main.height;

		RenderedText title = PixelScene.renderText(Messages.get(this, "title"), 9);
		title.hardlight(Window.TITLE_COLOR);
		title.x = (w - title.width()) / 2f;
		title.y = (16 - title.baseLine()) / 2f;
		align(title);
		add(title);

		ExitButton btnExit = new ExitButton();
		btnExit.setPos(Camera.main.width - btnExit.width(), 0);
		add(btnExit);

		NinePatch panel = Chrome.get(Chrome.Type.TOAST);

		int pw = 135 + panel.marginLeft() + panel.marginRight() - 2;
		int ph = h - 16;

		panel.size(pw, ph);
		panel.x = (w - pw) / 2f;
		panel.y = title.y + title.height();
		align(panel);
		add(panel);

		ScrollPane list = new ScrollPane(new Component()) {

			@Override
			public void onClick(float x, float y) {
				for (ChangeInfo info : infos) if (info.onClick(x, y)) return;
			}

		};
		add(list);
		addSection("v0.0.0", true, Window.TITLE_COLOR);
		addSection("0.0.0a",false,Window.TITLE_COLOR).addButtons(
				new ChangeButton(
						new ItemSprite(new MagicalHolster().image()),
						new MagicalHolster().trueName(),
						"_-_ missile weapon durability boost buffed (1.2 --> 4/3)"
				),
				new ChangeButton(
						new ItemSprite(new MagicalHolster().image()),
						new WandOfCorruption().trueName(),
						"_-_ Can no longer inflict cripple\n" +
								"_-_ Instead inflicts vertigo at a reduced chance\n" +
								"_-_ Minor debuffs now have weights:\n" +
								"    _*_ Weaken   : 4\n" +
								"    _*_ Blindness: 3\n" +
								"    _*_ Terror   : 2\n" +
								"    _*_ Vertigo  : 1"
				)
		);
		addSection("New Content", false, Window.TITLE_COLOR).addButtons(
				addDeveloperCommentary( Version.LUST000,
						"I'm honestly just happy to have figured this out. As of this moment, " +
								"I'm waiting on Shattered 0.7.1 to be released so I can implement it.",
						Version.SHPD070),
				new ChangeButton(
						new Image(Assets.HUNTRESS, 0, 15, 12, 15),
						"Huntress (Base)",

						"The Huntress's potential is being wasted by the Boomerang. " +
								"By dumping into the Boomerang, the player wastes the majority of her natural versatility, " +
								"so much so that she is turned into a class that tends to have very repetitive gameplay.\n" +
								"\nTo address this, I have made the following changes:\n\n" +
								"_-_ _Boomerang_ removed from the game.\n" +
								"_-_ Huntress now starts with _two darts_ and a _tier-1 whip_ (a Cord) instead of a _knuckleduster._\n" +
								"_-_ Huntress now starts with the _magical holster_ instead of the _seed pouch._ \n" +
						//		"    _-_ The Darts illustrate her ability to benefit from having bonus strength when throwing missile weapons.\n" +
						//		"    _-_ The Cord gives her semi-reliable range throughout sewers.\n" +
						//		"    _-_ The Cord also illustrates her x-ray vision by allowing otherwise impossible attacks.\n" +
								"\n" +
								"The idea is for the player to use the Huntress's natural perks, which naturally incentivize the player to adopt " +
								"a ranged build, whether it be through missile weapons, wands, or weapons with reach. The bonus durability perk further " +
								"makes missile weapons viable by causing them to last longer and thus have a more defined presence in a run."
				),
				new ChangeButton(
						HeroSprite.avatar(HeroClass.HUNTRESS,6), //Assets.HUNTRESS, 0, 90, 12, 15),
						"Huntress (Mastery)",
						"Because the Huntress subclasses contributed so much to make this class " +
								"one-dimensional, they have been _removed_ for the time being.\n" +
								"Instead, " +
								"the Huntress now has access to the _Freerunner_ and _Warlock_ subclasses!\n" +
								"\n[challenge users rejoice now]\n\n" +
								"These subclasses were chosen to further illustrate the contrast between her and the Warrior. " +
								"I realize that this may feel wrong in the beginning, but I think that ultimately these " +
								"subclasses will at the very least put on display the Huntress's natural versatility. " +
								"I may yet reintroduce them at a later date if Evan impresses me with v0.7.1. Alternatively, " +
								"I may simply dream up brand new subclasses for our ranged friend."
				),
				new ChangeButton(
						new ShamanSprite.MM(),
						"Gnoll Shamans",
						"Shamans now have variants! The default variant now shoots magic missiles " +
								"instead of lightning.\n" +
								"_-_ Makes up most shamans\n" +
								"_-_ 3x accuracy on zaps, up from the standard 2x\n" +
								"_-_ zaps do 4-12 damage, up from 4-10"
				),
				new ChangeButton(
						new ShamanSprite.Lightning(),
						"Lightning Shaman",
						"_-_ second most common shamans\n" +
								"_-_ zaps now do 6-12 right off the bat to compensate for new rarity." +
								"_-_ zaps now do +25% bonus damage in water, down from +50%"
				),
				new ChangeButton(
						new ShamanSprite.Firebolt(),
						"Firebolt Shaman",
						"_-_ 1/8 of all shamans\n" +
								"_-_ bolts do 6-12 damage and inflict burning\n" +
								"_-_ bolts will ignite the tile they are targeted at whether or not they hit their target\n" +
								"_-_ Firebolt Shamans resist fire-based attacks and effects.\n" +
								"_-_ You probably shouldn't be letting these shoot at you."
				),
				new ChangeButton(
						new ShamanSprite.Frost(),
						"Frost Shaman",
						"_-_ 1/8 of all shamans\n" +
								"_-_ bolts do 6-10 damage and inflict 1-5 turns of chilling\n" +
								"_-_ They resist appropriate sources.\n" +
								"_-_ Bolts will freeze items if they happen to land on them."
				),
				new ChangeButton(
						new ItemSprite(
								ItemSpriteSheet.ARMOR_LEATHER,
								new Volatility().glowing()
						),
						"Volatility",
						"Inspired from the ideas of MarshalldotEXE\n" +
								"_-_ 5% chance to explode on hit.\n" +
								"_-_ unupgraded armor MAAAY not withstand the explosion ;)"
				),
				new ChangeButton(
						new ItemSprite(new TeleportationBomb().image(), null),
						"Teleportation Bomb",
						"_-_ Made with Bomb + Scroll of Teleportation (5 energy)\n" +
								"_-_ Instead of exploding, teleports everything in a 5x5 radius, including items\n" +
								"_-_ Useful for clearing things from a room; for example, from a piranha room or a trap room."
				)
		);
		addSection("Buffs", false, CharSprite.POSITIVE).addButtons(
				new ChangeButton(
						new Image(Assets.WARRIOR, 0, 90, 12, 15),
						"Berserker",
						"_-_ A recovering Berserker can now gain Rage up to the extent recovered\n" +
								"_-_ Berserk now decays 15% slower\n" +
								"_-_ Damage boost while berserking is now 1.75x\n" +
								"_-_ Being at low health now speeds up Rage building by up to 1/3."
				),
				new ChangeButton(
						new ItemSprite(new Ankh().image()),
						new Ankh().trueName(),
						"More items are now preserved through resurrection! In addition, they can no longer be sold to a shop, " +
								"disintegrated, destroyed by explosions, or stolen by crazy thieves.\n\n" +
								"_-_ Scrolls of Upgrade and Enchantment\n" +
								"_-_ Scrolls of Transmutation and Polymorph\n" +
								"_-_ Potions of Strength and Adrenaline Surge\n" +
								"_-_ Elixir of Might\n" +
								"_-_ Magical Infusion\n" +
								"_-_ Darts\n" +
								"_-_ Ankhs"
				),
				new ChangeButton(
						new ItemSprite(ItemSpriteSheet.RING_AMETHYST, null),
						new RingOfWealth().trueName(),
						"I've always liked the idea of Sprouted Pixel Dungeon, and Rings of Wealth " +
								"were always so underpowered in Shattered..... So I've changed that!\n" +
								"_-_ Drops are now more varied, and can now include equipment, runestones, " +
								"food, dew, and even Stones of Enchantment!\n" +
								"_-_ 20% of the Scrolls and Potions that are dropped as a result of " +
								"a rare drop will now be exotic!\n" +
								"_-_ Passive drop rate boost boosted by ~4.3% (1.15 --> 1.2)"
				),
				new ChangeButton(
						new ItemSprite(
								new Quarterstaff().image(),
								null
						),
						"Quarterstaff",
						"_-_ Quarterstaff's block now scales by +0/+1\n" +
								"_-_ Base block reduced by 1/3 (3 -- > 2)"
				),
				new ChangeButton(
						new ItemSprite( new Knuckles().image() ),
						new Knuckles().trueName(),
						"While no heroes now start with the knuckleduster now, that does not mean it is no longer in the game!\n" +
								"_-_ Transmuting any tier-1 weapon (aside from Mage's Staff) will yield a Knuckleduster.\n" +
								"_-_ Transmuting a Knuckleduster will yield a random non-Knuckleduster tier-1 weapon like normal.\n" +
								"_-_ The Knuckleduster now blocks up to 1 point of damage!"
				),
				new ChangeButton(
						new ItemSprite(ItemSpriteSheet.WAND_TRANSFUSION),
						"Wands",
						"Wand of Transfusion:\n" +
								"_-_ Charm now scales by 2 (was 1)\n" +
								"_-_ Battlemage effect's proc rate boosted by 25% (1/10 --> 1/8)\n" +
								"\nWand of Fireblast:\n" +
								"_-_ When consuming 3 charges at once, now applies both paralysis and cripple"
				),
				new ChangeButton(
						new Image(
								Assets.BUFFS_LARGE,
								80,
								16,
								16,
								16
						),
						"Charm",
						"Charm now only recovers if hit by whoever applied it."
				),
				new ChangeButton(
						new ItemSprite(
								ItemSpriteSheet.SHORTSWORD,
								new Elastic().glowing()
						),
						"Elastic",
						"Elastic can easily be a run-ender if the hero doesn't have any " +
								"alternatives to do damage.\nThis change should make these situations " +
								"less unfair and run-ending:\n" +
								"_-_ Elastic weapons now deal 1/6 damage, instead of 0.\n\n" +
								"With this, bosses should now be beatable with only an Elastic weapon (given enough time)"
				)
		);
		addSection("Changes", false, CharSprite.WARNING).addButtons(
				new ChangeButton(
						new ItemSprite(ItemSpriteSheet.STONE_HOLDER),
						"Stone generation changes",
						"_-_ Runestone generation is now weighted based on its base scroll rarity and its alchemical scroll-stone ratio\n" +
								"_-_ Stones of Augmentation and Enchantment can now drop as rare stones."
				),
				new ChangeButton(
						Icons.get(Icons.DEPTH),
						"Mob Spawn Changes",
						"_-_ Shamans now spawn on floors 11 and 12 (0 --> 1) \n\n" +
								"Rare Mob spawns adjusted:\n" +
								"_-_ MM shamans now spawn on floor 4 (0 --> 0.05)\n" +
								"_-_ MM shaman spawn rate 10x more than normal on floor 6\n" +
								"_-_ Fire Elementals now also spawn on floor 12 (0 --> 0.02)\n" +
								"_-_ Dwarf Warlocks now spawn on floors 13 and 14 (0 --> 0.01) \n" +
								"_-_ Monk spawn rate halved on floor 14 (0.01 --> 0.005) \n" +
								"_-_ Monks now spawn on floor 16 (0 --> 0.2) \n" +
								"_-_ Golems now spawn on floor 17 (0 --> 0.2) \n" +
								"_-_ Succubus now spawn on floor 18 (0 --> 0.02) \n" +
								"_-_ Evil Eyes now spawn on floor 19 (0 --> 0.01) \n"
				),
				new ChangeButton(
						Icons.get(Icons.PREFS),
						"Misc Changes",
						"_-_ There's now a post-halls tier generation table, so crypt rooms in " +
								"floors 22-24 are even less likely to give low tier armor now.\n" +
								"_-_ Adjusted rare mobs. They will now spawn earlier and have a bit more variety.\n" +
								"_-_ Huntress, Journal Pages, and Challenges are now enabled by default.\n" +
								"_-_ Food, Arcane Styli, and Tomes of Mastery can now be quickslotted. (idea credit s0i)\n\n" +
								"_-_ Cursed wands can now spawn Inferno and Blizzard\n\n" +
								"_-_ Weapons that block damage now say how much damage they can block.\n" +
								"_-_ Transmutation and Recycle now have a VFX effect!\n" +
								"_-_ Darts and Shurikens now have a faster throw animation.\n" +
								"_-_ Some descriptions reworded."
				),
				new ChangeButton(
						new Image(
								Assets.SPINNER,
								144,
								0,
								16,
								16
						),
						"Bugfixes",
						"_-_ Attacks by Stunning weapons potentially instantly breaking paralysis"
				),
				new ChangeButton(
						Icons.get(Icons.LANGS),
						"Removed Translations",
						"The ability to play the game in other languages than English has been " +
								"removed for the time being. This mod is not on transifex, and thus has no " +
								"way to obtain new translations for any content changes."
				)
		);

		Component content = list.content();
		content.clear();

		float posY = 0, nextPosY = 0;
		boolean second = false;
		for (ChangeInfo info : infos){
			if (info.major) {
				posY = nextPosY;
				second = false;
				info.setRect(0, posY, panel.innerWidth(), 0);
				content.add(info);
				posY = nextPosY = info.bottom();
			} else {
				if (!second){
					second = true;
					info.setRect(0, posY, panel.innerWidth()/2f, 0);
					content.add(info);
					nextPosY = info.bottom();
				} else {
					second = false;
					info.setRect(panel.innerWidth()/2f, posY, panel.innerWidth()/2f, 0);
					content.add(info);
					nextPosY = Math.max(info.bottom(), nextPosY);
					posY = nextPosY;
				}
			}
		}


		content.setSize( panel.innerWidth(), (int)Math.ceil(posY) );

		list.setRect(
				panel.x + panel.marginLeft(),
				panel.y + panel.marginTop() - 1,
				panel.innerWidth(),
				panel.innerHeight() + 2);
		list.scrollTo(0, 0);

		Archs archs = new Archs();
		archs.setSize( Camera.main.width, Camera.main.height );
		addToBack( archs );

		fadeIn();
	}

	@Override
	protected void onBackPressed() {
		LustrousPixelDungeon.switchNoFade(TitleScene.class);
	}

	private static class ChangeInfo extends Component {

		protected ColorBlock line;

		private RenderedText title;
		private boolean major;

		private RenderedTextMultiline text;

		private ArrayList<ChangeButton> buttons = new ArrayList<>();

		public ChangeInfo( String title, boolean majorTitle, String text){
			super();
			
			if (majorTitle){
				this.title = PixelScene.renderText( title, 9 );
				line = new ColorBlock( 1, 1, 0xFF222222);
				add(line);
			} else {
				this.title = PixelScene.renderText( title, 6 );
				line = new ColorBlock( 1, 1, 0xFF333333);
				add(line);
			}
			major = majorTitle;

			add(this.title);

			if (text != null && !text.equals("")){
				this.text = PixelScene.renderMultiline(text, 6);
				add(this.text);
			}

		}

		public void hardlight( int color ){
			title.hardlight( color );
		}

		public void addButton( ChangeButton button ){
			buttons.add(button);
			add(button);

			button.setSize(16, 16);
			layout();
		}
		public void addButtons( ChangeButton... buttons) {
			for(ChangeButton button : buttons) addButton(button);
		}

		public boolean onClick( float x, float y ){
			for( ChangeButton button : buttons){
				if (button.inside(x, y)){
					button.onClick();
					return true;
				}
			}
			return false;
		}

		@Override
		protected void layout() {
			float posY = this.y + 2;
			if (major) posY += 2;

			title.x = x + (width - title.width()) / 2f;
			title.y = posY;
			PixelScene.align( title );
			posY += title.baseLine() + 2;

			if (text != null) {
				text.maxWidth((int) width());
				text.setPos(x, posY);
				posY += text.height();
			}

			float posX = x;
			float tallest = 0;
			for (ChangeButton change : buttons){

				if (posX + change.width() >= right()){
					posX = x;
					posY += tallest;
					tallest = 0;
				}

				//centers
				if (posX == x){
					float offset = width;
					for (ChangeButton b : buttons){
						offset -= b.width();
						if (offset <= 0){
							offset += b.width();
							break;
						}
					}
					posX += offset / 2f;
				}

				change.setPos(posX, posY);
				posX += change.width();
				if (tallest < change.height()){
					tallest = change.height();
				}
			}
			posY += tallest + 2;

			height = posY - this.y;
			
			if (major) {
				line.size(width(), 1);
				line.x = x;
				line.y = y+2;
			} else if (x == 0){
				line.size(1, height());
				line.x = width;
				line.y = y;
			} else {
				line.size(1, height());
				line.x = x;
				line.y = y;
			}
		}
	}

	//not actually a button, but functions as one.
	private static class ChangeButton extends Component {

		protected Image icon;
		protected String title;
		protected String message;

		public ChangeButton( Image icon, String title, String message){
			super();
			
			this.icon = icon;
			add(this.icon);

			this.title = Messages.titleCase(title);
			this.message = message;

			layout();
		}

		public ChangeButton( Item item, String message ){
			this( new ItemSprite(item), item.name(), message);
		}

		protected void onClick() {
			LustrousPixelDungeon.scene().add(new ChangesWindow(new Image(icon), title, message));
		}

		@Override
		protected void layout() {
			super.layout();

			icon.x = x + (width - icon.width) / 2f;
			icon.y = y + (height - icon.height) / 2f;
			PixelScene.align(icon);
		}
	}
	
	private static class ChangesWindow extends WndTitledMessage {
	
		public ChangesWindow( Image icon, String title, String message ) {
			super( icon, title, message);
			
			add( new TouchArea( chrome ) {
				@Override
				protected void onClick( Touchscreen.Touch touch ) {
					hide();
				}
			} );
			
		}
		
	}
}
