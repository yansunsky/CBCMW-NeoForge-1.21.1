package riftyboi.cbcmodernwarfare.cannons.rotarycannon.breech;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;

/**
 * 转管炮炮闩的方块实体具体实现。
 *
 * <p><b>1.20.1 的形态</b>：这个类原来在 {@code forge/} 侧，由
 * {@code IndexPlatform#makeRotarycannonBreech} 构造，并带一个
 * {@code createItemHandler()} 暴露 {@link RotarycannonBreechInterface}，
 * 供 {@code RotaryCannonContraptionMixin} 从 contraption 侧取用。
 *
 * <p><b>1.21.1 的处理</b>：平台层间接去掉（单平台无收益），但
 * {@code createItemHandler()} **必须保留** —— 它是「炮车/机械臂能塞弹」的唯一入口。
 * 之前把整个 {@code RotarycannonBreechInterface} 一起删掉是错的，
 * 后果是炮车装填静默失效（见该类 javadoc）。
 *
 * <p>返回裸 {@code IItemHandler}（可为 null）而不是 {@code LazyOptional}，
 * 与 CBC 1.21.1 的 {@code AutocannonBreechBlockEntity#createItemHandler()} 形态一致。
 */
public class RotarycannonBreechBlockEntity extends AbstractRotarycannonBreechBlockEntity {

    private IItemHandler inventory;

    public RotarycannonBreechBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /**
     * 惰性创建并缓存弹药handler。
     *
     * <p>注意：这个 handler 是**无状态视图**，读写都直接落到
     * {@code inputBuffer} / {@code outputBuffer} 上，所以缓存是安全的 ——
     * 不会出现「缓存了一份过期的队列」。
     */
    public IItemHandler createItemHandler() {
        return this.inventory == null
                ? this.inventory = new RotarycannonBreechInterface(this)
                : this.inventory;
    }
}
