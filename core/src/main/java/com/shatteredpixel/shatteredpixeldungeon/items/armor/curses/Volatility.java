package com.shatteredpixel.shatteredpixeldungeon.items.armor.curses;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;


public class Volatility extends Armor.Glyph {
    private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );
    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {
        if(Random.Int(20)==0)
            new Bomb().explode(defender.pos);
        return damage;
    }

    @Override
    public boolean curse() {
        return true;
    }
    public ItemSprite.Glowing glowing() {return BLACK;}
}
