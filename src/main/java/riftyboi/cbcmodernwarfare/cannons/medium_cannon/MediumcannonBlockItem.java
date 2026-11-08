package riftyboi.cbcmodernwarfare.cannons.medium_cannon;

import javax.annotation.Nullable;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import riftyboi.cbcmodernwarfare.cannons.medium_cannon.material.MediumcannonMaterial;
import riftyboi.cbcmodernwarfare.content.CBCModernWarfareTooltip;

import java.util.List;

/**
 * 中型炮方块对应的物品：负责悬停说明，并在放置时按材质决定是否立即连接邻节。
 *
 * <p><b>1.20.1 → 1.21.1 的改动</b>：{@code Item#appendHoverText} 的第二个参数
 * 从 {@code Level} 变成了 {@code Item.TooltipContext}（1.21 的新签名）：
 * <pre>
 * 1.20.1: appendHoverText(ItemStack, Level, List&lt;Component&gt;, TooltipFlag)
 * 1.21.1: appendHoverText(ItemStack, Item.TooltipContext, List&lt;Component&gt;, TooltipFlag)
 * </pre>
 * {@code TooltipContext#level()} 可能为 null（例如在创造模式搜索栏里），所以传下去之前要判空。
 */
public class MediumcannonBlockItem<T extends Block & MediumcannonBlock> extends BlockItem {

    private final T mediumcannonBlock;

    public MediumcannonBlockItem(T block, Properties properties) {
        super(block, properties);
        this.mediumcannonBlock = block;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        CBCModernWarfareTooltip.appendTextMediumcannon(stack, context.level(), tooltip, flag, this.mediumcannonBlock);
    }

    /**
     * 放置方块。
     *
     * <p>与 1.20.1 一致：先在（客户端预测的）位置上调用 {@code super.place}，
     * 再按材质决定要不要触发相邻连接。{@code connectsInSurvival} 为 false 的材质
     * （本项目四种材质都是 false）只有在创造模式放置时才会自动连接。
     */
    @Override
    public InteractionResult place(BlockPlaceContext context) {
        InteractionResult result = super.place(context);
        Player player = context.getPlayer();
        MediumcannonMaterial material = this.mediumcannonBlock.getMediumcannonMaterial();
        if (player != null && (material.properties().connectsInSurvival() || player.isCreative())) {
            MediumcannonBlock.onPlace(context.getLevel(), context.getClickedPos());
        }
        return result;
    }

    /** 未使用的辅助：1.20.1 版本里由父类隐式提供，这里保留以备 R4b 装配态显示名称用。 */
    @Nullable
    static String customName(ItemStack stack) {
        Component name = stack.get(DataComponents.CUSTOM_NAME);
        return name == null ? null : name.getString();
    }
}
