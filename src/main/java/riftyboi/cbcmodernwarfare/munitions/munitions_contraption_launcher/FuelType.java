package riftyboi.cbcmodernwarfare.munitions.munitions_contraption_launcher;

import net.minecraft.util.StringRepresentable;

/**
 * 发射器载具弹药的燃料类型。
 *
 * <p>1.20.1 原样迁移，无 API 变化（{@link StringRepresentable} 在 1.21.1 未改动）。
 *
 * <p>序列化名 {@code "solid"} / {@code "liquid"} 会写进方块状态与网络包，
 * **不要改名**，否则旧的存档与客户端会解析失败。
 */
public enum FuelType implements StringRepresentable {

    SOLID_FUEL("solid"),
    LIQUID_FUEL("liquid");

    private final String serializedName;

    FuelType(String name) {
        this.serializedName = name;
    }

    @Override
    public String getSerializedName() {
        return this.serializedName;
    }
}
