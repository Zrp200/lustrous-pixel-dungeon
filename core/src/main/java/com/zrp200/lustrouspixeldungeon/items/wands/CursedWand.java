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

package com.zrp200.lustrouspixeldungeon.items.wands;

import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Assets;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.LustrousPixelDungeon;
import com.zrp200.lustrouspixeldungeon.actors.Actor;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.blobs.Blizzard;
import com.zrp200.lustrouspixeldungeon.actors.blobs.Blob;
import com.zrp200.lustrouspixeldungeon.actors.blobs.ConfusionGas;
import com.zrp200.lustrouspixeldungeon.actors.blobs.Fire;
import com.zrp200.lustrouspixeldungeon.actors.blobs.Inferno;
import com.zrp200.lustrouspixeldungeon.actors.blobs.ParalyticGas;
import com.zrp200.lustrouspixeldungeon.actors.blobs.Regrowth;
import com.zrp200.lustrouspixeldungeon.actors.blobs.ToxicGas;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Burning;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Frost;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Recharging;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.actors.mobs.Mimic;
import com.zrp200.lustrouspixeldungeon.actors.mobs.Mob;
import com.zrp200.lustrouspixeldungeon.actors.mobs.npcs.Sheep;
import com.zrp200.lustrouspixeldungeon.effects.CellEmitter;
import com.zrp200.lustrouspixeldungeon.effects.Flare;
import com.zrp200.lustrouspixeldungeon.effects.MagicMissile;
import com.zrp200.lustrouspixeldungeon.effects.Speck;
import com.zrp200.lustrouspixeldungeon.effects.SpellSprite;
import com.zrp200.lustrouspixeldungeon.effects.particles.ShadowParticle;
import com.zrp200.lustrouspixeldungeon.items.Generator;
import com.zrp200.lustrouspixeldungeon.items.Item;
import com.zrp200.lustrouspixeldungeon.items.artifacts.TimekeepersHourglass;
import com.zrp200.lustrouspixeldungeon.items.bombs.Bomb;
import com.zrp200.lustrouspixeldungeon.items.scrolls.ScrollOfRecharging;
import com.zrp200.lustrouspixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.zrp200.lustrouspixeldungeon.levels.Terrain;
import com.zrp200.lustrouspixeldungeon.levels.traps.ArmageddonTrap;
import com.zrp200.lustrouspixeldungeon.levels.traps.CursingTrap;
import com.zrp200.lustrouspixeldungeon.levels.traps.ShockingTrap;
import com.zrp200.lustrouspixeldungeon.levels.traps.StormTrap;
import com.zrp200.lustrouspixeldungeon.levels.traps.SummoningTrap;
import com.zrp200.lustrouspixeldungeon.mechanics.Ballistica;
import com.zrp200.lustrouspixeldungeon.messages.Languages;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.plants.Plant;
import com.zrp200.lustrouspixeldungeon.scenes.GameScene;
import com.zrp200.lustrouspixeldungeon.scenes.InterlevelScene;
import com.zrp200.lustrouspixeldungeon.ui.TargetHealthIndicator;
import com.zrp200.lustrouspixeldungeon.utils.GLog;
import com.zrp200.lustrouspixeldungeon.windows.WndOptions;

import java.io.IOException;
import java.util.ArrayList;

//helper class to contain all the cursed wand zapping logic, so the main wand class doesn't get huge.
public class CursedWand {

	private static float COMMON_CHANCE = 0.6f;
	private static float UNCOMMON_CHANCE = 0.3f;
	private static float RARE_CHANCE = 0.09f;
	private static float VERY_RARE_CHANCE = 0.01f;

	public static void cursedZap(final Wand wand, final Hero user, final Ballistica bolt){
		switch (Random.chances(COMMON_CHANCE, UNCOMMON_CHANCE, RARE_CHANCE, VERY_RARE_CHANCE)){
			case 0:
			default:
				commonEffect(wand, user, bolt);
				break;
			case 1:
				uncommonEffect(wand, user, bolt);
				break;
			case 2:
				rareEffect(wand, user, bolt);
				break;
			case 3:
				veryRareEffect(wand, user, bolt);
				break;
		}
		wand.wandUsed();
	}

