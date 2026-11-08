package riftyboi.cbcmodernwarfare;

import java.util.function.Supplier;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;

import net.createmod.catnip.lang.FontHelper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.api.distmarker.Dist;

import riftyboi.cbcmodernwarfare.index.CBCModernWarfareBlocks;
import riftyboi.cbcmodernwarfare.multiloader.IndexPlatform;

/**
 * CBC Modern Warfare — NeoForge 1.21.1 entry point.
 *
 * <p>This class is the migration target for the original 1.20.1 Architectury
 * {@code common/} + {@code forge/} modules. The port is done in phases:
 *
 * <ul>
 *   <li><b>Phase 1 (done)</b> — clean MDK skeleton: project scaffolding, mod id,
 *       Gradle build, dependency wiring.</li>
 *   <li><b>Phase 2 (current)</b> — port {@code index/} registries and platform glue so the
 *       mod compiles. See {@code docs/plan/02-迁移计划书.md}.</li>
 *   <li><b>Phase 3</b> — resources and data (assets / data / recipes / tags).</li>
 * </ul>
 *
 * <p><b>1.20.1 vs 1.21.1 entry point.</b> 1.20.1 had a {@code common} class with a bare
 * {@code init()} driven by a separate Forge {@code @Mod} class in {@code forge/}, which also
 * held the platform deferred registers and used {@code DistExecutor.unsafeRunWhenOn} to reach
 * the client class. None of that survives: there is one {@code @Mod} class, it takes the mod
 * event bus straight from the FML constructor (the shape the official 1.21.1 MDK recommends),
 * and client bootstrap lives entirely in
 * {@link riftyboi.cbcmodernwarfare.forge.CBCModernWarfareClientForge}, which this class
 * <b>never references</b> — that is what keeps the dedicated server from class-loading it.
 */
@Mod(CBCModernWarfare.MOD_ID)
public class CBCModernWarfare {

    /** The mod id. Must stay in sync with {@code mod_id} in gradle.properties. */
    public static final String MOD_ID = "cbcmodernwarfare";

    public static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Shared Registrate instance. Every content registration in this mod goes
     * through it so that datagen, lang keys and models stay consistent.
     */
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);

    static {
        // 1.20.1 kept this in a static initialiser of its common class. Same job, same
        // order: every item registered afterwards gets the Create-style description
        // tooltip plus the kinetic stats block.
        REGISTRATE.setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                .andThen(TooltipModifier.mapNull(KineticStats.create(item))));
    }

    /**
     * FML injects the mod event bus and the mod container. In 1.21.1 NeoForge
     * this is the recommended constructor shape (see the official MDK).
     */
    public CBCModernWarfare(IEventBus modEventBus, ModContainer modContainer) {
        REGISTRATE.registerEventListeners(modEventBus);

        // Platform-layer deferred registries (recipe serializers / recipe types).
        IndexPlatform.register(modEventBus);

        // Must run *before* any content builder is declared, so that Registrate routes
        // those entries into this mod's tab. 1.20.1 did the equivalent through
        // ModGroupImpl.useModTab() after the Forge deferred register was attached.
        ModGroup.useModTab();

        init();

        // Client bootstrap. 1.20.1 reached this through DistExecutor.unsafeRunWhenOn; on
        // NeoForge 1.21.1 a plain dist check is enough, and the method reference below is
        // only resolved inside the branch, so the dedicated server never loads the client
        // classes behind it.
        if (FMLEnvironment.dist == Dist.CLIENT) {
            riftyboi.cbcmodernwarfare.forge.CBCModernWarfareClientModEvents.register(modEventBus);
        }

        LOGGER.info("[{}] initialized on NeoForge 1.21.1", MOD_ID);
    }

    /**
     * Registers every content index of this mod.
     *
     * <p>The order below mirrors the original 1.20.1 {@code CBCModernWarfare.init()} and must
     * be preserved as the entries are ported. Anything not marked done is still commented out
     * because the class it names has not been ported yet — the round that ports the package
     * uncomments its line:
     *
     * <pre>
     * R3    CBCModernWarfareSoundEvents.prepare();
     * R3    CBCModernWarfareMunitionPropertiesHandlers.init();
     * R1    ModGroup.register();                          // done
     * R2    CBCModernWarfareBlocks.register();            // done (vertical slice only)
     * R3    CBCModernWarfareItem.register();
     * R3    CBCModernWarfareBlockEntities.register();
     * R3    CBCModernWarfareFluids.register();
     * R3    CBCModernWarfareMenuTypes.register();
     * R3    CBCModernWarfareEntityTypes.register();
     * R3    CBCModernWarfareRecipeTypes.register();
     * R3    CBCMWCannonContraptionTypes.register();
     * R3    CBCModernWarfareChecks.register();
     * </pre>
     */
    public static void init() {
        ModGroup.register();
        CBCModernWarfareBlocks.register();
    }

    /** Creates a {@code cbcmodernwarfare:} namespaced {@link ResourceLocation}. */
    public static ResourceLocation resource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
