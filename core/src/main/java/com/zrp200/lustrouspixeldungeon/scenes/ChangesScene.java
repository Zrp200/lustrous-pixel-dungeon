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
import com.zrp200.lustrouspixeldungeon.LustrousPixelDungeon.Version;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Burning;
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
import com.zrp200.lustrouspixeldungeon.items.wands.WandOfTransfusion;
import com.zrp200.lustrouspixeldungeon.items.weapon.curses.Chaotic;
import com.zrp200.lustrouspixeldungeon.items.weapon.curses.Necromantic;
import com.zrp200.lustrouspixeldungeon.items.weapon.curses.WeaponCurse;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Blazing;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Chilling;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Dazzling;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Elastic;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Vampiric;
import com.zrp200.lustrouspixeldungeon.items.weapon.melee.Gauntlet;
import com.zrp200.lustrouspixeldungeon.items.weapon.melee.Glaive;
import com.zrp200.lustrouspixeldungeon.items.weapon.melee.Gloves;
import com.zrp200.lustrouspixeldungeon.items.weapon.melee.Greataxe;
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
import com.zrp200.lustrouspixeldungeon.levels.traps.ArmageddonTrap;
import com.zrp200.lustrouspixeldungeon.levels.traps.InfernalTrap;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
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
		LUST012 (Version.v012,					5, 20,2019) {
			@Override
			public void addChanges() {
				new ChangeInfo(Version.v012.name, true);
				new ChangeInfo(ChangeInfo.Template.BUFFS).addButtons(
				        new ChangeButton(new ArmageddonTrap().getImage(), "Armageddon Trap",
								"Look, it's your fault for activating this.\n").appendList(
				                "Regrowth seeded up to 30 units from 24",
                                "The game now tries twice as hard to surround the hero with grass",
                                "The game now tries twice as hard to place enemies on grass tiles",
                                "Passive mobs are no longer teleported, but active statues can be teleported now."
                        )
                );
				new ChangeInfo(ChangeInfo.Template.CHANGES).addButtons(
						ChangeButton.misc(
								"Only transmuting the original starting weapon will result in a cord; a warrior transmuting a dagger will not get a cord guarenteed, though it is still a chance.",
								"Result from transmuting tier-1 weapons is now weighted where Gloves > Dagger = Cord > Worn Shortsword; " +
										"you are more likely to get gloves and less likely to get worn shortsword than before"
						),
						ChangeButton.bugfix("Issues with guaranteed scroll of transmutation drop",
								"Distortion traps destroying important items", "Warping trap not hiding level")
				);
			}
		},
		LUST011 (Version.v011,			 		4, 4 ,2019) {
			@Override
			public void addChanges() {
				new ChangeInfo(Version.v011.name, true);
				new ChangeInfo("v0.1.1b",false).addButtons(
						new ChangeButton(new ItemSprite(new Longsword().enchant(new Blazing())),"Partially Implemented Shattered v0.7.2b")
								.appendList("Swift buff duration increased to 4, from 2 (was increased to 5 in shattered)",
										"Vampiric heal internally now 2.5%-15%, from 0-10%. New variation mechanic remains however.",
										"Lucky base proc rate boosted to 8%",
										"Grim changes implemented to the letter, overwriting previous changes.",
										"Blooming and blazing changes not implemented."),
						new ChangeButton(new Burning().getLargeIcon(), "Fire-based mechanics").appendList(
								"Blazing now has a 60% chance to ignite when not on flammable tiles, up from 50%",
								"Fire imbue now has a 50% chance to proc blazing effect, rather than just inflict burning.",
								"Firebolt shaman and fire elementals now inflict 6 turns of burning, down from 8, from their attacks.",
								"Certain types of terrain will always burn for a specific amount of time; mostly " +
										"flammable things will burn for 4 turns, but tall grass, barricades, " +
										"and bookshelves will burn for 5, and grass will burn for 2.",
								"Blazing burning duration now matches shattered exactly; 4, down from 6."
						),
						ChangeButton.misc(
								"Hunting mobs will now cluster around their target even if some can't actually reach.",
								"Mobs now avoid returning boomerangs if not amok",
								"Returning boomerang can no longer surprise attack and has a 5% accuracy penalty",
								"Terror no longer removes amok",
								"Spinner now show rage vfx when switching back to hunting their targets",
								"Weakness grey starts at 3 turns now",
								"Scroll of Upgrade's enchant nullification chance now starts at +3, down from +4, and is guarenteed at +7, down from +8.",
								"Adjusted swift description to better indicate wand option, and added a proc vfx",
								"Shaman resistances should be a bit more sensical",
								"Readded key-bug code that will prevent future key bugs from destroying games"
						),
						ChangeButton.bugfix(
								"Quest sad ghost always moving at 1x speed",
								"Various bugs that result in losing items",
								"Rose sad ghost crashing the game if fighting without a weapon.",
								"Various issues with mage's staff when starting a game",
								"Blooming rooting non-enemy targets for 2 turns instead of one turn",
								"Tipped darts temporarily hiding enchantment when becoming untipped.",
								"Guardian traps causing enchant reveal animation to play",
								"Resists affecting damaging debuff duration",
								"Incorrect pricing of some missile weapons",
								"Formatting error with shields and typical blocking")
								.appendLine("\nFrom Shattered v0.7.2b")
								.appendList(
										"Cloak of Shadows not able to be turned off at 0 charges",
										"Multiplicity curse spawning rats on floor 5",
										"Dried rose rarely being usable before completing ghost quest",
										"Corrupted thieves being able to steal from the hero",
										"Rare crashes involving rankings window",
										"Crashes and other odd behavior when a berserking hero is affected by shielding buffs")
				);
				new ChangeInfo("v0.1.1a",false).addButtons(
						new ChangeButton(new ItemSprite(new Glaive().enchant(new Dazzling())), "Dazzling",
								"Adding slow turned out to be more powerful than I thought, so I'm reducing " +
										"the potency of the enchant somewhat. I like slow, though, so " +
										"I'm not getting rid of it.")
								.appendList(
										"Blindness scaling halved (now +0/+.5 instead of +0/+1)",
										"Blindness base duration remains at 1-2 turns, however.",
										"Slow base duration reduced to 1 - 1.33, down from 1 - 1.5",
										"Slow scaling now +0/+.33, down from +0/+.5"),
						new ChangeButton(new ItemSprite(new Greataxe().enchant(new Vampiric())),"Vampiric")
								.appendList(
										"Life steal is now normally distributed between 0 and double the previous " +
												"rate (which varies according to your health)",
										"This means that the range is now effective 0-20% instead of 0-10%",
										"The rolls are retried for as many times as the weapon has levels, taking " +
												"the highest roll each time, and then applying that roll " +
												"to the damage to determine how much healing is granted.")
								.appendLine("\nThese changes should make vampiric feel more powerful, especially early, " +
										"while not really actually boosting its typical life-steal from v0.7.2 levels, at least initially."),
						ChangeButton.misc("Blooming root reduced from 2 turns to 1.",
								"Grim's min chance increases by .2% per level.",
								"Chaotic can no longer roll Overgrowth.",
								"Chaotic's max level roll for glyphs is now 12, down (again) from 20.",
								"Chaotic now procs displacement and multiplicity twice as often, and repulsing half as often",
								"Displacement can now proc for prismatic images, and displacing can now proc " +
										"against the hero when used by an amok mirror image",
								"Blazing trap duration doubled",
								"Grass will now consistently burn for 4 turns.",
								"Swapped positions of buttons in rename window",
								"Went back and properly credited people for inspiring various parts of 0.1.1 " +
										"and also changed some icons around for consistency."),
						ChangeButton.bugfix(
								"Major crash bugs with floor 26",
								"Crash bugs with terror",
								"Crash bugs with throwing potions",
								"Transmuting an artifact into spellbook causing a crash",
								"Necromantic being unobtainable",
								"Dazzling enchantment being called !!!NO TEXT FOUND!!!",
								"Incorrect terror behavior",
								"Enchanting missile weapons resulting in you losing them",
								"Minor fire bugs",
								"Visual visual issues with fire elementals and buffs"));
				new ChangeInfo(ChangeInfo.Template.NEW_CONTENT).addButtons(
						addDeveloperCommentary(LUST011,Messages.get(Milestone.class, "011"), SHPD072),
						new ChangeButton(new ArmageddonTrap().getImage(), "New Demon Halls Trap!",
								"Armageddon traps now appear quite rarely in floors 22-24, " +
										"and inflict fiery death upon all who are unfortunate enough to " +
										"exist on the floor... and some who may not be on the floor.")
								.appendLine("\nRelated changes:")
								.appendList("Cursed wand's forest fire effect replaced with armageddon trap activation",
										"Regrowth blobs are now flammable and also consumed by fire as fuel.",
										"Fire will not destroy grass on a tile affected by regrowth blobs"),
						new ChangeButton(new ItemSprite(new Shortsword().enchant(new Necromantic())),"New Curse: Necromantic", "Added a new curse to replace Elastic, this one should be interesting to play with:\n")
								.appendList("All non-wraith enemies killed by a necromantic weapon will rise again as wraiths.")
								.appendLine("\nThis means that fighting multiple enemies at a time is a lot more difficult. " +
										"In addition, door combat is both more necessary and also penalized (especially in the cases of Warrior and Huntress), since if a wraith spawns in a door, you won't be able to use the door against them.")
								.appendLine("\nWraith spawning, of course, has its uses...")
								.appendLine("\nIdea credit to _TrustyWay#1377_"),
						new ChangeButton(Icons.get(Icons.BACKPACK),"Equipment Renaming!")
								.appendLines("Added a weapon/wand renaming feature. " +
												"It's extremely similar to Shattered's donation perk, with the " +
												"additions of wand renaming and the option to remove enchant prefixes.",
										"\nBecause it's so similar, I may remove this functionality if Evan wishes it."));
				new ChangeInfo("from Shattered v0.7.2a",false,Window.SHPX_COLOR).addButtons(
						new ChangeButton(new ItemSprite(ItemSpriteSheet.POTION_CATALYST, null), "Catalysts and Alchemy").appendList(
								"Alchemical and arcane catalysts and all related alchemy recipe adjustments implemented",
								"When a recipe asks for any item of a certain type that item no longer has to be identified.",
								"Alchemy pages now spawn slower towards the beginning of the game, and much faster at the end.")
								.appendLine("\nElixir of Might:").appendList(
								"Recipe changed to: strength + catalyst + 5 energy",
								"Health boost now scales up with level, but fades after the hero gains a few levels"),
						new ChangeButton(new ItemSprite(ItemSpriteSheet.LONGSWORD, new ItemSprite.Glowing(0x0000FF)), "Enchantments",
								"Mostly implemented enchant changes, making them more similar to glyphs and less about direct damage\n")
								.appendList("Chilling buffed (stacks with itself over multiple procs more effectively, common)\n",
										"Lucky buffed (now works like a pseudo-Ring of Wealth instead of old effect).\n",
										"Shocking, Grim, and Vampiric nerfed.\n",
										"Vorpal, Venomous, Eldritch, and Stunning removed.\n",
										"Blooming, Elastic (formerly a curse), Precise, and Swift added.\n",
										"New Curse: Polarized -- old lucky effect with 1.5x damage and no tilt.\n")
								.appendLine("Not Implemented:")
								.appendList("Blazing nerf - changed instead (see corresponding section)",
										"Blocking not implemented")
								.appendLine("\nSome battlemage effects have been adjusted to accommodate these new enchantments. " +
										"Most of these are very minor, except staff of regrowth, which now procs blooming."),
						new ChangeButton(new ItemSprite(ItemSpriteSheet.MAGIC_INFUSE, null), "Upgrade-Enchantment Interaction",
								"_-_ Items now always safe up to +4, then have a growing chance until +8 where enchantment loss is guaranteed.\n\n" +
										"_-_ Upgrades now have a set 33% chance to cleanse curses, instead of a chance which scales with level.\n\n" +
										"Magical Infusion spell implemented:\n" +
										"_-_ Recipe changed to: upgrade + catalyst + 4 energy.\n" +
										"_-_ No longer applies an enchant/glyph, instead is guaranteed to preserve one while upgrading."),
						new ChangeButton(new ItemSprite(ItemSpriteSheet.BREW_WICKED, null), "Combination Items",
								"Implemented removal of combination brews and elixirs; the following items are no longer craftable:\n" +
										"_-_ Wicked Brew\n" +
										"_-_ Frigid Brew\n" +
										"_-_ FrostFire Brew\n" +
										"_-_ Elixir of Restoration\n" +
										"_-_ Elixir of Vitality"),
						new ChangeButton(new ItemSprite(ItemSpriteSheet.SEED_SWIFTTHISTLE, null), "Item Balance Adjustments",
								"Several seeds and stones have been buffed:\n" +
										"_-_ Player can now move without cancelling swiftthistle's effect\n" +
										"_-_ Duration of poison from sorrowmoss increased by ~33%\n" +
										"_-_ Strength of warden's earthroot effect increased\n" +
										"_-_ Stone of clairvoyance no longer disarms traps, now goes through walls instead\n" +
										"_-_ Stone of detect curse is reworked, now stone of disarming. Disarms up to 9 traps where it is thrown.\n" +
										"_-_ Stone of aggression now forces enemies to attack a target. Duration is longer if thrown at allies.\n\n" +
										"_-_ Scroll of teleportation now teleports the player to the entrance of secret/special rooms instead of into them\n\n" +
										"_-_ Blessed ankhs now cure the same debuffs as a potions of healing\n\n" +
										"Fire and toxic gas now deal damage based on dungeon depth, and not target max health. " +
										"Several bosses have lost their resistances to these effects as a result of this change."),
						new ChangeButton(new ItemSprite(new WandOfTransfusion()),"Items","The following items have been updated to match 0.7.2a:")
								.appendLine("\nWand of Transfusion")
								.appendList("Shields hero when used on non-undead enemies instead of damaging",
										"Charm duration scaling removed",
										"Undead damage nerfed heavily")
								.appendLine("\nRing of Wealth")
								.appendList("Drop table now matches 0.7.2a", "1/10 chance for rare drop", "drops happen 50% more often"),
						ChangeButton.misc(
								"The Identification system has been adjusted to require EXP gain in addition to item uses, " +
										"preventing exploits where an item could be used in unintended ways to rapidly ID it. ",
								"Items should ID at about the same rate if exp is gained while using them.\n\n",
								"Increased the max level to gain exp from gnoll brutes and cave spinners by 1.\n",
								"Sniper's mark now lasts for 2 turns, up from 1. This should make it easier to use with slow weapons, or while slowed.\n",
								"Meat Pie recipe cost reduced from 9 to 6, total healing reduced from 45 to 25"),
						ChangeButton.bugfix(
								"Tengu spawning on top of other characters",
								"Cloak of shadows only being usable from quickslots if it has 1 charge",
								"Various rare crash bugs",
								"Various minor visual bugs",
								"Grim enchant activating when an enemy is already dead",
								"Burning destroying scrolls when the hero is immune to it",
								"Chasms killing enemies which are already dead in some cases",
								"Thieves not correctly interacting with quickslotted items",
								"Screen orientation not always being set when game starts",
								"Flying characters pushing the ground after teleporting,",
								"Bombs rarely damaging Tengu multiple times",
								"Thrown weapons instantly breaking in rare cases",
								"Dwarf King summoning skeletons while frozen",
								"Incorrect behaviour when wands recharge very quickly",
								"Thieves rarely escaping when they are close",
								"Beacon of returning losing set location when scroll holder is picked up",
								"Recycle not giving an item if inventory is full",
								"Rare cases where the game wouldn't save during alchemy"));
				new ChangeInfo(ChangeInfo.Template.BUFFS).addButtons(
						new ChangeButton(new ItemSprite(new Longsword().enchant(new Blazing())),"Blazing")
								.appendList(
										"Now uncommon (was common)",
										"Proc rate reduced to (1+L)/(5+L)",
										"Old behavior merged with new behavior.",
										"Still has a 50% chance to reignite, but now has 75% chance to reignite on flammable tiles.",
										"Damage scales based on depth instead of level",
										"Initial ignition is not accompanied by damage",
										"Procs on a burning target do slightly reduced blazing damage, "
												+ "but set the target's burn duration to 4 if that would extend it."),
						new ChangeButton(new ItemSprite(new Glaive().enchant(new Dazzling())), "Dazzling",
								"Instead of removing dazzling, I've decided instead to double down on its mechanics. " +
										"Dazzling was sometimes considered to be a better stunning already, " +
										"so I think these changes should fit well.")
								.appendList(
										"Now rare (was uncommon), replacing stunning",
										"Base Proc rate reduced to (1+L)/(8+L)",
										"Color is now light yellow to properly account for the addition of Swift",
										"Inflicts 1-(1.5+0.5L) turns of slow instead of 1-(1+0.5L) turns of cripple",
										"Base blind duration is now 1-2 (up from 1-1), scaling unchanged"),
						ChangeButton.misc(
								"All upgradeable items spawned in floors 22-24 now get a free upgrade.",
								"Blooming now applies a brief root if grass was created at the target's location.",
								"Swift works with wands now!",
								"Precise is now a common enchant (was uncommon in 0.72), replacing blazing in common",
								"Lucky is now uncommon (was rare in 0.7.2)"));
				new ChangeInfo(ChangeInfo.Template.CHANGES).addButtons(
						new ChangeButton(new ItemSprite(new Shortsword().enchant(new Chaotic())),"Curses")
								.append("I've realized that there are too many weapon curses for how obscure they are.")
								.appendLine("\nRemoved curses:").appendList("Wayward, Fragile")
								.appendLine("\nChaotic adjustments:")
								.appendList("Cannot proc removed curses",
										"No longer can proc holy providence",
										"Viscosity returned to pre-0.1.0b potency")
								.appendLine("\n\nMagical Infusion will now also never overwrite curse enchants."),
						new ChangeButton(new ItemSprite(new PlateArmor().image,new ItemSprite.Glowing(0x88EEFF)), "Anti-magic Removed")
								.appendLines("To even out enchant quantity between armors and enchantments, I'm removing anti-magic. " +
												"It's basically outclassed by ring of elements, " +
												"has competetion from stone, and suffers from being completely useless in the early-game, " +
												"and near-useless otherwise; thus, I see no issues with removing it.",
										"Armors that had glyphs of anti-magic will instead have holy providence."),
						ChangeButton.misc(
								"Arcane Catalyst and Alchemical Catalyst can now be transmuted into each other.",
								"Shocking's initial arc (attacker to defender) removed.",
								"Charm vfx shouldn't play on immune enemies anymore."),
						ChangeButton.bugfix(
								"Infinite loop in secret larder room generation resulting in an inability to progress further in the game.",
								"Crashes with dried rose",
								"Enchanting a stack of two missile weapons resulting in two stacks, with one containing 0 items.",
								"Stewed meat giving mystery meat eat effects."
						)
				);
			}
		},
		LUST010 (Version.v010,		 	 		2, 14,2019) {
			@Override
			public void addChanges() {
				MissileWeapon enchantedMissile;
				do {
					try {
						enchantedMissile = (MissileWeapon)Random.oneOf(Random.oneOf(Generator.misTiers).classes).newInstance();
					} catch (Exception invalid) { enchantedMissile = TippedDart.randomTipped(); } // keep trying until we get something that works
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
						new ChangeButton(Icons.get(Icons.NOTES),"Changelog omissions","Amended v0.1.0 changelog to include another new mechanic I forgot to mention and also fixed some formatting issues.")
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
								"They can addAllChanges up to 2/3 of their intended duration onto durations."),
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
				SHPD071.addChanges();
			}
		},
		LUST001 (Version.v001,					12,20,2018) {
			@Override
			public void addChanges() {
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
		},
		LUST000b("Lustrous v0.0.0b",		12,6 ,2018),
		LUST000a("Lustrous v0.0.0a",  	12,4 ,2018),
		LUST000 ("Lustrous v0.0.0",		12,1 ,2018) {
			@Override
			public void addChanges() {
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
										"_-_ bolts will reignite the tile they are targeted at whether or not they hit their target\n" +
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
		},

		// everything past this point is not displayed.
		SHPD072 ("Shattered v0.7.2", 	 	3 ,18,2019),
		SHPD071d("Shattered v0.7.1d",	 	1 ,18,2019),
		SHPD071b("Shattered v0.7.1b", 	12,30,2018),
		SHPD071 ("Shattered v0.7.1",		12,18,2018) {
			@Override
			public void addChanges() {
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
		},
		SHPD070 ("Shattered v0.7.0",		10,18,2018);

		protected String name;
		private Date releaseDate;

		Milestone(String name, int releaseMonth, int releaseDay, int releaseYear) {
			GregorianCalendar calender = new GregorianCalendar();
			calender.set(releaseYear,releaseMonth-1,releaseDay);

			this.name = name;
			this.releaseDate = calender.getTime();
		}
		Milestone(Version version, int releaseMonth, int releaseDay, int releaseYear) {
			this("Lustrous " + version.name, releaseMonth, releaseDay, releaseYear);
		}
		public void addChanges() {} // nothing by default

		public static void addAllChanges() {
			try {
				for(Milestone milestone : values() ) {
					milestone.addChanges();
					if(milestone == LUST000) break; // don't want add shattered stuff.
				}
			} catch(Exception e) {
				LustrousPixelDungeon.reportException(e);
			}
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
		message.append(commentary == null ? "Dev commentary will be added here in the future." : commentary); // addAllChanges commentary
		return new ChangeButton( devIcon,"Developer Commentary", message.toString() );
	}
	private static ChangeButton addDeveloperCommentary(Milestone release, String commentary,Milestone...eventsToCompare) {
		return addDeveloperCommentary(new Image(Assets.ZRP200),release,commentary,eventsToCompare);
	}

	private static final ArrayList<ChangeInfo> infos = new ArrayList<ChangeInfo>();

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
		Milestone.addAllChanges(); // addAllChanges the actual changes
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
        public ChangeButton appendLines(int lines) {
			for(int line=0; line<lines; line++) {
				appendLine();
			}
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
