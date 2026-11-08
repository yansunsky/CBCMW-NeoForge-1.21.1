package riftyboi.cbcmodernwarfare.munitions.medium_cannon.ap;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareEntityTypes;
import javax.annotation.Nonnull;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareItem;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareMunitionPropertiesHandlers;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.AbstractMediumcannonProjectile;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonRoundItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.config.MediumcannonProjectilePropertiesComponent;

public class APMediumcannonRoundItem extends MediumcannonRoundItem {

	public APMediumcannonRoundItem(Properties properties) {
		super(properties);
	}

	@Override
	public AbstractMediumcannonProjectile getMediumcannonProjectile(ItemStack stack, Level level) {
		return CBCModernWarfareEntityTypes.AP_ROUND.create(level);
	}
	@Nonnull
	@Override
	public MediumcannonProjectilePropertiesComponent getMediumcannonProperties(ItemStack itemStack) {
		return CBCModernWarfareMunitionPropertiesHandlers.INERT_MEDIUMCANNON_PROJECTILE.getPropertiesOf(this.getEntityType(itemStack)).mediumcannonProperties();
	}

	@Override
	public EntityType<?> getEntityType(ItemStack stack) {
		return CBCModernWarfareEntityTypes.AP_ROUND.get();
	}
	@Override public ItemStack getCartridgeType() { return CBCModernWarfareItem.AP_MEDIUMCANNON_CARTRIDGE.asStack(); }
}
