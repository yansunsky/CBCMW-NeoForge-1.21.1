package riftyboi.cbcmodernwarfare.index;

import static riftyboi.cbcmodernwarfare.CBCModernWarfare.REGISTRATE;

import java.util.function.Consumer;

import com.tterrag.registrate.util.entry.EntityEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityType.EntityFactory;
import net.minecraft.world.entity.MobCategory;

import rbasamoyai.createbigcannons.index.CBCMunitionPropertiesHandlers;
import rbasamoyai.createbigcannons.multiloader.EntityTypeConfigurator;
import rbasamoyai.createbigcannons.munitions.autocannon.AbstractAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonProjectileRenderer;
import rbasamoyai.createbigcannons.munitions.big_cannon.grapeshot.GrapeshotBurstRenderer;
import rbasamoyai.createbigcannons.munitions.config.MunitionPropertiesHandler;
import rbasamoyai.createbigcannons.munitions.config.PropertiesTypeHandler;
import rbasamoyai.ritchiesprojectilelib.RPLTags;

import riftyboi.cbcmodernwarfare.munitions.autocannon.apds.APDSAutocannonProjectile;
import riftyboi.cbcmodernwarfare.munitions.autocannon.canister.CanisterAutocannonProjectile;
import riftyboi.cbcmodernwarfare.munitions.autocannon.he.ExplosiveAutocannonProjectile;
import riftyboi.cbcmodernwarfare.munitions.autocannon.hvap.HVAPAutocannonProjectile;
import riftyboi.cbcmodernwarfare.munitions.big_cannon.heap_shell.HEAPBurst;
import riftyboi.cbcmodernwarfare.munitions.big_cannon.heap_shell.HEAPBurstRenderer;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.AbstractMediumcannonProjectile;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonProjectileRenderer;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.ap.APMediumcannonProjectile;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.apds.APDSMediumcannonProjectile;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.apfsds.APFSDSMediumcannonProjectile;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.aphe.APHEMediumcannonProjectile;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.canister.CanisterBurst;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.canister.CanisterMediumcannonProjectile;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.he.HEMediumcannonProjectile;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.heap.HEAPMediumcannonProjectile;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.hefrag.HEFMediumcannonProjectile;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.smoke.SmokeMediumcannonProjectile;

/**
 * entity types of this mod.
 *
 * <p><b>R3 status: skeleton only.</b> 1.20.1 registered 25 entity types here; every one needs
 * classes from munitions/ 与 cannon_control/ that have not been ported yet, so the entries are held back per round.
 * Registering an entry whose factory references a missing class does not compile, which is exactly
 * what the phase plan forbids.
 *
 * <p>What belongs here (round assignments, from the 1.20.1 source):
 * <ul>
 *   <li>R5: the big-cannon projectiles (HEAP/HEF/APDS) and their projectile-burst entities.</li>
 *   <li>R6a: the four autocannon projectiles (HVAP/APDS/CANISTER/HE).</li>
 *   <li>R6c: the smoke discharger projectile.</li>
 *   <li>R7: MunitionsPhysicsContraptionEntity (the 49 KB class).</li>
 *   <li>R8: AbstractSightEntity and its four subclasses.</li>
 * </ul>
 */
public class CBCModernWarfareEntityTypes {

    // ------------------------------------------------------------------------------------
    // R5 — 中型炮炮弹（9 个）+ 两个爆散实体
    // ------------------------------------------------------------------------------------
    //
    // 与 1.20.1 一致：APDS / APFSDS / AP 共用 INERT 属性处理器。
    // 辅助方法 mediumcannonProjectile() 把「尺寸 + 渲染器 + RPL 精确运动 tag + 属性
    // handler 注册」四件事打包，避免每个实体重复。

    public static final EntityEntry<APMediumcannonProjectile> AP_ROUND = mediumcannonProjectile(
            "ap_mediumshell", APMediumcannonProjectile::new, "Armor Piercing (AP) Mediumcannon Round",
            CBCModernWarfareMunitionPropertiesHandlers.INERT_MEDIUMCANNON_PROJECTILE);

