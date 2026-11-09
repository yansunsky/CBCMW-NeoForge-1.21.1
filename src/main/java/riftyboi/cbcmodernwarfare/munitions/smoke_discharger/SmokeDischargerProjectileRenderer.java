package riftyboi.cbcmodernwarfare.munitions.smoke_discharger;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import rbasamoyai.createbigcannons.utils.CBCUtils;

/**
 * 烟雾排放器炮弹的渲染器：把弹药物品本身按飞行方向画出来。
 *
 * <p>与 1.20.1 逐行等价。{@link #getTextureLocation} 返回 {@code null} ——
 * 因为整个渲染走的是 {@code ItemRenderer}，不采样实体贴图
 * （1.20.1 原样，别"修"成返回一个假贴图）。
 *
 * <p>1.20.1 → 1.21.1：{@code renderStatic} 的参数表变了
 * （多了 {@code seed}，且最后是 {@code level}），已按 1.21.1 签名调整；
 * {@code CBCUtils.mat4x4fFacing} 未变。
 */
public class SmokeDischargerProjectileRenderer<T extends SmokeDischargerProjectile>
		extends EntityRenderer<T> {

	public SmokeDischargerProjectileRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack,
			MultiBufferSource buffers, int packedLight) {
		ItemStack item = entity.getItem();
		Vec3 vel = entity.getOrientation();
		if (vel.lengthSqr() < 1.0E-4) {
			vel = new Vec3(0.0, -1.0, 0.0);
		}

		poseStack.pushPose();
		if (vel.horizontalDistanceSqr() > 1.0E-4 && Math.abs(vel.y) > 0.01) {
			// 斜飞：先转到「飞行方向」，再叠加水平朝向
			Vec3 horizontal = new Vec3(vel.x, 0.0, vel.z).normalize();
			poseStack.mulPose(CBCUtils.mat4x4fFacing(vel.normalize().reverse(), horizontal));
			poseStack.mulPose(CBCUtils.mat4x4fFacing(horizontal));
		} else {
			poseStack.mulPose(CBCUtils.mat4x4fFacing(vel.normalize()));
		}

		Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemDisplayContext.NONE, packedLight, 0,
				poseStack, buffers, entity.level(), 0);
		poseStack.popPose();
		super.render(entity, entityYaw, partialTicks, poseStack, buffers, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(T projectile) {
		return null;
	}
}
