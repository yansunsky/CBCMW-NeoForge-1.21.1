package riftyboi.cbcmodernwarfare.cannons.medium_cannon.recoil_barrel;

import com.tterrag.registrate.util.nullness.NonNullFunction;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.simibubi.create.foundation.block.IBE;

import rbasamoyai.createbigcannons.crafting.casting.CannonCastShape;

import riftyboi.cbcmodernwarfare.cannons.medium_cannon.MediumcannonBaseBlock;
import riftyboi.cbcmodernwarfare.cannons.medium_cannon.MovesWithMediumcannonRecoilBarrel;
import riftyboi.cbcmodernwarfare.cannons.medium_cannon.material.MediumcannonMaterial;
import riftyboi.cbcmodernwarfare.crafting.casting.CBCModernWarfareCastShapes;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareBlockEntities;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareShapes;

/**
 * 中型炮的后坐炮管：开火后整段往后缩，并提供一个可替换的移动态方块。
 *
 * <p><b>1.20.1 → 1.21.1</b>：{@code NonNullFunction<Direction, BlockState>} 仍在
 * {@code com.tterrag.registrate.util.nullness}，签名未变。
 */
public class MediumcannonRecoilBarrelBlock extends MediumcannonBaseBlock
        implements IBE<MediumcannonRecoilBarrelBlockEntity>, MovesWithMediumcannonRecoilBarrel {

    /** 装配为 contraption 时的形态切换函数。 */
    private final NonNullFunction<Direction, BlockState> movingBlockFunction;

    public static final BooleanProperty ASSEMBLED = BooleanProperty.create("assembled");

    public MediumcannonRecoilBarrelBlock(Properties properties, MediumcannonMaterial material,
            NonNullFunction<Direction, BlockState> movingBlockFunction) {
        super(properties, material);
        this.movingBlockFunction = movingBlockFunction;
        this.registerDefaultState(this.defaultBlockState().setValue(ASSEMBLED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ASSEMBLED);
    }

    @Override
    public Class<MediumcannonRecoilBarrelBlockEntity> getBlockEntityClass() {
        return MediumcannonRecoilBarrelBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends MediumcannonRecoilBarrelBlockEntity> getBlockEntityType() {
        return CBCModernWarfareBlockEntities.MEDIUMCANNON_RECOIL_BARREL.get();
    }

    @Override
    public CannonCastShape getCannonShape() {
        return CBCModernWarfareCastShapes.MEDIUMCANNON_RECOIL_BARREL;
    }

    @Override
    public boolean isBreechMechanism(BlockState state) {
        return false;
    }

    @Override
    public boolean isComplete(BlockState state) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return CBCModernWarfareShapes.BARREL.get(this.getFacing(state).getAxis());
    }

    @Override
    public BlockState getMovingState(BlockState original) {
        return this.movingBlockFunction.apply(this.getFacing(original));
    }

    @Override
    public BlockState getStationaryState(BlockState original) {
        return original.setValue(ASSEMBLED, false);
    }

    /** 未连接时的形态（装配态用）。 */
    public BlockState getDisconnectedState(BlockState original) {
        return original.setValue(ASSEMBLED, true);
    }
}
