package riftyboi.cbcmodernwarfare.cannons.rotarycannon.bearing;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;

import riftyboi.cbcmodernwarfare.cannons.rotarycannon.RotarycannonBlockEntity;

/**
 * 转管炮轴承的方块实体：记录旋转角，以及「跟着转」的方块快照。
 *
 * <p><b>1.20.1 → 1.21.1 的三处改动</b>：
 * <ol>
 *   <li>{@code write}/{@code read} 增加 {@link HolderLookup.Provider} 参数。</li>
 *   <li>{@code NbtUtils.readBlockPos(CompoundTag)} → {@code readBlockPos(tag, key)}
 *       （返回 {@code Optional<BlockPos>}）。</li>
 *   <li>{@code NbtUtils.readBlockState(CompoundTag)} → 需要 {@code HolderGetter<Block>}，
 *       用 {@code this.blockHolderGetter()}。</li>
 * </ol>
 *
 * <p><b>注意</b>：1.20.1 的 {@code setRotation} 末尾有一个多余的分号（`;;`），
 * 语义无影响，迁移时去掉。
 */
public class RotarycannonBearingBlockEntity extends RotarycannonBlockEntity {

    /** 需要随轴承一起旋转的方块（局部坐标 → 方块状态）。 */
    public Map<BlockPos, BlockState> toAnimate = new HashMap<>();

    private float rotation = 0;
    private float rot = 0;
    private float power = 0;

    public RotarycannonBearingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
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
        if (this.rotation < 0) {
            this.rotation = 0;
        }
        if (this.rotation > 360) {
            this.rotation -= 360;
        }
    }

    public void setRotation(float rot, float time) {
        this.rotation = ((time * rot * 2.5f / 10) % 360) / 180 * (float) Math.PI;
    }

    public float getRotation() {
        if (this.power < 1 || this.power > 15) {
            return 0;
        }
        return this.rotation;
    }

    public float getRot() {
        return this.rot;
    }

    public void updateRotation(int fireRate, int power) {
        this.power = power;
        this.rot = (fireRate / 1200f) * 360;
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);

        tag.putFloat("Rotation", this.rotation);

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

        this.rotation = tag.getFloat("Rotation");

        this.toAnimate.clear();
        ListTag renderedList = tag.getList("RenderedBlocks", Tag.TAG_COMPOUND);
        for (int i = 0; i < renderedList.size(); ++i) {
            CompoundTag block = renderedList.getCompound(i);
            this.toAnimate.put(
                    NbtUtils.readBlockPos(block, "Pos").orElse(BlockPos.ZERO),
                    NbtUtils.readBlockState(this.blockHolderGetter(), block.getCompound("Block")));
        }
    }
}
