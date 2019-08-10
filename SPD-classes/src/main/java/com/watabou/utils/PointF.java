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

package com.watabou.utils;

public class PointF {
	
	public static final float PI	= 3.1415926f;
	public static final float PI2	= PI * 2;
	public static final float G2R	= PI / 180;
	
	public float x;
	public float y;
	
	public PointF() { }
	
	public PointF( float x, float y ) {
		set(x,y);
	}
	
	public PointF( PointF p ) {
		set( p );
	}
	
	public PointF( Point p ) {
		set( p.x, p.y );
	}
	
	public PointF clone() {
		return new PointF( this );
	}
	
	public PointF scale( float f ) {
		return set(x*f, y*f);
	}
	
	public PointF invScale( float f ) {
		return scale(1/f);
	}
	
	public PointF set( float x, float y ) {
		this.x = x;
		this.y = y;
		return this;
	}
	
	public PointF set( PointF p ) {
		return set(p.x,p.y);
	}
	
	public PointF set( float v ) {
		return set(v, v);
	}
	
	public PointF polar( float a, float l ) {
		this.x = l * (float)Math.cos( a );
		this.y = l * (float)Math.sin( a );
		return this;
	}
	
	public PointF offset( float dx, float dy ) {
		return set(x+dx, y+dy);
	}
	
	public PointF offset( PointF p ) {
		return offset(p.x,p.y);
	}
	
	public PointF negate() {
		return set(-x,-y);
	}
	
	public PointF normalize() {
		float l = length();
		return set(x/l,y/l);
	}
	
	public Point floor() {
		return new Point( (int)x, (int)y );
	}
	
	public float length() {
		return (float)Math.sqrt( x * x + y * y );
	}
	
	public static PointF sum( PointF a, PointF b ) {
		return new PointF( a.x + b.x, a.y + b.y );
	}
	
	public static PointF diff( PointF a, PointF b ) {
		return new PointF( a.x - b.x, a.y - b.y );
	}
	
	public static PointF inter( PointF a, PointF b, float d ) {
		return new PointF( a.x + (b.x - a.x) * d, a.y + (b.y - a.y) * d );
	}
	
	public static float distance( PointF a, PointF b ) {
		float dx = a.x - b.x;
		float dy = a.y - b.y;
		return (float)Math.sqrt( dx * dx + dy * dy );
	}
	
	public static float angle( PointF start, PointF end ) {
		return (float)Math.atan2( end.y - start.y, end.x - start.x );
	}
	
	@Override
	public String toString() {
		return "" + x + ", " + y;
	}

	@Override
	public boolean equals(Object o) {
		if (super.equals(o))
			return true;
		return o instanceof PointF && (((PointF)o).x == x && ((PointF)o).y == y);
	}
}
