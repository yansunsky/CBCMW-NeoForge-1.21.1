package riftyboi.cbcmodernwarfare.munitions.medium_cannon.apfsds;

import net.minecraft.world.item.ItemStack;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonCartridgeItem;

public class APFSDSMediumcannonCartridgeItem extends MediumcannonCartridgeItem {
	public APFSDSMediumcannonCartridgeItem(Properties properties) {
		super(properties);
	}

	public ItemStack getCreativeTabCartridgeItem() {
		ItemStack stack = CBCModernWarfareItem.APFSDS_MEDIUMCANNON_CARTRIDGE.asStack();;
		MediumcannonCartridgeItem.writeProjectile(CBCModernWarfareItem.APFSDS_MEDIUMCANNON_ROUND.asStack(), stack);
		return stack;
	}
}
