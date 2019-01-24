package com.zrp200.lustrouspixeldungeon.items.armor.glyphs;

import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Adrenaline;
import com.zrp200.lustrouspixeldungeon.actors.buffs.AdrenalineSurge;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Barkskin;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Bless;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.EarthImbue;
import com.zrp200.lustrouspixeldungeon.actors.buffs.FlavourBuff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.FrostImbue;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Haste;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Recharging;
import com.zrp200.lustrouspixeldungeon.items.armor.Armor;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSprite;

import java.util.HashMap;

public class HolyProvidence extends Armor.Glyph {
    private static ItemSprite.Glowing GOLDEN_YELLOW = new ItemSprite.Glowing(0xffbb00);

    @SuppressWarnings("unchecked")
    private static HashMap<Class<?extends FlavourBuff>,Integer> procEffects = new HashMap() {
        {
            put(AdrenalineSurge.class, 20);
            put(Recharging.class,      18);
            put(FrostImbue.class,      15);
            put(EarthImbue.class,      14);
            put(Haste.class,           12);
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
        int level = Math.max(0,armor.level());
        Class<? extends FlavourBuff> buffClass = (Class<?extends FlavourBuff>)Random.oneOf( procEffects.keySet().toArray() );
        if(Random.Int(30+level) >= 28)
            Buff.prolong( defender, buffClass, Random.Int( 6, procEffects.get(buffClass) ) );
        return damage;
    }
}
