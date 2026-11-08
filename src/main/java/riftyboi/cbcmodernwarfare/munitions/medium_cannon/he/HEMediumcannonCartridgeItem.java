package riftyboi.cbcmodernwarfare.munitions.medium_cannon.he;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonCartridgeItem;

public class HEMediumcannonCartridgeItem extends MediumcannonCartridgeItem {
	public HEMediumcannonCartridgeItem(Properties properties) {
		super(properties);
	}

	public ItemStack getCreativeTabCartridgeItem() {
		ItemStack stack = CBCModernWarfareItem.HE_MEDIUMCANNON_CARTRIDGE.asStack();;
		MediumcannonCartridgeItem.writeProjectile(CBCModernWarfareItem.HE_MEDIUMCANNON_ROUND.asStack(), stack);
		return stack;
	}

}
