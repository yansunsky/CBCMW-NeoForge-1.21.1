package riftyboi.cbcmodernwarfare.munitions.medium_cannon;

import javax.annotation.Nullable;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import riftyboi.cbcmodernwarfare.munitions.medium_cannon.config.MediumcannonProjectilePropertiesComponent;

/**
 * 能装进中型炮的弹药。
 *
 * <p><b>1.20.1 → 1.21.1</b>：接口本身零变化。实现类（`MediumcannonRoundItem` /
 * `MediumcannonCartridgeItem` 及九种变体）归 R5，因为它们压在
 * DataComponents 迁移（R-01，75 处 `getOrCreateTag`）这条最大的单点风险上。
 *
 * <p>本轮（R4b）只需要这个接口存在 —— 装配层的 `fireShot` 与炮闩的装弹交互都按它取弹药，
 * 不关心具体实现。
 */
public interface MediumcannonAmmoItem {

    @Nullable
    AbstractMediumcannonProjectile getMediumcannonProjectile(ItemStack stack, Level level);

    @Nullable
    EntityType<?> getEntityType(ItemStack stack);

    MediumcannonProjectilePropertiesComponent getMediumcannonProperties(ItemStack itemStack);

    boolean isTracer(ItemStack stack);

    void setTracer(ItemStack stack, boolean value);

    /** 击发后留在膛内的空壳（退壳时弹出）。 */
    ItemStack getSpentItem(ItemStack stack);

    MediumcannonAmmoType getType();
}
