package riftyboi.cbcmodernwarfare.mixin;

import java.util.List;

import com.simibubi.create.foundation.utility.CreateLang;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import org.spongepowered.asm.mixin.Mixin;

import rbasamoyai.createbigcannons.munitions.autocannon.bullet.MachineGunRoundItem;

import riftyboi.cbcmodernwarfare.CBCModernWarfare;
import riftyboi.cbcmodernwarfare.munitions.ItemStackDataUtil;
import riftyboi.cbcmodernwarfare.munitions.autocannon.AmmoItemMixinInerface;

/**
 * 给 CBC 的机枪弹（{@code MachineGunRoundItem}）加燃烧剂状态与 tooltip。
 *
 * <p>这是 4 个 mixin 里最轻的一个：只读一个布尔标记、加一行 tooltip。
 *
 * <p><b>1.20.1 → 1.21.1 的两处改动</b>：{@code appendHoverText} 第二参数
 * {@code @Nullable Level} → {@code Item.TooltipContext}；标记读写换成
 * {@link ItemStackDataUtil}（CUSTOM_DATA 组件）。
 *
 * <p><b>行为等价照搬</b>：1.20.1 原文的 tooltip 判的是 {@code "Tracer"} 键
 * 却打印 {@code .incendiary} 文案 —— 这是原版的笔误，本轮保持等价不修
 * （见 R6 计划书 §8.4「不在迁移期重构业务逻辑」）。
 */
@Mixin(MachineGunRoundItem.class)
public abstract class AutocannonMGRoundItemMixin extends Item implements AmmoItemMixinInerface {

	public AutocannonMGRoundItemMixin(Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents,
			TooltipFlag isAdvanced) {
		super.appendHoverText(stack, context, tooltipComponents, isAdvanced);
		if (ItemStackDataUtil.getCustomBoolean(stack, "Tracer")) {
			CreateLang.builder("tooltip").translate(CBCModernWarfare.MOD_ID + ".incendiary").addTo(tooltipComponents);
		}
	}

	@Override
	public boolean isIncendiary(ItemStack stack) {
		return ItemStackDataUtil.getCustomBoolean(stack, "Incendiary");
	}

	@Override
	public void setIncendiary(ItemStack stack, boolean value) {
		if (!stack.isEmpty()) {
			ItemStackDataUtil.setCustomBoolean(stack, "Incendiary", value);
		}
	}
}
