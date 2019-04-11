package com.zrp200.lustrouspixeldungeon.items.weapon.curses;

import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.LustrousPixelDungeon;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.items.armor.Armor;
import com.zrp200.lustrouspixeldungeon.items.armor.curses.AntiEntropy;
import com.zrp200.lustrouspixeldungeon.items.armor.curses.Corrosion;
import com.zrp200.lustrouspixeldungeon.items.armor.curses.Metabolism;
import com.zrp200.lustrouspixeldungeon.items.armor.curses.Multiplicity;
import com.zrp200.lustrouspixeldungeon.items.armor.curses.Stench;
import com.zrp200.lustrouspixeldungeon.items.armor.glyphs.Entanglement;
import com.zrp200.lustrouspixeldungeon.items.armor.glyphs.Repulsion;
import com.zrp200.lustrouspixeldungeon.items.armor.glyphs.Viscosity;
import com.zrp200.lustrouspixeldungeon.items.bombs.Bomb;
import com.zrp200.lustrouspixeldungeon.items.weapon.Weapon;
import com.zrp200.lustrouspixeldungeon.items.weapon.missiles.MissileWeapon;

import java.util.ArrayList;

public class Chaotic extends WeaponCurse {
    private static WeaponCurse glyphToChaoticCurse(final Armor.Glyph glyph) {
        return new WeaponCurse() {
            @Override
            public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
                WeaponCurse curse = Random.oneOf( glyphToCurse(glyph), glyphToOffensiveCurse(glyph) );
                return curse.proc(weapon,attacker,defender,damage);
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
    private static WeaponCurse glyphToOffensiveCurse(final Armor.Glyph glyph) {
        return new WeaponCurse() {
            @Override
            public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
                return glyphToCurse(glyph).proc(weapon,defender,attacker,damage);
            }
        };
    }
    private static WeaponCurse glyphToOffensiveCurse(Class<?extends Armor.Glyph> glyphClass) {
        try {
            return glyphToOffensiveCurse(glyphClass.newInstance());
        } catch (Exception e) {
            LustrousPixelDungeon.reportException(e);
            return new Chaotic();
        }
    }
    private static WeaponCurse glyphToCurse(final Armor.Glyph glyph) { // it's useful for the options
        return new WeaponCurse() {
            @Override
            public int proc(Weapon weapon, Char attacker, final Char defender, int damage) {
                final int effectiveLevel = Random.Int(13); // Ah yes, the number of evil
                return glyph.proc(new Armor(Dungeon.depth/5) {
                    @Override public int DRRoll() { return 0; } // weapons proc after damage reduction, not before
                    @Override public int level() { return effectiveLevel; }
                    }, defender, attacker, damage);
            }
        };
    }

    @SuppressWarnings("unchecked")
    private ArrayList<WeaponCurse> curseList = new ArrayList() {
        {
            for (Class<? extends Weapon.Enchantment> curseClass : curses) try {
                if (curseClass != Chaotic.class && curseClass != Displacing.class) // displacing handled elsewhere
                    add(curseClass.newInstance());
            } catch (Exception e) { LustrousPixelDungeon.reportException(e); }

            Class<?extends Armor.Glyph>[] offensiveCurses = new Class[] {
                    Stench.class, Corrosion.class, Entanglement.class
            };
            Class<? extends Armor.Glyph>[] chaoticArmorCurses = new Class[]{ // these can proc in favor of either side.
                    AntiEntropy.class, Viscosity.class, Metabolism.class
            };

            for(Class<?extends Armor.Glyph> glyphClass : offensiveCurses)
                add(glyphToOffensiveCurse(glyphClass));
            for (Class<?extends Armor.Glyph> curseClass : chaoticArmorCurses)
                add(glyphToChaoticCurse(curseClass));

            // "Special" cases
            add(new WeaponCurse() {
                @SuppressWarnings("ConstantConditions")
                @Override
                public int proc(Weapon weapon, Char attacker, Char defender, int damage) { // basically this is displacement and displacing "bundled"
                    if(Random.Int(2) == 0) { // swap
                        Char temp = attacker;
                        attacker = defender;
                        defender = temp;
                    }
                    return new Displacing().proc(weapon,attacker,defender,damage);
                }
            });
            add(new WeaponCurse() {
                @Override
                public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
                    if(Random.Int(10) == 0) // doubled proc rate
                        Multiplicity.proc(defender,attacker);
                    return damage;
                }
            });
            add(new WeaponCurse() {
                @Override
                public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
                    return Random.Int(3) >= 1 // a crude way of halving repulsion proc rate
                        ? glyphToOffensiveCurse(Repulsion.class).proc(weapon, attacker, defender, damage)
                        : damage;
                }
            });

            // manual implementations
            add(new WeaponCurse() {
                @Override
                public int proc(Weapon weapon, Char attacker, Char defender, int damage) { // Volatility
                    if(Random.Int(10) == 0)
                        new Bomb().explode(
                            //will always explode at target location if missile weapon
                                weapon instanceof MissileWeapon || Random.Int(2) == 0 ? defender.pos : attacker.pos);
                    return damage;
                }
            });
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
