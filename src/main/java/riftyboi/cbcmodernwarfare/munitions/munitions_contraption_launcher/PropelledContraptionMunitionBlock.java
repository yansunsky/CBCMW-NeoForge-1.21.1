package riftyboi.cbcmodernwarfare.munitions.munitions_contraption_launcher;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

/**
 * 「可被发射器载具装载 / 推进」的方块需要实现的行为契约。
 *
 * <p>1.20.1 原样迁移，**零 API 变化** —— 这 5 个方法用到的类型
 * （{@code StructureBlockInfo}、{@code Rotation}、{@code Direction.Axis}）
 * 在 1.21.1 全部未改动。
 *
 * <p>被 {@link ContraptionMunitionBlock} 直接 implements，所以改动这里会影响所有载具弹药方块。
 */
public interface PropelledContraptionMunitionBlock {

    /**
     * 炮管旋转时如何变换本方块的状态。
     *
     * @param oldState        旋转前的状态
     * @param rotationAxis    绕哪根轴旋转
     * @param rotation        旋转方向（顺时针/逆时针/180°）
     * @return 旋转后的状态
     */
    BlockState onCannonRotate(BlockState oldState, Direction.Axis rotationAxis, Rotation rotation);

    /**
     * 本方块当前朝向的轴（用于判断它是否能塞进某个朝向的炮管）。
     */
    Direction.Axis getAxis(BlockState state);

    /**
     * 能否被装进朝向为 {@code facing} 轴的炮管。
     */
    boolean canBeLoaded(BlockState state, Direction.Axis facing);

    /**
     * 手动装填时，要写进 contraption 结构模板的方块信息。
     *
     * @param stack             手上拿着的物品栈
     * @param localPos          该方块在 contraption 里的局部坐标
     * @param cannonOrientation 炮口朝向
     */
    StructureTemplate.StructureBlockInfo getHandloadingInfo(ItemStack stack, BlockPos localPos,
                                                           Direction cannonOrientation);

    /**
     * 从结构模板信息还原出对应的物品栈（拆除/退出时用）。
     */
    ItemStack getExtractedItem(StructureTemplate.StructureBlockInfo info);
}
