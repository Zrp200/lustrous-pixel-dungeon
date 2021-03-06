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

package com.zrp200.lustrouspixeldungeon.items.scrolls;

import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Assets;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Actor;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Invisibility;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.effects.Speck;
import com.zrp200.lustrouspixeldungeon.levels.RegularLevel;
import com.zrp200.lustrouspixeldungeon.levels.Terrain;
import com.zrp200.lustrouspixeldungeon.levels.rooms.Room;
import com.zrp200.lustrouspixeldungeon.levels.rooms.secret.SecretRoom;
import com.zrp200.lustrouspixeldungeon.levels.rooms.special.SpecialRoom;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.scenes.CellSelector;
import com.zrp200.lustrouspixeldungeon.scenes.GameScene;
import com.zrp200.lustrouspixeldungeon.sprites.HeroSprite;
import com.zrp200.lustrouspixeldungeon.utils.BArray;
import com.zrp200.lustrouspixeldungeon.utils.GLog;

import java.util.ArrayList;

public class ScrollOfTeleportation extends Scroll {

	{
		initials = 8;
	}
	// prevents reading on boss floors
	@Override
	protected boolean shouldRead(boolean silent) {
		if( Dungeon.bossLevel() )
		{
			if(!silent) GLog.w( Messages.get(ScrollOfTeleportation.class, "no_tele") );
			return false;
		}
		return true;
	}

	@Override
	public void doRead() {

		Sample.INSTANCE.play( Assets.SND_READ );
		Invisibility.dispel();
		
		teleportPreferringUnseen( curUser );
		setKnown();

		readAnimation();
	}
	
	@Override
	public void empoweredRead() {
		
		if (Dungeon.bossLevel()){
			GLog.w( Messages.get(this, "no_tele") );
			return;
		}
		
		GameScene.selectCell(new CellSelector.Listener() {
			@Override
			public void onSelect(Integer target) {
				if (target != null) {
					//time isn't spent
					((HeroSprite)curUser.sprite).read();
					teleportToLocation(curUser, target);
					
				}
			}
			
			@Override
			public String prompt() {
				return Messages.get(ScrollOfTeleportation.class, "prompt");
			}
		});
	}
	
	public static void teleportToLocation(Hero hero, int pos){
		PathFinder.buildDistanceMap(pos, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null));
		if (PathFinder.distance[hero.pos] == Integer.MAX_VALUE
				|| (!Dungeon.level.passable[pos] && !Dungeon.level.avoid[pos])
				|| Actor.findChar(pos) != null){
			GLog.w( Messages.get(ScrollOfTeleportation.class, "cant_reach") );
			return;
		}
		
		appear( hero, pos, true );
		if (!hero.flying) Dungeon.level.press( pos, hero );
		Dungeon.observe();
		GameScene.updateFog();
		
	}

	public static void teleportHero( Hero  hero ) {

		int count = 10;
		int pos;
		do {
			pos = Dungeon.level.randomRespawnCell();
			if (count-- <= 0) {
				break;
			}
		} while (pos == -1);
		
		if (pos == -1 || Dungeon.bossLevel()) {
			
			GLog.w( Messages.get(ScrollOfTeleportation.class, "no_tele") );
			
		} else {
			
			GLog.i( Messages.get(ScrollOfTeleportation.class, "tele") );
			
			appear( hero, pos );
			if (!hero.flying) Dungeon.level.press( pos, hero );
			Dungeon.observe();
			GameScene.updateFog();
			
		}
	}
	
	public static void teleportPreferringUnseen( Hero hero ){
		
		if (Dungeon.bossLevel() || !(Dungeon.level instanceof RegularLevel)){
			teleportHero( hero );
			return;
		}
		
		RegularLevel level = (RegularLevel) Dungeon.level;
		ArrayList<Integer> candidates = new ArrayList<>();
		
		for (Room r : level.rooms()){
			if (r instanceof SpecialRoom){
				int terr;
				boolean locked = false;
				for (Point p : r.getPoints()){
					terr = level.map[level.pointToCell(p)];
					if (terr == Terrain.LOCKED_DOOR || terr == Terrain.BARRICADE){
						locked = true;
						break;
					}
				}
				if (locked){
					continue;
				}
			}
			
			int cell;
			for (Point p : r.charPlaceablePoints(level)){
				cell = level.pointToCell(p);
				if (level.passable[cell] && !level.visited[cell] && Actor.findChar(cell) == null){
					candidates.add(cell);
				}
			}
		}
		
		if (candidates.isEmpty()){
			teleportHero( hero );
		} else {
			int pos = Random.element(candidates);
			boolean secretDoor = false;
			int doorPos = -1;
			if (level.room(pos) instanceof SpecialRoom){
				SpecialRoom room = (SpecialRoom) level.room(pos);
				if (room.entrance() != null){
					doorPos = level.pointToCell(room.entrance());
					for (int i : PathFinder.NEIGHBOURS8){
						if (!room.inside(level.cellToPoint(doorPos + i))
								&& level.passable[doorPos + i]
								&& Actor.findChar(doorPos + i) == null){
							secretDoor = room instanceof SecretRoom;
							pos = doorPos + i;
							break;
						}
					}
				}
			}
			appear( hero, pos );
			if (!hero.flying) Dungeon.level.press( pos, hero );
			if (secretDoor && level.map[doorPos] == Terrain.SECRET_DOOR){
				Sample.INSTANCE.play( Assets.SND_SECRET );
				int oldValue = Dungeon.level.map[doorPos];
				GameScene.discoverTile( doorPos, oldValue );
				Dungeon.level.discover( doorPos );
				ScrollOfMagicMapping.discover( doorPos );
			}
			Dungeon.observe();
			GameScene.updateFog();
		}
		
	}

	public static void appear( Char ch, int pos) {
		appear(ch,pos,false);
	}
	public static void appear( Char ch, int pos, boolean silent ) {

		if(!silent && ch instanceof Hero ) GLog.i( Messages.get(ScrollOfTeleportation.class, "tele") );

		if(ch.sprite != null) ch.sprite.interruptMotion();

		ch.move( pos );
		if (ch.pos == pos) ch.sprite.place( pos );

		if (ch.invisible == 0) {
			ch.sprite.alpha( 0 );
			ch.sprite.parent.add( new AlphaTweener( ch.sprite, 1, 0.4f ) );
		}
		if(ch instanceof Hero || !silent && Dungeon.level.heroFOV[pos]) {
			ch.sprite.emitter().start( Speck.factory(Speck.LIGHT), 0.2f, 3 );
			Sample.INSTANCE.play( Assets.SND_TELEPORT );
		}
	}
	
	@Override
	public int price() {
		return isKnown() ? 30 * quantity : super.price();
	}
}
