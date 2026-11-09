package riftyboi.cbcmodernwarfare.munitions.autocannon.canister;

import java.util.function.Predicate;

import javax.annotation.Nonnull;

import net.minecraft.core.Position;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.config.CBCConfigs;
import rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile.ImpactResult;
import rbasamoyai.createbigcannons.munitions.ProjectileContext;
import rbasamoyai.createbigcannons.munitions.autocannon.AbstractAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.big_cannon.shrapnel.ShrapnelExplosion;
import rbasamoyai.createbigcannons.munitions.config.components.BallisticPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.EntityDamagePropertiesComponent;
import rbasamoyai.createbigcannons.munitions.fragment_burst.CBCProjectileBurst;
import rbasamoyai.createbigcannons.munitions.fragment_burst.ProjectileBurstParentPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.fuzes.FuzeItem;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareEntityTypes;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareMunitionPropertiesHandlers;

/**
 * 筒弹（canister）机炮炮弹实体。
 *
 * <p><b>1.20.1 → 1.21.1 的改动，逐条依据</b>：
 * <ul>
 *   <li>{@code ImpactResult} 现在是 {@code AbstractCannonProjectile} 的嵌套 record
 *       （javap 确认），显式 import；1.20.1 靠继承作用域解析，故原文无 import。</li>
 *   <li>{@code expireProjectile()} / {@code onClip(...)} 仍是 CBC 的可覆写点，签名未变。</li>
 *   <li>实体 NBT 的 {@code "Fuze"} 仍是<b>实体存档</b>（不是物品组件），
 *       但 {@code ItemStack.save/of} 在 1.21.1 需要注册表访问器：
 *       {@code fuze.save(new CompoundTag())} → {@code fuze.save(this.registryAccess())}；
 *       {@code ItemStack.of(tag.getCompound("Fuze"))} →
 *       {@code ItemStack.parseOptional(this.registryAccess(), tag.getCompound("Fuze"))}。</li>
 *   <li>{@code ShrapnelExplosion} 在 CBC 5.11.7 把原来的单个 {@code explosivePower}
 *       拆成 {@code blockDamagePower} + {@code entityDamagePower}（javap 确认），
 *       故这里由 1 个 float 变成 2 个 float 实参。</li>
 * </ul>
 */
public class CanisterAutocannonProjectile extends AbstractAutocannonProjectile {
	private ItemStack fuze = ItemStack.EMPTY;

	@Override
	public void tick() {
		super.tick();
		if (this.canDetonate(fz -> fz.onProjectileTick(this.fuze, this))) {
			this.detonate(this.position());
			this.removeNextTick = true;
		} else if (!this.level().isClientSide && this.fuze.isEmpty()) {
			this.disintegrate();
			this.discard();
		}
	}
	@Override
	protected void expireProjectile() {
		if (this.fuze.getItem() instanceof FuzeItem fuzeItem && fuzeItem.onProjectileExpiry(this.fuze, this))
			this.detonate(this.position());
		super.expireProjectile();
	}

	@Override
	protected boolean onImpact(HitResult hitResult, ImpactResult impactResult, ProjectileContext projectileContext) {
		super.onImpact(hitResult, impactResult, projectileContext);
		if (this.canDetonate(fz -> fz.onProjectileImpact(this.fuze, this, hitResult, impactResult, false))) {
			this.detonate(hitResult.getLocation());
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean onClip(ProjectileContext ctx, Vec3 start, Vec3 end) {
		if (super.onClip(ctx, start, end)) return true;
		if (this.canDetonate(fz -> fz.onProjectileClip(this.fuze, this, start, end, ctx, false))) {
			this.detonate(start);
			return true;
		}
		return false;
	}

	public void setFuze(ItemStack fuze) { this.fuze = fuze; }

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		if (this.fuze != null && !this.fuze.isEmpty()) tag.put("Fuze", this.fuze.save(this.registryAccess()));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.fuze = tag.contains("Fuze", Tag.TAG_COMPOUND) ? ItemStack.parseOptional(this.registryAccess(), tag.getCompound("Fuze")) : ItemStack.EMPTY;
	}

	protected final boolean canDetonate(Predicate<FuzeItem> cons) {
		return !this.level().isClientSide && this.level().hasChunkAt(this.blockPosition()) && !this.isRemoved()
			&& this.fuze.getItem() instanceof FuzeItem fuzeItem && cons.test(fuzeItem);
	}


	public CanisterAutocannonProjectile(EntityType<? extends CanisterAutocannonProjectile> type, Level level) {
		super(type, level);
	}

	protected void disintegrate() {
		ProjectileBurstParentPropertiesComponent properties = this.getAllProperties().flakBurst();
		CBCProjectileBurst.spawnConeBurst(this.level(), CBCModernWarfareEntityTypes.CANISTER_BURST.get(), this.position(), this.getDeltaMovement(),
			properties.burstProjectileCount(), properties.burstSpread());
	}

	protected void detonate(Position position) {
		Vec3 oldDelta = this.getDeltaMovement();
		CanisterAutocannonProjectileProperties properties = this.getAllProperties();
		ShrapnelExplosion explosion = new ShrapnelExplosion(this.level(), null, this.indirectArtilleryFire(false), position.x(),
			position.y(), position.z(), properties.explosion().blockDamagePower(), properties.explosion().entityDamagePower(),
			CBCConfigs.server().munitions.damageRestriction.get().explosiveInteraction());
		CreateBigCannons.handleCustomExplosion(this.level(), explosion);
		CBCProjectileBurst.spawnConeBurst(this.level(), CBCModernWarfareEntityTypes.CANISTER_BURST.get(), new Vec3(position.x(), position.y(), position.z()),
			oldDelta, properties.flakBurst().burstProjectileCount(), properties.flakBurst().burstSpread());
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

	protected CanisterAutocannonProjectileProperties getAllProperties() {
		return CBCModernWarfareMunitionPropertiesHandlers.CANISTER_AUTOCANNON.getPropertiesOf(this);
	}
}
