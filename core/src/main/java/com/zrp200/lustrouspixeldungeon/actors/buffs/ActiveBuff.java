package com.zrp200.lustrouspixeldungeon.actors.buffs;

import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.ui.BuffIndicator;

public class ActiveBuff extends Buff { // this uses an internal counter so it can act every turn.
    {
        announced = true;
    }
    protected float left, initial, turnReduction = TICK;
    protected float startGrey = 5;

    boolean isDamaging = false;

    @Override
    protected float modifyDurationForResist(float duration, Char target) {
        return isDamaging ? duration : super.modifyDurationForResist(duration, target);
    }

    public float getLeft() {
        return left;
    }

    void prolong( float duration ) {
        left = Math.max(left, duration);
        initial = Math.max(initial, left);
    }

    void set( float duration ) {
        left = initial = duration;
    }

    void extend( float duration ) {
        left += duration;
        initial = Math.max(initial, left);
    }

    public void afflict(float duration) { // sort of a mix between #set and #extend, used by negative buffs
        if(target == null) return;
        float MAX_EXTEND = 2/3f;
        Buff.affect(target, getClass(), Random.Float(duration*MAX_EXTEND));
        Buff.prolong(target, getClass(), duration);
    }

    @Override
    public boolean act() {
        spend(TICK);
        if ( !target.isAlive() || (left -= turnReduction) <= 0 ){
            detach();
        } else {
            BuffIndicator.refreshHero();
        }
        return true;
    }

    @Override
    public void tintIcon(Image icon) {
        greyIcon(icon,startGrey,left);
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", dispTurns(left));
    }

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( "initial", initial );
        bundle.put( "left", left );
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        initial = bundle.getFloat( "initial" );
        left = bundle.getFloat( "left" );
    }


}
