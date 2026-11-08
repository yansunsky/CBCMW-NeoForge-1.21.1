package riftyboi.cbcmodernwarfare.compat.jei;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeRegistration;

import rbasamoyai.createbigcannons.index.CBCItems;
import rbasamoyai.createbigcannons.munitions.fuzes.FuzeItem;

import riftyboi.cbcmodernwarfare.CBCModernWarfare;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.FuzedMediumItemMunition;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonCartridgeItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonFuzes;

/**
 * 把本模组的弹药装配配方喂给 JEI。
 *
 * <p><b>为什么必须单独做</b>：这 4 个配方是 {@code CustomRecipe}，而
 * {@code CustomRecipe.isSpecial()} 恒为 {@code true}。JEI 的
 * {@code CategoryRecipeValidator} 里第一句就是
 *
 * <pre>
 * if (recipe.isSpecial()) return true;   // 直接放行，不生成配方页
 * </pre>
 *
 * 所以 JEI **主动跳过**它们 —— 不是注册失败，是设计如此。要显示只能像 CBC 那样
 * 自己造一批「展示用」配方喂给 JEI。
 *
 * <p><b>为什么要用 {@link ShapelessRecipe} 伪造</b>：它的
 * {@code isSpecial()} 是 {@code false}，JEI 才会渲染。这些配方**只存在于 JEI 的
 * 配方页里**，不进 {@code RecipeManager}，所以不会污染真实合成（真实逻辑仍由
 * {@code crafting/munition_assembly} 下的 4 个 {@code CustomRecipe} 负责）。
 *
 * <p>写法对照 CBC 的 {@code compat/common_jei/MunitionAssemblyRecipes}：
 * 用 {@code ItemLore} 给结果物品加一行说明（比如「+ 引信」），
 * 让玩家知道这一步做了什么。
 */
public final class MunitionAssemblyJeiRecipes {

    /** 与 CBC 一致，行首用这个假 group 让配方页归组。 */
    private static final String GROUP = CBCModernWarfare.MOD_ID + ".munition_assembly";

    /** 能装引信的弹头（6 种）：HE / HEF / HEAP / APHE / Canister / Smoke。 */
    private static final List<Item> FUZABLE_ROUNDS = List.of(
            CBCModernWarfareItem.HE_MEDIUMCANNON_ROUND.get(),
            CBCModernWarfareItem.HEF_MEDIUMCANNON_ROUND.get(),
            CBCModernWarfareItem.HEAP_MEDIUMCANNON_ROUND.get(),
            CBCModernWarfareItem.APHE_MEDIUMCANNON_ROUND.get(),
            CBCModernWarfareItem.CANISTER_MEDIUMCANNON_ROUND.get(),
            CBCModernWarfareItem.SMOKE_MEDIUMCANNON_ROUND.get());

    /** 9 种弹药筒，与弹头一一对应（顺序必须对齐，见 {@link #CARTRIDGES}）。 */
    private static final List<Item> CARTRIDGES = List.of(
            CBCModernWarfareItem.HE_MEDIUMCANNON_CARTRIDGE.get(),
            CBCModernWarfareItem.HEF_MEDIUMCANNON_CARTRIDGE.get(),
            CBCModernWarfareItem.HEAP_MEDIUMCANNON_CARTRIDGE.get(),
            CBCModernWarfareItem.APHE_MEDIUMCANNON_CARTRIDGE.get(),
            CBCModernWarfareItem.CANISTER_MEDIUMCANNON_CARTRIDGE.get(),
            CBCModernWarfareItem.SMOKE_MEDIUMCANNON_CARTRIDGE.get());

    /** 动能弹（AP / APDS / APFSDS）：能组装、能涂曳光，但**不能装引信**。 */
    private static final List<Item> KINETIC_ROUNDS = List.of(
            CBCModernWarfareItem.AP_MEDIUMCANNON_ROUND.get(),
            CBCModernWarfareItem.APDS_MEDIUMCANNON_ROUND.get(),
            CBCModernWarfareItem.APFSDS_MEDIUMCANNON_ROUND.get());

