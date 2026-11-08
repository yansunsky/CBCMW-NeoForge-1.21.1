package riftyboi.cbcmodernwarfare.index;

import dev.engine_room.flywheel.api.visualization.VisualizerRegistry;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;

import riftyboi.cbcmodernwarfare.cannons.medium_cannon.breech.MediumcannonBreechVisual;
import riftyboi.cbcmodernwarfare.cannons.medium_cannon.recoil_barrel.MediumcannonRecoilBarrelVisual;

/**
 * Flywheel 的方块实体视觉注册。
 *
 * <p><b>1.20.1 → 1.21.1</b>：{@code VisualizerRegistry.setVisualizer} 与
 * {@code new SimpleBlockEntityVisualizer(Factory, Predicate)} 签名都未变
 * （Flywheel 1.0.6 javap 核对）。
 *
 * <p><b>泛型注意</b>：{@code setVisualizer} 的签名是
 * {@code <T extends BlockEntity> void setVisualizer(BlockEntityType<T>, BlockEntityVisualizer<? super T>)}。
 * 一开始把 {@code BlockEntityType<?>} 强转成 {@code BlockEntityType<SmartBlockEntity>}，
 * 导致 T 被推断成父类、visual 类型对不上而编译失败。正确做法是让 T 由
 * {@code BlockEntityEntry#get()} 自己推断 —— 所以下面两行写的是
 * {@code CBCModernWarfareBlockEntities.MEDIUMCANNON_BREECH.get()} 的裸类型，
 * 不做任何强转。
 *
 * <p><b>只能在客户端调用</b>：本类引用 {@code MediumcannonBreechVisual} 等客户端类，
 * 由 {@code CBCModernWarfareClientCommon#onClientSetup()} 触发，而后者是
 * {@code @OnlyIn(Dist.CLIENT)}。
 */
public class CBCModernWarfareFlywheelVisuals {

    public static void registerVisuals() {
        VisualizerRegistry.setVisualizer(
                CBCModernWarfareBlockEntities.MEDIUMCANNON_BREECH.get(),
                new SimpleBlockEntityVisualizer<>(MediumcannonBreechVisual::new, be -> false));
        VisualizerRegistry.setVisualizer(
                CBCModernWarfareBlockEntities.MEDIUMCANNON_RECOIL_BARREL.get(),
                new SimpleBlockEntityVisualizer<>(MediumcannonRecoilBarrelVisual::new, be -> false));
    }

    // TODO(R4b-2): rotarycannon 与 launcher 的 visual（随它们的类一起迁）。

    private CBCModernWarfareFlywheelVisuals() {}
}
