package riftyboi.cbcmodernwarfare.cannons.rotarycannon.breech;

import static rbasamoyai.createbigcannons.cannons.big_cannons.BigCannonBlock.writeAndSyncSingleBlockData;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.simibubi.create.content.contraptions.Contraption;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;
import rbasamoyai.createbigcannons.munitions.autocannon.ammo_container.AutocannonAmmoContainerItem;

import riftyboi.cbcmodernwarfare.cannons.rotarycannon.AnimatedRotarycannon;
import riftyboi.cbcmodernwarfare.cannons.rotarycannon.RotarycannonBlockEntity;

/**
 * 转管炮炮闩的抽象方块实体：射速档位、开火冷却、弹匣与进出料缓冲。
 *
 * <p><b>1.20.1 → 1.21.1 的三处改动</b>：
 * <ol>
 *   <li>{@code read}/{@code write} 增加 {@link HolderLookup.Provider} 参数。</li>
 *   <li>{@code ItemStack.of(CompoundTag)} → {@code ItemStack.parseOptional(provider, tag)}；
 *       {@code stack.save(new CompoundTag())} → {@code stack.save(provider)}。</li>
 *   <li>{@code AutocannonAmmoContainerItem} 的三个静态方法
 *       （{@code getTotalAmmoCount} / {@code pollItemFromContainer} / {@code getTypeOfContainer}）
 *       签名未变（javap 核对 5.11.7）。</li>
 * </ol>
 *
 * <p><b>射速表含义</b>：{@code FIRE_RATES[i]} 是第 {@code i+1} 档的冷却 tick 数，
 * 15 档从 10 rpm 到 1200 rpm。这个表在原代码里就是硬编码的，保持原样。
 */
