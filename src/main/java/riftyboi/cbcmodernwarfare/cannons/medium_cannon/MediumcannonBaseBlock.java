package riftyboi.cbcmodernwarfare.cannons.medium_cannon;

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
import net.minecraft.world.level.material.PushReaction;

import riftyboi.cbcmodernwarfare.cannons.medium_cannon.material.MediumcannonMaterial;

/**
 * 所有中型炮部件的公共基类：定向、可含水、不可被活塞推动。
 *
 * <p><b>1.20.1 → 1.21.1 唯一的变化</b>：{@code Block} 新增了抽象方法
 * {@code protected MapCodec<? extends Block> codec()}（1.21 的方块 codec 化改造）。
 * {@link DirectionalBlock} 把它收窄成 {@code MapCodec<? extends DirectionalBlock>}，
 * 所以子类必须实现。CBC 自己也这么做（见 {@code QuickfiringBreechBlock#codec}），
 * 这里沿用 {@link MapCodec#unit(Object)} —— 我们的方块没有可编码的构造参数。
 *
 * <p>其余方法（{@code onRemove} / {@code getStateForPlacement} / {@code rotate} /
 * {@code mirror} / {@code getFluidState} / {@code updateShape}）签名全部未变。
 */
public abstract class MediumcannonBaseBlock extends DirectionalBlock implements MediumcannonBlock, SimpleWaterloggedBlock {

    private final MediumcannonMaterial material;

    protected MediumcannonBaseBlock(Properties properties, MediumcannonMaterial material) {
        super(properties.pushReaction(PushReaction.BLOCK));
        this.material = material;
        this.registerDefaultState(this.getStateDefinition().any().setValue(BlockStateProperties.WATERLOGGED, false));
    }

    /**
     * 1.21 的方块 codec。
     *
     * <p>{@code MapCodec.unit(this)} 表示「这个方块没有可序列化的配置」，
     * 与 1.20.1 的空实现等价。参考实现（0.0.6v 的 1.21.1 移植）用的也是这一句。
     */
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
    public MediumcannonMaterial getMediumcannonMaterial() {
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

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean waterlogged = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
        return this.defaultBlockState()
                .setValue(FACING, context.getNearestLookingDirection().getOpposite())
                .setValue(BlockStateProperties.WATERLOGGED, waterlogged);
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
