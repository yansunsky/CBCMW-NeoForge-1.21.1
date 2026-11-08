package riftyboi.cbcmodernwarfare.munitions.medium_cannon.hefrag;

import rbasamoyai.createbigcannons.munitions.config.components.BallisticPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.EntityDamagePropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.ExplosionPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.fragment_burst.ProjectileBurstParentPropertiesComponent;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.config.MediumcannonProjectilePropertiesComponent;

public record HEFMediumcannonProjectileProperties(BallisticPropertiesComponent ballistics, EntityDamagePropertiesComponent damage,
												  MediumcannonProjectilePropertiesComponent mediumcannonProperties, ExplosionPropertiesComponent explosion,
												  ProjectileBurstParentPropertiesComponent grapeshotBurst) {
}
