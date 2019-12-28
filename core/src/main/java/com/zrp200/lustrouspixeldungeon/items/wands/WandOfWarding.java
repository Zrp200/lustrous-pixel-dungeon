package com.zrp200.lustrouspixeldungeon.items.wands;

import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import com.zrp200.lustrouspixeldungeon.Assets;
import com.zrp200.lustrouspixeldungeon.Dungeon;
import com.zrp200.lustrouspixeldungeon.actors.Actor;
import com.zrp200.lustrouspixeldungeon.actors.Char;
import com.zrp200.lustrouspixeldungeon.actors.hero.Hero;
import com.zrp200.lustrouspixeldungeon.actors.mobs.npcs.NPC;
import com.zrp200.lustrouspixeldungeon.effects.MagicMissile;
import com.zrp200.lustrouspixeldungeon.items.weapon.melee.MagesStaff;
import com.zrp200.lustrouspixeldungeon.mechanics.Ballistica;
import com.zrp200.lustrouspixeldungeon.messages.Messages;
import com.zrp200.lustrouspixeldungeon.scenes.GameScene;
import com.zrp200.lustrouspixeldungeon.sprites.CharSprite;
import com.zrp200.lustrouspixeldungeon.sprites.ItemSpriteSheet;
import com.zrp200.lustrouspixeldungeon.sprites.WardSprite;
import com.zrp200.lustrouspixeldungeon.utils.GLog;
import com.zrp200.lustrouspixeldungeon.windows.WndOptions;

public class WandOfWarding extends Wand {

	private static int MAX_TIER = 6;

	{
		collisionProperties = Ballistica.STOP_TARGET;

		image = ItemSpriteSheet.WAND_WARDING;
		usesTargeting = false;
	}
	
	private boolean wardAvailable = true;

	private boolean isValidLocation(int pos) { // checks to see if a ward is here or if we can place one here.
		boolean isValid = curUser.fieldOfView[pos] && Dungeon.level.passable[pos];
		Char ch = Actor.findChar(pos);
		isValid = isValid && (ch == null && canPlaceWard(pos) || ch instanceof Ward);
		if(!isValid) {
			GLog.w( Messages.get(this, "bad_location"));
			return false;
		}
		return true;
	}
	@Override
	public boolean tryToZap(Hero owner, int target) {
		
		int currentWardEnergy = 0;
		for (Char ch : Actor.chars()){
			if (ch instanceof Ward){
				currentWardEnergy += ((Ward) ch).tier + 1;
			}
		}
		
		int maxWardEnergy = 0;
		for (Wand.Charger charger : curUser.buffs(Wand.Charger.class)){
			if (charger.wand() instanceof WandOfWarding){
				maxWardEnergy += 3 + charger.wand().level();
			}
		}
		
		wardAvailable = (currentWardEnergy < maxWardEnergy);
		
		Char ch = Actor.findChar(target);
		if ( ( ch instanceof Ward && ( (Ward) ch ).isSentry() || wardAvailable )
				|| currentWardEnergy+2 <= maxWardEnergy) {
			return isValidLocation(target) && super.tryToZap(owner, target);
		}
		GLog.w( Messages.get(this, "no_more_wards"));
		return false;
	}
	@Override
	protected void onZap(Ballistica bolt) {
		// location checking logic already handled.
		Char ch = Actor.findChar(bolt.collisionPos);
		if (ch instanceof Ward){
			enhanceWard((Ward) ch);
		} else { // if there's no ward here, then place a ward.
			Ward ward = new Ward();
			ward.pos = bolt.collisionPos;
			ward.wandLevel = level();
			GameScene.add(ward, Actor.TICK);
			Dungeon.level.press(ward.pos, ward);
			ward.sprite.emitter().burst(MagicMissile.WardParticle.UP, ward.tier);
		}
	}

