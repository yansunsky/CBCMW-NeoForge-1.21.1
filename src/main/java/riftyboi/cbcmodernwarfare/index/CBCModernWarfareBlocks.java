package riftyboi.cbcmodernwarfare.index;

import java.util.function.Supplier;

import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;

import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import rbasamoyai.createbigcannons.CBCTags;
import rbasamoyai.createbigcannons.crafting.casting.CannonCastMouldBlock;
import rbasamoyai.createbigcannons.crafting.casting.CannonCastShape;
import rbasamoyai.createbigcannons.datagen.assets.CBCBuilderTransformers;

import riftyboi.cbcmodernwarfare.cannons.medium_cannon.MediumcannonBarrelBlock;
import riftyboi.cbcmodernwarfare.cannons.medium_cannon.breech.MediumcannonBreechBlock;
import riftyboi.cbcmodernwarfare.cannons.medium_cannon.recoil_barrel.MediumcannonRecoilBarrelBlock;
import riftyboi.cbcmodernwarfare.content.reactive.ERABlock;
import riftyboi.cbcmodernwarfare.content.reactive.ERASlabBlock;
import riftyboi.cbcmodernwarfare.content.reactive.ERAStairBlock;
import riftyboi.cbcmodernwarfare.crafting.casting.CBCModernWarfareCastShapes;
import riftyboi.cbcmodernwarfare.crafting.incomplete.IncompleteMediumcannonBlock;
import riftyboi.cbcmodernwarfare.datagen.assets.CBCModernWarfareBuilderTransformers;

import static riftyboi.cbcmodernwarfare.CBCModernWarfare.REGISTRATE;

/**
 * Block registry of CBC Modern Warfare.
 *
 * <p><b>R2 status — skeleton plus the vertical slice.</b> 1.20.1 registered 105
 * {@code BlockEntry} values in this one file (61 KB). R2 registers the reactive-armour family
 * only — the shortest chain that still crosses every layer (block class → builder transform →
 * blockstate/model datagen → loot → lang → creative tab). The other ~99 entries are added in
 * R3/R4/R5 as their classes get ported; the ordering comments below mark where they go.
 *
 * <p><b>Why the class is statically initialised, not "called".</b> Registrate registers entries
 * as their {@code BlockEntry} fields are initialised. {@link #register()} therefore only exists
 * to make the class load at a deliberate point in {@code CBCModernWarfare#init()} — touching the
 * class is what registers the blocks, and doing it from {@code init()} keeps 1.20.1's ordering
 * contract (creative tab first, then content).
 */
public class CBCModernWarfareBlocks {

    // ------------------------------------------------------------------------------------
    // Reactive armour family (R2 — the vertical slice)
    // ------------------------------------------------------------------------------------
    //
    // Properties come straight from the 1.20.1 registration:
    //   era_block  : initialProperties(NETHERITE_BLOCK), requiresCorrectToolForDrops,
    //                strength(15,12), sound(NETHERITE_BLOCK)
    //   era_stairs : same but strength(15,12), vanilla StairBlock with BRICK_STAIRS base state
    //   era_slab   : strength(10,12)
    // NOTE(1.21.1): `BlockBehaviour.Properties#requiresCorrectToolForDrops()` kept its name;
    // `instabreak()` and `strength(float,float)` also did. No renames were needed here.

    public static final BlockEntry<ERABlock> ERA_BLOCK = REGISTRATE
            .block("era_block", ERABlock::new)
            .initialProperties(() -> Blocks.NETHERITE_BLOCK)
            .properties(BlockBehaviour.Properties::requiresCorrectToolForDrops)
            .properties(p -> p.strength(15.0F, 12.0F))
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .simpleItem()
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .transform(CBCModernWarfareBuilderTransformers.reactiveArmorBlock(""))
            .tag(BlockTags.NEEDS_STONE_TOOL)
            .register();

