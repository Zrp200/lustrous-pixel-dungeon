package com.zrp200.lustrouspixeldungeon.levels.traps;

import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.blobs.Inferno;

public class InfernalTrap extends BlobTrap {
    {
        color = RED;
        blobClass = Inferno.class;
    }

    @Override
    protected int blobAmount() {
        return 80+5* Dungeon.depth;
    }
}
