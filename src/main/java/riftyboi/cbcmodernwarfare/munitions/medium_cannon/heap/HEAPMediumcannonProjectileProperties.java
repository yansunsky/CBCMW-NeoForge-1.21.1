package riftyboi.cbcmodernwarfare.munitions.medium_cannon.heap;

import rbasamoyai.createbigcannons.munitions.big_cannon.config.BigCannonFuzePropertiesComponent;
import rbasamoyai.createbigcannons.munitions.big_cannon.config.BigCannonProjectilePropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.BallisticPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.EntityDamagePropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.ExplosionPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.fragment_burst.ProjectileBurstParentPropertiesComponent;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.config.MediumcannonProjectilePropertiesComponent;

public record HEAPMediumcannonProjectileProperties(BallisticPropertiesComponent ballistics, EntityDamagePropertiesComponent damage,
									  MediumcannonProjectilePropertiesComponent mediumcannonProperties,
									  ExplosionPropertiesComponent explosion, ProjectileBurstParentPropertiesComponent shrapnelBurst) {
}