    private static final List<Item> KINETIC_CARTRIDGES = List.of(
            CBCModernWarfareItem.AP_MEDIUMCANNON_CARTRIDGE.get(),
            CBCModernWarfareItem.APDS_MEDIUMCANNON_CARTRIDGE.get(),
            CBCModernWarfareItem.APFSDS_MEDIUMCANNON_CARTRIDGE.get());

    private MunitionAssemblyJeiRecipes() {
    }

    /**
     * 注册入口（由 {@link CBCModernWarfareJeiPlugin} 调用）。
     *
     * <p>一个玩法缺口一个「配方族」，全部挂到 JEI 的原版工作台分类下。
     */
    public static void register(IRecipeRegistration registration) {
        registration.addRecipes(RecipeTypes.CRAFTING, cartridgeAssemblyRecipes());
        registration.addRecipes(RecipeTypes.CRAFTING, fuzingRecipes());
        registration.addRecipes(RecipeTypes.CRAFTING, fuzeRemovalRecipes());
        registration.addRecipes(RecipeTypes.CRAFTING, tracerRecipes());
    }

    /** 空筒 + 弹头 → 装好弹头的弹药筒（9 种弹头各一条）。 */
    private static List<RecipeHolder<CraftingRecipe>> cartridgeAssemblyRecipes() {
        List<RecipeHolder<CraftingRecipe>> out = new ArrayList<>();
        List<Item> allRounds = new ArrayList<>(FUZABLE_ROUNDS);
        allRounds.addAll(KINETIC_ROUNDS);
        List<Item> allCartridges = new ArrayList<>(CARTRIDGES);
        allCartridges.addAll(KINETIC_CARTRIDGES);

        for (int i = 0; i < allRounds.size(); i++) {
            Item round = allRounds.get(i);
            Item cartridge = allCartridges.get(i);
            ItemStack result = withLore(new ItemStack(cartridge),
                    lore("tooltip." + CBCModernWarfare.MOD_ID + ".jei_info.assembled"));
            // 输入：空筒在上一格、弹头在下一格（竖列，与真实配方的列规则一致）
            NonNullList<Ingredient> in = NonNullList.of(Ingredient.EMPTY,
                    Ingredient.of(CBCModernWarfareItem.FILLED_MEDIUMCANNON_CARTRIDGE.get()),
                    Ingredient.of(round));
            out.add(holder("cartridge_assembly/" + path(round), in, result));
        }
        return out;
    }

    /** 弹药筒 + 引信 → 装上引信的弹药筒（6 种可装引信的弹种）。 */
    private static List<RecipeHolder<CraftingRecipe>> fuzingRecipes() {
        List<RecipeHolder<CraftingRecipe>> out = new ArrayList<>();
        // 引信：CBC 全部 7 种引信类型都用 FuzeItem 的实例，取一个代表 + 逐个枚举
        List<Item> fuzes = List.of(
                CBCItems.IMPACT_FUZE.get(),
                CBCItems.INERTIA_FUZE.get(),
                CBCItems.DELAYED_IMPACT_FUZE.get(),
                CBCItems.DELAYED_INERTIA_FUZE.get(),
                CBCItems.TIMED_FUZE.get(),
                CBCItems.PROXIMITY_FUZE.get(),
                CBCItems.WIRED_FUZE.get());

        Ingredient fuzeIngredient = Ingredient.of(fuzes.stream().map(ItemStack::new)
                .toArray(ItemStack[]::new));

        for (int i = 0; i < FUZABLE_ROUNDS.size(); i++) {
            Item round = FUZABLE_ROUNDS.get(i);
            Item cartridge = CARTRIDGES.get(i);

            // ① 裸弹头 + 引信
            ItemStack bareResult = withLore(new ItemStack(round),
                    lore("tooltip." + CBCModernWarfare.MOD_ID + ".jei_info.added_fuze"));
            NonNullList<Ingredient> bareIn = NonNullList.of(Ingredient.EMPTY,
                    Ingredient.of(round), fuzeIngredient);
            out.add(holder("fuzing/" + path(round), bareIn, bareResult));

            // ② 弹药筒 + 引信（筒里的弹头被装上引信）
            ItemStack cartridgeResult = withLore(new ItemStack(cartridge),
                    lore("tooltip." + CBCModernWarfare.MOD_ID + ".jei_info.added_fuze"));
            NonNullList<Ingredient> cartridgeIn = NonNullList.of(Ingredient.EMPTY,
                    Ingredient.of(cartridge), fuzeIngredient);
            out.add(holder("fuzing/" + path(cartridge), cartridgeIn, cartridgeResult));
        }
        return out;
    }

