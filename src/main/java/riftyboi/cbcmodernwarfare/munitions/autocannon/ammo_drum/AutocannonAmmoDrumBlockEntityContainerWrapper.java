package riftyboi.cbcmodernwarfare.munitions.autocannon.ammo_drum;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * 把方块实体包装成容器（客户端菜单用）。
 *
 * <p><b>为什么需要它</b>：客户端拿到的是 {@code BlockEntity}，它可能为 {@code null}
 * （方块已被破坏）或类型不对，而这个包装类把两种情况都吸收成「空容器」，
 * 菜单代码就不必到处判空。
 */
public class AutocannonAmmoDrumBlockEntityContainerWrapper implements IAutocannonAmmoDrumContainer {

	@Nullable
	private final AutocannonAmmoDrumBlockEntity be;
	private final BlockPos pos;

	public AutocannonAmmoDrumBlockEntityContainerWrapper(@Nullable AutocannonAmmoDrumBlockEntity be, BlockPos pos) {
		this.be = be;
		this.pos = pos;
	}

	@Override
	public ItemStack getMainAmmoStack() {
		return this.be == null ? ItemStack.EMPTY : this.be.getMainAmmoStack();
	}

	@Override
	public ItemStack getSecondaryAmmoStack() {
		return this.be == null ? ItemStack.EMPTY : this.be.getSecondaryAmmoStack();
	}

	@Override
	public ItemStack getTracerStack() {
		return this.be == null ? ItemStack.EMPTY : this.be.getTracerStack();
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		return this.be == null ? ItemStack.EMPTY : this.be.removeItem(slot, amount);
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		return this.be == null ? ItemStack.EMPTY : this.be.removeItemNoUpdate(slot);
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		if (this.be != null) {
			this.be.setItem(slot, stack);
		}
	}

	@Override
	public void setChanged() {
		if (this.be != null) {
			this.be.setChanged();
		}
	}

	@Override
	public void clearContent() {
		if (this.be != null) {
			this.be.clearContent();
		}
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return this.be != null && this.be.canPlaceItem(index, stack);
	}

	@Override
	public boolean stillValid(Player player) {
		return this.pos.closerThan(player.blockPosition(), 4.0);
	}
}
