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
import com.zrp200.lustrouspixeldungeon.actors.mobs.Statue;
import com.zrp200.lustrouspixeldungeon.effects.Flare;
import com.zrp200.lustrouspixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.zrp200.lustrouspixeldungeon.items.wands.CursedWand;
import com.zrp200.lustrouspixeldungeon.levels.Terrain;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.scenes.GameScene;
import com.zrp200.lustrouspixeldungeon.utils.BArray;
import com.zrp200.lustrouspixeldungeon.utils.GLog;

import java.util.HashSet;

import static com.zrp200.lustrouspixeldungeon.Dungeon.level;

public class ArmageddonTrap extends Trap {
    @Override
    public void activate() {
        for(int i = 0; i<7;i++) { // seven mobs
            Mob mob = level.createMob();
            if(mob == null) break;
            level.mobs.add(mob);
            mob.pos = level.randomRespawnCell();
            GameScene.add(mob, 2);
        }
        HashSet<Char> chars = new HashSet<Char>(Dungeon.level.mobs);
        chars.add(Dungeon.hero);

        for(Char ch : chars) {
            if(ch instanceof Statue || ch.properties().contains(Char.Property.IMMOVABLE)) {
                continue;
            }
            int tries = 0;
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
                }
            } while(illegal || (!Terrain.fertile(respawn) && (tries++ < 150)));
            ScrollOfTeleportation.appear(ch,respawn,true);
        }
        for (int i = 0; i < Dungeon.level.length(); i++){
            GameScene.add( Blob.seed(i, 15, Regrowth.class));
        }
        byte fire = 0;
        int seed;
        arson:
        do {
            seed = level.randomDestination();
            for(int dir : PathFinder.NEIGHBOURS9) {
                if(Actor.findChar(seed+dir) != null) continue arson;
            }
            GameScene.add(Blob.seed(seed, 10, Fire.class));
            fire++;
        } while (fire < 3 || Random.Int(6) != 0);
        BArray.setFalse(Dungeon.level.visited);
        BArray.setFalse(Dungeon.level.mapped);
        GLog.w(Messages.get(CursedWand.class, "fire"));
        level.blobs.get(Regrowth.class).act();
        GameScene.updateFog();
        Dungeon.observe();
        new Flare(8, 32).color(0xFFFF66, true).show(Dungeon.hero.sprite, 1f);
        Sample.INSTANCE.play(Assets.SND_TELEPORT);
    }
}