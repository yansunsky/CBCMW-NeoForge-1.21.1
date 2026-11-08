package riftyboi.cbcmodernwarfare.crafting.munition_assembly;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import rbasamoyai.createbigcannons.munitions.fuzes.FuzeItem;

import riftyboi.cbcmodernwarfare.index.CBCModernWarfareItem;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareRecipeTypes;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.FuzedMediumItemMunition;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonCartridgeItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonFuzes;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonRoundItem;

/**
 * 弹药筒组装：弹头 + 空筒（+ 可选引信）→ 装好弹头的弹药筒。
 *
 * <p>四个物品必须竖着排成一列（同一列上任意三格都行），这是原版的约束，
 * 由 {@code position % width} 判定。
 *
 * <p><b>1.20.1 → 1.21.1</b>：
 * <ul>
 *   <li>{@code CraftingContainer} 换成 {@link CraftingInput}，
 *       {@code getContainerSize()} 已删除，改用 {@link CraftingInput#size()}；</li>
 *   <li>{@code Recipe#assemble(Container, RegistryAccess)} 换成
 *       {@link #assemble(CraftingInput, HolderLookup.Provider)}（第二个参数原样不用）；</li>
 *   <li>弹头与引信改为写组件：{@link MediumcannonCartridgeItem#writeProjectile} +
 *       {@link MediumcannonFuzes#setFuze}，不再手工拼 {@code CompoundTag}。</li>
 * </ul>
 */
public class CartridgeAssemblyRecipe extends CustomRecipe {

    /** 供 {@code SimpleRecipeSerializer} 的 {@code MapCodec.unit} 使用（配方无状态）。 */
    public CartridgeAssemblyRecipe() {
        this(CraftingBookCategory.MISC);
    }

    /** 数据生成走这个构造（{@code SpecialRecipeBuilder} 会传类别进来）。 */
    public CartridgeAssemblyRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        int roundPosition = -1;
        int cartridgePosition = -1;
        boolean searchFuze = false;
        int fuzePosition = -1;

        for (int i = 0; i < input.size(); ++i) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.getItem() instanceof MediumcannonRoundItem) {
                if (roundPosition != -1) {
                    return false;
                }
                roundPosition = i;
                // 只有可装引信的弹头才允许出现第三种材料
                if (stack.getItem() instanceof FuzedMediumItemMunition) {
                    searchFuze = true;
                }
            } else if (stack.getItem() == CBCModernWarfareItem.FILLED_MEDIUMCANNON_CARTRIDGE.get()) {
                if (cartridgePosition != -1) {
                    return false;
                }
                cartridgePosition = i;
            } else if (stack.getItem() instanceof FuzeItem) {
                if (fuzePosition != -1) {
                    return false;
                }
                fuzePosition = i;
            } else {
                return false;
            }
        }

        if (roundPosition == -1 || cartridgePosition == -1 || !searchFuze && fuzePosition != -1) {
            return false;
        }
        int width = input.width();
        int roundColumn = roundPosition % width;
        return cartridgePosition % width == roundColumn
                && (fuzePosition == -1 || fuzePosition % width == roundColumn);
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        int roundPosition = -1;
        int cartridgePosition = -1;
        boolean searchFuze = false;
        int fuzePosition = -1;

        for (int i = 0; i < input.size(); ++i) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.getItem() instanceof MediumcannonRoundItem) {
                if (roundPosition != -1) {
                    return ItemStack.EMPTY;
                }
                roundPosition = i;
                if (stack.getItem() instanceof FuzedMediumItemMunition) {
                    searchFuze = true;
                }
            } else if (stack.getItem() == CBCModernWarfareItem.FILLED_MEDIUMCANNON_CARTRIDGE.get()) {
                if (cartridgePosition != -1) {
                    return ItemStack.EMPTY;
                }
                cartridgePosition = i;
            } else if (stack.getItem() instanceof FuzeItem) {
                if (fuzePosition != -1) {
                    return ItemStack.EMPTY;
                }
                fuzePosition = i;
            } else {
                return ItemStack.EMPTY;
            }
        }

        if (roundPosition == -1 || cartridgePosition == -1 || !searchFuze && fuzePosition != -1) {
            return ItemStack.EMPTY;
        }
        int width = input.width();
        int roundColumn = roundPosition % width;
        if (cartridgePosition % width != roundColumn || fuzePosition != -1 && fuzePosition % width != roundColumn) {
            return ItemStack.EMPTY;
        }

        ItemStack result = ItemStack.EMPTY;
        if (input.getItem(roundPosition).getItem() instanceof MediumcannonRoundItem round) {
            result = round.getCartridgeType();
        }

        ItemStack roundCopy = input.getItem(roundPosition).copy();
        roundCopy.setCount(1);
        if (fuzePosition != -1) {
            ItemStack fuzeCopy = input.getItem(fuzePosition).copy();
            fuzeCopy.setCount(1);
            // 引信先装到「弹头副本」上，再把弹头整个写进弹药筒（顺序不能反）
            MediumcannonFuzes.setFuze(roundCopy, fuzeCopy);
        }
        MediumcannonCartridgeItem.writeProjectile(roundCopy, result);

        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 1 && height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CBCModernWarfareRecipeTypes.MEDIUM_CARTRIDGE_ASSEMBLY.getSerializer();
    }
}
