package riftyboi.cbcmodernwarfare.munitions.big_cannon.heap_shell;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import rbasamoyai.ritchiesprojectilelib.projectile_burst.ProjectileBurst;
import rbasamoyai.ritchiesprojectilelib.projectile_burst.ProjectileBurstRenderer;

import riftyboi.cbcmodernwarfare.CBCModernWarfare;

/**
 * HEAP 金属射流的渲染器：面向摄像机的一个发光面片。
 *
 * <p><b>1.20.1 → 1.21.1</b>：{@code ProjectileBurstRenderer} 的
 * {@code renderSubProjectile(SubProjectile, float, PoseStack, MultiBufferSource, int)}
 * 签名未变（RPL 2.1.2 javap 核对）。
 */
public class HEAPBurstRenderer<T extends HEAPBurst> extends ProjectileBurstRenderer<T> {

    private static final ResourceLocation TEXTURE_LOCATION =
            CBCModernWarfare.resource("textures/entity/heat_jet.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(TEXTURE_LOCATION);

    public HEAPBurstRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSubProjectile(ProjectileBurst.SubProjectile subProjectile, float partialTick,
            PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
        PoseStack.Pose lastPose = poseStack.last();
        Matrix4f pose = lastPose.pose();
        Matrix3f normal = lastPose.normal();
        VertexConsumer builder = buffers.getBuffer(RENDER_TYPE);
        vertex(builder, pose, normal, packedLight, 0.0f, 0, 0, 1);
        vertex(builder, pose, normal, packedLight, 1.0f, 0, 1, 1);
        vertex(builder, pose, normal, packedLight, 1.0f, 1, 1, 0);
        vertex(builder, pose, normal, packedLight, 0.0f, 1, 0, 0);
        poseStack.popPose();
    }

    /** 同上：1.21.1 用 {@code addVertex(...).setColor(...)} 链，{@code endVertex} 已删除。 */
    private static void vertex(VertexConsumer builder, Matrix4f pose, Matrix3f normal, int packedLight,
            float x, int y, int u, int v) {
        builder.addVertex(pose, x - 0.5f, (float) y - 0.25f, 0.0f)
                .setColor(255, 255, 255, 255)
                .setUv((float) u, (float) v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(0.0f, 1.0f, 0.0f);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE_LOCATION;
    }
}
