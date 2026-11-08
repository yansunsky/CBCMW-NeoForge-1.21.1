package riftyboi.cbcmodernwarfare.cannons.rotarycannon.breech;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 转管炮炮闩的方具实体具体实现。
 *
 * <p><b>1.20.1 的形态</b>：这个类原来在 {@code forge/} 侧，由
 * {@code IndexPlatform#makeRotarycannonBreech} 通过
 * {@code IndexPlatformImpl} 构造，并且额外实现了一个 {@code RotarycannonBreechInterface}
 * （用来在平台层暴露 {@code IItemHandler}）。
 *
 * <p><b>1.21.1 的处理</b>：单平台下这层平台间接没有收益，整个
 * {@code RotarycannonBreechInterface} 删除，本类只保留构造。
 * 弹药容器的读写走 CBC 自己的 {@code AutocannonAmmoContainerItem}（父类已实现），
 * 不经过 Forge 能力系统。
 */
public class RotarycannonBreechBlockEntity extends AbstractRotarycannonBreechBlockEntity {

    public RotarycannonBreechBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