    public static final BlockEntry<ERAStairBlock> ERA_STAIRS = REGISTRATE
            .block("era_stairs", p -> new ERAStairBlock(Blocks.BRICK_STAIRS.defaultBlockState(), p))
            .initialProperties(() -> Blocks.NETHERITE_BLOCK)
            .properties(p -> p.strength(15.0F, 12.0F))
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .properties(BlockBehaviour.Properties::requiresCorrectToolForDrops)
            .item()
            .model((ctx, prov) -> prov.withExistingParent(ctx.getName(),
                    riftyboi.cbcmodernwarfare.CBCModernWarfare.resource("block/" + ctx.getName() + "_stairs")))
            .tag(net.minecraft.tags.ItemTags.STAIRS).build()
            .tag(BlockTags.STAIRS)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .tag(BlockTags.NEEDS_STONE_TOOL)
            .blockstate((ctx, prov) -> prov.stairsBlock(ctx.getEntry(), ctx.getName(),
                    prov.modLoc("block/tiled_reactive_armor"),
                    prov.modLoc("block/tiled_reactive_armor_bottom"),
                    prov.modLoc("block/tiled_reactive_armor")))
            .register();

    public static final BlockEntry<ERASlabBlock> ERA_SLAB = REGISTRATE
            .block("era_slab", ERASlabBlock::new)
            .initialProperties(() -> Blocks.NETHERITE_BLOCK)
            .properties(p -> p.strength(10.0F, 12.0F))
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .properties(BlockBehaviour.Properties::requiresCorrectToolForDrops)
            .item().tag(net.minecraft.tags.ItemTags.SLABS).build()
            .tag(BlockTags.SLABS)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .tag(BlockTags.NEEDS_STONE_TOOL)
            .blockstate((ctx, prov) -> prov.slabBlock(ctx.getEntry(),
                    prov.modLoc("block/tiled_reactive_slab"),
                    prov.modLoc("block/tiled_reactive_armor")))
            .register();

    // Tan reactive armour (R2 — same helper, different texture suffix)
    public static final BlockEntry<ERABlock> TAN_ERA_BLOCK = REGISTRATE
            .block("tan_era_block", ERABlock::new)
            .initialProperties(() -> Blocks.NETHERITE_BLOCK)
            .properties(p -> p.strength(10.0F, 12.0F))
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .properties(BlockBehaviour.Properties::requiresCorrectToolForDrops)
            .simpleItem()
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .tag(BlockTags.NEEDS_STONE_TOOL)
            .transform(CBCModernWarfareBuilderTransformers.reactiveArmorBlock("_tan"))
            .register();

    public static final BlockEntry<ERAStairBlock> TAN_ERA_STAIRS = REGISTRATE
            .block("tan_era_stairs", p -> new ERAStairBlock(Blocks.BRICK_STAIRS.defaultBlockState(), p))
            .initialProperties(() -> Blocks.NETHERITE_BLOCK)
            .properties(p -> p.strength(10.0F, 12.0F))
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .properties(BlockBehaviour.Properties::requiresCorrectToolForDrops)
            .item()
            .model((ctx, prov) -> prov.withExistingParent(ctx.getName(),
                    riftyboi.cbcmodernwarfare.CBCModernWarfare.resource("block/" + ctx.getName() + "_stairs")))
            .tag(net.minecraft.tags.ItemTags.STAIRS).build()
            .tag(BlockTags.STAIRS)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .tag(BlockTags.NEEDS_STONE_TOOL)
            .blockstate((ctx, prov) -> prov.stairsBlock(ctx.getEntry(), ctx.getName(),
                    prov.modLoc("block/tiled_tan_reactive_armor"),
                    prov.modLoc("block/tiled_tan_reactive_armor_bottom"),
                    prov.modLoc("block/tiled_tan_reactive_armor")))
            .register();

    public static final BlockEntry<ERASlabBlock> TAN_ERA_SLAB = REGISTRATE
            .block("tan_era_slab", ERASlabBlock::new)
            .initialProperties(() -> Blocks.NETHERITE_BLOCK)
            .properties(p -> p.strength(10.0F, 12.0F))
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .properties(BlockBehaviour.Properties::requiresCorrectToolForDrops)
            .item().tag(net.minecraft.tags.ItemTags.SLABS).build()
            .tag(BlockTags.SLABS)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .tag(BlockTags.NEEDS_STONE_TOOL)
            .blockstate((ctx, prov) -> prov.slabBlock(ctx.getEntry(),
                    prov.modLoc("block/tiled_tan_reactive_slab"),
                    prov.modLoc("block/tiled_tan_reactive_armor")))
            .register();

