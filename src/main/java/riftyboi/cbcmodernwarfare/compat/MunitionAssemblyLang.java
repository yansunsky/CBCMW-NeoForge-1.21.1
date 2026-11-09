package riftyboi.cbcmodernwarfare.compat;

import riftyboi.cbcmodernwarfare.CBCModernWarfare;

/**
 * 与 JEI 展示配方相关的语言键。
 *
 * <p><b>为什么单独放这里</b>：这些键要走 {@code REGISTRATE.addRawLang} 才能进
 * datagen 输出的 {@code en_us.json}，而 datagen 在 mod 初始化阶段就跑完了 ——
 * 不能放在 {@code @JeiPlugin} 里（那时 datagen 早结束，且服务端根本不加载 JEI 类）。
 * 放在这个**不引用任何 JEI 类型**的类里，服务端也能安全加载。
 *
 * <p><b>R6a 起也承担普通 tooltip 键</b>（{@code tooltip.cbcmodernwarfare.*}）——
 * 机炮 mixin 在服务端与客户端都会加载，把 lang 键注册留在 init() 阶段最稳，
 * 不需要为此再开一个类。
 *
 * <p>中文写在 {@code src/main/resources/assets/cbcmodernwarfare/lang/zh_cn.json}。
 */
public final class MunitionAssemblyLang {

    private static final String BASE = "tooltip." + CBCModernWarfare.MOD_ID + ".jei_info.";

    /** 通用 tooltip 键的前缀（不带 {@code jei_info} 那一段）。 */
    private static final String TOOLTIP = "tooltip." + CBCModernWarfare.MOD_ID + ".";

    private MunitionAssemblyLang() {
    }

    /** 在 {@code CBCModernWarfare.init()} 里调用。 */
    public static void register() {
        CBCModernWarfare.REGISTRATE.addRawLang(BASE + "assembled", "Assembled Cartridge");
        CBCModernWarfare.REGISTRATE.addRawLang(BASE + "added_fuze", "+ Fuze");
        CBCModernWarfare.REGISTRATE.addRawLang(BASE + "has_fuze", "Fuze installed");
        CBCModernWarfare.REGISTRATE.addRawLang(BASE + "fuze_removed", "Fuze removed");
        CBCModernWarfare.REGISTRATE.addRawLang(BASE + "added_tracer", "+ Tracer");

        // R6a：机炮弹药的燃烧剂 tooltip（3 个 mixin 都用这一条）。
        // 键名照 1.20.1 的 en_us.json："tooltip.cbcmodernwarfare.incendiary"。
        CBCModernWarfare.REGISTRATE.addRawLang(TOOLTIP + "incendiary", "+ Incendiary");
    }
}
