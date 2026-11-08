package riftyboi.cbcmodernwarfare.cannons.rotarycannon;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import riftyboi.cbcmodernwarfare.cannons.rotarycannon.material.RotarycannonMaterial;

/**
 * 所有转管炮部件的公共基类：定向、可含水。
 *
 * <p><b>1.20.1 → 1.21.1</b>：
 * <ul>
 *   <li>{@code Block#codec()} 是 1.21 新增的抽象方法，{@link DirectionalBlock} 收窄成
 *       {@code MapCodec<? extends DirectionalBlock>}，这里用 {@code MapCodec.unit(this)}。</li>
 *   <li><b>与 {@code MediumcannonBaseBlock} 的真实差异</b>：<b>没有</b>
 *       {@code properties.pushReaction(PushReaction.BLOCK)}，放置时也<b>不</b>写
 *       {@code WATERLOGGED}。1.20.1 就是这样 —— 转管炮可以被活塞推动，
 *       含水状态由后续流程设置。迁移期保持等价，不"顺手修正"。</li>
 * </ul>
 */
public abstract class RotarycannonBaseBlock extends DirectionalBlock
        implements RotarycannonBlock, SimpleWaterloggedBlock {

    private final RotarycannonMaterial material;

    protected RotarycannonBaseBlock(Properties properties, RotarycannonMaterial material) {
        super(properties);
        this.material = material;
        this.registerDefaultState(this.getStateDefinition().any().setValue(BlockStateProperties.WATERLOGGED, false));
    }

    /** 1.21 的方块 codec：无配置参数。 */
    @Override
    protected MapCodec<? extends DirectionalBlock> codec() {
        return MapCodec.unit(this);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING).add(BlockStateProperties.WATERLOGGED);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public RotarycannonMaterial getRotarycannonMaterial() {
        return this.material;
    }

    @Override
    public Direction getFacing(BlockState state) {
        return state.getValue(FACING);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!level.isClientSide) {
            this.onRemoveCannon(state, level, pos, newState, isMoving);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    /** 注意：不写 {@code WATERLOGGED}（与 1.20.1 一致，见类注释）。 */
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(BlockStateProperties.WATERLOGGED)
                ? Fluids.WATER.getSource(false)
                : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public BlockState updateShape(BlockState state, Direction face, BlockState otherState, LevelAccessor level,
            BlockPos pos, BlockPos otherPos) {
        if (state.getValue(BlockStateProperties.WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, face, otherState, level, pos, otherPos);
    }
}