	private static void commonEffect(final Wand wand, final Hero user, final Ballistica bolt){
		switch(Random.Int(4)){

			//anti-entropy
			case 0:
				cursedFX(user, bolt, new Callback() {
						public void call() {
							Char target = Actor.findChar(bolt.collisionPos);
							switch (Random.Int(2)){
								case 0:
									if (target != null)
										Buff.affect(target, Burning.class).reignite(target);
									Buff.affect(user, Frost.class, Frost.duration(user) * Random.Float(3f, 5f));
									break;
								case 1:
									Buff.affect(user, Burning.class).reignite(user);
									if (target != null)
										Buff.affect(target, Frost.class, Frost.duration(target) * Random.Float(3f, 5f));
									break;
							}
						}
					});
				break;

			//spawns some regrowth
			case 1:
				cursedFX(user, bolt, new Callback() {
					public void call() {
						GameScene.add( Blob.seed(bolt.collisionPos, 30, Regrowth.class));
					}
				});
				break;

			//random teleportation
			case 2:
				switch(Random.Int(2)){
					case 0:
						ScrollOfTeleportation.teleportHero(user);
						break;
					case 1:
						cursedFX(user, bolt, new Callback() {
							public void call() {
								Char ch = Actor.findChar( bolt.collisionPos );
								if (ch == user){
									ScrollOfTeleportation.teleportHero(user);
								} else if (ch != null && !ch.properties().contains(Char.Property.IMMOVABLE)) {
									int count = 10;
									int pos;
									do {
										pos = Dungeon.level.randomRespawnCell();
										if (count-- <= 0) {
											break;
										}
									} while (pos == -1);
									if (pos == -1 || Dungeon.bossLevel()) {
										GLog.w( Messages.get(ScrollOfTeleportation.class, "no_tele") );
									} else {
										ch.pos = pos;
										if (((Mob) ch).state == ((Mob) ch).HUNTING)((Mob) ch).state = ((Mob) ch).WANDERING;
										ch.sprite.place(ch.pos);
										ch.sprite.visible = Dungeon.level.heroFOV[pos];
									}
								}
							}
						});
						break;
				}
				break;

			//random gas at location
			case 3:
				cursedFX(user, bolt, new Callback() {
					public void call() {
						switch (Random.Int(5)) {
							case 0:
								GameScene.add( Blob.seed( bolt.collisionPos, 800, ConfusionGas.class ) );
								break;
							case 1:
								GameScene.add( Blob.seed( bolt.collisionPos, 500, ToxicGas.class ) );
								break;
							case 2:
								GameScene.add( Blob.seed( bolt.collisionPos, 400, Inferno.class));
								break;
							case 3:
								GameScene.add( Blob.seed( bolt.collisionPos, 300, Blizzard.class));
								break;
							case 4:
								GameScene.add( Blob.seed( bolt.collisionPos, 200, ParalyticGas.class ) );
								break;
						}
					}
				});
				break;
		}

	}

	private static void uncommonEffect(final Wand wand, final Hero user, final Ballistica bolt){
		switch(Random.Int(4)){

			//Random plant
			case 0:
				cursedFX(user, bolt, new Callback() {
					public void call() {
						int pos = bolt.collisionPos;
						//place the plant in front of an enemy so they walk into it.
						if (Actor.findChar(pos) != null && bolt.dist > 1) {
							pos = bolt.path.get(bolt.dist - 1);
						}

						if (pos == Terrain.EMPTY ||
								pos == Terrain.EMBERS ||
								pos == Terrain.EMPTY_DECO ||
								pos == Terrain.GRASS ||
								pos == Terrain.HIGH_GRASS ||
								pos == Terrain.FURROWED_GRASS) {
							Dungeon.level.plant((Plant.Seed) Generator.random(Generator.Category.SEED), pos);
						}
					}
				});
				break;

			//Health transfer
			case 1:
				final Char target = Actor.findChar( bolt.collisionPos );
				if (target != null) {
					cursedFX(user, bolt, new Callback() {
						public void call() {
							int damage = user.lvl * 2;
							switch (Random.Int(2)) {
								case 0:
									user.HP = Math.min(user.HT, user.HP + damage);
									user.sprite.emitter().burst(Speck.factory(Speck.HEALING), 3);
									target.damage(damage, wand);
									target.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10);
									break;
								case 1:
									user.damage( damage, this );
									user.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10);
									target.HP = Math.min(target.HT, target.HP + damage);
									target.sprite.emitter().burst(Speck.factory(Speck.HEALING), 3);
									Sample.INSTANCE.play(Assets.SND_CURSED);
									if (!user.isAlive()) {
										Dungeon.fail( wand.getClass() );
										GLog.n(Messages.get(CursedWand.class, "ondeath", wand.name()));
									}
									break;
							}
						}
					});
				} else {
					GLog.i(Messages.get(CursedWand.class, "nothing"));
				}
				break;

			//Bomb explosion
			case 2:
				cursedFX(user, bolt, new Callback() {
					public void call() {
						new Bomb().explode(bolt.collisionPos);
					}
				});
				break;

