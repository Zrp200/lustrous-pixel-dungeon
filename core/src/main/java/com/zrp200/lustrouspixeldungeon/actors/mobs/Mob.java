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

package com.zrp200.lustrouspixeldungeon.actors.mobs;

import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Badges;
import com.zrp200.lustrouspixeldungeon.Challenges;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.LustrousPixelDungeon;
import com.zrp200.lustrouspixeldungeon.Statistics;
import com.zrp200.lustrouspixeldungeon.actors.Actor;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Adrenaline;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Amok;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Charm;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Corruption;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Hunger;
import com.zrp200.lustrouspixeldungeon.actors.buffs.MagicalSleep;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Preparation;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Sleep;
import com.zrp200.lustrouspixeldungeon.actors.buffs.SoulMark;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Terror;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Weakness;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.actors.mobs.npcs.MirrorImage;
import com.zrp200.lustrouspixeldungeon.effects.Flare;
import com.zrp200.lustrouspixeldungeon.effects.Speck;
import com.zrp200.lustrouspixeldungeon.effects.Surprise;
import com.zrp200.lustrouspixeldungeon.effects.Wound;
import com.zrp200.lustrouspixeldungeon.items.Generator;
import com.zrp200.lustrouspixeldungeon.items.Item;
import com.zrp200.lustrouspixeldungeon.items.artifacts.DriedRose;
import com.zrp200.lustrouspixeldungeon.items.artifacts.TimekeepersHourglass;
import com.zrp200.lustrouspixeldungeon.items.potions.Potion;
import com.zrp200.lustrouspixeldungeon.items.rings.Ring;
import com.zrp200.lustrouspixeldungeon.items.rings.RingOfWealth;
import com.zrp200.lustrouspixeldungeon.items.stones.StoneOfAggression;
import com.zrp200.lustrouspixeldungeon.items.weapon.SpiritBow;
import com.zrp200.lustrouspixeldungeon.items.weapon.Weapon;
import com.zrp200.lustrouspixeldungeon.items.weapon.curses.Necromantic;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Lucky;
import com.zrp200.lustrouspixeldungeon.items.weapon.missiles.MissileWeapon;
import com.zrp200.lustrouspixeldungeon.levels.features.Chasm;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.plants.Swiftthistle;
import com.zrp200.lustrouspixeldungeon.sprites.CharSprite;
import com.zrp200.lustrouspixeldungeon.utils.GLog;

import java.util.ArrayList;
import java.util.HashSet;

import static com.zrp200.lustrouspixeldungeon.Dungeon.getTempBlock;
import static com.zrp200.lustrouspixeldungeon.Dungeon.level;

public abstract class Mob extends Char {

	{
		name = Messages.get(this, "name");
		actPriority = MOB_PRIO;
		
		alignment = Alignment.ENEMY;
	}

	public AiState SLEEPING     = new Sleeping();
	public AiState HUNTING		= new Hunting();
	public AiState WANDERING	= new Wandering();
	public AiState FLEEING		= new Fleeing();
	public AiState PASSIVE		= new Passive();
	public AiState state = SLEEPING;
	
	public Class<? extends CharSprite> spriteClass;
	
	protected int target = -1;
	public boolean isIgnoringBlockages; // an inelegant way to tell the game to ignore temporary blockages while pathfinding.
	
	protected int defenseSkill = 0;
	protected int attackSkill = 0;
	protected int armor=0;

	public int EXP = 1;
	public int maxLvl = Hero.MAX_LEVEL;
	
	protected Char enemy;
	protected boolean enemySeen;
	protected boolean alerted = false;

	public Char getEnemy() {
		return enemy;
	}

	private static final float TIME_TO_WAKE_UP = 1f;
	
	private static final String STATE	= "state";
	private static final String SEEN	= "seen";
	private static final String TARGET	= "target";

