package com.zrp200.lustrouspixeldungeon.items.food;

import com.zrp200.lustrouspixeldungeon.actors.buffs.Hunger;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.items.Item;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSpriteSheet;

public abstract class Meat extends Food {

    {
        image = ItemSpriteSheet.FOOD_HOLDER;
        energy = 2*(Hunger.HUNGRY)/3;
        value = 5;
        name="meat";
    }

    @Override
    protected void satisfy(Hero hero) {
        super.satisfy(hero);
        effect(hero);
    }

    public static class PlaceHolder extends Meat {
        @Override
        public boolean isSimilar(Item item) {
            return item instanceof MysteryMeat;
        }

        @Override
        public String info() {
            return "";
        }
    }

    public void effect(Hero hero) { } // nothing by default
}
