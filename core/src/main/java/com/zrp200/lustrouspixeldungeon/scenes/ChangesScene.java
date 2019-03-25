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
import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Assets;
import com.zrp200.lustrouspixeldungeon.Badges;
import com.zrp200.lustrouspixeldungeon.Chrome;
import com.zrp200.lustrouspixeldungeon.LustrousPixelDungeon;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Bleeding;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Charm;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Terror;
import com.zrp200.lustrouspixeldungeon.actors.buffs.ToxicImbue;
import com.zrp200.lustrouspixeldungeon.actors.hero.HeroClass;
import com.zrp200.lustrouspixeldungeon.actors.mobs.Shielded;
import com.zrp200.lustrouspixeldungeon.actors.mobs.Succubus;
import com.zrp200.lustrouspixeldungeon.actors.mobs.Warlock;
import com.zrp200.lustrouspixeldungeon.effects.BadgeBanner;
import com.zrp200.lustrouspixeldungeon.items.Ankh;
import com.zrp200.lustrouspixeldungeon.items.Generator;
import com.zrp200.lustrouspixeldungeon.items.Item;
import com.zrp200.lustrouspixeldungeon.items.TomeOfMastery;
import com.zrp200.lustrouspixeldungeon.items.armor.LeatherArmor;
import com.zrp200.lustrouspixeldungeon.items.armor.MailArmor;
import com.zrp200.lustrouspixeldungeon.items.armor.PlateArmor;
import com.zrp200.lustrouspixeldungeon.items.armor.ScaleArmor;
import com.zrp200.lustrouspixeldungeon.items.armor.curses.Metabolism;
import com.zrp200.lustrouspixeldungeon.items.armor.curses.Volatility;
import com.zrp200.lustrouspixeldungeon.items.armor.glyphs.HolyProvidence;
import com.zrp200.lustrouspixeldungeon.items.armor.glyphs.Stone;
import com.zrp200.lustrouspixeldungeon.items.bags.MagicalHolster;
import com.zrp200.lustrouspixeldungeon.items.bombs.TeleportationBomb;
import com.zrp200.lustrouspixeldungeon.items.food.Meat;
import com.zrp200.lustrouspixeldungeon.items.rings.RingOfEnergy;
import com.zrp200.lustrouspixeldungeon.items.rings.RingOfForce;
import com.zrp200.lustrouspixeldungeon.items.rings.RingOfFuror;
import com.zrp200.lustrouspixeldungeon.items.rings.RingOfWealth;
import com.zrp200.lustrouspixeldungeon.items.wands.WandOfCorruption;
import com.zrp200.lustrouspixeldungeon.items.wands.WandOfRegrowth;
import com.zrp200.lustrouspixeldungeon.items.weapon.curses.Chaotic;
import com.zrp200.lustrouspixeldungeon.items.weapon.curses.WeaponCurse;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Chilling;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Elastic;
import com.zrp200.lustrouspixeldungeon.items.weapon.melee.Gauntlet;
import com.zrp200.lustrouspixeldungeon.items.weapon.melee.Gloves;
import com.zrp200.lustrouspixeldungeon.items.weapon.melee.Longsword;
import com.zrp200.lustrouspixeldungeon.items.weapon.melee.Quarterstaff;
import com.zrp200.lustrouspixeldungeon.items.weapon.melee.Shortsword;
import com.zrp200.lustrouspixeldungeon.items.weapon.melee.Sword;
import com.zrp200.lustrouspixeldungeon.items.weapon.missiles.Bolas;
import com.zrp200.lustrouspixeldungeon.items.weapon.missiles.Boomerang;
import com.zrp200.lustrouspixeldungeon.items.weapon.missiles.MissileWeapon;
import com.zrp200.lustrouspixeldungeon.items.weapon.missiles.ObsidianKnife;
import com.zrp200.lustrouspixeldungeon.items.weapon.missiles.Shuriken;
import com.zrp200.lustrouspixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.zrp200.lustrouspixeldungeon.levels.traps.InfernalTrap;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.plants.Earthroot;
import com.zrp200.lustrouspixeldungeon.plants.Starflower;
import com.zrp200.lustrouspixeldungeon.sprites.CharSprite;
import com.zrp200.lustrouspixeldungeon.sprites.ElementalSprite;
import com.zrp200.lustrouspixeldungeon.sprites.GnollTricksterSprite;
import com.zrp200.lustrouspixeldungeon.sprites.HeroSprite;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSprite;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSpriteSheet;
import com.zrp200.lustrouspixeldungeon.sprites.KingSprite;
import com.zrp200.lustrouspixeldungeon.sprites.ShamanSprite;
import com.zrp200.lustrouspixeldungeon.sprites.ShieldedSprite;
import com.zrp200.lustrouspixeldungeon.sprites.ShopkeeperSprite;
import com.zrp200.lustrouspixeldungeon.sprites.StatueSprite;
import com.zrp200.lustrouspixeldungeon.sprites.SuccubusSprite;
import com.zrp200.lustrouspixeldungeon.sprites.TenguSprite;
import com.zrp200.lustrouspixeldungeon.sprites.WarlockSprite;
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

import static com.zrp200.lustrouspixeldungeon.ui.Icons.CHALLENGE_ON;
import static com.zrp200.lustrouspixeldungeon.ui.Icons.DEPTH;
import static com.zrp200.lustrouspixeldungeon.ui.Icons.LANGS;
import static com.zrp200.lustrouspixeldungeon.ui.Icons.PREFS;
import static com.zrp200.lustrouspixeldungeon.ui.Icons.get;

//TODO: update this class with relevant info as new versions come out.
public class ChangesScene extends PixelScene {

	public enum Milestone {
		LUST010 ("Lustrous v0.1.0",		 2,14,2019),
		LUST001 ("Lustrous v0.0.1",		12,20,2018),
		LUST000b("Lustrous v0.0.0b",	12, 6,2018),
		LUST000a("Lustrous v0.0.0a",  	12, 4,2018),
		LUST000 ("Lustrous v0.0.0",		12, 1,2018),

		SHPD071d("Shattered v0.7.1d",	 1,18,2019),
		SHPD071b("Shattered v0.7.1b", 	12,30,2018),
		SHPD071 ("Shattered v0.7.1",	12,18,2018),
		SHPD070 ("Shattered v0.7.0",	10,18,2018);

		private String name;
		private Date releaseDate;

		Milestone(String name, int releaseMonth, int releaseDay, int releaseYear) {
			GregorianCalendar calender = new GregorianCalendar();
			calender.set(releaseYear,releaseMonth-1,releaseDay);

			this.name = name;
			this.releaseDate = calender.getTime();
		}
	}

	private static ChangeButton addDeveloperCommentary(Image devIcon, Milestone release, String commentary,Milestone...eventsToCompare) {
		StringBuilder message = new StringBuilder();
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);

