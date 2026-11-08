package riftyboi.cbcmodernwarfare;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Client-side common logic (1.20.1 {@code CBCModernWarfareClientCommon}).
 *
 * <p>NeoForge 1.21.1 is a single module, so the "common" client class must be kept
 * out of the dedicated-server classpath by annotation rather than by module split — the
 * whole class is {@link OnlyIn @OnlyIn(Dist.CLIENT)} and is only ever touched from
 * {@code CBCModernWarfareClientForge} (also client-only), never from {@link CBCModernWarfare}.
 *
 * <p><b>R1 status — skeleton.</b> The 1.20.1 body referenced four things that do not exist
 * in this repository yet:
 * <ul>
 *   <li>{@code index/CBCModernWarfareBlockPartials} / {@code EntityVisualisers} /
 *       {@code FlywheelVisuals} — arrive in R3 (registration skeleton) and R8.</li>
 *   <li>{@code content/sights/camera_handler/CameraPossessionController} and
 *       {@code content/sights/entity/AbstractSightEntity} — arrive in R8.</li>
 *   <li>{@code ponder/CBCModernWarfarePonderIndex} — arrives in R11.</li>
 *   <li>CBC's {@code CameraAccessor} mixin, used to move the camera position.</li>
 * </ul>
 * Until then the methods keep the <b>exact 1.20.1 signatures</b> (so that
 * {@code CBCModernWarfareClientForge} does not have to change twice) and return the
 * inert value. Every body is registered in {@code docs/log/02-Phase2-R1.md} as a stub.
 */
@OnlyIn(Dist.CLIENT)
public class CBCModernWarfareClientCommon {

    /** Called from {@code FMLClientSetupEvent}. */
    public static void onClientSetup() {
        // TODO(R3/R8): CBCModernWarfareBlockPartials.resolveDeferredModels();
        // TODO(R3/R8): CBCModernWarfareEntityVisualisers.register();
        // TODO(R3/R8): CBCModernWarfareFlywheelVisuals.registerVisuals();
    }

    /** Called at the start of every client tick. */
    public static void onClientTickStart(Minecraft mc) {
        if (isGameActive()) {
            // TODO(R8): CameraPossessionController.onClientTick(mc, true);
        }
    }

    /** Called at the end of every client tick. */
    public static void onClientTickEnd(Minecraft mc) {
        if (isGameActive()) {
            // TODO(R8): CameraPossessionController.onClientTick(mc, false);
        }
    }

    /** Called every client game tick. */
    public static void onClientGameTick(Minecraft mc) {
        if (mc.player == null || mc.level == null) {
            return;
        }
        // TODO(R8): SmokeDischargerBoxRenderer.tick();
    }

    /**
     * Sprint + scroll wheel zoom while possessing a sight entity.
     *
     * <p>TODO(R8): needs {@code content/sights/entity/AbstractSightEntity#changeFov(double)}.
     * The 1.20.1 body tested {@code mc.cameraEntity instanceof AbstractSightEntity}.
     *
     * @return whether the scroll was consumed (and the input event should be cancelled)
     */
    public static boolean onScrollMouse(Minecraft mc, double delta) {
        if (mc.player == null || mc.level == null) {
            return false;
        }
        if (!mc.options.keySprint.isDown()) {
            return false;
        }
        // TODO(R8): restore the AbstractSightEntity branch once the sight entity is ported.
        return false;
    }

    /**
     * Camera placement while looking through a sight.
     *
     * <p>TODO(R8): the 1.20.1 body read the sight entity's yaw and offsets and moved the
     * camera through CBC's {@code CameraAccessor} mixin.
     *
     * @return whether the camera angles were overridden
     */
    public static boolean onCameraSetup(Camera camera, double partialTicks,
                                        Supplier<Float> yaw, Supplier<Float> pitch, Supplier<Float> roll,
                                        Consumer<Float> setYaw, Consumer<Float> setPitch, Consumer<Float> setRoll) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) {
            return false;
        }
        // TODO(R8): restore the AbstractSightEntity branch once the sight entity is ported.
        return false;
    }

    protected static boolean isGameActive() {
        return !(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null);
    }
}
