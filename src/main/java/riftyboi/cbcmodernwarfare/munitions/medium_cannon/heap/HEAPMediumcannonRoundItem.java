package riftyboi.cbcmodernwarfare.munitions.medium_cannon.heap;


import net.createmod.catnip.lang.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.munitions.fuzes.FuzeItem;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareEntityTypes;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareItem;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareMunitionPropertiesHandlers;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.FuzedMediumItemMunition;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.AbstractMediumcannonProjectile;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonFuzes;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonRoundItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.config.MediumcannonProjectilePropertiesComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HEAPMediumcannonRoundItem extends MediumcannonRoundItem implements FuzedMediumItemMunition {

	public HEAPMediumcannonRoundItem(Properties properties) {
		super(properties);
	}

	@Override
	public AbstractMediumcannonProjectile getMediumcannonProjectile(ItemStack stack, Level level) {
		HEAPMediumcannonProjectile projectile = CBCModernWarfareEntityTypes.HEAP_ROUND.create(level);
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
	public MediumcannonProjectilePropertiesComponent getMediumcannonProperties(ItemStack itemStack) {
		return CBCModernWarfareMunitionPropertiesHandlers.HEAP_MEDIUMCANNON.getPropertiesOf(this.getEntityType(itemStack)).mediumcannonProperties();
	}

	@Override
	public EntityType<?> getEntityType(ItemStack stack) {
		return CBCModernWarfareEntityTypes.HEAP_ROUND.get();
	}
	@Override public ItemStack getCartridgeType() { return CBCModernWarfareItem.HEAP_MEDIUMCANNON_CARTRIDGE.asStack(); }
}
