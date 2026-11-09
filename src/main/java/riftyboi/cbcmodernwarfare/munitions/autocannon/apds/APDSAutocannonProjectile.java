package riftyboi.cbcmodernwarfare.munitions.autocannon.apds;

import javax.annotation.Nonnull;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.index.CBCMunitionPropertiesHandlers;
import rbasamoyai.createbigcannons.munitions.autocannon.AbstractAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.autocannon.config.InertAutocannonProjectileProperties;
import rbasamoyai.createbigcannons.munitions.config.components.BallisticPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.EntityDamagePropertiesComponent;

/**
 * APDS（脱壳穿甲弹）机炮炮弹实体。
 *
 * <p>1.20.1 → 1.21.1：业务逻辑零变化；HVAP/APDS 复用 CBC 自带的
 * {@code INERT_AUTOCANNON_PROJECTILE} 属性处理器（与 1.20.1 一致）。
 */
public class APDSAutocannonProjectile extends AbstractAutocannonProjectile {

	public APDSAutocannonProjectile(EntityType<? extends APDSAutocannonProjectile> type, Level level) {
		super(type, level);
	}

	@Nonnull
	@Override
	public EntityDamagePropertiesComponent getDamageProperties() {
		return this.getAllProperties().damage();
	}

	@Nonnull
	@Override
	protected BallisticPropertiesComponent getBallisticProperties() {
		return this.getAllProperties().ballistics();
	}

	protected InertAutocannonProjectileProperties getAllProperties() {
		return CBCMunitionPropertiesHandlers.INERT_AUTOCANNON_PROJECTILE.getPropertiesOf(this);
	}

}
