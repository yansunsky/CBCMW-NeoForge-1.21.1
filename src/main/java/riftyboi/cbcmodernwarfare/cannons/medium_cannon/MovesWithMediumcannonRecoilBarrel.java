package riftyboi.cbcmodernwarfare.cannons.medium_cannon;

import net.minecraft.world.level.block.state.BlockState;

/**
 * 会随后坐炮管一起移动的方块。
 *
 * <p>1.20.1 → 1.21.1 零变化。装配成 contraption 时，炮管需要知道自己该切成
 * 「运动中」还是「静止」的状态，这两个方法就是那个开关。
 */
public interface MovesWithMediumcannonRecoilBarrel {

    BlockState getMovingState(BlockState original);

    BlockState getStationaryState(BlockState original);
}
