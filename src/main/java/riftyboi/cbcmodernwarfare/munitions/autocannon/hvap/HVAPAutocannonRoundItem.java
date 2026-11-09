package riftyboi.cbcmodernwarfare.munitions.autocannon.hvap;

import javax.annotation.Nonnull;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.index.CBCMunitionPropertiesHandlers;
import rbasamoyai.createbigcannons.munitions.autocannon.AbstractAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonRoundItem;
import rbasamoyai.createbigcannons.munitions.autocannon.config.AutocannonProjectilePropertiesComponent;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareEntityTypes;

/**
 * HVAP 机炮弹丸物品。
 *
 * <p>1.20.1 → 1.21.1：本类无 NBT/组件读写，仅签名不变，业务逻辑零变化。
 */
public class HVAPAutocannonRoundItem extends AutocannonRoundItem {

	public HVAPAutocannonRoundItem(Properties properties) {
		super(properties);
	}

	@Override
	public AbstractAutocannonProjectile getAutocannonProjectile(ItemStack stack, Level level) {
		return CBCModernWarfareEntityTypes.HVAP_AUTOCANNON.create(level);
	}

	@Nonnull
	@Override
	public AutocannonProjectilePropertiesComponent getAutocannonProperties(ItemStack itemStack) {
		return CBCMunitionPropertiesHandlers.INERT_AUTOCANNON_PROJECTILE.getPropertiesOf(this.getEntityType(itemStack)).autocannonProperties();
	}

	@Override
	public EntityType<?> getEntityType(ItemStack stack) {
		return CBCModernWarfareEntityTypes.HVAP_AUTOCANNON.get();
	}

}
