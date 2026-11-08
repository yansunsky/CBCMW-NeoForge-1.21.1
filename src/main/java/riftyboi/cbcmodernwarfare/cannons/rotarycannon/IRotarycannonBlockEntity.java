package riftyboi.cbcmodernwarfare.cannons.rotarycannon;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;
import rbasamoyai.createbigcannons.cannons.ICannonBlockEntity;
import rbasamoyai.createbigcannons.cannons.ItemCannonBehavior;

/**
 * 转管炮方块实体的公共接口。
 *
 * <p><b>1.20.1 → 1.21.1</b>：{@code ICannonBlockEntity<T>} 与 {@code ItemCannonBehavior}
 * 签名未变，逐字搬入。
 */
public interface IRotarycannonBlockEntity extends ICannonBlockEntity<ItemCannonBehavior> {

    default void tickFromContraption(Level level, PitchOrientedContraptionEntity poce, BlockPos localPos) {}

    default List<ItemStack> getDrops() {
        List<ItemStack> list = new ArrayList<>();
        list.add(this.cannonBehavior().getItem());
        return list;
    }
}
