package riftyboi.cbcmodernwarfare.munitions.autocannon.ammo_drum;

import javax.annotation.Nullable;

import net.minecraft.world.inventory.ContainerData;

/**
 * 方块版弹鼓的同步数据（只同步「曳光间隔」一个值）。
 *
 * <p>1.20.1 → 1.21.1：{@link ContainerData} 接口未变。
 */
public class AutocannonAmmoDrumServerData implements ContainerData {

	@Nullable
	private final AutocannonAmmoDrumBlockEntity be;

	public AutocannonAmmoDrumServerData(@Nullable AutocannonAmmoDrumBlockEntity be) {
		this.be = be;
	}

	@Override
	public int get(int index) {
		return this.be != null && index == 0 ? this.be.getSpacing() : 1;
	}

	@Override
	public void set(int index, int value) {
		if (this.be != null && index == 0) {
			this.be.setSpacing(value);
		}
	}

	@Override
	public int getCount() {
		return 1;
	}
}
