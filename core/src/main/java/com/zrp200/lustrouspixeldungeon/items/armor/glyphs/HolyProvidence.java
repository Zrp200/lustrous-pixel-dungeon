package com.zrp200.lustrouspixeldungeon.items.armor.glyphs;

import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Bless;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.items.armor.Armor;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSprite;

public class HolyProvidence extends Armor.Glyph {
    private static ItemSprite.Glowing GOLDEN_YELLOW = new ItemSprite.Glowing(0xFFDF00);

    @Override
    public ItemSprite.Glowing glowing() {
        return GOLDEN_YELLOW;
    }

    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {
        int level = Math.max(0,armor.level());
        if(Random.Int(25+level ) >= 23 ) Buff.prolong(defender,Bless.class,5f);
        return damage;
    }
}
