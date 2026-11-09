package riftyboi.cbcmodernwarfare.munitions.autocannon.ammo_drum;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import rbasamoyai.createbigcannons.index.CBCDataComponents;
import rbasamoyai.createbigcannons.munitions.autocannon.ammo_container.AutocannonAmmoContainerItem;

import riftyboi.cbcmodernwarfare.index.CBCModernWarfareMenuTypes;

/**
 * 弹鼓物品：右键打开界面（手持版）；对已装填弹鼓按放置逻辑走父类的 {@code useOn}。
 *
 * <p><b>1.20.1 → 1.21.1 的变化（经 javap 核实）</b>：
 * <ul>
 *   <li>{@code appendHoverText} 第二参数 {@code Level} → {@code Item.TooltipContext}；
 *       需要注册表时用 {@code context.registries()}。</li>
 *   <li>{@code MenuEntry#open} 的 buf lambda 参数类型是
 *       {@code RegistryFriendlyByteBuf}。</li>
 *   <li>物品 NBT 读写统一走 {@code ItemStack.save(Provider)} /
 *       {@code ItemStack.parseOptional(Provider, CompoundTag)}。</li>
 * </ul>
 */
public class AutocannonAmmoDrumItem extends AutocannonAmmoContainerItem {

	public AutocannonAmmoDrumItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public Component getDisplayName() {
		return this.getDescription();
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
		return AutocannonAmmoDrumMenu.getServerMenuForItemStack(id, inventory, player.getMainHandItem());
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		if (player.mayBuild()) {
			ItemStack stack = player.getItemInHand(hand);
			if (player instanceof ServerPlayer splayer) {
				int spacing = getTracerSpacing(stack);
				Component screenName = stack.has(DataComponents.CUSTOM_NAME)
						? stack.getHoverName()
						: this.getDisplayName();
				CBCModernWarfareMenuTypes.AUTOCANNON_AMMO_DRUM.open(splayer, screenName, this, buf -> {
					buf.writeVarInt(spacing);
					buf.writeBoolean(false); // false = 物品版
					ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, new ItemStack(this));
				});
			}
			return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
		}
		return super.use(level, player, hand);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		InteractionResult superResult = super.useOn(context);
		return superResult.consumesAction()
				? superResult
				: this.use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
	}

	// ------------------------------------------------------------------------------------
	// 内容读写（物品栈里存的是方块实体数据组件）
	// ------------------------------------------------------------------------------------

	public static ItemStack getMainAmmoStack(ItemStack container) {
		return getMainAmmoStack(container, builtinRegistries());
	}

	public static ItemStack getMainAmmoStack(ItemStack container, Provider registries) {
		return readSlot(container, "Ammo", registries);
	}

	public static ItemStack getSecondaryAmmoStack(ItemStack container) {
		return getSecondaryAmmoStack(container, builtinRegistries());
	}

	public static ItemStack getSecondaryAmmoStack(ItemStack container, Provider registries) {
		return readSlot(container, "Ammo1", registries);
	}

	public static ItemStack getTracerAmmoStack(ItemStack container) {
		return getTracerAmmoStack(container, builtinRegistries());
	}

	public static ItemStack getTracerAmmoStack(ItemStack container, Provider registries) {
		return readSlot(container, "Tracers", registries);
	}

	private static ItemStack readSlot(ItemStack container, String key, Provider registries) {
		CompoundTag tag = getData(container);
		return tag.contains(key) ? ItemStack.parseOptional(registries, tag.getCompound(key)) : ItemStack.EMPTY;
	}

	public static int getTracerSpacing(ItemStack container) {
		// 优先读组件（CBC 的 TRACER_SPACING），没有再看物品里的旧键。
		if (container.has(CBCDataComponents.TRACER_SPACING)) {
			return Mth.clamp(container.getOrDefault(CBCDataComponents.TRACER_SPACING, 1), 1, 6);
		}
		CompoundTag tag = getData(container);
		return tag.contains("TracerSpacing") ? Mth.clamp(tag.getInt("TracerSpacing"), 1, 6) : 1;
	}

	private static CompoundTag getData(ItemStack container) {
		CustomData data = container.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
		if (data.isEmpty()) {
			data = container.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
		}
		return data.copyTag();
	}

	private static Provider builtinRegistries() {
		return RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents,
			TooltipFlag isAdvanced) {
		ItemStack secondaryAmmo = getSecondaryAmmoStack(stack, context.registries());
		if (!secondaryAmmo.isEmpty()) {
			tooltipComponents.add(Component.translatable(
					"block.createbigcannons.autocannon_ammo_container.tooltip.main_ammo",
					Integer.toString(secondaryAmmo.getCount()), secondaryAmmo.getDisplayName()));
		}
		super.appendHoverText(stack, context, tooltipComponents, isAdvanced);
	}
}