		if(release != null) { // compare release date to milestones if possible
			message.append( "_-_ Released on " )
					.append(  dateFormat.format( release.releaseDate )  )
					.append("\n");

			if ( eventsToCompare != null ) for ( Milestone event : eventsToCompare ) {
				message.append("_-_ ");

				// convert milliseconds to days
				long daysSinceEvent = (release.releaseDate.getTime() - event.releaseDate.getTime()) / 86400000;

				message.append(daysSinceEvent)
						.append(" day")
						.append(daysSinceEvent != 1 ? "s" : "") // "1 days" makes no sense.
						.append(" after ").append(event.name).append("\n");
			}
			message.append("\n");
		}
		message.append(commentary == null ? "Dev commentary will be added here in the future." : commentary); // add commentary
		return new ChangeButton( devIcon,"Developer Commentary", message.toString() );
	}
	private static ChangeButton addDeveloperCommentary(Milestone release, String commentary,Milestone...eventsToCompare) {
		return addDeveloperCommentary(new Image(Assets.ZRP200),release,commentary,eventsToCompare);
	}

	private static final ArrayList<ChangeInfo> infos = new ArrayList<ChangeInfo>();

	private void add010Changes() throws IllegalAccessException {
		MissileWeapon enchantedMissile;
		do {
			try {
				enchantedMissile = (MissileWeapon)Random.oneOf(Random.oneOf(Generator.misTiers).classes).newInstance();
			} catch (InstantiationException invalid) { enchantedMissile = TippedDart.randomTipped(); } // keep trying until we get something that works
		} while(enchantedMissile instanceof Boomerang || enchantedMissile instanceof ObsidianKnife || enchantedMissile == null);
		//noinspection unchecked
		enchantedMissile.enchantment = MissileWeapon.Enchantment.random();
		enchantedMissile.enchantKnown = true;

		new ChangeInfo("v0.1.0",true);
		new ChangeInfo("v0.1.0b",false).addButtons(
				addDeveloperCommentary(null,"I've been made aware of a major bug " +
						"that prevents the player from entering a floor by any means, but am unable " +
						"to reproduce it despite everything. I'd love to fix it... If you get it and " +
						"can reproduce it consistently, please let me know ASAP so I can fix it once " +
						"and for all.\n\nOn a lighter note, I've put in the code for renaming weapons, " +
						"and it'll take effect once I release v0.1.1 within the next week or two."),
				new ChangeButton(new ItemSprite(new Shortsword().image, WeaponCurse.GLOWING),"Chaotic").appendList(
						"Volatility now procs twice as often when rolled, but will not destroy armor.",
						"Volatility will not activate at the attacker's position if the attacker is using a missile weapon.",
						"Stench and corrosion now always proc at the enemy's position.",
						"Chaotic can now roll Holy Providence... for the enemy. Have fun!",
						"Metabolism will now always drain hunger regardless of whether it procced for you.",
						"Effects that would normally scale now generate lower random level to pretend to be, now up to +6 (previous max was +19).",
						"Fragile can no longer be rolled; will be replaced with, uh, a better curse later."),
				ChangeButton.misc(
						"You can now enchant two missile weapons at a time if they are in the same stack.",
						"Cord is now guaranteed to be the result of transmuting a tier-1 weapon.",
						"Cord can now be passed through remains.",
						"Exhausting can now proc for mirror images.",
						"Forest Fires are much more.... destructive. ;)",
						"Fire can now travel on regrowth blobs.",
						"Magic Missile wands now gain a phantom charge with each upgrade.",
						"Adjusted burning internally, no behavior changes intended but please let me " +
								"know if you notice a difference."
				),
				ChangeButton.bugfix(
						"Things getting destroyed when they should be immune to getting destroyed",
						"Infernal Trap being the wrong color (thanks color-blindness)",
						"Odd behavior for elastic boomerang",
						"Mirror Images and sad ghost being able to surprise attack when they shouldn't be able to",
						"Holy Providence having a 0% proc rate until +3, then an abnormally low proc rate",
						"'Frozen' and 'Chilled' message displaying for elementals",
						"Charm vfx playing against immune enemies",
						"Grim proccing against immune enemies",
						"Attacking an enemy directly after loading the game is always a surprise attack",
						"Crashes with sad ghost",
						"Tome of Mastery spawning every load for those who still have the old subclasses",
						"Some text mistakes"));
		new ChangeInfo("v0.1.0a",false).addButtons(
				new ChangeButton(new ShamanSprite.MM(), "Magic Missile Shamans").appendList(
						"MM shaman now does 4-10, down from 4-12",
						"MM shamans now drop wands of magic missile with a 1.65% chance.",
						"MM shaman spawnrate reduced by about 7% (50% --> ~46%)",
						"MM shaman now less likely to spawn on floor 4"),
				new ChangeButton(new ShamanSprite.Lightning(),"Lightning Shamans").appendList(
						"Zaps now do 4-12 damage (down from 6-12)",
						"Zaps get a +50% boost against targets in water, up from +25%",
						"Can now drop wands of lightning with a 1.65% chance",
						"Lightning shaman spawnrate boosted by about 11% (30% --> 33%)."
				),
				new ChangeButton(new ShamanSprite.Firebolt(),"Firebolt Shamans").appendList("Firebolt shaman spawnrate reduced by about 23% (10% -> 7.6%).",
						"Firebolts now do 4-12 damage, down from 6-12",
						"Now drops wands of fireblast with a 1.65% chance",
						"Now also drop potions of liquid flame.",
						"Fire-based resistances now consistent with burning fist and fire elemental"),
				new ChangeButton(new ShamanSprite.Frost(),"Frost Shamans")
						.appendList(
								"Frost shamans now inflict 1-2 turns of frost every zap, stacking up to 6 turns of chill.",
								"Frost shamans can now drop potions of frost.",
								"Frost shamans can now drop wands of frost with a 1.65% chance.",
								"Frost shaman spawnrate boosted by 53% (10% --> 15.3%)"),
				ChangeButton.misc(
						"Boomerangs now move a minimum of two tiles per turn when returning, up from 1.5",
						"Bleeding grey is now based on target's current hp, rather than hero's total hp",
						"Scorpios and gnoll tricksters now suffer an accuracy penalty when melee attacking."
				),
				ChangeButton.bugfix(
						"Rogue not starting with a bag",
						"Attempting to upgrade toolkit causing crashes",
                        "Ring-related crashes.",
						"Blessing ankhs causing crashes",
						"Activating earthroot with an item causing crashes",
						"Enhanced missile weapons not properly seperating from base stack",
						"Chargrilled meat having an incorrect name.",
						"Mystery meat being worth more than it should.",
						"Darts not being considered unique.",
						"!!!NO TEXT FOUND!!! being found when inspecting blobs of regrowth."),
                new ChangeButton(Icons.get(Icons.INFO),"Changelog omissions","Amended v0.1.0 changelog to include another new mechanic I forgot to mention and also fixed some formatting issues.")
		);
		new ChangeInfo(ChangeInfo.Template.NEW_CONTENT).addButtons(
				addDeveloperCommentary(
						Milestone.LUST010,
						Messages.get(WelcomeScene.class,"update_msg"),
						Milestone.SHPD071d,
						Milestone.LUST001,
						Milestone.SHPD071
				),
				new ChangeButton(new InfernalTrap().getImage(),
						"Traps",
						"Added two new traps: the Infernal and Blizzard traps!\n").appendList(
								"Mirror infernal and blizzard brews, respectively",
								"Scaling is identical to toxic gas trap's","They appear starting in caves."),
				new ChangeButton(new Image(Assets.SHPD),"Implemented Shattered v0.7.1")
						.appendList(
								"Huntress implemented; in-game runs will gain a spirit bow and have access to 0.7.1 subclasses",
								"Stone glyph implemented.",
								"Upgradable missile weapons implemented",
								"New blocking weapon descriptions not implemented",
								"Teleportation trap change not implemented"),
				new ChangeButton(new Boomerang(), "The boomerang returns as a tier-3 missile weapon!\n")
							.appendList(
									"4-10 damage @ +2/+2 scaling.",
									"8 durability at base.\n",
									"Attempts to return to the position it was thrown from over the course of a few turns.\n",
									"Moves 1.5 tiles per turn when returning, but will speed up if " +
											"necessary to return to its original position within three turns.\n",
									"Can hit enemies while returning, does not benefit from subclass perks or missile weapon-specific accuracy modifiers while doing so.\n",
									"Can be caught early by intercepting its flight path.\n",
									"Original Idea Credit: _00-Evan_"),
					new ChangeButton(new ObsidianKnife()).appendList(
									"Tier-4 missile weapon",
									"6-18 damage @ +2/+4 scaling",
									"When surprise attacking, deals at least 50% of max-min to max (this is identical to Assassin's Blade mechanics).",
									"5 durability at base.\n",
									"Sprite Credit: _ConsideredHamster_")
							.appendLine("\nBoth this and the boomerang drop more rarely than their counterparts (1/8 of their respective tiers' drops), and are also worth slightly more."),
				new ChangeButton(
						enchantedMissile.sprite(), "Enchantable Missile Weapons!")
						.appendLine("Missile Weapons can now be enchanted! ")
						.append("Enchanting a missile weapon will enchant ONE missile of that stack, ")
						.append("boosting durability by 50% as well as adding an enchantment."),
				new ChangeButton(
						new ItemSprite(ItemSpriteSheet.MISSILE_HOLDER),"Darts").appendList(
						"can now be untipped via alchemy with no additional costs\n",
						"can now be upgraded, boosting their tipped durability in addition to their regular damage.\n",
						"can be enchanted just like other weapons, but this will not increase their durability.\n",
						"once again dropped by remains."),
                new ChangeButton(new ItemSprite(ItemSpriteSheet.RING_TOPAZ),new RingOfEnergy().trueName()).appendList(
                        "Reduced wand charge multiplier (+25% -> +22.5%)",
                        "Now also boosts artifact recharge rate."
                )
        );
		new ChangeInfo(ChangeInfo.Template.BUFFS).addButtons(
				new ChangeButton(
						new ItemSprite( new PlateArmor().inscribe( new HolyProvidence() ) ),
						"Glyphs and Enchantments")
							.append("_Holy Providence_")
							.appendList(
									"Proc Rate is now (2+level)/(40+level).",
									"Successful procs will bestow one of Bless (6-10 turns), Adrenaline (6-8 turns), " +
											"or Frost Imbue (6-12 turns) upon the wearer with equal chances for each.",
									"Durations are uniformly distributed")
							.appendLine("\n_Eldritch_").appendList(
									"now applies more terror (now 15 base + 2.5*level) and can stack it like bleeding.",
									"vertigo base duration boosted by 2.")
							.appendLine("\n_Chaotic_")
							.appendList(
									"Displacing and Displacement now occupy the same curse 'slot'.",
									"Viscosity now incorporates armor properly"),
				new ChangeButton(
						new ItemSprite( new TomeOfMastery() ),
						"Subclasses")
							.append("_Sniper_")
							.appendList(
									"Now always ignores armor when using missile weapons, even at point-blank range.",
									"Sniper Shot always surprise attacks (visual change).")
							.appendLine("\n_Gladiator_")
							.appendList("Each hit of fury now rolls twice.",
									"The higher of the two will be applied to the target.")
							.appendLine("\n_Assassin_")
							.appendList("Blinking now considers the reach of the user's weapon."),
				new ChangeButton(new ShopkeeperSprite(),"Runestone Prices").appendList(
				        "Augmentation now sells for 25g (down from 30g)",
                        "Blast now sells for 20g (up from 10g)",
                        "Affection and aggression now sell for 12.5g (rounded to 13g usually, up from 10g).",
                        "Deepened sleep and stones of clairvoyance now sell for 13.33g (rounded to 13g usually, up from 10g)",
                        "Detect curse and shock now sell for 15g (up from 10g)."
                ),
				new ChangeButton(new Meat.PlaceHolder()).appendList(
						"Frozen carpaccio and chargrilled meat now restore 200 points of satiety, up from 150.\n",
						"Stewed meat is now worth 6g, up from 5g.",
						"Stewed meat can now be frozen and burned."
				),
				new ChangeButton(new ElementalSprite(), "Mobs").appendList(
						"Bosses are now immune to amok.", "Elementals now resist bleeding.",
						"Tengu now resists burning.", "Fetid rat now considered acidic",
						"Rot heart now always produces gas when damaged."
				),
				new ChangeButton(
						new ToxicImbue().getLargeIcon(), "Buffs")
							.append("_Toxic Imbue_")
							.appendList(
									"Less toxic gas is produced the closer the buff is to expiring.",
									"Characters imbued with toxicity now resist Corrosion and Caustic Ooze.")
							.appendLine()
							.appendLine("_Bleeding, Caustic Ooze_").appendList(
									"Now can stack, albeit inconsistently.",
									"They can add up to 2/3 of their intended duration onto durations."),
				new ChangeButton(
						new ItemSprite(ItemSpriteSheet.SCROLL_YNGVI),
						"Scroll of Transmutation").appendList(
								"Thrown weapons can now be transmuted into another of the same tier, albeit one at a time.",
								"Darts cannot be transmuted in this update",
								"Goo blobs and cursed metal shards can be transmuted into each other",
								"Scrolls of transmutation are no longer considered unique"),
				ChangeButton.misc(
						"Tenacity general effectiveness boosted by 6.25% (0.85 --> 0.8)",
						"Teleport traps (and bombs) now clear blobs in their teleport radii",
						"Electricity now recharges wands in inventory passively (identical to recharging)",
						"Cursed Wand effect \"Shock and Recharge\" now uses storm trap effect rather than shocking trap.",
						"Warden now gets regular effect from earthroot in addition to unique one.",
						"Earthroot now roots enemies for 5 turns when trampled.",
						"Rot darts now are affected by durability boosts, albeit ~73% less effective than normal"
				)
		);
		new ChangeInfo(ChangeInfo.Template.CHANGES).addButtons(
		        new ChangeButton(
						HeroSprite.avatar(HeroClass.HUNTRESS,0),
						"Huntress",
						"_-_ Starts with v0.7.1 studded gloves instead of cord.",
						"_-_ Starts with v0.7.1 bow instead of darts\n",
						"_-_ a buffed cord (+1/+1 -> +1/+2) can be obtained via transmuting a tier-1 weapon"
				),
				new ChangeButton(get(DEPTH),"Room Generation")
						.append("_General_").appendList(
								"Items cannot spawn on traps.",
								"Tombs are now much less likely to give gold")
						.appendLine("\n_Secret Larder Rooms_")
						.appendList(
								"Contents of the room are much more random, but overall has the same amount of food.",
								"Frozen Carpaccio and Rations can now be found in the room.")
						.appendLine("\n_Secret Maze Room_").appendList("Prize is now visibly uncursed")
						.appendLine("\n_Pixel Mart_")
						.appendList(
								"All upgradable items are now identified.",
								"Now sells a greater variety of weapons, both thrown and melee."),
				new ChangeButton(get(CHALLENGE_ON),"Challenges").appendList(
						"Blocked items are more likely to be replaced by valid items",
						"Secret Larder Rooms no longer spawn for On Diet",
						"Crypt Rooms no longer spawn for Faith is My Armor",
						"Gardens no longer spawn for Barren Land",
						"Pixel Mart now sells torches for Into Darkness"
				),
				ChangeButton.misc(
						"Throwing darts with an unidentified crossbow equipped will now count towards identifying it.",
						"Throwing items manually should now 'trickshot' as if thrown from a quickslot",
						"Guards and Skeletons now drop visually unupgraded equipment",
						"Enemies that drop potions of healing no longer drop them over chasms",
						"Fly splitting mechanics adjusted to properly account for lucky",
						"Reduced frost shaman spawnrate by half"),
				ChangeButton.bugfix(
						"Crash bugs with wraiths",
						"Trickster combo not getting reset when melee attacking.",
						"Crashes when teleportation bomb is activated early (eg by liquid flame).",
						"Position of chests teleported by teleportation bomb revealed to player immediately.",
						"Issues with ring of wealth drops.",
						"Glyphs of affection and thorns not scaling properly with upgrades.",
						"MM shamans stopping game execution.",
						"Shopkeeper not dropping bags (not retroactive unfort).",
						"Swarm intelligence messing with terror.",
						"Armor enchants showing their vfx when forcefully identified (eg by well or scroll).",
						"Incendiary darts instantly losing their tips when thrown on a flammable tile.",
						"Ankhs not working as I intended; they are now stackable."
				),
				new ChangeButton(
						get(LANGS),
						"Text Adjustments",
						"",
						"_-_ Ring of Tenacity description now includes current damage reduction",
						"_-_ Some enemy/weapon descriptions",
						"_-_ Alchemist's Toolkit no longer gives hint at +10"
				)
		);
		new ChangeInfo(ChangeInfo.Template.NERFS).addButtons(
				new ChangeButton(new KingSprite(), "Mob Resistances")
						.append("_Dwarf King_")
						.appendList(
								"Burning and Toxic Gas are now 0.75x effective (up from 0.5x)",
								"Paralysis and Blindness are now 0.25x effective (down from 0.5x)",
								"Vertigo is now 0.25x effective (up from 0x)",
								"Now resists slow")
						.appendLine("\n_Gnoll Brutes_").appendList(
								"Terror 0.25x effective (up from 0x)",
								"Terror is 0x effective when enraged",
								"Amok is 1.25x effective (up from 1x)")
						.appendLine("\n_Evil Eyes_").appendList(
							"Wand of Disintegration now 0.75x effective",
							"Terror is 0.33x effective against a non-charging eye",
							"Terror interaction with deathgaze unchanged.")
						.appendLine("\n_Monks, Golems_")
						.appendList(
								"Terror and amok immunities changed to resistances.",
								"Golems have a 75% resistance to amok."),
				new ChangeButton(
						new MagicalHolster(), "Now that missile weapons can be upgraded and thus are more viable, " +
						"the 0.0.0a holster buff is a bit over the top (especially for huntress)\n",
						"_-_ Holster durability boost reduced (1.33 -> 1.2)\n",
						"Speaking of, the huntress once again starts with the holster."
				)
		);
	}
	private void add002Changes() {
		new ChangeInfo("v0.0.2-BETA",true);
		new ChangeInfo("BETA-4",false).addButtons(
//		new ChangeInfo("BETA-3",false).addButtons(
//				new ChangeButton( get(SHPX), "Implemented Shattered 0.7.1d","Will release shattered changelog later."),
//				ChangeButton.bugfix(
//						"Invincible Swarms","All attacks by hero ignoring evasion of enemy","Anti-magic not working properly with sad ghost"
//				))
		);
		new ChangeInfo("BETA-2",false).addButtons(
				// new content
				new ChangeButton( new TenguSprite(), "Implemented 0.7.1c")
					.appendList(
							"Boss mechanics implemented... across ALL bosses.",
							"Mage buffs implemented"),
				new ChangeButton( new ItemSprite(ItemSpriteSheet.RING_TOPAZ), "Ring of Energy",
						"Reduced wand charge boost slightly (+25% --> 22.5%) and added an "
								+ "artifact charge boost (+9% base). This should make the ring a bit "
								+ "more interesting to use."),
				new ChangeButton( new Bleeding(), "Bleeding can now stack, albeit rather inconsistently." ),
				ChangeButton.misc(
						"Flies now split more logically (aka lucky interacts properly now)",
						"Ring of Tenacity now gives both numbers.",
						"Graves are now half as likely to give gold."),
				ChangeButton.bugfix(
						"Holy furor not applying its buff properly.",
						"Pickaxe description containing !!!NO TEXT FOUND!!!",
						"Incendiary Darts untipping when used on flammable terrain",
						"Ankhs not working as I intended.",
						"Dewdrops not stacking (wealth issue)",
						"Rotberry warden effect overriding a current adrenaline surge",
						"Visual display issue with about scene. Added an extra message for landscape.")
		);
		new ChangeInfo("BETA-1",false).addButtons(
				// new content
				new ChangeButton(get(DEPTH),"Room Generation")
						.append("_Secret Larder Rooms:_")
						.appendList(
							"Contents of the room are much more random, but overall has the same amount of food.",
							"Frozen Carpaccio and Rations can now be found in the room.")
						.appendLine("\n_Secret Maze Room:_").appendList("Prize is now visibly uncursed")
						.appendLine("\n_Pixel Mart:_")
						.appendList(
							"All upgradable are now identified",
							"Now sells a greater variety of weapons, both thrown and melee"),
				// buffs
				new ChangeButton(new InfernalTrap().getImage(),"Infernal and Blizzard Traps").appendList(
						"Blob amounts are now identical to that of the toxic gas trap."
				),
				// nerfs
				new ChangeButton( new ObsidianKnife() ).appendList(
						"now deals 6-18 @ +2/+3 damage instead of 8-16 @ +2/+4 damage\n",
						"min-to-max surprise % is now 50%, down from 67%\n",
						"lowered spawn rate by 43% (2/9 -> 1/8 of t4s)"
				),
				new ChangeButton(new ItemSprite(new Longsword().enchant(new Chilling())),"Chilling",
						"Reverted previous changes and added new behavior:\n",
						"_-_ A proc will extend the current chill duration by 1-2 turns or set the "
								+ "chill duration to a random number between 2 and 3 depending on which "
								+ "action would result in a higher stack of chill."
				),
				// changes
				ChangeButton.misc(
						"One scroll of transmutation is now guaranteed to spawn at some point.\n",
						"Warden sorrowmoss effect now stacks\n",
						"Sniper now always pierces armor with thrown weapons regardless of distance.\n",
						"Changed way missile weapons' descriptions are handled, and changed their content too."
				),
				ChangeButton.bugfix(
						"Position of Chests teleported by teleportation bomb revealed to player immediately",
						"Ring of Accuracy's description is now fixed.",
						"Curse removal revealing the existance of a curse enchant if hidden.",
						"Weapon curses getting revealed briefly on pickup.",
						"Skeletons' and Thieves' spawnrates being 10x the expected rates on floor 4",
						"Crash bugs"
				)
		);
		new ChangeInfo(ChangeInfo.Template.NEW_CONTENT).addButtons(
				addDeveloperCommentary(
						null,
						"Well, here it is: my implementation of 0.7.1. I've implemented basically all of it, " +
								"with exceptions of course.\n\n" +
								"In this update, I've added two new traps to the game: the Infernal and Blizzard traps. " +
								"They mirror the Infernal and Blizzard brews, respectively. In addition, I've added a new missile weapon as " +
								"well as a way to untip darts via alchemy. See the changelog to see the various minor adjustments I've also made.",
						Milestone.LUST001
				),
				new ChangeButton(new InfernalTrap().getImage(),
						"Traps",
						"Added two new traps: the Infernal and Blizzard traps!\n",
						"_-_ Mirror Infernal and Blizzard brews, respectively",
						"_-_ They appear starting in caves."
				),
				new ChangeButton(
						new ObsidianKnife(), "Added a new t4 missile weapon!",
						"_-_ 8-16 damage with +2/+4 scaling",
						"_-_ When surprise attacking, deals 67% to max.",
						"_-_ 5 durability at base."
				),
				new ChangeButton(
						new ItemSprite(ItemSpriteSheet.MISSILE_HOLDER),"Darts",
						"Darts can now be untipped via alchemy!",
						"\nIn addition, they can once again be dropped by hero's remains."
				)
		);
		new ChangeInfo(ChangeInfo.Template.BUFFS).addButtons(
				new ChangeButton(
						HeroSprite.avatar(HeroClass.WARRIOR,6),
						"Gladiator",
						"Each hit of fury now rolls twice. The higher roll will be applied to the target."
				),
				new ChangeButton(
						new TenguSprite(), "Tengu",
						"Tengu is now immune to terror. Inflicting it will cause Tengu to teleport."
				),
				new ChangeButton(
						new Earthroot.Seed(),
						"_-_ Now roots enemies for 5 turns",
						"_-_ Wardens get both their unique effects as well as the standard Herbal Armor."
				),
				new ChangeButton(
						new Starflower.Seed(),
						"Wardens get 10 turns adrenaline instead of 30 turns of recharging."
				)
		);
		new ChangeInfo(ChangeInfo.Template.CHANGES).addButtons(
				new ChangeButton(
						HeroSprite.avatar(HeroClass.HUNTRESS,0),
						"Huntress",
						"_-_ Starts with v0.7.1 studded gloves instead of cord.",
						"_-_ Starts with v0.7.1 bow instead of darts\n",
						"_-_ a buffed cord (+1/+1 -> +1/+2) can be obtained via transmuting a tier-1 weapon"
				),
				new ChangeButton(
						new ItemSprite( new PlateArmor().inscribe( new HolyProvidence() ) ),
						"Holy Providence",
						"_-_ Proc Rate is now a static (2+level)/(45+level)",
						"_-_ Successful procs will bestow one of Bless (6-10 turns), Adrenaline (6-8 turns), " +
								"or Haste (6-12 turns) upon the wearer with equal chances for each.",
						"_-_ Durations are uniformly distributed"
				),
				new ChangeButton(
						new ToxicImbue(),
						"_-_ Less toxic gas is produced the closer the buff is to expiring.",
						"_-_ Characters imbued with toxicity now resist Corrosion and Caustic Ooze."),
				ChangeButton.bugfix(
						"Crash bugs with wraiths",
						"Ring of Wealth failing to drop things when it should",
						"Affection and thorns not scaling properly with upgrades"
				),
				new ChangeButton(
						get(LANGS),
						"Text Adjustments",
						"Changed how some things are worded, including:\n",
						"_-_ Ring of Tenacity description",
						"_-_ Some enemy/weapon descriptions"
				)
		);
		new ChangeInfo(ChangeInfo.Template.NERFS).addButtons(
				new ChangeButton(
						new MagicalHolster(), "Now that missile weapons can be upgraded and thus are more viable, " +
						"the 0.0.0a holster buff is a bit over the top (especially for huntress)\n",
						"_-_ Holster durability boost reduced (1.33 -> 1.2)\n",
						"Speaking of, the huntress once again starts with the holster."
				)
		);
	}
	private void add071Changes() {
		new ChangeInfo("Implemented v0.7.1", true,Window.SHPX_COLOR);
		new ChangeInfo("v0.7.1c & v0.7.1d",false).addButtons(
				new ChangeButton( HeroSprite.avatar(HeroClass.MAGE,6), "Warlock").appendList(
						"Fixed the previous change to walock's soul mark making the effect MUCH more common than intended. The base chance buff was intended to be 10% to 15%, but ended up being closer to 80%, oops!\n")
						.appendLine("Despite the unintended change, the warlock was not massively overpowered, which tells me I have more power budget in soul mark than I thought:\n")
						.appendList("Soul mark hunger restoration increased by 100%","Soul mark health restoration increased by 33%"),
				new ChangeButton( new KingSprite(), "Dwarf King", "While I would like to make more extensive changes to Dwarf King in the future, I've made a couple smaller tweaks for now to make him harder to cheese:\n")
						.appendList("Dwarf King is now able to summon skeletons even if he cannot see the hero","Dwarf King is now resistant to fire and toxic gas")
						.appendLine("\nNote that in 0.7.1d I've fixed a bug where Dwarf King was attacking in several cases where he should have gone to try and summon. This was an unintended change to his behaviour, and now he should behave more like how he was before 0.7.1c."),
				ChangeButton.bugfix(
						"Recycle being able to produce health potions with pharmacophobia enabled",
						"Magical porter soft-locking the game in rare cases",
						"Mystical Energy not recharging artifacts correctly in some cases"));

		new ChangeInfo("v0.7.1a & v0.7.1b", false).addButtons(
				new ChangeButton(HeroSprite.avatar(HeroClass.HUNTRESS,6), "Hero Balance Changes", "After pouring over some analytics numbers, I have decided to give out some hero buffs primarily focused on base power, and one nerf based on power in lategame:\n")
					.appendLine("Huntress:")
					.appendList(
							"Gloves base damage up to 1-6 from 1-5",
							"Spirit bow damage scaling up 20%\n(now gets exactly +1/+2 dmg every level up)",
							"Warden barkskin increased by 5 points\n")
					.appendLines(
							"Rogue's cloak of shadows base charge speed increased by ~11%, scaling reduced to compensate.\n",
							"Warlock's soul mark base chance increased to 15% from 10%, scaling reduced to compensate.\n",
							"Warrior's shielding regen scaling reduced by ~15%. This is primarily a lategame nerf."),
				new ChangeButton( new ItemSprite(ItemSpriteSheet.RING_DIAMOND, null), "Other Balance Changes",
				"Similarly to heroes, I have gone over the balance of items, and am making several buffs + one nerf.\n\n" +
						"wand of fireblast buffed:\n" +
						"_-_ shot distance at 3 charges reduced by 1\n" +
						"_-_ damage at 1 charge reduced slightly\n" +
						"_-_ damage at 2/3 charges increased by ~15%\n" +
						"\n" +
						"_-_ vorpal enchant bleed reduced by 20%\n" +
						"_-_ glyph of potential wand charge bonus increased by 20%\n" +
						"_-_ glyph of stone evasion conversion efficiency increased to 75% from 60%\n" +
						"\n" +
						"_-_ ring of elements power increased to 16% from 12.5%\n" +
						"_-_ ring of energy charge speed increased to 25% from 20%\n" +
						"_-_ ring of wealth 'luck' bonus increased to 20% from 15%"),
				ChangeButton.bugfix(
						"various crash bugs",
						"rare crashes involving alchemy",
						"health potion limits not applying to prison guards",
						"traps with ground-based effects affecting flying characters"));
		new ChangeInfo(ChangeInfo.Template.NEW_CONTENT).addButtons(
				addDeveloperCommentary( Icons.get(Icons.SHPX), Milestone.SHPD071,
						"This update overhauls the huntress! She has a new signature item " +
								"(a bow!), as well as significantly reworked subclass abilities.\n\n" +
								"Thrown weapons can now be upgraded, and there are a variety of other " +
								"smaller tweaks and improvements as well.",
						Milestone.SHPD070),
				new ChangeButton( HeroSprite.avatar(HeroClass.HUNTRESS,0), "Huntress Reworked!",
				"The Huntress has received a class overhaul!\n\n" +
						"Her boomerang has been replaced with a bow. The bow has infinite uses, like the boomerang, but cannot be upgraded directly, instead it will grow stronger as the huntress levels up.\n\n" +
						"Her knuckledusters have been replaced with studded gloves. This change is purely cosmetic.\n\n" +
						"Those with runs in progress will have their boomerang turn into a bow, and will regain most of the scrolls of upgrade spent on the boomerang.\n\n" +
						"The huntress can now also move through grass without trampling it (she 'furrows' it instead)."),
				new ChangeButton( HeroSprite.avatar(HeroClass.HUNTRESS,6), "Huntress Subclasses Reworked!",
				"Huntress subclasses have also received overhauls:\n\n" +
						"The Sniper can now see 50% further, penetrates armor with ranged attacks, and can perform a special attack with her bow.\n\n" +
						"The Warden can now see through grass and gains a variety of bonuses to plant interaction."),
				new ChangeButton( new ItemSprite(ItemSpriteSheet.TRIDENT, null), "Thrown Weapon Improvements",
				"Thrown weapons now show their tier, ranging from 1-5 like with melee weapons.\n\n" +
						"All Heroes now benefit from excess strength on thrown weapons.\n\n" +
						"Thrown weapons now get +50% accuracy when used at range.\n\n" +
						"Thrown weapons can now be upgraded!\n" +
						"_-_ Upgrades work on a single thrown weapon\n" +
						"_-_ Increases damage based on tier\n" +
						"_-_ Gives 3x durability each upgrade\n" +
						"_-_ Weapons with 100+ uses now last forever\n" +
						"_-_ Darts are not upgradeable, but tipped darts can get extra durability\n\n" +
						"Ring of sharpshooting has been slightly adjusted to tie into this new upgrade system."));

		new ChangeInfo(ChangeInfo.Template.CHANGES).addButtons(
				new ChangeButton(BadgeBanner.image(Badges.Badge.UNLOCK_MAGE.image), "Hero Class changes",
				"All heroes except the warrior now need to be unlocked via new badges. The requirements are quite simple, with the goal of giving new players some early goals. Players who have already unlocked characters will not need to re-unlock them.\n\n" +
						"To help accelerate item identification for alchemy, all heroes now start with 3 identified items: The scroll of identify, a potion, and another scroll."),
				new ChangeButton(Icons.get(Icons.PREFS), Messages.get(this, "misc"),
				"Added a partial turn indicator to the game interface, which occupies the same spot as the busy icon. This should make it much easier to plan actions that take more or less than 1 turn.\n\n" +
						"Rings now have better descriptions for their stats! All rings now show exactly how they affect you in a similar way to how other equipment gives direct stats.\n\n" +
						"Precise descriptions have been added for weapons which block damage.\n\n" +
						"Added item stats to the item catalog.\n\n" +
						"Dropping an item now takes 1 turn, up from 0.5 turns."),
				new ChangeButton(new Image(Assets.SPINNER, 144, 0, 16, 16), Messages.get(this, "bugfixes"),
				"Fixed:\n" +
						"_-_ various minor visual bugs\n" +
						"_-_ odd behaviour when transmuting certain items\n" +
						"_-_ keys rarely spawning without chests\n" +
						"_-_ fireblast rarely damaging things it shouldn't\n" +
						"_-_ dew drops from dew catchers landing on stairs\n" +
						"_-_ ankh revive window rarely closing when it shouldn't\n" +
						"_-_ flock and summoning traps creating harsh multi-sound effects\n" +
						"_-_ thrown weapons being lost when used on sheep\n" +
						"_-_ various specific errors when actions took more/less than 1 turn\n" +
						"_-_ various freeze bugs caused by Tengu"));
		new ChangeInfo(ChangeInfo.Template.BUFFS).addButtons(
				new ChangeButton( new ItemSprite(ItemSpriteSheet.RING_TOPAZ, null), new RingOfEnergy().trueName(),
				"The ring of energy has been simplified/buffed:\n\n" +
						"_-_ Now grants a flat +20% charge speed per level, instead of +1 effective missing charge per level"),
				new ChangeButton( new Bolas(),
				"Bolas have received a damage buff:\n\n" +
						"_-_ Base damage increased to 6-9 from 4-6"),
				new ChangeButton( new WandOfRegrowth(),
				"Thanks to the new furrowed grass system, the wand of regrowth can receive a slight buff:\n\n" +
						"_-_ When the wand of regrowth begins to run out of energy due to excessive use, it will now spawn furrowed grass, instead of short grass."));

		new ChangeInfo(ChangeInfo.Template.NERFS).addButtons(
				new ChangeButton( new ItemSprite(ItemSpriteSheet.RING_RUBY, null), new RingOfFuror().trueName(),
				"Ring of furor has been nerfed/simplified:\n\n" +
						"_-_ Now provides a flat +10.5% attack speed per level, instead of speed which scales based on how slow the weapon is.\n\n" +
						"This means the ring is effectively nerfed for slow weapons and regular weapons, and slightly buffed for fast weapons.\n\n" +
						"A +6 ring grants almost exactly doubled attack speed."),
				new ChangeButton( new ItemSprite(ItemSpriteSheet.RING_GARNET, null), new RingOfForce().trueName(),
				"The ring of force's equipped weapon bonus was always meant as a small boost so it wasn't useless if the player already had a better weapon. It wasn't intended to be used to both replace melee and then boost thrown weapons.\n" +
						"_-_ The ring of force no longer gives bonus damage to thrown weapons."),
				new ChangeButton( new Gauntlet(),
				"As furor now works much better with fast weapons, I've taken the oppourtunity to very slightly nerf sai and gauntlets\n\n" +
						"_-_ Sai blocking down to 0-2 from 0-3\n" +
						"_-_ Gauntlet blocking down to 0-4 from 0-5"),
				new ChangeButton( new Shuriken(),
				"Shuriken have been adjusted due to the new upgrade system:\n\n" +
						"_-_ Base damage increased to 4-8 from 4-6\n" +
						"_-_ Durability reduced to 5 from 10"));
	}
	private void add001Changes() {
		new ChangeInfo("v0.0.1",true);
		new ChangeInfo(ChangeInfo.Template.NEW_CONTENT).addButtons(
				addDeveloperCommentary(
						Milestone.LUST001,
						"This is the culmination of my tinkering prior to my implementation " +
								"of Shattered v0.7.1",
						Milestone.SHPD071, Milestone.LUST000b, Milestone.LUST000
				),
				new ChangeButton(
						new SuccubusSprite.Winged(),
						new Succubus.Winged().name,
						"Added a new succubus variant. It has a little less HP and accuracy " +
								"and deals reduced damage, but moves faster and is more evasive.\n",
						"Stats:",
						"_-_ 75 HP (down from 80)",
						"_-_ 8 armor (down from 10)",
						"_-_ 37 accuracy (down from 40)",
						"_-_ 28 evasion (up from 25)",
						"_-_ 20-28 damage, down from 22-30",
						"_-_ 2x movement speed, flying\n",
						"_-_ Sprite credit to _hellocoolgame#8751_"
				),
				new ChangeButton(
						new ItemSprite(new PlateArmor().image(), new HolyProvidence().glowing()),
						"New Rare Glyph: Holy Providence",
						"Added a new rare glyph that buffs you in combat!\n" +
								"_-_ (2+level)/(50+level) chance (4% @ +0) to bless the user for 6-10 turns, normally distributed\n" +
								"_-_ (2+level)/(50+level) chance (4% @ +0) to give 6-8 turns of adrenaline if bless wasn't proc'd first, normally distributed"
				),
				new ChangeButton(
						new ItemSprite(new Shortsword().image(), new Chaotic().glowing()),
						"New Weapon Curse: Chaotic"
				).appendList("Basically unstable for curses.\n")
						.appendLine("The following effects can be called by this curse:")
						.appendList(
								"Annoying", "Displacing", "Elastic", "Exhausting", "Fragile", "Friendly",
								"Sacrificial", "Wayward", "Anti-entropy (you or target)", "Corrosion (target)",
								"Displacement", "Multiplicity", "Overgrowth (you or target)",
								"Stench (you or target)", "Volatility (you or target)", "Viscosity (you or target)"
						)
		);
		new ChangeInfo(ChangeInfo.Template.BUFFS).addButtons(
				new ChangeButton(
						new KingSprite(),
						"Boss Changes",
						"_Dwarf King:_",
						"_-_ Now resists toxic gas and amok",
						"_-_ Blindness and paralysis immunities are now resists",
						"_-_ No longer resists wand of disintegration",
						"_-_ Passively regains 1 HP per turn",
						"_-_ Undead no longer is immune to grim",
						"_-_ Undead apply slow instead of paralysis",
						"_-_ Undead now resist slow\n",
						"_Yog:_",
						"_-_ Can no longer debuffed",
						"_-_ Is immune to all blobs",
						"_-_ Larva spawns when attacked by magic in addition to melee\n",
						"_Rotting Fist:_",
						"_-_ Now attacks everything in a 3x3 radius at the same time",
						"_-_ Resists burning\n",
						"_Burning Fist:_",
						"_-_ Now immune to frost and chill"
				),
				new ChangeButton(new SuccubusSprite(), new Succubus().name,
						"Succubus can now stack shield if they already have shielding when attacking charmed enemies, " +
								"but can only heal from enemies that are charmed by them, rather than from any charmed enemy."
				),
				new ChangeButton(
						new GnollTricksterSprite(),
						"Gnoll Trickster (and scorpios)",
						"Now fight when cornered.\n" +
								"_-_ They won't apply effects in this case."
				),
				new ChangeButton(
						new ItemSprite( new ScaleArmor().inscribe( new Stone() ) ),
						"Glyphs",
						"_Stone:_",
						"_-_ Damage multiplier is now the chance for the enemy to hit you with" +
								"(60+level)% of your evasion",
						"_-_ Previously it was 1-[0.6*(hit chance)].",
						"_-_ Now applies to magical attacks as well, but is half as effective.\n",
						"_Anti-Magic:_",
						"_-_ No longer applies to all attacks from enemies that can use magic",
						"_-_ Now blocks up to 1/2 armor from magic damage to (more than) compensate\n",
						"_Affection:_",
						"_-_ More likely to give higher charm durations with higher levels"
				),
				new ChangeButton(
						new ItemSprite(ItemSpriteSheet.WEAPON_HOLDER,null),
						"Unarmed Attacks",
						"It doesn't make sense for unarmed attacks to be slower than a knuckleduster, " +
								"so now you can attack twice per turn if unarmed. Ring of Force adjusted accordingly:\n\n" +
								"_-_ Ring of Force max damage halved. It's now basically a fast weapon of its tier."
				),
				new ChangeButton(
						new ItemSprite(new LeatherArmor().inscribe(new Metabolism())),
						"Metabolism",
						"Metabolism healing boosted by 12.5% (4 -> 4.5)"
				)
		);
		new ChangeInfo(ChangeInfo.Template.CHANGES).addButtons(
				new ChangeButton(
						new ItemSprite(Random.Int(2) == 0 ? new MailArmor().inscribe() : new Sword().enchant()),
						"Enchantment/Glyph Identification",
						"Enchantments, curses, and glyphs are now identified on equip rather than on sight. " +
								"To make it obvious that an equipped item is enchanted, revealed enchantments will now" +
								"have a vfx effect signaling that the item you just equipped was actually enchanted. " +
								"There's no need to check your inventory to check for their existance.\n",
						"_-_ chance weapon is enchanted boosted by 50% (10% -> 15%)",
						"_-_ chance armor is enchanted boosted by 33%  (15% -> 20%)"
				),
				new ChangeButton(
						new StatueSprite(),
						"Mob Changes",
						"_Evil Eyes:_",
						"_-_ No longer immune to terror, now resist instead",
						"_-_ Attempting to apply terror to a charging evil eye will cause it to break its gaze",
						"_-_ Resist deathgazes, Grim Traps, and Disintegration Traps",
						"_-_ Now immune to cripple (they do float)\n",
						"_Wraiths:_",
						"_-_ Lose grim and terror immunities",
						"_-_ Terror is now resisted",
						"_-_ Are now inorganic",
						"_-_ Now immune to bleeding and cripple\n",
						"_Misc:_",
						"_-_ Acidic enemies now resist poison, and fiery enemies now resist blazing instead of being immune",
						"_-_ Fire Elementals can no longer be crippled\n\n",
						"_-_ Allies and pirahnas should now ignore each other unless provoked",
						"_-_ reduced the likelyhood of certain rare mobs spawning"
				),
				ChangeButton.misc(
						"Arcane Styli can now be transmuted into stones of enchantment",
						"Shopkeeper now offers different prices for cursed items depending on their other attributes",
						"Added a visual indicator for soul mark recovery.",
						"Changed adrenaline icon",
						"More buffs now grey when about to expire.",
						"Berserk now gives a death message if you die while berserking",
						"Magic from shamans now deal damage distributed randomly rather than normally",
						"Enemy spawn logic adjusted slightly."
				),
				ChangeButton.bugfix(
						"Berserker rage weakening his attacks",
						"Unblessed ankhs giving a 'Groundhog Day'-like effect",
						"Velvet Pouch unable to spawn for huntress",
						"Magical Holster duplicate getting sold to huntress",
						"Various bugs with prismatic image and mirror image caused by tinkering",
						"Formatting bug with blocking weapons",
						"Terror getting visually broken on fatal attacks"
				)
		);
		new ChangeInfo(ChangeInfo.Template.NERFS).addButton(
				new ChangeButton(
						new WarlockSprite(),
						new Warlock().name,
						"_-_ Warlock weaken duration is now random.",
						"_-_ Weaken lasts 0-40 turns, rather than 40 turns"
				)
		);
	}
	private void enumerateChanges() {
		try {
			add010Changes(); // Lustrous v0.0.2
			add071Changes(); // Shattered v0.7.1
			add001Changes();
		} catch(Exception e) {
			LustrousPixelDungeon.reportException(e);
		}
		// v0.0.1

		new ChangeInfo("v0.0.0",true);
		new ChangeInfo("v0.0.0b", false).addButtons(
				new ChangeButton(new WandOfCorruption(),
						"It's extremely obvious that giving wands of corruption the ability to " +
								"inflict vertigo was the wrong decision. Terror + Vertigo is extremely " +
								"powerful, and the wand of corruption was already an amazing wand.\n\n" +
								"_-_ Reverted cripple --> vertigo change"),
				new ChangeButton(new ItemSprite(new Longsword().image(), new Elastic().glowing()),"Eldritch",
						"_-_ Now inflicts brief vertigo on targets immune to terror (4+level)\n" +
								"_-_ Base Terror duration halved (10 -> 5)\n" +
								"_-_ Terror duration now scales with level (+2.5/level)"
				),
				new ChangeButton(
						new Terror(), "Terrified enemies now recover faster when cornered."
				),
				ChangeButton.bugfix(
						"More changelog mistakes",
						"In-text typo with blocking weapons",
						"Attacks not surprise attacking when they should"
				)
		);
		new ChangeInfo("v0.0.0a",false).addButtons(
				new ChangeButton(
						new MagicalHolster(),
						"_-_ Missile weapon durability boost buffed (1/5 -> 1/3)\n" +
								"\nThis should make its integration with huntress a bit more " +
								"intuitive; with the change the huntress effectively gets double " +
								"durability on missile weapons."
				),
				new ChangeButton(
						new WandOfCorruption(),
						"_-_ Can no longer inflict cripple\n" +
								"_-_ Instead inflicts vertigo at a reduced chance\n" +
								"_-_ Minor debuffs now have weights:\n" +
								"    _*_ Weaken   : 4\n" +
								"    _*_ Blindness: 3\n" +
								"    _*_ Terror   : 2\n" +
								"    _*_ Vertigo  : 1"
				),
				new ChangeButton(new ItemSprite(new Longsword().enchant(new Chilling())), "Enchantments",
						"_-_ Chilling now stacks chill\n" +
								"_-_ Venomous now scales a bit better with levels."
				),
				new ChangeButton(
						new ShieldedSprite(),
						new Shielded().name,
						"_-_ Now also gains rage.\n" +
								"_-_ Gets up to 6 shielding just like a warrior with plate."
				),
				ChangeButton.bugfix(
						"Paralytic Darts potentially breaking paralysis",
						"Fatal attacks visually breaking paralysis",
						"Slow and Chill not stacking",
						"Taking 0 damage weakening charm and terror and breaking magical sleep and frost",
						"Changelog typos"
				)
		);
		new ChangeInfo(ChangeInfo.Template.NEW_CONTENT).addButtons(
				addDeveloperCommentary(
						Milestone.LUST000,
						"I'm honestly just happy to have figured this out. As of this moment, " +
								"I'm waiting on Shattered 0.7.1 to be released so I can implement it.",
						Milestone.SHPD070
				),
				new ChangeButton(
						HeroSprite.avatar(HeroClass.HUNTRESS,0),
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
								"_-_ zaps now do 6-12 right off the bat to compensate for new rarity.\n" +
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
		new ChangeInfo(ChangeInfo.Template.BUFFS).addButtons(
				new ChangeButton(
						new Image(Assets.WARRIOR, 0, 90, 12, 15),
						"Berserker",
						"_-_ A recovering Berserker can now gain Rage up to the extent recovered\n" +
								"_-_ Berserk now decays 15% slower\n" +
								"_-_ Damage boost while berserking is now 1.75x\n" +
								"_-_ Being at low health now speeds up Rage building by up to 1/3."
				),
				new ChangeButton(
						new Ankh(),
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
				new ChangeButton( new Quarterstaff(),
						"_-_ Quarterstaff's block now scales by +0/+1\n" +
								"_-_ Base block reduced by 1/3 (3 -- > 2)"
				),
				new ChangeButton( new Gloves(),
						"While no heroes now start with the knuckleduster now, that does not mean it is no longer in the game!\n" +
								"_-_ Transmuting any tier-1 weapon (aside from Mage's Staff) will yield a Knuckleduster.\n" +
								"_-_ Transmuting a Knuckleduster will yield a random non-Knuckleduster tier-1 weapon like normal.\n" +
								"_-_ The Knuckleduster now blocks up to 1 point of damage!"
				),
				new ChangeButton(new ItemSprite(ItemSpriteSheet.WAND_TRANSFUSION), "Wands")
						.appendLine("Wand of Transfusion:")
						.appendList(
								"Charm now scales by 2 (was 1)",
								"Battlemage effect's proc rate boosted by 25% (1/10 -> 1/8)\n"
						).appendLine("Wand of Fireblast:")
						.appendList("When consuming 3 charges at once, now applies both paralysis and cripple"),
				new ChangeButton(
						new Charm().getLargeIcon(), "Charm",
						"_-_ Charm now only recovers if hit by whoever applied it.\n" +
								"\nThis is both a buff to charm in general and a nerf to viscosity."
				),
				new ChangeButton(
						new ItemSprite(
								ItemSpriteSheet.SHORTSWORD,
								WeaponCurse.GLOWING
						),
						"Elastic",
						"Elastic can easily be a run-ender if the hero doesn't have any " +
								"alternatives to do damage.\nThis change should make these situations " +
								"less unfair and run-ending:\n" +
								"_-_ Elastic weapons now deal 1/6 damage, instead of 0.\n\n" +
								"With this, bosses should now be beatable with only an Elastic weapon (given enough time)"
				)
		);
		new ChangeInfo(ChangeInfo.Template.CHANGES).addButtons(
				new ChangeButton(
						new ItemSprite(ItemSpriteSheet.STONE_HOLDER),
						"Stone generation changes",
						"_-_ Runestone generation is now weighted based on its base scroll "
								+ "rarity and its alchemical scroll-stone ratio",
						"_-_ Stones of Augmentation and Enchantment can now drop as rare stones."
				),
				new ChangeButton(
						get(DEPTH),
						"Mob Spawn Changes",
						"_-_ Shamans now spawn on floors 11 and 12 (0 -> 1) \n",
						"Rare Mob spawns adjusted:",
						"_-_ MM shamans now spawn on floor 4 (0 -> .05)",
						"_-_ MM shaman spawn rate 10x more than normal on floor 6",
						"_-_ Fire Elementals now also spawn on floor 12 (0 -> .02)",
						"_-_ Dwarf Warlocks now spawn on floors 13 and 14 (0 -> 0.01)",
						"_-_ Monk spawn rate halved on floor 14 (.01 -> .005)",
						"_-_ Monks now spawn on floor 16 (0 -> .2)",
						"_-_ Golems now spawn on floor 17 (0 -> .2)",
						"_-_ Succubus now spawn on floor 18 (0 -> .02)",
						"_-_ Evil Eyes now spawn on floor 19 (0 -> .01)"
				),
				new ChangeButton(
						get(PREFS),
						"Misc Changes",
						"_-_ There's now a post-halls tier generation table, so crypt rooms in " +
								"floors 22-24 are even less likely to give low tier armor now.\n",
						"_-_ Adjusted rare mobs. They will now spawn earlier and have a bit more variety.\n",
						"_-_ Huntress, Journal Pages, and Challenges are now enabled by default.",
						"_-_ Food, Arcane Styli, and Tomes of Mastery can now be quickslotted. (idea credit s0i)",

						"_-_ Cursed wands can now spawn Inferno and Blizzard\n",

						"_-_ Weapons that block damage now say how much damage they can block.",
						"_-_ Transmutation and Recycle now have a VFX effect!",
						"_-_ Darts and Shurikens now have a faster throw animation.",
						"_-_ Some descriptions reworded."
				),
				ChangeButton.bugfix("Attacks by Stunning weapons potentially instantly breaking paralysis"),
				new ChangeButton(
						get(LANGS),
						"Removed Translations",
						"The ability to play the game in other languages than English has been " +
								"removed for the time being. This mod is not on transifex, and thus has no " +
								"way to obtain new translations for any content changes."
				)
		);
	}

	@Override
	public void create() {
		infos.clear();
		super.create();

		int w = Camera.main.width, 	h = Camera.main.height;

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

		int pw = 135 + panel.marginLeft() + panel.marginRight() - 2,
				ph = h - 16;

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
		enumerateChanges(); // add the actual changes
		Component content = list.content();
		content.clear();

		float posY = 0, nextPosY = 0;	boolean second = false;
		for (ChangeInfo info : infos){
			if (info.major) {
				posY = nextPosY; second = false;
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

	@SuppressWarnings("SpellCheckingInspection")
	private static class ChangeInfo extends Component {

		enum Template {
			NEW_CONTENT("New Content",Window.TITLE_COLOR),
			BUFFS("Buffs",CharSprite.POSITIVE),
			CHANGES("Changes",CharSprite.WARNING),
			NERFS("Nerfs",CharSprite.NEGATIVE),
			BLANK("",Window.TITLE_COLOR);

			String title;
			boolean major;
			int color;

			Template(String title, boolean isMajor, int color) {
				this.title = title;
				this.major = isMajor;
				this.color = color;
			}
			Template(String title, int color) {
				this(title,false,color);
			}
		}

		protected ColorBlock line;

		private RenderedText title;
		private boolean major;

		private RenderedTextMultiline text;

		private ArrayList<ChangeButton> buttons = new ArrayList<>();

		ChangeInfo( String title, boolean majorTitle, int color, String text ) {
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
			hardlight(color);
			infos.add(this);
		}
		ChangeInfo( String title, boolean majorTitle, int color) {
			this(title, majorTitle, color, null);
		}
		ChangeInfo( Template template ) {
			this(template.title,template.major,template.color);
		}
		ChangeInfo(String title, boolean majorTitle) { this(title,majorTitle,Window.TITLE_COLOR);}

		public void hardlight( int color ){
			title.hardlight( color );
		}

		void addButton( ChangeButton button ){
			buttons.add(button);
			add(button);

			button.setSize(16, 16);
			layout();
		}
		void addButtons( ChangeButton... buttons) { for(ChangeButton button : buttons) addButton(button); }

		public boolean onClick( float x, float y ){
			for( ChangeButton button : buttons) if (button.inside(x, y)) {
				button.onClick();
				return true;
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

			float posX = x,	tallest = 0;
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

		private static ChangeButton misc(String... changes) {
			return new ChangeButton(get(PREFS),Messages.get(ChangesScene.class,"misc")).appendList(changes);
		}

        private static ChangeButton bugfix(String...fixes) { // it's flawed but it's still better than nothing
			return new ChangeButton(
					new Image( Assets.SPINNER, 144, 0, 16, 16),
					Messages.get(ChangesScene.class,"bugfixes")
			).appendList(fixes);
        }

		protected Image icon;
		protected String title;
		protected String message;

		ChangeButton( Image icon, String title, String... messages){
			super();
			
			this.icon = icon;
			add(this.icon);

			this.title = Messages.titleCase(title);

			this.message = "";
			appendLines(messages);

			layout();
		}

		ChangeButton( Item item, String... messages ){
			this( new ItemSprite(item), item.name(), messages);
		}

        ChangeButton( Buff buff, String... messages) {
			this( buff.getLargeIcon(), buff.toString(), messages);
		}

		ChangeButton appendList(String... items ) {
			for(String item : items)
				appendLine("_-_ " + item);
			return this;
		}
		@SuppressWarnings("StringConcatenationInLoop")
		public ChangeButton appendLine(String message) {
		    if (message != null && !message.equals("")) appendLine();
		    return append(message);
		}
		public ChangeButton appendLine() {
			return append("\n");
		}
		public ChangeButton appendLines(String... messages) {
		    for(String message : messages) appendLine(message);
		    return this;
        }
		public ChangeButton append(String message) {
		    this.message += message;
		    return this;
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
	
		ChangesWindow( Image icon, String title, String message ) {
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
