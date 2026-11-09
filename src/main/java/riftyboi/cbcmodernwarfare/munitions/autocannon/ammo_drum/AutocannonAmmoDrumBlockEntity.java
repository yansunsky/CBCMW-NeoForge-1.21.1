package riftyboi.cbcmodernwarfare.munitions.autocannon.ammo_drum;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;

import riftyboi.cbcmodernwarfare.index.CBCModernWarfareBlocks;

/**
 * 机炮弹鼓的方块实体：三个槽位（主弹药 / 副弹药 / 曳光弹）+ 曳光间隔。
 *
 * <p><b>1.20.1 → 1.21.1 的变化（全部经 javap 核实）</b>：
 * <ul>
 *   <li>{@code saveAdditional}/{@code loadAdditional} 现在都多一个
 *       {@link Provider} 参数。</li>
 *   <li>{@code ItemStack.save(...)} 要传 {@code Provider}、
 *       {@code ItemStack.of(CompoundTag)} 已改为
 *       {@code ItemStack.parseOptional(Provider, CompoundTag)}。</li>
 *   <li>{@code Component.Serializer.toJson/fromJson} 也要传 {@code Provider}。</li>
 *   <li>{@code getUpdateTag} 也要传 {@code Provider}。</li>
 * </ul>
 */
