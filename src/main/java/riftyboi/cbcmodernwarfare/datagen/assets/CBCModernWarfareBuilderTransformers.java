package riftyboi.cbcmodernwarfare.datagen.assets;

import com.simibubi.create.AllTags.AllBlockTags;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.foundation.data.BlockStateGen;

import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.tags.BlockTags;

import rbasamoyai.createbigcannons.crafting.casting.CannonCastMouldBlock;

import riftyboi.cbcmodernwarfare.CBCModernWarfare;
import riftyboi.cbcmodernwarfare.cannons.medium_cannon.MediumcannonBlock;
import riftyboi.cbcmodernwarfare.cannons.medium_cannon.MediumcannonBlockItem;
import riftyboi.cbcmodernwarfare.cannons.medium_cannon.MediumcannonBarrelBlock;
import riftyboi.cbcmodernwarfare.cannons.medium_cannon.breech.MediumcannonBreechBlock;
import riftyboi.cbcmodernwarfare.cannons.rotarycannon.RotarycannonBarrelBlock;
import riftyboi.cbcmodernwarfare.cannons.rotarycannon.RotarycannonBlock;
import riftyboi.cbcmodernwarfare.cannons.rotarycannon.RotarycannonBlockItem;

/**
 * 方块构建器的装饰辅助：1.20.1 用 {@code @ExpectPlatform}（37 个桩）+ Forge 侧
 * {@code CBCModernWarfareBuilderTransformersImpl}（550 行）实现。
 *
 * <p>单平台后不再是两段式，直接在这里给出实现。
 *
 * <p><b>1.20.1 → 1.21.1 的两处 API 变化</b>：
 * <ol>
 *   <li>{@code BlockBuilder#addLayer(...)} 已 {@code @Deprecated(forRemoval = true)}。
 *       改用模型 JSON 里的 {@code render_type}：{@code BlockModelBuilder#renderType("minecraft:cutout_mipped")}。</li>
 *   <li>{@code ModelFile} 与 {@code BlockModelBuilder} 换了包：
 *       {@code net.minecraftforge.client.model.generators.*} →
 *       {@code net.neoforged.neoforge.client.model.generators.*}。</li>
 * </ol>
 * {@code BlockStateGen.directionalBlockIgnoresWaterlogged(...)}、{@code AllBlockTags.SAFE_NBT}、
 * {@code RegistrateBlockstateProvider#simpleBlock} 等全部未变（javap 核对）。
 */
public final class CBCModernWarfareBuilderTransformers {

    private CBCModernWarfareBuilderTransformers() {}

    // ------------------------------------------------------------------------------------
    // R2 已迁：反应装甲 / 复合装甲
    // ------------------------------------------------------------------------------------

    /**
     * 反应装甲：共享基础模型 {@code block/reactive_block}，
     * {@code side} 与 {@code bottom} 指向 {@code tiled<name>_reactive_armor} 那一对贴图。
     *
     * @param name 贴图后缀 —— 默认套为 {@code ""}，另有 {@code "_tan"}、{@code "_green"}
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

    /** 复合装甲：共享 {@code block/sided_block} 模型，上下与侧面各用一张贴图。 */
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

    // ------------------------------------------------------------------------------------
    // R4a：铸型
    // ------------------------------------------------------------------------------------

    /**
     * 铸型方块：主体 + 可选沙层（{@code CannonCastMouldBlock.SAND} 为 true 时叠第二层模型）。
     *
     * <p>1.20.1 用 {@code addLayer(RenderType::solid)}，这里改成模型 JSON 的
     * {@code render_type: minecraft:solid}（solid 也是默认值，写出来是为了与迁移前的意图对齐）。
     */
    public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> castMould(String size) {
        ResourceLocation baseLoc = CBCModernWarfare.resource("block/cast_mould/" + size + "_cast_mould");
        ResourceLocation sandLoc = CBCModernWarfare.resource("block/casting_sand");
        return b -> b
                .properties(p -> p.mapColor(MapColor.PODZOL))
                .properties(p -> p.strength(2.0f, 3.0f))
                .properties(p -> p.sound(SoundType.WOOD))
                .properties(p -> p.noOcclusion())
                .tag(BlockTags.MINEABLE_WITH_AXE)
                .tag(AllBlockTags.SAFE_NBT.tag)
                .blockstate((c, p) -> p.getMultipartBuilder(c.getEntry())
                        .part()
                        .modelFile(p.models().getExistingFile(baseLoc))
                        .addModel()
                        .end()
                        .part()
                        .modelFile(p.models().getExistingFile(sandLoc))
                        .addModel()
                        .condition(CannonCastMouldBlock.SAND, true)
                        .end())
                .item()
                .model((c, p) -> p.getBuilder(c.getName()).parent(p.getExistingFile(baseLoc)))
                .build();
    }

