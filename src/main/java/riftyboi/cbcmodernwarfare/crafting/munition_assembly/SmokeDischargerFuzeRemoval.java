package riftyboi.cbcmodernwarfare.crafting.munition_assembly;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import riftyboi.cbcmodernwarfare.index.CBCModernWarfareRecipeTypes;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonFuzes;
import riftyboi.cbcmodernwarfare.munitions.smoke_discharger.SmokeDischargerRoundItem;

/**
 * 拆烟雾排放器弹药的引信：装好引信的弹药 → 引信（输出槽）+ 拆掉引信的那发（留在输入槽）。
 *
 * <p><b>1.20.1 → 1.21.1</b>：{@code CraftingContainer} → {@link CraftingInput}；
 * {@code RegistryAccess} → {@link Provider}；引信读写换成 {@link MediumcannonFuzes}
 * （组件 API 里「传空即卸下」是统一的 {@code remove} 写法）。
 */
public class SmokeDischargerFuzeRemoval extends CustomRecipe {

	public SmokeDischargerFuzeRemoval() {
		this(CraftingBookCategory.MISC);
	}

	public SmokeDischargerFuzeRemoval(CraftingBookCategory category) {
		super(category);
	}

	@Override
	public boolean matches(CraftingInput input, Level level) {
		ItemStack target = ItemStack.EMPTY;

		for (int i = 0; i < input.size(); ++i) {
			ItemStack stack = input.getItem(i);
			if (stack.isEmpty()) {
				continue;
			}
			if (!target.isEmpty()) {
				return false;
			}
			if (stack.getItem() instanceof SmokeDischargerRoundItem) {
				if (!MediumcannonFuzes.hasFuze(stack)) {
					return false;
				}
				target = stack;
			} else {
				return false;
			}
		}
		return !target.isEmpty();
	}

	@Override
	public ItemStack assemble(CraftingInput input, Provider registries) {
		ItemStack target = ItemStack.EMPTY;

		for (int i = 0; i < input.size(); ++i) {
			ItemStack stack = input.getItem(i);
			if (stack.isEmpty()) {
				continue;
			}
			if (!target.isEmpty()) {
				return ItemStack.EMPTY;
			}
			if (stack.getItem() instanceof SmokeDischargerRoundItem) {
				if (!MediumcannonFuzes.hasFuze(stack)) {
					return ItemStack.EMPTY;
				}
				target = stack;
			} else {
				return ItemStack.EMPTY;
			}
		}
		return MediumcannonFuzes.getFuze(target);
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
		NonNullList<ItemStack> result = super.getRemainingItems(input);
		for (int i = 0; i < input.size(); ++i) {
			ItemStack stack = input.getItem(i);
			if (stack.getItem() instanceof SmokeDischargerRoundItem) {
				if (MediumcannonFuzes.hasFuze(stack)) {
					ItemStack copy = stack.copy();
					copy.setCount(1);
					MediumcannonFuzes.setFuze(copy, ItemStack.EMPTY);
					result.set(i, copy);
				}
				break;
			}
		}
		return result;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 1;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return CBCModernWarfareRecipeTypes.SMOKE_FUZE_REMOVAL.getSerializer();
	}
}
