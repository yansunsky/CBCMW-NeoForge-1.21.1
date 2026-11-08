package riftyboi.cbcmodernwarfare.index;

import static net.minecraft.core.Direction.NORTH;

import static net.minecraft.world.level.block.Block.box;

import com.simibubi.create.AllShapes.Builder;

import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.world.phys.shapes.Shapes;

/**
 * Voxel shapes shared by the cannon parts.
 *
 * <p><b>1.20.1 → 1.21.1: no change at all.</b> {@code com.simibubi.create.AllShapes.Builder} and
 * {@code net.createmod.catnip.math.VoxelShaper} kept their packages and their
 * {@code forHorizontal(Direction)} / {@code forAxis()} terminal methods (verified by javap on
 * Create 6.0.10). Copied over verbatim.
 */
public class CBCModernWarfareShapes {

    public static final VoxelShaper
            PERISCOPE = new Builder(Shapes.or(box(14, 8, -3, 19, 24, 2),
                    box(-3, 8, -3, 2, 24, 2), box(2, 8, -4, 14, 24, 0),
                    box(16, 8, 2, 20, 24, 8), box(-4, 8, 2, 0, 24, 8))).forHorizontal(NORTH),

            PERISCOPE_RING = new Builder(Shapes.or(
                    box(14, 8, -3, 19, 24, 2),
                    box(-3, 8, -3, 2, 24, 2),
                    box(-3, 8, 14, 2, 24, 19),
                    box(14, 8, 14, 19, 24, 19),
                    box(2, 8, -4, 14, 24, 0),
                    box(16, 8, 2, 20, 24, 14),
                    box(-4, 8, 2, 0, 24, 14),
                    box(2, 8, 16, 14, 24, 20))).forHorizontal(NORTH),

            BARREL = new Builder(Shapes.or(box(4, 0, 4, 12, 16, 12))).forAxis(),
            RECOIL_BARREL = new Builder(Shapes.or(box(3, 0, 3, 13, 16, 13))).forAxis(),
            BREECH = new Builder(Shapes.or(box(1, 0, 1, 15, 16, 15))).forAxis();

    private CBCModernWarfareShapes() {}
}
