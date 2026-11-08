package riftyboi.cbcmodernwarfare.munitions.medium_cannon.canister;

import net.minecraft.world.item.ItemStack;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonCartridgeItem;

public class CanisterMediumcannonCartridgeItem extends MediumcannonCartridgeItem {
	public CanisterMediumcannonCartridgeItem(Properties properties) {
		super(properties);
	}

	public ItemStack getCreativeTabCartridgeItem() {
		ItemStack stack = CBCModernWarfareItem.CANISTER_MEDIUMCANNON_CARTRIDGE.asStack();;
		MediumcannonCartridgeItem.writeProjectile(CBCModernWarfareItem.CANISTER_MEDIUMCANNON_ROUND.asStack(), stack);
		return stack;
	}
}
