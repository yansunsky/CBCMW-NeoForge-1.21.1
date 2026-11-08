package riftyboi.cbcmodernwarfare.munitions.medium_cannon;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;

import rbasamoyai.createbigcannons.index.CBCDataComponents;

import riftyboi.cbcmodernwarfare.index.CBCModernWarfareItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.config.MediumcannonProjectilePropertiesComponent;

/**
 * 弹药筒（装弹头的那个外壳）。
 *
 * <p><b>1.20.1 → 1.21.1 的 DataComponents 迁移（本类是全 R5 的核心）</b>：
 *
 * <table border="1">
 *   <tr><th>1.20.1</th><th>1.21.1</th></tr>
 *   <tr><td>{@code tag.contains("Projectile", TAG_COMPOUND)}</td>
 *       <td>{@code stack.has(CBCDataComponents.PROJECTILE)}</td></tr>
 *   <tr><td>{@code ItemStack.of(tag.getCompound("Projectile"))}</td>
 *       <td>{@code stack.getOrDefault(PROJECTILE, ItemContainerContents.EMPTY).copyOne()}</td></tr>
 *   <tr><td>{@code tag.put("Projectile", round.save(new CompoundTag()))}</td>
 *       <td>{@code stack.set(PROJECTILE, ItemContainerContents.fromItems(Lists.newArrayList(round)))}</td></tr>
 *   <tr><td>弹头栈 {@code .getBoolean("Tracer")}</td>
 *       <td>弹头栈 {@code .getOrDefault(AUTOCANNON_TRACER, false)}</td></tr>
 * </table>
 *
 * <p>映射依据是 CBC 自己的 {@code AutocannonCartridgeItem} 的字节码
 * （javap -c 逐条核对），详见 {@code docs/analysis/05-DataComponents迁移规范.md} §2。
 *
 * <p><b>两处对 1.20.1 的修正</b>（都登记在 R5 日志里）：
 * <ol>
 *   <li>{@link #setTracer} —— 1.20.1 里它无视传入的 {@code value}（写死 {@code true}），
 *       而且改的是 {@code getCompound} 返回的副本、从不写回，所以原版里这个方法
 *       实际上完全无效。组件 API 下必须写回才生效，这里按正确语义实现。</li>
 * </ol>
 */
public class MediumcannonCartridgeItem extends Item implements MediumcannonAmmoItem {

    public MediumcannonCartridgeItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip,
            TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        ItemStack round = getProjectileStack(stack);
        if (!round.isEmpty()) {
            tooltip.add(Component.translatable("item.minecraft.crossbow.projectile")
                    .append(" ")
                    .append(round.getDisplayName()));
            if (round.getItem() instanceof MediumcannonRoundItem) {
                List<Component> subTooltip = new ArrayList<>();
                round.getItem().appendHoverText(round, context, subTooltip, flag);
                for (int i = 0; i < subTooltip.size(); ++i) {
                    subTooltip.set(i, Component.literal("  ").append(subTooltip.get(i)).withStyle(ChatFormatting.GRAY));
                }
                tooltip.addAll(subTooltip);
            }
        }
    }

    @Override
    public MediumcannonProjectilePropertiesComponent getMediumcannonProperties(ItemStack itemStack) {
        ItemStack projectileStack = getProjectileStack(itemStack);
        return projectileStack.getItem() instanceof MediumcannonRoundItem roundItem
                ? roundItem.getMediumcannonProperties(itemStack)
                : MediumcannonProjectilePropertiesComponent.DEFAULT;
    }

    @Override
    public ItemStack getSpentItem(ItemStack stack) {
        return CBCModernWarfareItem.EMPTY_MEDIUMCANNON_CARTRIDGE.asStack();
    }

    @Override
    public MediumcannonAmmoType getType() {
        return MediumcannonAmmoType.MEDIUMCANNON;
    }

    @Override
    @Nullable
    public AbstractMediumcannonProjectile getMediumcannonProjectile(ItemStack stack, Level level) {
        ItemStack projectileStack = getProjectileStack(stack);
        return projectileStack.getItem() instanceof MediumcannonRoundItem projectileItem
                ? projectileItem.getMediumcannonProjectile(projectileStack, level)
                : null;
    }

    @Nullable
    @Override
    public EntityType<?> getEntityType(ItemStack stack) {
        ItemStack projectileStack = getProjectileStack(stack);
        return projectileStack.getItem() instanceof MediumcannonRoundItem projectileItem
                ? projectileItem.getEntityType(projectileStack)
                : null;
    }

    // ------------------------------------------------------------------------------------
    // 组件读写 —— 全 R5 的统一入口，其他类一律调这三个静态方法，不要自己碰组件
    // ------------------------------------------------------------------------------------

    /** 取出弹头栈（副本；未装弹头返回 {@link ItemStack#EMPTY}）。 */
    public static ItemStack getProjectileStack(ItemStack stack) {
        return stack.getOrDefault(CBCDataComponents.PROJECTILE, ItemContainerContents.EMPTY).copyOne();
    }

    /** 是否已装弹头。 */
    public static boolean hasProjectile(ItemStack stack) {
        return stack.has(CBCDataComponents.PROJECTILE);
    }

    /** 把弹头装进弹药筒。 */
    public static void writeProjectile(ItemStack round, ItemStack cartridge) {
        if (round.getItem() instanceof MediumcannonRoundItem) {
            cartridge.set(CBCDataComponents.PROJECTILE,
                    ItemContainerContents.fromItems(Lists.newArrayList(round)));
        }
    }

    @Override
    public boolean isTracer(ItemStack stack) {
        return hasProjectile(stack)
                && getProjectileStack(stack).getOrDefault(CBCDataComponents.AUTOCANNON_TRACER, false);
    }

    /**
     * 设置曳光标记。
     *
     * <p>{@code getProjectileStack} 返回副本，所以改完必须 {@link #writeProjectile} 写回 ——
     * 这也是 1.20.1 原版方法失效的原因（原版没写回）。
     */
    @Override
    public void setTracer(ItemStack stack, boolean value) {
        if (!hasProjectile(stack)) {
            return;
        }
        ItemStack projectile = getProjectileStack(stack);
        projectile.set(CBCDataComponents.AUTOCANNON_TRACER, value);
        writeProjectile(projectile, stack);
    }
}
