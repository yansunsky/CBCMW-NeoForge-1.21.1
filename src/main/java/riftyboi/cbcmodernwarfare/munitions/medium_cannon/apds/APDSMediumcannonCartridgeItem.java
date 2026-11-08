package riftyboi.cbcmodernwarfare.munitions.medium_cannon.apds;

import net.minecraft.world.item.ItemStack;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonCartridgeItem;

public class APDSMediumcannonCartridgeItem extends MediumcannonCartridgeItem {
	public APDSMediumcannonCartridgeItem(Properties properties) {
		super(properties);
	}

	public ItemStack getCreativeTabCartridgeItem() {
		ItemStack stack = CBCModernWarfareItem.APDS_MEDIUMCANNON_CARTRIDGE.asStack();;
		MediumcannonCartridgeItem.writeProjectile(CBCModernWarfareItem.APDS_MEDIUMCANNON_ROUND.asStack(), stack);
		return stack;
	}
}
