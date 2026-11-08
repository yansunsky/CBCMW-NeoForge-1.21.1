package riftyboi.cbcmodernwarfare.index;

import java.util.function.Supplier;

import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.content.contraptions.Contraption;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import riftyboi.cbcmodernwarfare.CBCModernWarfare;
import riftyboi.cbcmodernwarfare.cannon_control.contraption.MountedMediumcannonContraption;

/**
 * 本模组的 contraption 类型。
 *
 * <p><b>1.20.1 → 1.21.1 的关键改动</b>：Create 6.0.10 把 {@link ContraptionType} 变成了
 * <b>注册表对象</b>，不再是 1.20.1 那种随便 {@code new} 的类。证据（javap）：
 * <ul>
 *   <li>{@code ContraptionType} 有 {@code public final Holder.Reference<ContraptionType> holder}</li>
 *   <li>{@code AllContraptionTypes} 内部走 {@code register(String, Supplier)}</li>
 *   <li>注册表 key 是 {@link CreateRegistries#CONTRAPTION_TYPE}
 *       —— <b>Create 自己的注册表，不是 {@code NeoForgeRegistries}</b>。
 *       这一点踩过一次：先写了 {@code NeoForgeRegistries.Keys.CONTRAPTION_TYPE}，编译期就报找不到符号。</li>
 * </ul>
 *
 * <p><b>为什么只注册 1 个（1.20.1 是 4 个）</b>：另外三个
 * （`MOUNTED_MUNITIONS_LAUNCHER` / `MOUNTED_ROTARYCANNON` / `MUNITIONS_PHYSICS`）
 * 依赖还没迁的 contraption 类，随它们的轮次补。
 */
public class CBCModernWarfareContraptionTypes {

    /** contraption 类型注册表（来自 Create，不是 NeoForge）。 */
    public static final DeferredRegister<ContraptionType> CONTRAPTION_TYPES =
            DeferredRegister.create(CreateRegistries.CONTRAPTION_TYPE, CBCModernWarfare.MOD_ID);

    /** 中型炮：装配成 contraption 后挂在炮架上。 */
    public static final DeferredHolder<ContraptionType, ContraptionType> MOUNTED_MEDIUMCANNON =
            register("mounted_mediumcannon", MountedMediumcannonContraption::new);

    // TODO(R4b-2): MOUNTED_MUNITIONS_LAUNCHER / MOUNTED_ROTARYCANNON / MUNITIONS_PHYSICS。

    private static DeferredHolder<ContraptionType, ContraptionType> register(String name,
            Supplier<? extends Contraption> factory) {
        return CONTRAPTION_TYPES.register(name, () -> new ContraptionType(factory));
    }

    /** 把延迟注册器挂到模组事件总线上。 */
    public static void register(IEventBus modEventBus) {
        CONTRAPTION_TYPES.register(modEventBus);
    }

    private CBCModernWarfareContraptionTypes() {}
}