    // ------------------------------------------------------------------------------------
    // R4a：中型炮炮管
    // ------------------------------------------------------------------------------------

    /**
     * 中型炮炮管。
     *
     * <p>方块状态由 {@code ASSEMBLED} + {@code BARREL_END} 组合出三种模型：
     * 装配态（只有粒子贴图）、法兰态、普通态。
     */
    public static <T extends Block & MediumcannonBlock, P> NonNullUnaryOperator<BlockBuilder<T, P>> mediumcannonBarrel(
            String pathAndMaterial) {
        ResourceLocation texLoc = CBCModernWarfare.resource("block/" + pathAndMaterial + "_medium_barrel");
        return b -> b
                .properties(p -> p.noOcclusion())
                .blockstate((c, p) -> BlockStateGen.directionalBlockIgnoresWaterlogged(c, p, s -> {
                    boolean assembled = s.getValue(MediumcannonBarrelBlock.ASSEMBLED);
                    String name = c.getName() + "_"
                            + (assembled ? "assembled" : s.getValue(MediumcannonBarrelBlock.BARREL_END).getSerializedName());
                    if (assembled) {
                        return p.models().getBuilder(name).texture("particle", texLoc);
                    }
                    ResourceLocation loc = switch (s.getValue(MediumcannonBarrelBlock.BARREL_END)) {
                        case FLANGED -> CBCModernWarfare.resource("block/mediumcannon/barrel_flanged");
                        default -> CBCModernWarfare.resource("block/mediumcannon/barrel");
                    };
                    return p.models().withExistingParent(name, loc).texture("material", texLoc);
                }))
                .tag(AllBlockTags.SAFE_NBT.tag)
                .item(MediumcannonBlockItem::new)
                .model((c, p) -> p.withExistingParent(c.getName(),
                        CBCModernWarfare.resource("block/mediumcannon/barrel")).texture("material", texLoc))
                .build();
    }

    /**
     * 中型炮炮闩。
     *
     * @param complete 成品为 true（有三种旋转态 + 装配态），未完成品为 false（只有一种）
     */
    public static <T extends Block & MediumcannonBlock, P> NonNullUnaryOperator<BlockBuilder<T, P>> mediumcannonBreech(
            String pathAndMaterial, boolean complete) {
        ResourceLocation texLoc = CBCModernWarfare.resource("block/" + pathAndMaterial + "_medium_breech");
        ResourceLocation baseLoc = CBCModernWarfare.resource("block/mediumcannon/breech");
        NonNullUnaryOperator<BlockBuilder<T, P>> result = b -> b
                .properties(p -> p.noOcclusion())
                .blockstate((c, p) -> BlockStateGen.directionalBlockIgnoresWaterlogged(c, p, s -> {
                    if (complete && s.getValue(MediumcannonBreechBlock.ASSEMBLED)) {
                        return p.models().getBuilder(baseLoc + "_assembled").texture("particle", texLoc);
                    }
                    boolean axisAlongFirst = s.getValue(DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE);
                    boolean rotated = (s.getValue(BlockStateProperties.FACING).getAxis() == Direction.Axis.X) == axisAlongFirst;
                    String suf = rotated ? "_rotated" : "";
                    return p.models().withExistingParent(c.getName() + suf, baseLoc + suf).texture("material", texLoc);
                }))
                .tag(AllBlockTags.SAFE_NBT.tag);
        if (complete) {
            result = result.andThen(b -> b.item(MediumcannonBlockItem::new)
                    .model((c, p) -> p.withExistingParent(c.getName(),
                            CBCModernWarfare.resource("block/mediumcannon/breech_item")).texture("material", texLoc))
                    .build());
        } else {
            result = result.andThen(b -> b.item(MediumcannonBlockItem::new)
                    .model((c, p) -> p.blockItem(c))
                    .build());
        }
        return result;
    }

