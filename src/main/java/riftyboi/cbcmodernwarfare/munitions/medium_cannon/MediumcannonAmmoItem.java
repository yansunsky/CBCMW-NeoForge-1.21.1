package riftyboi.cbcmodernwarfare.munitions.medium_cannon;

import javax.annotation.Nullable;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import riftyboi.cbcmodernwarfare.munitions.medium_cannon.config.MediumcannonProjectilePropertiesComponent;

/**
 * 中型炮弹药物品的公共接口。
 *
 * <p><b>1.20.1 → 1.21.1</b>：接口与 record 均无变化（这个 interface 本身不带数据）。
 * 数据存储换成 DataComponents，见 {@code MediumcannonCartridgeItem}。
 */
public interface MediumcannonAmmoItem {

    @Nullable
    AbstractMediumcannonProjectile getMediumcannonProjectile(ItemStack stack, Level level);

    @Nullable
    EntityType<?> getEntityType(ItemStack stack);

    MediumcannonProjectilePropertiesComponent getMediumcannonProperties(ItemStack itemStack);

    boolean isTracer(ItemStack stack);

    void setTracer(ItemStack stack, boolean value);

    ItemStack getSpentItem(ItemStack stack);

    MediumcannonAmmoType getType();
}
