package riftyboi.cbcmodernwarfare.munitions.autocannon.ammo_drum;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Pair;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.base.ItemStackServerData;
import rbasamoyai.createbigcannons.base.SimpleValueContainer;
import rbasamoyai.createbigcannons.index.CBCDataComponents;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonAmmoItem;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonAmmoType;

import riftyboi.cbcmodernwarfare.index.CBCModernWarfareMenuTypes;

/**
 * 弹鼓界面容器：3 个弹药槽 + 玩家背包。
 *
 * <p><b>1.21.1 的关键差异（经 javap 核对 Registrate 与 CBC 字节码）</b>：
 * Registrate 的 {@code menu(...)} 在 1.21.1 必须用
 * {@link com.tterrag.registrate.builders.MenuBuilder.ForgeMenuFactory} ——
 * 它的签名是 {@code create(MenuType, int, Inventory, RegistryFriendlyByteBuf)}，
 * <b>比 1.20.1 多一个 buf 参数</b>。所以 {@link #getClientMenu} 也跟着多一个 buf 形参，
 * 由 Registrate 在客户端调用时注入。
 */
public class AutocannonAmmoDrumMenu extends AbstractContainerMenu implements SimpleValueContainer {

	private static final ResourceLocation TRACER_SLOT = CreateBigCannons.resource("item/tracer_slot");

	/**
	 * 曳光槽在这个菜单 {@code slots} 列表里的下标。
	 *
	 * <p>三个弹药槽是按 0/1/2 的顺序加进 {@code slots} 的，所以槽位下标与容器槽号一致。
	 * 单独开一个常量给客户端 Screen 用（Screen 不该直接依赖容器接口的常量语义）。
	 */
	public static final int TRACER_SLOT_INDEX = 2;

	private final IAutocannonAmmoDrumContainer container;
	private final ContainerData data;
	private final Inventory playerInv;
	private final boolean isItem;

	public static AutocannonAmmoDrumMenu getServerMenuForItemStack(int id, Inventory playerInv, ItemStack stack) {
		IAutocannonAmmoDrumContainer ct = new AutocannonAmmoDrumItemContainer(stack);
		return new AutocannonAmmoDrumMenu(CBCModernWarfareMenuTypes.AUTOCANNON_AMMO_DRUM.get(), id, playerInv, ct,
				new ItemStackServerData(stack, CBCDataComponents.TRACER_SPACING), true);
	}

	public static AutocannonAmmoDrumMenu getServerMenuForBlockEntity(int id, Inventory playerInv,
			AutocannonAmmoDrumBlockEntity be) {
		return new AutocannonAmmoDrumMenu(CBCModernWarfareMenuTypes.AUTOCANNON_AMMO_DRUM.get(), id, playerInv, be,
				new AutocannonAmmoDrumServerData(be), false);
	}

	/**
	 * 客户端构造。1.21.1 的 {@code ForgeMenuFactory} 会把这个 buf 传进来。
	 */
	public static AutocannonAmmoDrumMenu getClientMenu(MenuType<AutocannonAmmoDrumMenu> type, int id,
			Inventory playerInv, RegistryFriendlyByteBuf buf) {
		ContainerData data = new SimpleContainerData(1);
		data.set(0, buf.readVarInt());
		boolean isBlock = buf.readBoolean();
		IAutocannonAmmoDrumContainer ct;
		if (isBlock) {
			BlockPos pos = buf.readBlockPos();
			BlockEntity be = playerInv.player.level().getBlockEntity(pos);
			ct = new AutocannonAmmoDrumBlockEntityContainerWrapper(
					be instanceof AutocannonAmmoDrumBlockEntity abe ? abe : null, pos);
		} else {
			ct = new AutocannonAmmoDrumItemContainer(ItemStack.OPTIONAL_STREAM_CODEC.decode(buf));
		}
		return new AutocannonAmmoDrumMenu(type, id, playerInv, ct, data, !isBlock);
	}

