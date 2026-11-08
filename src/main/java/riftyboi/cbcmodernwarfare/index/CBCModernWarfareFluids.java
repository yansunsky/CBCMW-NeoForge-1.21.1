package riftyboi.cbcmodernwarfare.index;

import com.tterrag.registrate.util.entry.FluidEntry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

import rbasamoyai.createbigcannons.CBCTags.CBCFluidTags;
import rbasamoyai.createbigcannons.crafting.foundry.MoltenMetalLiquidBlock;
import rbasamoyai.createbigcannons.utils.CBCRegistryUtils;
import rbasamoyai.createbigcannons.utils.CBCUtils;

import riftyboi.cbcmodernwarfare.CBCModernWarfare;

import static riftyboi.cbcmodernwarfare.CBCModernWarfare.REGISTRATE;

/**
 * Fluids of this mod.
 *
 * <p><b>1.20.1 → 1.21.1: this is the single biggest API change found so far.</b>
 *
 * <p>1.20.1 used CBC's own fluid stack:
 * {@code rbasamoyai.createbigcannons.index.fluid_utils.FluidBuilder} +
 * {@code CBCFlowingFluid} + {@code IndexPlatform.doFluidBuilderTransforms}. In CBC 5.11.7
 * <b>the entire {@code fluid_utils} package is gone</b> ({@code jar tf} → zero matches).
 * CBC's own {@code CBCFluids} now builds its molten metals on
 * <b>Registrate's {@code FluidBuilder} + NeoForge's {@code BaseFlowingFluid}</b> — confirmed by
 * decompiling {@code CBCFluids}' static initialiser:
 *
 * <pre>
 * ldc "molten_cast_iron"
 * invokestatic CBCFluids$SolidRenderedPlaceableFluidType.create(int, Supplier)
 * invokevirtual CreateRegistrate.standardFluid(String, FluidBuilder$FluidTypeFactory)
 * invokevirtual com.tterrag.registrate.builders.FluidBuilder.lang / tag / properties / fluidProperties
 * </pre>
 *
 * <p>Two {@code BaseFlowingFluid.Properties} renames come with it:
 * <ul>
 *   <li>{@code flowSpeed(int)} → {@code slopeFindDistance(int)}</li>
 *   <li>{@code blastResistance(float)} → {@code explosionResistance(float)}</li>
 * </ul>
 *
 * <p><b>Not done here:</b> CBC 5.11.7 gives its own molten metals a custom
 * {@code SolidRenderedPlaceableFluidType} (so they render opaque/solid in the world). We use
 * Registrate's default fluid type instead, which is a purely cosmetic difference.
 * TODO(R11): port a matching fluid type once the fluid textures exist in this tree.
 */
public class CBCModernWarfareFluids {

    public static final FluidEntry<BaseFlowingFluid.Flowing> MOLTEN_CARBON_STEEL =
            standardFluid("molten_carbon_steel")
                    .lang("Molten Carbon Steel")
                    // NOTE(1.21.1): 1.20.1 emitted BOTH a `forge:` and a `c:` tag so either loader's
                    // convention would match. On NeoForge 1.21.1 the `forge:` namespace is the
                    // legacy one — running the server prints
                    //   "Dev warning - Legacy Tags detected. Please migrate your 'forge' namespace
                    //    tags to the 'c' namespace!"
                    // so only the `c:` tag is emitted here. (Plan §4.3: data/forge/ -> data/c/.)
                    .tag(commonTag("molten_carbon_steel"))
                    .tag(CBCFluidTags.MOLTEN_METAL)
                    .fluidProperties(p -> p.levelDecreasePerBlock(2)
                            .tickRate(25)
                            // NOTE(1.21.1): 1.20.1 called this flowSpeed(3).
                            .slopeFindDistance(3)
                            // NOTE(1.21.1): 1.20.1 called this blastResistance(100f).
                            .explosionResistance(100.0F))
                    .block(MoltenMetalLiquidBlock::new).build()
                    .register();

    public static void register() {
        // Registration happens in the static initialiser — see CBCModernWarfareBlocks.
    }

    private static com.tterrag.registrate.builders.FluidBuilder<BaseFlowingFluid.Flowing, com.simibubi.create.foundation.data.CreateRegistrate> standardFluid(String name) {
        return CBCModernWarfare.REGISTRATE.standardFluid(name);
    }

    /** {@code c:} namespaced common tag (the 1.21 convention). */
    private static TagKey<Fluid> commonTag(String path) {
        return TagKey.create(CBCRegistryUtils.getFluidRegistryKey(), CBCUtils.location("c", path));
    }

    private CBCModernWarfareFluids() {}

    /** Unused, but keeps the {@code REGISTRATE} import meaningful for readers. */
    static ResourceLocation rl(String path) {
        return CBCModernWarfare.resource(path);
    }
}
