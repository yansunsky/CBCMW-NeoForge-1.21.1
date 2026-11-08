package riftyboi.cbcmodernwarfare.munitions.medium_cannon;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.createmod.catnip.lang.Lang;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.index.CBCDataComponents;

import riftyboi.cbcmodernwarfare.munitions.medium_cannon.config.MediumcannonProjectilePropertiesComponent;

/**
 * 弹头（未装进弹药筒的那一发）的基类。
 *
 * <p><b>1.20.1 → 1.21.1 的 DataComponents 迁移</b>：
 * <pre>
 * 1.20.1: stack.getOrCreateTag().getBoolean("Tracer")
 * 1.21.1: stack.getOrDefault(CBCDataComponents.AUTOCANNON_TRACER, false)
 * </pre>
 * 复用 CBC 的组件而不是自己注册 —— 依据见
 * {@code docs/analysis/05-DataComponents迁移规范.md} §1。
 */
public abstract class MediumcannonRoundItem extends Item {

    protected MediumcannonRoundItem(Properties properties) {
        super(properties);
    }

    public abstract AbstractMediumcannonProjectile getMediumcannonProjectile(ItemStack stack,
            net.minecraft.world.level.Level level);

    @Nonnull
    public abstract MediumcannonProjectilePropertiesComponent getMediumcannonProperties(ItemStack itemStack);

    public abstract EntityType<?> getEntityType(ItemStack stack);

    public abstract ItemStack getCartridgeType();

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents,
            TooltipFlag isAdvanced) {
        super.appendHoverText(stack, context, tooltipComponents, isAdvanced);
        if (stack.getOrDefault(CBCDataComponents.AUTOCANNON_TRACER, false)) {
            Lang.builder("tooltip").translate(CreateBigCannons.MOD_ID + ".tracer").addTo(tooltipComponents);
        }
    }
}
