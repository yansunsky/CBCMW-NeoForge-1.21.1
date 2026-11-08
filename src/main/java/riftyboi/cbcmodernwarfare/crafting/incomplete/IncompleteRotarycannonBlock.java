package riftyboi.cbcmodernwarfare.crafting.incomplete;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.simibubi.create.AllShapes;

import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.createmod.catnip.math.VoxelShaper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import rbasamoyai.createbigcannons.cannons.CannonBehavior;
import rbasamoyai.createbigcannons.cannons.ICannonBlockEntity;
import rbasamoyai.createbigcannons.crafting.casting.CannonCastShape;
import rbasamoyai.createbigcannons.crafting.incomplete.IncompleteWithItemsCannonBlock;

import riftyboi.cbcmodernwarfare.cannons.rotarycannon.AbstractIncompleteRotarycannonBlock;
import riftyboi.cbcmodernwarfare.cannons.rotarycannon.material.RotarycannonMaterial;
import riftyboi.cbcmodernwarfare.crafting.casting.CBCModernWarfareCastShapes;

/**
 * 未完成的转管炮部件：拿指定物品右键补齐。
 *
 * <p><b>1.20.1 → 1.21.1</b>：{@code Block#use} 拆成 {@code useItemOn}（返回
 * {@link ItemInteractionResult}）与 {@code useWithoutItem}。这里实装前者，
 * 映射与 {@code IncompleteMediumcannonBlock} 一致。
 */
public class IncompleteRotarycannonBlock extends AbstractIncompleteRotarycannonBlock
        implements IncompleteWithItemsCannonBlock {

    private final VoxelShaper shapes;
    private final Supplier<CannonCastShape> cannonShape;
    private final NonNullSupplier<? extends ItemLike> itemSupplier;
    private ItemLike item;
    private List<ItemLike> requiredItems = null;

    private final NonNullSupplier<? extends Block> resultBlockSup;
    private Block resultBlock;

    public IncompleteRotarycannonBlock(Properties properties, RotarycannonMaterial material, VoxelShape shape,
            Supplier<CannonCastShape> cannonShape, NonNullSupplier<? extends Block> completeBlockSup,
            NonNullSupplier<? extends ItemLike> item) {
        super(properties, material);
        this.shapes = new AllShapes.Builder(shape).forDirectional();
        this.cannonShape = cannonShape;
        this.itemSupplier = item;
        this.resultBlockSup = completeBlockSup;
    }

    /** 炮闩：内径 14。 */
    public static IncompleteRotarycannonBlock breech(Properties properties, RotarycannonMaterial material,
            NonNullSupplier<? extends Block> completeBlock, NonNullSupplier<? extends ItemLike> item) {
        return new IncompleteRotarycannonBlock(properties, material, box(1, 0, 1, 15, 16, 15),
                () -> CBCModernWarfareCastShapes.ROTARYCANNON_BREECH, completeBlock, item);
    }

    /** 轴承：内径 10。 */
    public static IncompleteRotarycannonBlock bearing(Properties properties, RotarycannonMaterial material,
            NonNullSupplier<? extends Block> completeBlock, NonNullSupplier<? extends ItemLike> item) {
        return new IncompleteRotarycannonBlock(properties, material, box(3, 0, 3, 13, 16, 13),
                () -> CBCModernWarfareCastShapes.ROTARYCANNON_BEARING, completeBlock, item);
    }

    private ItemLike resolveItem() {
        if (this.item == null) {
            this.item = this.itemSupplier.get();
        }
        return this.item;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.shapes.get(this.getFacing(state));
    }

    @Override
    public CannonCastShape getCannonShape() {
        return this.cannonShape.get();
    }

    @Override
    public List<ItemLike> requiredItems() {
        if (this.requiredItems == null) {
            this.requiredItems = new ArrayList<>(1);
            this.requiredItems.add(this.resolveItem());
        }
        return this.requiredItems;
    }

    @Override
    public int progress(BlockState state) {
        return 0;
    }

    protected Block getResultBlock() {
        if (this.resultBlock == null) {
            this.resultBlock = this.resultBlockSup.get();
        }
        return this.resultBlock;
    }

    @Override
    public BlockState getCompleteBlockState(BlockState state) {
        BlockState complete = this.getResultBlock().defaultBlockState();
        return complete.hasProperty(FACING) ? complete.setValue(FACING, state.getValue(FACING)) : complete;
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult result) {
        if (stack.getItem() != this.resolveItem()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        level.playSound(player, pos, SoundEvents.NETHERITE_BLOCK_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }

        if (!player.isCreative()) {
            stack.shrink(1);
        }
        this.withBlockEntityDo(level, pos, cbe -> {
            CannonBehavior behavior = cbe.cannonBehavior();
            cbe.setRemoved();

            level.setBlock(pos, this.getCompleteBlockState(state), 3 | 16);

            BlockEntity be = level.getBlockEntity(pos);
            if (!(be instanceof ICannonBlockEntity<?> cbe1)) {
                return;
            }
            CannonBehavior behavior1 = cbe1.cannonBehavior();
            for (Direction dir : Direction.values()) {
                boolean isConnected = behavior.isConnectedTo(dir);
                behavior1.setConnectedFace(dir, isConnected);
                if (level.getBlockEntity(pos.relative(dir)) instanceof ICannonBlockEntity<?> cbe2) {
                    cbe2.cannonBehavior().setConnectedFace(dir.getOpposite(), isConnected);
                }
            }
        });

        return ItemInteractionResult.CONSUME;
    }
}
