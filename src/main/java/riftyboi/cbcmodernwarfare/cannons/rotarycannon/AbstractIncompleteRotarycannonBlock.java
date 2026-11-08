package riftyboi.cbcmodernwarfare.cannons.rotarycannon;

import com.simibubi.create.foundation.block.IBE;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import riftyboi.cbcmodernwarfare.cannons.rotarycannon.material.RotarycannonMaterial;
import riftyboi.cbcmodernwarfare.crafting.incomplete.IncompleteRotarycannonBlockEntity;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareBlockEntities;

/**
 * 「未完成」的转管炮部件。
 *
 * <p><b>1.20.1 → 1.21.1</b>：{@code IBE<T>} 的三个方法未变。
 * 注意 1.20.1 的 import 里带了 {@code CBCBlockEntities} / {@code IncompleteAutocannonBlockEntity}
 * 但实际没用到（是残留 import），迁移时按它们的职责判断：真正用到的只有本项目自己的
 * {@code IncompleteRotarycannonBlockEntity} 与 {@code CBCModernWarfareBlockEntities}。
 */
public abstract class AbstractIncompleteRotarycannonBlock extends RotarycannonBaseBlock
        implements IBE<IncompleteRotarycannonBlockEntity> {

    protected AbstractIncompleteRotarycannonBlock(Properties properties, RotarycannonMaterial material) {
        super(properties, material);
    }

    @Override
    public boolean isComplete(BlockState state) {
        return false;
    }

    @Override
    public boolean isBreechMechanism(BlockState state) {
        return false;
    }

    @Override
    public Class<IncompleteRotarycannonBlockEntity> getBlockEntityClass() {
        return IncompleteRotarycannonBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends IncompleteRotarycannonBlockEntity> getBlockEntityType() {
        return CBCModernWarfareBlockEntities.INCOMPLETE_ROTARYCANNON.get();
    }
}
