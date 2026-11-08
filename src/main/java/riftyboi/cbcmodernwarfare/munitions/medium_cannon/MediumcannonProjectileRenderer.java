package riftyboi.cbcmodernwarfare.munitions.medium_cannon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.utils.CBCUtils;

/**
 * 中型炮炮弹的渲染器：画弹体模型，曳光弹额外画一个面向摄像机的发光面片。
 *
 * <p><b>1.20.1 → 1.21.1</b>：{@code ItemRenderer#renderStatic}、
 * {@code CBCUtils.mat4x4fFacing}、{@code PoseStack.Pose} 的 {@code pose()/normal()}
 * 全部未变（javap 核对）。去掉了 1.20.1 里未使用的
 * {@code ItemTransforms} / {@code RenderShape} / {@code BlockState} / {@code AABB} /
 * {@code Matrix3f 多余的 CallbackI} 等 import。
 *
 * <p><b>注意</b>：本类在 1.20.1 就位于 common 包但引用客户端类 —— 这是原项目的结构问题。
 * 本项目按约定放在客户端侧（{@code forge/} 下由 ClientForge 引用），
 * 但类本身留在 {@code munitions/medium_cannon} 包内以保持包结构一致，
 * 由 {@code CBCModernWarfareClientForge} 在客户端 dist 下才加载。
 */
public class MediumcannonProjectileRenderer<T extends AbstractMediumcannonProjectile> extends EntityRenderer<T> {

    public MediumcannonProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack,
            MultiBufferSource buffers, int packedLight) {
        boolean isTracer = entity.isTracer();
        ItemStack item = entity.getItem();

        Vec3 vel = entity.getOrientation();
        if (vel.lengthSqr() < 1e-4d) {
            vel = new Vec3(0, -1, 0);
        }

        poseStack.pushPose();
        if (vel.horizontalDistanceSqr() > 1e-4d && Math.abs(vel.y) > 1e-2d) {
            Vec3 horizontal = new Vec3(vel.x, 0, vel.z).normalize();
            poseStack.mulPose(CBCUtils.mat4x4fFacing(vel.normalize().reverse(), horizontal));
            poseStack.mulPose(CBCUtils.mat4x4fFacing(horizontal));
        } else {
            poseStack.mulPose(CBCUtils.mat4x4fFacing(vel.normalize()));
        }
        Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemDisplayContext.NONE,
                isTracer ? LightTexture.FULL_BRIGHT : packedLight, 0, poseStack, buffers, entity.level(), 0);
        poseStack.popPose();

        if (isTracer) {
            int frame = (int) ((entity.getId() + entity.level().getGameTime()) % 4L);
            ResourceLocation textureLoc = CreateBigCannons
                    .resource(String.format("textures/entity/tracer_glow%d.png", frame));
            RenderType renderType = RenderType.entityCutoutNoCull(textureLoc);

            poseStack.pushPose();
            poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            poseStack.scale(1f, 1f, 1f);

            PoseStack.Pose lastPose = poseStack.last();
            Matrix4f pose = lastPose.pose();
            Matrix3f normal = lastPose.normal();
            VertexConsumer builder = buffers.getBuffer(renderType);

            vertex(builder, pose, normal, LightTexture.FULL_BRIGHT, -0.5f, -0.5f, 0, 1);
            vertex(builder, pose, normal, LightTexture.FULL_BRIGHT, -0.5f, 0.5f, 0, 0);
            vertex(builder, pose, normal, LightTexture.FULL_BRIGHT, 0.5f, 0.5f, 1, 0);
            vertex(builder, pose, normal, LightTexture.FULL_BRIGHT, 0.5f, -0.5f, 1, 1);
            poseStack.popPose();
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffers, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(T projectile) {
        // 弹体走物品模型，不贴实体纹理。
        return null;
    }

    @Override
    public boolean shouldRender(T entity, Frustum camera, double camX, double camY, double camZ) {
        return entity.isTracer() || super.shouldRender(entity, camera, camX, camY, camZ);
    }

    /**
     * 1.21.1 的 {@link VertexConsumer} 把 {@code vertex(...).color(...).uv(...).endVertex()} 链
     * 换成了 {@code addVertex(...).setColor(...).setUv(...)}（{@code endVertex} 已删除）。
     * 位置用 {@code addVertex(Matrix4f, ...)} 重载，仍是世界变换后的坐标。
     */
    private static void vertex(VertexConsumer builder, Matrix4f pose, Matrix3f normal, int packedLight,
            float x, float y, int u, int v) {
        builder.addVertex(pose, x, y, 0.0f)
                .setColor(255, 255, 255, 255)
                .setUv((float) u, (float) v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(0.0f, 1.0f, 0.0f);
    }
}
