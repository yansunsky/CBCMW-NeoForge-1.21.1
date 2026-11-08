package riftyboi.cbcmodernwarfare.cannon_control.cannon_types;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import net.minecraft.resources.ResourceLocation;

import rbasamoyai.createbigcannons.cannon_control.cannon_types.CannonContraptionTypeRegistry;
import rbasamoyai.createbigcannons.cannon_control.cannon_types.ICannonContraptionType;

import riftyboi.cbcmodernwarfare.CBCModernWarfare;

/**
 * 本模组的火炮「类型」（用于 CBC 的炮弹弹道/后坐配置查找）。
 *
 * <p><b>1.20.1 → 1.21.1</b>：{@code ICannonContraptionType} 只有一个 {@code getId()}，
 * {@code CannonContraptionTypeRegistry.register(ResourceLocation, T)} 签名未变。
 * 枚举常量名即注册路径（转小写），必须与 1.20.1 一致 —— 它会被写进 contraption 的 NBT。
 */
public enum CBCMWCannonContraptionTypes implements ICannonContraptionType {

    MEDIUMCANNON,
    MOUNTED_LAUNCHER,
    ROTARYCANNON;

    private static final Map<ResourceLocation, CBCMWCannonContraptionTypes> BY_ID =
            Arrays.stream(values()).collect(Collectors.toMap(CBCMWCannonContraptionTypes::getId, Function.identity()));

    private final ResourceLocation id = CBCModernWarfare.resource(this.name().toLowerCase(Locale.ROOT));

    CBCMWCannonContraptionTypes() {
        CannonContraptionTypeRegistry.register(this.id, this);
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Nullable
    public static CBCMWCannonContraptionTypes byId(ResourceLocation loc) {
        return BY_ID.get(loc);
    }

    /** 强制类初始化，枚举常量构造时即完成注册。 */
    public static void register() {
        // 故意留空 —— 见类注释。
    }
}
