/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2019 Evan Debenham
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

package com.zrp200.lustrouspixeldungeon.actors.buffs;

import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.zrp200.lustrouspixeldungeon.Assets;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.effects.SpellSprite;
import com.zrp200.lustrouspixeldungeon.items.BrokenSeal.WarriorShield;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.scenes.GameScene;
import com.zrp200.lustrouspixeldungeon.ui.BuffIndicator;

public class Berserk extends Buff {

	private enum State { NORMAL, BERSERK, RECOVERING }
	private State state = State.NORMAL;

	private static final float
			LEVEL_RECOVER_START	= 3f,	// levels required to regain berserk
			BERSERK_BONUS 		= 2f;	// damage multiplier while berserking

	private float levelRecovery;


	private float power = 0;

	private static final String STATE = "state";
	private static final String LEVEL_RECOVERY = "levelrecovery";
	private static final String POWER = "power";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(STATE, state);
		bundle.put(POWER, power);
		if (state == State.RECOVERING) bundle.put(LEVEL_RECOVERY, levelRecovery);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		state = bundle.getEnum(STATE, State.class);
		if (bundle.contains(POWER)){
			power = bundle.getFloat(POWER);
		} else {
			power = 1f;
		}
		if (state == State.RECOVERING) levelRecovery = bundle.getFloat(LEVEL_RECOVERY);
	}

	@Override
	public boolean act() {
		if (berserking()){
			ShieldBuff buff = target.buff(WarriorShield.class);
			if (target.HP <= 0) {
			    int damage = (int) Math.ceil(target.shielding() * .05f); // 20 turns max, up from 10
				if (buff != null && buff.shielding() > 0) {
                    buff.absorbDamage(damage);
				} else {
					//if there is no shield buff, or it is empty, then try to remove from other shielding buffs
					buff = target.buff(ShieldBuff.class);
					if (buff != null) buff.absorbDamage(damage);
				}
				if (target.shielding() <= 0) {
					target.die(this);
				}
			} else {
				state = State.RECOVERING;
				levelRecovery = LEVEL_RECOVER_START;
				BuffIndicator.refreshHero();
				if (buff != null) buff.absorbDamage(buff.shielding());
				power = 0f;
			}
		} else {
			power -= Math.max(0.1f, power) * 0.1f * Math.pow( ( target.HP / (float) target.HT ), 2); // -10% rage per turn at full hp

			if (power <= 0)
				if (state == State.RECOVERING)
					power = 0f;
				else
					detach();
			BuffIndicator.refreshHero();
		}
		spend(TICK);
		return true;
	}

	public int damageFactor(int dmg) {
		float bonus = Math.min(1+0.5f*power,1.5f);
		if (state == State.BERSERK)
			bonus = BERSERK_BONUS; // gotta kill those enemies before you're basically helpless. Also insurance for power-based bugs
		return Math.round(dmg * bonus);
	}

	public boolean berserking(){
		if (target.HP == 0 && state == State.NORMAL && power >= 1f && target instanceof Hero){

			WarriorShield shield = target.buff(WarriorShield.class);
			if (shield != null){
				state = State.BERSERK;
				BuffIndicator.refreshHero();
				shield.supercharge(shield.maxShield() * 15);

				SpellSprite.show(target, SpellSprite.BERSERK);
				Sample.INSTANCE.play( Assets.SND_CHALLENGE );
				GameScene.flash(0xFF0000);
			}

		}

		return state == State.BERSERK && target.shielding() > 0;
	}
	public float maxPower() {
		return state == State.RECOVERING
				? 1f - ( levelRecovery / LEVEL_RECOVER_START ) // a little more generous than Shattered's 0.
				: 1.1f;
	}
	public void damage(int damage){
		power = Math.min(
				maxPower(),
				power + ( damage)/(float)(target.HT*2+target.HP) ); // The Hero's current HP is factored in now, making it a bit easier to get the power.
		BuffIndicator.refreshHero();
	}

	public void recover(float percent){
		if (levelRecovery > 0) {
			levelRecovery -= percent;
			BuffIndicator.refreshHero();
			if (levelRecovery <= 0) {
				state = State.NORMAL;
				levelRecovery = 0;
			}
		}
	}

	@Override
	public int icon() {
		return BuffIndicator.BERSERK;
	}
	
	@Override
	public void tintIcon(Image icon) {
		float r = 1, g = 1, b = 1;
		switch (state){
			case BERSERK:
				r = 1;
				g = b = 0;
				break;
			case RECOVERING:
				r -= levelRecovery*0.5f;
				g -= levelRecovery*0.3f;
				if(power <= 0f) break;
			default:
				if(state == State.NORMAL) b = 0.5f;
				if (power < 0.5f)         b *= 1-(power/100f);
				else if (power <= 1f) { g *= 1.5f - power; b = 0f; }
				else                  { r = 1; g = b = 0; } // basically berserking at that point.
				break;
		}
		b = Math.max(0,b);
		g = Math.max(0,g);
		icon.hardlight(r,g,b);
	}
	
	@SuppressWarnings("NullableProblems")
	@Override
	public String toString() {
		switch (state){
			case BERSERK:
				return Messages.get(this, "berserk");
			case RECOVERING:
				return Messages.get(this, "recovering");
			case NORMAL: default:
				return Messages.get(this, "angered");
		}
	}

	@Override
	public String desc() {
		String desc = "";
		float dispDamage = (damageFactor(10000) / 100f) - 100f;
		switch (state){
			case BERSERK:	return Messages.get(this, "berserk_desc");
			case RECOVERING:
				desc += Messages.get(this, "recovering_desc", levelRecovery);
				if(power > 0) 	desc += "\n\n";
				else 			break;
			case NORMAL: default:
				desc += Messages.get(this, "angered_desc");
				if(state == State.NORMAL && target instanceof Hero) desc += "\n\n" + Messages.get(this, "berserk_active");
				desc += "\n\n" + Messages.get(this, "rage",Math.floor(power * 100f), dispDamage);
				break;
		}
		return desc;

	}
}