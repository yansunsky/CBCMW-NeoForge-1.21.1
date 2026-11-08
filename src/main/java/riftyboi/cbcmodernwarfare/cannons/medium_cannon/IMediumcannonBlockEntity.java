package riftyboi.cbcmodernwarfare.cannons.medium_cannon;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;
import rbasamoyai.createbigcannons.cannons.ICannonBlockEntity;
import rbasamoyai.createbigcannons.cannons.ItemCannonBehavior;

/**
 * 中型炮方块实体的公共接口。
 *
 * <p><b>1.20.1 → 1.21.1</b>：{@code ICannonBlockEntity<T>} 与 {@code ItemCannonBehavior}
 * 都还在原包下，签名未变（javap 于 createbigcannons 5.11.7 核对）。
 */
public interface IMediumcannonBlockEntity extends ICannonBlockEntity<ItemCannonBehavior> {

    /**
     * 在 contraption 内每 tick 调用一次。
     *
     * <p>默认空实现：只有带动画的部件（炮闩、后坐炮管）才覆盖它。
     */
    default void tickFromContraption(Level level, PitchOrientedContraptionEntity poce, BlockPos localPos) {}

    /** 方块被破坏时要掉落的内容物，默认就是炮膛里那一发。 */
    default List<ItemStack> getDrops() {
        List<ItemStack> list = new ArrayList<>();
        list.add(this.cannonBehavior().getItem());
        return list;
    }
}
