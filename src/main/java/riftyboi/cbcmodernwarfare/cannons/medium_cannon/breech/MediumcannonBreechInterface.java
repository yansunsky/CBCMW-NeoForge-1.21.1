package riftyboi.cbcmodernwarfare.cannons.medium_cannon.breech;

import javax.annotation.Nonnull;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonAmmoItem;

/**
 * 中口径炮炮闩的 {@link IItemHandler} 门面。
 *
 * <p>两条入口共用它：
 * <ul>
 *   <li><b>装配态</b>：{@code MountedMediumcannonContraption#getItemStorage()} ——
 *       CBC 的 {@code CannonMountBlockEntity} 会把 {@code Capabilities.ItemHandler.BLOCK}
 *       转发进来，于是漏斗/管道能对着「火炮底座」抽空壳、塞弹药筒。</li>
 *   <li><b>未装配态</b>：{@link MediumcannonBreechBlockEntity#getItemHandler} ——
 *       方块自身的物品能力，机械臂对着炮闩方块也能直接投料。</li>
 * </ul>
 *
 * <p>槽位语义：
 * <ul>
 *   <li>槽 0 = 输出（开火后打空的弹壳），只出不进</li>
 *   <li>槽 1 = 输入（待装填的弹药筒），只进不出</li>
 * </ul>
 *
 * <p><b>为什么需要这个类</b>：1.20.1 的 {@code MountedMediumcannonContraption} 开火后是把
 * 空壳塞回 {@code inputBuffer}（{@code breech.setInputBuffer(ejectStack)}），于是「炮闩里躺着空壳、
 * 必须手动开闩抠出来」，而且外界没有任何可抽取的槽。这里把输入/输出拆成两个槽，
 * 空壳才可能被「火炮底座」上的漏斗抽走。
 */
public record MediumcannonBreechInterface(MediumcannonBreechBlockEntity breech) implements IItemHandler {

    @Override
    public int getSlots() {
        return 2;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return switch (slot) {
            case 0 -> this.breech.getOutputBuffer();
            case 1 -> this.breech.getInputBuffer();
            default -> ItemStack.EMPTY;
        };
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (slot != 1 || !this.isItemValid(slot, stack) || this.breech.isInputFull()
                || !this.breech.canBeAutomaticallyLoaded()) {
            return stack;
        }
        if (!simulate) {
            ItemStack load = stack.copyWithCount(1);
            this.breech.setInputBuffer(load);
        }
        return stack.getCount() <= 1 ? ItemStack.EMPTY : stack.copyWithCount(stack.getCount() - 1);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot != 0 || amount <= 0) {
            return ItemStack.EMPTY;
        }
        ItemStack out = this.breech.getOutputBuffer();
        if (out.isEmpty()) {
            return ItemStack.EMPTY;
        }
        int n = Math.min(amount, out.getCount());
        return simulate ? out.copyWithCount(n) : out.split(n);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return stack.getItem() instanceof MediumcannonAmmoItem;
    }
}
