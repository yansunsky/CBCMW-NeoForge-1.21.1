package riftyboi.cbcmodernwarfare.munitions.medium_cannon.smoke;

import net.minecraft.world.item.ItemStack;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonCartridgeItem;

public class SmokeMediumcannonCartridgeItem extends MediumcannonCartridgeItem {
	public SmokeMediumcannonCartridgeItem(Properties properties) {
		super(properties);
	}

	public ItemStack getCreativeTabCartridgeItem() {
		ItemStack stack = CBCModernWarfareItem.SMOKE_MEDIUMCANNON_CARTRIDGE.asStack();
		writeProjectile(CBCModernWarfareItem.SMOKE_MEDIUMCANNON_ROUND.asStack(), stack);
		return stack;
	}
}
