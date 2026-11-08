package riftyboi.cbcmodernwarfare.munitions.medium_cannon.heap;

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

public class HEAPMediumcannonProjectilePropertiesHandler extends EntityPropertiesTypeHandler<HEAPMediumcannonProjectileProperties> {

	private static final HEAPMediumcannonProjectileProperties DEFAULT = new HEAPMediumcannonProjectileProperties(BallisticPropertiesComponent.DEFAULT,
		EntityDamagePropertiesComponent.DEFAULT, MediumcannonProjectilePropertiesComponent.DEFAULT, ExplosionPropertiesComponent.DEFAULT, ProjectileBurstParentPropertiesComponent.DEFAULT);

	@Override
	protected HEAPMediumcannonProjectileProperties parseJson(ResourceLocation location, JsonObject obj) throws JsonParseException {
		String id = location.toString();
		BallisticPropertiesComponent ballistics = BallisticPropertiesComponent.fromJson(id, obj);
		EntityDamagePropertiesComponent damage = EntityDamagePropertiesComponent.fromJson(id, obj);
		MediumcannonProjectilePropertiesComponent autocannonProperties = MediumcannonProjectilePropertiesComponent.fromJson(id, obj);
		ExplosionPropertiesComponent explosion = ExplosionPropertiesComponent.fromJson(id, obj);
		ProjectileBurstParentPropertiesComponent shrapnelBurst = ProjectileBurstParentPropertiesComponent.fromJson(id, "jet_", obj);
		return new HEAPMediumcannonProjectileProperties(ballistics, damage, autocannonProperties, explosion, shrapnelBurst);
	}

	@Override
	protected HEAPMediumcannonProjectileProperties readPropertiesFromNetwork(EntityType<?> entityType, FriendlyByteBuf buf) {
		BallisticPropertiesComponent ballistics = BallisticPropertiesComponent.fromNetwork(buf);
		EntityDamagePropertiesComponent damage = EntityDamagePropertiesComponent.fromNetwork(buf);
		MediumcannonProjectilePropertiesComponent autocannonProperties = MediumcannonProjectilePropertiesComponent.fromNetwork(buf);
		ExplosionPropertiesComponent explosion = ExplosionPropertiesComponent.fromNetwork(buf);
		ProjectileBurstParentPropertiesComponent shrapnelBurst = ProjectileBurstParentPropertiesComponent.fromNetwork(buf);
		return new HEAPMediumcannonProjectileProperties(ballistics, damage, autocannonProperties, explosion, shrapnelBurst);
	}

	@Override
	protected void writePropertiesToNetwork(HEAPMediumcannonProjectileProperties properties, FriendlyByteBuf buf) {
		properties.ballistics().toNetwork(buf);
		properties.damage().toNetwork(buf);
		properties.mediumcannonProperties().toNetwork(buf);
		properties.explosion().toNetwork(buf);
		properties.shrapnelBurst().toNetwork(buf);
	}

	@Override protected HEAPMediumcannonProjectileProperties getNoPropertiesValue() { return DEFAULT; }

}
