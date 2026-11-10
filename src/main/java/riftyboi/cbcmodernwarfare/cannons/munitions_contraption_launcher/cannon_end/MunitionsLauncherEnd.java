package riftyboi.cbcmodernwarfare.cannons.munitions_contraption_launcher.cannon_end;

import net.minecraft.util.StringRepresentable;

/**
 * 发射管口的开启状态。
 *
 * <p>1.20.1 原样迁移，零 API 变化（{@link StringRepresentable} 在 1.21.1 未改动）。
 *
 * <p>序列化名会写进方块状态与网络包，不要改名。
 */
public enum MunitionsLauncherEnd implements StringRepresentable {

    CLOSED("closed"),
    OPEN("open"),
    PARTIAL("partial");

    private final String serializedName;

    MunitionsLauncherEnd(String name) {
        this.serializedName = name;
    }

    @Override
    public String getSerializedName() {
        return this.serializedName;
    }

    /**
     * 按开启进度（0..1）推导状态：
     * {@code <= 0} 全闭、{@code >= 1} 全开、其余算半开。
     */
    public static MunitionsLauncherEnd getOpeningType(float openProgress) {
        return openProgress <= 0 ? CLOSED : openProgress >= 1 ? OPEN : PARTIAL;
    }
}
