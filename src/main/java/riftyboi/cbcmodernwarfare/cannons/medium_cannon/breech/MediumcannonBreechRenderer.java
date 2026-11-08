package riftyboi.cbcmodernwarfare.cannons.medium_cannon.breech;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;

import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;

import net.createmod.catnip.render.CachedBuffers;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import riftyboi.cbcmodernwarfare.cannons.medium_cannon.MediumcannonBlock;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareBlockPartials;

/**
 * 炮闩的原版渲染回退：Flywheel 不可用时（如光影/后端不支持）走这里。
 *
 * <p><b>1.20.1 → 1.21.1</b>：{@code CachedBuffers.partialFacing(...)} 与
 * {@code VisualizationManager.supportsVisualization(LevelAccessor)} 签名未变。
 * 唯一的适配点与 {@code MediumcannonBreechVisual} 相同：CBC 5.11.7 删掉了
 * {@code CBCClientCommon.getRotationAxis}，改为本类私有实现。
 */
public class MediumcannonBreechRenderer extends SmartBlockEntityRenderer<MediumcannonBreechBlockEntity> {

    public MediumcannonBreechRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRenderOffScreen(MediumcannonBreechBlockEntity blockEntity) {
        return true;
    }

    @Override
    protected void renderSafe(MediumcannonBreechBlockEntity te, float partialTicks, PoseStack ms,
            MultiBufferSource buffer, int light, int overlay) {
        BlockState blockState = te.getBlockState();

        // Flywheel 接管时不做原版渲染，否则会双重绘制。
        if (VisualizationManager.supportsVisualization(te.getLevel())) {
            return;
        }

        Direction facing = blockState.getValue(BlockStateProperties.FACING);
        Direction.Axis axis = getRotationAxis(blockState);
        Direction blockRotation = facing.getCounterClockWise(axis);
        if (blockRotation == Direction.UP) {
            blockRotation = Direction.DOWN;
        }

        Quaternionf qrot = new Quaternionf();

        boolean alongFirst = blockState.getValue(MediumcannonBreechBlock.AXIS);
        if (facing.getAxis().isHorizontal() && !alongFirst) {
            Direction rotDir = facing.getAxis() == Direction.Axis.X ? Direction.DOWN : Direction.WEST;
            qrot = Axis.of(rotDir.step()).rotationDegrees(90f);
        }
        if (facing.getAxis() == Direction.Axis.X && alongFirst) {
            qrot = Axis.of(blockRotation.step()).rotationDegrees(-90f);
        }

        VertexConsumer vcons = buffer.getBuffer(RenderType.solid());

        ms.pushPose();

        float progress = te.getOpenProgress(partialTicks);
        float renderedBreechblockOffset = progress / 16.0f * 11.0f;
        Vector3f normal = blockRotation.step();
        normal.mul(renderedBreechblockOffset);

        CachedBuffers.partialFacing(getPartialModelForState(te), blockState, blockRotation)
                .translate(normal.x(), normal.y(), normal.z())
                .rotateCentered(qrot)
                .light(light)
                .renderInto(ms, vcons);

        ms.popPose();
    }

    private static PartialModel getPartialModelForState(MediumcannonBreechBlockEntity breech) {
        return breech.getBlockState().getBlock() instanceof MediumcannonBlock cBlock
                ? CBCModernWarfareBlockPartials.mediumcannonEjectorFor(cBlock.getMediumcannonMaterial())
                : CBCModernWarfareBlockPartials.CAST_IRON_MEDIUMCANNON_FALLING_BREECH;
    }

    /**
     * 闩体的旋转轴。
     *
     * <p>1.20.1 调 {@code CBCClientCommon.getRotationAxis}；CBC 5.11.7 已删该方法，
     * 这里按同一算法本地实现。
     */
    private static Direction.Axis getRotationAxis(BlockState state) {
        boolean flag = state.getValue(MediumcannonBreechBlock.AXIS);
        return switch (state.getValue(MediumcannonBreechBlock.FACING).getAxis()) {
            case X -> flag ? Direction.Axis.Y : Direction.Axis.Z;
            case Y -> flag ? Direction.Axis.X : Direction.Axis.Z;
            case Z -> flag ? Direction.Axis.X : Direction.Axis.Y;
        };
    }
}
