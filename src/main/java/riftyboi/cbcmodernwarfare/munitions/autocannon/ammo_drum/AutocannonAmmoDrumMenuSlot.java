package riftyboi.cbcmodernwarfare.munitions.autocannon.ammo_drum;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonAmmoType;

/**
 * 弹鼓的三个弹药槽。
 *
 * <p>1.20.1 → 1.21.1：{@code Slot#mayPlace} / {@code Slot#getMaxStackSize} 签名未变。
 */
public class AutocannonAmmoDrumMenuSlot extends Slot {

	private final IAutocannonAmmoDrumContainer ammoContainer;

	public AutocannonAmmoDrumMenuSlot(IAutocannonAmmoDrumContainer container, int slot, int x, int y) {
		super(container, slot, x, y);
		this.ammoContainer = container;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		AutocannonAmmoType placeType = AutocannonAmmoType.of(stack);
		AutocannonAmmoType type = this.ammoContainer.getAmmoType();
		// 空桶：任意合法弹药都能放；非空桶：种类必须一致，且总量未到上限（容量 × 3）。
		return (placeType != AutocannonAmmoType.NONE && type == AutocannonAmmoType.NONE)
				|| (placeType == type && this.ammoContainer.getTotalCount() < type.getCapacity() * 3);
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		AutocannonAmmoType ctType = this.ammoContainer.getAmmoType();
		if (ctType == AutocannonAmmoType.NONE) {
			return AutocannonAmmoType.of(stack).getCapacity();
		}
		int buf = Math.max(ctType.getCapacity() * 3 - this.ammoContainer.getTotalCount(), 0);
		ItemStack item = this.ammoContainer.getItem(this.getContainerSlot());
		return Math.min(item.getCount() + buf, item.getMaxStackSize());
	}
}
