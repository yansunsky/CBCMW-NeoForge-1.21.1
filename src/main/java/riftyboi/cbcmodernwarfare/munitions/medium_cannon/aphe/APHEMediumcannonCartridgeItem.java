package riftyboi.cbcmodernwarfare.munitions.medium_cannon.aphe;

import net.minecraft.world.item.ItemStack;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonCartridgeItem;

public class APHEMediumcannonCartridgeItem extends MediumcannonCartridgeItem {
	public APHEMediumcannonCartridgeItem(Properties properties) {
		super(properties);
	}

	public ItemStack getCreativeTabCartridgeItem() {
		ItemStack stack = CBCModernWarfareItem.APHE_MEDIUMCANNON_CARTRIDGE.asStack();;
		MediumcannonCartridgeItem.writeProjectile(CBCModernWarfareItem.APHE_MEDIUMCANNON_ROUND.asStack(), stack);
		return stack;
	}
}
