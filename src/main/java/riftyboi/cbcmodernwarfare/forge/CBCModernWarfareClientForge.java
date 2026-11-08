package riftyboi.cbcmodernwarfare.forge;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import riftyboi.cbcmodernwarfare.CBCModernWarfare;
import riftyboi.cbcmodernwarfare.CBCModernWarfareClientCommon;

/**
 * Client-side event wiring.
 *
 * <p>1.20.1 had this class in the {@code forge/} module and split the work between
 * {@code prepareClient()} (manual {@code addListener} calls, because Architectury forced a
 * platform entry point) and {@code @EventBusSubscriber(Dist.CLIENT)}. On NeoForge 1.21.1 the
 * annotation alone is enough for the <i>game</i> bus; the two mod-bus listeners that remain
 * ({@code FMLClientSetupEvent}) are registered from the mod constructor instead.
 *
 * <p>Event class mapping applied here:
 * <ul>
 *   <li>{@code TickEvent.ClientTickEvent} (Forge, with a {@code Phase}) →
 *       {@code ClientTickEvent.Pre} / {@code ClientTickEvent.Post} (NeoForge).</li>
 *   <li>{@code TickEvent.ClientTickEvent} as the "game tick" listener →
 *       {@code ClientTickEvent.Post} (1.20.1 ran both on the same event).</li>
 *   <li>{@code InputEvent.MouseScrollingEvent#getScrollDelta()} →
 *       {@code getScrollDeltaY()}.</li>
 *   <li>{@code ViewportEvent.ComputeCameraAngles} keeps its name but is now
 *       {@code ICancellableEvent} instead of exposing {@code isCancelable()}.</li>
 * </ul>
 */
@EventBusSubscriber(modid = CBCModernWarfare.MOD_ID, value = Dist.CLIENT)
public class CBCModernWarfareClientForge {

    @SubscribeEvent
    public static void onClientTickPre(ClientTickEvent.Pre event) {
        CBCModernWarfareClientCommon.onClientTickStart(Minecraft.getInstance());
    }

    @SubscribeEvent
    public static void onClientTickPost(ClientTickEvent.Post event) {
        CBCModernWarfareClientCommon.onClientTickEnd(Minecraft.getInstance());
        CBCModernWarfareClientCommon.onClientGameTick(Minecraft.getInstance());
    }

    @SubscribeEvent
    public static void onSetupCamera(ViewportEvent.ComputeCameraAngles evt) {
        // NOTE(1.21.1): Forge's ComputeCameraAngles exposed isCancelable()/setCanceled().
        // The NeoForge event is NOT cancellable — the supported way to override the camera
        // is to call setYaw/setPitch/setRoll, which is exactly what the common handler
        // already receives as its setters. So there is nothing to cancel here any more.
        CBCModernWarfareClientCommon.onCameraSetup(evt.getCamera(), evt.getPartialTick(),
                evt::getYaw, evt::getPitch, evt::getRoll,
                evt::setYaw, evt::setPitch, evt::setRoll);
    }

    @SubscribeEvent
    public static void onScrollMouse(InputEvent.MouseScrollingEvent evt) {
        if (CBCModernWarfareClientCommon.onScrollMouse(Minecraft.getInstance(), evt.getScrollDeltaY())) {
            evt.setCanceled(true);
        }
    }
}
