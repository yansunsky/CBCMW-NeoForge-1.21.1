package riftyboi.cbcmodernwarfare.crafting.munition_assembly;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import rbasamoyai.createbigcannons.index.CBCDataComponents;
import rbasamoyai.createbigcannons.index.CBCItems;

import riftyboi.cbcmodernwarfare.index.CBCModernWarfareRecipeTypes;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonAmmoItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonRoundItem;

/**
 * 机械手版涂曳光：机械手手持曳光剂，作用于传送带/置物台上的弹头或弹药筒。
 *
 * <p><b>这个配方不是数据驱动的</b>。它是个「值对象」：{@code (munition, tracer)}
 * 由机械手每次查询时从手上的两个物品现场构造（见
 * {@link MunitionAssemblyDeployerRecipes}），所以 {@link #matches} 不看入参
 * {@code input}，只看构造时的字段。1.20.1 与 CBC 5.11.7 都是这个套路。
 *
 * <p><b>1.20.1 → 1.21.1</b>：{@code Recipe<Container>} → {@code Recipe<RecipeInput>}；
 * {@code assemble(Container, RegistryAccess)} → {@code assemble(RecipeInput, HolderLookup.Provider)}；
 * {@code getResultItem(RegistryAccess)} → {@code getResultItem(HolderLookup.Provider)}；
 * 曳光标记改组件。
 */
public class MediumTracerDeployerRecipe implements Recipe<RecipeInput> {

    private final ItemStack munition;
    private final ItemStack tracer;

    /** 供 {@code SimpleRecipeSerializer} 的 {@code MapCodec.unit} 使用。 */
    public MediumTracerDeployerRecipe() {
        this(ItemStack.EMPTY, ItemStack.EMPTY);
    }

    public MediumTracerDeployerRecipe(ItemStack munition, ItemStack tracer) {
        this.munition = munition.copy();
        this.tracer = tracer.copy();
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        if (!CBCItems.TRACER_TIP.isIn(this.tracer)) {
            return false;
        }
        if (this.munition.getItem() instanceof MediumcannonRoundItem) {
            return !this.munition.getOrDefault(CBCDataComponents.AUTOCANNON_TRACER, false);
        }
        if (this.munition.getItem() instanceof MediumcannonAmmoItem item) {
            return !item.isTracer(this.munition);
        }
        return false;
    }

    @Override
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) {
        return this.getResultItem(registries);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        ItemStack result = this.munition.copy();
        result.setCount(1);
        if (result.getItem() instanceof MediumcannonRoundItem) {
            result.set(CBCDataComponents.AUTOCANNON_TRACER, true);
        } else if (result.getItem() instanceof MediumcannonAmmoItem item) {
            item.setTracer(result, true);
        }
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CBCModernWarfareRecipeTypes.MEDIUM_TRACER_DEPLOYER_RECIPE.getSerializer();
    }

    @Override
    public RecipeType<?> getType() {
        return CBCModernWarfareRecipeTypes.MEDIUM_TRACER_DEPLOYER_RECIPE.getType();
    }
}