    /** 装了引信的弹药筒 → 拆出引信（6 种）。 */
    private static List<RecipeHolder<CraftingRecipe>> fuzeRemovalRecipes() {
        List<RecipeHolder<CraftingRecipe>> out = new ArrayList<>();
        for (int i = 0; i < FUZABLE_ROUNDS.size(); i++) {
            // 用「已装引信」的样子作为输入提示：给弹药筒挂一行说明
            ItemStack input = withLore(new ItemStack(CARTRIDGES.get(i)),
                    lore("tooltip." + CBCModernWarfare.MOD_ID + ".jei_info.has_fuze"));
            Ingredient in = Ingredient.of(input);
            NonNullList<Ingredient> ins = NonNullList.of(Ingredient.EMPTY, in);

            ItemStack result = withLore(new ItemStack(CBCItems.DELAYED_IMPACT_FUZE.get()),
                    lore("tooltip." + CBCModernWarfare.MOD_ID + ".jei_info.fuze_removed"));
            out.add(holder("fuze_removal/" + path(CARTRIDGES.get(i)), ins, result));
        }
        return out;
    }

    /** 弹药筒 + 曳光剂 → 带曳光的弹药筒（9 种全部可涂）。 */
    private static List<RecipeHolder<CraftingRecipe>> tracerRecipes() {
        List<RecipeHolder<CraftingRecipe>> out = new ArrayList<>();
        List<Item> allCartridges = new ArrayList<>(CARTRIDGES);
        allCartridges.addAll(KINETIC_CARTRIDGES);

        for (Item cartridge : allCartridges) {
            ItemStack result = withLore(new ItemStack(cartridge),
                    lore("tooltip." + CBCModernWarfare.MOD_ID + ".jei_info.added_tracer"));
            NonNullList<Ingredient> in = NonNullList.of(Ingredient.EMPTY,
                    Ingredient.of(cartridge),
                    Ingredient.of(CBCItems.TRACER_TIP.get()));
            out.add(holder("tracer/" + path(cartridge), in, result));
        }
        return out;
    }

    // ------------------------------------------------------------------------------------
    // 小工具
    // ------------------------------------------------------------------------------------

    private static RecipeHolder<CraftingRecipe> holder(String path, NonNullList<Ingredient> in,
            ItemStack result) {
        ResourceLocation id = CBCModernWarfare.resource("jei/" + path.replace(':', '/'));
        return new RecipeHolder<>(id, new ShapelessRecipe(GROUP, CraftingBookCategory.MISC, result, in));
    }

    private static ItemStack withLore(ItemStack stack, Component line) {
        stack.set(DataComponents.LORE, new ItemLore(List.of(line)));
        return stack;
    }

    private static Component lore(String key) {
        return Component.translatable(key);
    }

    private static String path(Item item) {
        return net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item).getPath();
    }

    /** 供自检/调试：引信物品是否都解析到了（避免某个 CBC 引信被删导致启动崩）。 */
    static boolean sanityCheck() {
        for (Item fuze : List.of(
                CBCItems.IMPACT_FUZE.get(), CBCItems.INERTIA_FUZE.get(),
                CBCItems.DELAYED_IMPACT_FUZE.get(), CBCItems.DELAYED_INERTIA_FUZE.get(),
                CBCItems.TIMED_FUZE.get(), CBCItems.PROXIMITY_FUZE.get(),
                CBCItems.WIRED_FUZE.get())) {
            if (!(fuze instanceof FuzeItem)) {
                return false;
            }
        }
        return true;
    }
}
