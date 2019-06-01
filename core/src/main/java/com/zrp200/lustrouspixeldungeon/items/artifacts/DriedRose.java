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

package com.zrp200.lustrouspixeldungeon.items.artifacts;

import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Assets;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Actor;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.blobs.CorrosiveGas;
import com.zrp200.lustrouspixeldungeon.actors.blobs.ToxicGas;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Burning;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Corruption;
import com.zrp200.lustrouspixeldungeon.actors.buffs.LockedFloor;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.actors.mobs.Mob;
import com.zrp200.lustrouspixeldungeon.actors.mobs.Wraith;
import com.zrp200.lustrouspixeldungeon.actors.mobs.npcs.Ghost;
import com.zrp200.lustrouspixeldungeon.actors.mobs.npcs.NPC;
import com.zrp200.lustrouspixeldungeon.effects.CellEmitter;
import com.zrp200.lustrouspixeldungeon.effects.Speck;
import com.zrp200.lustrouspixeldungeon.effects.particles.ShaftParticle;
import com.zrp200.lustrouspixeldungeon.items.Item;
import com.zrp200.lustrouspixeldungeon.items.armor.Armor;
import com.zrp200.lustrouspixeldungeon.items.scrolls.ScrollOfRetribution;
import com.zrp200.lustrouspixeldungeon.items.scrolls.exotic.ScrollOfPsionicBlast;
import com.zrp200.lustrouspixeldungeon.items.weapon.melee.BlockingWeapon;
import com.zrp200.lustrouspixeldungeon.items.weapon.melee.MeleeWeapon;
import com.zrp200.lustrouspixeldungeon.levels.Level;
import com.zrp200.lustrouspixeldungeon.messages.Languages;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.scenes.GameScene;
import com.zrp200.lustrouspixeldungeon.scenes.PixelScene;
import com.zrp200.lustrouspixeldungeon.sprites.GhostSprite;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSprite;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSpriteSheet;
import com.zrp200.lustrouspixeldungeon.ui.RenderedTextMultiline;
import com.zrp200.lustrouspixeldungeon.ui.Window;
import com.zrp200.lustrouspixeldungeon.utils.GLog;
import com.zrp200.lustrouspixeldungeon.windows.IconTitle;
import com.zrp200.lustrouspixeldungeon.windows.WndBag;
import com.zrp200.lustrouspixeldungeon.windows.WndBlacksmith;
import com.zrp200.lustrouspixeldungeon.windows.WndItem;
import com.zrp200.lustrouspixeldungeon.windows.WndQuest;

import java.util.ArrayList;

public class DriedRose extends Artifact {

	{
		image = ItemSpriteSheet.ARTIFACT_ROSE1;

		levelCap = 10;

		charge = 100;
		chargeCap = 100;

		defaultAction = AC_SUMMON;
	}

	private boolean talkedTo = false;
	private boolean firstSummon = false;
	
	private GhostHero ghost = null;
	private int ghostID = 0;
	
	private MeleeWeapon weapon = null;
	private Armor armor = null;

	public int droppedPetals = 0;

