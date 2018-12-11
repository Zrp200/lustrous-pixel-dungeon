package com.zrp200.lustrouspixeldungeon.actors.mobs;

import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.mechanics.Ballistica;

public class RangeExclusiveMob extends Mob {
    boolean canRetaliate;
    boolean justRetaliated;
    {
        canRetaliate = false;
        justRetaliated = false;
    }

    @Override
    protected boolean canAttack( Char enemy ) {
        Ballistica attack = new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE);
        boolean isAdjacent = Dungeon.level.adjacent(pos, enemy.pos);
        return (isAdjacent
                ? canRetaliate || !canGetFurther(target)
                : attack.collisionPos == enemy.pos);
    }

    @Override
    protected boolean act() {
        boolean result = super.act(); // do actions
        if(canRetaliate) {
            canRetaliate = false;
            justRetaliated = true;
        }
        else justRetaliated = false;
        return result;
    }

    @Override
    protected boolean getCloser( int target ) {
        if (state == HUNTING) {
            return enemySeen() && getFurther( target );
        } else {
            return super.getCloser( target );
        }
    }

    @Override
    public void damage(int dmg, Object src) {
        canRetaliate = !justRetaliated;
        super.damage(dmg, src);
    }
}