    // Green reactive armour
    public static final BlockEntry<ERABlock> GREEN_ERA_BLOCK = REGISTRATE
            .block("green_era_block", ERABlock::new)
            .initialProperties(() -> Blocks.NETHERITE_BLOCK)
            .properties(p -> p.strength(10.0F, 12.0F))
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .properties(BlockBehaviour.Properties::requiresCorrectToolForDrops)
            .simpleItem()
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .tag(BlockTags.NEEDS_STONE_TOOL)
            .transform(CBCModernWarfareBuilderTransformers.reactiveArmorBlock("_green"))
            .register();

    public static final BlockEntry<ERAStairBlock> GREEN_ERA_STAIRS = REGISTRATE
            .block("green_era_stairs", p -> new ERAStairBlock(Blocks.BRICK_STAIRS.defaultBlockState(), p))
            .initialProperties(() -> Blocks.NETHERITE_BLOCK)
            .properties(p -> p.strength(10.0F, 12.0F))
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .properties(BlockBehaviour.Properties::requiresCorrectToolForDrops)
            .item()
            .model((ctx, prov) -> prov.withExistingParent(ctx.getName(),
                    riftyboi.cbcmodernwarfare.CBCModernWarfare.resource("block/" + ctx.getName() + "_stairs")))
            .tag(net.minecraft.tags.ItemTags.STAIRS).build()
            .tag(BlockTags.STAIRS)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .tag(BlockTags.NEEDS_STONE_TOOL)
            .blockstate((ctx, prov) -> prov.stairsBlock(ctx.getEntry(), ctx.getName(),
                    prov.modLoc("block/tiled_green_reactive_armor"),
                    prov.modLoc("block/tiled_green_reactive_armor_bottom"),
                    prov.modLoc("block/tiled_green_reactive_armor")))
            .register();

    public static final BlockEntry<ERASlabBlock> GREEN_ERA_SLAB = REGISTRATE
            .block("green_era_slab", ERASlabBlock::new)
            .initialProperties(() -> Blocks.NETHERITE_BLOCK)
            .properties(p -> p.strength(10.0F, 12.0F))
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .properties(BlockBehaviour.Properties::requiresCorrectToolForDrops)
            .item().tag(net.minecraft.tags.ItemTags.SLABS).build()
            .tag(BlockTags.SLABS)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .tag(BlockTags.NEEDS_STONE_TOOL)
            .blockstate((ctx, prov) -> prov.slabBlock(ctx.getEntry(),
                    prov.modLoc("block/tiled_green_reactive_slab"),
                    prov.modLoc("block/tiled_green_reactive_armor")))
            .register();

    // ------------------------------------------------------------------------------------
    // Composite armour (R2 — second case, plain Block, proves the helper generalises)
    // ------------------------------------------------------------------------------------
    public static final BlockEntry<Block> COMPOSITE_BLOCK = REGISTRATE
            .block("composite_block", Block::new)
            .initialProperties(() -> Blocks.NETHERITE_BLOCK)
            .properties(BlockBehaviour.Properties::requiresCorrectToolForDrops)
            .properties(p -> p.strength(20.0F, 600.0F))
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .tag(BlockTags.NEEDS_STONE_TOOL)
            .lang("Composite Armor Block")
            .transform(CBCModernWarfareBuilderTransformers.compositeBlock())
            .item()
            .build()
            .register();

    // ------------------------------------------------------------------------------------
    // Carbon steel family (R2 — plain Block/Slab/Stair with vanilla datagen helpers)
    // ------------------------------------------------------------------------------------
    public static final BlockEntry<Block> CARBON_STEEL_BLOCK = REGISTRATE
            .block("carbon_steel_block", Block::new)
            .initialProperties(() -> Blocks.NETHERITE_BLOCK)
            .properties(BlockBehaviour.Properties::requiresCorrectToolForDrops)
            .properties(p -> p.strength(8.0F, 24.0F))
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .simpleItem()
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .tag(BlockTags.NEEDS_STONE_TOOL)
            .register();

