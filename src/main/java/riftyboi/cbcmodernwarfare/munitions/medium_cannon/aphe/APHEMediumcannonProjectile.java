package riftyboi.cbcmodernwarfare.munitions.medium_cannon.aphe;

import java.util.function.Predicate;

import javax.annotation.Nonnull;
import net.minecraft.core.Position;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.config.CBCConfigs;
import rbasamoyai.createbigcannons.munitions.ProjectileContext;
import rbasamoyai.createbigcannons.munitions.ShellExplosion;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakExplosion;
import rbasamoyai.createbigcannons.munitions.config.components.BallisticPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.EntityDamagePropertiesComponent;
import rbasamoyai.createbigcannons.munitions.fuzes.FuzeItem;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareItem;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareMunitionPropertiesHandlers;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonFuzedProjectile;

public class APHEMediumcannonProjectile extends MediumcannonFuzedProjectile {

	public APHEMediumcannonProjectile(EntityType<? extends APHEMediumcannonProjectile> type, Level level) {
        super(type, level);
    }

	protected void detonate(Position position) {
		ShellExplosion explosion = new ShellExplosion(this.level(), this, this.indirectArtilleryFire(false), position.x(),
			position.y(), position.z(), this.getAllProperties().explosion().blockDamagePower(),
			this.getAllProperties().explosion().entityDamagePower(), false,
			CBCConfigs.server().munitions.damageRestriction.get().explosiveInteraction());
		CreateBigCannons.handleCustomExplosion(this.level(), explosion);
	}

	@Override
	protected boolean onClip(ProjectileContext ctx, Vec3 start, Vec3 end) {
		if (super.onClip(ctx, start, end)) return true;
		if (this.canDetonate(fz -> fz.onProjectileClip(this.fuze, this, start, end, ctx, true))) {
			this.detonate(start);
			return true;
		}
		return false;
	}

	public ItemStack getItem() {
		return CBCModernWarfareItem.APHE_MEDIUMCANNON_ROUND.asStack();
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

	protected APHEMediumcannonProjectileProperties getAllProperties() {
		return CBCModernWarfareMunitionPropertiesHandlers.APHE_MEDIUMCANNON.getPropertiesOf(this);
	}

}