    public static final EntityEntry<APDSMediumcannonProjectile> APDS_ROUND = mediumcannonProjectile(
            "apds_mediumshell", APDSMediumcannonProjectile::new,
            "Armor Piercing (APDS) Discarding Sabot Mediumcannon Round",
            CBCModernWarfareMunitionPropertiesHandlers.INERT_MEDIUMCANNON_PROJECTILE);

    public static final EntityEntry<APFSDSMediumcannonProjectile> APFSDS_ROUND = mediumcannonProjectile(
            "apfsds_mediumshell", APFSDSMediumcannonProjectile::new,
            "Armor Piercing Fin (APFSDS) Stabilised Discarding Sabot Mediumcannon Round",
            CBCModernWarfareMunitionPropertiesHandlers.INERT_MEDIUMCANNON_PROJECTILE);

    public static final EntityEntry<APHEMediumcannonProjectile> APHE_ROUND = mediumcannonProjectile(
            "aphe_mediumshell", APHEMediumcannonProjectile::new,
            "Armor Piercing (APHE) High Explosive Mediumcannon Round",
            CBCModernWarfareMunitionPropertiesHandlers.APHE_MEDIUMCANNON);

    public static final EntityEntry<CanisterMediumcannonProjectile> CANISTER_ROUND = mediumcannonProjectile(
            "canister_mediumshell", CanisterMediumcannonProjectile::new,
            "Canister Anti Infantry (CAN) Mediumcannon Round",
            CBCModernWarfareMunitionPropertiesHandlers.CANISTER_MEDIUMCANNON);

    public static final EntityEntry<HEMediumcannonProjectile> HE_ROUND = mediumcannonProjectile(
            "he_mediumshell", HEMediumcannonProjectile::new, "High Explosive (HE) Mediumcannon Round",
            CBCModernWarfareMunitionPropertiesHandlers.HE_MEDIUMCANNON);

    public static final EntityEntry<HEFMediumcannonProjectile> HEF_ROUND = mediumcannonProjectile(
            "hef_mediumshell", HEFMediumcannonProjectile::new,
            "High Explosive (HE-F) Fragmentation Mediumcannon Round",
            CBCModernWarfareMunitionPropertiesHandlers.HEF_MEDIUMCANNON);

    public static final EntityEntry<HEAPMediumcannonProjectile> HEAP_ROUND = mediumcannonProjectile(
            "heap_mediumshell", HEAPMediumcannonProjectile::new,
            "High Explosive (HEAT) Anti Tank Mediumcannon Round",
            CBCModernWarfareMunitionPropertiesHandlers.HEAP_MEDIUMCANNON);

    public static final EntityEntry<SmokeMediumcannonProjectile> SMOKE_ROUND = mediumcannonProjectile(
            "smoke_mediumshell", SmokeMediumcannonProjectile::new, "Smoke Mediumcannon Round",
            CBCModernWarfareMunitionPropertiesHandlers.SMOKE_MEDIUMCANNON);

    /** HEAP 的金属射流。用 CBC 的 PROJECTILE_BURST 属性处理器。 */
    public static final EntityEntry<HEAPBurst> HEAP_BURST = REGISTRATE
            .entity("heat_jet", HEAPBurst::new, MobCategory.MISC)
            .properties(shrapnel())
            .renderer(() -> HEAPBurstRenderer::new)
            .onRegister(type -> MunitionPropertiesHandler.registerProjectileHandler(type,
                    CBCMunitionPropertiesHandlers.PROJECTILE_BURST))
            .register();

    /** 霰弹的弹丸散布。渲染器复用 CBC 的 GrapeshotBurstRenderer。 */
    public static final EntityEntry<CanisterBurst> CANISTER_BURST = REGISTRATE
            .entity("canister_burst", CanisterBurst::new, MobCategory.MISC)
            .properties(shrapnel())
            .renderer(() -> GrapeshotBurstRenderer::new)
            .onRegister(type -> MunitionPropertiesHandler.registerProjectileHandler(type,
                    CBCMunitionPropertiesHandlers.PROJECTILE_BURST))
            .register();

