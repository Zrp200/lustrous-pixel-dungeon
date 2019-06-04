/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2018 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.zrp200.lustrouspixeldungeon.items.weapon.missiles;

import com.zrp200.lustrouspixeldungeon.sprites.ItemSpriteSheet;

public class ObsidianKnife extends ThrowingKnife {
    {
        image = ItemSpriteSheet.OBSIDIAN_KNIFE;

        bones = true;

        tier = 4;
        surpriseToMax = 0.5f;
        value = 7;
    }

    @Override
    public int max(int lvl) {
        tier--;
        int trueMax = super.max(lvl);
        tier++;
        return trueMax;
    }

    @Override
    public int min(int lvl) {
        tier--;
        int trueMin = super.min(lvl);
        tier++;
        return trueMin;
    }
}