	@Override
	public void storeInBundle( Bundle bundle ) {
		
		super.storeInBundle( bundle );

		if (state == SLEEPING) {
			bundle.put( STATE, Sleeping.TAG );
		} else if (state == WANDERING) {
			bundle.put( STATE, Wandering.TAG );
		} else if (state == HUNTING) {
			bundle.put( STATE, Hunting.TAG );
		} else if (state == FLEEING) {
			bundle.put( STATE, Fleeing.TAG );
		} else if (state == PASSIVE) {
			bundle.put( STATE, Passive.TAG );
		}
		bundle.put( SEEN, enemySeen );
		bundle.put( TARGET, target );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		
		super.restoreFromBundle( bundle );

		String state = bundle.getString( STATE );
		switch (state) {
			case Sleeping.TAG:
				this.state = SLEEPING;
				break;
			case Wandering.TAG:
				this.state = WANDERING;
				break;
			case Hunting.TAG:
				this.state = HUNTING;
				break;
			case Fleeing.TAG:
				this.state = FLEEING;
				break;
			case Passive.TAG:
				this.state = PASSIVE;
				break;
		}

		enemySeen = bundle.getBoolean( SEEN );
		target = bundle.getInt( TARGET );
	}
	
	public CharSprite sprite() {
		CharSprite sprite = null;
		try {
			sprite = spriteClass.newInstance();
		} catch (Exception e) {
			LustrousPixelDungeon.reportException(e);
		}
		return sprite;
	}
	
	@Override
	protected boolean act() {
		
		super.act();
		
		boolean justAlerted = alerted;
		alerted = false;
		
		if (justAlerted){
			sprite.showAlert();
		} else {
			sprite.hideAlert();
			sprite.hideLost();
		}
		
		if (paralysed > 0) {
			enemySeen = false;
			spend( TICK );
			return true;
		}
		
		enemy = chooseEnemy();

		return state.act( justAlerted );
	}

	public boolean enemyInFOV() {
		return inFOV(enemy);
	}
	public boolean inFOV(Char enemy) {
		updateFieldOfView();
		return enemy != null && enemy.isAlive() && fieldOfView[enemy.pos] && enemy.invisible <= 0;
	}

	protected boolean needsNewEnemy() {
		//find a new enemy if..
		//we have no enemy, or the current one is dead
		if ( enemy == null || !enemy.isAlive() || state == WANDERING)
			return true;
			//We are an ally, and current enemy is another ally.
		else if (alignment == Alignment.ALLY && enemy.alignment == Alignment.ALLY)
			return true;
			//We are amoked and current enemy is the hero
		else if (buff( Amok.class ) != null && enemy == Dungeon.hero)
			return true;
			//We are charmed and current enemy is what charmed us
		else return isCharmedBy(enemy);
	}
	protected final Char chooseEnemy() {
		return chooseEnemy( needsNewEnemy() );
	}
	protected Char chooseEnemy(boolean newEnemy) {

        Terror terror = buff( Terror.class );
        if (terror != null) {
            Char source = (Char)Actor.findById( terror.object );
            if (source != null) {
                return source;
            }
        }

        //if we are an enemy, and have no target or current target isn't affected by aggression
        //then auto-prioritize a target that is affected by aggression, even another enemy
        if (alignment == Alignment.ENEMY
                && (enemy == null || enemy.buff(StoneOfAggression.Aggression.class) == null)) {
            for (Char ch : Actor.chars()) {
                if (ch != this && fieldOfView[ch.pos] &&
                        ch.buff(StoneOfAggression.Aggression.class) != null) {
                    return ch;
                }
            }
        }

		if ( newEnemy ) {
			//neutral characters in particular do not choose enemies.
			if (findEnemies().isEmpty()){
				return null;
			} else {
				//go after the closest potential enemy, preferring the hero if two are equidistant
				Char closest = null;
				for (Char curr : findEnemies()){
					if (closest == null
							|| level.distance(pos, curr.pos) < level.distance(pos, closest.pos)
							|| level.distance(pos, curr.pos) == level.distance(pos, closest.pos)
							&& curr == Dungeon.hero) {
						closest = curr;
					}
				}
				return closest;
			}

		} else return enemy;
	}

