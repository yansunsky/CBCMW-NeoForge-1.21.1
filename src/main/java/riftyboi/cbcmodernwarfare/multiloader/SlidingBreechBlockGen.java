package riftyboi.cbcmodernwarfare.multiloader;

import com.simibubi.create.foundation.data.SpecialBlockStateGen;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * 1.20.1 shipped this as an abstract base with an {@code @ExpectPlatform create()} whose
 * Forge implementation lived in {@code multiloader/forge/SlidingBreechBlockGenImpl}.
 *
 * <p>As of CBC 5.11.7 (1.21.1) Create Big Cannons itself exports
 * {@code rbasamoyai.createbigcannons.cannons.big_cannons.breeches.sliding_breech.SlidingBreechBlockGen},
 * which does exactly what the Forge impl used to do for CBC's <b>own</b> namespace
 * (the only difference was {@code CreateBigCannons.resource(...)} instead of
 * {@code CBCModernWarfare.resource(...)} — same {@code createbigcannons} textures,
 * re-pointed through a resource pack).
 *
 * <p>The platform indirection is therefore deleted: call sites should use
 * {@link rbasamoyai.createbigcannons.cannons.big_cannons.breeches.sliding_breech.SlidingBreechBlockGen#create(String)}
 * directly. This class is kept only until the last call site is ported (R4), and is
 * deliberately <b>not</b> registered anywhere.
 *
 * @deprecated use CBC's own {@code SlidingBreechBlockGen}
 */
@Deprecated
public abstract class SlidingBreechBlockGen extends SpecialBlockStateGen {

    protected final String pathAndMaterial;

    protected SlidingBreechBlockGen(String pathAndMaterial) {
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
