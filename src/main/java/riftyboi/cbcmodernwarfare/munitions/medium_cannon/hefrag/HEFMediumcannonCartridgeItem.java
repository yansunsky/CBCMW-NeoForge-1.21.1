package riftyboi.cbcmodernwarfare.munitions.medium_cannon.hefrag;

import net.minecraft.world.item.ItemStack;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonCartridgeItem;

public class HEFMediumcannonCartridgeItem extends MediumcannonCartridgeItem {
	public HEFMediumcannonCartridgeItem(Properties properties) {
		super(properties);
	}

	public ItemStack getCreativeTabCartridgeItem() {
		ItemStack stack = CBCModernWarfareItem.HEF_MEDIUMCANNON_CARTRIDGE.asStack();;
		MediumcannonCartridgeItem.writeProjectile(CBCModernWarfareItem.HEF_MEDIUMCANNON_ROUND.asStack(), stack);
		return stack;
	}

}
