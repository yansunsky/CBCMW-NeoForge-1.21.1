package riftyboi.cbcmodernwarfare.datagen;

import static riftyboi.cbcmodernwarfare.CBCModernWarfare.REGISTRATE;

import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;

import net.minecraft.data.recipes.SpecialRecipeBuilder;

import riftyboi.cbcmodernwarfare.crafting.munition_assembly.CartridgeAssemblyRecipe;
import riftyboi.cbcmodernwarfare.crafting.munition_assembly.IncendiaryApplicationDeployerRecipe;
import riftyboi.cbcmodernwarfare.crafting.munition_assembly.IncendiaryApplicationRecipie;
import riftyboi.cbcmodernwarfare.crafting.munition_assembly.IncendiaryRemovalRecipie;
import riftyboi.cbcmodernwarfare.crafting.munition_assembly.MediumFuzeRemoval;
import riftyboi.cbcmodernwarfare.crafting.munition_assembly.MediumMunitionFuzingRecipe;
import riftyboi.cbcmodernwarfare.crafting.munition_assembly.MediumTracerApply;
import riftyboi.cbcmodernwarfare.crafting.munition_assembly.SmokeDischargerFuzeRemoval;
import riftyboi.cbcmodernwarfare.crafting.munition_assembly.SmokeDischargerFuzing;

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

        // R6c-1 燃烧剂：3 个。注意 incendiary_deployer_application 也在这里生成 ——
        // 它虽然是机械手配方（不数据驱动），但 1.20.1 同样生成了一个只有 type 的空壳
        // JSON（见 docs/reference/.../recipes/incendiary_deployer_application.json），
        // 保持等价，别"顺手"去掉。
        SpecialRecipeBuilder.special(IncendiaryApplicationRecipie::new).save(provider, "incendiary_application");
        SpecialRecipeBuilder.special(IncendiaryRemovalRecipie::new).save(provider, "incendiary_removal");
        // incendiary_deployer_application 不是 CustomRecipe（它 implements Recipe），
        // 而 1.21.1 的 SpecialRecipeBuilder.special 只收 CustomRecipe 工厂，
        // 所以它走 RecipeOutput 直投（见 writeIncendiaryDeployerShell）。
        writeIncendiaryDeployerShell(provider);

        // R6c-2 烟雾排放器：2 个
        SpecialRecipeBuilder.special(SmokeDischargerFuzing::new).save(provider, "smoke_munition_fuzing");
        SpecialRecipeBuilder.special(SmokeDischargerFuzeRemoval::new).save(provider, "smoke_fuze_removal");
    }

    /**
     * 手写 incendiary_deployer_application 的空壳配方。
     *
     * <p><b>为什么不能走 SpecialRecipeBuilder</b>：1.21.1 的
     * {@code SpecialRecipeBuilder.special} 只接受
     * {@code Function<CraftingBookCategory, CustomRecipe>}，而机械手配方实现的是
     * {@code Recipe}（它不是 {@code CustomRecipe}）—— 类型对不上。
     *
     * <p><b>改用 RecipeOutput 直投</b>：{@code RecipeOutput.accept(id, recipe, advancement)}
     * 接受任意 {@code Recipe}，序列化交给配方自己的 serializer。我们的
     * {@code SimpleRecipeSerializer} 用的是 {@code MapCodec.unit}，所以产物正好是
     * {@code { "type": "cbcmodernwarfare:incendiary_deployer_application" }}，
     * 与 1.20.1 生成结果逐字一致。
     */
    private static void writeIncendiaryDeployerShell(RegistrateRecipeProvider provider) {
        provider.accept(
                net.minecraft.resources.ResourceLocation.withDefaultNamespace("incendiary_deployer_application"),
                new IncendiaryApplicationDeployerRecipe(),
                null);
    }
}
