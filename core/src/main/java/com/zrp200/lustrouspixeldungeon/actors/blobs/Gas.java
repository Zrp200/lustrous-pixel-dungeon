package com.zrp200.lustrouspixeldungeon.actors.blobs;

import com.zrp200.lustrouspixeldungeon.actors.Actor;
import com.zrp200.lustrouspixeldungeon.actors.Char;

public abstract class Gas extends Blob {

    @Override
    protected void evolve() {
        super.evolve();
        applyToBlobArea(1, new EvolveCallBack() {
            @Override
            protected void call() {
                Char ch = Actor.findChar(cell);
                if (cur[cell] > 0 && ch != null && !ch.isImmune( Gas.this.getClass() )) {
                    affectChar(ch);
                }
            }
        });
    }

    protected abstract void affectChar(Char ch);
}
