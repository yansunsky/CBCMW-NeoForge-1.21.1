package riftyboi.cbcmodernwarfare.munitions.medium_cannon;

import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import rbasamoyai.createbigcannons.munitions.ProjectileContext;
import rbasamoyai.createbigcannons.munitions.fuzes.FuzeItem;

/**
 * 带引信的中型炮炮弹基类。
 *
 * <p><b>1.20.1 → 1.21.1</b>：实体存的是 {@link ItemStack} 字段（不是物品组件的 NBT），
 * 所以 {@code addAdditionalSaveData}/{@code readAdditionalSaveData} 的
 * {@code ItemStack.save}/{@code ItemStack.of} 需要 {@link HolderLookup.Provider}：
 * <pre>
 * 1.20.1: fuze.save(new CompoundTag()) / ItemStack.of(tag.getCompound("Fuze"))
 * 1.21.1: fuze.save(provider)          / ItemStack.parseOptional(provider, tag.getCompound("Fuze"))
 * </pre>
 * provider 从 {@code this.registryAccess()} 取（Entity 上就有）。
 *
 * <p>注意这里的 {@code "Fuze"} 是**实体 NBT**，与物品上的 {@code CBCDataComponents.FUZE}
 * 是两回事：物品进实体时已经把引信栈取出来存进字段了。
 */
public abstract class MediumcannonFuzedProjectile extends AbstractMediumcannonProjectile {

    protected ItemStack fuze = ItemStack.EMPTY;

    protected MediumcannonFuzedProjectile(EntityType<? extends AbstractMediumcannonProjectile> type, Level level) {
        super(type, level);
    }

    public void setFuze(ItemStack fuze) {
        this.fuze = fuze;
    }

    @Nullable
    public ItemStack getFuze() {
        return this.fuze;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.canDetonate(fz -> fz.onProjectileTick(this.fuze, this))) {
            this.detonate(this.position());
            this.removeNextTick = true;
        }
    }

    protected abstract void detonate(Position position);

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.fuze != null && !this.fuze.isEmpty()) {
            tag.put("Fuze", this.fuze.save(this.registryAccess()));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.fuze = tag.contains("Fuze", CompoundTag.TAG_COMPOUND)
                ? ItemStack.parseOptional(this.registryAccess(), tag.getCompound("Fuze"))
                : ItemStack.EMPTY;
    }

    protected final boolean canDetonate(Predicate<FuzeItem> cons) {
        return !this.level().isClientSide
                && this.level().hasChunkAt(this.blockPosition())
                && this.fuze.getItem() instanceof FuzeItem fuzeItem
                && cons.test(fuzeItem);
    }

    @Override
    protected boolean onImpact(HitResult hitResult, ImpactResult impactResult, ProjectileContext projectileContext) {
        super.onImpact(hitResult, impactResult, projectileContext);
        if (this.canDetonate(fz -> fz.onProjectileImpact(this.fuze, this, hitResult, impactResult, false))) {
            this.detonate(hitResult.getLocation());
            return true;
        }
        return false;
    }

    @Override
    protected boolean onClip(ProjectileContext ctx, Vec3 start, Vec3 end) {
        if (super.onClip(ctx, start, end)) {
            return true;
        }
        if (this.canDetonate(fz -> fz.onProjectileClip(this.fuze, this, start, end, ctx, false))) {
            this.detonate(start);
            return true;
        }
        return false;
    }
}
