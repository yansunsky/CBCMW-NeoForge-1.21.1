package riftyboi.cbcmodernwarfare.index;

import com.tterrag.registrate.util.entry.BlockEntry;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import riftyboi.cbcmodernwarfare.content.reactive.ERABlock;
import riftyboi.cbcmodernwarfare.content.reactive.ERASlabBlock;
import riftyboi.cbcmodernwarfare.content.reactive.ERAStairBlock;
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

    // ------------------------------------------------------------------------------------
    // TODO(R3): 105-entry registration is completed here — smoke_discharger, sights,
    //   guidance blocks, rocket parts, ammo drum, all cannon barrels/breeches/bearings,
    //   cast moulds and the big-cannon shells. Each needs its block class first (R4–R9).
    // ------------------------------------------------------------------------------------
    // TODO(R9): the cast-mould helper below belongs with the crafting package.
    //   private static BlockEntry<CannonCastMouldBlock> castMould(...)

    /** Forces this class to initialise, which is what triggers registration. */
    public static void register() {
        // Intentionally empty — see the class Javadoc.
    }

    private CBCModernWarfareBlocks() {}
}
