package riftyboi.cbcmodernwarfare.datagen;

import static riftyboi.cbcmodernwarfare.CBCModernWarfare.REGISTRATE;

import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;

import net.minecraft.data.recipes.SpecialRecipeBuilder;

import riftyboi.cbcmodernwarfare.crafting.munition_assembly.CartridgeAssemblyRecipe;
import riftyboi.cbcmodernwarfare.crafting.munition_assembly.MediumFuzeRemoval;
import riftyboi.cbcmodernwarfare.crafting.munition_assembly.MediumMunitionFuzingRecipe;
import riftyboi.cbcmodernwarfare.crafting.munition_assembly.MediumTracerApply;

/**
 * 配方数据生成 —— 只生成「特殊配方」（{@code CustomRecipe}）的那几个 JSON。
 *
 * <p>每个文件的内容只有一行：
 * <pre>
 * { "type": "cbcmodernwarfare:medium_cartridge_assembly" }
 * </pre>
 * 因为配方本身无状态，{@code MapCodec.unit} 会把其余字段全部忽略。
 *
 * <p>两个机械手配方（{@code MEDIUM_DEPLOYER_MUNITION_FUZING} /
 * {@code MEDIUM_TRACER_DEPLOYER_RECIPE}）**不生成 JSON** —— 它们不是数据驱动的，
 * 由 {@code MunitionAssemblyDeployerRecipes} 在机械手查询时现场构造。1.20.1 也是这样。
 *
 * <p><b>1.20.1 → 1.21.1</b>：
 * <ul>
 *   <li>{@code Consumer<FinishedRecipe>} 换成 {@link RegistrateRecipeProvider}
 *       （它本身就是 1.21.1 的 {@code RecipeOutput}）；</li>
 *   <li>{@code SpecialRecipeBuilder.special(RecipeSerializer)} 没了，
 *       现在收的是 {@code Function<CraftingBookCategory, Recipe<?>>} ——
 *       所以每个配方类都必须有 {@code (CraftingBookCategory)} 构造；</li>
 *   <li>输出目录 {@code recipes/} → {@code recipe/}（单数），由原版自动处理。</li>
 * </ul>
 *
 * <p><b>顺带修的上游问题</b>：1.20.1 的 {@code CBCMWCraftingRecipieProvider.register()}
 * 定义了但**从来没有被调用过**，所以那次生成出来的 JSON 是历史遗留物，重新 datagen 是产不出来的。
 * 这里在 {@code init()} 里真的注册进去了，见 docs/log/13-Phase2-R5b.md §3。
 */
public final class CBCModernWarfareRecipeGen {

    private CBCModernWarfareRecipeGen() {
    }

    public static void register() {
        REGISTRATE.addDataGenerator(ProviderType.RECIPE, CBCModernWarfareRecipeGen::buildRecipes);
    }

    private static void buildRecipes(RegistrateRecipeProvider provider) {
        // id 保持 1.20.1 生成时的原值（minecraft 命名空间 + 原名），不改成 mod 命名空间
        SpecialRecipeBuilder.special(CartridgeAssemblyRecipe::new).save(provider, "medium_cartridge_assembly");
        SpecialRecipeBuilder.special(MediumMunitionFuzingRecipe::new).save(provider, "medium_munition_fuzing");
        SpecialRecipeBuilder.special(MediumFuzeRemoval::new).save(provider, "medium_fuze_removal");
        SpecialRecipeBuilder.special(MediumTracerApply::new).save(provider, "medium_tracer_application");
    }
}
