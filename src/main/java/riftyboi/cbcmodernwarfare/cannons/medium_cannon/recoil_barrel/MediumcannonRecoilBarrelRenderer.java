package riftyboi.cbcmodernwarfare.cannons.medium_cannon.recoil_barrel;

import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;

import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;

import dev.engine_room.flywheel.api.visualization.VisualizationManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import org.joml.Vector3f;

/**
 * 后坐炮管的原版渲染回退：把「跟着动」的方块按后坐位移重绘一遍。
 *
 * <p><b>1.20.1 → 1.21.1</b>：{@code BlockRenderDispatcher#renderSingleBlock} 的签名
 * 多了一个 {@code ModelData} 参数吗？—— 没有，仍是
 * {@code (BlockState, PoseStack, MultiBufferSource, int, int)}（javap 核对），逐字搬入。
 */
public class MediumcannonRecoilBarrelRenderer extends SmartBlockEntityRenderer<MediumcannonRecoilBarrelBlockEntity> {

    public MediumcannonRecoilBarrelRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(MediumcannonRecoilBarrelBlockEntity spring, float partialTicks, PoseStack ms,
            MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(spring, partialTicks, ms, buffer, light, overlay);

        // Flywheel 接管时不做原版渲染。
        if (VisualizationManager.supportsVisualization(spring.getLevel())) {
            return;
        }

        BlockState state = spring.getBlockState();
        Direction facing = state.getValue(BlockStateProperties.FACING);

        float scale = spring.getAnimateOffset(partialTicks);

        ms.pushPose();

        BlockRenderDispatcher brd = Minecraft.getInstance().getBlockRenderer();
        Vector3f normal = facing.step();
        normal.mul((1 - scale) * -0.5f);
        for (Map.Entry<BlockPos, BlockState> entry : spring.toAnimate.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            ms.pushPose();
            BlockPos pos = entry.getKey();
            ms.translate(pos.getX() + normal.x(), pos.getY() + normal.y(), pos.getZ() + normal.z());
            brd.renderSingleBlock(entry.getValue(), ms, buffer, light, OverlayTexture.NO_OVERLAY);
            ms.popPose();
        }

        ms.popPose();
    }
}
