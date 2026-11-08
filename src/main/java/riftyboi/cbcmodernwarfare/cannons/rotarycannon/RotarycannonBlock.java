package riftyboi.cbcmodernwarfare.cannons.rotarycannon;

import javax.annotation.Nonnull;

import com.simibubi.create.content.contraptions.Contraption;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.phys.Vec3;

import rbasamoyai.createbigcannons.cannon_control.contraption.AbstractMountedCannonContraption;
import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;
import rbasamoyai.createbigcannons.cannons.CannonContraptionProviderBlock;
import rbasamoyai.createbigcannons.cannons.InteractableCannonBlock;
import rbasamoyai.createbigcannons.cannons.ItemCannonBehavior;
import rbasamoyai.createbigcannons.crafting.casting.CannonCastShape;
import rbasamoyai.createbigcannons.crafting.welding.WeldableBlock;

import riftyboi.cbcmodernwarfare.cannons.rotarycannon.material.RotarycannonMaterial;

/**
 * 转管炮方块的公共契约：焊接、装配、相邻连接、拆解掉落。
 *
 * <p><b>与 {@code MediumcannonBlock} 的两处真实差异</b>（不是笔误，1.20.1 就是这样）：
 * <ol>
 *   <li>{@code canConnectToSide} 判的是<b>同轴</b>（{@code getAxis() == face.getAxis()}），
 *       中型炮判的是<b>同向</b>（{@code getFacing(state) == face}）。
 *       转管炮的多个炮管可以沿轴任意堆叠。</li>
 *   <li>{@code onRemoveCannon} 里<b>没有</b> {@code setWelded(...)} 调用 ——
 *       只断开连接，不动焊接标记。中型炮两者都清。</li>
 * </ol>
 *
 * <p><b>1.20.1 → 1.21.1</b>：三个 CBC 接口签名未变；{@code StructureBlockInfo} 是 record，
 * 取状态写 {@code info.state()}。
 */
public interface RotarycannonBlock extends WeldableBlock, CannonContraptionProviderBlock, InteractableCannonBlock {

    RotarycannonMaterial getRotarycannonMaterial();

    default RotarycannonMaterial getRotarycannonMaterialInLevel(LevelAccessor level, BlockState state, BlockPos pos) {
        return this.getRotarycannonMaterial();
    }

    @Override
    CannonCastShape getCannonShape();

    @Override
    default CannonCastShape getCannonShapeInLevel(LevelAccessor level, BlockState state, BlockPos pos) {
        return this.getCannonShape();
    }

    @Override
    Direction getFacing(BlockState state);

    /** 转管炮按**同轴**判定可连接（与中型炮的「同向」不同）。 */
    @Override
    default boolean canConnectToSide(BlockState state, Direction face) {
        return this.getFacing(state).getAxis() == face.getAxis();
    }

    boolean isBreechMechanism(BlockState state);

    @Override
    boolean isComplete(BlockState state);

    default void onRemoveCannon(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!(state.getBlock() instanceof RotarycannonBlock cBlock) || state.is(newState.getBlock())) {
            return;
        }
        Direction facing = cBlock.getFacing(state);
        Direction opposite = facing.getOpposite();
        RotarycannonMaterial material = cBlock.getRotarycannonMaterial();

