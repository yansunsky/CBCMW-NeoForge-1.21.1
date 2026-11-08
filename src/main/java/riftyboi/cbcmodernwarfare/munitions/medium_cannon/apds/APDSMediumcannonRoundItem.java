package riftyboi.cbcmodernwarfare.munitions.medium_cannon.apds;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareEntityTypes;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareItem;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareMunitionPropertiesHandlers;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.AbstractMediumcannonProjectile;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonRoundItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.config.MediumcannonProjectilePropertiesComponent;

import javax.annotation.Nonnull;

public class APDSMediumcannonRoundItem extends MediumcannonRoundItem {

	public APDSMediumcannonRoundItem(Properties properties) {
		super(properties);
	}

	@Override
	public AbstractMediumcannonProjectile getMediumcannonProjectile(ItemStack stack, Level level) {
		return CBCModernWarfareEntityTypes.APDS_ROUND.create(level);
	}
	@Nonnull
	@Override
	public MediumcannonProjectilePropertiesComponent getMediumcannonProperties(ItemStack itemStack) {
		return CBCModernWarfareMunitionPropertiesHandlers.INERT_MEDIUMCANNON_PROJECTILE.getPropertiesOf(this.getEntityType(itemStack)).mediumcannonProperties();
	}

	@Override
	public EntityType<?> getEntityType(ItemStack stack) {
		return CBCModernWarfareEntityTypes.APDS_ROUND.get();
	}
	@Override public ItemStack getCartridgeType() { return CBCModernWarfareItem.APDS_MEDIUMCANNON_CARTRIDGE.asStack(); }
}
