package riftyboi.cbcmodernwarfare.munitions.medium_cannon.smoke;

import rbasamoyai.createbigcannons.munitions.config.components.BallisticPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.EntityDamagePropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.ExplosionPropertiesComponent;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.config.MediumcannonProjectilePropertiesComponent;

public record SmokeMediumcannonProjectileProperties(BallisticPropertiesComponent ballistics, EntityDamagePropertiesComponent damage,
												   MediumcannonProjectilePropertiesComponent mediumcannonProperties, float smokeScale, int smokeDuration) {
}
