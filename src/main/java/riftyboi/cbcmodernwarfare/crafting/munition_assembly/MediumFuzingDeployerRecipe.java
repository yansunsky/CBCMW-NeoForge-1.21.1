package riftyboi.cbcmodernwarfare.crafting.munition_assembly;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import rbasamoyai.createbigcannons.munitions.fuzes.FuzeItem;

import riftyboi.cbcmodernwarfare.index.CBCModernWarfareRecipeTypes;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.FuzedMediumItemMunition;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonCartridgeItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonFuzes;

/**
 * 机械手版装引信：机械手手持引信，作用于传送带/置物台上的弹头或弹药筒。
 *
 * <p>与 {@link MediumTracerDeployerRecipe} 一样是「值对象」配方，不是数据驱动的，
 * 由 {@link MunitionAssemblyDeployerRecipes} 现场构造。
 *
 * <p><b>⚠ 与 1.20.1 的差异（修了一个上游 bug，详见 docs/log/13-Phase2-R5b.md §3）</b>：
 * 原版 {@code matches} 的第二个分支判的是 {@code MediumcannonRoundItem}（裸弹头），
 * 但分支体里调的是 {@code getProjectileStack(munition)} —— 那是「从弹药筒里取弹头」，
 * 作用在裸弹头上永远取到 {@link ItemStack#EMPTY}，于是返回 {@code false}；
 * 真正的弹药筒又在下一个分支被 {@code !(instanceof MediumcannonRoundItem)} 直接否决。
 * 结果这个配方对本模组的任何物品都不成立，只有 CBC 自己的 {@code FuzedItemMunition}
 * （大炮弹等）会命中 —— 等于替 CBC 抢了它自己的机械手配方。
 *
 * <p>这里按结构本来的意图修正：裸弹头走「直接判定」，弹药筒走「剥出弹头再判定」，
 * 并且**不再接管 CBC 的 {@code FuzedItemMunition}** —— 那是 CBC 自带
 * {@code MunitionFuzingDeployerRecipe} 的职责，两边同时命中会互相顶掉。
 * 这是一次**严格超集**修正：没有任何原先能成立的组合被取消。
 */
public class MediumFuzingDeployerRecipe implements Recipe<RecipeInput> {

    private final ItemStack munition;
    private final ItemStack fuze;

    /** 供 {@code SimpleRecipeSerializer} 的 {@code MapCodec.unit} 使用。 */
    public MediumFuzingDeployerRecipe() {
        this(ItemStack.EMPTY, ItemStack.EMPTY);
    }

    public MediumFuzingDeployerRecipe(ItemStack munition, ItemStack fuze) {
        this.munition = munition.copy();
        this.fuze = fuze.copy();
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        if (!(this.fuze.getItem() instanceof FuzeItem)) {
            return false;
        }
        // 裸弹头：还没装引信就能装
        if (this.munition.getItem() instanceof FuzedMediumItemMunition) {
            return !MediumcannonFuzes.hasFuze(this.munition);
        }
        // 弹药筒：筒里的弹头可装引信且还没装（getProjectileStack 返回副本，只读不改）
        if (this.munition.getItem() instanceof MediumcannonCartridgeItem) {
            ItemStack round = MediumcannonCartridgeItem.getProjectileStack(this.munition);
            return !round.isEmpty()
                    && round.getItem() instanceof FuzedMediumItemMunition
                    && !MediumcannonFuzes.hasFuze(round);
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
        ItemStack fuzeCopy = this.fuze.copy();
        fuzeCopy.setCount(1);

        if (result.getItem() instanceof FuzedMediumItemMunition) {
            MediumcannonFuzes.setFuze(result, fuzeCopy);
        } else if (result.getItem() instanceof MediumcannonCartridgeItem) {
            ItemStack projectile = MediumcannonCartridgeItem.getProjectileStack(result);
            MediumcannonFuzes.setFuze(projectile, fuzeCopy);
            MediumcannonCartridgeItem.writeProjectile(projectile, result);
        }
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CBCModernWarfareRecipeTypes.MEDIUM_DEPLOYER_MUNITION_FUZING.getSerializer();
    }

    @Override
    public RecipeType<?> getType() {
        return CBCModernWarfareRecipeTypes.MEDIUM_DEPLOYER_MUNITION_FUZING.getType();
    }
}
