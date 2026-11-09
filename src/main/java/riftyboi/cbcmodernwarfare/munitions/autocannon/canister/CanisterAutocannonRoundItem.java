package riftyboi.cbcmodernwarfare.munitions.autocannon.canister;


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
import rbasamoyai.createbigcannons.munitions.FuzedItemMunition;
import rbasamoyai.createbigcannons.munitions.autocannon.AbstractAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonRoundItem;
import rbasamoyai.createbigcannons.munitions.autocannon.config.AutocannonProjectilePropertiesComponent;
import rbasamoyai.createbigcannons.munitions.fuzes.FuzeItem;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareEntityTypes;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareMunitionPropertiesHandlers;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonFuzes;

/**
 * 筒弹（canister）机炮弹丸物品（可装引信）。
 *
 * <p><b>1.20.1 → 1.21.1 的改动，逐条依据</b>：
 * <ul>
 *   <li><b>NBT → DataComponents</b>：1.20.1 用 {@code stack.getOrCreateTag().getCompound("Fuze")}
 *       读引信；1.21.1 物品栈不能再用裸 NBT 存这个东西，改用 CBC 的
 *       {@code CBCDataComponents.FUZE}（值类型 {@code ItemContainerContents}）。
 *       统一走 R5 已建好的 {@link MediumcannonFuzes#getFuze(ItemStack)}
 *       （内部即 {@code stack.getOrDefault(CBCDataComponents.FUZE, ItemContainerContents.EMPTY).copyOne()}）。
 *       依据：{@code docs/analysis/05-DataComponents迁移规范.md}。</li>
 *   <li><b>appendHoverText 签名</b>：1.20.1 第二参数是 {@code @Nullable Level}，
 *       1.21.1 变成 {@code Item.TooltipContext}（javap 确认）。子 tooltip 递归那行
 *       同样要传 {@code context}（1.20.1 传的是 level）。</li>
 *   <li>本类只<b>读</b>引信；写回发生在弹鼓/装填器（不在本轮范围）。</li>
 * </ul>
 */
public class CanisterAutocannonRoundItem extends AutocannonRoundItem implements FuzedItemMunition {

	public CanisterAutocannonRoundItem(Properties properties) {
		super(properties);
	}



	@Override
	public AbstractAutocannonProjectile getAutocannonProjectile(ItemStack stack, Level level) {
		CanisterAutocannonProjectile projectile = CBCModernWarfareEntityTypes.CANISTER_AUTOCANNON.create(level);
		// DataComponents 迁移：引信读 CBC 的 FUZE 组件，不再走原始 NBT
		ItemStack fuze = MediumcannonFuzes.getFuze(stack);
		if (!fuze.isEmpty()) {
			projectile.setFuze(fuze);
		}
		return projectile;
	}
	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
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
				subTooltip.replaceAll(sibling -> Component.literal("  ").append(sibling).withStyle(ChatFormatting.GRAY));
				tooltip.addAll(subTooltip);
			}
		}
	}

	@Nonnull
	@Override
	public AutocannonProjectilePropertiesComponent getAutocannonProperties(ItemStack itemStack) {
		return CBCModernWarfareMunitionPropertiesHandlers.CANISTER_AUTOCANNON.getPropertiesOf(this.getEntityType(itemStack)).autocannonProperties();
	}

	@Override
	public EntityType<?> getEntityType(ItemStack stack) {
		return CBCModernWarfareEntityTypes.CANISTER_AUTOCANNON.get();
	}

}
