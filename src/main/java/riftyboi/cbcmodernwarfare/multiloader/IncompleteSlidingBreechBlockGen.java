package riftyboi.cbcmodernwarfare.multiloader;

import com.simibubi.create.foundation.data.SpecialBlockStateGen;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Counterpart of {@link SlidingBreechBlockGen} for the "incomplete" (partly built) blocks.
 *
 * <p>CBC 5.11.7 ships {@code rbasamoyai.createbigcannons.crafting.incomplete.IncompleteSlidingBreechBlockGen},
 * which is the same generator bound to CBC's own namespace, so the {@code @ExpectPlatform}
 * split is deleted here as well. See {@link SlidingBreechBlockGen} for the reasoning.
 *
 * @deprecated use CBC's own {@code IncompleteSlidingBreechBlockGen}
 */
@Deprecated
public abstract class IncompleteSlidingBreechBlockGen extends SpecialBlockStateGen {

    protected final String pathAndMaterial;

    protected IncompleteSlidingBreechBlockGen(String pathAndMaterial) {
        this.pathAndMaterial = pathAndMaterial;
    }

    @Override
    protected int getXRotation(BlockState state) {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        return facing.getAxis().isVertical() ? (facing == Direction.DOWN ? 180 : 0) : 90;
    }

    @Override
    protected int getYRotation(BlockState state) {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        return facing.getAxis().isVertical() ? 0 : this.horizontalAngle(facing) + 180;
    }
}
