package com.zrp200.lustrouspixeldungeon.items.weapon.curses;

import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.LustrousPixelDungeon;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.items.armor.curses.AntiEntropy;
import com.zrp200.lustrouspixeldungeon.items.armor.curses.Multiplicity;
import com.zrp200.lustrouspixeldungeon.items.weapon.Weapon;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Lucky;

import java.util.ArrayList;

public class Malevolent extends WeaponCurse {
    private ArrayList<Weapon.Enchantment> curseList = new ArrayList<>();

    {
        for (Class<? extends Weapon.Enchantment> curseClass : curses)
            try {
                if (curseClass != Malevolent.class) {
                    curseList.add(curseClass.newInstance()); // add standard curses
                }
            } catch (Exception e) {
                LustrousPixelDungeon.reportException(e);
            }
    }

    {
        curseList.add(
            new Lucky() { // Lucky curse
                @Override
                public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
                    attacker.remove(Luck.class); // nope.
                    return super.proc(weapon, attacker, defender, damage);
                }
            }
        );
        curseList.add(
                new WeaponCurse() { // Multiplicity curse
                    @Override public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
                        return new Multiplicity().proc(null, defender, attacker, damage);
                    }
                }
        );

        curseList.add(
            new WeaponCurse() {
                @Override
                public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
                    Char toFreeze, toIgnite;
                    if (Random.Int(2) == 0) { // just like the cursed wand effect
                        toFreeze = defender;
                        toIgnite = attacker;
                    } else {
                        toFreeze = attacker;
                        toIgnite = defender;
                    }
                    return new AntiEntropy().proc(null, toFreeze, toIgnite, damage);
                }
            }
        );
    }

    @SuppressWarnings("ConstantConditions")
    public Weapon.Enchantment randomCurse() {
        return (Weapon.Enchantment) Random.oneOf( curseList.toArray() );
    }

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        return randomCurse().proc( weapon, attacker, defender, damage );
    }
}
