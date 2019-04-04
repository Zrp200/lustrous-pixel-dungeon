package com.zrp200.lustrouspixeldungeon.levels.traps;

import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Actor;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.mechanics.Ballistica;

public abstract class AimingTrap extends Trap {
    {
        hideable = false;
    }
    @Override
    public void activate() {
        Char target = Actor.findChar(pos);

        //find the closest char that can be aimed at
        if (target == null){
            for (Char ch : Actor.chars()){
                Ballistica bolt = new Ballistica(pos, ch.pos, Ballistica.PROJECTILE);
                if (bolt.collisionPos == ch.pos &&
                        (target == null || Dungeon.level.trueDistance(pos, ch.pos) < Dungeon.level.trueDistance(pos, target.pos))){
                    target = ch;
                }
            }
        }
        if(target != null) shoot(target);
        else noTarget();
    }
    public abstract void shoot(Char target);
    public void noTarget() {}
}
