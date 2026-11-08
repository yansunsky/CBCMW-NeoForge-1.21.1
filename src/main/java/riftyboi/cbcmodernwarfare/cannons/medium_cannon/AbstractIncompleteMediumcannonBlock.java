package riftyboi.cbcmodernwarfare.cannons.medium_cannon;

import com.simibubi.create.foundation.block.IBE;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import riftyboi.cbcmodernwarfare.cannons.medium_cannon.material.MediumcannonMaterial;
import riftyboi.cbcmodernwarfare.crafting.incomplete.IncompleteMediumcannonBlockEntity;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareBlockEntities;

/**
 * 「未完成」的中型炮部件（铸造出来但还没加工完）。
 *
 * <p>与成品方块的区别只有三处：{@code isComplete}/{@code isBreechMechanism} 恒为 false，
 * 方块实体换成 {@link IncompleteMediumcannonBlockEntity}。
 *
 * <p><b>1.20.1 → 1.21.1</b>：{@code IBE<T>} 的三个方法签名未变。
 */
public abstract class AbstractIncompleteMediumcannonBlock extends MediumcannonBaseBlock
        implements IBE<IncompleteMediumcannonBlockEntity> {

    protected AbstractIncompleteMediumcannonBlock(Properties properties, MediumcannonMaterial material) {
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
    public Class<IncompleteMediumcannonBlockEntity> getBlockEntityClass() {
        return IncompleteMediumcannonBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends IncompleteMediumcannonBlockEntity> getBlockEntityType() {
        return CBCModernWarfareBlockEntities.INCOMPLETE_MEDIUMCANNON.get();
    }
}
