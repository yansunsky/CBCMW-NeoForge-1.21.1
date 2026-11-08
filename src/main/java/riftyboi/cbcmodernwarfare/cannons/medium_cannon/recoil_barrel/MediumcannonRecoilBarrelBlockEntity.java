package riftyboi.cbcmodernwarfare.cannons.medium_cannon.recoil_barrel;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;

import riftyboi.cbcmodernwarfare.cannons.medium_cannon.AnimatedMediumcannon;
import riftyboi.cbcmodernwarfare.cannons.medium_cannon.MediumcannonBlockEntity;

/**
 * 后坐炮管的方块实体：记录后坐动画进度，以及「跟着一起动」的方块快照。
 *
 * <p><b>1.20.1 → 1.21.1 的四处改动</b>：
 * <ol>
 *   <li>{@code write}/{@code read} 增加 {@link HolderLookup.Provider} 参数。</li>
 *   <li>{@code NbtUtils.readBlockPos(CompoundTag)} 改为
 *       {@code NbtUtils.readBlockPos(CompoundTag, String)}，返回 {@code Optional<BlockPos>}。</li>
 *   <li>{@code NbtUtils.readBlockState(CompoundTag)} 改为
 *       {@code NbtUtils.readBlockState(HolderGetter<Block>, CompoundTag)}，
 *       所以要先取 {@code blockHolderGetter()}。</li>
 *   <li>{@code ListTag} 的元素类型常量依旧是 {@link Tag#TAG_COMPOUND}。</li>
 * </ol>
 */
public class MediumcannonRecoilBarrelBlockEntity extends MediumcannonBlockEntity implements AnimatedMediumcannon {

    /** 需要随后坐一起位移的方块（局部坐标 → 方块状态）。 */
    public Map<BlockPos, BlockState> toAnimate = new HashMap<>();

    private int animateTicks = 5;

    public MediumcannonRecoilBarrelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        this.allTick();
    }

    @Override
    public void tickFromContraption(Level level, PitchOrientedContraptionEntity poce, BlockPos localPos) {
        super.tickFromContraption(level, poce, localPos);
        this.allTick();
    }

    private void allTick() {
        if (this.animateTicks < 5) {
            ++this.animateTicks;
        }
        if (this.animateTicks < 0) {
            this.animateTicks = 0;
        }
    }

    public void handleFiring() {
        this.animateTicks = 0;
    }

    /** 后坐动画的余弦位移：0 表示完全缩回，1 表示复位。 */
    public float getAnimateOffset(float partialTicks) {
        float t = ((float) this.animateTicks + partialTicks) * 1.2f;
        if (t <= 0 || t >= 4.8f) {
            return 1;
        }
        float f = t < 1 ? t : (4.8f - t) / 3.8f;
        return Mth.cos(f * Mth.HALF_PI);
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);

        tag.putInt("AnimateTicks", this.animateTicks);

        ListTag renderedList = new ListTag();
        for (Map.Entry<BlockPos, BlockState> entry : this.toAnimate.entrySet()) {
            CompoundTag block = new CompoundTag();
            block.put("Pos", NbtUtils.writeBlockPos(entry.getKey()));
            block.put("Block", NbtUtils.writeBlockState(entry.getValue()));
            renderedList.add(block);
        }
        tag.put("RenderedBlocks", renderedList);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);

        this.animateTicks = tag.getInt("AnimateTicks");

        this.toAnimate.clear();
        ListTag renderedList = tag.getList("RenderedBlocks", Tag.TAG_COMPOUND);
        for (int i = 0; i < renderedList.size(); ++i) {
            CompoundTag block = renderedList.getCompound(i);
            this.toAnimate.put(
                    NbtUtils.readBlockPos(block, "Pos").orElse(BlockPos.ZERO),
                    NbtUtils.readBlockState(this.blockHolderGetter(), block.getCompound("Block")));
        }
    }

    @Override
    public void incrementAnimationTicks() {
        ++this.animateTicks;
    }

    @Override
    public int getAnimationTicks() {
        return this.animateTicks;
    }
}
