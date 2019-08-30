package com.zrp200.lustrouspixeldungeon.items.weapon.missiles;

import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Actor;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.PinCushion;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSpriteSheet;
import com.zrp200.lustrouspixeldungeon.sprites.MissileSprite;
import com.zrp200.lustrouspixeldungeon.utils.BArray;

import java.util.ArrayList;

public class ThrowingGlaive extends Boomerang {
    {
        tier=5;
        sticky=true;
        image = ItemSpriteSheet.THROWING_GLAIVE;
    }

    @Override
    public int maxBase() { // 10-18 base (+2/+2 compared to boomerang).
        return Math.round(super.maxBase()*.9f);
    }

    public static final double RICOCHET_VELOCITY = 2/3d; // faster than returning, but slower than regular so that you don't completely miss it.

    @Override
    public boolean canSurpriseAttack() {
        return !isRichoceting && super.canSurpriseAttack();
    }

    // throw
    // hit, richochet --> returns after first miss.
    // the first hit will always attempt to ricochet.

    private boolean isRichoceting = false;
    public boolean isRichoceting() {
        return isRichoceting;
    }

    private int pos, lastPos;
    public int getLastPos() {return lastPos;}

    private void moveTo(int newPos) {
        lastPos = pos;
        pos = newPos;
    }

    @Override
    protected void onThrow(int cell) {
        isRichoceting = false;
        targets.clear();
        pos = cell;
        lastPos = curUser.pos;
        Char enemy = Actor.findChar(cell);
        if(enemy != null && enemy != curUser) curUser.shoot(enemy, this);
        richocetOff(enemy); // this will eventually call onThrowComplete.
    }

    @Override
    protected void afterThrow(int cell) {
        isRichoceting = false;
        super.afterThrow(cell);
    }

    @Override
    public boolean stickTo(PinCushion p) {
        if( isReturning() && returning().atDest() && super.stickTo(p) ) { // this only happens in an extremely specific situation, where an enemy is at the final position for some reason.
            terminateFlight();
            return true;
        }
        return false;
    }

    private ArrayList<Char> targets = new ArrayList<>();

    private Char getTargetWithin(@SuppressWarnings("SameParameterValue") int limit) {
        PathFinder.buildDistanceMap(pos, BArray.not(Dungeon.level.solid, null), limit);
        for( ArrayList<Integer> dstGroup : PathFinder.sortedMap() ) {
            Random.shuffle(dstGroup);
            for (int cell : dstGroup) {
                Char ch = Actor.findChar(cell);
                if ( ch != null && ch.alignment == Char.Alignment.ENEMY && !targets.contains(ch) )
                    return ch;
            }
        }
        return null;
    }

    private void richocetOff(final Char ch) {
        isRichoceting = true;
        if( ch == null || ch == curUser || isBreaking() || !rangedHit ) {
            onThrowComplete(pos);
            return; // this stops it.
        }

        targets.add(ch);
        final Char newCh = getTargetWithin(2);
        if(newCh == null) {
            onThrowComplete(pos);
            return;
        }

        detachEmbed();
        //terminateFlight();
        MissileSprite sprite = (MissileSprite) curUser.sprite.parent.recycle(MissileSprite.class); // we don't care about the returning sprite.
        rangedHit = false;
        moveTo(newCh.pos);
        sprite.reset(ch.sprite, newCh.sprite, this, new Callback() {
            @Override
            public void call() {
                curUser.shoot(newCh,ThrowingGlaive.this);
                richocetOff(newCh);
            }
        });
    }
}
