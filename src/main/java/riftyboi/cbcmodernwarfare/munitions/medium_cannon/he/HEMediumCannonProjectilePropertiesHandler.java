package riftyboi.cbcmodernwarfare.munitions.medium_cannon.he;

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

public class HEMediumCannonProjectilePropertiesHandler extends EntityPropertiesTypeHandler<HEMediumcannonProjectileProperties> {

	private static final HEMediumcannonProjectileProperties DEFAULT = new HEMediumcannonProjectileProperties(BallisticPropertiesComponent.DEFAULT,
		EntityDamagePropertiesComponent.DEFAULT, MediumcannonProjectilePropertiesComponent.DEFAULT, ExplosionPropertiesComponent.DEFAULT);

	@Override
	protected HEMediumcannonProjectileProperties parseJson(ResourceLocation location, JsonObject obj) throws JsonParseException {
		String id = location.toString();
		BallisticPropertiesComponent ballistics = BallisticPropertiesComponent.fromJson(id, obj);
		EntityDamagePropertiesComponent damage = EntityDamagePropertiesComponent.fromJson(id, obj);
		MediumcannonProjectilePropertiesComponent autocannonProperties = MediumcannonProjectilePropertiesComponent.fromJson(id, obj);
		ExplosionPropertiesComponent explosion = ExplosionPropertiesComponent.fromJson(id, obj);
		return new HEMediumcannonProjectileProperties(ballistics, damage, autocannonProperties, explosion);
	}

	@Override
	protected HEMediumcannonProjectileProperties readPropertiesFromNetwork(EntityType<?> entityType, FriendlyByteBuf buf) {
		BallisticPropertiesComponent ballistics = BallisticPropertiesComponent.fromNetwork(buf);
		EntityDamagePropertiesComponent damage = EntityDamagePropertiesComponent.fromNetwork(buf);
		MediumcannonProjectilePropertiesComponent autocannonProperties = MediumcannonProjectilePropertiesComponent.fromNetwork(buf);
		ExplosionPropertiesComponent explosion = ExplosionPropertiesComponent.fromNetwork(buf);
		return new HEMediumcannonProjectileProperties(ballistics, damage, autocannonProperties, explosion);
	}

	@Override
	protected void writePropertiesToNetwork(HEMediumcannonProjectileProperties properties, FriendlyByteBuf buf) {
		properties.ballistics().toNetwork(buf);
		properties.damage().toNetwork(buf);
		properties.mediumcannonProperties().toNetwork(buf);
		properties.explosion().toNetwork(buf);
	}

	@Override protected HEMediumcannonProjectileProperties getNoPropertiesValue() { return DEFAULT; }

}