public abstract class AbstractRotarycannonBreechBlockEntity extends RotarycannonBlockEntity
        implements AnimatedRotarycannon {

    /** 15 个射速档位的冷却 tick 数（10 / 20 / 30 / 40 / 50 / 60 / 80 / 100 / 120 / 200 / 240 / 300 / 400 / 600 / 1200 rpm）。 */
    protected static final int[] FIRE_RATES = new int[] {
            120, 60, 40, 30, 24, 20, 15, 12, 10, 6, 5, 4, 4, 2, 1
    };

    private int fireRate = 7;
    private int firingCooldown;
    private int animateTicks = 5;
    @Nullable
    DyeColor seat = null;
    private boolean updateInstance = true;

    private final Deque<ItemStack> inputBuffer = new LinkedList<>();
    private ItemStack outputBuffer = ItemStack.EMPTY;
    private ItemStack magazine = ItemStack.EMPTY;

    public AbstractRotarycannonBreechBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public int getQueueLimit() {
        return 5;
    }

    public Deque<ItemStack> getInputBuffer() {
        return this.inputBuffer;
    }

    public ItemStack getOutputBuffer() {
        return this.outputBuffer;
    }

    public void setMagazine(ItemStack stack) {
        this.magazine = stack;
        this.updateInstance = true;
    }

    public ItemStack getMagazine() {
        return this.magazine;
    }

    @Override
    public void tick() {
        super.tick();
        this.allTick(this.level);
    }

    @Override
    public void tickFromContraption(Level level, PitchOrientedContraptionEntity poce, BlockPos localPos) {
        super.tickFromContraption(level, poce, localPos);
        this.allTick(level);
        if (!level.isClientSide && this.updateInstance) {
            this.updateInstance = false;
            Contraption contraption = poce.getContraption();
            writeAndSyncSingleBlockData(this, contraption.getBlocks().get(localPos), poce, contraption);
        }
    }

    private void allTick(Level level) {
        if (this.fireRate < 0 || this.fireRate > 15) {
            this.fireRate = 0;
        }
        if (this.firingCooldown < 0) {
            this.firingCooldown = 0;
        }
        if (this.firingCooldown > 0) {
            this.firingCooldown--;
        }
        if (this.animateTicks < 5) {
            ++this.animateTicks;
        }
        if (this.animateTicks < 0) {
            this.animateTicks = 0;
        }
    }

    public void setFireRate(int power) {
        this.fireRate = Mth.clamp(power, 0, 15);
    }

    public int getFireRate() {
        return this.fireRate;
    }

    public int getActualFireRate() {
        if (this.fireRate < 1 || this.fireRate > 15) {
            return 0;
        }
        int cooldown = FIRE_RATES[this.fireRate - 1];
        return 1200 / cooldown;
    }

    public boolean canFire() {
        return this.getFireRate() > 0 && this.firingCooldown <= 0;
    }

    public void handleFiring() {
        if (this.fireRate > 0 && this.fireRate <= FIRE_RATES.length) {
            this.firingCooldown = FIRE_RATES[this.fireRate - 1];
            this.animateTicks = 0;
        }
    }

    /** 后坐动画位移：0 表示静止，正弦形（注意与中型炮的余弦形相反）。 */
    public float getAnimateOffset(float partialTicks) {
        float t = ((float) this.animateTicks + partialTicks) * 1.2f;
        if (t <= 0 || t >= 4.8f) {
            return 0;
        }
        float f = t < 1 ? t : (4.8f - t) / 3.8f;
        return Mth.sin(f * Mth.HALF_PI);
    }

    @Override
    public void incrementAnimationTicks() {
        ++this.animateTicks;
    }

    @Override
    public int getAnimationTicks() {
        return this.animateTicks;
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        this.fireRate = tag.getInt("FiringRate");
        this.firingCooldown = tag.getInt("Cooldown");
        this.animateTicks = tag.getInt("AnimateTicks");
        this.outputBuffer = tag.contains("Output")
                ? ItemStack.parseOptional(registries, tag.getCompound("Output"))
                : ItemStack.EMPTY;
        this.seat = DyeColor.byName(tag.getString("Seat"), null);

        this.inputBuffer.clear();
        ListTag inputTag = tag.getList("Input", Tag.TAG_COMPOUND);
        for (int i = 0; i < inputTag.size(); ++i) {
            this.inputBuffer.add(ItemStack.parseOptional(registries, inputTag.getCompound(i)));
        }
        this.magazine = tag.contains("Magazine")
                ? ItemStack.parseOptional(registries, tag.getCompound("Magazine"))
                : ItemStack.EMPTY;

        if (!clientPacket) {
            return;
        }
        this.updateInstance = tag.contains("UpdateInstance");
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putInt("FiringRate", this.fireRate);
        tag.putInt("Cooldown", this.firingCooldown);
        tag.putInt("AnimateTicks", this.animateTicks);
        if (this.outputBuffer != null && !this.outputBuffer.isEmpty()) {
            tag.put("Output", this.outputBuffer.save(registries));
        }
        if (this.seat != null) {
            tag.putString("Seat", this.seat.getSerializedName());
        }

        if (!this.inputBuffer.isEmpty()) {
            tag.put("Input", this.inputBuffer.stream()
                    .map(s -> s.save(registries))
                    .collect(Collectors.toCollection(ListTag::new)));
        }
        if (this.magazine != null && !this.magazine.isEmpty()) {
            tag.put("Magazine", this.magazine.save(registries));
        }

        if (!clientPacket) {
            return;
        }
        if (this.updateInstance) {
            tag.putBoolean("UpdateInstance", true);
        }
    }

    public boolean isInputFull() {
        return this.inputBuffer.size() >= this.getQueueLimit() || !this.magazine.isEmpty();
    }

    public boolean isOutputFull() {
        return !this.outputBuffer.isEmpty();
    }

    public ItemStack insertOutput(ItemStack stack) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (this.isOutputFull()) {
            return stack;
        }
        this.outputBuffer = stack;
        return ItemStack.EMPTY;
    }

    /** 从输入队列或弹匣里取下一发；弹匣只剩最后一发时标记需要同步。 */
    public ItemStack extractNextInput() {
        if (!this.inputBuffer.isEmpty()) {
            return this.inputBuffer.poll();
        }
        if (this.magazine.isEmpty()) {
            return ItemStack.EMPTY;
        }
        int totalCount = AutocannonAmmoContainerItem.getTotalAmmoCount(this.magazine);
        if (totalCount == 0) {
            return ItemStack.EMPTY;
        }
        if (totalCount == 1) {
            this.updateInstance = true;
        }
        return AutocannonAmmoContainerItem.pollItemFromContainer(this.magazine);
    }

    @Override
    public List<ItemStack> getDrops() {
        List<ItemStack> list = super.getDrops();
        for (ItemStack s : this.inputBuffer) {
            if (!s.isEmpty()) {
                list.add(s.copy());
            }
        }
        if (!this.outputBuffer.isEmpty()) {
            list.add(this.outputBuffer.copy());
        }
        if (!this.magazine.isEmpty()) {
            list.add(this.magazine.copy());
        }
        return list;
    }

    @Override
    protected AABB createRenderBoundingBox() {
        Direction dir = this.getBlockState().getValue(RotarycannonBreechBlock.FACING);
        Direction ammoContainerDir = dir.getAxis().isHorizontal()
                ? dir.getClockWise(Direction.Axis.Y)
                : Direction.EAST;
        return super.createRenderBoundingBox().expandTowards(
                new Vec3(dir.getOpposite().step()).add(new Vec3(ammoContainerDir.step())));
    }
}
