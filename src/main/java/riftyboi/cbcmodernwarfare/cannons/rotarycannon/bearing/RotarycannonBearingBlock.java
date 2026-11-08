package riftyboi.cbcmodernwarfare.cannons.rotarycannon.bearing;

import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import rbasamoyai.createbigcannons.crafting.casting.CannonCastShape;

import riftyboi.cbcmodernwarfare.cannons.rotarycannon.RotarycannonBaseBlock;
import riftyboi.cbcmodernwarfare.cannons.rotarycannon.material.RotarycannonMaterial;
import riftyboi.cbcmodernwarfare.crafting.casting.CBCModernWarfareCastShapes;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareBlockEntities;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareShapes;

/**
 * 转管炮的轴承：装配后整组炮管绕着它转。
 *
 * <p><b>1.20.1 → 1.21.1</b>：零改动。注意 {@code getMovingState} 返回<b>空气</b>
 * （轴承本身在装配态不可见），这是原代码的行为。
 * 形状复用 {@code RECOIL_BARREL} 而不是单独加一个 shaper —— 也是原样。
 */
public class RotarycannonBearingBlock extends RotarycannonBaseBlock
        implements IBE<RotarycannonBearingBlockEntity>, MovesWithRotaryCannonBearing {

    public RotarycannonBearingBlock(Properties properties, RotarycannonMaterial material) {
        super(properties, material);
    }

    @Override
    public Class<RotarycannonBearingBlockEntity> getBlockEntityClass() {
        return RotarycannonBearingBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends RotarycannonBearingBlockEntity> getBlockEntityType() {
        return CBCModernWarfareBlockEntities.ROTARYCANNON_BEARING.get();
    }

    @Override
    public CannonCastShape getCannonShape() {
        return CBCModernWarfareCastShapes.ROTARYCANNON_BEARING;
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
        return CBCModernWarfareShapes.RECOIL_BARREL.get(this.getFacing(state).getAxis());
    }

    /** 装配态下轴承自身不可见（由 visual 画转动的炮管组）。 */
    @Override
    public BlockState getMovingState(BlockState original) {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public BlockState getStationaryState(BlockState original) {
        return original;
    }
}