	protected HashSet<Char> findEnemies() {
		HashSet<Char> enemies = new HashSet<>();
		updateFieldOfView();

		//if the mob is amoked...
		if ( buff(Amok.class) != null) {
			//try to find an enemy mob to attack first.
			for (Mob mob : level.mobs)
				if (mob.alignment == Alignment.ENEMY && mob != this && fieldOfView[mob.pos])
					enemies.add(mob);

			if (enemies.isEmpty()) {
				//try to find ally mobs to attack second.
				for (Mob mob : level.mobs)
					if (mob.alignment == Alignment.ALLY && mob != this && fieldOfView[mob.pos])
						enemies.add(mob);

				if (enemies.isEmpty()) {
					//try to find the hero third
					if (fieldOfView[Dungeon.hero.pos]) {
						enemies.add(Dungeon.hero);
					}
				}
			}

			//if the mob is an ally...
		} else if ( alignment == Alignment.ALLY ) {
			//look for hostile mobs that are not passive to attack
			for (Mob mob : level.mobs)
				if (mob.alignment == Alignment.ENEMY
						&& fieldOfView[mob.pos]
						&& mob.state != mob.PASSIVE && (buff(Corruption.class) != null || !(mob instanceof Piranha)) )
					enemies.add(mob);

			//if the mob is an enemy...
		} else if (alignment == Alignment.ENEMY) {
			//look for ally mobs to attack
			for (Mob mob : level.mobs)
				if (mob.alignment == Alignment.ALLY && fieldOfView[mob.pos])
					enemies.add(mob);

			//and look for the hero
			if (fieldOfView[Dungeon.hero.pos]) {
				enemies.add(Dungeon.hero);
			}

		}

		Charm charm = buff( Charm.class );
		if (charm != null){
			Char source = (Char)Actor.findById( charm.object );
			if (source != null && enemies.contains(source) && enemies.size() > 1){
				enemies.remove(source);
			}
		}
		return enemies;
	}

	protected boolean moveSprite( int from, int to ) {

		if (sprite.isVisible() && (level.heroFOV[from] || level.heroFOV[to])) {
			sprite.move( from, to );
			return true;
		} else {
			sprite.turnTo(from, to);
			sprite.place( to );
			return true;
		}
	}
	
	@Override
	public void add( Buff buff ) {
		super.add( buff );
		if(buff instanceof Corruption) state = HUNTING;
		if (buff instanceof Amok) {
			for(Terror t : buffs(Terror.class)) {
				t.detach();
			}
			state = HUNTING;
		}
		if (buff instanceof Sleep) {
			state = SLEEPING;
			postpone( Sleep.SWS );
		}
	}
	
	protected boolean canAttack( Char enemy ) {
		return level.adjacent( pos, enemy.pos );
	}

