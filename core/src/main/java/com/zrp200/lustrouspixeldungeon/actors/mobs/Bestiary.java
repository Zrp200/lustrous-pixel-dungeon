/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2018 Evan Debenham
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

import java.util.HashMap;

public class Bestiary {
	
	public static HashMap<Class<? extends Mob>,Float> getMobRotation( int depth ){
		HashMap<Class<? extends Mob>,Float> mobs = standardMobRotation( depth );
		addRareMobs(depth, mobs);
		swapMobAlts(mobs);
		return mobs;
	}
	
	//returns a rotation of standard mobs, unshuffled.
	private static HashMap<Class<? extends Mob>,Float> standardMobRotation(final int depth ){
		return new HashMap<Class<? extends Mob>,Float>() {
			{
				switch(depth) {
					// Sewers
					case 1:
					default: // 10 rats.
						put(Rat.class, 10f);
						break;
					case 2:
						put(	Rat.class,  	3f	);
						put(	Gnoll.class,	3f	);
						break;
					case 3:
						put(Rat.class,  	2f);
						put(Gnoll.class,	4f);
						put(Crab.class, 	1f);
						put(Swarm.class,	1f);
						break;

					case 4:
						put(	Rat.class,  	1f	);
						put(	Gnoll.class,	2f	);
						put(	Crab.class, 	3f	);
						put(	Swarm.class,	1f	);

						// Rare
						put(Thief.class,              	0.0125f);
						put(Shaman.MagicMissile.class,	0.010f);
						put(Skeleton.class,            	0.005f);
						break;

					// Prison
					case 6:
						//3x skeleton, 1x thief, 1x swarm
						put(Skeleton.class, 3f);
						put(Thief.class, 1f);
						put(Swarm.class, 1f);
						break;
					case 7:
						//3x skeleton, 1x thief, 1x shaman, 1x guard
						put(Skeleton.class, 3f);
						put(Thief.class, 1f);
						put(Shaman.random(), 1f);
						put(Guard.class, 1f);
						break;
					case 8:
						//3x skeleton, 1x thief, 2x shaman, 2x guard
						put(Skeleton.class, 3f);
						put(Thief.class, 1f);
						put(Shaman.random(), 2f);
						put(Guard.class, 2f);
						break;
					case 9:
						//3x skeleton, 1x thief, 2x shaman, 3x guard
						put(Skeleton.class, 	3f);
						put(Thief.class,    	1f);
						put(Guard.class,    	3f);
						put(Shaman.random(),	2f);
						break;

					// Caves
					case 11:
						//5x bat, 1x brute
						put(Bat.class, 5f);
						put(Brute.class, 1f);
						break;
					case 12:
						//5x bat, 5x brute, 1x spinner, 1x shaman
						put(Bat.class,5f);
						put(Brute.class,5f);
						put(Spinner.class,1f);
						put(Shaman.random(),1f);
						break;
					case 13:
						//1x bat, 3x brute, 1x shaman, 1x spinner
						put(Bat.class, 1f);
						put(Brute.class,3f);
						put(Spinner.class,1f);
						put(Shaman.random(),1f);
						break;
					case 14:
						//1x bat, 3x brute, 1x shaman, 4x spinner
						put(Bat.class, 1f);
						put(Brute.class,3f);
						put(Spinner.class,4f);
						put(Shaman.random(),1f);
						break;

					// City
					case 16:
						//5x elemental, 5x warlock, 1x monk
						put(Elemental.class,5f);
						put(Warlock.class, 2f);
						put(Monk.class,1f);
						break;
					case 17:
						//2x elemental, 2x warlock, 2x monk
						put(Elemental.class, 2f);
						put(Warlock.class, 2f);
						put(Monk.class,2f);

						put(Golem.class,0.1f);
						break;
					case 18:
						//1x elemental, 1x warlock, 2x monk, 1x golem
						put(Elemental.class, 1f);
						put(Warlock.class, 1f);
						put(Monk.class,2f);
						put(Golem.class,1f);
						break;
					case 19:
						//1x elemental, 1x warlock, 2x monk, 3x golem
						put(Elemental.class, 1f);
						put(Warlock.class, 1f);
						put(Monk.class,2f);
						put(Golem.class,3f);
						break;
					// Halls
					case 22:
						//3x succubus, 3x evil eye
						put(Eye.class, 3f);
						put(Succubus.random(), 3f);
						break;
					case 23:
						//2x succubus, 4x evil eye, 2x scorpio
						put(Eye.class, 3f);
						put(Succubus.random(), 2f);
						put(Scorpio.class,2f);
						break;
					case 24:
						//1x succubus, 2x evil eye, 3x scorpio
						put(Eye.class, 2f);
						put(Succubus.random(), 1f);
						put(Scorpio.class,3f);
						break;
				}
			}
		};
	}
	
	//has a chance to add a rarely spawned mobs to the rotation
	public static void addRareMobs( int depth, HashMap<Class<?extends Mob>,Float> rotation ){
		
		switch (depth){
			
			// Sewers
			default:
				return;
				
			// Prison
			case 6:
				rotation.put(Shaman.MagicMissile.class,0.3f);
				rotation.put(Shaman.random(),0.2f);
				return;
			case 9:
				rotation.put(Brute.class,0.01f);
			case 8:
				rotation.put(Bat.class,0.02f);
				return;
				
			// Caves
			case 14:
				rotation.put(Monk.class,0.005f);
				rotation.put(Warlock.class,0.005f); // Because I'm evil.
			case 13:
				rotation.put(Elemental.class,0.02f);
				return;
				
			// City
			case 19:
				rotation.put(Eye.class,0.01f); // BWAHAHAHAHA
			case 18:
				rotation.put(Succubus.random(),0.02f);
		}
	}
	private static HashMap<Class<? extends  Mob>, Class<?extends Mob> > rareAlts =
			new HashMap<Class<? extends Mob>, Class<? extends Mob>>() {
		{
			put(	Rat.class,    	Albino.class  	);
			put(	Thief.class,  	Bandit.class  	);
			put(	Brute.class,  	Shielded.class	);
			put(	Monk.class,   	Senior.class  	);
			put(	Scorpio.class,	Acidic.class  	);
		}
	};

	//switches out regular mobs for their alt versions when appropriate
	private static void swapMobAlts(HashMap<Class<?extends Mob>,Float> rotation){
		for(Class<?extends Mob> mob : rareAlts.keySet()) {
			Float initialValue = rotation.get(mob);
			if(initialValue != null) {
				rotation.put(mob,initialValue*.98f);
				rotation.put(rareAlts.get(mob),initialValue*.02f);
			}
		}
	}
}
