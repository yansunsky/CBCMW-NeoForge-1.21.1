package riftyboi.cbcmodernwarfare.munitions.smoke_discharger;

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

/**
 * 烟雾排放器炮弹的属性处理器。
 *
 * <p>{@code smoke_scale} 夹到 [1, 20]，{@code smoke_duration} 至少 1 tick
 * （默认 10 / 300，与原项目一致）。
 *
 * <p>1.20.1 → 1.21.1：{@link EntityPropertiesTypeHandler} 的四个方法签名未变。
 */
public class SmokeDischargerProjectilePropertiesHandler
		extends EntityPropertiesTypeHandler<SmokeDischargerProjectileProperties> {

	private static final SmokeDischargerProjectileProperties DEFAULT = new SmokeDischargerProjectileProperties(
			BallisticPropertiesComponent.DEFAULT, EntityDamagePropertiesComponent.DEFAULT, 0.0F, 0);

	@Override
	protected SmokeDischargerProjectileProperties parseJson(ResourceLocation location, JsonObject obj)
			throws JsonParseException {
		String id = location.toString();
		BallisticPropertiesComponent ballistics = BallisticPropertiesComponent.fromJson(id, obj);
		EntityDamagePropertiesComponent damage = EntityDamagePropertiesComponent.fromJson(id, obj);
		float smokeScale = Mth.clamp(getOrWarn(obj, "smoke_scale", id, 10.0F, JsonElement::getAsFloat), 1.0F, 20.0F);
		int smokeDuration = Math.max(1, getOrWarn(obj, "smoke_duration", id, 300, JsonElement::getAsInt));
		return new SmokeDischargerProjectileProperties(ballistics, damage, smokeScale, smokeDuration);
	}

	@Override
	protected SmokeDischargerProjectileProperties readPropertiesFromNetwork(EntityType<?> entityType,
			FriendlyByteBuf buf) {
		BallisticPropertiesComponent ballistics = BallisticPropertiesComponent.fromNetwork(buf);
		EntityDamagePropertiesComponent damage = EntityDamagePropertiesComponent.fromNetwork(buf);
		float smokeScale = buf.readFloat();
		int smokeDuration = buf.readVarInt();
		return new SmokeDischargerProjectileProperties(ballistics, damage, smokeScale, smokeDuration);
	}

	@Override
	protected void writePropertiesToNetwork(SmokeDischargerProjectileProperties properties, FriendlyByteBuf buf) {
		properties.ballistics().toNetwork(buf);
		properties.damage().toNetwork(buf);
		buf.writeFloat(properties.smokeScale());
		buf.writeVarInt(properties.smokeDuration());
	}

	@Override
	protected SmokeDischargerProjectileProperties getNoPropertiesValue() {
		return DEFAULT;
	}
}
