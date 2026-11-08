package riftyboi.cbcmodernwarfare.cannons.medium_cannon.breech;

import java.util.function.Consumer;

import org.joml.Vector3f;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import riftyboi.cbcmodernwarfare.cannons.medium_cannon.MediumcannonBlock;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareBlockPartials;

/**
 * 炮闩的 Flywheel 视觉：开闩时闩体沿轴向平移。
 *
 * <p><b>1.20.1 → 1.21.1 的两处改动</b>：
 * <ol>
 *   <li>1.20.1 用 {@code CBCClientCommon.getRotationAxis(BlockState)} 算旋转轴 ——
 *       <b>这个方法在 CBC 5.11.7 已经不存在</b>（javap 全量核对过 {@code CBCClientCommon}
 *       的公开方法列表）。改为本类的私有实现 {@link #getRotationAxis(BlockState)}，
 *       算法与 1.20.1 的 {@code MediumcannonBreechRenderer} 里那份一致。</li>
 *   <li>{@code OrientedInstance#translatePosition(float,float,float)} / {@code position} /
 *       {@code setChanged} / {@code rotateYDegrees} / {@code rotateTo} 全部未变
 *       （Flywheel 1.0.6 javap 核对）。</li>
 * </ol>
 */
public class MediumcannonBreechVisual extends AbstractBlockEntityVisual<MediumcannonBreechBlockEntity>
        implements SimpleDynamicVisual {

    private final OrientedInstance breechblock;
    private final Direction blockRotation;
    private final Direction facing;

    public MediumcannonBreechVisual(VisualizationContext ctx, MediumcannonBreechBlockEntity blockEntity, float partialTick) {
        super(ctx, blockEntity, partialTick);

        Direction.Axis axis = getRotationAxis(this.blockState);
        this.facing = this.blockState.getValue(BlockStateProperties.FACING);
        Direction rotation = this.facing.getCounterClockWise(axis);
        if (rotation == Direction.DOWN) {
            rotation = Direction.UP;
        }
        this.blockRotation = rotation;

        this.breechblock = this.instancerProvider()
                .instancer(InstanceTypes.ORIENTED, Models.partial(this.getPartialModelForState()))
                .createInstance();

        boolean alongFirst = this.blockState.getValue(MediumcannonBreechBlock.AXIS);
        if (!alongFirst) {
            this.breechblock.rotateYDegrees(90f);
        }
        if (this.facing.getAxis().isHorizontal()) {
            this.breechblock.rotateTo(Direction.NORTH, Direction.UP);
        }

        this.updateTransforms(partialTick);
    }

    @Override
    public void beginFrame(DynamicVisual.Context ctx) {
        this.updateTransforms(ctx.partialTick());
    }

    private void updateTransforms(float partialTick) {
        float renderedBreechblockOffset = this.blockEntity.getOpenProgress(partialTick);
        renderedBreechblockOffset = renderedBreechblockOffset / 16.0f * 13.0f;
        Vector3f normal = this.blockRotation.step();
        normal.mul(renderedBreechblockOffset);
        this.breechblock.position(this.getVisualPosition())
                .translatePosition(normal.x(), normal.y(), normal.z())
                .setChanged();
    }

    @Override
    public void updateLight(float partialTick) {
        this.relight(this.pos, this.breechblock);
    }

    @Override
    protected void _delete() {
        this.breechblock.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept(this.breechblock);
    }

    private PartialModel getPartialModelForState() {
        return this.blockState.getBlock() instanceof MediumcannonBlock cBlock
                ? CBCModernWarfareBlockPartials.mediumcannonEjectorFor(cBlock.getMediumcannonMaterial())
                : CBCModernWarfareBlockPartials.CAST_IRON_MEDIUMCANNON_FALLING_BREECH;
    }

    /**
     * 闩体的旋转轴：由 {@code FACING} 与 {@code AXIS} 共同决定。
     *
     * <p>1.20.1 调 {@code CBCClientCommon.getRotationAxis}，但 CBC 5.11.7 已删掉它，
     * 这里按同一算法本地实现（与 {@code MediumcannonBreechRenderer} 里那份逐字一致）。
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
