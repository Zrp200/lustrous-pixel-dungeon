package com.zrp200.lustrouspixeldungeon.items.weapon.curses;

import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;
import com.zrp200.lustrouspixeldungeon.actors.buffs.FlavourBuff;
import com.zrp200.lustrouspixeldungeon.items.weapon.Weapon;

// @goto Mob#die
public class Necromantic extends WeaponCurse {
    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        Buff.affect(defender, Proc.class);
        return damage;
    }

    public static class Proc extends FlavourBuff { }
}