	private void enhanceWard(Ward ward) {
		if (ward.wandLevel < level()){
			ward.wandLevel = level();
		}
		int wardTier = ward.tier, wardHP = ward.HP;
		if (wardAvailable && wardTier < MAX_TIER) { // greater sentries cannot be upgraded
			ward.upgrade();
		} else {
			ward.wandHeal();
		}
		if(ward.HP > wardHP || ward.tier > wardTier) // if something happened
			ward.sprite.emitter().burst(MagicMissile.WardParticle.UP, ward.tier);
	}

	@Override
	protected void fx(Ballistica bolt, Callback callback) {
		MagicMissile m = MagicMissile.boltFromChar(curUser.sprite.parent,
				MagicMissile.WARD,
				curUser.sprite,
				bolt.collisionPos,
				callback);

		if (bolt.dist > 10){
			m.setSpeed(bolt.dist*20);
		}
		Sample.INSTANCE.play(Assets.SND_ZAP);
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {

		int level = Math.max( 0, staff.level() );

		// lvl 0 - 20%
		// lvl 1 - 33%
		// lvl 2 - 43%
		if (Random.Int( level + 5 ) >= 4) {
			for (Char ch : Actor.chars()){
				if (ch instanceof Ward && ch.HP < ch.HT){
					boolean wardAvailable = this.wardAvailable; // can't be accidentally upgrading them.
					this.wardAvailable = false;
					enhanceWard( (Ward) ch ); // heal the ward.
					this.wardAvailable = wardAvailable; // resetting to proper value
				}
			}
		}
	}

	@Override
	public void staffFx(MagesStaff.StaffParticle particle) {
		particle.color( 0x8822FF );
		particle.am = 0.3f;
		particle.setLifespan(3f);
		particle.speed.polar(Random.Float(PointF.PI2), 0.3f);
		particle.setSize( 1f, 2f);
		particle.radiateXY(2.5f);
	}

	public static boolean canPlaceWard(int pos){

		for (int i : PathFinder.CIRCLE8){
			if (Actor.findChar(pos+i) instanceof Ward){
				return false;
			}
		}

		return true;

	}
	
	@Override
	public String statsDesc() {
		if (levelKnown)
			return Messages.get(this, "stats_desc", level()+3);
		else
			return Messages.get(this, "stats_desc", 3);
	}

	public static class Ward extends NPC {

		public int tier = 1;
		private int wandLevel = 1;

		private int totalZaps = 0;

		{
			spriteClass = WardSprite.class;

			alignment = Alignment.ALLY;

			properties.add(Property.IMMOVABLE);

			viewDistance = 3;
			state = WANDERING;

			name = Messages.get(this, "name_" + tier );
		}

		public boolean isSentry() {
			return tier > 3;
		}

		public boolean isUpgradable() {
			return tier < MAX_TIER;
		}

		private static final int[] SENTRY_HT = new int[] {30, 48, 70};
		public void upgrade() {
			if( !isUpgradable() ) { // just heal it and stop.
				wandHeal();
				return;
			}

			int healAmount = healAmount();
			tier++; // increase tier now, allowing us to determine if it is now a sentry.
			if( isSentry() ) {
				HP += healAmount;
				int newHT = SENTRY_HT[tier-4];
				int newHP = newHT * HP/HT;
				healAmount += newHP - HP; // updating to match additional HP healed.
				HP = newHP;
				HT = newHT;
				if(healAmount > 0) sprite.showStatus( CharSprite.POSITIVE, String.valueOf(healAmount) );
			}

			viewDistance++;
            name = Messages.get(this, "name_" + tier );
			updateSpriteState();
            GameScene.updateFog(pos, viewDistance+1);
		}

		private int healAmount() {
			int healAmount = 2*(tier-1); // 6 for lesser sentries, 8 for regular sentries
			if(tier == 6) healAmount +=2; // 12 for greater sentries
			return isSentry() ? Math.max(HT-HP, healAmount) : 0;
		}

		private void wandHeal(){
			int healAmount = healAmount();
			if( healAmount > 0 ) {
				HP += healAmount;
				sprite.showStatus(CharSprite.POSITIVE, String.valueOf(healAmount));
			}
		}

		@Override
		public int defenseSkill(Char enemy) {
			if (tier > 3){
				defenseSkill = 4 + Dungeon.depth;
			}
			return super.defenseSkill(enemy);
		}

		@Override
		public int drRoll() {
			if ( isSentry() ){
				return Math.round(Random.NormalIntRange(0, 3 + Dungeon.depth/2) / (7f - tier));
			} else {
				return 0;
			}
		}

		@Override
		public float attackDelay() {
			switch (tier){
				case 1: case 2: default:
					return 2f;
				case 3: case 4:
					return 1.5f;
				case 5: case 6:
					return 1f;
			}
		}

		@Override
		protected boolean canAttack( Char enemy ) {
			return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
		}

		@Override
		protected boolean doAttack(Char enemy) {
			boolean visible = fieldOfView[pos] || fieldOfView[enemy.pos];
			if (visible) {
				sprite.zap( enemy.pos );
			} else {
				zap();
			}

			return !visible;
		}

		int minDamage() {
			return 2+wandLevel;
		}
		int maxDamage() {
			return 4*( minDamage() );
		}

		private void zap() {
			spend( TICK );

			//always hits
			int dmg = Random.NormalIntRange( minDamage(), maxDamage() );
			enemy.damage( dmg, WandOfWarding.class );
			if (enemy.isAlive()){
				Wand.processSoulMark(enemy, wandLevel, 1);
			}

			if (!enemy.isAlive() && enemy == Dungeon.hero) {
				Dungeon.fail( getClass() );
			}
			if( isSentry() ) {
				HP -= tier+1; // silent damage.
				if(HP <= 0) die(this);
			}
			else if(++totalZaps > (tier-1)*2) die(this); // goes 1 -> 3 -> 5
		}

		public void onZapComplete() {
			zap();
			next();
		}

		@Override
		protected boolean getCloser(int target) {
			return false;
		}

		@Override
		protected boolean getFurther(int target) {
			return false;
		}

		@Override
		public CharSprite sprite() {
			WardSprite sprite = (WardSprite) super.sprite();
			sprite.linkVisuals(this);
			return sprite;
		}

		@Override
		public void updateSpriteState() {
			super.updateSpriteState();
			((WardSprite)sprite).updateTier(tier);
			sprite.place(pos);
		}
		
		@Override
		public void destroy() {
			super.destroy();
			Dungeon.observe();
			GameScene.updateFog(pos, viewDistance+1);
		}
		
		@Override
		public boolean canInteract(Hero h) {
			return true;
		}

		@Override
		public boolean interact() {
			GameScene.show(new WndOptions( Messages.get(this, "dismiss_title"),
					Messages.get(this, "dismiss_body"),
					Messages.get(this, "dismiss_confirm"),
					Messages.get(this, "dismiss_cancel") ){
				@Override
				protected void onSelect(int index) {
					if (index == 0){
						die(null);
					}
				}
			});
			return true;
		}

		@Override
		public String description() {
			return Messages.get(this, "desc_" + tier, minDamage(), maxDamage() );
		}

		private static final String TIER = "tier";
		private static final String WAND_LEVEL = "wand_level";
		private static final String TOTAL_ZAPS = "total_zaps";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(TIER, tier);
			bundle.put(WAND_LEVEL, wandLevel);
			bundle.put(TOTAL_ZAPS, totalZaps);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			tier = bundle.getInt(TIER);
			viewDistance = 2 + tier;
			name = Messages.get(this, "name_" + tier );
			wandLevel = bundle.getInt(WAND_LEVEL);
			totalZaps = bundle.getInt(TOTAL_ZAPS);
		}
		
		{
			properties.add(Property.IMMOVABLE);
		}
	}
}