	protected boolean getCloser( int target ) {
		if (rooted || target == pos) {
			return false;
		}

		int step = -1;

		if (level.adjacent( pos, target )) {
			path = null;
			if (!Dungeon.getTempBlock(this, fieldOfView)[target] && level.passable[target]) {
				step = target;
			}
		} else {

			boolean newPath = false;
			//scrap the current path if it's empty, no longer connects to the current location
			//or if it's extremely inefficient and checking again may result in a much better path
			if (path == null || path.isEmpty()
					|| !level.adjacent(pos, path.getFirst())
					|| path.size() > 2* level.distance(pos, target))
				newPath = true;
			else if (path.getLast() != target) {
				//if the new target is adjacent to the end of the path, adjust for that
				//rather than scrapping the whole path.
				if (level.adjacent(target, path.getLast())) {
					int last = path.removeLast();

					if (path.isEmpty()) {

						//shorten for a closer one
						if (level.adjacent(target, pos)) {
							path.add(target);
							//extend the path for a further target
						} else {
							path.add(last);
							path.add(target);
						}

					} else {
						//if the new target is simply 1 earlier in the path shorten the path
						if (path.getLast() == target) {

							//if the new target is closer/same, need to modify end of path
						} else if (level.adjacent(target, path.getLast())) {
							path.add(target);

							//if the new target is further away, need to extend the path
						} else {
							path.add(last);
							path.add(target);
						}
					}

				} else {
					newPath = true;
				}

			}
			if (newPath || !isPathValid()) {
				path = Dungeon.findPath(this, pos, target,
						level.passable,
						fieldOfView);
			}

			//if hunting something, don't follow a path that is extremely inefficient
			//FIXME this is fairly brittle, primarily it assumes that hunting mobs can't see through
			// permanent terrain, such that if their path is inefficient it's always because
			// of a temporary blockage, and therefore waiting for it to clear is the best option.
			if (path == null ||
					(state == HUNTING && path.size() > Math.max(9, 2* level.distance(pos, target)))) {
				if(isIgnoringBlockages) {
					return false;
				} else {
					isIgnoringBlockages = true;
					boolean result = getCloser(target);
					isIgnoringBlockages = false;
					return result;
				}
			}

			step = path.removeFirst();
		}
		if (step != -1) {
			move( step );
			return true;
		} else {
			return false;
		}
	}

	private boolean canMoveTo(int pos) {
		return !(level.passable[pos] || level.avoid[pos])
				|| (fieldOfView[pos] && Actor.findChar(pos) != null);
	}

	private boolean isPathValid(int distance) { //looks ahead for path validity
		if(path == null) return false;
		for (int i = 0; i < distance; i++) {
			int cell = path.get(i);
			if (!level.passable[cell] || (getTempBlock(this,fieldOfView)[cell])) {
				return false;
			}
		}
		return true;
	}
	private boolean isPathValid() {
		return isPathValid((int)GameMath.gate(1, path.size()-1, 4));
	}

	boolean isTrapped(int target) {
		return rooted
				|| target == pos
				|| Dungeon.flee(this, pos, target, level.passable, fieldOfView) == -1;
	}

	protected boolean getFurther( int target ) {
		if(isTrapped(target)) return false;
		move( Dungeon.flee(this, pos, target, level.passable, fieldOfView) );
		return true;
	}

