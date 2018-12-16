package com.zrp200.lustrouspixeldungeon.items.weapon.melee;

import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.messages.Messages;

public class BlockingWeapon extends MeleeWeapon {
    public int maxBlock(int level) {
        return tier;
    }

    final public int maxBlock() {
        return maxBlock(level());
    }

    public int defenseFactor(Char owner, int level) {
        return Math.max(
                0,
                maxBlock(level) - (
                        owner instanceof Hero
                            ? 2*Math.max( 0, STRReq(level) - ( (Hero)owner ).STR() )
                            : 0
                )
        );
    }
    public String baseInfo() {
        int encumbrance = STRReq(levelKnown ? level() : 0) - Dungeon.hero.STR();
        String info = super.baseInfo();
        info += (Messages.get(this, "stats_desc").equals("") ? "\n\n" : "\n");
        if(defenseFactor(Dungeon.hero,level()+1) != defenseFactor(Dungeon.hero) && !levelKnown) {// if the difference would be meaningful and you don't know its level
            info += Messages.get(BlockingWeapon.class, "typical_block", defenseFactor(Dungeon.hero, 0))
                    + (encumbrance > 0 ? " " + Messages.get(BlockingWeapon.class,"typical_encumbrance") : "._");
        }
        else {
            info += Messages.get( BlockingWeapon.class, "block", maxBlock() )
                    + ( encumbrance > 0 ? Messages.get(BlockingWeapon.class,"encumbrance",defenseFactor(Dungeon.hero)) : "._" );
        }
        return info;
    }
}
