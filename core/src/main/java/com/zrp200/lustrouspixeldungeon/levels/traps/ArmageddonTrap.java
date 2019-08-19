package com.zrp200.lustrouspixeldungeon.levels.traps;

import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Assets;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Actor;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.blobs.Blob;
import com.zrp200.lustrouspixeldungeon.actors.blobs.Fire;
import com.zrp200.lustrouspixeldungeon.actors.blobs.Regrowth;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.actors.mobs.Mob;
import com.zrp200.lustrouspixeldungeon.effects.Flare;
import com.zrp200.lustrouspixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.zrp200.lustrouspixeldungeon.items.wands.CursedWand;
import com.zrp200.lustrouspixeldungeon.items.weapon.enchantments.Blazing;
import com.zrp200.lustrouspixeldungeon.levels.Terrain;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.scenes.GameScene;
import com.zrp200.lustrouspixeldungeon.utils.BArray;
import com.zrp200.lustrouspixeldungeon.utils.GLog;

import java.util.HashSet;

import static com.zrp200.lustrouspixeldungeon.Dungeon.level;

public class ArmageddonTrap extends Trap {
    {
        color = ORANGE;
        shape = LARGE_DOT;
        hideable = false;
    }

    @Override
    public void activate() {
        for(int i = 0; i<7;i++) { // seven mobs... one for each of the seven hells
            Mob mob = level.createMob();
            if(mob == null) break;
            level.mobs.add(mob);
            mob.pos = level.randomRespawnCell();
            GameScene.add(mob, 2);
        }
        HashSet<Char> chars = new HashSet<Char>(level.mobs);
        chars.add(Dungeon.hero);

        // place all characters on a flammable tile...
        for(Char ch : chars) {
            if(ch instanceof Mob && ((Mob)ch).state == ((Mob) ch).PASSIVE || ch.properties().contains(Char.Property.IMMOVABLE))
                continue;
            int tries = 0, heroTries=0;
            int respawn;
            boolean illegal;
            teleport:
            do {
                illegal = false;
                respawn = level.randomRespawnCell();
                for(int dir : PathFinder.NEIGHBOURS8) {
                    if(Actor.findChar(respawn+dir) != null) {
                        illegal = true;
                        continue teleport;
                    }
                    if(ch instanceof Hero && !Terrain.fertile(respawn+dir)&& heroTries++ < 200) {
                        illegal = true;
                        continue teleport;
                    }
                }
            } while(illegal || !Terrain.fertile(respawn) && tries++ < 300);
            ScrollOfTeleportation.appear(ch,respawn,true);
        }

        // flood the dungeon floor with grass...
        for (int i = 0; i < level.length(); i++)
            GameScene.add( Blob.seed(i, 30, Regrowth.class));
        level.blobs.get(Regrowth.class).act(); // get the grass up

        // light 'em up!
        int fire = 0, seed;
        arson:
        do {
            seed = level.randomDestination();
            PathFinder.buildDistanceMap( seed, BArray.not( Dungeon.level.solid, null ), 2 );
            for (int i = 0; i < PathFinder.distance.length; i++)
                if (PathFinder.distance[i] < Integer.MAX_VALUE
                        && Actor.findChar(i) instanceof Hero) {
                    continue arson;
                }
            Fire.ignite(seed, 4);
            fire++;
        } while (fire < 3 || Random.Int(3) != 0);

        // you smell burning...
        GLog.w(Messages.get(CursedWand.class, "fire"));
        Sample.INSTANCE.play(Assets.SND_BURNING);

        // acts like a warping trap just for visual effect.
        WarpingTrap.obfuscateLevel();

        GameScene.flash(new Blazing().glowing().color);
        new Flare(8, 32).color(0xFFFF66, true).show(Dungeon.hero.sprite, 1f); // external vfx
        Sample.INSTANCE.play(Assets.SND_TELEPORT); // sound effect
    }
}