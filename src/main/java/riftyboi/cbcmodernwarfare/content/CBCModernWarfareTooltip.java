package riftyboi.cbcmodernwarfare.content;

import java.util.List;

import javax.annotation.Nullable;

import com.simibubi.create.content.equipment.goggles.GogglesItem;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.CreateLang;

import net.createmod.catnip.lang.FontHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import rbasamoyai.createbigcannons.CreateBigCannons;

import riftyboi.cbcmodernwarfare.cannons.medium_cannon.MediumcannonBlock;
import riftyboi.cbcmodernwarfare.cannons.medium_cannon.material.MediumcannonMaterial;

/**
 * 方块物品的悬停说明（「按住 Shift 看详情」那一套）。
 *
 * <p><b>1.20.1 → 1.21.1</b>：本类内部用到的 Create API 全部未改名 ——
 * {@code GogglesItem.isWearingGoggles(Player)}、{@code TooltipHelper.makeProgressBar(int,int)}、
 * {@code CreateLang.translateDirect(String, Object...)}、{@code FontHelper.cutStringTextComponent(...)}
 * 都经 javap 核对过。
 *
 * <p><b>本轮只迁中型炮这一支</b>：{@code appendLauncherBlockText} 与
 * {@code appendTextRotarycannon} 依赖 {@code cannons/munitions_contraption_launcher} 与
 * {@code cannons/rotarycannon}，随 R4b 一起加。
 *
 * <p>注意：本类引用了客户端专有类型（{@code Screen}、{@code Minecraft}），
 * 但只在 {@code Item#appendHoverText} 里被调用，而那个方法在服务端不会被触发。
 * 1.20.1 也把它放在 {@code common/} 下，行为一致。
 */
public class CBCModernWarfareTooltip {

    private static final Style PRIMARY = FontHelper.Palette.GRAY_AND_WHITE.primary();
    private static final Style HIGHLIGHT = FontHelper.Palette.GRAY_AND_WHITE.highlight();

    private CBCModernWarfareTooltip() {}

    /** 「按住 Shift 查看详情」这一行。 */
    private static void addHoldShift(boolean desc, List<Component> tooltip) {
        String[] holdDesc = CreateLang.translateDirect("tooltip.holdForDescription", "$").getString().split("\\$");
        if (holdDesc.length < 2) {
            return;
        }
        Component keyShift = CreateLang.translateDirect("tooltip.keyShift");
        MutableComponent tabBuilder = Component.literal("");
        tabBuilder.append(Component.literal(holdDesc[0]).withStyle(ChatFormatting.DARK_GRAY));
        tabBuilder.append(keyShift.plainCopy().withStyle(desc ? ChatFormatting.WHITE : ChatFormatting.GRAY));
        tabBuilder.append(Component.literal(holdDesc[1]).withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(tabBuilder);
    }

    /** 没戴工程师护目镜时，用五格进度条代替精确数值。 */
    private static Component getNoGogglesMeter(int outOfFive, boolean invertColor, boolean canBeInvalid) {
        int value = invertColor ? 5 - outOfFive : outOfFive;
        ChatFormatting color = switch (value) {
            case 0, 1 -> ChatFormatting.RED;
            case 2, 3 -> ChatFormatting.GOLD;
            case 4, 5 -> ChatFormatting.YELLOW;
            default -> canBeInvalid ? ChatFormatting.DARK_GRAY : value < 0 ? ChatFormatting.RED : ChatFormatting.YELLOW;
        };
        return Component.literal(" " + TooltipHelper.makeProgressBar(5, outOfFive)).withStyle(color);
    }

    public static FontHelper.Palette getPalette(Level level, ItemStack stack) {
        return FontHelper.Palette.STANDARD_CREATE;
    }

    /** 中型炮方块物品的说明：炮管长度上限与重量影响。 */
    public static <T extends Block & MediumcannonBlock> void appendTextMediumcannon(ItemStack stack,
            @Nullable Level level, List<Component> tooltip, TooltipFlag flag, T block) {
        boolean desc = Screen.hasShiftDown();
        addHoldShift(desc, tooltip);
        if (!desc) {
            return;
        }

        FontHelper.Palette palette = getPalette(level, stack);
        MediumcannonMaterial material = block.getMediumcannonMaterial();
        Minecraft mc = Minecraft.getInstance();
        boolean hasGoggles = GogglesItem.isWearingGoggles(mc.player);
        String rootKey = "block." + CreateBigCannons.MOD_ID + ".autocannon.tooltip";
        tooltip.add(Component.literal(I18n.get(rootKey + ".materialProperties")).withStyle(ChatFormatting.GRAY));

        int maxLength = material.properties().maxBarrelLength();
        tooltip.add(Component.literal(" " + I18n.get(rootKey + ".maxBarrelLength")).withStyle(ChatFormatting.GRAY));
        if (hasGoggles) {
            tooltip.addAll(FontHelper.cutStringTextComponent(
                    I18n.get(rootKey + ".maxBarrelLength.goggles", maxLength + 1),
                    palette.primary(), palette.highlight(), 2));
        } else {
            tooltip.add(getNoGogglesMeter(maxLength == 0 ? 0 : (maxLength - 1) / 2 + 1, false, true));
        }

        tooltip.add(Component.literal(" " + I18n.get(rootKey + ".weightImpact")).withStyle(ChatFormatting.GRAY));
        float weightImpact = material.properties().weight();
        if (hasGoggles) {
            tooltip.addAll(FontHelper.cutStringTextComponent(
                    I18n.get(rootKey + ".weightImpact.goggles", String.format("%.2f", weightImpact)),
                    palette.primary(), palette.highlight(), 2));
        } else {
            tooltip.add(getNoGogglesMeter(weightImpact < 1d ? 0 : Mth.ceil(weightImpact), true, true));
        }
    }
}
