package riftyboi.cbcmodernwarfare.index;

import static riftyboi.cbcmodernwarfare.CBCModernWarfare.REGISTRATE;

import com.tterrag.registrate.util.entry.BlockEntityEntry;

import riftyboi.cbcmodernwarfare.cannons.medium_cannon.MediumcannonBlockEntity;
import riftyboi.cbcmodernwarfare.cannons.medium_cannon.breech.MediumcannonBreechBlockEntity;
import riftyboi.cbcmodernwarfare.cannons.medium_cannon.recoil_barrel.MediumcannonRecoilBarrelBlockEntity;
import riftyboi.cbcmodernwarfare.cannons.rotarycannon.RotarycannonBlockEntity;
import riftyboi.cbcmodernwarfare.cannons.rotarycannon.bearing.RotarycannonBearingBlockEntity;
import riftyboi.cbcmodernwarfare.cannons.rotarycannon.breech.AbstractRotarycannonBreechBlockEntity;
import riftyboi.cbcmodernwarfare.cannons.rotarycannon.breech.RotarycannonBreechBlockEntity;
import riftyboi.cbcmodernwarfare.crafting.incomplete.IncompleteMediumcannonBlockEntity;
import riftyboi.cbcmodernwarfare.crafting.incomplete.IncompleteRotarycannonBlockEntity;
import riftyboi.cbcmodernwarfare.munitions.autocannon.ammo_drum.AutocannonAmmoDrumBlockEntity;

/**
 * 本模组的方块实体类型。
 *
 * <p><b>状态</b>：1.20.1 注册了 21 个。R4a 只启用中型炮的四个
 * （{@code mediumcannon} / {@code mediumcannon_breech} / {@code mediumcannon_recoil_barrel} /
 * {@code incomplete_mediumcannon}），其余按轮次补齐。
 *
 * <p><b>R3 已核实的 API 事实</b>（{@code javap} 证据见 {@code docs/log/04-Phase2-R3.md}）：
 * <ul>
 *   <li>{@code BlockEntityType.Builder#build()} 现在需要
 *       {@code com.mojang.datafixers.types.Type<?>}；</li>
 *   <li>Registrate 的 {@code BlockEntityBuilder} 已吸收这个变化，模组侧的
 *       {@code blockEntity(name, factory)} → {@code validBlocks(...)} → {@code renderer(...)} →
 *       {@code register()} 链式 API 一个名字都没变。</li>
 * </ul>
 *
 * <p><b>id 必须保持 1.20.1 原值</b> —— 方块实体的注册 id 会被写进区块存档。
 */
public class CBCModernWarfareBlockEntities {

    // ------------------------------------------------------------------------------------
    // 中型炮（R4a）
    // ------------------------------------------------------------------------------------

    /** 普通炮管用的方块实体，四种材质共用同一个类型。 */
    public static final BlockEntityEntry<MediumcannonBlockEntity> MEDIUMCANNON = REGISTRATE
            .blockEntity("mediumcannon", MediumcannonBlockEntity::new)
            .validBlocks(
                    CBCModernWarfareBlocks.CAST_IRON_MEDIUMCANNON_BARREL,
                    CBCModernWarfareBlocks.BRONZE_MEDIUMCANNON_BARREL,
                    CBCModernWarfareBlocks.STEEL_MEDIUMCANNON_BARREL,
                    CBCModernWarfareBlocks.NETHERSTEEL_MEDIUMCANNON_BARREL)
            .register();

    public static final BlockEntityEntry<MediumcannonBreechBlockEntity> MEDIUMCANNON_BREECH = REGISTRATE
            .blockEntity("mediumcannon_breech", MediumcannonBreechBlockEntity::new)
            // 渲染器只在客户端实例化；这一行本身是安全的（方法引用不会被服务端解析）。
            .renderer(() -> riftyboi.cbcmodernwarfare.forge.CBCModernWarfareClientForge::mediumcannonBreechRenderer)
            .validBlocks(
                    CBCModernWarfareBlocks.CAST_IRON_MEDIUMCANNON_BREECH,
                    CBCModernWarfareBlocks.BRONZE_MEDIUMCANNON_BREECH,
                    CBCModernWarfareBlocks.STEEL_MEDIUMCANNON_BREECH,
                    CBCModernWarfareBlocks.NETHERSTEEL_MEDIUMCANNON_BREECH)
            .register();