			//shock and recharge
			case 3:
				new StormTrap().set( user.pos ).activate();
				break;
		}

	}

	private static void rareEffect(final Wand wand, final Hero user, final Ballistica bolt){
		switch(Random.Int(4)){

			//sheep transformation
			case 0:
				cursedFX(user, bolt, new Callback() {
					public void call() {
						Char ch = Actor.findChar( bolt.collisionPos );

						if (ch != null && ch != user
								&& !ch.properties().contains(Char.Property.BOSS)
								&& !ch.properties().contains(Char.Property.MINIBOSS)){
							Sheep sheep = new Sheep();
							sheep.lifespan = 10;
							sheep.pos = ch.pos;
							ch.destroy();
							ch.sprite.killAndErase();
							Dungeon.level.mobs.remove(ch);
							TargetHealthIndicator.instance.target(null);
							GameScene.add(sheep);
							CellEmitter.get(sheep.pos).burst(Speck.factory(Speck.WOOL), 4);
						} else {
							GLog.i(Messages.get(CursedWand.class, "nothing"));
						}
					}
				});
				break;

			//curses!
			case 1:
				CursingTrap.curse(user);
				break;

			//inter-level teleportation
			case 2:
				if (Dungeon.depth > 1 && !Dungeon.bossLevel()) {

					//each depth has 1 more weight than the previous depth.
					float[] depths = new float[Dungeon.depth-1];
					for (int i = 1; i < Dungeon.depth; i++) depths[i-1] = i;
					int depth = 1+Random.chances(depths);

					Buff buff = Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class);
					if (buff != null) buff.detach();

					InterlevelScene.mode = InterlevelScene.Mode.RETURN;
					InterlevelScene.returnDepth = depth;
					InterlevelScene.returnPos = -1;
					Game.switchScene(InterlevelScene.class);

				} else {
					ScrollOfTeleportation.teleportHero(user);

				}
				break;

			//summon monsters
			case 3:
				new SummoningTrap().set( user.pos ).activate();
				break;
		}
	}

	private static void veryRareEffect(final Wand wand, final Hero user, final Ballistica bolt){
		switch(Random.Int(4)){

			//great forest fire!
			case 0:
				new ArmageddonTrap().set(Dungeon.hero.pos).activate();
				break;

			//superpowered mimic
			case 1:
				cursedFX(user, bolt, new Callback() {
					public void call() {
						Mimic mimic = Mimic.spawnAt(bolt.collisionPos, new ArrayList<Item>());
						if (mimic != null) {
							mimic.adjustStats(Dungeon.depth + 10);
							mimic.HP = mimic.HT;
							Item reward;
							do {
								reward = Generator.random(Random.oneOf(Generator.Category.WEAPON, Generator.Category.ARMOR,
										Generator.Category.RING, Generator.Category.WAND));
							} while (reward.level() < 1);
							Sample.INSTANCE.play(Assets.SND_MIMIC, 1, 1, 0.5f);
							mimic.items.clear();
							mimic.items.add(reward);
						} else {
							GLog.i(Messages.get(CursedWand.class, "nothing"));
						}
					}
				});
				break;

			//crashes the game, yes, really.
			case 2:
				try {
					Dungeon.saveAll();
					if(Messages.lang() != Languages.ENGLISH){
						//Don't bother doing this joke to none-english speakers, I doubt it would translate.
						GLog.i(Messages.get(CursedWand.class, "nothing"));
					} else {
						GameScene.show(
								new WndOptions("CURSED WAND ERROR", "this application will now self-destruct", "abort", "retry", "fail") {
									
									@Override
									protected void onSelect(int index) {
										Game.instance.finish();
									}
									
									@Override
									public void onBackPressed() {
										//do nothing
									}
								}
						);
					}
				} catch(IOException e){
					LustrousPixelDungeon.reportException(e);
					//oookay maybe don't kill the game if the save failed.
					GLog.i(Messages.get(CursedWand.class, "nothing"));
				}
				break;

			//random transmogrification
			case 3:
				wand.detach(user.belongings.backpack);
				Item result;
				do {
					result = Generator.random(Random.oneOf(Generator.Category.WEAPON, Generator.Category.ARMOR,
							Generator.Category.RING, Generator.Category.ARTIFACT, Generator.Category.MISSILE));
				} while (result == null);
				result.quantity(1).upgrade(); // who cares if you can't upgrade artifacts normally....
				result.cursed = result.cursedKnown = true;
				GLog.w( Messages.get(CursedWand.class, "transmogrify") );
				result.drop(user.pos);
				break;
		}
	}

	private static void cursedFX(final Hero user, final Ballistica bolt, final Callback callback){
		MagicMissile.boltFromChar( user.sprite.parent,
				MagicMissile.RAINBOW,
				user.sprite,
				bolt.collisionPos,
				callback);
		Sample.INSTANCE.play( Assets.SND_ZAP );
	}

}
