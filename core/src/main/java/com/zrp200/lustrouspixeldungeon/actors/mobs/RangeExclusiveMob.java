package com.zrp200.lustrouspixeldungeon.actors.mobs;

import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.mechanics.Ballistica;

public class RangeExclusiveMob extends Mob {

    @Override
    protected boolean canAttack( Char enemy ) {
        Ballistica attack = new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE);
        boolean isAdjacent = Dungeon.level.adjacent(pos, enemy.pos);
        return (isAdjacent
                ? isTrapped(target)
                : attack.collisionPos == enemy.pos);
    }

    @Override
    public int attackSkill(Char target) {
        int attackSkill = super.attackSkill(target);
        if(Dungeon.level.adjacent(pos,target.pos)) attackSkill *= 0.75f;
        return attackSkill;
    }

    @Override
    protected boolean getCloser( int target ) {
        if (state == HUNTING) {
            return enemySeen() && getFurther( target );
        } else {
            return super.getCloser( target );
        }
    }
}
