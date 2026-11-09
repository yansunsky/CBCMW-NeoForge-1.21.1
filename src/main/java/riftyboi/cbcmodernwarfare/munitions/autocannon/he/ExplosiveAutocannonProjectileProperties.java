package riftyboi.cbcmodernwarfare.munitions.autocannon.he;

import rbasamoyai.createbigcannons.munitions.autocannon.config.AutocannonProjectilePropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.BallisticPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.EntityDamagePropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.ExplosionPropertiesComponent;

/**
 * 高爆（HE）机炮炮弹的属性集。
 *
 * <p>1.20.1 → 1.21.1：字段与顺序完全不变（record 是纯数据，没有 API 变化）。
 */
public record ExplosiveAutocannonProjectileProperties(BallisticPropertiesComponent ballistics, EntityDamagePropertiesComponent damage,
													  AutocannonProjectilePropertiesComponent autocannonProperties,
													  ExplosionPropertiesComponent explosion) {
}