    public static final BlockEntityEntry<MediumcannonRecoilBarrelBlockEntity> MEDIUMCANNON_RECOIL_BARREL = REGISTRATE
            .blockEntity("mediumcannon_recoil_barrel", MediumcannonRecoilBarrelBlockEntity::new)
            .renderer(() -> riftyboi.cbcmodernwarfare.forge.CBCModernWarfareClientForge::mediumcannonRecoilBarrelRenderer)
            .validBlocks(
                    CBCModernWarfareBlocks.CAST_IRON_MEDIUMCANNON_RECOIL_BARREL,
                    CBCModernWarfareBlocks.BRONZE_MEDIUMCANNON_RECOIL_BARREL,
                    CBCModernWarfareBlocks.STEEL_MEDIUMCANNON_RECOIL_BARREL,
                    CBCModernWarfareBlocks.NETHERSTEEL_MEDIUMCANNON_RECOIL_BARREL)
            .register();

    /** 未完成部件共用这一个类型（与 1.20.1 一致）。 */
    public static final BlockEntityEntry<IncompleteMediumcannonBlockEntity> INCOMPLETE_MEDIUMCANNON = REGISTRATE
            .blockEntity("incomplete_mediumcannon", IncompleteMediumcannonBlockEntity::new)
            .validBlocks(
                    CBCModernWarfareBlocks.INCOMPLETE_CAST_IRON_MEDIUMCANNON_BREECH,
                    CBCModernWarfareBlocks.INCOMPLETE_CAST_IRON_MEDIUMCANNON_RECOIL_BARREL,
                    CBCModernWarfareBlocks.INCOMPLETE_BRONZE_MEDIUMCANNON_BREECH,
                    CBCModernWarfareBlocks.INCOMPLETE_BRONZE_MEDIUMCANNON_RECOIL_BARREL,
                    CBCModernWarfareBlocks.INCOMPLETE_STEEL_MEDIUMCANNON_BREECH,
                    CBCModernWarfareBlocks.INCOMPLETE_STEEL_MEDIUMCANNON_RECOIL_BARREL,
                    CBCModernWarfareBlocks.INCOMPLETE_NETHERSTEEL_MEDIUMCANNON_BREECH,
                    CBCModernWarfareBlocks.INCOMPLETE_NETHERSTEEL_MEDIUMCANNON_RECOIL_BARREL)
            .register();

    // ------------------------------------------------------------------------------------
    // 转管炮（R4b-2）
    // ------------------------------------------------------------------------------------

    /** 普通转管炮管用的方块实体。 */
    public static final BlockEntityEntry<RotarycannonBlockEntity> ROTARYCANNON = REGISTRATE
            .blockEntity("rotarycannon", RotarycannonBlockEntity::new)
            .validBlocks(
                    CBCModernWarfareBlocks.BRONZE_ROTARYCANNON_BARREL,
                    CBCModernWarfareBlocks.STEEL_ROTARYCANNON_BARREL,
                    CBCModernWarfareBlocks.NETHERSTEEL_ROTARYCANNON_BARREL)
            .register();

