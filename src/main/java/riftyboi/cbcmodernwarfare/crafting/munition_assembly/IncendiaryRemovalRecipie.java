package riftyboi.cbcmodernwarfare.crafting.munition_assembly;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonAmmoItem;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonCartridgeItem;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonRoundItem;

import riftyboi.cbcmodernwarfare.index.CBCModernWarfareItem;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareRecipeTypes;
import riftyboi.cbcmodernwarfare.munitions.ItemStackDataUtil;
import riftyboi.cbcmodernwarfare.munitions.autocannon.AmmoItemMixinInerface;

/**
 * 拆燃烧剂：涂了燃烧剂的一发 → 燃烧头（输出槽）+ 拆掉燃烧剂的那一发（留在输入槽）。
 *
 * <p><b>这是 R6a 那个位运算缺陷的真正受害者</b>：本类的
 * {@link #getRemainingItems} 会调用 {@code mixinItem.setIncendiary(copy, false)} 来清除标记。
 * 1.20.1 的写法判读用 {@code & 3}、清除用 {@code & 0b11111101}，两者不对称 ——
 * 清除从未生效，所以「拆燃烧剂」在原项目里其实是坏的。
 * R6a 已把它改成对称的 {@code & ~3}（用户 2026-11-09 授权），本配方因此才真的能用。
 *
 * <p><b>1.20.1 → 1.21.1</b>：{@code CraftingContainer} → {@link CraftingInput}；
 * {@code RegistryAccess} → {@link Provider}；NBT 布尔换成 {@link ItemStackDataUtil}。
 */
public class IncendiaryRemovalRecipie extends CustomRecipe {

	public IncendiaryRemovalRecipie() {
		this(CraftingBookCategory.MISC);
	}

	public IncendiaryRemovalRecipie(CraftingBookCategory category) {
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
			// 弹药筒：看它里面装的那颗弹头
			if (stack.getItem() instanceof AutocannonCartridgeItem) {
				stack = AutocannonCartridgeItem.getProjectileStack(stack);
			}
			if (stack.getItem() instanceof AutocannonRoundItem) {
				if (!ItemStackDataUtil.getCustomBoolean(stack, "Incendiary")) {
					return false;
				}
				target = stack;
			} else if (stack.getItem() instanceof AutocannonAmmoItem item
					&& item instanceof AmmoItemMixinInerface mixinItem) {
				if (!mixinItem.isIncendiary(stack)) {
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
		return new ItemStack(CBCModernWarfareItem.INCENDIARY_TIP.get());
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
		NonNullList<ItemStack> result = super.getRemainingItems(input);
		int size = input.size();

		for (int i = 0; i < size; ++i) {
			ItemStack stack = input.getItem(i);
			ItemStack originalStack = stack.copy();

			boolean isCartridge = stack.getItem() instanceof AutocannonCartridgeItem;
			if (isCartridge) {
				stack = AutocannonCartridgeItem.getProjectileStack(stack);
			}

			if (stack.getItem() instanceof AutocannonRoundItem) {
				if (ItemStackDataUtil.getCustomBoolean(stack, "Incendiary")) {
					ItemStack copy = stack.copy();
					copy.setCount(1);
					ItemStackDataUtil.setCustomBoolean(copy, "Incendiary", false);
					result.set(i, isCartridge ? rebuildCartridge(originalStack, copy) : copy);
				}
				break;
			} else if (stack.getItem() instanceof AutocannonAmmoItem item
					&& item instanceof AmmoItemMixinInerface mixinItem) {
				if (mixinItem.isIncendiary(stack)) {
					ItemStack copy = stack.copy();
					copy.setCount(1);
					mixinItem.setIncendiary(copy, false);
					result.set(i, isCartridge ? rebuildCartridge(originalStack, copy) : copy);
				}
				break;
			}
		}
		return result;
	}

	/** 把「剥掉燃烧剂的弹头」塞回一个同种类的空弹药筒里。 */
	private static ItemStack rebuildCartridge(ItemStack originalCartridge, ItemStack strippedRound) {
		ItemStack cartridge = new ItemStack(originalCartridge.getItem());
		AutocannonCartridgeItem.writeProjectile(strippedRound, cartridge);
		return cartridge;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 1;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return CBCModernWarfareRecipeTypes.INCENDIARY_REMOVAL.getSerializer();
	}
}
