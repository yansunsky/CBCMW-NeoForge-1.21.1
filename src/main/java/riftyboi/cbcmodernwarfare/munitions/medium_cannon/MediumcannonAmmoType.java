package riftyboi.cbcmodernwarfare.munitions.medium_cannon;

import net.minecraft.world.item.ItemStack;

/**
 * 弹药类型。三种容量目前都是 0（1.20.1 原样，未使用）。
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
