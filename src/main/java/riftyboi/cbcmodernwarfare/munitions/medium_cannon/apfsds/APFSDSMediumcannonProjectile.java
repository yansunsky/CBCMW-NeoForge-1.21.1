package riftyboi.cbcmodernwarfare.munitions.medium_cannon.apfsds;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.munitions.config.components.BallisticPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.EntityDamagePropertiesComponent;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareItem;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareMunitionPropertiesHandlers;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.AbstractMediumcannonProjectile;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.ap.APMediumcannonProjectile;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.config.InertMediumcannonProjectileProperties;

import javax.annotation.Nonnull;

public class APFSDSMediumcannonProjectile extends AbstractMediumcannonProjectile {

	public APFSDSMediumcannonProjectile(EntityType<? extends APFSDSMediumcannonProjectile> type, Level level) {
		super(type, level);
	}

	public ItemStack getItem() {
		return CBCModernWarfareItem.APFSDS_MEDIUMCANNON_ROUND.asStack();
	}

	@Nonnull
	@Override
	public EntityDamagePropertiesComponent getDamageProperties() {
		return this.getAllProperties().damage();
	}

	@Nonnull
	@Override
	protected BallisticPropertiesComponent getBallisticProperties() {
		return this.getAllProperties().ballistics();
	}

	protected InertMediumcannonProjectileProperties getAllProperties() {
		return CBCModernWarfareMunitionPropertiesHandlers.INERT_MEDIUMCANNON_PROJECTILE.getPropertiesOf(this);
	}
}
