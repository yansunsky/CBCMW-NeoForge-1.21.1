package riftyboi.cbcmodernwarfare.index;

import java.util.function.Supplier;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.createmod.catnip.lang.Lang;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import riftyboi.cbcmodernwarfare.CBCModernWarfare;
import riftyboi.cbcmodernwarfare.crafting.munition_assembly.CartridgeAssemblyRecipe;
import riftyboi.cbcmodernwarfare.crafting.munition_assembly.IncendiaryApplicationDeployerRecipe;
import riftyboi.cbcmodernwarfare.crafting.munition_assembly.IncendiaryApplicationRecipie;
import riftyboi.cbcmodernwarfare.crafting.munition_assembly.IncendiaryRemovalRecipie;
import riftyboi.cbcmodernwarfare.crafting.munition_assembly.MediumFuzeRemoval;
import riftyboi.cbcmodernwarfare.crafting.munition_assembly.MediumFuzingDeployerRecipe;
import riftyboi.cbcmodernwarfare.crafting.munition_assembly.MediumMunitionFuzingRecipe;
import riftyboi.cbcmodernwarfare.crafting.munition_assembly.MediumTracerApply;
import riftyboi.cbcmodernwarfare.crafting.munition_assembly.MediumTracerDeployerRecipe;
import riftyboi.cbcmodernwarfare.crafting.munition_assembly.SmokeDischargerFuzeRemoval;
import riftyboi.cbcmodernwarfare.crafting.munition_assembly.SmokeDischargerFuzing;
import riftyboi.cbcmodernwarfare.multiloader.IndexPlatform;

/**
 * 本模组的配方序列化器与配方类型。
 *
 * <p><b>状态</b>：1.20.1 在这里登记了 11 个配方（这次 R5b 只放 6 个，其余见下表）。
 * 枚举常量名直接决定注册 id —— {@code Lang.asId(name())} 把
 * {@code MEDIUM_CARTRIDGE_ASSEMBLY} 变成 {@code medium_cartridge_assembly}，
 * 与 1.20.1 生成出来的配方 JSON 里的 {@code "type"} 一一对应，**不能改名**。
 *
 * <p>还没进来的 5 个（等对应轮次，见 docs/HANDOFF.md）：
 * {@code SMOKE_MUNITION_FUZING} / {@code SMOKE_FUZE_REMOVAL}（R6/R7 烟雾排放器）、
 * {@code INCENDIARY_APPLICATION} / {@code INCENDIARY_DEPLOYER_APPLICATION} /
 * {@code INCENDIARY_REMOVAL}（R6 燃烧剂）。
 *
 * <p><b>1.21.1 三个必改点</b>（全部 javap 核实，样板是 CBC 5.11.7 的
 * {@code CBCRecipeTypes}）：
 * <ol>
 *   <li>{@code CustomRecipe} 不再持有 {@code ResourceLocation}，构造只收
 *       {@link net.minecraft.world.item.crafting.CraftingBookCategory} —— 配方变成无状态单例；</li>
 *   <li>{@code RecipeSerializer} 的 {@code fromJson/fromNetwork/toNetwork} 换成
 *       {@link RecipeSerializer#codec()} + {@link RecipeSerializer#streamCodec()}；</li>
 *   <li>{@link IRecipeTypeInfo#getType()} 的泛型加宽成
 *       {@code <I extends RecipeInput, R extends Recipe<I>> RecipeType<R>}，
 *       签名不一致会报「方法不会覆盖」。</li>
 * </ol>
 *
 * <p>其中 {@code SimpleRecipeSerializer} 就是把 1.20.1 那套
 * {@code noSerializer} + 手写序列化器搬过来，只换实现：
 * {@code MapCodec.unit(Supplier)} + {@code StreamCodec.of(空编码器, 工厂解码器)}。
 * 因为配方是无状态的，JSON 里除了 {@code type} 什么都不需要。
 */
public enum CBCModernWarfareRecipeTypes implements IRecipeTypeInfo {

