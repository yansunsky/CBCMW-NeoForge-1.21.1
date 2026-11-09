package riftyboi.cbcmodernwarfare.crafting.munition_assembly;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonAmmoItem;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonRoundItem;

import riftyboi.cbcmodernwarfare.index.CBCModernWarfareItem;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareRecipeTypes;
import riftyboi.cbcmodernwarfare.munitions.ItemStackDataUtil;
import riftyboi.cbcmodernwarfare.munitions.autocannon.AmmoItemMixinInerface;

/**
 * 涂燃烧剂：燃烧头 + 一发机炮弹药 → 涂好燃烧剂的那一发。
 *
 * <p>「一发」可以是整装弹（{@link AutocannonRoundItem}）或弹药筒
 * （{@link AutocannonAmmoItem}）。已经涂过的不能再涂（两个分支各自判一次）。
 *
 * <p><b>1.20.1 → 1.21.1</b>：
 * <ul>
 *   <li>{@code CraftingContainer} → {@link CraftingInput}，
 *       {@code getContainerSize()} → {@link CraftingInput#size()}。</li>
 *   <li>{@code assemble(..., RegistryAccess)} → {@code assemble(..., HolderLookup.Provider)}。</li>
 *   <li>{@code getOrCreateTag().getBoolean("Incendiary")} →
 *       {@link ItemStackDataUtil#getCustomBoolean}（CUSTOM_DATA 组件）。</li>
 * </ul>
 */
public class IncendiaryApplicationRecipie extends CustomRecipe {

	public IncendiaryApplicationRecipie() {
		this(CraftingBookCategory.MISC);
	}

	public IncendiaryApplicationRecipie(CraftingBookCategory category) {
		super(category);
	}

	@Override
	public boolean matches(CraftingInput input, Level level) {
		ItemStack round = ItemStack.EMPTY;
		ItemStack tip = ItemStack.EMPTY;

		for (int i = 0; i < input.size(); ++i) {
			ItemStack stack = input.getItem(i);
			if (stack.isEmpty()) {
				continue;
			}
			if (stack.getItem() instanceof AutocannonRoundItem) {
				// 整装弹：标记在物品自身上（由 AutocannonRoundItemMixin 读写）
				if (!round.isEmpty() || ItemStackDataUtil.getCustomBoolean(stack, "Incendiary")) {
					return false;
				}
				round = stack;
			} else if (stack.getItem() instanceof AutocannonAmmoItem item
					&& item instanceof AmmoItemMixinInerface mixinItem) {
				// 弹药筒：标记在弹头上（mixin 内部处理写回）
				if (!round.isEmpty() || mixinItem.isIncendiary(stack)) {
					return false;
				}
				round = stack;
			} else if (CBCModernWarfareItem.INCENDIARY_TIP.isIn(stack)) {
				if (!tip.isEmpty()) {
					return false;
				}
				tip = stack;
			} else {
				return false;
			}
		}

		return !round.isEmpty() && !tip.isEmpty();
	}

	@Override
	public ItemStack assemble(CraftingInput input, Provider registries) {
		ItemStack round = ItemStack.EMPTY;
		ItemStack tip = ItemStack.EMPTY;

		for (int i = 0; i < input.size(); ++i) {
			ItemStack stack = input.getItem(i);
			if (stack.isEmpty()) {
				continue;
			}
			if (stack.getItem() instanceof AutocannonRoundItem
					|| stack.getItem() instanceof AutocannonAmmoItem) {
				if (!round.isEmpty()) {
					return ItemStack.EMPTY;
				}
				round = stack;
			} else if (CBCModernWarfareItem.INCENDIARY_TIP.isIn(stack)) {
				if (!tip.isEmpty()) {
					return ItemStack.EMPTY;
				}
				tip = stack;
			} else {
				return ItemStack.EMPTY;
			}
		}

		if (round.isEmpty() || tip.isEmpty()) {
			return ItemStack.EMPTY;
		}
		ItemStack result = round.copy();
		result.setCount(1);
		if (result.getItem() instanceof AutocannonRoundItem) {
			ItemStackDataUtil.setCustomBoolean(result, "Incendiary", true);
		} else if (result.getItem() instanceof AutocannonAmmoItem item
				&& item instanceof AmmoItemMixinInerface mixinItem) {
			mixinItem.setIncendiary(result, true);
		}
		return result;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return CBCModernWarfareRecipeTypes.INCENDIARY_APPLICATION.getSerializer();
	}
}
