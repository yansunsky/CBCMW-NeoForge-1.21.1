package riftyboi.cbcmodernwarfare.munitions.autocannon.ammo_drum;

import javax.annotation.Nullable;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import rbasamoyai.createbigcannons.munitions.autocannon.ammo_container.AutocannonAmmoContainerItem;

/**
 * 弹鼓「装进物品里」时的容器视图。
 *
 * <p>数据存在物品的 {@code BLOCK_ENTITY_DATA} 组件里（与 1.20.1 一致 —— 那边是
 * 方块实体 NBT 被 {@code saveToItem} 塞进物品栈）。
 *
 * <p><b>1.20.1 → 1.21.1</b>：{@code ItemStack#save(CompoundTag)} 改成
 * {@code save(Provider)}；{@code of(CompoundTag)} 改成
 * {@code parseOptional(Provider, CompoundTag)}。
 */
public class AutocannonAmmoDrumItemContainer implements IAutocannonAmmoDrumContainer {

	private final ItemStack stack;

	public AutocannonAmmoDrumItemContainer(ItemStack stack) {
		this.stack = stack;
	}

	@Override
	public ItemStack getMainAmmoStack() {
		return AutocannonAmmoDrumItem.getMainAmmoStack(this.stack);
	}

	@Override
	public ItemStack getSecondaryAmmoStack() {
		return AutocannonAmmoDrumItem.getSecondaryAmmoStack(this.stack);
	}

	@Override
	public ItemStack getTracerStack() {
		return AutocannonAmmoDrumItem.getTracerAmmoStack(this.stack);
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		if (amount <= 0 || slot < 0 || slot > 2) {
			return ItemStack.EMPTY;
		}
		ItemStack ammo = this.getItem(slot);
		if (ammo.isEmpty()) {
			return ItemStack.EMPTY;
		}
		ItemStack split = ammo.split(amount);
		this.putStack(slot, ammo);
		this.setChanged();
		return split;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		if (slot < 0 || slot > 2) {
			return ItemStack.EMPTY;
		}
		ItemStack ret = this.getItem(slot);
		this.putStack(slot, ItemStack.EMPTY);
		return ret;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		if (slot >= 0 && slot <= 2) {
			this.putStack(slot, stack);
			this.setChanged();
		}
	}

	private void putStack(int slot, ItemStack ammo) {
		String key = this.slotKey(slot);
		// 1.21.1：ItemStack#save(Provider) 返回 Tag（不再是 1.20.1 的 CompoundTag），
		// 但空栈时它会返回空 CompoundTag、非空时返回 CompoundTag —— 这里要的正是
		// 「空的也存一个空 compound」，所以用 saveOptional 再收窄类型。
		CompoundTag saved = (CompoundTag) ammo.saveOptional(registries());
		// ⚠️ 不能写 BLOCK_ENTITY_DATA：1.21.1 的该组件被 NeoForge 校验成「必须带 id」的
		// 方块实体数据。往里塞 Ammo/Tracers 这种自定义键会因为缺 id 在 ItemStack#save 时抛
		// IllegalStateException: Missing id for entity ...
		// （实机崩溃：转管炮的 magazine 存盘/发包时炸在 AbstractRotarycannonBreechBlockEntity.write）。
		// 物品形态的弹药数据写 CUSTOM_DATA —— 语义等价于 1.20.1 的物品根 tag。
		CustomData.update(DataComponents.CUSTOM_DATA, this.stack, tag -> {
			tag.put(key, saved);
		});
		// 拆方块掉落的物品会带 BLOCK_ENTITY_DATA（合法、带 id）。如果里面还留着同名键，
		// 会盖住玩家在界面里的新改动 —— 所以写新值时把旧键清掉。
		CustomData beData = this.stack.get(DataComponents.BLOCK_ENTITY_DATA);
		if (beData != null && !beData.isEmpty()) {
			CompoundTag tag = beData.copyTag();
			if (!tag.contains("id")) {
				// 老版本写坏的组件（缺 id）——整块丢掉，否则 ItemStack#save 必崩。
				this.stack.remove(DataComponents.BLOCK_ENTITY_DATA);
			} else if (tag.contains(key)) {
				tag.remove(key);
				this.stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(tag));
			}
		}
	}

	private String slotKey(int slot) {
		return switch (slot) {
			case 0 -> "Ammo";
			case 1 -> "Ammo1";
			default -> "Tracers";
		};
	}

	private Provider registries() {
		return RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
	}

	@Override
	public void setChanged() {
		// 物品容器没有「脏」的概念，数据已经写进组件了。
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public void clearContent() {
		this.putStack(0, ItemStack.EMPTY);
		this.putStack(1, ItemStack.EMPTY);
		this.putStack(2, ItemStack.EMPTY);
	}

	@Override
	public void startOpen(Player player) {
		player.level().playSound(player, player.blockPosition(), net.minecraft.sounds.SoundEvents.IRON_TRAPDOOR_OPEN,
				net.minecraft.sounds.SoundSource.PLAYERS, 0.5F,
				player.level().getRandom().nextFloat() * 0.1F + 0.9F);
	}

	@Override
	public void stopOpen(Player player) {
		player.level().playSound(player, player.blockPosition(), net.minecraft.sounds.SoundEvents.IRON_TRAPDOOR_CLOSE,
				net.minecraft.sounds.SoundSource.PLAYERS, 0.5F,
				player.level().getRandom().nextFloat() * 0.1F + 0.9F);
	}
}
