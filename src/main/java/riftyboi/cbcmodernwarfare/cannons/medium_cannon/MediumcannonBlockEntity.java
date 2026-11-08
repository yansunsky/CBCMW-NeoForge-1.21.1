package riftyboi.cbcmodernwarfare.cannons.medium_cannon;

import java.util.List;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import rbasamoyai.createbigcannons.cannons.ItemCannonBehavior;

/**
 * 中型炮方块实体的基类。
 *
 * <p><b>1.20.1 → 1.21.1</b>：{@code addBehaviours(List<BlockEntityBehaviour>)} 的签名没变
 * （Create 6.0.10 javap 核对）。{@code ItemCannonBehavior} 的构造仍然只接一个
 * {@code SmartBlockEntity}，所以整个类逐字搬过来即可。
 */
public class MediumcannonBlockEntity extends SmartBlockEntity implements IMediumcannonBlockEntity {

    private ItemCannonBehavior behavior;

    public MediumcannonBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(this.behavior = this.makeBehavior());
    }

    /** 未完成的炮管用 {@code IncompleteItemCannonBehavior} 覆盖这里。 */
    protected ItemCannonBehavior makeBehavior() {
        return new ItemCannonBehavior(this);
    }

    @Override
    public ItemCannonBehavior cannonBehavior() {
        return this.behavior;
    }
}
