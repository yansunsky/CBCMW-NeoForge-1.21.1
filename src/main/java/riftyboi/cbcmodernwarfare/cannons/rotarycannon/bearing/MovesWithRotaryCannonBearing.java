package riftyboi.cbcmodernwarfare.cannons.rotarycannon.bearing;

import net.minecraft.world.level.block.state.BlockState;

/**
 * 会随后坐轴承一起移动的方块。
 *
 * <p>1.20.1 → 1.21.1 零变化。
 */
public interface MovesWithRotaryCannonBearing {

    BlockState getMovingState(BlockState original);

    BlockState getStationaryState(BlockState original);
}
