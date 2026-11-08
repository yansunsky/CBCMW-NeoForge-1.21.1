package riftyboi.cbcmodernwarfare.crafting.munition_assembly;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import rbasamoyai.createbigcannons.index.CBCDataComponents;
import rbasamoyai.createbigcannons.index.CBCItems;

import riftyboi.cbcmodernwarfare.index.CBCModernWarfareRecipeTypes;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonAmmoItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonRoundItem;

/**
 * 涂曳光：弹头（或弹药筒）+ 曳光剂 → 带曳光标的那一发。
 *
 * <p><b>1.20.1 → 1.21.1</b>：曳光标记从 {@code tag.getBoolean("Tracer")} 换成
 * {@link CBCDataComponents#AUTOCANNON_TRACER}（复用 CBC 的组件，不自己注册），
 * 见 {@code docs/analysis/05-DataComponents迁移规范.md} §1。
 *
 * <p>注意 {@code MediumcannonAmmoItem#setTracer} 在 R5 里被修正为「真的写回」
 * —— 1.20.1 那个实现既无视 {@code value} 又从不写回，整条涂曳光链路在原版是死的。
 */
public class MediumTracerApply extends CustomRecipe {

    public MediumTracerApply() {
        this(CraftingBookCategory.MISC);
    }

    public MediumTracerApply(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        ItemStack round = ItemStack.EMPTY;
        ItemStack tracer = ItemStack.EMPTY;

        for (int i = 0; i < input.size(); ++i) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.getItem() instanceof MediumcannonRoundItem) {
                if (!round.isEmpty() || stack.getOrDefault(CBCDataComponents.AUTOCANNON_TRACER, false)) {
                    return false;
                }
                round = stack;
            } else if (stack.getItem() instanceof MediumcannonAmmoItem item) {
                if (!round.isEmpty() || item.isTracer(stack)) {
                    return false;
                }
                round = stack;
            } else if (CBCItems.TRACER_TIP.isIn(stack)) {
                if (!tracer.isEmpty()) {
                    return false;
                }
                tracer = stack;
            } else {
                return false;
            }
        }

        return !round.isEmpty() && !tracer.isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack round = ItemStack.EMPTY;
        ItemStack tracer = ItemStack.EMPTY;

        for (int i = 0; i < input.size(); ++i) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.getItem() instanceof MediumcannonRoundItem || stack.getItem() instanceof MediumcannonAmmoItem) {
                if (!round.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                round = stack;
            } else if (CBCItems.TRACER_TIP.isIn(stack)) {
                if (!tracer.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                tracer = stack;
            } else {
                return ItemStack.EMPTY;
            }
        }

        if (round.isEmpty() || tracer.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack result = round.copy();
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
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CBCModernWarfareRecipeTypes.MEDIUM_TRACER_APPLICATION.getSerializer();
    }
}
