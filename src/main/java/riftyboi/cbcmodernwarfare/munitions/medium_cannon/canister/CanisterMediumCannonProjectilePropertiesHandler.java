package riftyboi.cbcmodernwarfare.munitions.medium_cannon.canister;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import rbasamoyai.createbigcannons.munitions.config.EntityPropertiesTypeHandler;
import rbasamoyai.createbigcannons.munitions.config.components.BallisticPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.EntityDamagePropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.ExplosionPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.fragment_burst.ProjectileBurstParentPropertiesComponent;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.aphe.APHEMediumcannonProjectileProperties;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.config.MediumcannonProjectilePropertiesComponent;

public class CanisterMediumCannonProjectilePropertiesHandler extends EntityPropertiesTypeHandler<CanisterMediumcannonProjectileProperties> {

	private static final CanisterMediumcannonProjectileProperties DEFAULT = new CanisterMediumcannonProjectileProperties(BallisticPropertiesComponent.DEFAULT,
		EntityDamagePropertiesComponent.DEFAULT, MediumcannonProjectilePropertiesComponent.DEFAULT, ExplosionPropertiesComponent.DEFAULT, ProjectileBurstParentPropertiesComponent.DEFAULT);

	@Override
	protected CanisterMediumcannonProjectileProperties parseJson(ResourceLocation location, JsonObject obj) throws JsonParseException {
		String id = location.toString();
		BallisticPropertiesComponent ballistics = BallisticPropertiesComponent.fromJson(id, obj);
		EntityDamagePropertiesComponent damage = EntityDamagePropertiesComponent.fromJson(id, obj);
		MediumcannonProjectilePropertiesComponent mediumcannonProperties = MediumcannonProjectilePropertiesComponent.fromJson(id, obj);
		ExplosionPropertiesComponent explosion = ExplosionPropertiesComponent.fromJson(id, obj);
		ProjectileBurstParentPropertiesComponent grapeshotBurst = ProjectileBurstParentPropertiesComponent.fromJson(id, "canister_", obj);
		return new CanisterMediumcannonProjectileProperties(ballistics, damage, mediumcannonProperties, explosion, grapeshotBurst);
	}

	@Override
	protected CanisterMediumcannonProjectileProperties readPropertiesFromNetwork(EntityType<?> entityType, FriendlyByteBuf buf) {
		BallisticPropertiesComponent ballistics = BallisticPropertiesComponent.fromNetwork(buf);
		EntityDamagePropertiesComponent damage = EntityDamagePropertiesComponent.fromNetwork(buf);
		MediumcannonProjectilePropertiesComponent mediumcannonProperties = MediumcannonProjectilePropertiesComponent.fromNetwork(buf);
		ExplosionPropertiesComponent explosion = ExplosionPropertiesComponent.fromNetwork(buf);
		ProjectileBurstParentPropertiesComponent grapeshotBurst = ProjectileBurstParentPropertiesComponent.fromNetwork(buf);
		return new CanisterMediumcannonProjectileProperties(ballistics, damage, mediumcannonProperties, explosion, grapeshotBurst);
	}

	@Override
	protected void writePropertiesToNetwork(CanisterMediumcannonProjectileProperties properties, FriendlyByteBuf buf) {
		properties.ballistics().toNetwork(buf);
		properties.damage().toNetwork(buf);
		properties.mediumcannonProperties().toNetwork(buf);
		properties.explosion().toNetwork(buf);
		properties.grapeshotBurst().toNetwork(buf);
	}

	@Override protected CanisterMediumcannonProjectileProperties getNoPropertiesValue() { return DEFAULT; }

}
