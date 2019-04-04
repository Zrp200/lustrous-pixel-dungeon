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

package com.zrp200.lustrouspixeldungeon.levels.traps;

import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Assets;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.LustrousPixelDungeon;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.effects.Beam;
import com.zrp200.lustrouspixeldungeon.items.Heap;
import com.zrp200.lustrouspixeldungeon.items.Item;
import com.zrp200.lustrouspixeldungeon.items.bags.Bag;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.tiles.DungeonTilemap;
import com.zrp200.lustrouspixeldungeon.utils.GLog;

public class DisintegrationTrap extends AimingTrap {

	{
		color = VIOLET;
		shape = CROSSHAIR;
		hideable = false;
	}

	@Override
	public void activate() {
		Heap heap = Dungeon.level.heaps.get(pos);
		if (heap != null) heap.explode();
		super.activate();
	}

	@Override
	public void shoot(Char target) {
		if (Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[target.pos]) {
			Sample.INSTANCE.play(Assets.SND_RAY);
			LustrousPixelDungeon.scene().add(new Beam.DeathRay(DungeonTilemap.tileCenterToWorld(pos), target.sprite.center()));
		}
		target.damage( Math.max( target.HT/5, Random.Int(target.HP / 2, 2 * target.HP / 3) ), this );
		if(target != Dungeon.hero) return;

		Hero hero = (Hero)target;
		if (!hero.isAlive()){
			Dungeon.fail( getClass() );
			GLog.n( Messages.get(this, "ondeath") );
		} else {
			Item item = hero.belongings.randomUnequipped();
			Bag bag = hero.belongings.backpack;
			//bags do not protect against this trap
			if (item instanceof Bag){
				bag = (Bag)item;
				item = Random.element(bag.items);
			}
			if (item == null || !item.isDestroyable()) return;
			if (!item.stackable){
				item.detachAll(bag);
				GLog.w( Messages.get(this, "one", item.name()) );
			} else {
				int n = Random.NormalIntRange(1, (item.quantity()+1)/2);
				for(int i = 1; i <= n; i++)
					item.detach(bag);
				GLog.w( Messages.get(this, "some", item.name()) );
			}
		}
	}
}
