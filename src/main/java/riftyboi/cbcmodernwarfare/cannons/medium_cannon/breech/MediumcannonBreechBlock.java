package riftyboi.cbcmodernwarfare.cannons.medium_cannon.breech;

import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.api.contraption.transformable.TransformableBlock;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;
import rbasamoyai.createbigcannons.crafting.casting.CannonCastShape;

import riftyboi.cbcmodernwarfare.cannons.medium_cannon.MediumcannonBaseBlock;
import riftyboi.cbcmodernwarfare.cannons.medium_cannon.MovesWithMediumcannonRecoilBarrel;
import riftyboi.cbcmodernwarfare.cannons.medium_cannon.material.MediumcannonMaterial;
import riftyboi.cbcmodernwarfare.crafting.casting.CBCModernWarfareCastShapes;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareBlockEntities;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareShapes;

/**
 * 中型炮的炮闩（breech）。
 *
 * <p><b>1.20.1 → 1.21.1</b>：
 * <ul>
 *   <li>{@code Block#codec()} 是 1.21 新增的抽象方法，由 {@link MediumcannonBaseBlock} 统一实现。</li>
 *   <li>{@code StructureBlockInfo} 由 class 变 record，取状态要写 {@code info.state()}。</li>
 *   <li>{@code StructureTransform#rotateFacing(Direction)} 仍在，签名未变。</li>
 *   <li>{@code IWrenchable} 与 {@code TransformableBlock} 的包路径没动
 *       （{@code com.simibubi.create.content.equipment.wrench.IWrenchable}、
 *       {@code com.simibubi.create.api.contraption.transformable.TransformableBlock}）。</li>
 * </ul>
 */
public class MediumcannonBreechBlock extends MediumcannonBaseBlock
        implements IBE<MediumcannonBreechBlockEntity>, TransformableBlock, IWrenchable, MovesWithMediumcannonRecoilBarrel {

    public static final BooleanProperty AXIS = DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE;

    public static final BooleanProperty ASSEMBLED = BooleanProperty.create("assembled");

    public MediumcannonBreechBlock(Properties properties, MediumcannonMaterial material) {
        super(properties, material);
        this.registerDefaultState(this.defaultBlockState().setValue(ASSEMBLED, false));
    }

    @Override
    public Class<MediumcannonBreechBlockEntity> getBlockEntityClass() {
        return MediumcannonBreechBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends MediumcannonBreechBlockEntity> getBlockEntityType() {
        return CBCModernWarfareBlockEntities.MEDIUMCANNON_BREECH.get();
    }

    @Override
    public BlockState getMovingState(BlockState original) {
        return original.setValue(ASSEMBLED, false);
    }

    @Override
    public BlockState getStationaryState(BlockState original) {
        return original.setValue(ASSEMBLED, true);
    }

    @Override
    public CannonCastShape getCannonShape() {
        return CBCModernWarfareCastShapes.MEDIUMCANNON_BREECH;
    }

    @Override
    public boolean canConnectToSide(BlockState state, Direction face) {
        return this.getFacing(state) == face;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(AXIS).add(ASSEMBLED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction nearest = context.getNearestLookingDirection();
        return super.getStateForPlacement(context)
                .setValue(FACING, nearest)
                .setValue(AXIS, nearest.getAxis() == Direction.Axis.Z);
    }

    @Override
    public Direction getFacing(BlockState state) {
        return state.getValue(FACING);
    }

    /**
     * 装配成 contraption 后的交互（开闩 / 装弹 / 退壳）。
     *
     * <p><b>TODO(R4b)</b>：本方法依赖两样还没迁的东西 ——
     * {@code MountedMediumcannonContraption}（见 {@code getCannonContraption} 的占位说明）
     * 与 {@code munitions/medium_cannon} 的 {@code MediumcannonAmmoItem}（R5）。
     * 目前返回 {@code false} 表示「不拦截」，即装配后暂时无法手动装弹。
     */
    @Override
    public boolean onInteractWhileAssembled(Player player, BlockPos localPos, Direction side,
            InteractionHand interactionHand, Level level, Contraption contraption,
            BlockEntity be, StructureTemplate.StructureBlockInfo info, PitchOrientedContraptionEntity entity) {
        return false;
    }

    @Override
    public boolean isBreechMechanism(BlockState state) {
        return true;
    }

    @Override
    public boolean isComplete(BlockState state) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return CBCModernWarfareShapes.BREECH.get(this.getFacing(state).getAxis());
    }

    @Override
    public BlockState transform(BlockState state, StructureTransform transform) {
        if (transform.mirror != null) {
            state = mirror(state, transform.mirror);
        }
        if (transform.rotationAxis == Direction.Axis.Y) {
            return rotate(state, transform.rotation);
        }
        Direction newFacing = transform.rotateFacing(state.getValue(FACING));
        if (transform.rotationAxis == newFacing.getAxis() && transform.rotation.ordinal() % 2 == 1) {
            state = state.cycle(AXIS);
        }
        return state.setValue(FACING, newFacing);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        if (rot.ordinal() % 2 == 1) {
            state = state.cycle(AXIS);
        }
        return super.rotate(state, rot);
    }
}
