package riftyboi.cbcmodernwarfare.munitions;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * 物品栈上的自定义标量数据读写。
 *
 * <p><b>1.20.1 → 1.21.1</b>：1.20.1 直接往 {@code ItemStack#getOrCreateTag()} 上挂键
 * （例如 {@code stack.getOrCreateTag().getBoolean("Incendiary")}）。
 * 1.21.1 里 {@code ItemStack} 不再暴露可变 NBT，自定义数据统一走原版的
 * {@link DataComponents#CUSTOM_DATA}（值类型 {@link CustomData}，内部仍是一个
 * {@code CompoundTag}）：
 *
 * <pre>
 * 1.20.1: stack.getOrCreateTag().getBoolean("Incendiary")
 * 1.21.1: stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("Incendiary")
 * </pre>
 *
 * <p><b>为什么单独抽一个类</b>：燃烧剂（incendiary）标记要在 3 个 mixin
 * （{@code AutocannonRoundItemMixin} / {@code AutocannonMGRoundItemMixin} /
 * {@code AutocannonCartridgeItemMixin}）里读写，写法必须完全一致 ——
 * 内联的话任何一处写歪了都会造成「读得到、写不回」这类静默不一致。
 *
 * <p>当前只覆盖燃烧剂用的布尔标记。R6b（弹鼓）/ R6c（燃烧剂配方）如果需要
 * 组件级的引信 / 弹头读写，优先复用既有的
 * {@link riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonFuzes}
 * 与 CBC 自己的静态方法，不要在这里重复造一份。
 */
public final class ItemStackDataUtil {

    private ItemStackDataUtil() {
    }

    /** 读取自定义布尔键；没有该键时返回 {@code false}。 */
    public static boolean getCustomBoolean(ItemStack stack, String key) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean(key);
    }

    /**
     * 写入自定义布尔键。
     *
     * <p>与 1.20.1 的语义保持一致（值为 {@code false} 时键被移除而不是存 {@code false}），
     * 这样「无标记」的栈不会因为多了一个恒 {@code false} 的键而影响
     * {@code ItemStack#isSameItemSameComponents} 之类的组件比较。
     * 整个 tag 因此变空时，把 CUSTOM_DATA 组件一并移除。
     */
    public static void setCustomBoolean(ItemStack stack, String key, boolean value) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (value) {
            tag.putBoolean(key, true);
        } else {
            tag.remove(key);
        }
        if (tag.isEmpty()) {
            stack.remove(DataComponents.CUSTOM_DATA);
        } else {
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
    }
}
