package riftyboi.cbcmodernwarfare.cannons.medium_cannon.breech;

import static rbasamoyai.createbigcannons.cannons.big_cannons.BigCannonBlock.writeAndSyncSingleBlockData;

import java.util.List;

import com.simibubi.create.content.contraptions.Contraption;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;
import rbasamoyai.createbigcannons.config.CBCConfigs;

import riftyboi.cbcmodernwarfare.cannons.medium_cannon.AnimatedMediumcannon;
import riftyboi.cbcmodernwarfare.cannons.medium_cannon.MediumcannonBlockEntity;

/**
 * 中型炮炮闩的方块实体：开闩进度、装弹缓冲与后坐动画。
 *
 * <p><b>1.20.1 → 1.21.1 的三处改动</b>：
 * <ol>
 *   <li>{@code write}/{@code read} 多了 {@link HolderLookup.Provider} 参数
 *       （{@code SmartBlockEntity} 的新签名）。</li>
 *   <li>{@link ItemStack} 的 NBT 读写改成组件感知的：
 *       {@code ItemStack.of(tag)} → {@code ItemStack.parseOptional(registries, tag)}，
 *       {@code stack.save(new CompoundTag())} → {@code stack.save(registries)}。</li>
 *   <li>{@code writeAndSyncSingleBlockData} 仍来自 {@code BigCannonBlock}，签名未变。</li>
 * </ol>
 */
public class MediumcannonBreechBlockEntity extends MediumcannonBlockEntity implements AnimatedMediumcannon {

    private int animateTicks = 5;
    private int power = 5;
    private int openProgress;
    private int openDirection;
    private int loadingCooldown;

    private ItemStack inputBuffer = ItemStack.EMPTY;

    public MediumcannonBreechBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void handleFiring() {
        this.animateTicks = 0;
    }

    public void setPower(int p) {
        this.power = p * 2;
    }

    /** 后坐动画的余弦位移：0 表示完全缩回，1 表示复位。 */
    public float getAnimateOffset(float partialTicks) {
        float t = ((float) this.animateTicks + partialTicks) * 1.2f;
        if (t <= 0 || t >= ((float) this.power - 0.2f)) {
            return 1;
        }
        float f = t < 1 ? t : (((float) this.power - 0.2f) - t) / ((float) this.power - 1.2f);
        return Mth.cos(f * Mth.HALF_PI);
    }

    public ItemStack getInputBuffer() {
        return this.inputBuffer;
    }

    @Override
    public void tick() {
        super.tick();
        this.animateTicks = this.power;
        if (this.openProgress != 0) {
            this.openProgress = 0;
        }
        if (this.openDirection != 0) {
            this.openDirection = 0;
        }
        if (this.loadingCooldown != 0) {
            this.loadingCooldown = 0;
        }
    }

    private void allTick() {
        if (this.animateTicks < this.power) {
            ++this.animateTicks;
        }
        if (this.animateTicks < 0) {
            this.animateTicks = 0;
        }
    }

    public void tickAnimation() {
        if (this.openDirection != 0 && !isInstantOpen()) {
            this.openProgress = Mth.clamp(this.openProgress + this.openDirection, 0, Math.max(getOpeningTime(), 1));
            if (!this.onInteractionCooldown()) {
                this.openDirection = 0;
            }
        }
        if (this.loadingCooldown > 0) {
            --this.loadingCooldown;
        }
    }

    @Override
    public void tickFromContraption(Level level, PitchOrientedContraptionEntity poce, BlockPos localPos) {
        super.tickFromContraption(level, poce, localPos);
        this.allTick();
        this.tickAnimation();
        if (!level.isClientSide) {
            Contraption contraption = poce.getContraption();
            writeAndSyncSingleBlockData(this, contraption.getBlocks().get(localPos), poce, contraption);
        }
    }

    public boolean canFire() {
        return this.openProgress == 0;
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
        this.animateTicks = tag.getInt("AnimateTicks");
        this.power = tag.getInt("RecoilPower");
        this.inputBuffer = tag.contains("Input")
                ? ItemStack.parseOptional(registries, tag.getCompound("Input"))
                : ItemStack.EMPTY;
        this.openProgress = tag.getInt("OpenProgress");
        this.openDirection = Mth.clamp(tag.getInt("OpenDirection"), -1, 1);
        this.loadingCooldown = Math.max(0, tag.getInt("LoadingCooldown"));
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putInt("OpenProgress", this.openProgress);
        tag.putInt("OpenDirection", this.openDirection);
        tag.putInt("LoadingCooldown", this.loadingCooldown);
        tag.putInt("AnimateTicks", this.animateTicks);
        tag.putInt("RecoilPower", this.power);
        if (this.inputBuffer != null && !this.inputBuffer.isEmpty()) {
            tag.put("Input", this.inputBuffer.save(registries));
        }
    }

    public boolean isInputFull() {
        return !this.inputBuffer.isEmpty();
    }

    public void setInputBuffer(ItemStack stack) {
        this.inputBuffer = stack;
    }

    public void clearInputBuffer() {
        this.inputBuffer = ItemStack.EMPTY;
    }

    @Override
    public List<ItemStack> getDrops() {
        List<ItemStack> list = super.getDrops();
        if (!this.inputBuffer.isEmpty()) {
            list.add(this.inputBuffer.copy());
        }
        return list;
    }

    public boolean isOpen() {
        return isInstantOpen() ? this.openProgress > 0 : this.openProgress >= getOpeningTime();
    }

    public int getOpenDirection() {
        return this.openDirection;
    }

    public void toggleOpening() {
        if (isInstantOpen()) {
            this.openProgress = this.openProgress > 0 ? 0 : 1;
            return;
        }
        if (!this.onInteractionCooldown()) {
            this.openDirection = this.isOpen() ? -1 : 1;
        }
    }

    public void setAnimateTicks(int ticks) {
        this.animateTicks = ticks;
    }

    public int getPower() {
        return this.power;
    }

    public float getOpenProgress(float partialTicks) {
        if (isInstantOpen()) {
            return Mth.clamp(this.openProgress, 0.0f, 1.0f);
        }
        return Mth.clamp((this.openProgress + this.openDirection * partialTicks) / getOpeningTime(), 0.0f, 1.0f);
    }

    public int getOpenProgress() {
        return this.openProgress;
    }

    public boolean onInteractionCooldown() {
        return 0 < this.openProgress && this.openProgress < getOpeningTime();
    }

    public boolean canBeAutomaticallyLoaded() {
        return this.loadingCooldown <= 0 && this.openProgress == 0;
    }

    public void setLoadingCooldown(int value) {
        this.loadingCooldown = value;
    }

    public static int getOpeningTime() {
        return CBCConfigs.server().cannons.quickfiringBreechOpeningCooldown.get();
    }

    public static boolean isInstantOpen() {
        return getOpeningTime() <= 0;
    }
}
