package riftyboi.cbcmodernwarfare.munitions.medium_cannon.smoke;

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
import rbasamoyai.createbigcannons.munitions.big_cannon.smoke_shell.SmokeEmitterEntity;
import rbasamoyai.createbigcannons.munitions.big_cannon.smoke_shell.SmokeExplosion;
import rbasamoyai.createbigcannons.munitions.config.MunitionPropertiesHandler;
import rbasamoyai.createbigcannons.munitions.config.components.BallisticPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.EntityDamagePropertiesComponent;
import rbasamoyai.createbigcannons.munitions.fuzes.FuzeItem;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareItem;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareMunitionPropertiesHandlers;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.AbstractMediumcannonProjectile;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonFuzedProjectile;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.aphe.APHEMediumcannonProjectileProperties;


import javax.annotation.Nonnull;

import java.util.function.Predicate;

public class SmokeMediumcannonProjectile extends MediumcannonFuzedProjectile {

	public SmokeMediumcannonProjectile(EntityType<? extends SmokeMediumcannonProjectile> type, Level level) {
		super(type, level);
	}


	@Override
	protected void detonate(Position position) {
		SmokeMediumcannonProjectileProperties properties = this.getAllProperties();
		// 1.21.1：SmokeExplosion 构造多了一个 float（半径）参数，BlockInteraction 从 Level 移到 Explosion
		SmokeExplosion explosion = new SmokeExplosion(this.level(), null, position.x(), position.y(), position.z(), 2,
			2.0f, Explosion.BlockInteraction.KEEP);
		CreateBigCannons.handleCustomExplosion(this.level(), explosion);
		SmokeEmitterEntity smoke = CBCEntityTypes.SMOKE_EMITTER.create(this.level());
		smoke.setPos(new Vec3(position.x(), position.y(), position.z()));
		smoke.setDuration(properties.smokeDuration());
		smoke.setSize(properties.smokeScale());
		this.level().addFreshEntity(smoke);
	}


	public ItemStack getItem() {
		return CBCModernWarfareItem.SMOKE_MEDIUMCANNON_ROUND.asStack();
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

	protected SmokeMediumcannonProjectileProperties getAllProperties() {
		return CBCModernWarfareMunitionPropertiesHandlers.SMOKE_MEDIUMCANNON.getPropertiesOf(this);
	}
}

