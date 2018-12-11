package com.zrp200.lustrouspixeldungeon.items.weapon.curses;

import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.LustrousPixelDungeon;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.items.weapon.Weapon;

public class Malevolent extends WeaponCurse {
    private WeaponCurse[] curseList = {
            new Annoying(),
            new Displacing(),
            new Elastic(),
            new Exhausting(),
            new Friendly(),
            new Sacrificial()
    };

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        try {
            return Random.oneOf(curseList).proc( weapon, attacker, defender, damage );
        } catch (Exception e) {
            LustrousPixelDungeon.reportException(e);
            return damage;
        }
    }
}
