package riftyboi.cbcmodernwarfare.munitions.autocannon.canister;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import rbasamoyai.createbigcannons.munitions.autocannon.config.AutocannonProjectilePropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.EntityPropertiesTypeHandler;
import rbasamoyai.createbigcannons.munitions.config.components.BallisticPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.EntityDamagePropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.ExplosionPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.fragment_burst.ProjectileBurstParentPropertiesComponent;

/**
 * 筒弹机炮炮弹的属性处理器。
 *
 * <p>1.20.1 → 1.21.1：{@link EntityPropertiesTypeHandler} 的五个抽象方法签名的
 * 逐一经 javap 复核未变（{@code parseJson/readPropertiesFromNetwork/
 * writePropertiesToNetwork/getNoPropertiesValue}），故本类逐行等价搬运。
 *
 * <p>注意 {@code flakBurst} 用 {@code "canister_"} 前缀读 JSON
 * （对应 1.20.1 JSON 里的 {@code canister_spread}/{@code canister_count}）。
 */
public class CanisterAutocannonPropertiesHandler extends EntityPropertiesTypeHandler<CanisterAutocannonProjectileProperties> {

	private static final CanisterAutocannonProjectileProperties DEFAULT = new CanisterAutocannonProjectileProperties(BallisticPropertiesComponent.DEFAULT,
			EntityDamagePropertiesComponent.DEFAULT, AutocannonProjectilePropertiesComponent.DEFAULT, ExplosionPropertiesComponent.DEFAULT,
			ProjectileBurstParentPropertiesComponent.DEFAULT);

	@Override
	protected CanisterAutocannonProjectileProperties parseJson(ResourceLocation location, JsonObject obj) throws JsonParseException {
		String id = location.toString();
		BallisticPropertiesComponent ballistics = BallisticPropertiesComponent.fromJson(id, obj);
		EntityDamagePropertiesComponent damage = EntityDamagePropertiesComponent.fromJson(id, obj);
		AutocannonProjectilePropertiesComponent autocannonProperties = AutocannonProjectilePropertiesComponent.fromJson(id, obj);
		ExplosionPropertiesComponent explosion = ExplosionPropertiesComponent.fromJson(id, obj);
		ProjectileBurstParentPropertiesComponent flakBurst = ProjectileBurstParentPropertiesComponent.fromJson(id, "canister_", obj);
		return new CanisterAutocannonProjectileProperties(ballistics, damage, autocannonProperties, explosion, flakBurst);
	}

	@Override
	protected CanisterAutocannonProjectileProperties readPropertiesFromNetwork(EntityType<?> entityType, FriendlyByteBuf buf) {
		BallisticPropertiesComponent ballistics = BallisticPropertiesComponent.fromNetwork(buf);
		EntityDamagePropertiesComponent damage = EntityDamagePropertiesComponent.fromNetwork(buf);
		AutocannonProjectilePropertiesComponent autocannonProperties = AutocannonProjectilePropertiesComponent.fromNetwork(buf);
		ExplosionPropertiesComponent explosion = ExplosionPropertiesComponent.fromNetwork(buf);
		ProjectileBurstParentPropertiesComponent flakBurst = ProjectileBurstParentPropertiesComponent.fromNetwork(buf);
		return new CanisterAutocannonProjectileProperties(ballistics, damage, autocannonProperties, explosion, flakBurst);
	}

	@Override
	protected void writePropertiesToNetwork(CanisterAutocannonProjectileProperties properties, FriendlyByteBuf buf) {
		properties.ballistics().toNetwork(buf);
		properties.damage().toNetwork(buf);
		properties.autocannonProperties().toNetwork(buf);
		properties.explosion().toNetwork(buf);
		properties.flakBurst().toNetwork(buf);
	}

	@Override
	protected CanisterAutocannonProjectileProperties getNoPropertiesValue() {
		return DEFAULT;
	}

}
