package riftyboi.cbcmodernwarfare.munitions.medium_cannon.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import rbasamoyai.createbigcannons.munitions.config.EntityPropertiesTypeHandler;
import rbasamoyai.createbigcannons.munitions.config.components.BallisticPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.EntityDamagePropertiesComponent;

public class InertMediumcannonProjectilePropertiesHandler extends EntityPropertiesTypeHandler<InertMediumcannonProjectileProperties> {

	private static final InertMediumcannonProjectileProperties DEFAULT = new InertMediumcannonProjectileProperties(BallisticPropertiesComponent.DEFAULT,
		EntityDamagePropertiesComponent.DEFAULT, MediumcannonProjectilePropertiesComponent.DEFAULT);

	@Override
	protected InertMediumcannonProjectileProperties parseJson(ResourceLocation location, JsonObject obj) throws JsonParseException {
		String id = location.toString();
		BallisticPropertiesComponent ballistics = BallisticPropertiesComponent.fromJson(id, obj);
		EntityDamagePropertiesComponent damage = EntityDamagePropertiesComponent.fromJson(id, obj);
		MediumcannonProjectilePropertiesComponent autocannonProperties = MediumcannonProjectilePropertiesComponent.fromJson(id, obj);
		return new InertMediumcannonProjectileProperties(ballistics, damage, autocannonProperties);
	}

	@Override
	protected InertMediumcannonProjectileProperties readPropertiesFromNetwork(EntityType<?> entityType, FriendlyByteBuf buf) {
		BallisticPropertiesComponent ballistics = BallisticPropertiesComponent.fromNetwork(buf);
		EntityDamagePropertiesComponent damage = EntityDamagePropertiesComponent.fromNetwork(buf);
		MediumcannonProjectilePropertiesComponent autocannonProperties = MediumcannonProjectilePropertiesComponent.fromNetwork(buf);
		return new InertMediumcannonProjectileProperties(ballistics, damage, autocannonProperties);
	}

	@Override
	protected void writePropertiesToNetwork(InertMediumcannonProjectileProperties properties, FriendlyByteBuf buf) {
		properties.ballistics().toNetwork(buf);
		properties.damage().toNetwork(buf);
		properties.mediumcannonProperties().toNetwork(buf);
	}

	@Override protected InertMediumcannonProjectileProperties getNoPropertiesValue() { return DEFAULT; }

}
