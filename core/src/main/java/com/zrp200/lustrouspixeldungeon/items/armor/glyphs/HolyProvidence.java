package com.zrp200.lustrouspixeldungeon.items.armor.glyphs;

import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Adrenaline;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Bless;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.FrostImbue;
import com.zrp200.lustrouspixeldungeon.items.armor.Armor;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSprite;

import java.util.HashMap;

public class HolyProvidence extends Armor.Glyph {
    private static ItemSprite.Glowing GOLDEN_YELLOW = new ItemSprite.Glowing(0xffbb00);

    @SuppressWarnings("unchecked")
    private static HashMap<Class<?extends Buff>,Integer> procEffects = new HashMap() {
        { // keys are buff types (which can actually be active if I want), values are duration
            put(FrostImbue.class,      12);
            put(Bless.class,           10);
            put(Adrenaline.class,       8);
        }
    };

    @Override
    public ItemSprite.Glowing glowing() {
        return GOLDEN_YELLOW;
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {
        Class<? extends Buff> buffClass = (Class<?extends Buff>)Random.oneOf( procEffects.keySet().toArray() );
        if(Random.Int(armor.level()+40) >= 38)
            Buff.prolong(defender, buffClass, Random.Int(6, procEffects.get(buffClass)));
        return damage;
    }
}
