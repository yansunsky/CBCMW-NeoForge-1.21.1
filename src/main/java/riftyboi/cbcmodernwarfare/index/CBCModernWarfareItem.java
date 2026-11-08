package riftyboi.cbcmodernwarfare.index;

import static riftyboi.cbcmodernwarfare.CBCModernWarfare.REGISTRATE;

import com.tterrag.registrate.util.entry.ItemEntry;

import net.minecraft.world.item.Item;

import riftyboi.cbcmodernwarfare.datagen.assets.CBCModernWarfareBuilderTransformers;

/**
 * 本模组不依附方块的物品。
 *
 * <p><b>状态</b>：1.20.1 在这里注册了 25 个 {@code ItemEntry}。R4a 只补中型炮装配链
 * 直接需要的三样（四种材质的「落闩」+ 重型后坐弹簧），其余随对应轮次补齐：
 * <ul>
 *   <li>R5 — 弹药（弹壳、弹头、发射药等）</li>
 *   <li>R6 — 机炮弹药、燃烧剂、碳钢/硅锭</li>
 * </ul>
 *
 * <p><b>1.20.1 → 1.21.1</b>：{@code ItemBuilder} 的链式 API 未变
 * （{@code model} / {@code lang} / {@code tag} / {@code properties} / {@code register}）。
 * 物品 id 必须保持 1.20.1 原值 —— 存档与配方都按 id 引用。
 */
public class CBCModernWarfareItem {

    // ------------------------------------------------------------------------------------
    // 落闩（Falling Breech）—— 未完成炮闩补齐时消耗
    // ------------------------------------------------------------------------------------
    //
    // 模型来自 block/mediumcannon/falling_breech，只换 material 贴图；
    // helper 见 datagen/assets/CBCModernWarfareBuilderTransformers#mediumcannonBreechBlock。

    public static final ItemEntry<Item> CAST_IRON_MEDIUMCANNON_FALLING_BREECH = REGISTRATE
            .item("cast_iron_mediumcannon_falling_breech", Item::new)
            .transform(CBCModernWarfareBuilderTransformers.mediumcannonBreechBlock("mediumcannon/cast_iron_medium"))
            .register();

    public static final ItemEntry<Item> BRONZE_MEDIUMCANNON_FALLING_BREECH = REGISTRATE
            .item("bronze_mediumcannon_falling_breech", Item::new)
            .transform(CBCModernWarfareBuilderTransformers.mediumcannonBreechBlock("mediumcannon/bronze_medium"))
            .register();

    public static final ItemEntry<Item> STEEL_MEDIUMCANNON_FALLING_BREECH = REGISTRATE
            .item("steel_mediumcannon_falling_breech", Item::new)
            .transform(CBCModernWarfareBuilderTransformers.mediumcannonBreechBlock("mediumcannon/steel_medium"))
            .register();

    public static final ItemEntry<Item> NETHERSTEEL_MEDIUMCANNON_FALLING_BREECH = REGISTRATE
            .item("nethersteel_mediumcannon_falling_breech", Item::new)
            .transform(CBCModernWarfareBuilderTransformers.mediumcannonBreechBlock("mediumcannon/nethersteel_medium"))
            .register();

    // ------------------------------------------------------------------------------------
    // 转管炮炮闩取出器（R4b-2）—— 未完成转管炮炮闩补齐时消耗
    // ------------------------------------------------------------------------------------
    //
    // 修复：1.20.1 原版 STEEL 那个的注册 id 误写成 "steel_rotaryannon_breech_extractor"
    // （rotaryannon 少一个 c），另两个都是正确的 "rotarycannon"。
    // 项目尚未发布，没有外部存档或配方引用这个 id，现在改成本最低。
    // lang 键也随之改为正确的拼写。

    public static final ItemEntry<Item> BRONZE_ROTARYCANNON_BREECH_EXTRACTOR = REGISTRATE
            .item("bronze_rotarycannon_breech_extractor", Item::new)
            .transform(CBCModernWarfareBuilderTransformers.mediumcannonBreechBlock("mediumcannon/bronze_medium"))
            .register();

    public static final ItemEntry<Item> STEEL_ROTARYCANNON_BREECH_EXTRACTOR = REGISTRATE
            .item("steel_rotarycannon_breech_extractor", Item::new)
            .transform(CBCModernWarfareBuilderTransformers.mediumcannonBreechBlock("mediumcannon/steel_medium"))
            .register();

    public static final ItemEntry<Item> NETHERSTEEL_ROTARYCANNON_BREECH_EXTRACTOR = REGISTRATE
            .item("nethersteel_rotarycannon_breech_extractor", Item::new)
            .transform(CBCModernWarfareBuilderTransformers.mediumcannonBreechBlock("mediumcannon/nethersteel_medium"))
            .register();

    // ------------------------------------------------------------------------------------
    // 重型后坐弹簧 —— 未完成后坐炮管补齐时消耗（四种材质共用）
    // ------------------------------------------------------------------------------------

    public static final ItemEntry<Item> HEAVY_RECOIL_SPRING = REGISTRATE
            .item("heavy_recoil_spring", Item::new)
            .register();

    // TODO(R5): filled/empty mediumcannon cartridge、cartridge sheet、九组弹药。
    // TODO(R6): 机炮弹药、燃烧剂、碳钢/硅锭、烟雾发射器弹。

    /** 强制本类初始化，字段初始化时才真正注册。 */
    public static void register() {
        // 故意留空 —— 见类注释。
    }

    private CBCModernWarfareItem() {}
}
