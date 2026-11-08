package riftyboi.cbcmodernwarfare.munitions.medium_cannon.hefrag;

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
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.config.MediumcannonProjectilePropertiesComponent;

public class HEFMediumCannonProjectilePropertiesHandler extends EntityPropertiesTypeHandler<HEFMediumcannonProjectileProperties> {

	private static final HEFMediumcannonProjectileProperties DEFAULT = new HEFMediumcannonProjectileProperties(BallisticPropertiesComponent.DEFAULT,
		EntityDamagePropertiesComponent.DEFAULT, MediumcannonProjectilePropertiesComponent.DEFAULT, ExplosionPropertiesComponent.DEFAULT, ProjectileBurstParentPropertiesComponent.DEFAULT);

	@Override
	protected HEFMediumcannonProjectileProperties parseJson(ResourceLocation location, JsonObject obj) throws JsonParseException {
		String id = location.toString();
		BallisticPropertiesComponent ballistics = BallisticPropertiesComponent.fromJson(id, obj);
		EntityDamagePropertiesComponent damage = EntityDamagePropertiesComponent.fromJson(id, obj);
		MediumcannonProjectilePropertiesComponent mediumcannonProperties = MediumcannonProjectilePropertiesComponent.fromJson(id, obj);
		ExplosionPropertiesComponent explosion = ExplosionPropertiesComponent.fromJson(id, obj);
		ProjectileBurstParentPropertiesComponent grapeshotBurst = ProjectileBurstParentPropertiesComponent.fromJson(id, "shrapnel_", obj);
		return new HEFMediumcannonProjectileProperties(ballistics, damage, mediumcannonProperties, explosion, grapeshotBurst);
	}

	@Override
	protected HEFMediumcannonProjectileProperties readPropertiesFromNetwork(EntityType<?> entityType, FriendlyByteBuf buf) {
		BallisticPropertiesComponent ballistics = BallisticPropertiesComponent.fromNetwork(buf);
		EntityDamagePropertiesComponent damage = EntityDamagePropertiesComponent.fromNetwork(buf);
		MediumcannonProjectilePropertiesComponent mediumcannonProperties = MediumcannonProjectilePropertiesComponent.fromNetwork(buf);
		ExplosionPropertiesComponent explosion = ExplosionPropertiesComponent.fromNetwork(buf);
		ProjectileBurstParentPropertiesComponent grapeshotBurst = ProjectileBurstParentPropertiesComponent.fromNetwork(buf);
		return new HEFMediumcannonProjectileProperties(ballistics, damage, mediumcannonProperties, explosion, grapeshotBurst);
	}

	@Override
	protected void writePropertiesToNetwork(HEFMediumcannonProjectileProperties properties, FriendlyByteBuf buf) {
		properties.ballistics().toNetwork(buf);
		properties.damage().toNetwork(buf);
		properties.mediumcannonProperties().toNetwork(buf);
		properties.explosion().toNetwork(buf);
		properties.grapeshotBurst().toNetwork(buf);
	}

	@Override protected HEFMediumcannonProjectileProperties getNoPropertiesValue() { return DEFAULT; }

}
