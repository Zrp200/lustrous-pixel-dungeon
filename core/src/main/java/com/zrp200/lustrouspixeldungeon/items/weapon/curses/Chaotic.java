package com.zrp200.lustrouspixeldungeon.items.weapon.curses;

import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.LustrousPixelDungeon;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.items.armor.Armor;
import com.zrp200.lustrouspixeldungeon.items.armor.curses.AntiEntropy;
import com.zrp200.lustrouspixeldungeon.items.armor.curses.Corrosion;
import com.zrp200.lustrouspixeldungeon.items.armor.curses.Displacement;
import com.zrp200.lustrouspixeldungeon.items.armor.curses.Multiplicity;
import com.zrp200.lustrouspixeldungeon.items.armor.curses.Overgrowth;
import com.zrp200.lustrouspixeldungeon.items.armor.curses.Stench;
import com.zrp200.lustrouspixeldungeon.items.armor.curses.Volatility;
import com.zrp200.lustrouspixeldungeon.items.armor.glyphs.Viscosity;
import com.zrp200.lustrouspixeldungeon.items.weapon.Weapon;

import java.util.ArrayList;

public class Chaotic extends WeaponCurse {
    private static WeaponCurse glyphToChaoticCurse(final Class<?extends Armor.Glyph> glyphClass) {
        return new WeaponCurse() {
            @Override
            public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
                Char effect1, effect2;
                if (Random.Int(2) == 0) { // just like the cursed wand effect
                    effect1 = defender;
                    effect2 = attacker;
                } else {
                    effect1 = attacker;
                    effect2 = defender;
                }
                return glyphToCurse(glyphClass).proc(weapon,effect1,effect2,damage);
            }
        };
    }
    private static WeaponCurse glyphToCurse(final Class<?extends Armor.Glyph> glyphClass) {
        return new WeaponCurse() {
            @Override
            public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
                try {
                    return glyphClass.newInstance().proc(null, defender, attacker, damage);
                } catch(Exception e) {
                    LustrousPixelDungeon.reportException(e);
                    return damage;
                }
            }
        };
    }
    private static WeaponCurse glyphToOffCurse(final Class<?extends Armor.Glyph> glyphClass) {
        return new WeaponCurse() {
            @Override
            public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
                try {
                    return glyphClass.newInstance().proc(null, attacker, defender, damage);
                } catch(Exception e) {
                    LustrousPixelDungeon.reportException(e);
                    return damage;
                }
            }
        };
    }
    @SuppressWarnings("unchecked")
    private ArrayList<WeaponCurse> curseList = new ArrayList() {
        {
            for (Class<? extends Weapon.Enchantment> curseClass : curses) try {
                if (curseClass != Chaotic.class) add(curseClass.newInstance());
            } catch (Exception e) { LustrousPixelDungeon.reportException(e); }

            Class<? extends Armor.Glyph>[] chaoticArmorCurses = new Class[]{ // these will proc like enchantments.
                AntiEntropy.class,  Volatility.class,   Overgrowth.class,
                Viscosity.class,    Stench.class
            };

            for (Class curse : chaoticArmorCurses) add(glyphToChaoticCurse(curse));

            add(glyphToCurse(Multiplicity.class));
            add(glyphToCurse(Displacement.class));
            add(glyphToOffCurse(Corrosion.class));
        }
    };

    @SuppressWarnings("ConstantConditions")
    public Weapon.Enchantment randomCurse() {
        return (Weapon.Enchantment) Random.oneOf( curseList.toArray() );
    }

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        return randomCurse().proc( weapon, attacker, defender, damage );
    }
}
