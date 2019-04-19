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

package com.zrp200.lustrouspixeldungeon.actors.mobs.npcs;

import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Assets;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.LustrousPixelDungeon;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Paralysis;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Roots;
import com.zrp200.lustrouspixeldungeon.actors.mobs.FetidRat;
import com.zrp200.lustrouspixeldungeon.actors.mobs.GnollTrickster;
import com.zrp200.lustrouspixeldungeon.actors.mobs.GreatCrab;
import com.zrp200.lustrouspixeldungeon.actors.mobs.Mob;
import com.zrp200.lustrouspixeldungeon.effects.CellEmitter;
import com.zrp200.lustrouspixeldungeon.effects.Speck;
import com.zrp200.lustrouspixeldungeon.items.Generator;
import com.zrp200.lustrouspixeldungeon.items.armor.Armor;
import com.zrp200.lustrouspixeldungeon.items.armor.LeatherArmor;
import com.zrp200.lustrouspixeldungeon.items.armor.MailArmor;
import com.zrp200.lustrouspixeldungeon.items.armor.PlateArmor;
import com.zrp200.lustrouspixeldungeon.items.armor.ScaleArmor;
import com.zrp200.lustrouspixeldungeon.items.weapon.Weapon;
import com.zrp200.lustrouspixeldungeon.items.weapon.melee.MeleeWeapon;
import com.zrp200.lustrouspixeldungeon.items.weapon.melee.Shortsword;
import com.zrp200.lustrouspixeldungeon.journal.Notes;
import com.zrp200.lustrouspixeldungeon.levels.SewerLevel;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.scenes.GameScene;
import com.zrp200.lustrouspixeldungeon.sprites.FetidRatSprite;
import com.zrp200.lustrouspixeldungeon.sprites.GhostSprite;
import com.zrp200.lustrouspixeldungeon.sprites.GnollTricksterSprite;
import com.zrp200.lustrouspixeldungeon.sprites.GreatCrabSprite;
import com.zrp200.lustrouspixeldungeon.utils.GLog;
import com.zrp200.lustrouspixeldungeon.windows.WndQuest;
import com.zrp200.lustrouspixeldungeon.windows.WndSadGhost;

public class Ghost extends Noncombatant {

	{
		spriteClass = GhostSprite.class;
		
		flying = true;
		
		state = WANDERING;
	}
	
	public Ghost() {
		super();
		Sample.INSTANCE.load( Assets.SND_GHOST );
	}

	@Override
	protected boolean act() {
		if (Quest.processed())
			target = Dungeon.hero.pos;
		return super.act();
	}

	@Override
	public float speed() {
		return Quest.processed() ? 2f : 1/2f;
	}


