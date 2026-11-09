package riftyboi.cbcmodernwarfare.munitions.autocannon.ammo_drum;

import java.util.Locale;

import javax.annotation.Nullable;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import rbasamoyai.createbigcannons.index.CBCShapes;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonAmmoType;

import riftyboi.cbcmodernwarfare.index.CBCModernWarfareBlockEntities;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareMenuTypes;

/**
 * 机炮弹鼓方块。
 *
 * <p><b>注册 id 是 {@code rotarycannon_ammo_drum}（不是 autocannon）</b> —— 这是
 * 1.20.1 原项目的写法，别"顺手统一"（R6 计划书 §9.6）。BE 与 Menu 则叫
 * {@code autocannon_ammo_drum}。三者不一致是原版如此。
 *
 * <p><b>1.20.1 → 1.21.1 的变化（经 javap 核实）</b>：
 * <ul>
 *   <li>{@code Block#use} 拆成 {@code useItemOn}/{@code useWithoutItem}，
 *       前者返回 {@link ItemInteractionResult}。</li>
 *   <li>{@code getCloneItemStack} 的 {@code Provider} 要自己从 {@code Level} 取
 *       （接口签名不带 Provider，但 {@code saveToItem} 需要）。</li>
 *   <li>打开菜单走 {@code MenuEntry#open(ServerPlayer, Component, MenuConstructor,
 *       Consumer<RegistryFriendlyByteBuf>)}。</li>
 * </ul>
 */
public class AutocannonAmmoDrumBlock extends Block
		implements IWrenchable, SimpleWaterloggedBlock, IBE<AutocannonAmmoDrumBlockEntity> {

	public static final EnumProperty<Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final EnumProperty<State> CONTAINER_STATE =
			EnumProperty.create("state", State.class);

	public AutocannonAmmoDrumBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState()
				.setValue(CONTAINER_STATE, State.CLOSED)
				.setValue(WATERLOGGED, false));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(AXIS, CONTAINER_STATE, WATERLOGGED);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(AXIS, context.getHorizontalDirection().getAxis());
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return CBCShapes.AUTOCANNON_AMMO_CONTAINER.get(state.getValue(AXIS));
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
			ItemStack stack) {
		if (level.getBlockEntity(pos) instanceof AutocannonAmmoDrumBlockEntity be) {
			if (stack.has(DataComponents.CUSTOM_NAME)) {
				be.setCustomName(stack.getHoverName());
			}
			Provider registries = level.registryAccess();
			be.setMainAmmoDirect(AutocannonAmmoDrumItem.getMainAmmoStack(stack, registries));
			be.setSecondaryAmmoDirect(AutocannonAmmoDrumItem.getSecondaryAmmoStack(stack, registries));
			be.setTracersDirect(AutocannonAmmoDrumItem.getTracerAmmoStack(stack, registries));
			be.setSpacing(AutocannonAmmoDrumItem.getTracerSpacing(stack));
		}
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		// 创造模式挖掉时装满弹药的桶会掉一个带内容的物品，不静默丢失内容。
		if (!level.isClientSide && level.getBlockEntity(pos) instanceof AutocannonAmmoDrumBlockEntity be
				&& player.isCreative() && be.canDropInCreative()) {
			ItemStack stack = new ItemStack(this.asItem());
			be.saveToItem(stack, level.registryAccess());
			if (be.hasCustomName()) {
				stack.set(DataComponents.CUSTOM_NAME, be.getCustomName());
			}
			ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
					stack);
			itemEntity.setDefaultPickUpDelay();
			level.addFreshEntity(itemEntity);
		}
		return super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			if (level.getBlockEntity(pos) instanceof AutocannonAmmoDrumBlockEntity) {
				level.updateNeighbourForOutputSignal(pos, state.getBlock());
			}
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	@Override
	public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
		ItemStack itemStack = super.getCloneItemStack(level, pos, state);
		if (level.getBlockEntity(pos) instanceof AutocannonAmmoDrumBlockEntity be) {
			be.saveToItem(itemStack, registries(level));
		}
		return itemStack;
	}

	/** 从 {@link BlockGetter} 取注册表视图（1.21.1 的 saveToItem 需要）。 */
	private static Provider registries(BlockGetter level) {
		return level instanceof Level l
				? l.registryAccess()
				: RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
			Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.getBlockEntity(pos) instanceof AutocannonAmmoDrumBlockEntity be) {
			if (player instanceof ServerPlayer splayer) {
				CBCModernWarfareMenuTypes.AUTOCANNON_AMMO_DRUM.open(splayer, be.getDisplayName(), be, buf -> {
					buf.writeVarInt(be.getSpacing());
					buf.writeBoolean(true); // true = 方块版
					buf.writeBlockPos(pos);
				});
			}
			return ItemInteractionResult.sidedSuccess(level.isClientSide);
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof AutocannonAmmoDrumBlockEntity be) {
			AutocannonAmmoType type = be.getAmmoType();
			return type == AutocannonAmmoType.NONE
					? 0
					: (int) Math.floor((float) be.getTotalCount() / (float) type.getCapacity() * 15.0F);
		}
		return 0;
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		super.tick(state, level, pos, random);
		if (level.getBlockEntity(pos) instanceof AutocannonAmmoDrumBlockEntity be) {
			be.recheckOpen();
		}
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
	}

	@Override
	public BlockState updateShape(BlockState state, Direction face, BlockState otherState, LevelAccessor level,
			BlockPos pos, BlockPos otherPos) {
		if (state.getValue(WATERLOGGED)) {
			level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
		return super.updateShape(state, face, otherState, level, pos, otherPos);
	}

	@Override
	public Class<AutocannonAmmoDrumBlockEntity> getBlockEntityClass() {
		return AutocannonAmmoDrumBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends AutocannonAmmoDrumBlockEntity> getBlockEntityType() {
		return CBCModernWarfareBlockEntities.AUTOCANNON_AMMO_DRUM.get();
	}

	/** 外观三态：关着 / 空 / 满。 */
	public enum State implements StringRepresentable {
		CLOSED,
		EMPTY,
		FILLED;

		private final String id = this.name().toLowerCase(Locale.ROOT);

		@Override
		public String getSerializedName() {
			return this.id;
		}

		public static State getFromFilled(boolean filled) {
			return filled ? FILLED : EMPTY;
		}
	}
}
