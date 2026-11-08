package riftyboi.cbcmodernwarfare.cannons.rotarycannon.breech;

import static rbasamoyai.createbigcannons.cannons.big_cannons.BigCannonBlock.writeAndSyncSingleBlockData;

import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import rbasamoyai.createbigcannons.cannon_control.contraption.AbstractMountedCannonContraption;
import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;
import rbasamoyai.createbigcannons.crafting.casting.CannonCastShape;
import rbasamoyai.createbigcannons.index.CBCSoundEvents;
import rbasamoyai.createbigcannons.munitions.autocannon.ammo_container.AutocannonAmmoContainerItem;

import riftyboi.cbcmodernwarfare.cannons.rotarycannon.RotarycannonBaseBlock;
import riftyboi.cbcmodernwarfare.cannons.rotarycannon.material.RotarycannonMaterial;
import riftyboi.cbcmodernwarfare.crafting.casting.CBCModernWarfareCastShapes;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareBlockEntities;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareShapes;

/**
 * 转管炮的炮闩：右键装/卸弹匣（{@code AutocannonAmmoContainerItem}）。
 *
 * <p><b>1.20.1 → 1.21.1 的关键改动：{@code Block#use} 拆成两个方法。</b>
 * <pre>
 * 1.20.1: use(BlockState, Level, BlockPos, Player, InteractionHand, BlockHitResult) → InteractionResult
 * 1.21.1: useItemOn(ItemStack, BlockState, Level, BlockPos, Player, InteractionHand, BlockHitResult) → ItemInteractionResult
 *         useWithoutItem(BlockState, Level, BlockPos, Player, BlockHitResult) → InteractionResult
 * </pre>
 * 原代码的 {@code use} 要读手里的物品（判 {@code AutocannonAmmoContainerItem}），
 * 所以实装 {@code useItemOn}，返回 {@link ItemInteractionResult}。
 * {@code PASS_TO_DEFAULT_BLOCK_INTERACTION} 对应原代码的 {@code super.use(...)}。
 *
 * <p>另：{@code getStateForPlacement} 用的是 {@code getNearestLookingDirection()}（不加
 * {@code .getOpposite()}）—— 与 {@code RotarycannonBaseBlock} 的默认行为<b>不同</b>，
 * 这是 1.20.1 的原样写法（炮闩朝向玩家视线方向）。保持不变。
 */
