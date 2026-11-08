package riftyboi.cbcmodernwarfare.cannons.rotarycannon;

import javax.annotation.Nullable;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.List;

import riftyboi.cbcmodernwarfare.cannons.rotarycannon.material.RotarycannonMaterial;
import riftyboi.cbcmodernwarfare.content.CBCModernWarfareTooltip;

/**
 * 转管炮方块对应的物品。
 *
 * <p><b>1.20.1 → 1.21.1</b>：{@code Item#appendHoverText} 的第二个参数由 {@code Level}
 * 变为 {@code Item.TooltipContext}，其 {@code level()} 可能为 null
 * （创造模式搜索栏里没有世界），所以传下去之前要判空 —— 与本项目的
 * {@code MediumcannonBlockItem} 同一处理方式。
 */
public class RotarycannonBlockItem<T extends Block & RotarycannonBlock> extends BlockItem {

    private final T rotarycannonBlock;

    public RotarycannonBlockItem(T block, Properties properties) {
        super(block, properties);
        this.rotarycannonBlock = block;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        CBCModernWarfareTooltip.appendTextRotarycannon(stack, context.level(), tooltip, flag, this.rotarycannonBlock);
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        InteractionResult result = super.place(context);
        Player player = context.getPlayer();
        RotarycannonMaterial material = this.rotarycannonBlock.getRotarycannonMaterial();
        if (player != null && (material.properties().connectsInSurvival() || player.isCreative())) {
            RotarycannonBlock.onPlace(context.getLevel(), context.getClickedPos());
        }
        return result;
    }

    /** 未使用，保持与 1.20.1 相同的类形状。 */
    @SuppressWarnings("unused")
    @Nullable
    private static String unused(@Nullable String s) {
        return s;
    }
}