    /**
     * 未完成炮闩。
     *
     * <p><b>与 {@link #mediumcannonBreech} 是两段独立逻辑，不能合并</b> ——
     * 未完成方块没有 {@code AXIS} 属性（{@code IncompleteMediumcannonBlock} 只继承了
     * {@code FACING}/{@code WATERLOGGED}），去读 {@code AXIS_ALONG_FIRST_COORDINATE}
     * 会抛 {@code IllegalArgumentException: Cannot get property ... as it does not exist}。
     * 1.20.1 的 Impl 里这两个方法本来就是分开写的，这里保持原样。
     */
    public static <T extends Block & MediumcannonBlock, P> NonNullUnaryOperator<BlockBuilder<T, P>> incompleteCannonBreech(
            String pathAndMaterial, boolean complete) {
        ResourceLocation texLoc = CBCModernWarfare.resource("block/" + pathAndMaterial + "_medium_breech");
        ResourceLocation baseLoc = CBCModernWarfare.resource("block/mediumcannon/breech");
        NonNullUnaryOperator<BlockBuilder<T, P>> result = b -> b
                .properties(p -> p.noOcclusion())
                .blockstate((c, p) -> BlockStateGen.directionalBlockIgnoresWaterlogged(c, p,
                        s -> p.models().withExistingParent(c.getName(), baseLoc).texture("material", texLoc)))
                .tag(AllBlockTags.SAFE_NBT.tag);
        result = result.andThen(b -> b.item(MediumcannonBlockItem::new)
                .model((c, p) -> p.blockItem(c))
                .build());
        return result;
    }

    /**
     * 中型炮后坐炮管。
     *
     * @param complete 成品为 true（带 {@code ASSEMBLED} 态），未完成品为 false
     */
    public static <T extends Block & MediumcannonBlock, P> NonNullUnaryOperator<BlockBuilder<T, P>> mediumcannonRecoilBarrel(
            String pathAndMaterial, boolean complete) {
        ResourceLocation texLoc = CBCModernWarfare.resource("block/" + pathAndMaterial + "_medium_recoil_barrel");
        ResourceLocation baseLoc = CBCModernWarfare.resource("block/mediumcannon/recoil_barrel");
        NonNullUnaryOperator<BlockBuilder<T, P>> result = b -> b
                .properties(p -> p.noOcclusion())
                .blockstate((c, p) -> BlockStateGen.directionalBlockIgnoresWaterlogged(c, p, s -> {
                    // 只有成品方块才有 ASSEMBLED；未完成方块读它同样会抛异常。
                    if (complete && s.getValue(
                            riftyboi.cbcmodernwarfare.cannons.medium_cannon.recoil_barrel
                                    .MediumcannonRecoilBarrelBlock.ASSEMBLED)) {
                        return p.models().getBuilder(baseLoc + "_assembled").texture("particle", texLoc);
                    }
                    return p.models().withExistingParent(c.getName(), baseLoc).texture("material", texLoc);
                }))
                .tag(AllBlockTags.SAFE_NBT.tag);
        if (complete) {
            result = result.andThen(b -> b.item(MediumcannonBlockItem::new)
                    .model((c, p) -> p.withExistingParent(c.getName(), baseLoc).texture("material", texLoc))
                    .build());
        } else {
            result = result.andThen(b -> b.item(MediumcannonBlockItem::new)
                    .model((c, p) -> p.blockItem(c))
                    .build());
        }
        return result;
    }

    // ------------------------------------------------------------------------------------
    // R4a：未开孔的炮管（只作装饰 / 后续钻孔）
    // ------------------------------------------------------------------------------------

    public static <T extends Block & MediumcannonBlock, P> NonNullUnaryOperator<BlockBuilder<T, P>> unboredMediumcannonBarrel(
            String pathAndMaterial) {
        return unboredMediumcannonBlock(
                CBCModernWarfare.resource("block/" + pathAndMaterial + "_medium_barrel"),
                CBCModernWarfare.resource("block/mediumcannon/barrel"));
    }

