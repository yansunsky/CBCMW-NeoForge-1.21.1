package riftyboi.cbcmodernwarfare.munitions.medium_cannon.heap;

import net.minecraft.core.Position;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.config.CBCConfigs;
import rbasamoyai.createbigcannons.munitions.config.components.BallisticPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.EntityDamagePropertiesComponent;
import rbasamoyai.createbigcannons.munitions.fragment_burst.CBCProjectileBurst;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareEntityTypes;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareItem;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareMunitionPropertiesHandlers;
import riftyboi.cbcmodernwarfare.munitions.big_cannon.heap_shell.HEAPExplosion;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonFuzedProjectile;

import javax.annotation.Nonnull;

public class HEAPMediumcannonProjectile extends MediumcannonFuzedProjectile {

	public HEAPMediumcannonProjectile(EntityType<? extends HEAPMediumcannonProjectile> type, Level level) {
		super(type, level);
	}

	@Override
	protected void detonate(Position position) {
		Vec3 oldDelta = this.getDeltaMovement().normalize().scale(2f);;
		HEAPMediumcannonProjectileProperties properties = this.getAllProperties();
		HEAPExplosion explosion = new HEAPExplosion(this.level(), this, this.indirectArtilleryFire(false), position.x(),
			position.y(), position.z(), this.getAllProperties().explosion().blockDamagePower(),
			this.getAllProperties().explosion().entityDamagePower(), true,
			CBCConfigs.server().munitions.damageRestriction.get().explosiveInteraction());
		CreateBigCannons.handleCustomExplosion(this.level(), explosion);
		CBCProjectileBurst.spawnConeBurst(this.level(), CBCModernWarfareEntityTypes.HEAP_BURST.get(), this.position(), oldDelta,
			properties.shrapnelBurst().burstProjectileCount(), properties.shrapnelBurst().burstSpread());
	}


	public ItemStack getItem() {
		return CBCModernWarfareItem.HEAP_MEDIUMCANNON_ROUND.asStack();
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

	protected HEAPMediumcannonProjectileProperties getAllProperties() {
		return CBCModernWarfareMunitionPropertiesHandlers.HEAP_MEDIUMCANNON.getPropertiesOf(this);
	}

}