	public static final String AC_SUMMON = "SUMMON";
	public static final String AC_OUTFIT = "OUTFIT";

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (!Ghost.Quest.completed()){
			actions.remove(AC_EQUIP);
			return actions;
		}
		if (isEquipped( hero ) && charge == chargeCap && !cursed) {
			actions.add(AC_SUMMON);
		}
		if (isIdentified() && !cursed){
			actions.add(AC_OUTFIT);
		}
		
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {

		super.execute(hero, action);

		if (action.equals(AC_SUMMON)) {

			if (!Ghost.Quest.completed())   GameScene.show(new WndItem(null, this, true));
			else if (ghost != null)         GLog.i( Messages.get(this, "spawned") );
			else if (!isEquipped( hero ))   GLog.i( Messages.get(Artifact.class, "need_to_equip") );
			else if (charge != chargeCap)   GLog.i( Messages.get(this, "no_charge") );
			else if (cursed)                GLog.i( Messages.get(this, "cursed") );
			else {
				ArrayList<Integer> spawnPoints = new ArrayList<Integer>();
				for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
					int p = hero.pos + PathFinder.NEIGHBOURS8[i];
					if (Actor.findChar(p) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
						spawnPoints.add(p);
					}
				}

				if (spawnPoints.size() > 0) {
					ghost = new GhostHero( this );
					ghostID = ghost.id();
					ghost.pos = Random.element(spawnPoints);

					GameScene.add(ghost, 1f);
					CellEmitter.get(ghost.pos).start( ShaftParticle.FACTORY, 0.3f, 4 );
					CellEmitter.get(ghost.pos).start( Speck.factory(Speck.LIGHT), 0.2f, 3 );

					hero.spend(1f);
					hero.busy();
					hero.sprite.operate(hero.pos);

					if (!firstSummon) {
						ghost.yell( Messages.get(GhostHero.class, "hello", Dungeon.hero.givenName()) );
						Sample.INSTANCE.play( Assets.SND_GHOST );
						firstSummon = true;
					} else
						ghost.saySpawned();
					
					charge = 0;
					updateQuickslot();

				} else
					GLog.i( Messages.get(this, "no_space") );
			}

		} else if (action.equals(AC_OUTFIT)){
			GameScene.show( new WndGhostHero(this) );
		}
	}
	
	public int ghostStrength(){
		return 13 + level()/2;
	}

	@Override
	public String desc() {
		if (!Ghost.Quest.completed() && !isIdentified()){
			return Messages.get(this, "desc_no_quest");
		}
		
		String desc = super.desc();

		if (isEquipped( Dungeon.hero )){
			if (!cursed){

				if (level() < levelCap)
					desc+= "\n\n" + Messages.get(this, "desc_hint");

			} else
				desc += "\n\n" + Messages.get(this, "desc_cursed");
		}

		return desc;
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new roseRecharge();
	}
	
	@Override
	public void charge(Hero target) {
		if (ghost == null && charge < chargeCap){
			partialCharge += 0.25f;
			if (partialCharge >= 1){
				partialCharge--;
				charge++;
				updateQuickslot();
				if (charge == chargeCap){
					partialCharge = 0f;
					GLog.p( Messages.get(DriedRose.class, "charged") );
				}
			}
		}
	}
	
	@Override
	public Item upgrade() {
		if (level() >= 9)
			image = ItemSpriteSheet.ARTIFACT_ROSE3;
		else if (level() >= 4)
			image = ItemSpriteSheet.ARTIFACT_ROSE2;

		//For upgrade transferring via well of transmutation
		droppedPetals = Math.max( level(), droppedPetals );
		
		if (ghost != null){
			ghost.updateRose();
		}

		return super.upgrade();
	}

	private static final String TALKEDTO =      "talkedto";
	private static final String FIRSTSUMMON =   "firstsummon";
	private static final String GHOSTID =       "ghostID";
	private static final String PETALS =        "petals";
	
	private static final String WEAPON =        "weapon";
	private static final String ARMOR =         "armor";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);

		bundle.put( TALKEDTO, talkedTo );
		bundle.put( FIRSTSUMMON, firstSummon );
		bundle.put( GHOSTID, ghostID );
		bundle.put( PETALS, droppedPetals );
		
		if (weapon != null) bundle.put( WEAPON, weapon );
		if (armor != null)  bundle.put( ARMOR, armor );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);

		talkedTo = bundle.getBoolean( TALKEDTO );
		firstSummon = bundle.getBoolean( FIRSTSUMMON );
		ghostID = bundle.getInt( GHOSTID );
		droppedPetals = bundle.getInt( PETALS );
		
		if (bundle.contains(WEAPON)) weapon = (MeleeWeapon)bundle.get( WEAPON );
		if (bundle.contains(ARMOR))  armor = (Armor)bundle.get( ARMOR );
	}
	
	// *** static methods for transferring a ghost hero between floors ***
	
	private static GhostHero heldGhost;
	
	public static void holdGhostHero( Level level ){
		for (Mob mob : level.mobs.toArray( new Mob[0] )) {
			if (mob instanceof DriedRose.GhostHero) {
				level.mobs.remove( mob );
				heldGhost = (GhostHero) mob;
				break;
			}
		}
	}
	
	public static void restoreGhostHero( Level level, int pos ){
		if (heldGhost != null){
			level.mobs.add( heldGhost );
			
			int ghostPos;
			do {
				ghostPos = pos + PathFinder.NEIGHBOURS8[Random.Int(8)];
			} while (Dungeon.level.solid[ghostPos] || level.findMob(ghostPos) != null);
			
			heldGhost.pos = ghostPos;
			heldGhost = null;
		}
	}
	
	public static void clearHeldGhostHero(){
		heldGhost = null;
	}

	public class roseRecharge extends ArtifactBuff {

		@Override
		public boolean act() {
			
			spend( TICK );
			
			if (ghost == null && ghostID != 0){
				Actor a = Actor.findById(ghostID);
				if (a != null){
					ghost = (GhostHero)a;
				} else {
					ghostID = 0;
				}
			}
			
			//rose does not charge while ghost hero is alive
			if (ghost != null){
				return true;
			}
			
			LockedFloor lock = target.buff(LockedFloor.class);
			if ( lock == null || lock.regenOn() ) {
				gainCharge(1/5f );
			} else if (cursed && Random.Int(100) == 0) {
				ArrayList<Integer> spawnPoints = new ArrayList<Integer>();

				for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
					int p = target.pos + PathFinder.NEIGHBOURS8[i];
					if (Actor.findChar(p) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
						spawnPoints.add(p);
					}
				}

				if (spawnPoints.size() > 0) {
					Wraith.spawnAt(Random.element(spawnPoints));
					Sample.INSTANCE.play(Assets.SND_CURSED);
				}

			}

			updateQuickslot();

			return true;
		}
	}

	public static class Petal extends Item {

		{
			stackable = true;
			image = ItemSpriteSheet.PETAL;
			unique = true;
		}

		@Override
		public boolean doPickUp( Hero hero ) {
			DriedRose rose = hero.belongings.getItem( DriedRose.class );

			if (rose == null){
				GLog.w( Messages.get(this, "no_rose") );
				return false;
			} if ( rose.level() >= rose.levelCap ){
				GLog.i( Messages.get(this, "no_room") );
				hero.spendAndNext(TIME_TO_PICK_UP);
				return true;
			} else {

				rose.upgrade();
				if (rose.level() == rose.levelCap) {
					GLog.p( Messages.get(this, "maxlevel") );
				} else
					GLog.i( Messages.get(this, "levelup") );

				Sample.INSTANCE.play( Assets.SND_DEWDROP );
				hero.spendAndNext(TIME_TO_PICK_UP);
				return true;

			}
		}

	}

	public static class GhostHero extends NPC {

		{
			spriteClass = GhostSprite.class;

			flying = true;

			alignment = Alignment.ALLY;
			
			WANDERING = new Wandering();
			
			state = HUNTING;
			
			//before other mobs
			actPriority = MOB_PRIO + 1;
			
			properties.add(Property.UNDEAD);
		}
		
		private DriedRose rose = null;
		
		@SuppressWarnings("unused")
		public GhostHero(){
			super();
		}

		public boolean canSurpriseAttack() {
		    return rose.weapon != null && rose.weapon.canSurpriseAttack();
        }

		public GhostHero(DriedRose rose){
			super();
			this.rose = rose;
			updateRose();
			HP = HT;
		}
		
		private void updateRose(){
			if (rose == null) {
				rose = Dungeon.hero.belongings.getItem(DriedRose.class);
			}
			
			defenseSkill = Hero.EVASION*2;
			if (rose == null) return;
			HT = 20 + 4*rose.level();
		}

		public void saySpawned(){
			if (Messages.lang() != Languages.ENGLISH) return; //don't say anything if not on english
			int i = (Dungeon.depth - 1) / 5;
			fieldOfView = new boolean[Dungeon.level.length()];
			Dungeon.level.updateFieldOfView(this, fieldOfView);
			if (chooseEnemy() == null)
				yell( Random.element( VOICE_AMBIENT[i] ) );
			else
				yell( Random.element( VOICE_ENEMIES[i][ Dungeon.bossLevel() ? 1 : 0 ] ) );
			Sample.INSTANCE.play( Assets.SND_GHOST );
		}

		public void sayAnhk(){
			yell( Random.element( VOICE_BLESSEDANKH ) );
			Sample.INSTANCE.play( Assets.SND_GHOST );
		}

		public void sayDefeated(){
			if (Messages.lang() != Languages.ENGLISH) return; //don't say anything if not on english
			yell( Random.element( VOICE_DEFEATED[ Dungeon.bossLevel() ? 1 : 0 ] ) );
			Sample.INSTANCE.play( Assets.SND_GHOST );
		}

		public void sayHeroKilled(){
			if (Messages.lang() != Languages.ENGLISH) return; //don't say anything if not on english
			yell(Random.element(VOICE_HEROKILLED));
			Sample.INSTANCE.play( Assets.SND_GHOST );
		}

		public void sayBossBeaten(){
			yell( Random.element( VOICE_BOSSBEATEN[ Dungeon.depth==25 ? 1 : 0 ] ) );
			Sample.INSTANCE.play( Assets.SND_GHOST );
		}

		@Override
		protected boolean act() {
			updateRose();
			if (rose == null || !rose.isEquipped(Dungeon.hero)){
				damage(1, this);
			}
			
			if (!isAlive())
				return true;
			if (!Dungeon.hero.isAlive()){
				sayHeroKilled();
				sprite.die();
				destroy();
				return true;
			}
			return super.act();
		}
		
		@Override
		protected Char chooseEnemy(boolean newEnemy) {
			Char enemy = super.chooseEnemy(newEnemy);
			
			//will never attack something far from the player
			if (enemy != null && Dungeon.level.mobs.contains(enemy)
					&& Dungeon.level.distance(enemy.pos, Dungeon.hero.pos) <= 8){
				return enemy;
			} else {
				return null;
			}
		}

		@Override
		public int attackSkill(Char target) {

			//same accuracy as the hero.
			int acc = Dungeon.hero.lvl + Hero.ACCURACY;
			
			if (rose != null && rose.weapon != null){
				acc *= rose.weapon.accuracyFactor(this);
			}
			
			return acc;
		}
		
		@Override
		public float attackDelay() {
			float delay = super.attackDelay();
			if (rose != null && rose.weapon != null){
				delay *= rose.weapon.speedFactor(this);
			}
			return delay;
		}
		
		@Override
		protected boolean canAttack(Char enemy) {
			return super.canAttack(enemy) || (rose != null && rose.weapon != null && rose.weapon.canReach(this, enemy.pos));
		}
		
		@Override
		public int damageRoll() {
			return rose != null && rose.weapon != null ? rose.weapon.damageRoll(this) : Random.NormalInt(5);
		}
		
		@Override
		public int attackProc(Char enemy, int damage) {
			damage = super.attackProc(enemy, damage);
			if (rose != null && rose.weapon != null) {
				damage = rose.weapon.proc( this, enemy, damage );
			}
			return damage;
		}
		
		@Override
		public int defenseProc(Char enemy, int damage) {
			if (rose != null && rose.armor != null) {
				return rose.armor.proc( enemy, this, damage );
			} else {
				return super.defenseProc(enemy, damage);
			}
		}

		@Override
		public float speed() {
			float speed = super.speed();
			
			if (rose != null && rose.armor != null){
				speed = rose.armor.speedFactor(this, speed);
			}
			
			return speed;
		}
		
		@Override
		public int defenseSkill(Char enemy) {
			int defense = super.defenseSkill(enemy);

			if (defense != 0 && rose != null && rose.armor != null ){
				defense = Math.round(rose.armor.evasionFactor( this, defense ));
			}
			
			return defense;
		}
		
		@Override
		public float stealth() {
			float stealth = super.stealth();
			
			if (rose != null && rose.armor != null){
				stealth = rose.armor.stealthFactor(this, stealth);
			}
			
			return stealth;
		}
		
		@Override
		public int drRoll() {
			int block = 0;
			if (rose != null && rose.armor != null){
				block += rose.armor.DRRoll();
			}
			if (rose != null && rose.weapon instanceof BlockingWeapon){
				block += rose.weapon.defenseFactor( this );
			}
			return block;
		}

		@Override
		public boolean interact() {
			updateRose();
			if (rose != null && !rose.talkedTo){
				rose.talkedTo = true;
				GameScene.show(new WndQuest(this, Messages.get(this, "introduce") ));
				return false;
			} else if (Dungeon.level.passable[pos] || Dungeon.hero.flying) {
				int curPos = pos;

				moveSprite( pos, Dungeon.hero.pos );
				move( Dungeon.hero.pos );

				Dungeon.hero.sprite.move( Dungeon.hero.pos, curPos );
				Dungeon.hero.move( curPos );

				Dungeon.hero.spend( 1 / Dungeon.hero.speed() );
				Dungeon.hero.busy();
				return true;
			} else {
				return false;
			}
		}

		@Override
		public void die(Object cause) {
			sayDefeated();
			super.die(cause);
		}

		@Override
		public void destroy() {
			updateRose();
			if (rose != null) {
				rose.ghost = null;
				rose.ghostID = -1;
			}
			super.destroy();
		}
		
		{
			immunities.add( ToxicGas.class );
			immunities.add( CorrosiveGas.class );
			immunities.add( Burning.class );
			immunities.add( ScrollOfRetribution.class );
			immunities.add( ScrollOfPsionicBlast.class );
			immunities.add( Corruption.class );
		}
		
		private class Wandering extends Mob.Wandering {
			
			@Override
			public boolean act( boolean justAlerted ) {
				if ( enemyInFOV() ) {
					
					enemySeen = true;
					
					notice();
					alerted = true;
					state = HUNTING;
					target = enemy.pos;
					
				} else {
					
					enemySeen = false;
					
					int oldPos = pos;
					//always move towards the hero when wandering
					if (getCloser( target = Dungeon.hero.pos )) {
						//moves 2 tiles at a time when returning to the hero from a distance
						if (!Dungeon.level.adjacent(Dungeon.hero.pos, pos)){
							getCloser( target = Dungeon.hero.pos );
						}
						spend( 1 / speed() );
						return moveSprite( oldPos, pos );
					} else {
						spend( TICK );
					}
					
				}
				return true;
			}
			
		}

		//************************************************************************************
		//This is a bunch strings & string arrays, used in all of the sad ghost's voice lines.
		//************************************************************************************

		private static final String VOICE_INTRODUCE = "My spirit is bound to this rose, it was very precious to me, a "+
			"gift from my love whom I left on the surface.\n\nI cannot return to him, but thanks to you I have a " +
			"second chance to complete my journey. When I am able I will respond to your call and fight with you.\n\n" +
			"hopefully you may succeed where I failed...";

		//1st index - depth type, 2nd index - specific line.
		public static final String[][] VOICE_AMBIENT = {
			{
					"These sewers were once safe, some even lived here in the winter...",
					"I wonder what happened to the guard patrols, did they give up?...",
					"I had family on the surface, I hope they are safe..."
			},{
					"I've heard stories about this place, nothing good...",
					"This place was always more of a dungeon than a prison...",
					"I can't imagine what went on when this place was abandoned..."
			},{
					"No human or dwarf has been here for a very long time...",
					"Something must have gone very wrong, for the dwarves to abandon a gold mine...",
					"I feel great evil lurking below..."
			},{
					"The dwarves were industrious, but greedy...",
					"I hope the surface never ends up like this place...",
					"So the dwarvern metropolis really has fallen..."
			},{
					"What is this place?...",
					"So the stories are true, we have to fight a demon god...",
					"I feel a great evil in this place..."
			},{
					"... I don't like this place... We should leave as soon as possible..."
			}
		};

		//1st index - depth type, 2nd index - boss or not, 3rd index - specific line.
		public static final String[][][] VOICE_ENEMIES = {
			{
				{
					"Let's make the sewers safe again...",
					"If the guards couldn't defeat them, perhaps we can...",
					"These crabs are extremely annoying..."
				},{
					"Beware Goo!...",
					"Many of my friends died to this thing, time for vengeance...",
					"Such an abomination cannot be allowed to live..."
				}
			},{
				{
					"What dark magic happened here?...",
					"To think the captives of this place are now its guardians...",
					"They were criminals before, now they are monsters..."
				},{
					"If only he would see reason, he doesn't seem insane...",
					"He assumes we are hostile, if only he would stop to talk...",
					"The one prisoner left sane is a deadly assassin. Of course..."
				}
			},{
				{
					"The creatures here are twisted, just like the sewers... ",
					"more gnolls, I hate gnolls...",
					"Even the bats are bloodthirsty here..."
				},{
					"Only dwarves would build a mining machine that kills looters...",
					"That thing is huge...",
					"How has it survived here for so long?..."
				}
			},{
				{
					"Dwarves aren't supposed to look that pale...",
					"I don't know what's worse, the dwarves, or their creations...",
					"They all obey their master without question, even now..."
				},{
					"When people say power corrupts, this is what they mean...",
					"He's more a Lich than a King now...",
					"Looks like he's more demon than dwarf now..."
				}
			},{
				{
					"What the heck is that thing?...",
					"This place is terrifying...",
					"What were the dwarves thinking, toying with power like this?..."
				},{
					"Oh.... this doesn't look good...",
					"So that's what a god looks like?...",
					"This is going to hurt..."
				}
			},{
				{
					"I don't like this place... we should leave as soon as we can..."
				},{
					"Hello source viewer, I'm writing this here as this line should never trigger. Have a nice day!"
				}
			}
		};

		//1st index - Yog or not, 2nd index - specific line.
		public static final String[][] VOICE_BOSSBEATEN = {
			{
					"Yes!",
					"Victory!"
			},{
					"It's over... we won...",
					"I can't believe it... We just killed a god..."
			}
		};

		//1st index - boss or not, 2nd index - specific line.
		public static final String[][] VOICE_DEFEATED = {
			{
					"Good luck...",
					"I will return...",
					"Tired... for now..."
			},{
					"No... I can't....",
					"I'm sorry.. good luck..",
					"Finish it off... without me..."
			}
		};

		public static final String[] VOICE_HEROKILLED = {
					"nooo...",
					"no...",
					"I couldn't help them..."
		};

		public static final String[] VOICE_BLESSEDANKH = {
					"Incredible!...",
					"Wish I had one of those...",
					"How did you survive that?..."
		};
	}
	
	private static class WndGhostHero extends Window{
		
		private static final int BTN_SIZE	= 32;
		private static final float GAP		= 2;
		private static final float BTN_GAP	= 12;
		private static final int WIDTH		= 116;
		
		private WndBlacksmith.ItemButton btnWeapon;
		private WndBlacksmith.ItemButton btnArmor;
		
		WndGhostHero(final DriedRose rose){
			
			IconTitle titlebar = new IconTitle();
			titlebar.icon( new ItemSprite(rose) );
			titlebar.label( Messages.get(this, "title") );
			titlebar.setRect( 0, 0, WIDTH, 0 );
			add( titlebar );
			
			RenderedTextMultiline message =
					PixelScene.renderMultiline(Messages.get(this, "desc", rose.ghostStrength()), 6);
			message.maxWidth( WIDTH );
			message.setPos(0, titlebar.bottom() + GAP);
			add( message );
			
			btnWeapon = new WndBlacksmith.ItemButton(){
				@Override
				protected void onClick() {
					if (rose.weapon != null){
						item(new WndBag.Placeholder(ItemSpriteSheet.WEAPON_HOLDER));
						if (!rose.weapon.doPickUp(Dungeon.hero)){
							Dungeon.level.drop( rose.weapon, Dungeon.hero.pos);
						}
						rose.weapon = null;
					} else {
						GameScene.selectItem(new WndBag.Listener() {
							@Override
							public void onSelect(Item item) {
								if (!(item instanceof MeleeWeapon)) {
									//do nothing, should only happen when window is cancelled
								} else if (item.unique) {
									GLog.w( Messages.get(WndGhostHero.class, "cant_unique"));
									hide();
								} else if (!item.isIdentified()) {
									GLog.w( Messages.get(WndGhostHero.class, "cant_unidentified"));
									hide();
								} else if (item.cursed) {
									GLog.w( Messages.get(WndGhostHero.class, "cant_cursed"));
									hide();
								} else if (((MeleeWeapon)item).STRReq() > rose.ghostStrength()) {
									GLog.w( Messages.get(WndGhostHero.class, "cant_strength"));
									hide();
								} else {
									if (item.isEquipped(Dungeon.hero)){
										((MeleeWeapon) item).doUnequip(Dungeon.hero, false, false);
									} else {
										item.detach(Dungeon.hero.belongings.backpack);
									}
									rose.weapon = (MeleeWeapon) item;
									item(rose.weapon);
								}
								
							}
						}, WndBag.Mode.WEAPON, Messages.get(WndGhostHero.class, "weapon_prompt"));
					}
				}
			};
			btnWeapon.setRect( (WIDTH - BTN_GAP) / 2 - BTN_SIZE, message.top() + message.height() + GAP, BTN_SIZE, BTN_SIZE );
			if (rose.weapon != null) {
				btnWeapon.item(rose.weapon);
			} else {
				btnWeapon.item(new WndBag.Placeholder(ItemSpriteSheet.WEAPON_HOLDER));
			}
			add( btnWeapon );
			
			btnArmor = new WndBlacksmith.ItemButton(){
				@Override
				protected void onClick() {
					if (rose.armor != null){
						item(new WndBag.Placeholder(ItemSpriteSheet.ARMOR_HOLDER));
						if (!rose.armor.doPickUp(Dungeon.hero)){
							Dungeon.level.drop( rose.armor, Dungeon.hero.pos);
						}
						rose.armor = null;
					} else {
						GameScene.selectItem(new WndBag.Listener() {
							@Override
							public void onSelect(Item item) {
								if (!(item instanceof Armor)) {
									//do nothing, should only happen when window is cancelled
								} else if (item.unique || ((Armor) item).checkSeal() != null) {
									GLog.w( Messages.get(WndGhostHero.class, "cant_unique"));
									hide();
								} else if (!item.isIdentified()) {
									GLog.w( Messages.get(WndGhostHero.class, "cant_unidentified"));
									hide();
								} else if (item.cursed) {
									GLog.w( Messages.get(WndGhostHero.class, "cant_cursed"));
									hide();
								} else if (((Armor)item).STRReq() > rose.ghostStrength()) {
									GLog.w( Messages.get(WndGhostHero.class, "cant_strength"));
									hide();
								} else {
									if (item.isEquipped(Dungeon.hero)){
										((Armor) item).doUnequip(Dungeon.hero, false, false);
									} else {
										item.detach(Dungeon.hero.belongings.backpack);
									}
									rose.armor = (Armor) item;
									item(rose.armor);
								}
								
							}
						}, WndBag.Mode.ARMOR, Messages.get(WndGhostHero.class, "armor_prompt"));
					}
				}
			};
			btnArmor.setRect( btnWeapon.right() + BTN_GAP, btnWeapon.top(), BTN_SIZE, BTN_SIZE );
			if (rose.armor != null) {
				btnArmor.item(rose.armor);
			} else {
				btnArmor.item(new WndBag.Placeholder(ItemSpriteSheet.ARMOR_HOLDER));
			}
			add( btnArmor );
			
			resize(WIDTH, (int)(btnArmor.bottom() + GAP));
		}
	
	}
}