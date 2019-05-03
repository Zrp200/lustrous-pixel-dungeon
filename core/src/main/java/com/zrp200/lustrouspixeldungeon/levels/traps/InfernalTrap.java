package com.zrp200.lustrouspixeldungeon.levels.traps;

import com.zrp200.lustrouspixeldungeon.actors.blobs.Inferno;

public class InfernalTrap extends BlobTrap {
    {
        color = ORANGE;
        blobClass = Inferno.class;

        name = "Infernal Trap";
    }

    @Override
    public String desc() {
        return "When activated, this trap will summon a destructive inferno that will reignite anything it touches in flames.";
    }
}
