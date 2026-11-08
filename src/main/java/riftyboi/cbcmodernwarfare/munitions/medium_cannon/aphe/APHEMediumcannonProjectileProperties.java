package riftyboi.cbcmodernwarfare.munitions.medium_cannon.aphe;


import rbasamoyai.createbigcannons.munitions.config.components.BallisticPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.EntityDamagePropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.ExplosionPropertiesComponent;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.config.MediumcannonProjectilePropertiesComponent;

public record APHEMediumcannonProjectileProperties(BallisticPropertiesComponent ballistics, EntityDamagePropertiesComponent damage,
												 MediumcannonProjectilePropertiesComponent mediumcannonProperties,
												 ExplosionPropertiesComponent explosion) {
}
