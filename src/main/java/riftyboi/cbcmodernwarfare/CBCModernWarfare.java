package riftyboi.cbcmodernwarfare;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

/**
 * CBC Modern Warfare — NeoForge 1.21.1 entry point.
 *
 * <p>This class is the migration target for the original 1.20.1 Architectury
 * {@code common/} + {@code forge/} modules. The port is being done in phases:
 *
 * <ul>
 *   <li><b>Phase 1 (current)</b> — clean MDK skeleton: project scaffolding, mod
 *       id, Gradle build, dependency wiring. The content registries are not yet
 *       ported; {@link #init()} only prepares logging and the shared
 *       {@link CreateRegistrate} instance.</li>
 *   <li><b>Phase 2</b> — port {@code index/} registries and platform glue so the
 *       mod compiles.</li>
 *   <li><b>Phase 3</b> — port the runtime behaviour (DataComponents, network
 *       payloads, Flywheel visuals, client isolation).</li>
 * </ul>
 *
 * <p>See {@code docs/plan/} for the full migration plan.
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

    /**
     * FML injects the mod event bus and the mod container. In 1.21.1 NeoForge
     * this is the recommended constructor shape (see the official MDK).
     */
    public CBCModernWarfare(IEventBus modEventBus, ModContainer modContainer) {
        REGISTRATE.registerEventListeners(modEventBus);

        init();

        LOGGER.info("[{}] initialized on NeoForge 1.21.1", MOD_ID);
    }

    /**
     * Registers every content index of this mod.
     *
     * <p>Phase 1 leaves the body empty on purpose: the individual
     * {@code CBCModernWarfareXxx.register()} calls are restored one package at a
     * time while porting. The order below mirrors the original 1.20.1 version and
     * must be preserved once the entries are ported:
     *
     * <pre>
     * CBCModernWarfareSoundEvents.prepare()
     * CBCModernWarfareMunitionPropertiesHandlers.init()
     * ModGroup.register()
     * CBCModernWarfareBlocks.register()
     * CBCModernWarfareItem.register()
     * CBCModernWarfareBlockEntities.register()
     * CBCModernWarfareFluids.register()
     * CBCModernWarfareMenuTypes.register()
     * CBCModernWarfareEntityTypes.register()
     * CBCModernWarfareRecipeTypes.register()
     * CBCMWCannonContraptionTypes.register()
     * CBCModernWarfareChecks.register()
     * </pre>
     */
    public static void init() {
        // Intentionally empty during Phase 1 — see the Javadoc above.
    }

    /** Creates a {@code cbcmodernwarfare:} namespaced {@link ResourceLocation}. */
    public static ResourceLocation resource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
