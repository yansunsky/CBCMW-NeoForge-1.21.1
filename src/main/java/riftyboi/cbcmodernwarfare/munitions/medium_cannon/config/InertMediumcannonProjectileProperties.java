package riftyboi.cbcmodernwarfare.munitions.medium_cannon.config;

import rbasamoyai.createbigcannons.munitions.config.components.BallisticPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.EntityDamagePropertiesComponent;

public record InertMediumcannonProjectileProperties(BallisticPropertiesComponent ballistics, EntityDamagePropertiesComponent damage,
												  MediumcannonProjectilePropertiesComponent mediumcannonProperties) {
}