    public static final BlockEntry<net.minecraft.world.level.block.StairBlock> CARBON_STEEL_STAIRS = REGISTRATE
            .block("carbon_steel_stairs", p -> new net.minecraft.world.level.block.StairBlock(
                    Blocks.BRICK_STAIRS.defaultBlockState(), p))
            .initialProperties(() -> Blocks.NETHERITE_BLOCK)
            .properties(BlockBehaviour.Properties::requiresCorrectToolForDrops)
            .properties(p -> p.strength(8.0F, 22.0F))
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .item()
            .model((ctx, prov) -> prov.withExistingParent(ctx.getName(),
                    prov.modLoc("block/carbon_steel_stairs")))
            .tag(net.minecraft.tags.ItemTags.STAIRS).build()
            .tag(BlockTags.STAIRS)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .tag(BlockTags.NEEDS_STONE_TOOL)
            .blockstate((ctx, prov) -> prov.stairsBlock(ctx.getEntry(),
                    prov.modLoc("block/carbon_steel_block")))
            .register();

    public static final BlockEntry<net.minecraft.world.level.block.SlabBlock> CARBON_STEEL_SLAB = REGISTRATE
            .block("carbon_steel_slab", net.minecraft.world.level.block.SlabBlock::new)
            .initialProperties(() -> Blocks.NETHERITE_BLOCK)
            .properties(p -> p.strength(8.0F, 22.0F))
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .properties(BlockBehaviour.Properties::requiresCorrectToolForDrops)
            .item().tag(net.minecraft.tags.ItemTags.SLABS).build()
            .tag(BlockTags.SLABS)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .tag(BlockTags.NEEDS_STONE_TOOL)
            .blockstate((ctx, prov) -> prov.slabBlock(ctx.getEntry(),
                    prov.modLoc("block/carbon_steel_block"),
                    prov.modLoc("block/carbon_steel_block")))
            .register();

    // ====================================================================================
    // R4a — 中型炮（medium cannon）
    // ====================================================================================
    //
    // 1.20.1 的中型炮是「4 材质 × 6 部件 = 24 个方块」：
    //   barrel / recoil_barrel / breech 三个成品
    //   incomplete_breech / incomplete_recoil_barrel 两个未完成品
    //   unbored_barrel / unbored_breech / unbored_recoil_barrel 三个未开孔件
    // 本轮（R4a）先落 16 个：4 材质 × (barrel + recoil_barrel + breech + 2 个 incomplete)。
    // unbored 系列需要 UnboredMediumcannonBlock（依赖 crafting/boring，归 R9），暂缓。
    //
    // 公共属性来自 1.20.1 的 cannonBlock() helper：
    //   strength(5,6) + sound(NETHERITE_BLOCK) + requiresCorrectToolForDrops
    //   + tag(MINEABLE_WITH_PICKAXE) + tag(NEEDS_IRON_TOOL)
    //   barrel 额外带 DRILL_CAN_PASS_THROUGH（可被加农钻头穿过）。

    // ---------------------------------------------------------------- Cast iron

    public static final BlockEntry<MediumcannonBarrelBlock> CAST_IRON_MEDIUMCANNON_BARREL = REGISTRATE
            .block("cast_iron_mediumcannon_barrel",
                    p -> new MediumcannonBarrelBlock(p, CBCModernWarfareMediumcannonMaterials.CAST_IRON))
            .transform(cannonBlock())
            .loot(CBCBuilderTransformers.castIronScrapLoot(2))
            .transform(CBCModernWarfareBuilderTransformers.mediumcannonBarrel("mediumcannon/cast_iron"))
            .register();

    public static final BlockEntry<MediumcannonRecoilBarrelBlock> CAST_IRON_MEDIUMCANNON_RECOIL_BARREL = REGISTRATE
            .block("cast_iron_mediumcannon_recoil_barrel",
                    p -> new MediumcannonRecoilBarrelBlock(p, CBCModernWarfareMediumcannonMaterials.CAST_IRON,
                            CBCModernWarfareBlocks::castIronMediumcannonBarrel))
            .transform(cannonBlock(false))
            .loot(CBCBuilderTransformers.castIronScrapLoot(3))
            .transform(CBCModernWarfareBuilderTransformers.mediumcannonRecoilBarrel("mediumcannon/cast_iron", true))
            .register();

