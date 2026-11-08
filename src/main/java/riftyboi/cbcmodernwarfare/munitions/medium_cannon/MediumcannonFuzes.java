package riftyboi.cbcmodernwarfare.munitions.medium_cannon;

import com.google.common.collect.Lists;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

import rbasamoyai.createbigcannons.index.CBCDataComponents;

/**
 * 引信（Fuze）的组件读写。
 *
 * <p><b>1.20.1 → 1.21.1</b>：
 * <pre>
 * 1.20.1: tag.contains("Fuze", TAG_COMPOUND) ? ItemStack.of(tag.getCompound("Fuze")) : EMPTY
 * 1.21.1: stack.getOrDefault(CBCDataComponents.FUZE, ItemContainerContents.EMPTY).copyOne()
 * </pre>
 *
 * <p>单独抽成一个工具类，是因为 1.20.1 里这段代码在 5 个弹头类里各写了一遍
 * （13 处 {@code getCompound("Fuze")}），而且写法略有出入。
 * 统一入口后，R6/R7 的机炮与发射器弹药也能直接用。
 */
public final class MediumcannonFuzes {

    private MediumcannonFuzes() {
    }

    /** 取出引信栈（副本；未装引信返回 {@link ItemStack#EMPTY}）。 */
    public static ItemStack getFuze(ItemStack stack) {
        return stack.getOrDefault(CBCDataComponents.FUZE, ItemContainerContents.EMPTY).copyOne();
    }

    /** 是否已装引信。 */
    public static boolean hasFuze(ItemStack stack) {
        return stack.has(CBCDataComponents.FUZE);
    }

    /** 装上引信。传 {@link ItemStack#EMPTY} 等价于卸下。 */
    public static void setFuze(ItemStack stack, ItemStack fuze) {
        if (fuze.isEmpty()) {
            stack.remove(CBCDataComponents.FUZE);
        } else {
            stack.set(CBCDataComponents.FUZE, ItemContainerContents.fromItems(Lists.newArrayList(fuze)));
        }
    }
}
