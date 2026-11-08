package riftyboi.cbcmodernwarfare.cannons.rotarycannon.bearing;

import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;

import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;

import net.createmod.catnip.animation.AnimationTickHolder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import riftyboi.cbcmodernwarfare.cannons.rotarycannon.breech.RotarycannonBreechBlock;

/**
 * 转管炮轴承的原版渲染回退：把「跟着转」的方块绕轴旋转后重绘。
 *
 * <p><b>1.20.1 → 1.21.1</b>：{@code AnimationTickHolder.getRenderTime()} 未变
 * （在 ponder jar 的 {@code net.createmod.catnip.animation} 下）。
 * {@code BlockRenderDispatcher#renderSingleBlock} 签名未变。
 *
 * <p><b>旋转中心</b>：{@code translate(0.5,0.5,0.5) → mulPose(qrot) → translate(-0.5,-0.5,-0.5)}
 * 把方块中心移到原点再转回来，所以整组炮管绕轴承中心转，而不是绕方块角。
 */
public class RotarycannonBearingRenderer extends SmartBlockEntityRenderer<RotarycannonBearingBlockEntity> {

    public RotarycannonBearingRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(RotarycannonBearingBlockEntity spring, float partialTicks, PoseStack ms,
            MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(spring, partialTicks, ms, buffer, light, overlay);

        Direction facing = spring.getBlockState().getValue(BlockStateProperties.FACING);
        float time = AnimationTickHolder.getRenderTime();
        float rot = spring.getRot();
        spring.setRotation(rot, time);
        Quaternionf qrot = new Quaternionf().rotationAxis(spring.getRotation(), new Vector3f(facing.step()));

        ms.pushPose();
        BlockRenderDispatcher brd = Minecraft.getInstance().getBlockRenderer();

        for (Map.Entry<BlockPos, BlockState> entry : spring.toAnimate.entrySet()) {
            if (entry.getValue() == null || entry.getValue().isAir()) {
                continue;
            }
            ms.pushPose();
            ms.translate(entry.getKey().getX(), entry.getKey().getY(), entry.getKey().getZ());
            if (rotatesWithBarrels(entry.getValue())) {
                ms.translate(0.5f, 0.5f, 0.5f);
                ms.mulPose(qrot);
                ms.translate(-0.5f, -0.5f, -0.5f);
            }
            brd.renderSingleBlock(entry.getValue(), ms, buffer, light, OverlayTexture.NO_OVERLAY);
            ms.popPose();
        }

        ms.popPose();
    }

    /** 轴承与炮闩本身不跟着转（它们是参照系）。 */
    private static boolean rotatesWithBarrels(BlockState state) {
        return !(state.getBlock() instanceof RotarycannonBearingBlock)
                && !(state.getBlock() instanceof RotarycannonBreechBlock);
    }
}
