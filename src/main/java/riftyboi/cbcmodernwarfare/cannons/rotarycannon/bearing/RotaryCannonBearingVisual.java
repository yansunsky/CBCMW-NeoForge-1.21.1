package riftyboi.cbcmodernwarfare.cannons.rotarycannon.bearing;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;

import net.createmod.catnip.animation.AnimationTickHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * 转管炮轴承的 Flywheel 视觉：整组炮管绕轴承轴旋转。
 *
 * <p><b>1.20.1 → 1.21.1</b>：{@code AnimationTickHolder.getRenderTime()} 仍在
 * （在 ponder jar 的 {@code net.createmod.catnip.animation} 下，javap 核对过），
 * {@code OrientedInstance#rotate(Quaternionf)} / {@code position} / {@code translatePosition}
 * 全部未变。
 *
 * <p>与 {@code MediumcannonRecoilBarrelVisual} 的区别：这里每帧都要刷新「跟着转」的方块集合
 * （炮管可能被拆掉或替换），所以有 {@link #refreshAnimatedBlocks()}。
 */
public class RotaryCannonBearingVisual extends AbstractBlockEntityVisual<RotarycannonBearingBlockEntity>
        implements SimpleDynamicVisual {

    private final Map<BlockPos, OrientedInstance> blocks = new HashMap<>();
    private final Map<BlockPos, BlockState> renderedStates = new HashMap<>();
    private final Direction facing;

    public RotaryCannonBearingVisual(VisualizationContext ctx, RotarycannonBearingBlockEntity blockEntity,
            float partialTicks) {
        super(ctx, blockEntity, partialTicks);
        this.facing = this.blockState.getValue(BlockStateProperties.FACING);

        this.blocks.clear();
        for (Map.Entry<BlockPos, BlockState> entry : this.blockEntity.toAnimate.entrySet()) {
            if (entry.getValue() != null) {
                this.blocks.put(entry.getKey(), this.instancerProvider()
                        .instancer(InstanceTypes.ORIENTED, Models.block(entry.getValue()))
                        .createInstance());
                this.renderedStates.put(entry.getKey(), entry.getValue());
            }
        }

        this.updateTransforms(partialTicks);
    }

    @Override
    public void beginFrame(DynamicVisual.Context ctx) {
        this.refreshAnimatedBlocks();
        this.updateTransforms(ctx.partialTick());
    }

    /** 同步「跟着转」的方块集合：删掉消失的，补上新增或换过状态的。 */
    private void refreshAnimatedBlocks() {
        this.blocks.entrySet().removeIf(entry -> {
            if (!this.blockEntity.toAnimate.containsKey(entry.getKey())) {
                entry.getValue().delete();
                this.renderedStates.remove(entry.getKey());
                return true;
            }
            return false;
        });

        for (Map.Entry<BlockPos, BlockState> entry : this.blockEntity.toAnimate.entrySet()) {
            BlockState state = entry.getValue();
            if (state == null) {
                continue;
            }
            BlockState previous = this.renderedStates.get(entry.getKey());
            if (!this.blocks.containsKey(entry.getKey()) || !state.equals(previous)) {
                OrientedInstance old = this.blocks.remove(entry.getKey());
                if (old != null) {
                    old.delete();
                }
                this.blocks.put(entry.getKey(), this.instancerProvider()
                        .instancer(InstanceTypes.ORIENTED, Models.block(state))
                        .createInstance());
                this.renderedStates.put(entry.getKey(), state);
            }
        }
    }

    private void updateTransforms(float partialTicks) {
        BlockPos visualPos = this.getVisualPosition();
        Vector3f basePos = new Vector3f(visualPos.getX(), visualPos.getY(), visualPos.getZ());
        float rot = this.blockEntity.getRot();
        this.blockEntity.setRotation(rot, AnimationTickHolder.getRenderTime());
        Quaternionf qrot = new Quaternionf()
                .rotationAxis(this.blockEntity.getRotation(), new Vector3f(this.facing.step()));

        for (Map.Entry<BlockPos, OrientedInstance> entry : this.blocks.entrySet()) {
            BlockPos pos1 = entry.getKey();
            entry.getValue().position(basePos)
                    .translatePosition(pos1.getX(), pos1.getY(), pos1.getZ());
            entry.getValue().rotate(qrot);
            entry.getValue().setChanged();
        }
    }

    @Override
    public void updateLight(float partialTicks) {
        this.relight(this.pos, new FlatLit[0]);
        for (Map.Entry<BlockPos, OrientedInstance> entry : this.blocks.entrySet()) {
            this.relight(this.pos.offset((Vec3i) entry.getKey()), new FlatLit[] { entry.getValue() });
        }
    }

    @Override
    protected void _delete() {
        for (OrientedInstance block : this.blocks.values()) {
            block.delete();
        }
        this.blocks.clear();
        this.renderedStates.clear();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        // 参与旋转的方块不参与破碎动画（与参考实现一致）。
    }
}
