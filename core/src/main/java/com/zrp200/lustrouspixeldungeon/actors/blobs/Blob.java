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

package com.zrp200.lustrouspixeldungeon.actors.blobs;

import com.watabou.utils.Bundle;
import com.watabou.utils.Point;
import com.watabou.utils.Rect;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.LustrousPixelDungeon;
import com.zrp200.lustrouspixeldungeon.actors.Actor;
import com.zrp200.lustrouspixeldungeon.effects.BlobEmitter;
import com.zrp200.lustrouspixeldungeon.levels.Level;
import com.zrp200.lustrouspixeldungeon.messages.Messages;

import static com.zrp200.lustrouspixeldungeon.Dungeon.level;

public class Blob extends Actor {

	{
		actPriority = BLOB_PRIO;
	}
	
	public int volume = 0;
	
	public int[] cur;
	protected int[] off;
	
	public BlobEmitter emitter;

	public Rect area = new Rect();

	private static final String CUR		= "cur";
	private static final String START	= "start";
	private static final String LENGTH	= "length";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		
		if (volume > 0) {
		
			int start;
			for (start=0; start < level.length(); start++) {
				if (cur[start] > 0) {
					break;
				}
			}
			int end;
			for (end= level.length()-1; end > start; end--) {
				if (cur[end] > 0) {
					break;
				}
			}
			
			bundle.put( START, start );
			bundle.put( LENGTH, cur.length );
			bundle.put( CUR, trim( start, end + 1 ) );
			
		}
	}
	
	private int[] trim( int start, int end ) {
		int len = end - start;
		int[] copy = new int[len];
		System.arraycopy( cur, start, copy, 0, len );
		return copy;
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		
		super.restoreFromBundle( bundle );

		if (bundle.contains( CUR )) {

			cur = new int[bundle.getInt(LENGTH)];
			off = new int[cur.length];

			int[] data = bundle.getIntArray(CUR);
			int start = bundle.getInt(START);
			for (int i = 0; i < data.length; i++) {
				cur[i + start] = data[i];
				volume += data[i];
			}

		}
	}
	
	@Override
	public boolean act() {
		
		spend( TICK );
		
		if (volume > 0) {

			if (area.isEmpty())
				setupArea();

			volume = 0;

			evolve();
			int[] tmp = off;
			off = cur;
			cur = tmp;
			
		} else {
			area.setEmpty();
		}
		
		return true;
	}

	public void setupArea(){
		for (int cell=0; cell < cur.length; cell++) {
			if (cur[cell] != 0){
				area.union(cell% level.width(), cell/ level.width());
			}
		}
	}
	
	public void use( BlobEmitter emitter ) {
		this.emitter = emitter;
	}
	
	protected void evolve() {
		applyToBlobArea(new EvolveCallBack() {
			@Override
			public void call() {
				if (!level.solid[cell]) {
					int count = 1;
					int sum = cur[cell];

					if (x > area.left && !level.solid[cell-1]) {
						sum += cur[cell-1];
						count++;
					}
					if (x < area.right && !level.solid[cell+1]) {
						sum += cur[cell+1];
						count++;
					}
					if (y > area.top && !level.solid[cell- level.width()]) {
						sum += cur[cell- level.width()];
						count++;
					}
					if (y < area.bottom && !level.solid[cell+ level.width()]) {
						sum += cur[cell+ level.width()];
						count++;
					}

					int value = sum >= count ? (sum / count) - 1 : 0;
					off[cell] = value;

					if (value > 0){
						if (y < area.top)
							area.top = y;
						else if (y >= area.bottom)
							area.bottom = y+1;
						if (x < area.left)
							area.left = x;
						else if (x >= area.right)
							area.right = x+1;
					}

					volume += value;
				} else {
					off[cell] = 0;
				}
			}
		});
	}


	protected abstract class EvolveCallBack {
		protected int cell, x, y;
		protected boolean observe = false;

		protected abstract void call();

		private void affectCell(int x, int y) {
			this.x = x;
			this.y = y;
			cell = level.pointToCell(new Point(x, y));
			if(level.insideMap(cell)) call();
		}
	}

	protected void applyToBlobArea(int offset, EvolveCallBack callback) {
		boolean observe = false; // for uh, technical things
		for(int x = area.left-1+offset; x <= area.right-offset; x++) {
			for(int y = area.top-1+offset; y <= area.bottom-offset; y++) {
				callback.affectCell(x,y);
				observe = observe || callback.observe;
			}
		}
		if(observe) Dungeon.observe();
	}
	protected final void applyToBlobArea(EvolveCallBack callBack) {
		applyToBlobArea(0, callBack);
	}

	public void seed( Level level, int cell, int amount ) {
		if (cur == null) cur = new int[level.length()];
		if (off == null) off = new int[cur.length];

		cur[cell] += amount;
		volume += amount;

		area.union(cell%level.width(), cell/level.width());
	}

	public void clear( int cell, int amount ) {
		if (volume == 0 || amount < 0) return;
		amount = Math.min(amount, cur[cell]);
		cur[cell] -= amount;
		volume -= amount;
	}
	
	public final void clear( int cell ) {
		clear(cell, Integer.MAX_VALUE);
	}

	public void fullyClear(){
		volume = 0;
		area.setEmpty();
		cur = new int[level.length()];
		off = new int[level.length()];
	}

	public String tileDesc() {
		return Messages.get(this, "desc");
	}

	
	public static<T extends Blob> T seed( int cell, int amount, Class<T> type ) {
		return seed(cell, amount, type, level);
	}
	
	@SuppressWarnings("unchecked")
	public static<T extends Blob> T seed( int cell, int amount, Class<T> type, Level level ) {
		try {
			
			T gas = (T)level.blobs.get( type );
			if (gas == null) {
				gas = type.newInstance();
				level.blobs.put( type, gas );
			}
			
			gas.seed( level, cell, amount );
			
			return gas;
			
		} catch (Exception e) {
			LustrousPixelDungeon.reportException(e);
			return null;
		}
	}

	public int volumeAt(int cell) {
		return volume != 0 ? cur[cell] : 0;
	}

	public static int volumeAt( int cell, Class<? extends Blob> type){
		Blob gas = level.blobs.get( type );
		return gas != null ? gas.volumeAt(cell) : 0;
	}
}