    public static final BlockEntry<MediumcannonBreechBlock> CAST_IRON_MEDIUMCANNON_BREECH = REGISTRATE
            .block("cast_iron_mediumcannon_breech",
                    p -> new MediumcannonBreechBlock(p, CBCModernWarfareMediumcannonMaterials.CAST_IRON))
            .transform(cannonBlock(false))
            .loot(CBCBuilderTransformers.castIronScrapLoot(4))
            .transform(CBCModernWarfareBuilderTransformers.mediumcannonBreech("mediumcannon/cast_iron", true))
            .register();

    public static final BlockEntry<IncompleteMediumcannonBlock> INCOMPLETE_CAST_IRON_MEDIUMCANNON_BREECH = REGISTRATE
            .block("incomplete_cast_iron_mediumcannon_breech", p -> IncompleteMediumcannonBlock.breech(p,
                    CBCModernWarfareMediumcannonMaterials.CAST_IRON,
                    CAST_IRON_MEDIUMCANNON_BREECH,
                    CBCModernWarfareItem.CAST_IRON_MEDIUMCANNON_FALLING_BREECH))
            .transform(cannonBlock())
            .loot(CBCBuilderTransformers.castIronScrapLoot(4))
            .transform(CBCModernWarfareBuilderTransformers.incompleteCannonBreech("mediumcannon/cast_iron", false))
            .register();

    public static final BlockEntry<IncompleteMediumcannonBlock> INCOMPLETE_CAST_IRON_MEDIUMCANNON_RECOIL_BARREL = REGISTRATE
            .block("incomplete_cast_iron_mediumcannon_recoil_barrel",
                    p -> IncompleteMediumcannonBlock.recoilBarrel(p,
                            CBCModernWarfareMediumcannonMaterials.CAST_IRON,
                            CAST_IRON_MEDIUMCANNON_RECOIL_BARREL,
                            CBCModernWarfareItem.HEAVY_RECOIL_SPRING))
            .transform(cannonBlock())
            .loot(CBCBuilderTransformers.castIronScrapLoot(3))
            .transform(CBCModernWarfareBuilderTransformers.mediumcannonRecoilBarrel("mediumcannon/cast_iron", false))
            .register();

    // ---------------------------------------------------------------- Bronze

    public static final BlockEntry<MediumcannonBarrelBlock> BRONZE_MEDIUMCANNON_BARREL = REGISTRATE
            .block("bronze_mediumcannon_barrel",
                    p -> new MediumcannonBarrelBlock(p, CBCModernWarfareMediumcannonMaterials.BRONZE))
            .transform(cannonBlock())
            .loot(CBCBuilderTransformers.bronzeScrapLoot(2))
            .transform(CBCModernWarfareBuilderTransformers.mediumcannonBarrel("mediumcannon/bronze"))
            .register();

    public static final BlockEntry<MediumcannonRecoilBarrelBlock> BRONZE_MEDIUMCANNON_RECOIL_BARREL = REGISTRATE
            .block("bronze_mediumcannon_recoil_barrel",
                    p -> new MediumcannonRecoilBarrelBlock(p, CBCModernWarfareMediumcannonMaterials.BRONZE,
                            CBCModernWarfareBlocks::bronzeMediumcannonBarrel))
            .transform(cannonBlock(false))
            .loot(CBCBuilderTransformers.bronzeScrapLoot(3))
            .transform(CBCModernWarfareBuilderTransformers.mediumcannonRecoilBarrel("mediumcannon/bronze", true))
            .register();

    public static final BlockEntry<MediumcannonBreechBlock> BRONZE_MEDIUMCANNON_BREECH = REGISTRATE
            .block("bronze_mediumcannon_breech",
                    p -> new MediumcannonBreechBlock(p, CBCModernWarfareMediumcannonMaterials.BRONZE))
            .transform(cannonBlock(false))
            .loot(CBCBuilderTransformers.bronzeScrapLoot(4))
            .transform(CBCModernWarfareBuilderTransformers.mediumcannonBreech("mediumcannon/bronze", true))
            .register();

