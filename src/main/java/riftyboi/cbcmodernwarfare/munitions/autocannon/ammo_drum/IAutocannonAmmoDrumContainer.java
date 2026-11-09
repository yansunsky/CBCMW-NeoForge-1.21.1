package riftyboi.cbcmodernwarfare.munitions.autocannon.ammo_drum;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonAmmoType;

/**
 * 机炮弹鼓的容器契约（方块与物品两种载体共用）。
 *
 * <p>三个槽位的语义与 1.20.1 一致：
 * <ul>
 *   <li>槽 0 = 主弹药</li>
 *   <li>槽 1 = 副弹药</li>
 *   <li>槽 2 = 曳光弹</li>
 * </ul>
 *
 * <p>1.20.1 → 1.21.1 无签名变化（{@link AutocannonAmmoType} 及其
 * {@code of}/{@code getCapacity}/{@code isValidMunition} 都未变，已 javap 核实）。
 */
public interface IAutocannonAmmoDrumContainer extends Container {

	int AMMO_SLOT = 0;
	int AMMO_SLOT1 = 1;
	int TRACER_SLOT = 2;

	ItemStack getMainAmmoStack();

	ItemStack getSecondaryAmmoStack();

	ItemStack getTracerStack();

	default int getMainAmmoCapacity() {
		int remainder = Math.max(0, this.getAmmoType().getCapacity() * 3 - this.getTotalCount());
		ItemStack stack = this.getMainAmmoStack();
		return Math.min(stack.getCount() + remainder, stack.getMaxStackSize());
	}

	default int getSecondaryAmmoCapacity() {
		int remainder = Math.max(0, this.getAmmoType().getCapacity() * 3 - this.getTotalCount());
		ItemStack stack = this.getSecondaryAmmoStack();
		return Math.min(stack.getCount() + remainder, stack.getMaxStackSize());
	}

	default int getTracerAmmoCapacity() {
		int remainder = Math.max(0, this.getAmmoType().getCapacity() * 3 - this.getTotalCount());
		ItemStack stack = this.getTracerStack();
		return Math.min(stack.getCount() + remainder, stack.getMaxStackSize());
	}

	default int getTotalCount() {
		return this.getMainAmmoStack().getCount() + this.getSecondaryAmmoStack().getCount()
				+ this.getTracerStack().getCount();
	}

	/** 以第一个非空的槽位决定这桶弹药的种类。 */
	default AutocannonAmmoType getAmmoType() {
		AutocannonAmmoType type = AutocannonAmmoType.of(this.getMainAmmoStack());
		AutocannonAmmoType type1 = AutocannonAmmoType.of(this.getSecondaryAmmoStack());
		return type != AutocannonAmmoType.NONE
				? type
				: (type1 != AutocannonAmmoType.NONE ? type1 : AutocannonAmmoType.of(this.getTracerStack()));
	}

	@Override
	default int getContainerSize() {
		return 3;
	}

	@Override
	default boolean isEmpty() {
		return this.getMainAmmoStack().isEmpty() && this.getSecondaryAmmoStack().isEmpty()
				&& this.getTracerStack().isEmpty();
	}

	@Override
	default ItemStack getItem(int slot) {
		return switch (slot) {
			case 0 -> this.getMainAmmoStack();
			case 1 -> this.getSecondaryAmmoStack();
			case 2 -> this.getTracerStack();
			default -> ItemStack.EMPTY;
		};
	}

	@Override
	default boolean canPlaceItem(int index, ItemStack stack) {
		if (index != 0 && index != 1 && index != 2) {
			return false;
		}
		AutocannonAmmoType ammoType = this.getAmmoType();
		if (!ammoType.isValidMunition(stack)) {
			return false;
		}
		// 空桶时按将要放入的弹药类型判容量；非空时按当前桶的剩余容量判。
		int currentCapacity;
		if (ammoType == AutocannonAmmoType.NONE) {
			currentCapacity = AutocannonAmmoType.of(stack).getCapacity();
		} else {
			currentCapacity = switch (index) {
				case 0 -> this.getMainAmmoCapacity();
				case 1 -> this.getSecondaryAmmoCapacity();
				default -> this.getTracerAmmoCapacity();
			};
			currentCapacity -= this.getItem(index).getCount();
		}
		return currentCapacity > 0;
	}
}
