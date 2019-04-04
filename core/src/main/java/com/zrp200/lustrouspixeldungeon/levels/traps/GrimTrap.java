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
import com.watabou.utils.Callback;
import com.zrp200.lustrouspixeldungeon.Assets;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Actor;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.effects.CellEmitter;
import com.zrp200.lustrouspixeldungeon.effects.MagicMissile;
import com.zrp200.lustrouspixeldungeon.effects.particles.ShadowParticle;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.tiles.DungeonTilemap;
import com.zrp200.lustrouspixeldungeon.utils.GLog;

public class GrimTrap extends AimingTrap {

	{
		color = GREY;
		shape = LARGE_DOT;
	}

	@Override
	public void shoot(final Char target) {
		final GrimTrap trap = this;
		int damage;
			
		//almost kill the player
		if (target == Dungeon.hero && ((float)target.HP/target.HT) >= 0.9f){
			damage = target.HP-1;
			//kill 'em
		} else {
			damage = target.HP;
		}
			
		final int finalDmg = damage;
			
		Actor.add(new Actor() {
			{
				//it's a visual effect, gets priority no matter what
				actPriority = VFX_PRIO;
			}
			@Override
			protected boolean act() {
				final Actor toRemove = this;
				((MagicMissile)target.sprite.parent.recycle(MagicMissile.class)).reset(
						MagicMissile.SHADOW,
						DungeonTilemap.tileCenterToWorld(pos),
						target.sprite.center(),
						new Callback() {
								@Override
								public void call() { 
									target.damage(finalDmg, trap);
									if (target == Dungeon.hero) {
										Sample.INSTANCE.play(Assets.SND_CURSED);
										if (!target.isAlive()) { 
											Dungeon.fail( GrimTrap.class );
											GLog.n( Messages.get(GrimTrap.class, "ondeath") ); 
										} 
									} else {
										Sample.INSTANCE.play(Assets.SND_BURNING); 
									}
									target.sprite.emitter().burst(ShadowParticle.UP, 10);
									Actor.remove(toRemove);
									next(); 
								}
							});
				return false; 
			}
		});
	} 
	public void noTarget() {
		CellEmitter.get(pos).burst(ShadowParticle.UP, 10);
		Sample.INSTANCE.play(Assets.SND_BURNING);
	}
}