    // ------------------------------------------------------------------------------------
    // R6a — 机炮炮弹（4 个）
    // ------------------------------------------------------------------------------------
    //
    // 与 1.20.1 一致：HVAP / APDS 共用 CBC 的 INERT_AUTOCANNON_PROJECTILE 属性处理器，
    // 我们只为 CANISTER / HE 各建一个自己的处理器。
    // 实体属性用 autocannonProperties()（尺寸 0.2×0.2 + 不追踪速度），
    // 渲染器复用 CBC 的 AutocannonProjectileRenderer。

    public static final EntityEntry<HVAPAutocannonProjectile> HVAP_AUTOCANNON = autocannonProjectile(
            "hvap_autocannon", HVAPAutocannonProjectile::new,
            "High Velocity Armor Piercing (AP) Autocannon Round",
            CBCMunitionPropertiesHandlers.INERT_AUTOCANNON_PROJECTILE);

    public static final EntityEntry<APDSAutocannonProjectile> APDS_AUTOCANNON = autocannonProjectile(
            "apds_autocannon", APDSAutocannonProjectile::new,
            "Armor Piercing (APDS) Discarding Sabot Autocannon Round",
            CBCMunitionPropertiesHandlers.INERT_AUTOCANNON_PROJECTILE);

    public static final EntityEntry<CanisterAutocannonProjectile> CANISTER_AUTOCANNON = autocannonProjectile(
            "canister_autocannon", CanisterAutocannonProjectile::new,
            "Canister Autocannon Round", CBCModernWarfareMunitionPropertiesHandlers.CANISTER_AUTOCANNON);

    public static final EntityEntry<ExplosiveAutocannonProjectile> HE_AUTOCANNON = autocannonProjectile(
            "he_autocannon", ExplosiveAutocannonProjectile::new,
            "HE Autocannon Round", CBCModernWarfareMunitionPropertiesHandlers.HE_AUTOCANNON);

    // ------------------------------------------------------------------------------------
    // 辅助方法
    // ------------------------------------------------------------------------------------

    private static <T extends AbstractAutocannonProjectile> EntityEntry<T> autocannonProjectile(String id,
            EntityFactory<T> factory, String enUSdiffLang, PropertiesTypeHandler<EntityType<?>, ?> handler) {
        return REGISTRATE
                .entity(id, factory, MobCategory.MISC)
                .properties(projectileProperties())
                .renderer(() -> AutocannonProjectileRenderer::new)
                .lang(enUSdiffLang)
                .tag(RPLTags.PRECISE_MOTION)
                .onRegister(type -> MunitionPropertiesHandler.registerProjectileHandler(type, handler))
                .register();
    }

    private static <T extends AbstractMediumcannonProjectile> EntityEntry<T> mediumcannonProjectile(String id,
            EntityFactory<T> factory, String enUSdiffLang, PropertiesTypeHandler<EntityType<?>, ?> handler) {
        return REGISTRATE
                .entity(id, factory, MobCategory.MISC)
                .properties(projectileProperties())
                .renderer(() -> MediumcannonProjectileRenderer::new)
                .lang(enUSdiffLang)
                .tag(RPLTags.PRECISE_MOTION)
                .onRegister(type -> MunitionPropertiesHandler.registerProjectileHandler(type, handler))
                .register();
    }

    private static <T> NonNullConsumer<T> configure(Consumer<EntityTypeConfigurator> cons) {
        return b -> cons.accept(EntityTypeConfigurator.of(b));
    }

    /** 炮弹的通用实体属性（与 1.20.1 的 autocannonProperties 一致）。 */
    private static <T> NonNullConsumer<T> projectileProperties() {
        return configure(c -> c.size(0.2f, 0.2f)
                .fireImmune()
                .updateInterval(1)
                .updateVelocity(false)
                .trackingRange(16));
    }

    /** 爆散弹丸的属性。 */
    private static <T> NonNullConsumer<T> shrapnel() {
        return configure(c -> c.size(0.8f, 0.8f)
                .fireImmune()
                .updateInterval(1)
                .updateVelocity(true)
                .trackingRange(16));
    }

    /** Forces class init; entries register from their field initialisers. */
    public static void register() {
        // 字段初始化器负责注册，这里只保证类被加载。
    }

    private CBCModernWarfareEntityTypes() {
    }
}
