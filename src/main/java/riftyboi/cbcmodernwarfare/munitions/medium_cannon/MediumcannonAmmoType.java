package riftyboi.cbcmodernwarfare.munitions.medium_cannon;

import net.minecraft.world.item.ItemStack;

/**
 * 弹药按「装填容器」分类：中型炮 / 机炮 / 无。
 *
 * <p><b>1.20.1 → 1.21.1</b>：零变化。三个枚举值的 {@code getCapacity()} 在 1.20.1
 * 就全部返回 0（预留接口，实际没用），保持原样不改语义。
 */
public enum MediumcannonAmmoType {

    MEDIUMCANNON {
        @Override
        public int getCapacity() {
            return 0;
        }
    },
    MACHINE_GUN {
        @Override
        public int getCapacity() {
            return 0;
        }
    },
    NONE {
        @Override
        public int getCapacity() {
            return 0;
        }
    };

    public abstract int getCapacity();

    public static MediumcannonAmmoType of(ItemStack stack) {
        return stack.getItem() instanceof MediumcannonAmmoItem item ? item.getType() : NONE;
    }
}
