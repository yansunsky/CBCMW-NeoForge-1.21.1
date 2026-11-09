package riftyboi.cbcmodernwarfare.crafting.munition_assembly;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonAmmoItem;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonRoundItem;

import riftyboi.cbcmodernwarfare.index.CBCModernWarfareItem;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareRecipeTypes;
import riftyboi.cbcmodernwarfare.munitions.ItemStackDataUtil;
import riftyboi.cbcmodernwarfare.munitions.autocannon.AmmoItemMixinInerface;

/**
 * 机械手版涂燃烧剂：机械手手持燃烧头，作用于置物台上的机炮弹药。
 *
 * <p><b>这个配方不是数据驱动的</b>（与 R5b 的两个机械手配方同一套路）：
 * 它是个值对象，{@code (munition, tip)} 由机械手每次查询时从手上两个物品现场构造
 * （见 {@link MunitionAssemblyDeployerRecipes}），所以 {@link #matches} 不看入参
 * {@code input}，只看构造时的字段。
 *
 * <p><b>1.20.1 → 1.21.1</b>：{@code Recipe<Container>} → {@code Recipe<RecipeInput>}；
 * {@code RegistryAccess} → {@link Provider}；燃烧剂标记改组件。
 */
public class IncendiaryApplicationDeployerRecipe implements Recipe<RecipeInput> {

	private final ItemStack munition;
	private final ItemStack tip;

	/** 供 {@code SimpleRecipeSerializer} 的 {@code MapCodec.unit} 使用。 */
	public IncendiaryApplicationDeployerRecipe() {
		this(ItemStack.EMPTY, ItemStack.EMPTY);
	}

	public IncendiaryApplicationDeployerRecipe(ItemStack munition, ItemStack tip) {
		this.munition = munition.copy();
		this.tip = tip.copy();
	}

	@Override
	public boolean matches(RecipeInput input, Level level) {
		if (!CBCModernWarfareItem.INCENDIARY_TIP.isIn(this.tip)) {
			return false;
		}
		if (this.munition.getItem() instanceof AutocannonRoundItem) {
			return !ItemStackDataUtil.getCustomBoolean(this.munition, "Incendiary");
		}
		if (this.munition.getItem() instanceof AutocannonAmmoItem item
				&& item instanceof AmmoItemMixinInerface mixinItem) {
			return !mixinItem.isIncendiary(this.munition);
		}
		return false;
	}

	@Override
	public ItemStack assemble(RecipeInput input, Provider registries) {
		return this.getResultItem(registries);
	}

	@Override
	public ItemStack getResultItem(Provider registries) {
		ItemStack result = this.munition.copy();
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
		return true;
	}

	// 注意：1.20.1 的 Recipe 接口有 getId()，1.21.1 已把它从接口移除
	// （配方变成无状态、id 由 RecipeManager 的 key 决定）。所以这里不再覆写 getId()，
	// 与 R5b 的 MediumTracerDeployerRecipe / MediumFuzingDeployerRecipe 保持一致。

	@Override
	public RecipeSerializer<?> getSerializer() {
		return CBCModernWarfareRecipeTypes.INCENDIARY_DEPLOYER_APPLICATION.getSerializer();
	}

	@Override
	public RecipeType<?> getType() {
		return CBCModernWarfareRecipeTypes.INCENDIARY_DEPLOYER_APPLICATION.getType();
	}
}
