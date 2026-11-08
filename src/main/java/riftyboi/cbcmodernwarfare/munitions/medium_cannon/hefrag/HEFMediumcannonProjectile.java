package riftyboi.cbcmodernwarfare.munitions.medium_cannon.hefrag;

import javax.annotation.Nonnull;

import net.minecraft.core.Position;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.config.CBCConfigs;
import rbasamoyai.createbigcannons.index.CBCEntityTypes;
import rbasamoyai.createbigcannons.munitions.ShellExplosion;
import rbasamoyai.createbigcannons.munitions.big_cannon.shrapnel.ShrapnelExplosion;
import rbasamoyai.createbigcannons.munitions.config.components.BallisticPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.EntityDamagePropertiesComponent;
import rbasamoyai.createbigcannons.munitions.fragment_burst.CBCProjectileBurst;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareEntityTypes;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareItem;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareMunitionPropertiesHandlers;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonFuzedProjectile;

public class HEFMediumcannonProjectile extends MediumcannonFuzedProjectile {

	public HEFMediumcannonProjectile(EntityType<? extends HEFMediumcannonProjectile> type, Level level) {
		super(type, level);
	}


	@Override
	protected void detonate(Position position) {
		Vec3 oldDelta = this.getDeltaMovement();
		HEFMediumcannonProjectileProperties properties = this.getAllProperties();
		ShellExplosion explosion = new ShellExplosion(this.level(), this, this.indirectArtilleryFire(false), position.x(),
			position.y(), position.z(), this.getAllProperties().explosion().blockDamagePower(),
			this.getAllProperties().explosion().entityDamagePower(), false,
			CBCConfigs.server().munitions.damageRestriction.get().explosiveInteraction());
		CreateBigCannons.handleCustomExplosion(this.level(), explosion);
		CBCProjectileBurst.spawnConeBurst(this.level(), CBCEntityTypes.SHRAPNEL_BURST.get(), new Vec3(position.x(), position.y(), position.z()),
			oldDelta, properties.grapeshotBurst().burstProjectileCount(), properties.grapeshotBurst().burstSpread());
	}

	public ItemStack getItem() {
		return CBCModernWarfareItem.HEF_MEDIUMCANNON_ROUND.asStack();
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

	protected HEFMediumcannonProjectileProperties getAllProperties() {
		return CBCModernWarfareMunitionPropertiesHandlers.HEF_MEDIUMCANNON.getPropertiesOf(this);
	}

}
