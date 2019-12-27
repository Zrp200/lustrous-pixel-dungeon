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

package com.zrp200.lustrouspixeldungeon.items.quest;

import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Assets;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Actor;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.actors.mobs.NewbornElemental;
import com.zrp200.lustrouspixeldungeon.effects.CellEmitter;
import com.zrp200.lustrouspixeldungeon.effects.particles.ElmoParticle;
import com.zrp200.lustrouspixeldungeon.items.Heap;
import com.zrp200.lustrouspixeldungeon.items.Item;
import com.zrp200.lustrouspixeldungeon.scenes.GameScene;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSpriteSheet;

import java.util.ArrayList;

import static com.watabou.utils.PathFinder.NEIGHBOURS4;


public class CeremonialCandle extends Item {

	//generated with the wandmaker quest
	public static int ritualPos;

	{
		image = ItemSpriteSheet.CANDLE;

		defaultAction = AC_THROW;

		unique = true;
		stackable = true;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public void doDrop(Hero hero) {
		super.doDrop(hero);
		checkCandles();
	}

	@Override
	protected void onThrow(int cell) {
		super.onThrow(cell);
		checkCandles();
	}

	private static void checkCandles(){
		Heap[] heaps = new Heap[4];
		for(int i=0; i < NEIGHBOURS4.length; i++) heaps[i] = Dungeon.level.heaps.get( ritualPos + NEIGHBOURS4[i] );
		for(Heap heap : heaps)
		{
			if ( heap == null || !(heap.peek() instanceof CeremonialCandle) ) return;
		}
		for(Heap heap : heaps) heap.pickUp();

		NewbornElemental elemental = new NewbornElemental();
		Char ch = Actor.findChar( ritualPos );
		if (ch != null) {
			ArrayList<Integer> candidates = new ArrayList<>();
			for (int n : PathFinder.NEIGHBOURS8)
			{
				int cell = ritualPos + n;
				if ( (Dungeon.level.passable[cell] || Dungeon.level.avoid[cell]) && Actor.findChar( cell ) == null )
				{
					candidates.add( cell );
				}
			}
			//noinspection ConstantConditions
			elemental.pos = candidates.size() > 0 ? Random.element( candidates ) : ritualPos;
		}
		else elemental.pos = ritualPos;
		elemental.state = elemental.HUNTING;
		GameScene.add(elemental, 1);

		for (int i : PathFinder.NEIGHBOURS9) CellEmitter.get(ritualPos+i).burst(ElmoParticle.FACTORY, 10);
		Sample.INSTANCE.play(Assets.SND_BURNING);
	}
}