public class RotarycannonBreechBlock extends RotarycannonBaseBlock
        implements IBE<AbstractRotarycannonBreechBlockEntity>, IWrenchable {

    public RotarycannonBreechBlock(Properties properties, RotarycannonMaterial material) {
        super(properties, material);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }

    @Override
    public Class<AbstractRotarycannonBreechBlockEntity> getBlockEntityClass() {
        return AbstractRotarycannonBreechBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends AbstractRotarycannonBreechBlockEntity> getBlockEntityType() {
        return CBCModernWarfareBlockEntities.ROTARYCANNON_BREECH.get();
    }

    @Override
    public CannonCastShape getCannonShape() {
        return CBCModernWarfareCastShapes.ROTARYCANNON_BREECH;
    }

    /** 炮闩按**同向**判定可连接（与炮管的同轴判定不同，这是原代码的写法）。 */
    @Override
    public boolean canConnectToSide(BlockState state, Direction face) {
        return this.getFacing(state) == face;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection());
    }

    @Override
    public boolean isBreechMechanism(BlockState state) {
        return true;
    }

    @Override
    public boolean isComplete(BlockState state) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return CBCModernWarfareShapes.BREECH.get(this.getFacing(state).getAxis());
    }

    @Override
    public net.minecraft.world.InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        return net.minecraft.world.InteractionResult.PASS;
    }

    /**
     * 右键：先卸弹匣（若里面有），再尝试装上手里的弹匣。
     *
     * <p>1.21.1 用 {@code useItemOn}；返回值映射：
     * 有变化 → {@code SUCCESS} / {@code CONSUME}；无变化 → {@code PASS_TO_DEFAULT_BLOCK_INTERACTION}。
     */
    @Override
    public ItemInteractionResult useItemOn(ItemStack heldStack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult result) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.getBlockEntity(pos) instanceof AbstractRotarycannonBreechBlockEntity breech) {

            ItemStack container = breech.getMagazine();
            boolean changed = false;
            boolean tryAdd = false;
            if (!container.isEmpty()) {
                if (!level.isClientSide) {
                    tryAdd = true;
                    breech.setMagazine(ItemStack.EMPTY);
                }
                changed = true;
            }
            if (stack.getItem() instanceof AutocannonAmmoContainerItem) {
                if (!level.isClientSide) {
                    breech.setMagazine(stack);
                    player.setItemInHand(hand, ItemStack.EMPTY);
                    CBCSoundEvents.PLACE_AUTOCANNON_AMMO_CONTAINER.playOnServer(level, pos);
                }
                changed = true;
            }

            if (tryAdd && !player.addItem(container)) {
                Vec3 spawnLoc = Vec3.atCenterOf(pos);
                ItemEntity dropEntity = new ItemEntity(level, spawnLoc.x, spawnLoc.y, spawnLoc.z, container);
                level.addFreshEntity(dropEntity);
            }
            if (changed) {
                breech.notifyUpdate();
                return level.isClientSide ? ItemInteractionResult.SUCCESS : ItemInteractionResult.CONSUME;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    /**
     * 装配成 contraption 后：装/卸弹匣，并把改动同步给同乘的玩家。
     *
     * <p>1.20.1 逐字搬入。{@code StructureBlockInfo} 是 record，但这里的
     * {@code writeAndSyncSingleBlockData} 直接接 {@code info}，签名未变。
     */
    @Override
    public boolean onInteractWhileAssembled(Player player, BlockPos localPos, Direction side,
            InteractionHand interactionHand, Level level, Contraption contraption, BlockEntity be,
            StructureBlockInfo info, PitchOrientedContraptionEntity entity) {
        if (!(be instanceof AbstractRotarycannonBreechBlockEntity breech)) {
            return false;
        }

        ItemStack stack = player.getItemInHand(interactionHand);
        ItemStack container = breech.getMagazine();
        Vec3 globalPos = entity.toGlobalVector(Vec3.atCenterOf(localPos), 0);

        boolean insertingContainer = stack.getItem() instanceof AutocannonAmmoContainerItem;
        boolean canRemove = insertingContainer
                || stack.isEmpty() && (AutocannonAmmoContainerItem.getTotalAmmoCount(container) == 0 || player.isShiftKeyDown());

        boolean changed = false;
        boolean tryAdd = false;
        if (!container.isEmpty() && canRemove) {
            if (!level.isClientSide) {
                tryAdd = true;
                breech.setMagazine(ItemStack.EMPTY);
            }
            changed = true;
        }
        if (breech.getMagazine().isEmpty() && insertingContainer) {
            if (!level.isClientSide) {
                breech.setMagazine(stack);
                player.setItemInHand(interactionHand, ItemStack.EMPTY);
                CBCSoundEvents.PLACE_AUTOCANNON_AMMO_CONTAINER.playOnServer(level, localPos);
            }
            changed = true;
        }

        if (tryAdd && !player.addItem(container)) {
            ItemEntity dropEntity = new ItemEntity(level, globalPos.x, globalPos.y, globalPos.z, container);
            level.addFreshEntity(dropEntity);
        }
        // AbstractMountedCannonContraption 是 Contraption 的子类，这里按原型传参即可。
        if (changed && !level.isClientSide
                && contraption instanceof AbstractMountedCannonContraption mounted) {
            writeAndSyncSingleBlockData(be, info, entity, mounted);
        }
        return changed;
    }
}
