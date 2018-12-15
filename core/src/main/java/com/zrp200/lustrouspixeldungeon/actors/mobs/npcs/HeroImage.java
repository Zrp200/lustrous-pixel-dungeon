    /*
     * Pixel Dungeon
     * Copyright (C) 2012-2015 Oleg Dolya
     *
     * Shattered Pixel Dungeon
     * Copyright (C) 2014-2018 Evan Debenham
     *
     * This program is free software: you can redistribute it and/or modify
     * it under the terms of the GNU General Public License as published by
     * the Free Software Foundation, either version 3 of the License, or
     * (at your option) any later version.
     *
     * This program is distributed in the hope that it will be useful,
     * but WITHOUT ANY WARRANTY; without even the implied warranty of
     * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
     * GNU General Public License for more details.
     *
     * You should have received a copy of the GNU General Public License
     * along with this program.  If not, see <http://www.gnu.org/licenses/>
     */

package com.zrp200.lustrouspixeldungeon.actors.mobs.npcs;

    import com.watabou.utils.Bundle;
    import com.zrp200.lustrouspixeldungeon.Dungeon;
    import com.zrp200.lustrouspixeldungeon.actors.Actor;
    import com.zrp200.lustrouspixeldungeon.actors.Char;
    import com.zrp200.lustrouspixeldungeon.actors.blobs.CorrosiveGas;
    import com.zrp200.lustrouspixeldungeon.actors.buffs.Corruption;
    import com.zrp200.lustrouspixeldungeon.actors.buffs.Cripple;
    import com.zrp200.lustrouspixeldungeon.actors.buffs.Ooze;
    import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
    import com.zrp200.lustrouspixeldungeon.actors.mobs.Mob;
    import com.zrp200.lustrouspixeldungeon.sprites.CharSprite;
    import com.zrp200.lustrouspixeldungeon.sprites.MirrorSprite;

public abstract class HeroImage extends NPC {

    {
        spriteClass = MirrorSprite.class;

        defenseSkill = 1;
        alignment = Alignment.ALLY;
        state = HUNTING;

        //before other mobs
        actPriority = MOB_PRIO + 1;
    }

    protected Hero hero;
    private int heroID;
    public int armTier;

    private boolean findHero() {
        hero = (hero != null) ? hero : (Hero) Actor.findById(heroID);
        return hero != null;
    }

    protected abstract void remove();

    @Override
    protected boolean act() {
        if( !findHero() ) {
            remove();
            return true;
        }
        if (hero.tier() != armTier){
            armTier = hero.tier();
            ((MirrorSprite)sprite).updateArmor( armTier );
        }

        return super.act();
    }

    private static final String HEROID	= "hero_id";

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( HEROID, heroID );
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        heroID = bundle.getInt( HEROID );
    }

    public void duplicate( Hero hero ) {
        this.hero = hero;
        heroID = this.hero.id();
    }

    @Override
    public int attackSkill( Char target ) {
            return hero.attackSkill(target);
        }

    @Override
    public int defenseSkill(Char enemy) {
        if (hero != null) {
            int baseEvasion = 4 + hero.lvl;
            int heroEvasion = hero.defenseSkill(enemy);

            //if the hero has more/less evasion, 50% of it is applied
            return super.defenseSkill(enemy) * (baseEvasion + heroEvasion) / 2;
        } else {
            return 0;
        }
    }

    @Override
    protected float attackDelay() {
            return hero.attackDelay(); //handles ring of furor
        }

    @Override
    public int attackProc( Char enemy, int damage ) {
        if (enemy instanceof Mob) ( (Mob) enemy ).aggro( this );
        return super.attackProc(enemy, damage);
    }

    @Override
    public CharSprite sprite() {
        CharSprite s = super.sprite();
        if ( findHero() ) {
            armTier = hero.tier();
        }
        ((MirrorSprite)s).updateArmor( armTier );
        return s;
    }

    @Override
    public boolean interact() {

        if (!Dungeon.level.passable[pos]){
            return true;
        }

        int curPos = pos;

        moveSprite( pos, Dungeon.hero.pos );
        move( Dungeon.hero.pos );

        Dungeon.hero.sprite.move( Dungeon.hero.pos, curPos );
        Dungeon.hero.move( curPos );

        Dungeon.hero.spend( 1 / Dungeon.hero.speed() );
        Dungeon.hero.busy();

        return true;
    }

    {
        properties.add( Property.INORGANIC );
        immunities.add( CorrosiveGas.class );
        immunities.add( Ooze.class );
        immunities.add( Cripple.class );
        immunities.add( Corruption.class );
    }
}
