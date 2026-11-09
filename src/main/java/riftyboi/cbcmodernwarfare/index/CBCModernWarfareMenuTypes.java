package riftyboi.cbcmodernwarfare.index;

import com.tterrag.registrate.util.entry.MenuEntry;

import riftyboi.cbcmodernwarfare.CBCModernWarfare;
import riftyboi.cbcmodernwarfare.munitions.autocannon.ammo_drum.AutocannonAmmoDrumMenu;
import riftyboi.cbcmodernwarfare.munitions.autocannon.ammo_drum.AutocannonAmmoDrumScreen;

/**
 * 本模组的菜单类型。
 *
 * <p><b>R6b 状态</b>：1.20.1 注册了 1 个（{@code autocannon_ammo_drum}），现在补齐。
 *
 * <p><b>关于「注册自定义 MenuType 会让客户端断线」的旧结论（R3 遗留，已推翻）</b>：
 * R3 的骨架注释曾担心「没装本模组的客户端会在注册表同步时被踢」。实测不成立，理由：
 * <ol>
 *   <li>CBC 5.11.7 自己在 {@code CBCMenuTypes} 里注册了 <b>5 个</b>自定义 {@code MenuType}
 *       （4 个引信设置容器 + {@code autocannon_ammo_container}），而它是主依赖 ——
 *       如果这个担心成立，任何只有 CBC 的客户端都连不进来。</li>
 *   <li>参考实现（0.0.6v，完整可运行的 1.21.1 移植）同样直接注册本菜单，未做任何规避。</li>
 *   <li>NeoForge 的注册表同步只对「标记为需同步」的注册表做严格校验；
 *       {@code minecraft:menu} 不在其中（菜单是打开时按 id 查的，不是连接期同步的）。</li>
 * </ol>
 * 所以这里按 1.20.1 原样注册，不做「改用 vanilla MenuType + 自定义通道」的规避。
 * 若将来真有断线报告，再回来改（登记在 {@code docs/log/15-Phase2-R6b.md}）。
 *
 * <p><b>1.20.1 → 1.21.1 的 API 变化</b>：Registrate 的 {@code menu(...)} 在 1.21.1 必须用
 * {@code ForgeMenuFactory} —— 它的 {@code create(MenuType, int, Inventory, RegistryFriendlyByteBuf)}
 * 比 1.20.1 的 {@code MenuFactory} <b>多一个 buf 参数</b>，由 Registrate 在客户端注入。
 * 因此 {@link AutocannonAmmoDrumMenu#getClientMenu} 也跟着多一个 buf 形参。
 */
public class CBCModernWarfareMenuTypes {

    public static final MenuEntry<AutocannonAmmoDrumMenu> AUTOCANNON_AMMO_DRUM = CBCModernWarfare.REGISTRATE
            .menu("autocannon_ammo_drum", AutocannonAmmoDrumMenu::getClientMenu,
                    () -> AutocannonAmmoDrumScreen::new)
            .register();

    /** 强制本类初始化，字段初始化时才真正注册。 */
    public static void register() {
        // 故意留空 —— 见类注释。
    }

    private CBCModernWarfareMenuTypes() {}
}
