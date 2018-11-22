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

package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;

public class RingOfWealth extends Ring {
	
	private float triesToDrop = 0;
	
	@Override
	protected RingBuff buff( ) {
		return new Wealth();
	}
	
	public static float dropChanceMultiplier( Char target ){
		return (float)Math.pow(1.2, getBonus(target, Wealth.class)); // +4.3% compared to shattered
	}
	
	public static ArrayList<Item> tryRareDrop(Char target, int tries ){
		if (getBonus(target, Wealth.class) <= 0) return null;
		
		HashSet<Wealth> buffs = target.buffs(Wealth.class);
		float triesToDrop = -1;
		
		//find the largest count (if they aren't synced yet)
		for (Wealth w : buffs){
			if (w.triesToDrop() > triesToDrop){
				triesToDrop = w.triesToDrop();
			}
		}
		
		//reset (if needed), decrement, and store counts
		if (triesToDrop <= 0) triesToDrop += Random.NormalIntRange(15, 60);
		triesToDrop -= dropProgression( target, tries );
		for (Wealth w : buffs){
			w.triesToDrop(triesToDrop);
		}
		
		//now handle reward logic
		if (triesToDrop <= 0){
			return generateRareDrop();
		} else {
			return null;
		}
		
	}
	
	//TODO this is a start, but i'm sure this could be made more interesting...
	private static ArrayList<Item> generateRareDrop(){
		float roll = Random.Float();
		ArrayList<Item> items = new ArrayList<>();
		if (roll < 0.6f){
			Generator.random(); // let's make this......interesting ;)
		} else if (roll < 0.9f){
			switch (Random.Int(3)){
				case 0:
					items.add(Generator.random(Generator.Category.SEED));
					items.add(Generator.random(Generator.Category.SEED));
					items.add(Generator.random(Generator.Category.SEED));
					items.add(Generator.random(Generator.Category.SEED));
					items.add(Generator.random(Generator.Category.SEED));
					break;
				case 1:
					items.add(Generator.random(Random.Int(2) == 0 ? Generator.Category.POTION : Generator.Category.SCROLL ));
					items.add(Generator.random(Random.Int(2) == 0 ? Generator.Category.POTION : Generator.Category.SCROLL ));
					items.add(Generator.random(Random.Int(2) == 0 ? Generator.Category.POTION : Generator.Category.SCROLL ));
					break;
				case 2:
					items.add(new Bomb().random());
					items.add(new Honeypot());
					break;
			}
		} else {
			Gold g = new Gold();
			g.random();
			g.quantity( g.quantity()*5 );
			items.add(g);
		}
		for(int i = 0; i < items.size(); i++) if(Random.Int(5) == 0) { // 20% chance to get an exotic instead O_O
			Item item = items.get(i);
			if(item instanceof Scroll) item = ScrollOfTransmutation.changeScroll( (Scroll) item );
			else if(item instanceof Potion) item = ScrollOfTransmutation.changePotion( (Potion) item );
			items.set(i, item);
		}
		return items;
	}
	
	private static float dropProgression( Char target, int tries ){
		return tries * (float)Math.pow(1.2f, getBonus(target, Wealth.class) - 1 );
	}

	public class Wealth extends RingBuff {
		
		private void triesToDrop( float val){
			triesToDrop = val;
		}
		
		private float triesToDrop(){
			return triesToDrop;
		}
		
		
	}
}
