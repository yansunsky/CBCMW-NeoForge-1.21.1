package riftyboi.cbcmodernwarfare.cannons.rotarycannon.breech;

import java.util.Deque;

import javax.annotation.Nonnull;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import rbasamoyai.createbigcannons.munitions.FuzedItemMunition;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonAmmoItem;

/**
 * 转管炮炮闩的 {@link IItemHandler} 门面 —— 让炮车（{@code CannonCarriageEntity}）与
 * 机械臂（deployer）能把弹药塞进炮闩的输入队列、把空弹壳取走。
 *
 * <p><b>为什么需要它</b>：CBC 的 {@code CannonCarriageEntity} 装填逻辑是
 * 「{@code entity instanceof ItemCannon} → {@code insertItemIntoCannon(stack, false)}」。
 * 1.20.1 原项目的 {@code MountedRotarycannonContraption} 里那两个方法是**桩**
 * （直接返回入参/空栈），真正的实现在 forge 侧的
 * {@code RotaryCannonContraptionMixin} 里 —— 它 override 成走
 * {@code getItemStorage().map(h -> h.insertItem(1, stack, simulate))}。
 * 迁移时这个 mixin 连同 {@code RotarycannonBreechInterface} 一起丢了，
 * 于是炮能装配、能手动开火，但**炮车/机械臂塞不进弹**（塞进去也立刻退回）。
 *
 * <p><b>1.21.1 的形态变化</b>：{@code GetItemStorage} 从
 * {@code createbigcannons.forge.mixin_interface} 挪到了
 * {@code createbigcannons.remix}，且**返回值不再是 {@code LazyOptional<IItemHandler>}，
 * 而是裸 {@code IItemHandler}（可为 null）**。CBC 自己的
 * {@code MountedAutocannonContraption#getItemStorage()} 也是这么写的（javap 已核）。
 * 所以这里照 CBC 的形态走，不再用 LazyOptional。
 *
 * <p>槽位语义（与 1.20.1 逐行一致）：
 * <ul>
 *   <li>槽 0 = 输出（打空的弹壳）</li>
 *   <li>槽 1 = 输入队列（待击发的弹药筒）</li>
 * </ul>
 */
public record RotarycannonBreechInterface(AbstractRotarycannonBreechBlockEntity breech) implements IItemHandler {

    @Override
    public int getSlots() {
        return 2;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        Deque<ItemStack> inputBuffer = this.breech.getInputBuffer();
        return switch (slot) {
            case 0 -> this.breech.getOutputBuffer();
            case 1 -> this.breech.isInputFull() && !inputBuffer.isEmpty()
                    ? inputBuffer.peekLast()
                    : ItemStack.EMPTY;
            default -> ItemStack.EMPTY;
        };
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (slot != 1 || !this.isItemValid(slot, stack) || this.breech.isInputFull()) {
            return stack;
        }
        int maxCount = Math.min(this.breech.getQueueLimit() - this.breech.getInputBuffer().size(),
                stack.getCount());
        if (!simulate) {
            for (int i = 0; i < maxCount; ++i) {
                this.breech.getInputBuffer().add(stack.copyWithCount(1));
            }
        }
        return stack.getCount() == maxCount
                ? ItemStack.EMPTY
                : stack.copyWithCount(stack.getCount() - maxCount);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0) {
            return ItemStack.EMPTY;
        }
        return switch (slot) {
            case 0 -> simulate
                    ? this.breech.getOutputBuffer().copyWithCount(1)
                    : this.breech.getOutputBuffer().split(1);
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return stack.getItem() instanceof AutocannonAmmoItem
                && !(stack.getItem() instanceof FuzedItemMunition);
    }
}
