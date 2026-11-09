package riftyboi.cbcmodernwarfare.munitions.autocannon.he;

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

/**
 * 高爆（HE）机炮炮弹的属性处理器。
 *
 * <p>1.20.1 → 1.21.1：{@link EntityPropertiesTypeHandler} 的五个抽象方法签名经
 * javap 复核未变，故本类逐行等价搬运（1.20.1 里多 import 的
 * {@code ProjectileBurstParentPropertiesComponent} 本就未使用，未带过来）。
 */
public class ExplosiveAutocannonPropertiesHandler extends EntityPropertiesTypeHandler<ExplosiveAutocannonProjectileProperties> {

	private static final ExplosiveAutocannonProjectileProperties DEFAULT = new ExplosiveAutocannonProjectileProperties(BallisticPropertiesComponent.DEFAULT,
			EntityDamagePropertiesComponent.DEFAULT, AutocannonProjectilePropertiesComponent.DEFAULT, ExplosionPropertiesComponent.DEFAULT);

	@Override
	protected ExplosiveAutocannonProjectileProperties parseJson(ResourceLocation location, JsonObject obj) throws JsonParseException {
		String id = location.toString();
		BallisticPropertiesComponent ballistics = BallisticPropertiesComponent.fromJson(id, obj);
		EntityDamagePropertiesComponent damage = EntityDamagePropertiesComponent.fromJson(id, obj);
		AutocannonProjectilePropertiesComponent autocannonProperties = AutocannonProjectilePropertiesComponent.fromJson(id, obj);
		ExplosionPropertiesComponent explosion = ExplosionPropertiesComponent.fromJson(id, obj);
		return new ExplosiveAutocannonProjectileProperties(ballistics, damage, autocannonProperties, explosion);
	}

	@Override
	protected ExplosiveAutocannonProjectileProperties readPropertiesFromNetwork(EntityType<?> entityType, FriendlyByteBuf buf) {
		BallisticPropertiesComponent ballistics = BallisticPropertiesComponent.fromNetwork(buf);
		EntityDamagePropertiesComponent damage = EntityDamagePropertiesComponent.fromNetwork(buf);
		AutocannonProjectilePropertiesComponent autocannonProperties = AutocannonProjectilePropertiesComponent.fromNetwork(buf);
		ExplosionPropertiesComponent explosion = ExplosionPropertiesComponent.fromNetwork(buf);
		return new ExplosiveAutocannonProjectileProperties(ballistics, damage, autocannonProperties, explosion);
	}

	@Override
	protected void writePropertiesToNetwork(ExplosiveAutocannonProjectileProperties properties, FriendlyByteBuf buf) {
		properties.ballistics().toNetwork(buf);
		properties.damage().toNetwork(buf);
		properties.autocannonProperties().toNetwork(buf);
		properties.explosion().toNetwork(buf);
	}

	@Override
	protected ExplosiveAutocannonProjectileProperties getNoPropertiesValue() {
		return DEFAULT;
	}

}
