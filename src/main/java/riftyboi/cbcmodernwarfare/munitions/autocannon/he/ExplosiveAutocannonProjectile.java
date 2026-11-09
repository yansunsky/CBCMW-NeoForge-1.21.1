package riftyboi.cbcmodernwarfare.munitions.autocannon.he;

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
import rbasamoyai.createbigcannons.munitions.ShellExplosion;
import rbasamoyai.createbigcannons.munitions.autocannon.AbstractAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.config.components.BallisticPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.EntityDamagePropertiesComponent;
import rbasamoyai.createbigcannons.munitions.fuzes.FuzeItem;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareMunitionPropertiesHandlers;

/**
 * 高爆（HE）机炮炮弹实体。
 *
 * <p><b>1.20.1 → 1.21.1 的改动，逐条依据</b>：
 * <ul>
 *   <li>{@code ImpactResult} 现在是 {@code AbstractCannonProjectile} 的嵌套 record
 *       （javap 确认），显式 import。</li>
 *   <li>实体 NBT 的 {@code "Fuze"} 仍是<b>实体存档</b>（不是物品组件），
 *       但 {@code ItemStack.save/of} 在 1.21.1 需要注册表访问器：
 *       {@code fuze.save(new CompoundTag())} → {@code fuze.save(this.registryAccess())}；
 *       {@code ItemStack.of(tag.getCompound("Fuze"))} →
 *       {@code ItemStack.parseOptional(this.registryAccess(), tag.getCompound("Fuze"))}。</li>
 *   <li>{@code ShellExplosion} 在 CBC 5.11.7 把原来的单个 {@code explosivePower}
 *       拆成 {@code blockDamagePower} + {@code entityDamagePower}（javap 确认），
 *       故 1 个 float 实参变成 2 个（其余 {@code isPlume=false} 与
 *       {@code Explosion.BlockInteraction} 位置不变）。</li>
 * </ul>
 */
public class ExplosiveAutocannonProjectile extends AbstractAutocannonProjectile {
	private ItemStack fuze = ItemStack.EMPTY;


	@Override
	public void tick() {
		super.tick();
		if (this.canDetonate(fz -> fz.onProjectileTick(this.fuze, this))) {
			this.detonate(this.position());
			this.removeNextTick = true;
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


	public ExplosiveAutocannonProjectile(EntityType<? extends ExplosiveAutocannonProjectile> type, Level level) {
		super(type, level);
	}

	protected void detonate(Position position) {
		ShellExplosion explosion = new ShellExplosion(this.level(), this, this.indirectArtilleryFire(false), position.x(),
			position.y(), position.z(), this.getAllProperties().explosion().blockDamagePower(),
			this.getAllProperties().explosion().entityDamagePower(), false,
			CBCConfigs.server().munitions.damageRestriction.get().explosiveInteraction());
		CreateBigCannons.handleCustomExplosion(this.level(), explosion);
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

	protected ExplosiveAutocannonProjectileProperties getAllProperties() {
		return CBCModernWarfareMunitionPropertiesHandlers.HE_AUTOCANNON.getPropertiesOf(this);
	}
}
