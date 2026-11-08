package riftyboi.cbcmodernwarfare.forge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import riftyboi.cbcmodernwarfare.CBCModernWarfareClientCommon;

/**
 * Client-side <b>mod-bus</b> bootstrap.
 *
 * <p><b>Why this is not {@code @EventBusSubscriber(bus = Bus.MOD)}.</b> In NeoForge 21.1 the
 * annotation's {@code bus()} attribute is deprecated for removal
 * ({@code @Deprecated(since="1.21.1", forRemoval=true)}) — mod-bus listeners are meant to be
 * attached to the bus the {@code @Mod} constructor already receives. The 1.20.1 code did the
 * same thing the hard way, through
 * {@code DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> prepareClient(...))}; the
 * NeoForge equivalent is a plain {@code FMLEnvironment.dist} check plus a method reference.
 *
 * <p>The dist check lives in {@link riftyboi.cbcmodernwarfare.CBCModernWarfare}. The method
 * reference to {@link #onClientSetup(FMLClientSetupEvent)} is only resolved when that branch
 * actually runs, so the dedicated server never class-loads this class nor
 * {@link CBCModernWarfareClientCommon} behind it.
 */
public final class CBCModernWarfareClientModEvents {

    private CBCModernWarfareClientModEvents() {}

    /** Attaches this class's mod-bus listeners. Only ever called on the client. */
    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(CBCModernWarfareClientModEvents::onClientSetup);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        CBCModernWarfareClientCommon.onClientSetup();
    }
}
