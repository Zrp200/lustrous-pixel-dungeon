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
import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Actor;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Cripple;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Light;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Terror;
import com.zrp200.lustrouspixeldungeon.effects.CellEmitter;
import com.zrp200.lustrouspixeldungeon.effects.particles.PurpleParticle;
import com.zrp200.lustrouspixeldungeon.items.Dewdrop;
import com.zrp200.lustrouspixeldungeon.items.wands.WandOfDisintegration;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Grim;
import com.zrp200.lustrouspixeldungeon.levels.traps.DisintegrationTrap;
import com.zrp200.lustrouspixeldungeon.levels.traps.GrimTrap;
import com.zrp200.lustrouspixeldungeon.mechanics.Ballistica;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.scenes.GameScene;
import com.zrp200.lustrouspixeldungeon.sprites.CharSprite;
import com.zrp200.lustrouspixeldungeon.sprites.EyeSprite;
import com.zrp200.lustrouspixeldungeon.utils.GLog;

public class Eye extends Mob {
	{
		spriteClass = EyeSprite.class;
		
		HP = HT = 100;
		defenseSkill = 20;
		viewDistance = Light.DISTANCE;
		
		EXP = 13;
		maxLvl = 25;
		
		flying = true;

		HUNTING = new Hunting();
		
		loot = new Dewdrop();
		lootChance = 0.5f;

		properties.add(Property.DEMONIC);
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(20, 30);
	}

	@Override
	public int attackSkill( Char target ) {
		return 30;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 10);
	}
	
	private Ballistica beam;
	private int beamTarget = -1;
	private int beamCooldown;
	public boolean beamCharged;

	@Override
	protected boolean canAttack( Char enemy ) {

		if (beamCooldown == 0) {
			Ballistica aim = new Ballistica(pos, enemy.pos, Ballistica.STOP_TERRAIN);

			if (enemy.invisible == 0 && !isCharmedBy(enemy) && fieldOfView[enemy.pos] && aim.subPath(1, aim.dist).contains(enemy.pos)){
				beam = aim;
				beamTarget = aim.collisionPos;
				return true;
			} else
				//if the beam is charged, it has to attack, will aim at previous location of target.
				return beamCharged;
		} else
			return super.canAttack(enemy);
	}

	@Override
	protected boolean act() {
		if (beamCharged && state != HUNTING)
			beamCharged = false;
		if (beam == null && beamTarget != -1) {
			beam = new Ballistica(pos, beamTarget, Ballistica.STOP_TERRAIN);
			sprite.turnTo(pos, beamTarget);
		}
		if (beamCooldown > 0) beamCooldown--;
		return super.act();
	}

	@Override
	protected boolean doAttack( Char enemy ) {

		if (beamCooldown > 0) {
			return super.doAttack(enemy);
		} else if (!beamCharged){
			((EyeSprite)sprite).charge( enemy.pos );
			spend( attackDelay()*2f );
			beamCharged = true;
			return true;
		} else {
			beam = new Ballistica(pos, beamTarget, Ballistica.STOP_TERRAIN);
			if (Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[beam.collisionPos] ) {
				sprite.zap( beam.collisionPos );
				return false;
			} else {
				deathGaze();
				return true;
			}
		}

	}

	public void damage(int dmg, Object src, boolean magic) {
		if(src instanceof Eye && magic) dmg /= 2; // deathgaze resist
		if(beamCharged) dmg /= 4;
		super.damage(dmg, src, magic);

	}

	private void resetDeathgaze() {
		beamCharged = false;
		beamCooldown = Random.Int(3,6);
		beamTarget = -1;
		beam = null;
		sprite.idle();
		spend( attackDelay() - cooldown() );
	}

	public void deathGaze(){
		if (!beamCharged || beamCooldown > 0 || beam == null)
			return;
		boolean terrainAffected = false;

		for (int pos : beam.subPath(1, beam.dist)) {

			if (Dungeon.level.flamable[pos]) {

				Dungeon.level.destroy( pos );
				GameScene.updateMap( pos );
				terrainAffected = true;

			}

			Char ch = Actor.findChar( pos );
			if (ch == null) {
				continue;
			}

			if (hit( this, ch, true )) {
				ch.damage( Random.NormalIntRange( 30, 50 ), this, true );

				if (Dungeon.level.heroFOV[pos]) {
					ch.sprite.flash();
					CellEmitter.center( pos ).burst( PurpleParticle.BURST, Random.IntRange( 1, 2 ) );
				}

				if (!ch.isAlive() && ch == Dungeon.hero) {
					Dungeon.fail( getClass() );
					GLog.n( Messages.get(this, "deathgaze_kill") );
				}
			} else {
				ch.sprite.showStatus( CharSprite.NEUTRAL,  ch.defenseVerb() );
			}
		}

		if (terrainAffected) {
			Dungeon.observe();
		}

		resetDeathgaze(); // we just used it, don't want it to immediately use it again lol
	}

	@Override
	public void add(Buff buff) {
		if(buff instanceof Terror && beamCharged) {
			resetDeathgaze();
			sprite.idle();
			sprite.showStatus( CharSprite.NEGATIVE, Messages.get(this, "rage") );
		}
		else super.add(buff);
	}

	private static final String BEAM_TARGET     = "beamTarget";
	private static final String BEAM_COOLDOWN   = "beamCooldown";
	private static final String BEAM_CHARGED    = "beamCharged";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( BEAM_TARGET, beamTarget);
		bundle.put( BEAM_COOLDOWN, beamCooldown );
		bundle.put( BEAM_CHARGED, beamCharged );
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains(BEAM_TARGET))
			beamTarget = bundle.getInt(BEAM_TARGET);
		beamCooldown = bundle.getInt(BEAM_COOLDOWN);
		beamCharged = bundle.getBoolean(BEAM_CHARGED);
	}

	@Override
	public float resist(Class effect) {
		float effectiveness =  super.resist(effect);
		if(effect == WandOfDisintegration.class) effectiveness *= 0.75f; // disint is 0.75x effective, up from 0.5x
		if(effect == Terror.class)				 effectiveness *= 0.33f; // terror is 0.33x effective, up from 0x
		return effectiveness;
	}

	{
		resistances.add( Grim.class );

		resistances.add( DisintegrationTrap.class );
		resistances.add( GrimTrap.class );

		immunities.add( Cripple.class ); // because levitating
	}

	private class Hunting extends Mob.Hunting{
		@Override
		public boolean act(boolean justAlerted) {
			//even if enemy isn't seen, attack them if the beam is charged
			if (beamCharged && enemy != null && canAttack(enemy)) {
				enemySeen = enemyInFOV(); // sync
				return doAttack(enemy);
			}
			return super.act(justAlerted);
		}
	}
}