    public static final BlockEntry<IncompleteMediumcannonBlock> INCOMPLETE_BRONZE_MEDIUMCANNON_BREECH = REGISTRATE
            .block("incomplete_bronze_mediumcannon_breech", p -> IncompleteMediumcannonBlock.breech(p,
                    CBCModernWarfareMediumcannonMaterials.BRONZE,
                    BRONZE_MEDIUMCANNON_BREECH,
                    CBCModernWarfareItem.BRONZE_MEDIUMCANNON_FALLING_BREECH))
            .transform(cannonBlock())
            .loot(CBCBuilderTransformers.bronzeScrapLoot(4))
            .transform(CBCModernWarfareBuilderTransformers.incompleteCannonBreech("mediumcannon/bronze", false))
            .register();

    public static final BlockEntry<IncompleteMediumcannonBlock> INCOMPLETE_BRONZE_MEDIUMCANNON_RECOIL_BARREL = REGISTRATE
            .block("incomplete_bronze_mediumcannon_recoil_barrel",
                    p -> IncompleteMediumcannonBlock.recoilBarrel(p,
                            CBCModernWarfareMediumcannonMaterials.BRONZE,
                            BRONZE_MEDIUMCANNON_RECOIL_BARREL,
                            CBCModernWarfareItem.HEAVY_RECOIL_SPRING))
            .transform(cannonBlock())
            .loot(CBCBuilderTransformers.bronzeScrapLoot(3))
            .transform(CBCModernWarfareBuilderTransformers.mediumcannonRecoilBarrel("mediumcannon/bronze", false))
            .register();

    // ---------------------------------------------------------------- Steel

    public static final BlockEntry<MediumcannonBarrelBlock> STEEL_MEDIUMCANNON_BARREL = REGISTRATE
            .block("steel_mediumcannon_barrel",
                    p -> new MediumcannonBarrelBlock(p, CBCModernWarfareMediumcannonMaterials.STEEL))
            .transform(cannonBlock())
            .loot(CBCBuilderTransformers.steelScrapLoot(2))
            .transform(CBCModernWarfareBuilderTransformers.mediumcannonBarrel("mediumcannon/steel"))
            .register();

    public static final BlockEntry<MediumcannonRecoilBarrelBlock> STEEL_MEDIUMCANNON_RECOIL_BARREL = REGISTRATE
            .block("steel_mediumcannon_recoil_barrel",
                    p -> new MediumcannonRecoilBarrelBlock(p, CBCModernWarfareMediumcannonMaterials.STEEL,
                            CBCModernWarfareBlocks::steelMediumcannonBarrel))
            .transform(cannonBlock(false))
            .loot(CBCBuilderTransformers.steelScrapLoot(3))
            .transform(CBCModernWarfareBuilderTransformers.mediumcannonRecoilBarrel("mediumcannon/steel", true))
            .register();

    public static final BlockEntry<MediumcannonBreechBlock> STEEL_MEDIUMCANNON_BREECH = REGISTRATE
            .block("steel_mediumcannon_breech",
                    p -> new MediumcannonBreechBlock(p, CBCModernWarfareMediumcannonMaterials.STEEL))
            .transform(cannonBlock(false))
            .loot(CBCBuilderTransformers.steelScrapLoot(4))
            .transform(CBCModernWarfareBuilderTransformers.mediumcannonBreech("mediumcannon/steel", true))
            .register();

    public static final BlockEntry<IncompleteMediumcannonBlock> INCOMPLETE_STEEL_MEDIUMCANNON_BREECH = REGISTRATE
            .block("incomplete_steel_mediumcannon_breech", p -> IncompleteMediumcannonBlock.breech(p,
                    CBCModernWarfareMediumcannonMaterials.STEEL,
                    STEEL_MEDIUMCANNON_BREECH,
                    CBCModernWarfareItem.STEEL_MEDIUMCANNON_FALLING_BREECH))
            .transform(cannonBlock())
            .loot(CBCBuilderTransformers.steelScrapLoot(4))
            .transform(CBCModernWarfareBuilderTransformers.incompleteCannonBreech("mediumcannon/steel", false))
            .register();

