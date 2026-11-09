package riftyboi.cbcmodernwarfare.index;

import static riftyboi.cbcmodernwarfare.CBCModernWarfare.REGISTRATE;

import com.tterrag.registrate.util.entry.ItemEntry;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

import rbasamoyai.createbigcannons.CBCTags.CBCItemTags;

import riftyboi.cbcmodernwarfare.datagen.assets.CBCModernWarfareBuilderTransformers;
import riftyboi.cbcmodernwarfare.munitions.autocannon.apds.APDSAutocannonRoundItem;
import riftyboi.cbcmodernwarfare.munitions.autocannon.canister.CanisterAutocannonRoundItem;
import riftyboi.cbcmodernwarfare.munitions.autocannon.he.ExplosiveAutocannonRoundItem;
import riftyboi.cbcmodernwarfare.munitions.autocannon.hvap.HVAPAutocannonRoundItem;
import riftyboi.cbcmodernwarfare.munitions.smoke_discharger.SmokeDischargerRoundItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.ap.APMediumcannonCartridgeItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.ap.APMediumcannonRoundItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.apds.APDSMediumcannonCartridgeItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.apds.APDSMediumcannonRoundItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.apfsds.APFSDSMediumcannonCartridgeItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.apfsds.APFSDSMediumcannonRoundItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.aphe.APHEMediumcannonCartridgeItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.aphe.APHEMediumcannonRoundItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.canister.CanisterMediumcannonCartridgeItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.canister.CanisterMediumcannonRoundItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.he.HEMediumcannonCartridgeItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.he.HEMediumcannonRoundItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.heap.HEAPMediumcannonCartridgeItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.heap.HEAPMediumcannonRoundItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.hefrag.HEFMediumcannonCartridgeItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.hefrag.HEFMediumcannonRoundItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.smoke.SmokeMediumcannonCartridgeItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.smoke.SmokeMediumcannonRoundItem;

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
    // R5 — 中型炮弹药
    // ------------------------------------------------------------------------------------
    //
    // 结构：
    //   *_MEDIUMCANNON_ROUND      = 弹头（可装引信、可标曳光）
    //   *_MEDIUMCANNON_CARTRIDGE  = 弹药筒（装弹头用，不在创造栏搜索里单独出现）
    //   EMPTY_MEDIUMCANNON_CARTRIDGE = 打完的空壳
    //
    // 9 种弹头：AP / APDS / APFSDS / APHE / CANISTER / HE / HEF / HEAP / SMOKE。
    // APDS 与 APFSDS 共用 INERT 属性，与 1.20.1 一致。

    public static final ItemEntry<Item> EMPTY_MEDIUMCANNON_CARTRIDGE = REGISTRATE
            .item("empty_mediumcannon_cartridge", Item::new)
            .model((c, p) -> {})
            .register();

    /**
     * 弹药筒的「空筒」—— 组装配方 {@code CartridgeAssemblyRecipe} 的输入之一。
     *
     * <p>名字里的 "filled" 是 1.20.1 的历史包袱（装配链早期设计的遗留），
     * 它其实是**空筒**：打完炮退出来的是 {@link #EMPTY_MEDIUMCANNON_CARTRIDGE}，
     * 往前装弹用的是这个。两个 id 都按 1.20.1 原样保留，不要「顺手改名」。
     *
     * <p>模型与贴图在 {@code src/main/resources} 里手写（与 EMPTY 一样走
     * {@code .model((c, p) -> {})} 让 datagen 不要生成）。
     */
    public static final ItemEntry<Item> FILLED_MEDIUMCANNON_CARTRIDGE = REGISTRATE
            .item("filled_mediumcannon_cartridge", Item::new)
            .model((c, p) -> {})
            .register();

    // ---------------------------------------------------------------- AP

    public static final ItemEntry<APMediumcannonCartridgeItem> AP_MEDIUMCANNON_CARTRIDGE = REGISTRATE
            .item("ap_mediumcannon_cartridge", APMediumcannonCartridgeItem::new)
            .model((c, p) -> {})
            .removeTab(CreativeModeTabs.SEARCH)
            .model((c, p) -> {})
            .register();

    public static final ItemEntry<APMediumcannonRoundItem> AP_MEDIUMCANNON_ROUND = REGISTRATE
            .item("ap_mediumcannon_round", APMediumcannonRoundItem::new)
            .lang("Armor Piercing Mediumcannon Round")
            .model((c, p) -> {})
            .register();

    // ---------------------------------------------------------------- APDS

    public static final ItemEntry<APDSMediumcannonCartridgeItem> APDS_MEDIUMCANNON_CARTRIDGE = REGISTRATE
            .item("apds_mediumcannon_cartridge", APDSMediumcannonCartridgeItem::new)
            .model((c, p) -> {})
            .removeTab(CreativeModeTabs.SEARCH)
            .model((c, p) -> {})
            .register();

    public static final ItemEntry<APDSMediumcannonRoundItem> APDS_MEDIUMCANNON_ROUND = REGISTRATE
            .item("apds_mediumcannon_round", APDSMediumcannonRoundItem::new)
            .lang("Armor Piercing Discarding Sabot Round")
            .model((c, p) -> {})
            .register();

    // ---------------------------------------------------------------- APFSDS

    public static final ItemEntry<APFSDSMediumcannonCartridgeItem> APFSDS_MEDIUMCANNON_CARTRIDGE = REGISTRATE
            .item("apfsds_mediumcannon_cartridge", APFSDSMediumcannonCartridgeItem::new)
            .model((c, p) -> {})
            .removeTab(CreativeModeTabs.SEARCH)
            .model((c, p) -> {})
            .register();

    public static final ItemEntry<APFSDSMediumcannonRoundItem> APFSDS_MEDIUMCANNON_ROUND = REGISTRATE
            .item("apfsds_mediumcannon_round", APFSDSMediumcannonRoundItem::new)
            .lang("Armor Piercing Discarding Sabot Round")
            .model((c, p) -> {})
            .register();

    // ---------------------------------------------------------------- APHE

    public static final ItemEntry<APHEMediumcannonCartridgeItem> APHE_MEDIUMCANNON_CARTRIDGE = REGISTRATE
            .item("aphe_mediumcannon_cartridge", APHEMediumcannonCartridgeItem::new)
            .model((c, p) -> {})
            .removeTab(CreativeModeTabs.SEARCH)
            .model((c, p) -> {})
            .register();

    public static final ItemEntry<APHEMediumcannonRoundItem> APHE_MEDIUMCANNON_ROUND = REGISTRATE
            .item("aphe_mediumcannon_round", APHEMediumcannonRoundItem::new)
            .lang("Armor Piercing High Explosive Mediumcannon Round")
            .model((c, p) -> {})
            .register();

    // ---------------------------------------------------------------- CANISTER

    public static final ItemEntry<CanisterMediumcannonCartridgeItem> CANISTER_MEDIUMCANNON_CARTRIDGE = REGISTRATE
            .item("canister_mediumcannon_cartridge", CanisterMediumcannonCartridgeItem::new)
            .model((c, p) -> {})
            .removeTab(CreativeModeTabs.SEARCH)
            .model((c, p) -> {})
            .register();

    public static final ItemEntry<CanisterMediumcannonRoundItem> CANISTER_MEDIUMCANNON_ROUND = REGISTRATE
            .item("canister_mediumcannon_round", CanisterMediumcannonRoundItem::new)
            .lang("Canister Mediumcannon Round")
            .model((c, p) -> {})
            .register();

    // ---------------------------------------------------------------- HE

    public static final ItemEntry<HEMediumcannonCartridgeItem> HE_MEDIUMCANNON_CARTRIDGE = REGISTRATE
            .item("he_mediumcannon_cartridge", HEMediumcannonCartridgeItem::new)
            .model((c, p) -> {})
            .removeTab(CreativeModeTabs.SEARCH)
            .model((c, p) -> {})
            .register();

    public static final ItemEntry<HEMediumcannonRoundItem> HE_MEDIUMCANNON_ROUND = REGISTRATE
            .item("he_mediumcannon_round", HEMediumcannonRoundItem::new)
            .lang("High Explosive Mediumcannon Round")
            .model((c, p) -> {})
            .register();

    // ---------------------------------------------------------------- HEF

    public static final ItemEntry<HEFMediumcannonCartridgeItem> HEF_MEDIUMCANNON_CARTRIDGE = REGISTRATE
            .item("hef_mediumcannon_cartridge", HEFMediumcannonCartridgeItem::new)
            .model((c, p) -> {})
            .removeTab(CreativeModeTabs.SEARCH)
            .model((c, p) -> {})
            .register();

    public static final ItemEntry<HEFMediumcannonRoundItem> HEF_MEDIUMCANNON_ROUND = REGISTRATE
            .item("hef_mediumcannon_round", HEFMediumcannonRoundItem::new)
            .lang("High Explosive Fragmentation Mediumcannon Round")
            .model((c, p) -> {})
            .register();

    // ---------------------------------------------------------------- HEAP

    public static final ItemEntry<HEAPMediumcannonCartridgeItem> HEAP_MEDIUMCANNON_CARTRIDGE = REGISTRATE
            .item("heap_mediumcannon_cartridge", HEAPMediumcannonCartridgeItem::new)
            .model((c, p) -> {})
            .removeTab(CreativeModeTabs.SEARCH)
            .model((c, p) -> {})
            .register();

    public static final ItemEntry<HEAPMediumcannonRoundItem> HEAP_MEDIUMCANNON_ROUND = REGISTRATE
            .item("heap_mediumcannon_round", HEAPMediumcannonRoundItem::new)
            .lang("Armor Piercing High Explosive Mediumcannon Round")
            .model((c, p) -> {})
            .register();

    // ---------------------------------------------------------------- SMOKE

    public static final ItemEntry<SmokeMediumcannonCartridgeItem> SMOKE_MEDIUMCANNON_CARTRIDGE = REGISTRATE
            .item("smoke_mediumcannon_cartridge", SmokeMediumcannonCartridgeItem::new)
            .model((c, p) -> {})
            .removeTab(CreativeModeTabs.SEARCH)
            .model((c, p) -> {})
            .register();

    public static final ItemEntry<SmokeMediumcannonRoundItem> SMOKE_MEDIUMCANNON_ROUND = REGISTRATE
            .item("smoke_mediumcannon_round", SmokeMediumcannonRoundItem::new)
            .lang("Smoke Mediumcannon Round")
            .model((c, p) -> {})
            .register();

    // ------------------------------------------------------------------------------------
    // R6a — 机炮弹药（4 种弹头）
    // ------------------------------------------------------------------------------------
    //
    // 都打上 CBC 的 AUTOCANNON_ROUNDS 物品 tag（机炮弹药箱按这个 tag 收弹）。
    // 模型全部手写（src/main/resources/assets/cbcmodernwarfare/models/item/），
    // 所以这里用 .model((c, p) -> {}) 抑制 datagen，避免与手写文件撞同一路径。
    // CANISTER / HE 在 1.20.1 里没有 .lang(...)，保持原样（走 raw lang 键）。

    public static final ItemEntry<HVAPAutocannonRoundItem> HVAP_AUTOCANNON_ROUND = REGISTRATE
            .item("hvap_autocannon_round", HVAPAutocannonRoundItem::new)
            .lang("High Velocity (HVAP) Armor Piercing Autocannon Round")
            .model((c, p) -> {})
            .tag(CBCItemTags.AUTOCANNON_ROUNDS)
            .register();

    public static final ItemEntry<APDSAutocannonRoundItem> APDS_AUTOCANNON_ROUND = REGISTRATE
            .item("apds_autocannon_round", APDSAutocannonRoundItem::new)
            .lang("Armor Piercing (APDS) Discarding Sabot Autocannon Round")
            .model((c, p) -> {})
            .tag(CBCItemTags.AUTOCANNON_ROUNDS)
            .register();

    public static final ItemEntry<CanisterAutocannonRoundItem> CANISTER_AUTOCANNON_ROUND = REGISTRATE
            .item("canister_autocannon_round", CanisterAutocannonRoundItem::new)
            .lang("Canister Autocannon Round")
            .model((c, p) -> {})
            .tag(CBCItemTags.AUTOCANNON_ROUNDS)
            .register();

    public static final ItemEntry<ExplosiveAutocannonRoundItem> HE_AUTOCANNON_ROUND = REGISTRATE
            .item("he_autocannon_round", ExplosiveAutocannonRoundItem::new)
            // 必须显式给 lang：否则 Registrate 自动 titlecase 会生成 "He Autocannon Round"，
            // 与实体名 "HE Autocannon Round" 不一致（HE = High Explosive，不是单词 He）。
            .lang("HE Autocannon Round")
            .model((c, p) -> {})
            .tag(CBCItemTags.AUTOCANNON_ROUNDS)
            .register();

    // ------------------------------------------------------------------------------------
    // R6c-1 — 燃烧剂
    // ------------------------------------------------------------------------------------

    /**
     * 燃烧头：涂在机炮弹药上，命中后起火。
     *
     * <p>1.20.1 只写了 {@code REGISTRATE.item("incendiary_tip", Item::new).register()}，
     * 模型由 datagen 生成 {@code item/generated}（贴图 {@code item/incendiary_tip}）。
     * 这里补一个 {@code .lang(...)}，避免 Registrate 自动 titlecase 出 "Incendiary Tip"
     * 之外还要靠 raw lang —— 其实 titlecase 结果正好对，所以不写也一致；
     * 显式写出来是为了让 en_us 与 zh_cn 的对照更直观。
     */
    public static final ItemEntry<Item> INCENDIARY_TIP = REGISTRATE
            .item("incendiary_tip", Item::new)
            .lang("Incendiary Tip")
            .register();

    /**
     * 烟雾排放器弹药（R6c-2）。
     *
     * <p>1.20.1 的注册 id 是 {@code smoke_discharger_round}，**没有** {@code .model(...)} ——
     * 模型由 datagen 生成 {@code item/generated}（贴图 {@code item/smoke_discharger_round}）。
     * 所以这里也不抑制 datagen，让它照常生成。
     */
    public static final ItemEntry<SmokeDischargerRoundItem> SMOKE_DISCHARGER_ROUND = REGISTRATE
            .item("smoke_discharger_round", SmokeDischargerRoundItem::new)
            .lang("Smoke Discharger Round")
            .register();

    // ------------------------------------------------------------------------------------
    // 重型后坐弹簧 —— 未完成后坐炮管补齐时消耗（四种材质共用）
    // ------------------------------------------------------------------------------------

    public static final ItemEntry<Item> HEAVY_RECOIL_SPRING = REGISTRATE
            .item("heavy_recoil_spring", Item::new)
            .register();

    // TODO(R6c): 燃烧剂、碳钢/硅锭、烟雾发射器弹。

    /** 强制本类初始化，字段初始化时才真正注册。 */
    public static void register() {
        // 故意留空 —— 见类注释。
    }

    private CBCModernWarfareItem() {}
}
