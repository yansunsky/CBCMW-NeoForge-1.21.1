package riftyboi.cbcmodernwarfare.munitions.smoke_discharger;

import rbasamoyai.createbigcannons.munitions.config.components.BallisticPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.EntityDamagePropertiesComponent;

/**
 * 烟雾排放器炮弹的属性。
 *
 * <p>{@code smokeScale} / {@code smokeDuration} 是本模组自己加的（CBC 的
 * {@code InertAutocannonProjectileProperties} 没有这两项），来自属性 JSON 的
 * {@code smoke_scale} / {@code smoke_duration} 两个键。
 *
 * <p>1.20.1 → 1.21.1：record 形态未变。
 */
public record SmokeDischargerProjectileProperties(BallisticPropertiesComponent ballistics,
		EntityDamagePropertiesComponent damage, float smokeScale, int smokeDuration) {
}
