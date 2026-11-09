package riftyboi.cbcmodernwarfare.mixin;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonCartridgeItem;

import riftyboi.cbcmodernwarfare.munitions.ItemStackDataUtil;
import riftyboi.cbcmodernwarfare.munitions.autocannon.AmmoItemMixinInerface;

/**
 * 给 CBC 的机炮弹药筒（{@code AutocannonCartridgeItem}）加燃烧剂状态读写。
 *
 * <p>弹药筒本身不存标记 —— 1.20.1 是把 {@code "Incendiary"} 写在**弹头**的 tag 里
 * （{@code getProjectileStack(...).getOrCreateTag().getBoolean("Incendiary")}），
 * 本类保持同一位置，只是换成组件读写。
 *
 * <p><b>1.20.1 → 1.21.1 的关键点</b>：
 * {@code AutocannonCartridgeItem.getProjectileStack} 与 {@code hasProjectile}
 * 在 5.11.7 里**签名未变**（已 javap 核实），仍是 {@code public static}，
 * 所以 {@code @Shadow} 可以照搬。
 */
@Mixin(AutocannonCartridgeItem.class)
public abstract class AutocannonCartridgeItemMixin extends Item implements AmmoItemMixinInerface {

	@Shadow
	public static ItemStack getProjectileStack(ItemStack stack) {
		return null;
	}

	@Shadow
	public static boolean hasProjectile(ItemStack stack) {
		return false;
	}

	public AutocannonCartridgeItemMixin(Properties properties) {
		super(properties);
	}

	@Override
	public boolean isIncendiary(ItemStack stack) {
		return hasProjectile(stack) && ItemStackDataUtil.getCustomBoolean(getProjectileStack(stack), "Incendiary");
	}

	@Override
	public void setIncendiary(ItemStack stack, boolean value) {
		if (!hasProjectile(stack)) {
			return;
		}
		// getProjectileStack 返回的是副本，改完必须写回弹药筒。
		ItemStack projectile = getProjectileStack(stack);
		ItemStackDataUtil.setCustomBoolean(projectile, "Incendiary", value);
		AutocannonCartridgeItem.writeProjectile(projectile, stack);
	}
}