    public static final BlockEntry<IncompleteMediumcannonBlock> INCOMPLETE_STEEL_MEDIUMCANNON_RECOIL_BARREL = REGISTRATE
            .block("incomplete_steel_mediumcannon_recoil_barrel",
                    p -> IncompleteMediumcannonBlock.recoilBarrel(p,
                            CBCModernWarfareMediumcannonMaterials.STEEL,
                            STEEL_MEDIUMCANNON_RECOIL_BARREL,
                            CBCModernWarfareItem.HEAVY_RECOIL_SPRING))
            .transform(cannonBlock())
            .loot(CBCBuilderTransformers.steelScrapLoot(3))
            .transform(CBCModernWarfareBuilderTransformers.mediumcannonRecoilBarrel("mediumcannon/steel", false))
            .register();

    // ---------------------------------------------------------------- Nethersteel

    public static final BlockEntry<MediumcannonBarrelBlock> NETHERSTEEL_MEDIUMCANNON_BARREL = REGISTRATE
            .block("nethersteel_mediumcannon_barrel",
                    p -> new MediumcannonBarrelBlock(p, CBCModernWarfareMediumcannonMaterials.NETHERSTEEL))
            .transform(cannonBlock())
            .loot(CBCBuilderTransformers.nethersteelScrapLoot(2))
            .transform(CBCModernWarfareBuilderTransformers.mediumcannonBarrel("mediumcannon/nethersteel"))
            .register();

    public static final BlockEntry<MediumcannonRecoilBarrelBlock> NETHERSTEEL_MEDIUMCANNON_RECOIL_BARREL = REGISTRATE
            .block("nethersteel_mediumcannon_recoil_barrel",
                    p -> new MediumcannonRecoilBarrelBlock(p, CBCModernWarfareMediumcannonMaterials.NETHERSTEEL,
                            CBCModernWarfareBlocks::nethersteelMediumcannonBarrel))
            .transform(cannonBlock(false))
            .loot(CBCBuilderTransformers.nethersteelScrapLoot(3))
            .transform(CBCModernWarfareBuilderTransformers.mediumcannonRecoilBarrel("mediumcannon/nethersteel", true))
            .register();

    public static final BlockEntry<MediumcannonBreechBlock> NETHERSTEEL_MEDIUMCANNON_BREECH = REGISTRATE
            .block("nethersteel_mediumcannon_breech",
                    p -> new MediumcannonBreechBlock(p, CBCModernWarfareMediumcannonMaterials.NETHERSTEEL))
            .transform(cannonBlock(false))
            .loot(CBCBuilderTransformers.nethersteelScrapLoot(4))
            .transform(CBCModernWarfareBuilderTransformers.mediumcannonBreech("mediumcannon/nethersteel", true))
            .register();

    public static final BlockEntry<IncompleteMediumcannonBlock> INCOMPLETE_NETHERSTEEL_MEDIUMCANNON_BREECH = REGISTRATE
            .block("incomplete_nethersteel_mediumcannon_breech", p -> IncompleteMediumcannonBlock.breech(p,
                    CBCModernWarfareMediumcannonMaterials.NETHERSTEEL,
                    NETHERSTEEL_MEDIUMCANNON_BREECH,
                    CBCModernWarfareItem.NETHERSTEEL_MEDIUMCANNON_FALLING_BREECH))
            .transform(cannonBlock())
            .loot(CBCBuilderTransformers.nethersteelScrapLoot(4))
            .transform(CBCModernWarfareBuilderTransformers.incompleteCannonBreech("mediumcannon/nethersteel", false))
            .register();

    public static final BlockEntry<IncompleteMediumcannonBlock> INCOMPLETE_NETHERSTEEL_MEDIUMCANNON_RECOIL_BARREL = REGISTRATE
            .block("incomplete_nethersteel_mediumcannon_recoil_barrel",
                    p -> IncompleteMediumcannonBlock.recoilBarrel(p,
                            CBCModernWarfareMediumcannonMaterials.NETHERSTEEL,
                            NETHERSTEEL_MEDIUMCANNON_RECOIL_BARREL,
                            CBCModernWarfareItem.HEAVY_RECOIL_SPRING))
            .transform(cannonBlock())
            .loot(CBCBuilderTransformers.nethersteelScrapLoot(3))
            .transform(CBCModernWarfareBuilderTransformers.mediumcannonRecoilBarrel("mediumcannon/nethersteel", false))
            .register();

