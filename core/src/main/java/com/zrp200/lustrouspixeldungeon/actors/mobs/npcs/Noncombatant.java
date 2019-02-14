package com.zrp200.lustrouspixeldungeon.actors.mobs.npcs;

import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.buffs.Buff;

public abstract class Noncombatant extends NPC {

    {
        state = PASSIVE;
    }

    @Override
    public int defenseSkill( Char enemy ) {
        return 1000;
    }

    @Override
    public void damage( int dmg, Object src, boolean magic ) { }

    @Override
    public void add( Buff buff ) { }

    @Override
    protected Char chooseEnemy(boolean newEnemy) {
        return null;
    }

    @Override
    public boolean surprisedBy(Char enemy) { return false; }

    @Override
    public void aggro(Char ch) { }

    @Override
    public boolean reset() { return true; }
}