	@Override
	public void updateSpriteState() {
		super.updateSpriteState();
		if (Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class) != null
				|| Dungeon.hero.buff(Swiftthistle.TimeBubble.class) != null)
			sprite.add( CharSprite.State.PARALYSED );
	}
	
	public float attackDelay() {
		float delay = 1f;
		if ( buff(Adrenaline.class) != null) delay /= 1.5f;
		return delay;
	}
	
	protected boolean doAttack( Char enemy ) {
		
		boolean visible = level.heroFOV[pos];
		
		if (visible) {
			sprite.attack( enemy.pos );
		} else {
			attack( enemy );
		}
				
		spend( attackDelay() );
		
		return !visible;
	}
	
	@Override
	public void onAttackComplete() {
		attack( enemy );
		super.onAttackComplete();
	}
	
	@Override
	public int attackProc(Char enemy, int damage) {
		damage = super.attackProc(enemy, damage);
		if (buff(Weakness.class) != null){
			damage *= 0.67f;
		}
		return damage;
	}

	public boolean enemySeen() { // just double checks things
		return enemySeen && (enemy == null || enemyInFOV());
	}

	@Override
	public int defenseSkill( Char enemy ) {
		if (surprisedBy(enemy) || paralysed > 0) {
			return 0;
		} else {
			return this.defenseSkill;
		}
	}

	@Override
	public int attackSkill(Char target) {
		return this.attackSkill;
	}

	private boolean hitWithRanged = false;

	@Override
	public int defenseProc( Char enemy, int damage ) {

		if (enemy instanceof Hero && ((Hero) enemy).belongings.weapon instanceof MissileWeapon){
			hitWithRanged = true;
		}

		if ( surprisedBy(enemy) ){
			Statistics.sneakAttacks++;
			Badges.validateRogueUnlock();
			if (enemy.buff(Preparation.class) != null) {
				Wound.hit(this);
			} else {
				Surprise.hit(this);
			}
		}

		//if attacked by something else than current target, and that thing is closer, switch targets
		if (this.enemy == null
				|| (enemy != this.enemy && (level.distance(pos, enemy.pos) < level.distance(pos, this.enemy.pos)))) {
			aggro(enemy);
			target = enemy.pos;
		}

		if (buff(SoulMark.class) != null) {
			int restoration = Math.min(damage, HP);
			if(restoration/2 > 0) {
				Dungeon.hero.buff(Hunger.class).satisfy(restoration);
				Integer toHeal=Math.min(HT-HP, Math.round(restoration*0.33f));
				HP += toHeal;
				if(toHeal > 0) Dungeon.hero.sprite.showStatus( CharSprite.POSITIVE,toHeal.toString() );
				Dungeon.hero.sprite.emitter().burst( Speck.factory(Speck.HEALING), 1 );
			}
		}

		return damage;
	}

	public boolean surprisedBy( Char enemy ){
		if(enemy instanceof Hero) {
			Hero hero = (Hero) enemy;
			if(!hero.canSurpriseAttack()) return false;
			if(hero.belongings.weapon instanceof SpiritBow.SpiritArrow) {
				final SpiritBow bow = ( (SpiritBow.SpiritArrow) hero.belongings.weapon ).getBow();
				if(bow.sniperSpecial && bow.augment == Weapon.Augment.DAMAGE) {
					Actor.add(new Actor() {
						{   actPriority = VFX_PRIO;  }
						@Override protected boolean act() {
							bow.sniperSpecial = false; // this is where it is turned off.
							remove(this);
							return true;
						}
					}); // this is where it is turned off.
					return true;
				}
			}
			if(alignment == Alignment.ALLY) return true;
		}
		if(enemy instanceof MirrorImage && !Dungeon.hero.canSurpriseAttack()) return false;
		if(enemy instanceof DriedRose.GhostHero && !((DriedRose.GhostHero)enemy).canSurpriseAttack())
			return false;
		return !enemySeen() || enemy.invisible > 0 || paralysed > 0;
	}

	public void aggro( Char ch ) {
		Terror t = buff(Terror.class);
		if(t != null) t.recover();
		if(buff(Terror.class) != null) return;

		enemy = ch;
		if (state != PASSIVE){
			state = HUNTING;
		}
	}

	public boolean isTargeting( Char ch){
		return enemy == ch;
	}

	@Override
	public void damage( int dmg, Object src, boolean magic ) {

		if (state == SLEEPING) {
			state = WANDERING;
		}
		if (state != HUNTING) {
			alerted = true;
		}
		
		super.damage( dmg, src, magic );
	}
	
	
	@Override
	public void destroy() {
		
		super.destroy();
		
		level.mobs.remove( this );
		
		if (Dungeon.hero.isAlive()) {
			
			if (alignment == Alignment.ENEMY) {
				Statistics.enemiesSlain++;
				Badges.validateMonstersSlain();
				Statistics.qualifiedForNoKilling = false;
				
				int exp = Dungeon.hero.lvl <= maxLvl ? EXP : 0;
				if (exp > 0) {
					Dungeon.hero.sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "exp", exp));
				}
				Dungeon.hero.earnExp(exp, getClass());
			}
		}
	}
	
	@Override
	public void die( Object cause ) {
		
		if (hitWithRanged){
			Statistics.thrownAssists++;
			Badges.validateHuntressUnlock();
		}

		if (cause == Chasm.class){
			EXP += Random.Int(EXP%2+1);	//50% chance to round up, 50% to round down
			EXP /= 2;
		}

		if (alignment == Alignment.ENEMY) rollToDropLoot();
		if (Dungeon.hero.isAlive() && !level.heroFOV[pos]) {
			GLog.i( Messages.get(this, "died") );
		}
		boolean wraithSpawn = buff(Necromantic.Proc.class) != null;
		super.die( cause );
		if(wraithSpawn && !(this instanceof Wraith)) {
			Wraith.spawnAt(pos);
			Wraith.playSFX();
		}
	}
	
	public void rollToDropLoot() {
		if (Dungeon.hero.lvl > maxLvl + 2) return;
		if (loot instanceof Potion && level.pit[pos]) return;

		float lootChance = this.lootChance;
		lootChance *= RingOfWealth.dropChanceMultiplier(Dungeon.hero);

		if (Random.Float() < lootChance) {
			Item loot = createLoot();
			if (loot != null) {
				loot.drop(pos);
			}
		}

		//ring of wealth logic
		if (Ring.getBonus(Dungeon.hero, RingOfWealth.Wealth.class) > 0) {
			int rolls = 1;
			if (properties.contains(Property.BOSS)) rolls = 15;
			else if (properties.contains(Property.MINIBOSS)) rolls = 5;
			ArrayList<Item> bonus = RingOfWealth.tryForBonusDrop(Dungeon.hero, rolls);
			if (bonus != null && !bonus.isEmpty()) {
				for (Item b : bonus) level.drop(b, pos).sprite.drop();
				if (RingOfWealth.latestDropWasRare) {
					new Flare(8, 48).color(0xAA00FF, true).show(sprite, 3f);
					RingOfWealth.latestDropWasRare = false;
				} else {
					new Flare(8, 24).color(0xFFFFFF, true).show(sprite, 3f);
				}
			}
		}

		//lucky enchant logic
		if (Dungeon.hero.lvl <= maxLvl && buff(Lucky.LuckProc.class) != null) {
			new Flare(8, 24).color(0x00FF00, true).show(sprite, 3f);
			Lucky.genLoot().drop(pos);
		}
	}
	
	protected Object loot = null;
	protected float lootChance = 0;

	@SuppressWarnings("unchecked")
	protected Item createLoot() {
		Item item;
		if (loot instanceof Generator.Category) {

			item = Generator.random( (Generator.Category)loot );

		} else if (loot instanceof Class<?>) {

			item = Generator.random( (Class<? extends Item>)loot );

		} else {

			item = (Item)loot;

		}
		return item;
	}
	
	public boolean reset() {
		return false;
	}
	
	public void beckon( int cell ) {
		if(buff(MagicalSleep.class) != null) return;

		notice();
		
		if (state != HUNTING) state = WANDERING;
		target = cell;
	}
	
	public String description() {
		String variantDesc = Messages.get(this,"variant_desc");
		return Messages.get(this, "desc")
				+ (variantDesc.equals("") ? "" : "\n\n" + variantDesc);
	}
	
	public void notice() {
		sprite.showAlert();
	}
	
	public void yell( String str ) {
		GLog.n( "%s: \"%s\" ", Messages.titleCase(name), str );
	}

	//returns true when a mob sees the hero, and is currently targeting them.
	public boolean focusingHero() {
		return enemySeen && (target == Dungeon.hero.pos);
	}

	class AiState {
		boolean act( boolean justAlerted ) {
			return enemySeen = hasNoticedEnemy();
		}
		boolean hasNoticedEnemy() {
			return enemyInFOV();
		}
	}

	protected class Sleeping extends AiState {
		static final String TAG	= "SLEEPING";

		public boolean hasNoticedEnemy() {
			return super.hasNoticedEnemy()
					&& Random.Float( distance( enemy ) + enemy.stealth() + (enemy.flying ? 2 : 0) ) < 1;
		}

		@Override
		public boolean act( boolean justAlerted ) {
			if (super.act(justAlerted)) {
				notice();
				state = HUNTING;
				target = enemy.pos;

				if (Dungeon.isChallenged( Challenges.SWARM_INTELLIGENCE )) {
					for (Mob mob : level.mobs) {
						if (level.distance(pos, mob.pos) <= 8 && mob.state != mob.HUNTING) {
							mob.beckon( target );
						}
					}
				}

				spend( TIME_TO_WAKE_UP );

			} else {
				spend( TICK );
			}
			return true;
		}
	}

	protected class Wandering extends AiState {

		static final String TAG	= "WANDERING";
		public boolean hasNoticedEnemy() {
			return super.hasNoticedEnemy() && (Random.Float( distance( enemy ) / 2f + enemy.stealth() ) < 1);
		}
		@Override
		public boolean act( boolean justAlerted ) {
			super.act(justAlerted);
			if ( enemySeen || enemyInFOV() && justAlerted) {
				notice();
				enemySeen = alerted = true;
				state = HUNTING;
				target = enemy.pos;

				if (Dungeon.isChallenged( Challenges.SWARM_INTELLIGENCE )) {
					for (Mob mob : level.mobs) {
						if (level.distance(pos, mob.pos) <= 8 && mob.state != mob.HUNTING) {
							mob.beckon( target );
						}
					}
				}

			} else {
				int oldPos = pos;
				if (target != -1 && getCloser( target )) {
					spend( 1 / speed() );
					return moveSprite( oldPos, pos );
				} else {
					target = level.randomDestination();
					spend( TICK );
				}

			}
			return true;
		}
	}

	protected class Hunting extends AiState {

		static final String TAG	= "HUNTING";

		@Override
		public boolean act( boolean justAlerted ) {
			Char origEnemy = enemy;
			if (super.act(justAlerted) && !isCharmedBy( enemy ) && ( canAttack(enemy) || canAttack(enemy = chooseEnemy()) ) ) {
				return doAttack(enemy);
			} else {
				enemy = origEnemy;
				if (enemySeen) {
					target = enemy.pos;
				} else if (enemy == null) {
					state = WANDERING;
					target = level.randomDestination();
					return true;
				}
				
				int oldPos = pos;
				if (target != -1 && getCloser( target )) {
					
					spend( 1 / speed() );
					return moveSprite( oldPos,  pos );

				} else {
					spend( TICK );
					if (!enemySeen) {
						sprite.showLost();
						state = WANDERING;
						target = level.randomDestination();
					}
					return true;
				}
			}
		}
	}

	protected class Fleeing extends AiState {

		static final String TAG	= "FLEEING";

		boolean isTrapped() {
			return target == -1 || !getFurther(target);
		}
		@Override
		public boolean act( boolean justAlerted ) {
			super.act(justAlerted);
			//loses target when 0-dist rolls a 6 or greater.
			if (enemy == null || !enemySeen && 1 + Random.Int(level.distance(pos, target)) >= 6){
				target = -1;
			
			//if enemy isn't in FOV, keep running from their previous position.
			} else if (enemySeen) {
				target = enemy.pos;
			}

			int oldPos = pos;
			if ( isTrapped() ) {
				spend(TICK);
				nowhereToRun();
				return true;
			}
			else {
				spend( 1 / speed() );
				return moveSprite( oldPos, pos );
			}
		}

		protected void nowhereToRun() {
		    if(buff(Terror.class) != null) buff(Terror.class).recover(); //you'd think that would have an impact
			else Terror.onRemove(Mob.this);
		}
	}

	protected class Passive extends AiState {

		static final String TAG	= "PASSIVE";

		@Override
		public boolean hasNoticedEnemy() {
			return false;
		}

		@Override
		public boolean act( boolean justAlerted ) {
			super.act(justAlerted);
			spend( TICK );
			return true;
		}
	}
}

