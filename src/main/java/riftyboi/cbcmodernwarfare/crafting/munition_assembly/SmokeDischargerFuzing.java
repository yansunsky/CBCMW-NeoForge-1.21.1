package riftyboi.cbcmodernwarfare.crafting.munition_assembly;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import rbasamoyai.createbigcannons.munitions.fuzes.FuzeItem;

import riftyboi.cbcmodernwarfare.index.CBCModernWarfareRecipeTypes;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonFuzes;
import riftyboi.cbcmodernwarfare.munitions.smoke_discharger.SmokeDischargerRoundItem;

/**
 * 给烟雾排放器弹药装引信：弹药 + 引信 → 装好引信的弹药。
 *
 * <p><b>1.20.1 → 1.21.1</b>：{@code CraftingContainer} → {@link CraftingInput}；
 * {@code RegistryAccess} → {@link Provider}；引信读写换成
 * {@link MediumcannonFuzes}（CBC 的 FUZE 组件）。
 */
public class SmokeDischargerFuzing extends CustomRecipe {

	public SmokeDischargerFuzing() {
		this(CraftingBookCategory.MISC);
	}

	public SmokeDischargerFuzing(CraftingBookCategory category) {
		super(category);
	}

	@Override
	public boolean matches(CraftingInput input, Level level) {
		ItemStack round = ItemStack.EMPTY;
		ItemStack fuze = ItemStack.EMPTY;

		for (int i = 0; i < input.size(); ++i) {
			ItemStack stack = input.getItem(i);
			if (stack.isEmpty()) {
				continue;
			}
			if (stack.getItem() instanceof SmokeDischargerRoundItem) {
				// 已经装了引信的不能再装
				if (!round.isEmpty() || MediumcannonFuzes.hasFuze(stack)) {
					return false;
				}
				round = stack;
			} else if (stack.getItem() instanceof FuzeItem) {
				if (!fuze.isEmpty()) {
					return false;
				}
				fuze = stack;
			} else {
				return false;
			}
		}

		return !round.isEmpty() && !fuze.isEmpty();
	}

	@Override
	public ItemStack assemble(CraftingInput input, Provider registries) {
		ItemStack round = ItemStack.EMPTY;
		ItemStack fuze = ItemStack.EMPTY;

		for (int i = 0; i < input.size(); ++i) {
			ItemStack stack = input.getItem(i);
			if (stack.isEmpty()) {
				continue;
			}
			if (stack.getItem() instanceof SmokeDischargerRoundItem) {
				if (!round.isEmpty()) {
					return ItemStack.EMPTY;
				}
				round = stack;
			} else if (stack.getItem() instanceof FuzeItem) {
				if (!fuze.isEmpty()) {
					return ItemStack.EMPTY;
				}
				fuze = stack;
			} else {
				return ItemStack.EMPTY;
			}
		}

		if (round.isEmpty() || fuze.isEmpty()) {
			return ItemStack.EMPTY;
		}
		ItemStack result = round.copy();
		result.setCount(1);
		MediumcannonFuzes.setFuze(result, fuze.copyWithCount(1));
		return result;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return CBCModernWarfareRecipeTypes.SMOKE_MUNITION_FUZING.getSerializer();
	}
}
