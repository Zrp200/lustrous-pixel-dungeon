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

package com.zrp200.lustrouspixeldungeon.actors;

import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Assets;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.blobs.Blob;
import com.zrp200.lustrouspixeldungeon.actors.blobs.Electricity;
import com.zrp200.lustrouspixeldungeon.actors.blobs.Fire;
import com.zrp200.lustrouspixeldungeon.actors.blobs.Inferno;
import com.zrp200.lustrouspixeldungeon.actors.blobs.ToxicGas;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Adrenaline;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Amok;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Bleeding;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Bless;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Burning;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Charm;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Chill;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Corrosion;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Corruption;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Cripple;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Doom;
import com.zrp200.lustrouspixeldungeon.actors.buffs.EarthImbue;
import com.zrp200.lustrouspixeldungeon.actors.buffs.FireImbue;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Frost;
import com.zrp200.lustrouspixeldungeon.actors.buffs.FrostImbue;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Haste;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Hunger;
import com.zrp200.lustrouspixeldungeon.actors.buffs.MagicalSleep;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Ooze;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Paralysis;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Poison;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Preparation;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Roots;
import com.zrp200.lustrouspixeldungeon.actors.buffs.ShieldBuff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Slow;
import com.zrp200.lustrouspixeldungeon.actors.buffs.SnipersMark;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Speed;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Stamina;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Terror;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Vertigo;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.actors.hero.HeroSubClass;
import com.zrp200.lustrouspixeldungeon.actors.mobs.Shaman;
import com.zrp200.lustrouspixeldungeon.items.BrokenSeal;
import com.zrp200.lustrouspixeldungeon.items.Heap;
import com.zrp200.lustrouspixeldungeon.items.armor.glyphs.Brimstone;
import com.zrp200.lustrouspixeldungeon.items.armor.glyphs.Potential;
import com.zrp200.lustrouspixeldungeon.items.rings.RingOfElements;
import com.zrp200.lustrouspixeldungeon.items.scrolls.ScrollOfRetribution;
import com.zrp200.lustrouspixeldungeon.items.scrolls.exotic.ScrollOfPsionicBlast;
import com.zrp200.lustrouspixeldungeon.items.stones.StoneOfAggression;
import com.zrp200.lustrouspixeldungeon.items.wands.WandOfFireblast;
import com.zrp200.lustrouspixeldungeon.items.wands.WandOfLightning;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Blazing;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Grim;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Shocking;
import com.zrp200.lustrouspixeldungeon.items.weapon.missiles.Boomerang;
import com.zrp200.lustrouspixeldungeon.items.weapon.missiles.MissileWeapon;
import com.zrp200.lustrouspixeldungeon.items.weapon.missiles.darts.ShockingDart;
import com.zrp200.lustrouspixeldungeon.levels.Terrain;
import com.zrp200.lustrouspixeldungeon.levels.features.Chasm;
import com.zrp200.lustrouspixeldungeon.levels.features.Door;
import com.zrp200.lustrouspixeldungeon.levels.traps.GrimTrap;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.sprites.CharSprite;
import com.zrp200.lustrouspixeldungeon.utils.GLog;

import java.util.Arrays;
import java.util.HashSet;

public abstract class Char extends Actor {
	
	public int pos = 0;
	
	public CharSprite sprite;
	
	public String name = "mob";
	
	public int HT, HP;

	protected int armor=0;
	
	protected float baseSpeed	= 1;
	protected PathFinder.Path path;

	public int
			paralysed	= 0,
			invisible	= 0;

	public boolean
			rooted		= false,
			flying		= false;

	//these are relative to the hero
	public enum Alignment{
		ENEMY,
		NEUTRAL,
		ALLY
	}
	public Alignment alignment;
	
	public int viewDistance	= 8;
	
	public boolean[] fieldOfView = null;
	
	private HashSet<Buff> buffs = new HashSet<>();

