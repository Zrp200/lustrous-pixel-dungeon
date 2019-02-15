package com.zrp200.lustrouspixeldungeon.items.weapon.curses;

import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.LustrousPixelDungeon;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.items.armor.Armor;
import com.zrp200.lustrouspixeldungeon.items.armor.curses.AntiEntropy;
import com.zrp200.lustrouspixeldungeon.items.armor.curses.Corrosion;
import com.zrp200.lustrouspixeldungeon.items.armor.curses.Displacement;
import com.zrp200.lustrouspixeldungeon.items.armor.curses.Metabolism;
import com.zrp200.lustrouspixeldungeon.items.armor.curses.Multiplicity;
import com.zrp200.lustrouspixeldungeon.items.armor.curses.Overgrowth;
import com.zrp200.lustrouspixeldungeon.items.armor.curses.Stench;
import com.zrp200.lustrouspixeldungeon.items.armor.curses.Volatility;
import com.zrp200.lustrouspixeldungeon.items.armor.glyphs.Thorns;
import com.zrp200.lustrouspixeldungeon.items.armor.glyphs.Viscosity;
import com.zrp200.lustrouspixeldungeon.items.weapon.Weapon;

import java.util.ArrayList;

public class Chaotic extends WeaponCurse {
    private static WeaponCurse glyphToChaoticCurse(final Armor.Glyph glyph) {
        return new WeaponCurse() {
            @Override
            public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
                Char target1, target2;
                if (Random.Int(2) == 0) { // just like the cursed wand effect
                    target1 = defender;
                    target2 = attacker;
                } else {
                    target1 = attacker;
                    target2 = defender;
                }
                return glyphToCurse(glyph).proc(weapon,target1,target2,damage);
            }
        };
    }
    private static WeaponCurse glyphToChaoticCurse(Class<?extends Armor.Glyph> glyphClass) {
        try {
            return glyphToChaoticCurse(glyphClass.newInstance());
        } catch (Exception e) {
            LustrousPixelDungeon.reportException(e);
            return new Chaotic();
        }
    }
    private static WeaponCurse glyphToCurse(final Armor.Glyph glyph) { // it's useful for the options
        return new WeaponCurse() {
            @Override
            public int proc(Weapon weapon, Char attacker, final Char defender, int damage) {
                final int effectiveLevel = Random.Int(20);
                return glyph.proc(new Armor(Dungeon.depth/5) {
                    @Override public int DRRoll() { return 0; } // weapons proc after damage reduction, not before
                    @Override public int level() { return effectiveLevel; }
                    }, defender, attacker, damage);
            }
        };
    }
    private static WeaponCurse glyphToCurse(Class<?extends Armor.Glyph> glyphClass) {
        try {
            return glyphToCurse(glyphClass.newInstance());
        } catch (Exception e) {
            LustrousPixelDungeon.reportException(e);
            return new Chaotic();
        }
    }

    @SuppressWarnings("unchecked")
    private ArrayList<WeaponCurse> curseList = new ArrayList() {
        {
            for (Class<? extends Weapon.Enchantment> curseClass : curses) try {
                if (curseClass != Chaotic.class && curseClass != Displacing.class) add(curseClass.newInstance());
            } catch (Exception e) { LustrousPixelDungeon.reportException(e); }

            add(new WeaponCurse() {
                @SuppressWarnings("ConstantConditions")
                @Override
                public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
                    WeaponCurse curse = Random.Int(2) == 0 ? new Displacing() : glyphToCurse(Displacement.class);
                    return curse.proc(weapon,attacker,defender,damage);
                }
            });

            Class<? extends Armor.Glyph>[] chaoticArmorCurses = new Class[]{ // these will proc like enchantments.
                AntiEntropy.class,  Volatility.class,   Overgrowth.class,
                Viscosity.class,    Stench.class,       Corrosion.class
            };

            for (Class<?extends Armor.Glyph> curseClass : chaoticArmorCurses)
                add(glyphToChaoticCurse(curseClass));

            add(glyphToCurse( Multiplicity.class ));
            add(glyphToCurse( Metabolism.class ));
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

    private static final String CURSE_LIST = "curseList";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(CURSE_LIST,curseList);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        curseList.clear();
        for(Bundlable item : bundle.getCollection(CURSE_LIST)) curseList.add((WeaponCurse) item);
    }
}
