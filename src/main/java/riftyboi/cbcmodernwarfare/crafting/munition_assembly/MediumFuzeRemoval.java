package riftyboi.cbcmodernwarfare.crafting.munition_assembly;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import riftyboi.cbcmodernwarfare.index.CBCModernWarfareRecipeTypes;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.FuzedMediumItemMunition;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonCartridgeItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonFuzes;

/**
 * 拆引信：装好引信的一发 → 引信（输出槽）+ 拆掉引信的那一发（留在输入槽）。
 *
 * <p>「拆掉引信的那一发」由 {@link #getRemainingItems(CraftingInput)} 返回，
 * 这是 1.20.1 的原始做法，保持不变。
 *
 * <p><b>1.20.1 → 1.21.1</b>：{@code getRemainingItems(Container)} 改成
 * {@link #getRemainingItems(CraftingInput)}；{@code copy.getOrCreateTag().remove("Fuze")}
 * 换成 {@link MediumcannonFuzes#setFuze} 传 {@link ItemStack#EMPTY}（组件 API 里
 * 「传空即卸下」是 {@code remove} 的统一写法）。
 */
public class MediumFuzeRemoval extends CustomRecipe {

    public MediumFuzeRemoval() {
        this(CraftingBookCategory.MISC);
    }

    public MediumFuzeRemoval(CraftingBookCategory category) {
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

            if (stack.getItem() instanceof MediumcannonCartridgeItem) {
                stack = MediumcannonCartridgeItem.getProjectileStack(stack);
            }
            if (stack.getItem() instanceof FuzedMediumItemMunition) {
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
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack target = ItemStack.EMPTY;

        for (int i = 0; i < input.size(); ++i) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (!target.isEmpty()) {
                return ItemStack.EMPTY;
            }

            if (stack.getItem() instanceof MediumcannonCartridgeItem) {
                stack = MediumcannonCartridgeItem.getProjectileStack(stack);
            }
            if (stack.getItem() instanceof FuzedMediumItemMunition) {
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
            if (stack.getItem() instanceof FuzedMediumItemMunition) {
                // 裸弹头：留着弹头本身，只是剥掉引信
                if (MediumcannonFuzes.hasFuze(stack)) {
                    ItemStack copy = stack.copy();
                    MediumcannonFuzes.setFuze(copy, ItemStack.EMPTY);
                    copy.setCount(1);
                    result.set(i, copy);
                }
                break;
            } else if (stack.getItem() instanceof MediumcannonCartridgeItem) {
                // 弹药筒：弹头剥引信后写回新筒（原版就是重建一个，不保留其它组件）
                ItemStack projectile = MediumcannonCartridgeItem.getProjectileStack(stack);
                if (projectile.getItem() instanceof FuzedMediumItemMunition
                        && MediumcannonFuzes.hasFuze(projectile)) {
                    ItemStack roundCopy = projectile.copy();
                    MediumcannonFuzes.setFuze(roundCopy, ItemStack.EMPTY);
                    roundCopy.setCount(1);
                    ItemStack cartridge = new ItemStack(stack.getItem());
                    MediumcannonCartridgeItem.writeProjectile(roundCopy, cartridge);
                    result.set(i, cartridge);
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
        return CBCModernWarfareRecipeTypes.MEDIUM_FUZE_REMOVAL.getSerializer();
    }
}