    public static <T extends Block & MediumcannonBlock, P> NonNullUnaryOperator<BlockBuilder<T, P>> unboredMediumcannonRecoilBarrel(
            String pathAndMaterial) {
        return unboredMediumcannonBlock(
                CBCModernWarfare.resource("block/" + pathAndMaterial + "_medium_recoil_barrel"),
                CBCModernWarfare.resource("block/mediumcannon/recoil_barrel"));
    }

    public static <T extends Block & MediumcannonBlock, P> NonNullUnaryOperator<BlockBuilder<T, P>> unboredMediumcannonBreech(
            String pathAndMaterial) {
        return unboredMediumcannonBlock(
                CBCModernWarfare.resource("block/" + pathAndMaterial + "_medium_breech"),
                CBCModernWarfare.resource("block/mediumcannon/breech"));
    }

    private static <T extends Block & MediumcannonBlock, P> NonNullUnaryOperator<BlockBuilder<T, P>> unboredMediumcannonBlock(
            ResourceLocation tex, ResourceLocation model) {
        return b -> b
                .properties(p -> p.noOcclusion())
                .blockstate((c, p) -> BlockStateGen.directionalBlockIgnoresWaterlogged(c, p,
                        state -> p.models().withExistingParent(c.getName(), model).texture("material", tex)))
                .tag(AllBlockTags.SAFE_NBT.tag)
                .item(MediumcannonBlockItem::new)
                .model((c, p) -> p.blockItem(c))
                .build();
    }

    // ------------------------------------------------------------------------------------
    // R4a：落闩物品（方块物品的模型）
    // ------------------------------------------------------------------------------------

    /** 落闩物品：共用 {@code block/mediumcannon/falling_breech} 模型，只换 {@code material} 贴图。 */
    public static <T extends Item, P> NonNullUnaryOperator<ItemBuilder<T, P>> mediumcannonBreechBlock(String pathAndMaterial) {
        return b -> b.model((c, p) -> p.getBuilder(c.getName())
                .parent(p.getExistingFile(CBCModernWarfare.resource("block/mediumcannon/falling_breech")))
                .texture("material", CBCModernWarfare.resource("block/" + pathAndMaterial + "_breech")));
    }

    // ------------------------------------------------------------------------------------
    // R4b-2：转管炮
    // ------------------------------------------------------------------------------------

    /**
     * 转管炮炮管。
     *
     * <p>{@code ASSEMBLED} 为 true 时只输出粒子贴图（模型由 visual 画），
     * 否则按 {@code BARREL_END} 选普通 / 法兰模型。
     */
    public static <T extends Block & RotarycannonBlock, P> NonNullUnaryOperator<BlockBuilder<T, P>> rotarycannonBarrel(
            String pathAndMaterial) {
        ResourceLocation texLoc = CBCModernWarfare.resource("block/" + pathAndMaterial + "_rotarycannon_barrel");
        return b -> b
                .properties(p -> p.noOcclusion())
                .blockstate((c, p) -> BlockStateGen.directionalBlockIgnoresWaterlogged(c, p, s -> {
                    boolean assembled = s.getValue(RotarycannonBarrelBlock.ASSEMBLED);
                    String name = c.getName() + "_"
                            + (assembled ? "assembled" : s.getValue(RotarycannonBarrelBlock.BARREL_END).getSerializedName());
                    if (assembled) {
                        return p.models().getBuilder(name).texture("particle", texLoc);
                    }
                    ResourceLocation loc = switch (s.getValue(RotarycannonBarrelBlock.BARREL_END)) {
                        case FLANGED -> CBCModernWarfare.resource("block/rotarycannon/barrel_flanged");
                        default -> CBCModernWarfare.resource("block/rotarycannon/barrel");
                    };
                    return p.models().withExistingParent(name, loc).texture("material", texLoc);
                }))
                .tag(AllBlockTags.SAFE_NBT.tag)
                .item(RotarycannonBlockItem::new)
                .model((c, p) -> p.withExistingParent(c.getName(),
                        CBCModernWarfare.resource("block/rotarycannon/barrel")).texture("material", texLoc))
                .build();
    }