	@Override
	public boolean interact() {
		sprite.turnTo( pos, Dungeon.hero.pos );
		
		Sample.INSTANCE.play( Assets.SND_GHOST );
		
		if (Quest.given) {
			if (Quest.weapon == null)
				return false;

			if (Quest.processed) {
				GameScene.show(new WndSadGhost(this, Quest.current));
			} else {
				GameScene.show(new WndQuest(this, Messages.get(this, Quest.current.name +"_2")));

				int newPos = -1;
				for (int i = 0; i < 10; i++) {
					newPos = Dungeon.level.randomRespawnCell();
					if (newPos != -1) {
						break;
					}
				}

				if (newPos == -1) {
					return false;
				}

				CellEmitter.get(pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
				pos = newPos;
				sprite.place(pos);
				sprite.visible = Dungeon.level.heroFOV[pos];
			}
		} else if (Quest.createBoss().pos != -1) {
			GameScene.show( new WndQuest( this,
					Messages.get(this, Quest.current +"_1", Dungeon.hero.givenName()) ) );
			Quest.given = true;
			Notes.add( Notes.Landmark.GHOST );
		}

		return false;
	}
	
	{
		immunities.add( Paralysis.class );
		immunities.add( Roots.class );
	}

	public enum Quest {
		RAT("rat", FetidRat.class, new FetidRatSprite()),
		GNOLL("gnoll", GnollTrickster.class, new GnollTricksterSprite()),
		CRAB("crab", GreatCrab.class, new GreatCrabSprite());

		public final String name;
		public final Class<?extends Mob> bossClass;
		public final Image icon;

		Quest(String name, Class<?extends Mob> bossClass, Image icon) {
			this.name = name;
			this.bossClass = bossClass;
			this.icon = icon;
		}
		
		private static boolean spawned;

		private static Quest current;

		public static Mob createBoss() {
			Mob questBoss;
			try {
				questBoss = current.bossClass.newInstance();
				return questBoss;
			} catch (Exception e) {
				LustrousPixelDungeon.reportException(e);
				questBoss = new FetidRat();
			}
			questBoss.pos = Dungeon.level.randomRespawnCell();
			return questBoss;
		}

		private static boolean given;
		private static boolean processed;
		
		private static int depth;
		
		public static Weapon weapon;
		public static Armor armor;
		
		public static void reset() {
			spawned = false;
			
			weapon = null;
			armor = null;
		}
		
		private static final String NODE		= "sadGhost";
		
		private static final String SPAWNED		= "spawned";
		private static final String TYPE        = "current";
		private static final String GIVEN		= "given";
		private static final String PROCESSED	= "processed";
		private static final String DEPTH		= "depth";
		private static final String WEAPON		= "weapon";
		private static final String ARMOR		= "armor";
		
		public static void storeInBundle( Bundle bundle ) {
			
			Bundle node = new Bundle();
			
			node.put( SPAWNED, spawned );
			
			if (spawned) {
				
				node.put( TYPE, current);
				
				node.put( GIVEN, given );
				node.put( DEPTH, depth );
				node.put( PROCESSED, processed);
				
				node.put( WEAPON, weapon );
				node.put( ARMOR, armor );
			}
			
			bundle.put( NODE, node );
		}
		
		public static void restoreFromBundle( Bundle bundle ) {
			
			Bundle node = bundle.getBundle( NODE );

			if (!node.isNull() && (spawned = node.getBoolean( SPAWNED ))) {

				current = node.getEnum(TYPE, Quest.class);
				given	= node.getBoolean( GIVEN );
				processed = node.getBoolean( PROCESSED );

				depth	= node.getInt( DEPTH );
				
				weapon	= (Weapon)node.get( WEAPON );
				armor	= (Armor)node.get( ARMOR );
			} else {
				reset();
			}
		}
		
		public static void spawn( SewerLevel level ) {
			if (!spawned && Dungeon.depth > 1 && Random.Int( 5 - Dungeon.depth ) == 0) {
				
				Ghost ghost = new Ghost();
				do {
					ghost.pos = level.randomRespawnCell();
				} while (ghost.pos == -1);
				level.mobs.add( ghost );
				
				spawned = true;
				//dungeon depth determines current of quest.
				//depth 2=fetid rat, 3=gnoll trickster, 4=great crab
				switch(Dungeon.depth) {
					case 2: default:
						current = RAT;
						break;
					case 3:
						current = GNOLL;
						break;
					case 4:
						current = CRAB;
						break;
				}
				
				given = false;
				processed = false;
				depth = Dungeon.depth;

				//50%:tier2, 30%:tier3, 15%:tier4, 5%:tier5
				float itemTierRoll = Random.Float();
				int wepTier;

				if (itemTierRoll < 0.5f) {
					wepTier = 2;
					armor = new LeatherArmor();
				} else if (itemTierRoll < 0.8f) {
					wepTier = 3;
					armor = new MailArmor();
				} else if (itemTierRoll < 0.95f) {
					wepTier = 4;
					armor = new ScaleArmor();
				} else {
					wepTier = 5;
					armor = new PlateArmor();
				}

				try {
					do {
						weapon = (Weapon) Generator.wepTiers[wepTier - 1].classes[Random.chances(Generator.wepTiers[wepTier - 1].probs)].newInstance();
					} while (!(weapon instanceof MeleeWeapon));
				} catch (Exception e){
					LustrousPixelDungeon.reportException(e);
					weapon = new Shortsword();
				}

				//50%:+0, 30%:+1, 15%:+2, 5%:+3
				float itemLevelRoll = Random.Float();
				int itemLevel;
				if (itemLevelRoll < 0.5f){
					itemLevel = 0;
				} else if (itemLevelRoll < 0.8f){
					itemLevel = 1;
				} else if (itemLevelRoll < 0.95f){
					itemLevel = 2;
				} else {
					itemLevel = 3;
				}
				weapon.upgrade(itemLevel);
				armor.upgrade(itemLevel);

				//10% to be enchanted
				if (Random.Int(10) == 0){
					weapon.enchant();
					armor.inscribe();
				}

			}
		}
		
		public static void process() {
			if (spawned && given && !processed && (depth == Dungeon.depth)) {
				GLog.n( Messages.get(Ghost.class, "find_me") );
				Sample.INSTANCE.play( Assets.SND_GHOST );
				processed = true;
			}
		}
		
		public static void complete() {
			weapon = null;
			armor = null;
			
			Notes.remove( Notes.Landmark.GHOST );
		}

		public static boolean processed(){
			return spawned && processed;
		}
		
		public static boolean completed(){
			return processed() && weapon == null && armor == null;
		}
	}
}
