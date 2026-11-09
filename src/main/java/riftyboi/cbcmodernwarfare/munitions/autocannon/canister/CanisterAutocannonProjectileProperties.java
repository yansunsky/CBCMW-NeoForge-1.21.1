package riftyboi.cbcmodernwarfare.munitions.autocannon.canister;

import rbasamoyai.createbigcannons.munitions.autocannon.config.AutocannonProjectilePropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.BallisticPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.EntityDamagePropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.ExplosionPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.fragment_burst.ProjectileBurstParentPropertiesComponent;

/**
 * 筒弹（canister）机炮炮弹的属性集。
 *
 * <p>1.20.1 → 1.21.1：字段与顺序完全不变（record 是纯数据，没有 API 变化）。
 * 注意 {@link ExplosionPropertiesComponent} 内部仍是 1.20.1 的
 * {@code explosive_power} JSON 键，只是取值侧改名（见 handler）。
 */
public record CanisterAutocannonProjectileProperties(BallisticPropertiesComponent ballistics, EntityDamagePropertiesComponent damage,
													 AutocannonProjectilePropertiesComponent autocannonProperties,
													 ExplosionPropertiesComponent explosion,
													 ProjectileBurstParentPropertiesComponent flakBurst) {
}
