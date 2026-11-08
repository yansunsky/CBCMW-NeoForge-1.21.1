package riftyboi.cbcmodernwarfare.munitions.medium_cannon.canister;

import net.minecraft.core.Position;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.config.CBCConfigs;
import rbasamoyai.createbigcannons.index.CBCEntityTypes;
import rbasamoyai.createbigcannons.munitions.ProjectileContext;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakExplosion;
import rbasamoyai.createbigcannons.munitions.big_cannon.shrapnel.ShrapnelExplosion;
import rbasamoyai.createbigcannons.munitions.config.components.BallisticPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.EntityDamagePropertiesComponent;
import rbasamoyai.createbigcannons.munitions.fragment_burst.CBCProjectileBurst;
import rbasamoyai.createbigcannons.munitions.fragment_burst.ProjectileBurstParentPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.fuzes.FuzeItem;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareEntityTypes;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareItem;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareMunitionPropertiesHandlers;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.AbstractMediumcannonProjectile;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonFuzedProjectile;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.aphe.APHEMediumcannonProjectileProperties;

import javax.annotation.Nonnull;

import java.util.function.Predicate;

public class CanisterMediumcannonProjectile extends MediumcannonFuzedProjectile {

	public CanisterMediumcannonProjectile(EntityType<? extends CanisterMediumcannonProjectile> type, Level level) {
		super(type, level);
	}

	@Override
	public void tick() {
		super.tick();
		if ((!this.level().isClientSide && this.fuze.isEmpty())) {
			this.disintegrate();
			this.discard();
		}
	}


	@Override
	protected void detonate(Position position) {
		Vec3 oldDelta = this.getDeltaMovement();
		CanisterMediumcannonProjectileProperties properties = this.getAllProperties();
		ShrapnelExplosion explosion = new ShrapnelExplosion(this.level(), null, this.indirectArtilleryFire(false), position.x(),
			position.y(), position.z(), properties.explosion().blockDamagePower(), properties.explosion().entityDamagePower(),
			CBCConfigs.server().munitions.damageRestriction.get().explosiveInteraction());
		CreateBigCannons.handleCustomExplosion(this.level(), explosion);
		CBCProjectileBurst.spawnConeBurst(this.level(), CBCModernWarfareEntityTypes.CANISTER_BURST.get(), new Vec3(position.x(), position.y(), position.z()),
			oldDelta, properties.grapeshotBurst().burstProjectileCount(), properties.grapeshotBurst().burstSpread());
	}

	protected void disintegrate() {
		CanisterMediumcannonProjectileProperties properties = this.getAllProperties();
		CBCProjectileBurst.spawnConeBurst(this.level(), CBCModernWarfareEntityTypes.CANISTER_BURST.get(), position(),
			this.getDeltaMovement(), properties.grapeshotBurst().burstProjectileCount(), properties.grapeshotBurst().burstSpread());
	}

	public ItemStack getItem() {
		return CBCModernWarfareItem.CANISTER_MEDIUMCANNON_ROUND.asStack();
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

	protected CanisterMediumcannonProjectileProperties getAllProperties() {
		return CBCModernWarfareMunitionPropertiesHandlers.CANISTER_MEDIUMCANNON.getPropertiesOf(this);
	}

}
