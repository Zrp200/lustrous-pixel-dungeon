package com.zrp200.lustrouspixeldungeon.levels.traps;

import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.blobs.Blob;
import com.zrp200.lustrouspixeldungeon.actors.blobs.ToxicGas;
import com.zrp200.lustrouspixeldungeon.scenes.GameScene;

public class BlobTrap extends Trap {
    {
        shape = GRILL;
    }

    Class<?extends Blob> blobClass;

    @Override
    public void activate() {
        GameScene.add( seedBlob() );
    }

    protected int blobAmount() {
        return 300 + 20*Dungeon.depth;
    }

    protected Blob seedBlob() {
        return Blob.seed(pos, blobAmount(), blobClass);
    }
}
