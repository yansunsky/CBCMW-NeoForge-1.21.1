package riftyboi.cbcmodernwarfare.cannons.munitions_contraption_launcher;

import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import rbasamoyai.createbigcannons.cannons.CannonBehavior;
import rbasamoyai.createbigcannons.munitions.big_cannon.BigCannonMunitionBlock;
import riftyboi.cbcmodernwarfare.munitions.munitions_contraption_launcher.PropelledContraptionMunitionBlock;

/**
 * 发射管里「装了什么」的行为容器 —— 与 CBC 的 {@code ItemCannonBehavior} 同构，
 * 但存的是一整个 {@link StructureBlockInfo}（方块 + NBT），因为发射器装的是方块（弹头/推进段），
 * 不是物品。
 *
 * <p><b>1.21.1 的两处 API 变化（R7 新发现）</b>
 *
 * <ol>
 *   <li>{@code write/read} 各多一个 {@link HolderLookup.Provider} 参数：
 *       <pre>
 * 1.20.1: write(CompoundTag, boolean)        / read(CompoundTag, boolean)
 * 1.21.1: write(CompoundTag, Provider, boolean) / read(CompoundTag, Provider, boolean)
 *       </pre>
 *       这是 1.21 把注册表访问显式化的结果 —— 读方块状态需要 registries 才能解析。</li>
 *   <li>{@code NbtUtils.readBlockState} 现在必须传 {@code HolderLookup.BlockGetter}。
 *       1.20.1 用的是 {@code this.blockEntity.blockHolderGetter()}，
 *       1.21.1 里这个方法名未变，但语义要求 provider 从参数传进来 ——
 *       两者都给，优先用参数里的 provider（它一定非空，而 blockHolderGetter 在
 *       虚拟 BE（contraption 里的）上可能是 null）。</li>
 * </ol>
 *
 * <p>其余逻辑与 1.20.1 逐行一致。
 */
public class MunitionsLauncherBehavior extends CannonBehavior {

    public static final BehaviourType<?> TYPE = new BehaviourType<>();

    public static final StructureBlockInfo EMPTY =
            new StructureBlockInfo(BlockPos.ZERO, Blocks.AIR.defaultBlockState(), null);

    protected Optional<StructureBlockInfo> containedBlockInfo = Optional.empty();
    protected Predicate<StructureBlockInfo> predicate;

    public MunitionsLauncherBehavior(SmartBlockEntity te, Predicate<StructureBlockInfo> predicate) {
        super(te);
        this.predicate = predicate;
    }

    @Nonnull
    public StructureBlockInfo block() {
        return this.containedBlockInfo.orElse(EMPTY);
    }

    public boolean tryLoadingBlock(StructureBlockInfo info) {
        if (!this.canLoadBlock(info)) {
            return false;
        }
        this.loadBlock(info);
        return true;
    }

    @Override
    protected void onRotate(Direction.Axis rotationAxis, Rotation rotation) {
        if (this.containedBlockInfo.isPresent()) {
            StructureBlockInfo oldInfo = this.containedBlockInfo.get();
            if (oldInfo.state().getBlock() instanceof BigCannonMunitionBlock mblock) {
                this.loadBlock(new StructureBlockInfo(oldInfo.pos(),
                        mblock.onCannonRotate(oldInfo.state(), rotationAxis, rotation), oldInfo.nbt()));
            }
            if (oldInfo.state().getBlock() instanceof PropelledContraptionMunitionBlock mblock) {
                this.loadBlock(new StructureBlockInfo(oldInfo.pos(),
                        mblock.onCannonRotate(oldInfo.state(), rotationAxis, rotation), oldInfo.nbt()));
            }
            this.blockEntity.setChanged();
        }
        super.onRotate(rotationAxis, rotation);
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean spawnPacket) {
        super.write(nbt, registries, spawnPacket);
        if (this.containedBlockInfo.isPresent()) {
            StructureBlockInfo blockInfo = this.containedBlockInfo.get();
            if (!blockInfo.state().isAir()) {
                nbt.putLong("Pos", blockInfo.pos().asLong());
                nbt.put("State", NbtUtils.writeBlockState(blockInfo.state()));
                if (blockInfo.nbt() != null) {
                    nbt.put("Data", blockInfo.nbt());
                }
            }
        }
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(nbt, registries, clientPacket);
        BlockPos pos = BlockPos.of(nbt.getLong("Pos"));
        // 优先用传入的 registries；behaviour 在 contraption 的虚拟 BE 上时
        // blockHolderGetter() 可能为 null，所以不能用它兜底。
        BlockState state = NbtUtils.readBlockState(registries.lookupOrThrow(
                net.minecraft.core.registries.Registries.BLOCK), nbt.getCompound("State"));
        CompoundTag tag = nbt.contains("Data") ? nbt.getCompound("Data") : null;
        this.containedBlockInfo = Optional.of(new StructureBlockInfo(pos, state, tag));
    }

    public boolean canLoadBlock(StructureBlockInfo info) {
        return this.predicate.test(info);
    }

    public void loadBlock(StructureBlockInfo info) {
        this.containedBlockInfo = Optional.ofNullable(info);
    }

    public void removeBlock() {
        this.loadBlock(null);
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }
}