	protected AutocannonAmmoDrumMenu(MenuType<? extends AutocannonAmmoDrumMenu> type, int id, Inventory playerInv,
			IAutocannonAmmoDrumContainer ct, ContainerData data, boolean isItem) {
		super(type, id);
		this.addSlot(new AutocannonAmmoDrumMenuSlot(ct, IAutocannonAmmoDrumContainer.AMMO_SLOT, 32, 26));
		this.addSlot(new AutocannonAmmoDrumMenuSlot(ct, IAutocannonAmmoDrumContainer.AMMO_SLOT1, 5, 26));
		this.addSlot(new AutocannonAmmoDrumMenuSlot(ct, IAutocannonAmmoDrumContainer.TRACER_SLOT, 59, 26) {
			@Nullable
			@Override
			public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
				return Pair.of(InventoryMenu.BLOCK_ATLAS, AutocannonAmmoDrumMenu.TRACER_SLOT);
			}
		});

		int add = 8;
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				this.addSlot(new Slot(playerInv, row * 9 + col + 9, col * 18 + add, row * 18 + 105));
			}
		}
		for (int i = 0; i < 9; i++) {
			this.addSlot(new Slot(playerInv, i, i * 18 + add, 163));
		}

		this.addDataSlots(data);
		this.data = data;
		this.container = ct;
		this.playerInv = playerInv;
		this.isItem = isItem;
		this.container.startOpen(playerInv.player);
	}

	@Override
	public boolean stillValid(Player player) {
		return this.container.stillValid(player);
	}

	/**
	 * 当前曳光间隔。
	 *
	 * <p>注意<b>不能加 {@code @Override}</b>：1.21.1 的 {@code SimpleValueContainer}
	 * 只剩 {@code setValue(int)} 一个方法（1.20.1 的 {@code getValue()} 已被移出接口，
	 * 已 javap 核实）。这里保留成普通方法，供客户端 {@code Screen} 读取初值。
	 */
	public int getValue() {
		return this.data.get(0);
	}

	@Override
	public void setValue(int value) {
		this.data.set(0, value);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack itemStack2 = slot.getItem();
			itemStack = itemStack2.copy();
			if (index < 3) {
				if (!this.moveItemStackTo(itemStack2, 3, 38, true)) {
					return ItemStack.EMPTY;
				}
			} else {
				if (!(itemStack2.getItem() instanceof AutocannonAmmoItem ammoItem)) {
					return ItemStack.EMPTY;
				}
				AutocannonAmmoType ammoType = ammoItem.getType();
				if (ammoType == AutocannonAmmoType.NONE) {
					return ItemStack.EMPTY;
				}
				AutocannonAmmoType ctType = this.container.getAmmoType();
				int buf = ctType == AutocannonAmmoType.NONE
						? ammoType.getCapacity()
						: Math.max(ctType.getCapacity() * 2 - this.container.getTotalCount(), 0);
				if (buf < 1) {
					return ItemStack.EMPTY;
				}
				int insertIndex = ammoItem.isTracer(itemStack2)
						? IAutocannonAmmoDrumContainer.TRACER_SLOT
						: (this.container.getMainAmmoCapacity() == this.container.getMainAmmoStack().getCount()
								? IAutocannonAmmoDrumContainer.AMMO_SLOT
								: IAutocannonAmmoDrumContainer.AMMO_SLOT1);
				Slot insertSlot = this.slots.get(insertIndex);
				if (insertSlot != null) {
					insertSlot.safeInsert(itemStack2);
				}
			}

			if (itemStack2.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
			if (itemStack2.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}
			slot.onTake(player, itemStack2);
		}
		return itemStack;
	}

	public boolean isFilled() {
		return this.container.getAmmoType() != AutocannonAmmoType.NONE;
	}

	@Override
	public void clicked(int slotId, int button, ClickType clickType, Player player) {
		// 物品版：禁止把正在用的那个弹鼓自己拖出去（slot 编号随选中格变化）。
		if (slotId != this.playerInv.selected + 29 || clickType == ClickType.THROW || !this.isItem) {
			super.clicked(slotId, button, clickType, player);
		}
	}

	public IAutocannonAmmoDrumContainer getContainer() {
		return this.container;
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		this.container.stopOpen(player);
	}
}