	protected void updateFieldOfView() {
		if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()){
			fieldOfView = new boolean[Dungeon.level.length()];
		}
		Dungeon.level.updateFieldOfView( this, fieldOfView );
	}

	@Override
	protected boolean act() {
		updateFieldOfView();
		if(properties().contains(Property.IMMOVABLE)) throwItem();
		return false;
	}

	protected void throwItem() {
		Heap heap = Dungeon.level.heaps.get( pos );
		if (heap != null && !heap.isEmpty()) {
			int n;
			do {
				n = pos + PathFinder.NEIGHBOURS8[Random.Int( 8 )];
			} while (!Dungeon.level.passable[n] && !Dungeon.level.avoid[n]);
			heap.pickUp().drop(n).sprite.drop(pos);
		}
	}

	protected static final String POS       = "pos";
	protected static final String TAG_HP    = "HP";
	protected static final String TAG_HT    = "HT";
	protected static final String TAG_SHLD  = "SHLD";
	protected static final String BUFFS	    = "buffs";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		
		super.storeInBundle( bundle );
		
		bundle.put( POS, pos );
		bundle.put( TAG_HP, HP );
		bundle.put( TAG_HT, HT );
		bundle.put( BUFFS, buffs );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		
		super.restoreFromBundle( bundle );
		
		pos = bundle.getInt( POS );
		HP = bundle.getInt( TAG_HP );
		HT = bundle.getInt( TAG_HT );
		
		for ( Bundlable b : bundle.getCollection( BUFFS ) ) if (b != null)
			( (Buff) b ).attachTo( this );
		
		//pre-0.7.0
		if (bundle.contains( "SHLD" )){
			int legacySHLD = bundle.getInt( "SHLD" );
			//attempt to find the buff that may have given the shield
			ShieldBuff buff = buff(Brimstone.BrimstoneShield.class);
			if (buff != null) legacySHLD -= buff.shielding();
			if (legacySHLD > 0){
				BrokenSeal.WarriorShield buff2 = buff(BrokenSeal.WarriorShield.class);
				if (buff != null) buff2.supercharge(legacySHLD);
			}
		}
	}
	
	public boolean attack( Char enemy ) {

		if (enemy == null) return false;
		
		boolean visibleFight = Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[enemy.pos];
		
		if (hit( this, enemy, false )) {
			
			int dr = enemy.drRoll();
			
			if (this instanceof Hero){
				Hero h = (Hero)this;
				if (h.belongings.weapon instanceof MissileWeapon && h.subClass == HeroSubClass.SNIPER){
					dr = 0;
				}
			}
			
			int dmg;
			Preparation prep = buff(Preparation.class);
			if (prep != null){
				dmg = prep.damageRoll(this, enemy);
			} else {
				dmg = damageRoll();
			}
			
			int effectiveDamage = enemy.defenseProc( this, dmg );
			effectiveDamage = Math.max( effectiveDamage - dr, 0 );
			effectiveDamage = attackProc( enemy, effectiveDamage );
			
			if (visibleFight) {
				Sample.INSTANCE.play( Assets.SND_HIT, 1, 1, Random.Float( 0.8f, 1.25f ) );
			}

			// If the enemy is already dead, interrupt the attack.
			// This matters as defence procs can sometimes inflict self-damage, such as armor glyphs.
			if (!enemy.isAlive()){
				return true;
			}

			//TODO: consider revisiting this and shaking in more cases.
			float shake = 0f;
			if (enemy == Dungeon.hero)
				shake = effectiveDamage / (enemy.HT / 4f);

			if (shake > 1f)
				Camera.main.shake( GameMath.gate( 1, shake, 5), 0.3f );

			enemy.damage( effectiveDamage, this );

			if (buff(FireImbue.class) != null)
				buff(FireImbue.class).proc(enemy);
			if (buff(EarthImbue.class) != null)
				buff(EarthImbue.class).proc(enemy);
			if (buff(FrostImbue.class) != null)
				buff(FrostImbue.class).proc(enemy);

			enemy.sprite.bloodBurstA( sprite.center(), effectiveDamage );
			enemy.sprite.flash();

			if (!enemy.isAlive() && visibleFight) {
				if (enemy == Dungeon.hero) {
					
					if (this == Dungeon.hero) {
						return true;
					}

					Dungeon.fail( getClass() );
					GLog.n( Messages.capitalize(Messages.get(Char.class, "kill", name)) );
					
				} else if (this == Dungeon.hero) {
					GLog.i( Messages.capitalize(Messages.get(Char.class, "defeat", enemy.name)) );
				}
			}
			
			return true;
			
		} else {
			
			if (visibleFight) {
				String defense = enemy.defenseVerb();
				enemy.sprite.showStatus( CharSprite.NEUTRAL, defense );
				
				Sample.INSTANCE.play(Assets.SND_MISS);
			}
			
			return false;
			
		}
	}
	
	public static boolean hit( Char attacker, Char defender, boolean magic ) {
		float acuRoll = Random.Float( attacker.attackSkill( defender ) );
		float defRoll = Random.Float( defender.defenseSkill( attacker ) );
		if (attacker.buff(Bless.class) != null) acuRoll *= 1.20f;
		if (defender.buff(Bless.class) != null) defRoll *= 1.20f;
		return (magic ? acuRoll * 2 : acuRoll) >= defRoll;
	}

	public int attackSkill( Char target ) {
		return 0;
	}
	
	public int defenseSkill( Char enemy ) {
		return 0;
	}
	
	public String defenseVerb() {
		return Messages.get(this, "def_verb");
	}
	
	public int drRoll() {
		return Random.NormalInt(armor);
	}

	protected final int[] damageRoll = new int[2];

	public int damageRoll() {
		return Random.NormalIntRange(damageRoll[0],damageRoll[1]);
	}
	
	public int attackProc( Char enemy, int damage ) {
		return damage;
	}
	
	public int defenseProc( Char enemy, int damage ) {
		return damage;
	}
	
	public float speed() {
		float speed = baseSpeed;
		if ( buff( Cripple.class ) != null ) speed /= 2f;
		if ( buff( Stamina.class ) != null) speed *= 1.5f;
		if ( buff( Adrenaline.class ) != null) speed *= 2f;
		if ( buff( Haste.class ) != null) speed *= 3f;
		return speed;
	}
	
	//used so that buffs(Shieldbuff.class) isn't called every time unnecessarily
	private int cachedShield = 0;
	public boolean needsShieldUpdate = true;
	
	public int shielding(){
		if (!needsShieldUpdate){
			return cachedShield;
		}
		
		cachedShield = 0;
		for (ShieldBuff s : buffs(ShieldBuff.class)){
			cachedShield += s.shielding();
		}
		needsShieldUpdate = false;
		return cachedShield;
	}
	
	public void damage(int dmg, Object src) { // magic for extremely stone usage.
		if (!isAlive() || dmg < 0) return;
		Charm c = buff(Charm.class);
		if (c != null && src instanceof Char && isCharmedBy( (Char) src) )
			c.recover();
		if (this.buff(Doom.class) != null) dmg *= 2; // The first thing that happens
		Class<?> srcClass = src.getClass();
		dmg = isImmune( srcClass ) ? 0 : Math.round( dmg * resist( srcClass ) );
		int shielded = dmg;
		//FIXME: when I add proper damage properties, should add an IGNORES_SHIELDS property to use here.
		if ( !( src instanceof Hunger ) ) for ( ShieldBuff s : buffs(ShieldBuff.class) ) {
			dmg = s.absorbDamage(dmg);
			if (dmg == 0) break;
		}
		shielded -= dmg;
		HP -= dmg;
		sprite.showStatus(
			HP > HT / 2 ? CharSprite.WARNING : CharSprite.NEGATIVE,
			Integer.toString( dmg + shielded )
		);
		if( shielded+dmg==0 ) return;

		if (HP < 0) HP = 0;

		if (!isAlive()) {
			die( src );
			return;
		}

		Terror t = buff(Terror.class);
		if (t != null)
			t.recover();
		if (this.buff(Frost.class) != null)
			Buff.detach( this, Frost.class );
		if (this.buff(MagicalSleep.class) != null)
			Buff.detach(this, MagicalSleep.class);
		if (buff( Paralysis.class ) != null)
			buff( Paralysis.class ).processDamage(dmg);
	}
	
	public void destroy() {
		HP = 0;
		Actor.remove( this );
	}
	
	public void die( Object src ) {
		destroy();
		if (src != Chasm.class) sprite.die();

        SnipersMark mark = Dungeon.hero.buff(SnipersMark.class);
        if(mark != null && id() == mark.object) mark.detach();
	}
	
	public boolean isAlive() {
		return HP > 0;
	}
	
	@Override
	protected void spend( float time ) {
		
		float timeScale = 1f;
		if (buff( Slow.class ) != null)
			timeScale *= 0.5f;
		if (buff( Chill.class ) != null) {
			timeScale *= buff( Chill.class ).speedFactor();
		}
		if (buff( Speed.class ) != null) {
			timeScale *= 2.0f;
		}
		
		super.spend( time / timeScale );
	}
	
	public synchronized HashSet<Buff> buffs() {
		return new HashSet<>(buffs);
	}
	
	@SuppressWarnings("unchecked")
	public synchronized <T extends Buff> HashSet<T> buffs( Class<T> c ) {
		HashSet<T> filtered = new HashSet<>();
		for (Buff b : buffs) {
			if (c.isInstance( b )) {
				filtered.add( (T)b );
			}
		}
		return filtered;
	}

	@SuppressWarnings("unchecked")
	public synchronized  <T extends Buff> T buff( Class<T> c ) {
		for (Buff b : buffs) {
			if (c.isInstance( b )) {
				return (T)b;
			}
		}
		return null;
	}

	public synchronized boolean isCharmedBy( Char ch ) {
		int chID = ch.id();
		for (Buff b : buffs) {
			if (b instanceof Charm && ((Charm)b).object == chID) {
				return true;
			}
		}
		return false;
	}

	public synchronized void add( Buff buff ) {
		buffs.add( buff );
		Actor.add( buff );

	}
	
	public synchronized void remove( Buff buff ) {
		
		buffs.remove( buff );
		Actor.remove( buff );

	}

	public synchronized void remove( Class<? extends Buff> buffClass ) {
		for (Buff buff : buffs( buffClass )) {
			remove( buff );
		}
	}
	
	@Override
	protected synchronized void onRemove() {
		for (Buff buff : buffs.toArray(new Buff[buffs.size()])) {
			buff.detach();
		}
	}
	
	public synchronized void updateSpriteState() {
		for (Buff buff:buffs) {
			buff.fx( true );
		}
	}
	
	public float stealth() {
		return 0;
	}
	
	public void move( int step ) {

		if (Dungeon.level.adjacent( step, pos ) && buff( Vertigo.class ) != null) {
			sprite.interruptMotion();
			int newPos = pos + PathFinder.NEIGHBOURS8[Random.Int( 8 )];
			if ((Dungeon.level.passable[newPos] || Dungeon.level.avoid[newPos]) && Actor.findChar(newPos) == null) {
				sprite.move(pos, newPos);
				step = newPos;
			} else {
				return;
			}
		}

		if (Dungeon.level.map[pos] == Terrain.OPEN_DOOR) {
			Door.leave( pos );
		}

		pos = step;
		
		if (flying && Dungeon.level.map[pos] == Terrain.DOOR) {
			Door.enter( pos );
		}
		
		if (this != Dungeon.hero) {
			sprite.visible = Dungeon.level.heroFOV[pos];
		}
		
		if (!flying) {
			Dungeon.level.press( pos, this );
		}

		for(Boomerang.Returning returning : Dungeon.boomerangsThisDepth() ) {
			if(returning.pos == pos) {
				returning.onCharCollision(this);
			}
		}
	}
	
	public int distance( Char other ) {
		return Dungeon.level.distance( pos, other.pos );
	}
	
	public void onMotionComplete() {
		//Does nothing by default
		//The main actor thread already accounts for motion,
		// so calling next() here isn't necessary (see Actor.process)
	}
	
	public void onAttackComplete() {
		next();
	}
	
	public void onOperateComplete() {
		next();
	}
	
	protected final HashSet<Class> resistances = new HashSet<>();
	
	//returns percent effectiveness after resistances
	//TODO currently resistances reduce effectiveness by a static 50%, and do not stack.
	public float resist( Class effect ){
		if(isImmune(effect)) return 0;
		HashSet<Class> resists = new HashSet<>(resistances);
		for (Property p : properties()){
			resists.addAll(p.resistances());
		}
		for (Buff b : buffs()){
			resists.addAll(b.resistances());
		}
		
		float result = 1f;
		for (Class c : resists){
			if (c.isAssignableFrom(effect)){
				result *= 0.5f;
			}
		}
		return result * RingOfElements.resist(this, effect);
	}
	
	protected final HashSet<Class> immunities = new HashSet<>();
	
	public boolean isImmune(Class effect ){
		HashSet<Class> immunes = new HashSet<>(immunities);
		for (Property p : properties()){
			immunes.addAll(p.immunities());
		}
		for (Buff b : buffs()){
			immunes.addAll(b.immunities());
		}
		
		for (Class c : immunes){
			if (c.isAssignableFrom(effect)){
				return true;
			}
		}
		return false;
	}

	protected HashSet<Property> properties = new HashSet<>();

	public HashSet<Property> properties() {
		return new HashSet<>(properties);
	}

	public enum Property{
		BOSS (
		        new HashSet<Class>(
		                Arrays.asList(
		                        Grim.class, GrimTrap.class, ScrollOfRetribution.class, ScrollOfPsionicBlast.class, Terror.class)
                ), new HashSet<Class>( Arrays.asList(Corruption.class, Amok.class, StoneOfAggression.Aggression.class) ) ),
		MINIBOSS ( new HashSet<Class>(),
				new HashSet<Class>( Arrays.asList(Corruption.class) )),
		UNDEAD,
		DEMONIC,
		INORGANIC ( new HashSet<Class>(),
				new HashSet<Class>( Arrays.asList(Bleeding.class, ToxicGas.class, Poison.class) )),
		BLOB_IMMUNE ( new HashSet<Class>(),
				new HashSet<Class>( Arrays.asList(Blob.class) )),
		FIERY (
				new HashSet<Class>( Arrays.asList( WandOfFireblast.class, Blazing.class, Shaman.Firebolt.class) ),
				new HashSet<Class>( Arrays.asList( Burning.class, Fire.class, Inferno.class) )
		),
		ACIDIC ( new HashSet<Class>( Arrays.asList(Corrosion.class, Poison.class)),
				new HashSet<Class>( Arrays.asList(Ooze.class))),
		ELECTRIC ( new HashSet<Class>( Arrays.asList(WandOfLightning.class, Shocking.class, Potential.class, Electricity.class, ShockingDart.class)),
				new HashSet<Class>()),
		IMMOVABLE (new HashSet<Class>(),new HashSet<Class>(Arrays.asList(Roots.class)));
		
		private HashSet<Class> resistances;
		private HashSet<Class> immunities;
		
		Property(){
			this(new HashSet<Class>(), new HashSet<Class>());
		}
		
		Property(HashSet<Class> resistances, HashSet<Class> immunities){
			this.resistances = resistances;
			this.immunities = immunities;
		}
		
		public HashSet<Class> resistances(){
			return new HashSet<>(resistances);
		}
		public HashSet<Class> immunities(){
			return new HashSet<>(immunities);
		}
	}
}