    /**
     * 转管炮炮闩。
     *
     * <p><b>注意</b>：1.20.1 的工厂是 {@code IndexPlatform::makeRotarycannonBreech}
     * （平台层桩，具体实现在 {@code forge/}）。单平台后这层间接没有意义，
     * 所以这里<b>直接内联</b>成抽象类的匿名子类 —— 1.20.1 的 forge 实现也只有构造 + 几个
     * getter，没有平台相关逻辑。
     */
    public static final BlockEntityEntry<RotarycannonBreechBlockEntity> ROTARYCANNON_BREECH = REGISTRATE
            .blockEntity("rotarycannon_breech", RotarycannonBreechBlockEntity::new)
            .renderer(() -> riftyboi.cbcmodernwarfare.forge.CBCModernWarfareClientForge::rotarycannonBreechRenderer)
            .validBlocks(
                    CBCModernWarfareBlocks.BRONZE_ROTARYCANNON_BREECH,
                    CBCModernWarfareBlocks.STEEL_ROTARYCANNON_BREECH,
                    CBCModernWarfareBlocks.NETHERSTEEL_ROTARYCANNON_BREECH)
            .register();

    public static final BlockEntityEntry<RotarycannonBearingBlockEntity> ROTARYCANNON_BEARING = REGISTRATE
            .blockEntity("rotarycannon_bearing", RotarycannonBearingBlockEntity::new)
            .renderer(() -> riftyboi.cbcmodernwarfare.forge.CBCModernWarfareClientForge::rotarycannonBearingRenderer)
            .validBlocks(
                    CBCModernWarfareBlocks.BRONZE_ROTARYCANNON_BEARING,
                    CBCModernWarfareBlocks.STEEL_ROTARYCANNON_BEARING,
                    CBCModernWarfareBlocks.NETHERSTEEL_ROTARYCANNON_BEARING)
            .register();

    /** 未完成转管炮部件共用这一个类型（与 1.20.1 一致）。 */
    public static final BlockEntityEntry<IncompleteRotarycannonBlockEntity> INCOMPLETE_ROTARYCANNON = REGISTRATE
            .blockEntity("incomplete_rotarycannoncannon", IncompleteRotarycannonBlockEntity::new)
            .validBlocks(
                    CBCModernWarfareBlocks.INCOMPLETE_BRONZE_ROTARYCANNON_BREECH,
                    CBCModernWarfareBlocks.INCOMPLETE_BRONZE_ROTARYCANNON_BEARING,
                    CBCModernWarfareBlocks.INCOMPLETE_STEEL_ROTARYCANNON_BREECH,
                    CBCModernWarfareBlocks.INCOMPLETE_STEEL_ROTARYCANNON_BEARING,
                    CBCModernWarfareBlocks.INCOMPLETE_NETHERSTEEL_ROTARYCANNON_BREECH,
                    CBCModernWarfareBlocks.INCOMPLETE_NETHERSTEEL_ROTARYCANNON_BEARING)
            .register();
    // ------------------------------------------------------------------------------------
    // 机炮弹鼓（R6b）
    // ------------------------------------------------------------------------------------

    /**
     * 机炮弹鼓。
     *
     * <p><b>注意 id 不一致是原版如此</b>：方块注册 id 是 {@code rotarycannon_ammo_drum}，
     * 而 BE 与 Menu 都是 {@code autocannon_ammo_drum}（R6 计划书 §9.6，别"顺手统一"）。
     */
    public static final BlockEntityEntry<AutocannonAmmoDrumBlockEntity> AUTOCANNON_AMMO_DRUM = REGISTRATE
            .blockEntity("autocannon_ammo_drum", AutocannonAmmoDrumBlockEntity::new)
            .validBlocks(CBCModernWarfareBlocks.AUTOCANNON_AMMO_DRUM)
            .register();

    // TODO(R5): fuzed_block。
    // TODO(R7): launcher_sliding_breech / launcher_end / launcher_quickfiring_breech /
    //   incomplete_cannon / munitions_launcher。
    // TODO(R8): compact_mount / optical_guidance / manual_guidance / infrared_guidance /
    //   smoke_discharger。

    /** 强制本类初始化，字段初始化时才真正注册。 */
    public static void register() {
        // 故意留空 —— 见类注释。
    }

    private CBCModernWarfareBlockEntities() {}
}
