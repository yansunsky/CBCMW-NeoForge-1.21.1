package riftyboi.cbcmodernwarfare.munitions.medium_cannon.smoke;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import rbasamoyai.createbigcannons.munitions.config.EntityPropertiesTypeHandler;
import rbasamoyai.createbigcannons.munitions.config.components.BallisticPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.EntityDamagePropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.ExplosionPropertiesComponent;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.config.MediumcannonProjectilePropertiesComponent;

public class SmokeMediumcannonProjectilePropertiesHandler extends EntityPropertiesTypeHandler<SmokeMediumcannonProjectileProperties> {

	private static final SmokeMediumcannonProjectileProperties DEFAULT = new SmokeMediumcannonProjectileProperties(BallisticPropertiesComponent.DEFAULT,
		EntityDamagePropertiesComponent.DEFAULT, MediumcannonProjectilePropertiesComponent.DEFAULT,0,0);

	@Override
	protected SmokeMediumcannonProjectileProperties parseJson(ResourceLocation location, JsonObject obj) throws JsonParseException {
		String id = location.toString();
		BallisticPropertiesComponent ballistics = BallisticPropertiesComponent.fromJson(id, obj);
		EntityDamagePropertiesComponent damage = EntityDamagePropertiesComponent.fromJson(id, obj);
		MediumcannonProjectilePropertiesComponent autocannonProperties = MediumcannonProjectilePropertiesComponent.fromJson(id, obj);
		float smokeScale = Mth.clamp(getOrWarn(obj, "smoke_scale", id, 10f, JsonElement::getAsFloat), 1f, 20f);
		int smokeDuration = Math.max(1, getOrWarn(obj, "smoke_duration", id, 300, JsonElement::getAsInt));
		return new SmokeMediumcannonProjectileProperties(ballistics, damage, autocannonProperties, smokeScale, smokeDuration);
	}

	@Override
	protected SmokeMediumcannonProjectileProperties readPropertiesFromNetwork(EntityType<?> entityType, FriendlyByteBuf buf) {
		BallisticPropertiesComponent ballistics = BallisticPropertiesComponent.fromNetwork(buf);
		EntityDamagePropertiesComponent damage = EntityDamagePropertiesComponent.fromNetwork(buf);
		MediumcannonProjectilePropertiesComponent autocannonProperties = MediumcannonProjectilePropertiesComponent.fromNetwork(buf);
		float smokeScale = buf.readFloat();
		int smokeDuration = buf.readVarInt();
		return new SmokeMediumcannonProjectileProperties(ballistics, damage, autocannonProperties, smokeScale, smokeDuration);
	}

	@Override
	protected void writePropertiesToNetwork(SmokeMediumcannonProjectileProperties properties, FriendlyByteBuf buf) {
		properties.ballistics().toNetwork(buf);
		properties.damage().toNetwork(buf);
		properties.mediumcannonProperties().toNetwork(buf);
		buf.writeFloat(properties.smokeScale());
		buf.writeVarInt(properties.smokeDuration());
	}

	@Override protected SmokeMediumcannonProjectileProperties getNoPropertiesValue() { return DEFAULT; }

}
