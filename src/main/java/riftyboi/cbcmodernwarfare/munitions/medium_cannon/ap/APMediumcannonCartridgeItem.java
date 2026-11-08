package riftyboi.cbcmodernwarfare.munitions.medium_cannon.ap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonCartridgeItem;

public class APMediumcannonCartridgeItem extends MediumcannonCartridgeItem {
	public APMediumcannonCartridgeItem(Properties properties) {
		super(properties);
	}

	public ItemStack getCreativeTabCartridgeItem(){
		ItemStack stack = CBCModernWarfareItem.AP_MEDIUMCANNON_CARTRIDGE.asStack();
		MediumcannonCartridgeItem.writeProjectile(stack, this.getDefaultInstance());
		MediumcannonCartridgeItem.writeProjectile(CBCModernWarfareItem.AP_MEDIUMCANNON_ROUND.asStack(), stack);
		return stack;
	}

}
