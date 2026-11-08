package riftyboi.cbcmodernwarfare.cannons.rotarycannon;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import rbasamoyai.createbigcannons.crafting.casting.CannonCastShape;

import riftyboi.cbcmodernwarfare.cannons.rotarycannon.bearing.MovesWithRotaryCannonBearing;
import riftyboi.cbcmodernwarfare.cannons.rotarycannon.material.RotarycannonMaterial;
import riftyboi.cbcmodernwarfare.crafting.casting.CBCModernWarfareCastShapes;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareBlockEntities;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareShapes;

/**
 * 转管炮的炮管。
 *
 * <p><b>1.20.1 → 1.21.1</b>：{@code IWrenchable} / {@code IBE} 签名未变，逐字搬入。
 * 与 {@code MediumcannonBarrelBlock} 的两处差异（都是原样）：
 * <ol>
 *   <li>实现的是 {@link MovesWithRotaryCannonBearing}（跟轴承一起动），不是跟复进炮管动。</li>
 *   <li>{@code onWrenched} 里取的是 {@code RotarycannonBlockEntity}，判据同样是「前方未连接」。</li>
 * </ol>
 */
public class RotarycannonBarrelBlock extends RotarycannonBaseBlock
        implements IBE<RotarycannonBlockEntity>, IWrenchable, MovesWithRotaryCannonBearing {

    public static final BooleanProperty ASSEMBLED = BooleanProperty.create("assembled");
    public static final EnumProperty<RotarycannonBarrelEnd> BARREL_END =
            EnumProperty.create("end", RotarycannonBarrelEnd.class);

    public RotarycannonBarrelBlock(Properties properties, RotarycannonMaterial material) {
        super(properties, material);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(BARREL_END, RotarycannonBarrelEnd.NOTHING)
                .setValue(ASSEMBLED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ASSEMBLED).add(BARREL_END);
    }

    @Override
    public BlockState getMovingState(BlockState original) {
        return original.setValue(ASSEMBLED, false);
    }

    @Override
    public BlockState getStationaryState(BlockState original) {
        return original.setValue(ASSEMBLED, true);
    }

    @Override
    public Class<RotarycannonBlockEntity> getBlockEntityClass() {
        return RotarycannonBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends RotarycannonBlockEntity> getBlockEntityType() {
        return CBCModernWarfareBlockEntities.ROTARYCANNON.get();
    }

    @Override
    public CannonCastShape getCannonShape() {
        return CBCModernWarfareCastShapes.ROTARYCANNON_BARREL;
    }

    @Override
    public boolean isBreechMechanism(BlockState state) {
        return false;
    }

    @Override
    public boolean isComplete(BlockState state) {
        return true;
    }

    @Override
    public CannonCastShape getCannonShapeInLevel(LevelAccessor level, BlockState state, BlockPos pos) {
        return switch (state.getValue(BARREL_END)) {
            case FLANGED -> CBCModernWarfareCastShapes.ROTARYCANNON_BARREL_FLANGED;
            default -> super.getCannonShapeInLevel(level, state, pos);
        };
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return CBCModernWarfareShapes.BARREL.get(this.getFacing(state).getAxis());
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        BlockPos pos = context.getClickedPos();
        RotarycannonBlockEntity cannon = this.getBlockEntity(level, pos);
        if (cannon == null) {
            return InteractionResult.PASS;
        }
        Direction facing = this.getFacing(state);

        if (!cannon.cannonBehavior().isConnectedTo(facing)) {
            level.setBlock(pos, state.cycle(BARREL_END), 3);
            RotarycannonBlockEntity updated = this.getBlockEntity(level, pos);
            if (updated != null) {
                boolean previouslyConnected = cannon.cannonBehavior().isConnectedTo(facing.getOpposite());
                updated.cannonBehavior().setConnectedFace(facing.getOpposite(), previouslyConnected);
                if (level.getBlockEntity(pos.relative(facing.getOpposite())) instanceof RotarycannonBlockEntity neighbour) {
                    neighbour.cannonBehavior().setConnectedFace(facing, previouslyConnected);
                }
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        return InteractionResult.PASS;
    }

    /** 炮管末端是否带法兰。 */
    public enum RotarycannonBarrelEnd implements StringRepresentable {
        NOTHING("nothing"),
        FLANGED("flanged");

        private final String id;

        RotarycannonBarrelEnd(String id) {
            this.id = id;
        }

        @Override
        public String getSerializedName() {
            return this.id;
        }
    }
}
