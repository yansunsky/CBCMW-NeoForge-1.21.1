package riftyboi.cbcmodernwarfare.munitions.smoke_discharger;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.createmod.catnip.lang.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.munitions.fuzes.FuzeItem;

import riftyboi.cbcmodernwarfare.index.CBCModernWarfareEntityTypes;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareMunitionPropertiesHandlers;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonFuzes;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.config.MediumcannonProjectilePropertiesComponent;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.smoke.SmokeMediumcannonProjectileProperties;

/**
 * 烟雾排放器的弹药（{@code smoke_discharger_round}）。
 *
 * <p><b>1.20.1 → 1.21.1</b>：
 * <ul>
 *   <li>{@code appendHoverText} 第二参数 {@code Level} → {@code Item.TooltipContext}；
 *       子 tooltip 递归也传 {@code context}。</li>
 *   <li>引信读 {@code tag.getCompound("Fuze")} → {@link MediumcannonFuzes#getFuze}
 *       （CBC 的 FUZE 组件）。</li>
 *   <li>{@code Lang.builder(...).translate(...)} 的调用形态未变。</li>
 * </ul>
 *
 * <p><b>注意 getMediumcannonProperties 读的是 SMOKE_MEDIUMCANNON</b> ——
 * 这是 1.20.1 原样（烟雾排放器借用中型炮烟雾弹的属性），不是笔误。
 */
public class SmokeDischargerRoundItem extends Item {

	public SmokeDischargerRoundItem(Properties properties) {
		super(properties);
	}

	public SmokeDischargerProjectile getLauncherProjectile(ItemStack stack, Level level) {
		SmokeDischargerProjectile projectile = CBCModernWarfareEntityTypes.SMOKE_GRENADE.create(level);
		ItemStack fuze = MediumcannonFuzes.getFuze(stack);
		if (!fuze.isEmpty()) {
			projectile.setFuze(fuze);
		}
		return projectile;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip,
			TooltipFlag flag) {
		super.appendHoverText(stack, context, tooltip, flag);
		ItemStack fuze = MediumcannonFuzes.getFuze(stack);
		if (!fuze.isEmpty()) {
			Lang.builder("block")
					.translate(CreateBigCannons.MOD_ID + ".shell.tooltip.fuze")
					.add(Component.literal(" "))
					.add(fuze.getDisplayName().copy())
					.addTo(tooltip);
			if (fuze.getItem() instanceof FuzeItem) {
				List<Component> subTooltip = new ArrayList<>();
				fuze.getItem().appendHoverText(fuze, context, subTooltip, flag);
				subTooltip.replaceAll(sibling -> Component.literal("  ").append(sibling)
						.withStyle(ChatFormatting.GRAY));
				tooltip.addAll(subTooltip);
			}
		}
	}

	@Nonnull
	public MediumcannonProjectilePropertiesComponent getMediumcannonProperties(ItemStack itemStack) {
		return CBCModernWarfareMunitionPropertiesHandlers.SMOKE_MEDIUMCANNON
				.getPropertiesOf(this.getEntityType(itemStack)).mediumcannonProperties();
	}

	public EntityType<?> getEntityType(ItemStack stack) {
		return CBCModernWarfareEntityTypes.SMOKE_ROUND.get();
	}
}
