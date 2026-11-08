package riftyboi.cbcmodernwarfare.crafting.munition_assembly;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import rbasamoyai.createbigcannons.munitions.fuzes.FuzeItem;

import riftyboi.cbcmodernwarfare.index.CBCModernWarfareRecipeTypes;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.FuzedMediumItemMunition;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonCartridgeItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonFuzes;

/**
 * 装引信：可装引信的弹头（或已装弹头的弹药筒）+ 引信 → 装好引信的那一发。
 *
 * <p><b>1.20.1 → 1.21.1 的关键差异</b>：1.20.1 在弹药筒分支里手工拼
 * {@code Projectile.tag.Fuze}；现在必须走「取副本 → 改副本 → 写回」三步，
 * 因为 {@link MediumcannonCartridgeItem#getProjectileStack} 返回的是副本：
 *
 * <pre>
 * ItemStack projectile = MediumcannonCartridgeItem.getProjectileStack(result);
 * MediumcannonFuzes.setFuze(projectile, fuzeCopy);
 * MediumcannonCartridgeItem.writeProjectile(projectile, result);   // ← 漏了这行就静默无效
 * </pre>
 */
public class MediumMunitionFuzingRecipe extends CustomRecipe {

    public MediumMunitionFuzingRecipe() {
        this(CraftingBookCategory.MISC);
    }

    public MediumMunitionFuzingRecipe(CraftingBookCategory category) {
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

            // 弹药筒先剥成里面的弹头再判定
            if (stack.getItem() instanceof MediumcannonCartridgeItem) {
                if (!round.isEmpty()) {
                    return false;
                }
                stack = MediumcannonCartridgeItem.getProjectileStack(stack);
            }

            if (stack.getItem() instanceof FuzedMediumItemMunition) {
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
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack round = ItemStack.EMPTY;
        ItemStack fuze = ItemStack.EMPTY;

        for (int i = 0; i < input.size(); ++i) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.getItem() instanceof FuzedMediumItemMunition
                    || stack.getItem() instanceof MediumcannonCartridgeItem) {
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
        ItemStack fuzeCopy = fuze.copy();
        fuzeCopy.setCount(1);

        if (result.getItem() instanceof FuzedMediumItemMunition) {
            // 裸弹头：引信直接装自己身上
            MediumcannonFuzes.setFuze(result, fuzeCopy);
        } else if (result.getItem() instanceof MediumcannonCartridgeItem) {
            // 弹药筒：引信装到筒内弹头上，再整体写回
            ItemStack projectile = MediumcannonCartridgeItem.getProjectileStack(result);
            MediumcannonFuzes.setFuze(projectile, fuzeCopy);
            MediumcannonCartridgeItem.writeProjectile(projectile, result);
        }
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CBCModernWarfareRecipeTypes.MEDIUM_MUNITION_FUZING.getSerializer();
    }
}
