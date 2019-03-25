package com.zrp200.lustrouspixeldungeon.items.weapon.curses;

import com.zrp200.lustrouspixeldungeon.items.weapon.Weapon;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSprite;

public abstract class WeaponCurse extends Weapon.Enchantment {
    public static ItemSprite.Glowing GLOWING = new ItemSprite.Glowing( 0x000000 ); // black
    @Override
    public boolean curse() {
        return true;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return GLOWING;
    }
}
