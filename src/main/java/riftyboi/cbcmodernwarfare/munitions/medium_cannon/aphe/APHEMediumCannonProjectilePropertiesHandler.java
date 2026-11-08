package riftyboi.cbcmodernwarfare.munitions.medium_cannon.aphe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import rbasamoyai.createbigcannons.munitions.config.EntityPropertiesTypeHandler;
import rbasamoyai.createbigcannons.munitions.config.components.BallisticPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.EntityDamagePropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.ExplosionPropertiesComponent;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.config.MediumcannonProjectilePropertiesComponent;

public class APHEMediumCannonProjectilePropertiesHandler extends EntityPropertiesTypeHandler<APHEMediumcannonProjectileProperties> {

	private static final APHEMediumcannonProjectileProperties DEFAULT = new APHEMediumcannonProjectileProperties(BallisticPropertiesComponent.DEFAULT,
		EntityDamagePropertiesComponent.DEFAULT, MediumcannonProjectilePropertiesComponent.DEFAULT, ExplosionPropertiesComponent.DEFAULT);

	@Override
	protected APHEMediumcannonProjectileProperties parseJson(ResourceLocation location, JsonObject obj) throws JsonParseException {
		String id = location.toString();
		BallisticPropertiesComponent ballistics = BallisticPropertiesComponent.fromJson(id, obj);
		EntityDamagePropertiesComponent damage = EntityDamagePropertiesComponent.fromJson(id, obj);
		MediumcannonProjectilePropertiesComponent autocannonProperties = MediumcannonProjectilePropertiesComponent.fromJson(id, obj);
		ExplosionPropertiesComponent explosion = ExplosionPropertiesComponent.fromJson(id, obj);
		return new APHEMediumcannonProjectileProperties(ballistics, damage, autocannonProperties, explosion);
	}

	@Override
	protected APHEMediumcannonProjectileProperties readPropertiesFromNetwork(EntityType<?> entityType, FriendlyByteBuf buf) {
		BallisticPropertiesComponent ballistics = BallisticPropertiesComponent.fromNetwork(buf);
		EntityDamagePropertiesComponent damage = EntityDamagePropertiesComponent.fromNetwork(buf);
		MediumcannonProjectilePropertiesComponent autocannonProperties = MediumcannonProjectilePropertiesComponent.fromNetwork(buf);
		ExplosionPropertiesComponent explosion = ExplosionPropertiesComponent.fromNetwork(buf);
		return new APHEMediumcannonProjectileProperties(ballistics, damage, autocannonProperties, explosion);
	}

	@Override
	protected void writePropertiesToNetwork(APHEMediumcannonProjectileProperties properties, FriendlyByteBuf buf) {
		properties.ballistics().toNetwork(buf);
		properties.damage().toNetwork(buf);
		properties.mediumcannonProperties().toNetwork(buf);
		properties.explosion().toNetwork(buf);
	}

	@Override protected APHEMediumcannonProjectileProperties getNoPropertiesValue() { return DEFAULT; }

}
