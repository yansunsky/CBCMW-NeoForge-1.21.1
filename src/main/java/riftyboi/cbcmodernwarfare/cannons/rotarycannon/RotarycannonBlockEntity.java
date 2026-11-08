package riftyboi.cbcmodernwarfare.cannons.rotarycannon;

import java.util.List;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import rbasamoyai.createbigcannons.cannons.ItemCannonBehavior;

/**
 * 转管炮方块实体的基类。
 *
 * <p><b>1.20.1 → 1.21.1</b>：{@code addBehaviours} 与 {@code ItemCannonBehavior} 的
 * 构造签名都未变，逐字搬入。
 */
public class RotarycannonBlockEntity extends SmartBlockEntity implements IRotarycannonBlockEntity {

    private ItemCannonBehavior behavior;

    public RotarycannonBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(this.behavior = this.makeBehavior());
    }

    protected ItemCannonBehavior makeBehavior() {
        return new ItemCannonBehavior(this);
    }

    @Override
    public ItemCannonBehavior cannonBehavior() {
        return this.behavior;
    }
}
