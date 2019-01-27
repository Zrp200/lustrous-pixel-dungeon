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

package com.zrp200.lustrouspixeldungeon.items.spells;

import com.watabou.noosa.audio.Sample;
import com.zrp200.lustrouspixeldungeon.Assets;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.buffs.ArtifactRecharge;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Recharging;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.effects.Lightning;
import com.zrp200.lustrouspixeldungeon.items.quest.MetalShard;
import com.zrp200.lustrouspixeldungeon.items.scrolls.ScrollOfRecharging;
import com.zrp200.lustrouspixeldungeon.levels.traps.Trap;
import com.zrp200.lustrouspixeldungeon.mechanics.Ballistica;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSpriteSheet;
import com.zrp200.lustrouspixeldungeon.tiles.DungeonTilemap;
import com.zrp200.lustrouspixeldungeon.utils.GLog;

public class ReclaimTrap extends TargetedSpell {
	
	{
		image = ItemSpriteSheet.RECLAIM_TRAP;
	}
	
	@Override
	protected void affectTarget(Ballistica bolt, Hero hero) {
		Trap t = Dungeon.level.traps.get(bolt.collisionPos);
		if (t != null && t.active){
			if (!t.visible) t.reveal();
			t.disarm();
			
			Sample.INSTANCE.play( Assets.SND_LIGHTNING );
			hero.sprite.parent.addToFront( new Lightning(DungeonTilemap.tileCenterToWorld(t.pos), hero.sprite.center(), null) );

			Buff.prolong(hero, Recharging.class, 15f);
			Buff.affect(hero, ArtifactRecharge.class).set( 15 );
			Recharging.showVFX(hero);
			
		} else {
			GLog.w(Messages.get(this, "no_trap"));
		}
	}
	
	@Override
	public int price() {
		//prices of ingredients, divided by output quantity
		return Math.round(quantity * ((30 + 100) / 3f));
	}
	
	public static class Recipe extends com.zrp200.lustrouspixeldungeon.items.Recipe.SimpleRecipe {
		
		{
			inputs =  new Class[]{ScrollOfRecharging.class, MetalShard.class};
			inQuantity = new int[]{1, 1};
			
			cost = 8;
			
			output = ReclaimTrap.class;
			outQuantity = 3;
		}
		
	}
	
}
