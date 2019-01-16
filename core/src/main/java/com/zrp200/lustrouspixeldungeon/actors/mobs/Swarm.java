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
import com.zrp200.lustrouspixeldungeon.actors.buffs.ActiveBuff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Burning;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Corruption;
import com.zrp200.lustrouspixeldungeon.actors.buffs.FlavourBuff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Poison;
import com.zrp200.lustrouspixeldungeon.effects.Pushing;
import com.zrp200.lustrouspixeldungeon.items.Item;
import com.zrp200.lustrouspixeldungeon.items.potions.PotionOfHealing;
import com.zrp200.lustrouspixeldungeon.levels.Terrain;
import com.zrp200.lustrouspixeldungeon.levels.features.Door;
import com.zrp200.lustrouspixeldungeon.scenes.GameScene;
import com.zrp200.lustrouspixeldungeon.sprites.SwarmSprite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Swarm extends Mob {

	{
		spriteClass = SwarmSprite.class;
		
		HP = HT = 50;
		defenseSkill = 5;

		EXP = 3;
		maxLvl = 9;
		
		flying = true;

		loot = new PotionOfHealing();
		lootChance = 0.1667f; //by default, see rollToDropLoot()
	}
	
	private static final float SPLIT_DELAY	= 1f;
	
	private int generation	= 0;
	
	private static final String GENERATION	= "generation";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( GENERATION, generation );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		generation = bundle.getInt( GENERATION );
		if (generation > 0) EXP = 0;
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 1, 4 );
	}

	@Override
	public void damage(int dmg, Object src, boolean magic) {
		super.damage(dmg, src, magic);
		if( isAlive() && needsToSplit ) split();
	}

	private boolean needsToSplit = false;
	@Override
	public int defenseProc( Char enemy, int damage ) {
		needsToSplit = true;
		return super.defenseProc(enemy, damage);
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 10;
	}
	
	private void split() {
		needsToSplit = false;
		ArrayList<Integer> candidates = new ArrayList<>();
		boolean[] solid = Dungeon.level.solid;

		int[] neighbours = {pos + 1, pos - 1, pos + Dungeon.level.width(), pos - Dungeon.level.width()};
		for (int n : neighbours) if (!solid[n] && Actor.findChar( n ) == null) {
			candidates.add( n );
		}

		if ( candidates.isEmpty() ) return;
		Swarm clone = new Swarm();
		clone.generation = generation + 1;
		clone.HP = HP / 2;
		clone.pos = Random.element(candidates);
		clone.state = clone.HUNTING;
		clone.EXP = 0;
		for(FlavourBuff buff : buffs(FlavourBuff.class)) {
			float c = buff.cooldown();
			Buff.detach(buff);
			for(Char ch : Arrays.asList(this,clone)) {
				Buff.affect(ch,buff.getClass(),c/2);
			}
		}
		for(Buff buff : buffs(Buff.class)) {
			Buff.affect(clone,buff.getClass());
		}
		HashSet<ActiveBuff> activeBuffs = buffs(ActiveBuff.class);
		for (ActiveBuff activeBuff : activeBuffs) { // side benefit of defining this as a class
			Buff.affect(clone, activeBuff.getClass()).set(activeBuff.getLeft() / 2);
			activeBuff.set(activeBuff.getLeft() / 2);
		}
		if (buff(Burning.class) != null) {
			Buff.affect(clone, Burning.class).reignite(clone);
		}

		if (buff(Corruption.class) != null) {
			Buff.affect(clone, Corruption.class);
		}

		if (Dungeon.level.map[clone.pos] == Terrain.DOOR) {
			Door.enter(clone.pos);
		}

		GameScene.add(clone, SPLIT_DELAY);
		Actor.addDelayed(new Pushing(clone, pos, clone.pos), -1);

		HP -= clone.HP;
	}
	
	@Override
	public void rollToDropLoot() {
		lootChance = 1f/(6 * (generation+1) );
		lootChance *= (5f - Dungeon.LimitedDrops.SWARM_HP.count) / 5f;
		super.rollToDropLoot();
	}
	
	@Override
	protected Item createLoot(){
		Dungeon.LimitedDrops.SWARM_HP.count++;
		return super.createLoot();
	}
}
