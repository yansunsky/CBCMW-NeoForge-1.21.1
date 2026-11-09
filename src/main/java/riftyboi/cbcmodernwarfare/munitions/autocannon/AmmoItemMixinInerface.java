package riftyboi.cbcmodernwarfare.munitions.autocannon;

import net.minecraft.world.item.ItemStack;

/**
 * 物品侧的燃烧剂（incendiary）状态读写。
 *
 * <p><b>注意类名拼写</b>：{@code Inerface} 少一个 f，这是 1.20.1 原项目的原始拼写。
 * 照搬不改 —— 改名会让所有引用点对不上（见 R6 计划书 §9.5）。
 *
 * <p>由 3 个 mixin 实现（注入到 CBC 自己的弹药类里，因为 CBC 的类不可改）：
 * {@code AutocannonRoundItemMixin} / {@code AutocannonCartridgeItemMixin} /
 * {@code AutocannonMGRoundItemMixin}。消费点在 R6c-1 的燃烧剂配方里。
 */
public interface AmmoItemMixinInerface {

	boolean isIncendiary(ItemStack stack);

	void setIncendiary(ItemStack stack, boolean value);
}