    // ---------------------------------------------------------------- Cast moulds (R4a)

    public static final BlockEntry<CannonCastMouldBlock> MEDIUMCANNON_BREECH_CAST_MOULD = castMould(
            "mediumcannon_breech", Block.box(1, 0, 1, 15, 16, 15),
            () -> CBCModernWarfareCastShapes.MEDIUMCANNON_BREECH);

    public static final BlockEntry<CannonCastMouldBlock> MEDIUMCANNON_RECOIL_BARREL_CAST_MOULD = castMould(
            "mediumcannon_recoil_barrel", Block.box(3, 0, 3, 13, 16, 13),
            () -> CBCModernWarfareCastShapes.MEDIUMCANNON_RECOIL_BARREL);

    public static final BlockEntry<CannonCastMouldBlock> MEDIUMCANNON_BARREL_CAST_MOULD = castMould(
            "mediumcannon_barrel", Block.box(4, 0, 4, 12, 16, 12),
            () -> CBCModernWarfareCastShapes.MEDIUMCANNON_BARREL);

    // ------------------------------------------------------------------------------------
    // TODO(R4b): rotarycannon 全套（barrel/breech/bearing + incomplete + 3 个铸型），
    //   以及 munitions_contraption_launcher（25 文件）与 unbored 系列（需 crafting/boring，R9）。
    // TODO(R5–R9): 剩下的 ~70 个方块（smoke_discharger、sights、guidance、rocket parts、
    //   ammo drum、大炮炮弹等）。每个都需要先有方块类。
    // ------------------------------------------------------------------------------------

    // ====================================================================================
    // 私有辅助
    // ====================================================================================

    /**
     * 火炮方块的公共属性（1.20.1 的 {@code cannonBlock()} helper 逐字搬入）。
     *
     * @param canPassThrough 是否允许 CBC 的加农钻头穿过（炮管为 true，炮闩与后坐炮管为 false）
     */
    private static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> cannonBlock(boolean canPassThrough) {
        NonNullUnaryOperator<BlockBuilder<T, P>> transform = b -> b
                .properties(p -> p.strength(5.0f, 6.0f))
                .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
                .properties(BlockBehaviour.Properties::requiresCorrectToolForDrops)
                .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .tag(BlockTags.NEEDS_IRON_TOOL);
        return canPassThrough
                ? transform.andThen(b -> b.tag(CBCTags.CBCBlockTags.DRILL_CAN_PASS_THROUGH))
                : transform;
    }

    private static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> cannonBlock() {
        return cannonBlock(true);
    }

    /** 铸型方块：主体 + 可选沙层。 */
    private static BlockEntry<CannonCastMouldBlock> castMould(String size, VoxelShape shape,
            Supplier<CannonCastShape> cannonShape) {
        return REGISTRATE
                .block(size + "_cast_mould", p -> new CannonCastMouldBlock(p, shape, cannonShape))
                .transform(CBCModernWarfareBuilderTransformers.castMould(size))
                .register();
    }

    // 后坐炮管需要知道「移动时换成哪个方块的哪个朝向」。
    private static BlockState castIronMediumcannonBarrel(Direction facing) {
        return CAST_IRON_MEDIUMCANNON_BARREL.getDefaultState().setValue(MediumcannonBarrelBlock.FACING, facing);
    }

    private static BlockState bronzeMediumcannonBarrel(Direction facing) {
        return BRONZE_MEDIUMCANNON_BARREL.getDefaultState().setValue(MediumcannonBarrelBlock.FACING, facing);
    }

    private static BlockState steelMediumcannonBarrel(Direction facing) {
        return STEEL_MEDIUMCANNON_BARREL.getDefaultState().setValue(MediumcannonBarrelBlock.FACING, facing);
    }

    private static BlockState nethersteelMediumcannonBarrel(Direction facing) {
        return NETHERSTEEL_MEDIUMCANNON_BARREL.getDefaultState().setValue(MediumcannonBarrelBlock.FACING, facing);
    }

    /** Forces this class to initialise, which is what triggers registration. */
    public static void register() {
        // Intentionally empty — see the class Javadoc.
    }

    private CBCModernWarfareBlocks() {}
}
