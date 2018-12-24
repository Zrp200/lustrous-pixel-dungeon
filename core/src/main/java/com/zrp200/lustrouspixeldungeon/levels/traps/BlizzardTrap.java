package com.zrp200.lustrouspixeldungeon.levels.traps;

import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.blobs.Blizzard;

public class BlizzardTrap extends BlobTrap {
    {
        color = WHITE;
        blobClass = Blizzard.class;
    }

    @Override
    protected int blobAmount() {
        return 64+4*Dungeon.depth;
    }
}
