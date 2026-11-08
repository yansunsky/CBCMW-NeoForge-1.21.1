package riftyboi.cbcmodernwarfare.index;

import static riftyboi.cbcmodernwarfare.CBCModernWarfare.REGISTRATE;

import com.tterrag.registrate.util.entry.BlockEntityEntry;

import riftyboi.cbcmodernwarfare.cannons.medium_cannon.MediumcannonBlockEntity;
import riftyboi.cbcmodernwarfare.cannons.medium_cannon.breech.MediumcannonBreechBlockEntity;
import riftyboi.cbcmodernwarfare.cannons.medium_cannon.recoil_barrel.MediumcannonRecoilBarrelBlockEntity;
import riftyboi.cbcmodernwarfare.crafting.incomplete.IncompleteMediumcannonBlockEntity;

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
            .validBlocks(
                    CBCModernWarfareBlocks.CAST_IRON_MEDIUMCANNON_BREECH,
                    CBCModernWarfareBlocks.BRONZE_MEDIUMCANNON_BREECH,
                    CBCModernWarfareBlocks.STEEL_MEDIUMCANNON_BREECH,
                    CBCModernWarfareBlocks.NETHERSTEEL_MEDIUMCANNON_BREECH)
            .register();

    public static final BlockEntityEntry<MediumcannonRecoilBarrelBlockEntity> MEDIUMCANNON_RECOIL_BARREL = REGISTRATE
            .blockEntity("mediumcannon_recoil_barrel", MediumcannonRecoilBarrelBlockEntity::new)
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

    // TODO(R4b): rotarycannon / rotarycannon_breech / rotarycannon_bearing /
    //   incomplete_rotarycannoncannon（注册 id 里的双 n 是 1.20.1 原样，保持）。
    //   rotarycannon_breech 的方块实体在 1.20.1 经 IndexPlatform::makeRotarycannonBreech
    //   创建，那个方法目前还是 Object 占位，迁移时直接在这里内联实现。
    // TODO(R5): fuzed_block。
    // TODO(R6): autocannon_ammo_drum。
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
