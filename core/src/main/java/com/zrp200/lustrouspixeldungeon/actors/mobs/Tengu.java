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

import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Assets;
import com.zrp200.lustrouspixeldungeon.Badges;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Actor;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.LockedFloor;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Terror;
import com.zrp200.lustrouspixeldungeon.actors.hero.HeroSubClass;
import com.zrp200.lustrouspixeldungeon.effects.CellEmitter;
import com.zrp200.lustrouspixeldungeon.effects.Speck;
import com.zrp200.lustrouspixeldungeon.items.TomeOfMastery;
import com.zrp200.lustrouspixeldungeon.items.artifacts.LloydsBeacon;
import com.zrp200.lustrouspixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.zrp200.lustrouspixeldungeon.items.weapon.missiles.MissileWeapon;
import com.zrp200.lustrouspixeldungeon.levels.Level;
import com.zrp200.lustrouspixeldungeon.levels.PrisonBossLevel;
import com.zrp200.lustrouspixeldungeon.levels.Terrain;
import com.zrp200.lustrouspixeldungeon.levels.traps.GrippingTrap;
import com.zrp200.lustrouspixeldungeon.mechanics.Ballistica;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.scenes.GameScene;
import com.zrp200.lustrouspixeldungeon.sprites.TenguSprite;
import com.zrp200.lustrouspixeldungeon.ui.BossHealthBar;

public class Tengu extends Mob {
	
	{
		spriteClass = TenguSprite.class;
		
		HP = HT = 120;
		EXP = 20;
		armor = 5;
		defenseSkill = 20;
		enemy = Dungeon.hero;

		HUNTING = new Hunting() {
			@Override
			public boolean act(boolean justAlerted) { // tengu is always hunting
				enemySeen = enemyInFOV(); // sync I guess
				if ( enemySeen && !isCharmedBy( enemy ) && canAttack( enemy ) ) return doAttack( enemy );
				else {
					if (!enemySeen) chooseEnemy();
					if (enemy != null) { target = enemy.pos; }
					spend( TICK );
					return true;
				}
			}
		};

		flying = true; //doesn't literally fly, but he is fleet-of-foot enough to avoid hazards

		properties.add(Property.BOSS);
	}
	
	@Override
	protected void onAdd() {
		//when he's removed and re-added to the fight, his time is always set to now.
		spend(-cooldown());
		super.onAdd();
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 6, 20 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 20;
	}

	@Override
	public void add(Buff buff) {
		if(buff instanceof Terror) jump();
		else super.add(buff);
	}

	@Override
	public void damage(int dmg, Object src) {
		
		PrisonBossLevel.State state = ((PrisonBossLevel)Dungeon.level).state();
		
		int hpBracket;
		if (state == PrisonBossLevel.State.FIGHT_START){
			hpBracket = 12;
		} else {
			hpBracket = 20;
		}

		int beforeHitHP = HP;
		super.damage(dmg, src);
		dmg = beforeHitHP - HP;

		LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
		if (lock != null) {
			int multiple = state == PrisonBossLevel.State.FIGHT_START ? 1 : 4;
			lock.addTime(dmg*multiple);
		}

		//phase 2 of the fight is over
		if (HP == 0 && state == PrisonBossLevel.State.FIGHT_ARENA) {
			if(src == Dungeon.hero && Dungeon.hero.belongings.weapon instanceof MissileWeapon)
				((MissileWeapon) Dungeon.hero.belongings.weapon).stickTo(this);
			((PrisonBossLevel)Dungeon.level).progress();
			return;
		}
		
		//phase 1 of the fight is over
		if (state == PrisonBossLevel.State.FIGHT_START && HP <= HT/2){
			HP = (HT/2)-1;
			yell(Messages.get(this, "interesting"));
			((PrisonBossLevel)Dungeon.level).progress();
			BossHealthBar.bleed(true);

		//if tengu has lost a certain amount of hp, jump
		} else if (beforeHitHP / hpBracket != HP / hpBracket) {
			jump();
		}
	}

	@Override
	public boolean isAlive() {
		return Dungeon.level.mobs.contains(this); //Tengu has special death rules, see prisonbosslevel.progress()
	}

	@Override
	public void die( Object cause ) {
		
		if (Dungeon.hero.subClass == HeroSubClass.NONE) {
			Dungeon.level.drop( new TomeOfMastery(), pos ).sprite.drop();
		}
		
		GameScene.bossSlain();
		super.die( cause );
		
		Badges.validateBossSlain();

		LloydsBeacon beacon = Dungeon.hero.belongings.getItem(LloydsBeacon.class);
		if (beacon != null) {
			beacon.upgrade();
		}
		
		yell( Messages.get(this, "defeated") );
	}

	@Override
	protected boolean canAttack( Char enemy ) {
		return new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE).collisionPos == enemy.pos;
	}

	//tengu's attack is always visible
	@Override
	protected boolean doAttack(Char enemy) {
		sprite.attack( enemy.pos );
		spend( attackDelay() );
		return false;
	}

	private void jump() {
		
		Level level = Dungeon.level;
		
		//incase tengu hasn't had a chance to act yet
		if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()){
			fieldOfView = new boolean[Dungeon.level.length()];
			Dungeon.level.updateFieldOfView( this, fieldOfView );
		}
		
		if (enemy == null) enemy = chooseEnemy();
		if (enemy == null) return;

		int newPos;
		//if we're in phase 1, want to warp around within the room
		if (((PrisonBossLevel)Dungeon.level).state() == PrisonBossLevel.State.FIGHT_START) {
			
			//place new traps
			int tries;
			for (int i=0; i < 4; i++) {
				int trapPos;
				tries = 15;
				do {
					trapPos = Random.Int( level.length() );
				} while (tries-- > 0 && level.map[trapPos] != Terrain.INACTIVE_TRAP
						&& level.map[trapPos] != Terrain.TRAP);
				
				if (level.map[trapPos] == Terrain.INACTIVE_TRAP) {
					level.setTrap( new GrippingTrap().reveal(), trapPos );
					Level.set( trapPos, Terrain.TRAP );
					ScrollOfMagicMapping.discover( trapPos );
				}
			}
			
			tries = 50;
			do {
				newPos = Random.IntRange(3, 7) + 32*Random.IntRange(26, 30);
			} while ( (level.adjacent(newPos, enemy.pos) || Actor.findChar(newPos) != null)
					&& --tries > 0);
			if (tries <= 0) return;

		//otherwise go wherever, as long as it's a little bit away
		} else {
			do {
				newPos = Random.Int(level.length());
			} while (
					level.solid[newPos] ||
					level.distance(newPos, enemy.pos) < 8 ||
					Actor.findChar(newPos) != null);
		}
		
		if (level.heroFOV[pos]) CellEmitter.get( pos ).burst( Speck.factory( Speck.WOOL ), 6 );

		sprite.move( pos, newPos );
		move( newPos );
		
		if (level.heroFOV[newPos]) CellEmitter.get( newPos ).burst( Speck.factory( Speck.WOOL ), 6 );
		Sample.INSTANCE.play( Assets.SND_PUFF );
		
		spend( 1 / speed() );
	}
	
	@Override
	public void notice() {
		super.notice();
		BossHealthBar.assignBoss(this);
		if (HP <= HT/2) BossHealthBar.bleed(true);
		if (HP == HT) {
			yell(Messages.get(this, "notice_mine", Dungeon.hero.givenName()));
		} else {
			yell(Messages.get(this, "notice_face", Dungeon.hero.givenName()));
		}
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		BossHealthBar.assignBoss(this);
		if (HP <= HT/2) BossHealthBar.bleed(true);
	}

}
