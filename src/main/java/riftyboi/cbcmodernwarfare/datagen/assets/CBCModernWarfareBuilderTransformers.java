package riftyboi.cbcmodernwarfare.datagen.assets;

import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import riftyboi.cbcmodernwarfare.CBCModernWarfare;

/**
 * Block-builder decoration helpers that 1.20.1 expressed with {@code @ExpectPlatform}
 * (37 stubs in {@code common/}, one Impl in {@code forge/}).
 *
 * <p><b>What the 1.20.1 code did</b> — every helper returns a
 * {@code NonNullUnaryOperator<BlockBuilder<T, P>>} so it can be used as
 * {@code .transform(CBCModernWarfareBuilderTransformers::reactiveArmorBlock)}. The Forge Impl set
 * {@code noOcclusion}, registered a {@code RenderType} layer, and wrote a blockstate with a shared
 * base model plus per-family textures.
 *
 * <p><b>Why only two helpers are ported in R2.</b> R2 is the vertical slice and only needs the
 * reactive-armour family. The remaining 35 belong to {@code cannons/}, {@code munitions/} and
 * {@code crafting/} and are ported with their blocks in R4–R9; writing them now would mean coding
 * against classes that do not exist yet. This file is the single home for all of them.
 *
 * <p><b>One 1.21.1 API change found by decompiling the dependency</b> (see
 * {@code docs/analysis/03-垂直切片验证结论.md}): {@code BlockBuilder#addLayer} is
 * {@code @Deprecated(forRemoval = true)} in Registrate {@code MC1.21-1.3.0+67}. Its replacement is
 * {@code ModelBuilder#renderType(ResourceLocation)}, which writes a {@code render_type} key into
 * the model JSON, resolved through {@code NamedRenderTypeManager}. Only the vanilla names are
 * pre-registered — {@code minecraft:solid / cutout / cutout_mipped / cutout_mipped_all /
 * translucent / tripwire} — and 1.20.1's {@code RenderType::cutoutMipped} maps to
 * {@code minecraft:cutout_mipped}. The blockstate half of the old code carries over verbatim:
 * {@code p.models().withExistingParent(...)} still returns a {@code BlockModelBuilder} and
 * {@code RegistrateBlockstateProvider#simpleBlock(Block, ModelFile)} is unchanged.
 */
public final class CBCModernWarfareBuilderTransformers {

    private CBCModernWarfareBuilderTransformers() {}

    /**
     * Reactive armour: shared base model {@code block/reactive_block} whose {@code side} and
     * {@code bottom} textures point at the {@code tiled<name>_reactive_armor} pair.
     *
     * @param name texture suffix — {@code ""} for the default set, else {@code "_tan"}, {@code "_green"}
     */
    public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> reactiveArmorBlock(String name) {
        ResourceLocation model = CBCModernWarfare.resource("block/reactive_block");
        ResourceLocation side = CBCModernWarfare.resource("block/tiled" + name + "_reactive_armor");
        ResourceLocation bottom = CBCModernWarfare.resource("block/tiled" + name + "_reactive_armor_bottom");
        return b -> b
                .properties(p -> p.noOcclusion())
                .blockstate((c, p) -> p.simpleBlock(c.getEntry(), p.models()
                        .withExistingParent(c.getName(), model)
                        .texture("side", side)
                        .texture("bottom", bottom)
                        .texture("particle", side)
                        .renderType("minecraft:cutout_mipped")));
    }

    /**
     * Composite armour: shared {@code block/sided_block} model with separate top/bottom/side
     * textures. Ported here because it is the sibling of the reactive helper and the same
     * one-liner; it is used by {@code COMPOSITE_BLOCK}, which R2 registers as a second, simpler
     * case to prove the helper generalises.
     */
    public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> compositeBlock() {
        ResourceLocation model = CBCModernWarfare.resource("block/sided_block");
        ResourceLocation side = CBCModernWarfare.resource("block/composite_armor_side");
        ResourceLocation bottom = CBCModernWarfare.resource("block/composite_armor_bottom");
        ResourceLocation top = CBCModernWarfare.resource("block/composite_armor");
        return b -> b
                .properties(p -> p.noOcclusion())
                .blockstate((c, p) -> p.simpleBlock(c.getEntry(), p.models()
                        .withExistingParent(c.getName(), model)
                        .texture("side", side)
                        .texture("bottom", bottom)
                        .texture("top", top)
                        .texture("particle", top)
                        .renderType("minecraft:cutout_mipped")));
    }
}