public class AutocannonAmmoDrumBlockEntity extends BlockEntity
		implements IAutocannonAmmoDrumContainer, MenuProvider, Nameable {

	private ItemStack ammoStack1 = ItemStack.EMPTY;
	private ItemStack ammoStack2 = ItemStack.EMPTY;
	private ItemStack tracers = ItemStack.EMPTY;
	private int spacing = 1;
	@Nullable
	private Component name;

	private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
		@Override
		protected void onOpen(Level level, BlockPos pos, BlockState state) {
			AutocannonAmmoDrumBlockEntity.this.playSound(SoundEvents.IRON_TRAPDOOR_OPEN);
			AutocannonAmmoDrumBlockEntity.this.updateBlockState(state, true);
		}

		@Override
		protected void onClose(Level level, BlockPos pos, BlockState state) {
			AutocannonAmmoDrumBlockEntity.this.playSound(SoundEvents.IRON_TRAPDOOR_CLOSE);
			AutocannonAmmoDrumBlockEntity.this.updateBlockState(state, false);
		}

		@Override
		protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int count, int openCount) {
		}

		@Override
		protected boolean isOwnContainer(Player player) {
			return player.containerMenu instanceof AutocannonAmmoDrumMenu menu
					&& menu.getContainer() == AutocannonAmmoDrumBlockEntity.this;
		}
	};

	public AutocannonAmmoDrumBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// ------------------------------------------------------------------------------------
	// 容器内容
	// ------------------------------------------------------------------------------------

	@Override
	public ItemStack getMainAmmoStack() {
		return this.ammoStack1;
	}

	@Override
	public ItemStack getSecondaryAmmoStack() {
		return this.ammoStack2;
	}

	@Override
	public ItemStack getTracerStack() {
		return this.tracers;
	}

	public int getSpacing() {
		return this.spacing;
	}

	public void setMainAmmoDirect(ItemStack stack) {
		this.ammoStack1 = stack == null ? ItemStack.EMPTY : stack;
	}

	public void setSecondaryAmmoDirect(ItemStack stack) {
		this.ammoStack2 = stack == null ? ItemStack.EMPTY : stack;
	}

	public void setTracersDirect(ItemStack stack) {
		this.tracers = stack == null ? ItemStack.EMPTY : stack;
	}

	public void setSpacing(int spacing) {
		this.spacing = Mth.clamp(spacing, 1, 6);
	}

	/** 创造模式挖掉时是否要把桶里的弹药掉出来。 */
	public boolean canDropInCreative() {
		return !this.getMainAmmoStack().isEmpty() || !this.getSecondaryAmmoStack().isEmpty()
				|| !this.getTracerStack().isEmpty();
	}

	// ------------------------------------------------------------------------------------
	// 存档
	// ------------------------------------------------------------------------------------

	@Override
	protected void saveAdditional(CompoundTag tag, Provider registries) {
		super.saveAdditional(tag, registries);
		if (!this.ammoStack1.isEmpty()) {
			tag.put("Ammo", this.ammoStack1.save(registries));
		}
		if (!this.ammoStack2.isEmpty()) {
			tag.put("Ammo1", this.ammoStack2.save(registries));
		}
		if (!this.tracers.isEmpty()) {
			tag.put("Tracers", this.tracers.save(registries));
		}
		if (this.name != null) {
			tag.putString("CustomName", Serializer.toJson(this.name, registries));
		}
		tag.putInt("TracerSpacing", this.spacing);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, Provider registries) {
		super.loadAdditional(tag, registries);
		this.ammoStack1 = tag.contains("Ammo") ? ItemStack.parseOptional(registries, tag.getCompound("Ammo"))
				: ItemStack.EMPTY;
		this.ammoStack2 = tag.contains("Ammo1") ? ItemStack.parseOptional(registries, tag.getCompound("Ammo1"))
				: ItemStack.EMPTY;
		this.tracers = tag.contains("Tracers") ? ItemStack.parseOptional(registries, tag.getCompound("Tracers"))
				: ItemStack.EMPTY;
		this.spacing = tag.contains("TracerSpacing") ? Mth.clamp(tag.getInt("TracerSpacing"), 1, 6) : 1;
		this.name = tag.contains("CustomName", 8)
				? Serializer.fromJson(tag.getString("CustomName"), registries)
				: null;
	}

	@Override
	public CompoundTag getUpdateTag(Provider registries) {
		return this.saveWithFullMetadata(registries);
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	// ------------------------------------------------------------------------------------
	// 名称
	// ------------------------------------------------------------------------------------

	@Nullable
	@Override
	public Component getCustomName() {
		return this.name;
	}

	public void setCustomName(Component name) {
		this.name = name;
	}

	@Override
	public Component getName() {
		return this.name == null ? this.getDefaultName() : this.name;
	}

	/**
	 * 默认名。
	 *
	 * <p><b>注意</b>：1.20.1 这里取的是 <b>CBC</b> 的 {@code AUTOCANNON_AMMO_CONTAINER}
	 * 方块的描述 id（原项目就这么写，可能是笔误 —— 显示成 CBC 的名字）。
	 * 本轮保持等价不修（§8.4），但改用<b>我们自己的</b>方块名字更合理，
	 * 故这里按本项目方块取（已登记在本轮日志的偏差表里）。
	 */
	protected Component getDefaultName() {
		return Component.translatable(CBCModernWarfareBlocks.AUTOCANNON_AMMO_DRUM.get().getDescriptionId());
	}

	@Override
	public Component getDisplayName() {
		return this.getName();
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
		return AutocannonAmmoDrumMenu.getServerMenuForBlockEntity(id, inventory, this);
	}

	// ------------------------------------------------------------------------------------
	// Container
	// ------------------------------------------------------------------------------------

	@Override
	public ItemStack removeItem(int slot, int amount) {
		ItemStack ammo = this.getItem(slot);
		if (ammo.isEmpty()) {
			return ItemStack.EMPTY;
		}
		ItemStack split = ammo.split(amount);
		this.setItem(slot, ammo);
		this.setChanged();
		return split;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		ItemStack ret = this.getItem(slot);
		switch (slot) {
			case 0 -> this.ammoStack1 = ItemStack.EMPTY;
			case 1 -> this.ammoStack2 = ItemStack.EMPTY;
			case 2 -> this.tracers = ItemStack.EMPTY;
			default -> {
			}
		}
		return ret;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		switch (slot) {
			case 0 -> this.setMainAmmoDirect(stack);
			case 1 -> this.setSecondaryAmmoDirect(stack);
			case 2 -> this.setTracersDirect(stack);
			default -> {
			}
		}
		this.setChanged();
	}

	@Override
	public void setChanged() {
		super.setChanged();
		// 开着界面时同步方块状态（空/满），让外观跟着变化。
		if (this.level != null && this.openersCounter.getOpenerCount() > 0) {
			BlockState state = this.getBlockState();
			this.level.setBlock(this.getBlockPos(), state.setValue(AutocannonAmmoDrumBlock.CONTAINER_STATE,
					AutocannonAmmoDrumBlock.State.getFromFilled(this.getTotalCount() > 0)), 3);
		}
	}

	@Override
	public boolean stillValid(Player player) {
		return this.getBlockPos().closerThan(player.blockPosition(), 4.0);
	}

	@Override
	public void clearContent() {
		this.ammoStack1 = ItemStack.EMPTY;
		this.ammoStack2 = ItemStack.EMPTY;
		this.tracers = ItemStack.EMPTY;
	}

	@Override
	public void startOpen(Player player) {
		if (!this.remove && !player.isSpectator()) {
			this.openersCounter.incrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
		}
	}

	@Override
	public void stopOpen(Player player) {
		if (!this.remove && !player.isSpectator()) {
			this.openersCounter.decrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
		}
	}

	public void recheckOpen() {
		if (!this.remove) {
			this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
		}
	}

	void updateBlockState(BlockState state, boolean open) {
		AutocannonAmmoDrumBlock.State containerState = open
				? AutocannonAmmoDrumBlock.State.getFromFilled(this.getTotalCount() > 0)
				: AutocannonAmmoDrumBlock.State.CLOSED;
		this.level.setBlock(this.getBlockPos(), state.setValue(AutocannonAmmoDrumBlock.CONTAINER_STATE, containerState), 3);
	}

	void playSound(SoundEvent sound) {
		double x = this.worldPosition.getX() + 0.5;
		double y = this.worldPosition.getY() + 0.5;
		double z = this.worldPosition.getZ() + 0.5;
		this.level.playSound(null, x, y, z, sound, SoundSource.BLOCKS, 0.5F,
				this.level.getRandom().nextFloat() * 0.1F + 0.9F);
	}
}
