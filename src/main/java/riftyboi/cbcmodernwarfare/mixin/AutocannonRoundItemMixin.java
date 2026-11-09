package riftyboi.cbcmodernwarfare.mixin;

import java.util.List;

import net.createmod.catnip.lang.Lang;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonRoundItem;

import riftyboi.cbcmodernwarfare.CBCModernWarfare;
import riftyboi.cbcmodernwarfare.munitions.ItemStackDataUtil;
import riftyboi.cbcmodernwarfare.munitions.autocannon.AmmoItemMixinInerface;

/**
 * 给 CBC 的机炮整装弹（{@code AutocannonRoundItem}）加「+ 燃烧」tooltip 行与燃烧剂状态。
 *
 * <p>注入点到 CBC 的类里，是因为燃烧剂状态由本项目维护而 CBC 的类不可改
 * （见 R6 计划书 §1）。
 *
 * <p><b>1.20.1 → 1.21.1 的两处改动</b>：
 * <ol>
 *   <li>{@code appendHoverText} 第二参数由 {@code @Nullable Level} 变成
 *       {@code Item.TooltipContext}，所以 {@code @Inject} 必须写完整描述符，
 *       否则 mixin 找不到目标方法。</li>
 *   <li>读标记由 {@code stack.getOrCreateTag().getBoolean("Incendiary")} 换成
 *       {@link ItemStackDataUtil#getCustomBoolean}（CUSTOM_DATA 组件）。</li>
 * </ol>
 *
 * <p><b>⚠ 本类修掉了 1.20.1 的一个缺陷（R6a 发现，必须修）</b>：
 * 1.20.1 的 {@code AutocannonRoundItemMixin} 声明了
 * {@code implements AmmoItemMixinInerface}，却<b>只写了 tooltip 注入、没实现那两个接口方法</b>。
 * mixin 的接口是直接合成进目标类字节码的，Java 编译器看不到「目标类其实没实现」，
 * 所以能编译通过；但一旦有代码真的调用 {@code isIncendiary(stack)}，
 * 运行期就会抛 {@code AbstractMethodError}。
 *
 * <p>R6a 的服务端自检（⑪）实测抓到了这个错误 —— 也就是说，
 * <b>若照 1.20.1 原样迁移，R6c-1 的燃烧剂配方一调用就会崩服</b>。
 * 这里补上真实实现，语义与 {@code AutocannonMGRoundItemMixin} 一致（标记存在物品自身上）。
 *
 * <p>注：这属于「不修就无法工作」的必需修正（与 R5 修 {@code setTracer} 同类），
 * 不是迁移期重构。
 */
@Mixin(AutocannonRoundItem.class)
public abstract class AutocannonRoundItemMixin extends Item implements AmmoItemMixinInerface {

	public AutocannonRoundItemMixin(Properties properties) {
		super(properties);
	}

	@Inject(
		method = "appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/Item$TooltipContext;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V",
		at = @At("HEAD")
	)
	private void cbcAppendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents,
			TooltipFlag isAdvanced, CallbackInfo ci) {
		if (ItemStackDataUtil.getCustomBoolean(stack, "Incendiary")) {
			Lang.builder("tooltip").translate(CBCModernWarfare.MOD_ID + ".incendiary").addTo(tooltipComponents);
		}
	}

	// ------------------------------------------------------------------------------------
	// 接口方法的真实实现（**修掉 1.20.1 的一个缺陷，见类注释**）
	// ------------------------------------------------------------------------------------

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