        if (level.getBlockEntity(pos) instanceof IRotarycannonBlockEntity cbe) {
            for (ItemStack stack : cbe.getDrops()) {
                Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack.copy());
            }
        }

        if (cBlock.canConnectToSide(state, facing)) {
            BlockPos pos1 = pos.relative(facing);
            BlockState state1 = level.getBlockState(pos1);
            BlockEntity be1 = level.getBlockEntity(pos1);

            if (state1.getBlock() instanceof RotarycannonBlock cBlock1
                    && cBlock1.getRotarycannonMaterialInLevel(level, state1, pos1) == material
                    && cBlock1.canConnectToSide(state1, opposite)
                    && be1 instanceof IRotarycannonBlockEntity cbe1) {
                cbe1.cannonBehavior().setConnectedFace(opposite, false);
                be1.setChanged();
            }
        }

        if (cBlock.canConnectToSide(state, opposite)) {
            BlockPos pos2 = pos.relative(opposite);
            BlockState state2 = level.getBlockState(pos2);
            BlockEntity be2 = level.getBlockEntity(pos2);

            if (state2.getBlock() instanceof RotarycannonBlock cBlock2
                    && cBlock2.getRotarycannonMaterialInLevel(level, state2, pos2) == material
                    && cBlock2.canConnectToSide(state2, facing)
                    && be2 instanceof IRotarycannonBlockEntity cbe2) {
                cbe2.cannonBehavior().setConnectedFace(facing, false);
                be2.setChanged();
            }
        }
    }

    /** 放置一节转管炮管时，与相邻同材质炮管建立连接。 */
    static void onPlace(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel slevel)) {
            return;
        }
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof RotarycannonBlock cBlock)) {
            return;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof IRotarycannonBlockEntity cbe)) {
            return;
        }

        Direction facing = cBlock.getFacing(state);
        Direction opposite = facing.getOpposite();

        Vec3 center = Vec3.atCenterOf(pos);
        Vec3 offset = Vec3.atBottomCenterOf(facing.getNormal()).scale(0.5d);
        RotarycannonMaterial material = cBlock.getRotarycannonMaterialInLevel(level, state, pos);

        BlockPos pos1 = pos.relative(facing);
        BlockState state1 = level.getBlockState(pos1);
        if (cBlock.canConnectToSide(state, facing)
                && state1.getBlock() instanceof RotarycannonBlock cBlock1
                && cBlock1.getRotarycannonMaterialInLevel(level, state1, pos1) == material
                && cBlock1.canConnectToSide(state1, opposite)) {
            if (state1.hasProperty(RotarycannonBarrelBlock.BARREL_END)) {
                level.setBlock(pos1, state1.setValue(RotarycannonBarrelBlock.BARREL_END,
                        RotarycannonBarrelBlock.RotarycannonBarrelEnd.NOTHING), 3);
            }
            if (level.getBlockEntity(pos1) instanceof IRotarycannonBlockEntity cbe1) {
                cbe.cannonBehavior().setConnectedFace(facing, true);
                cbe1.cannonBehavior().setConnectedFace(facing.getOpposite(), true);
                cbe1.cannonBehavior().blockEntity.setChanged();
                Vec3 particlePos = center.add(offset);
                slevel.sendParticles(ParticleTypes.CRIT, particlePos.x, particlePos.y, particlePos.z,
                        10, 0.5d, 0.5d, 0.5d, 0.1d);
            }
        }

        BlockPos pos2 = pos.relative(opposite);
        BlockState state2 = level.getBlockState(pos2);
        if (cBlock.canConnectToSide(state, opposite)
                && state2.getBlock() instanceof RotarycannonBlock cBlock2
                && cBlock2.getRotarycannonMaterialInLevel(level, state2, pos2) == material
                && cBlock2.canConnectToSide(state2, facing)) {
            if (state2.hasProperty(RotarycannonBarrelBlock.BARREL_END)) {
                level.setBlock(pos2, state2.setValue(RotarycannonBarrelBlock.BARREL_END,
                        RotarycannonBarrelBlock.RotarycannonBarrelEnd.NOTHING), 3);
            }
            if (level.getBlockEntity(pos2) instanceof IRotarycannonBlockEntity cbe2) {
                cbe.cannonBehavior().setConnectedFace(facing.getOpposite(), true);
                cbe2.cannonBehavior().setConnectedFace(facing, true);
                cbe2.cannonBehavior().blockEntity.setChanged();
                Vec3 particlePos = center.add(offset.reverse());
                slevel.sendParticles(ParticleTypes.CRIT, particlePos.x, particlePos.y, particlePos.z,
                        10, 0.5d, 0.5d, 0.5d, 0.1d);
            }
        }

        be.setChanged();
    }

    @Override
    default boolean onInteractWhileAssembled(Player player, BlockPos localPos, Direction side,
            InteractionHand interactionHand, Level level, Contraption contraption, BlockEntity be,
            StructureBlockInfo info, PitchOrientedContraptionEntity entity) {
        return false;
    }

    @Override
    default boolean isWeldable(BlockState state) {
        return this.getRotarycannonMaterial().properties().isWeldable();
    }

    @Override
    default int weldDamage() {
        return this.getRotarycannonMaterial().properties().weldDamage();
    }

    @Override
    default boolean canWeldSide(Level level, Direction dir, BlockState state, BlockState otherState, BlockPos pos) {
        return otherState.getBlock() instanceof RotarycannonBlock cblock
                && cblock.getRotarycannonMaterial() == this.getRotarycannonMaterial()
                && this.isWeldable(state)
                && cblock.isWeldable(otherState)
                && this.canConnectToSide(state, dir)
                && cblock.canConnectToSide(otherState, dir.getOpposite())
                && level.getBlockEntity(pos) instanceof IRotarycannonBlockEntity cbe
                && level.getBlockEntity(pos.relative(dir)) instanceof IRotarycannonBlockEntity cbe1
                && (!cbe.cannonBehavior().isConnectedTo(dir) || !cbe1.cannonBehavior().isConnectedTo(dir.getOpposite()));
    }

    @Override
    default void weldBlock(Level level, BlockState state, BlockPos pos, Direction dir) {
        if (!(level.getBlockEntity(pos) instanceof IRotarycannonBlockEntity cbe)) {
            return;
        }
        ItemCannonBehavior behavior = cbe.cannonBehavior();
        behavior.setConnectedFace(dir, true);
        behavior.setWelded(dir, true);
        behavior.blockEntity.notifyUpdate();
    }

    /**
     * 装配成 contraption 时用的实现。
     *
     * <p><b>TODO(R4b-2b)</b>：{@code MountedRotarycannonContraption} 依赖
     * {@code munitions/autocannon}（机炮弹药，24 文件），与 R6 捆在一起。
     * 在此之前调用会抛异常 —— 调用点是 CBC 的 {@code CannonMountBlockEntity}，
     * 本项目侧的 {@code compact_mount} 归 R8，R4b-2 阶段不会被触发。
     */
    @Nonnull
    @Override
    default AbstractMountedCannonContraption getCannonContraption() {
        throw new UnsupportedOperationException("TODO(R4b-2b): MountedRotarycannonContraption 尚未迁移");
    }
}