    /**
     * 转管炮炮闩。
     *
     * <p>注意：完整品（{@code complete=true}）也只有一个模型 —— 与中型炮不同，
     * 转管炮炮闩的方块没有 {@code AXIS} / {@code ASSEMBLED} 属性，所以不需要分支。
     */
    public static <T extends Block & RotarycannonBlock, P> NonNullUnaryOperator<BlockBuilder<T, P>> rotarycannonBreech(
            String pathAndMaterial, boolean complete) {
        ResourceLocation texLoc = CBCModernWarfare.resource("block/" + pathAndMaterial + "_rotarycannon_breech");
        ResourceLocation baseLoc = CBCModernWarfare.resource("block/rotarycannon/breech");
        NonNullUnaryOperator<BlockBuilder<T, P>> result = b -> b
                .properties(p -> p.noOcclusion())
                .blockstate((c, p) -> BlockStateGen.directionalBlockIgnoresWaterlogged(c, p,
                        s -> p.models().withExistingParent(c.getName(), baseLoc).texture("material", texLoc)))
                .tag(AllBlockTags.SAFE_NBT.tag);
        if (complete) {
            result = result.andThen(b -> b.item(RotarycannonBlockItem::new)
                    .model((c, p) -> p.withExistingParent(c.getName(),
                            CBCModernWarfare.resource("block/rotarycannon/breech_item")).texture("material", texLoc))
                    .build());
        } else {
            result = result.andThen(b -> b.item(RotarycannonBlockItem::new)
                    .model((c, p) -> p.blockItem(c))
                    .build());
        }
        return result;
    }

    /** 转管炮轴承。 */
    public static <T extends Block & RotarycannonBlock, P> NonNullUnaryOperator<BlockBuilder<T, P>> rotarycannonBearing(
            String pathAndMaterial, boolean complete) {
        ResourceLocation texLoc = CBCModernWarfare.resource("block/" + pathAndMaterial + "_rotarycannon_bearing");
        ResourceLocation baseLoc = CBCModernWarfare.resource("block/rotarycannon/bearing");
        NonNullUnaryOperator<BlockBuilder<T, P>> result = b -> b
                .properties(p -> p.noOcclusion())
                .blockstate((c, p) -> BlockStateGen.directionalBlockIgnoresWaterlogged(c, p,
                        s -> p.models().withExistingParent(c.getName(), baseLoc).texture("material", texLoc)))
                .tag(AllBlockTags.SAFE_NBT.tag);
        if (complete) {
            result = result.andThen(b -> b.item(RotarycannonBlockItem::new)
                    .model((c, p) -> p.withExistingParent(c.getName(), baseLoc).texture("material", texLoc))
                    .build());
        } else {
            result = result.andThen(b -> b.item(RotarycannonBlockItem::new)
                    .model((c, p) -> p.blockItem(c))
                    .build());
        }
        return result;
    }

    // TODO(R5-R9): cannonBarrel / builtUpCannonBarrel / cannonChamber / munitonsLauncherBlock /
    //   slidingBreech* / torpedoScrewBreech / projectile* / fuzedProjectileItem /
    //   mediumRound / mediumCartridge / compactMount / apdsShot / autocannonAmmoDrum /
    //   liquidBucket / slabBlock / stairsBlock / shell / warhead。
    // 已迁：R2 的 2 个 + R4a 的 9 个 + R4b-2 的 3 个 = 14 / 37。
    // TODO(old): rotarycannonBarrel / rotarycannonBreech / rotarycannonBearing /
    //   slidingBreech / slidingBreechIncomplete / slidingBreechUnbored /
    //   torpedoScrewBreech / munitonsLauncherBlock / cannonBarrel / builtUpCannonBarrel /
    //   cannonChamber / projectile(2) / projectileLegacy / fuzedProjectileItem /
    //   mediumRound / mediumCartridge / compactMount / apdsShot / autocannonAmmoDrum /
    //   liquidBucket / slabBlock / stairsBlock / shell / warhead。
    //   共 37 个方法，R4a 落了 9 个（含 R2 的 2 个）。
}