    MEDIUM_CARTRIDGE_ASSEMBLY(noSerializer(CartridgeAssemblyRecipe::new)),
    MEDIUM_MUNITION_FUZING(noSerializer(MediumMunitionFuzingRecipe::new)),
    MEDIUM_FUZE_REMOVAL(noSerializer(MediumFuzeRemoval::new)),
    MEDIUM_TRACER_APPLICATION(noSerializer(MediumTracerApply::new)),
    MEDIUM_DEPLOYER_MUNITION_FUZING(noSerializer(MediumFuzingDeployerRecipe::new)),
    MEDIUM_TRACER_DEPLOYER_RECIPE(noSerializer(MediumTracerDeployerRecipe::new)),

    // R6c-1 燃烧剂（3 个）
    INCENDIARY_APPLICATION(noSerializer(IncendiaryApplicationRecipie::new)),
    INCENDIARY_DEPLOYER_APPLICATION(noSerializer(IncendiaryApplicationDeployerRecipe::new)),
    INCENDIARY_REMOVAL(noSerializer(IncendiaryRemovalRecipie::new)),

    // R6c-2 烟雾排放器（2 个）
    SMOKE_MUNITION_FUZING(noSerializer(SmokeDischargerFuzing::new)),
    SMOKE_FUZE_REMOVAL(noSerializer(SmokeDischargerFuzeRemoval::new));

    private final ResourceLocation id;
    private final Supplier<RecipeSerializer<?>> serializerObject;
    private final RecipeType<?> typeObject;
    private final NonNullSupplier<RecipeType<?>> type;

    CBCModernWarfareRecipeTypes(NonNullSupplier<RecipeSerializer<?>> serializerSupplier) {
        String name = Lang.asId(name());
        this.id = CBCModernWarfare.resource(name);
        this.serializerObject = IndexPlatform.registerRecipeSerializer(this.id, serializerSupplier);
        // 1.20.1 在这里匿名 new RecipeType 并重写 toString，1.21.1 原版已内置同款工厂
        this.typeObject = RecipeType.simple(this.id);
        this.type = () -> this.typeObject;
        IndexPlatform.registerRecipeType(this.id, this.type);
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) this.serializerObject.get();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <I extends RecipeInput, R extends Recipe<I>> RecipeType<R> getType() {
        return (RecipeType<R>) this.type.get();
    }

    /**
     * 触发本枚举的类初始化（配方序列化器与类型在构造器里就登记了）。
     *
     * <p>与 CBC 的 {@code CBCRecipeTypes#register()} 同样留空 —— 枚举常量的
     * 初始化顺序由 JVM 保证，只要有人碰了这个类就够了。
     */
    public static void register() {
        // Intentionally empty.
    }

    private static <T extends Recipe<?>> NonNullSupplier<RecipeSerializer<?>> noSerializer(Supplier<T> factory) {
        return () -> new SimpleRecipeSerializer<>(factory);
    }

    /**
     * 无状态配方的序列化器：JSON 里没有内容，网络包里也没有内容。
     *
     * <p>解码时返回 {@code factory.get()} —— 配方本身无状态，多实例无害；
     * {@code MapCodec.unit} 会缓存 {@code codec()} 那一次的调用结果，
     * 这也是 CBC 5.11.7 的写法（{@code CBCRecipeTypes$SimpleRecipeSerializer}）。
     *
     * <p>与规划时用的名字 {@code UnitRecipeSerializer} 不同：这里沿用 1.20.1 与
     * CBC 的类名，两边对照着看不会串。
     */
    private static class SimpleRecipeSerializer<T extends Recipe<?>> implements RecipeSerializer<T> {

        private final MapCodec<T> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

        SimpleRecipeSerializer(Supplier<T> factory) {
            this.codec = MapCodec.unit(factory);
            // 编码器空实现（配方无状态），解码器每次给一个实例
            this.streamCodec = StreamCodec.of((buf, recipe) -> {}, buf -> factory.get());
        }

        @Override
        public MapCodec<T> codec() {
            return this.codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
            return this.streamCodec;
        }
    }
}
