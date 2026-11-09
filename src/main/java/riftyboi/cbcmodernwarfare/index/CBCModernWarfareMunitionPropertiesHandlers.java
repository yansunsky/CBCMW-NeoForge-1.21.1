package riftyboi.cbcmodernwarfare.index;

import riftyboi.cbcmodernwarfare.munitions.autocannon.canister.CanisterAutocannonPropertiesHandler;
import riftyboi.cbcmodernwarfare.munitions.autocannon.he.ExplosiveAutocannonPropertiesHandler;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.aphe.APHEMediumCannonProjectilePropertiesHandler;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.canister.CanisterMediumCannonProjectilePropertiesHandler;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.config.InertMediumcannonProjectilePropertiesHandler;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.he.HEMediumCannonProjectilePropertiesHandler;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.heap.HEAPMediumcannonProjectilePropertiesHandler;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.hefrag.HEFMediumCannonProjectilePropertiesHandler;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.smoke.SmokeMediumcannonProjectilePropertiesHandler;

/**
 * 弹药属性处理器。
 *
 * <p><b>R5 状态</b>：中型炮的 7 个已就位（APDS/APFSDS 共用 {@code INERT}）。
 * <b>R6a</b> 补机炮的 2 个（HVAP/APDS 同样共用 CBC 的 {@code INERT_AUTOCANNON_PROJECTILE}，
 * 不在这里新建）。发射器（R6c/R7）的还没迁。
 *
 * <p>{@link #init()} 必须在 {@code CBCModernWarfare#init()} 里**最先**调用 ——
 * 这是 1.20.1 的顺序，因为任何炮弹方块/物品查属性前 handler 必须已存在。
 */
public class CBCModernWarfareMunitionPropertiesHandlers {

    public static final InertMediumcannonProjectilePropertiesHandler INERT_MEDIUMCANNON_PROJECTILE =
            new InertMediumcannonProjectilePropertiesHandler();
    public static final APHEMediumCannonProjectilePropertiesHandler APHE_MEDIUMCANNON =
            new APHEMediumCannonProjectilePropertiesHandler();
    public static final CanisterMediumCannonProjectilePropertiesHandler CANISTER_MEDIUMCANNON =
            new CanisterMediumCannonProjectilePropertiesHandler();
    public static final HEMediumCannonProjectilePropertiesHandler HE_MEDIUMCANNON =
            new HEMediumCannonProjectilePropertiesHandler();
    public static final HEFMediumCannonProjectilePropertiesHandler HEF_MEDIUMCANNON =
            new HEFMediumCannonProjectilePropertiesHandler();
    public static final HEAPMediumcannonProjectilePropertiesHandler HEAP_MEDIUMCANNON =
            new HEAPMediumcannonProjectilePropertiesHandler();
    public static final SmokeMediumcannonProjectilePropertiesHandler SMOKE_MEDIUMCANNON =
            new SmokeMediumcannonProjectilePropertiesHandler();

    // ---------------------------------------------------------------- R6a 机炮

    /** 霰弹机炮炮弹——弹丸实体是 CBC 的 ProjectileBurst，属性由本处理器读。 */
    public static final CanisterAutocannonPropertiesHandler CANISTER_AUTOCANNON =
            new CanisterAutocannonPropertiesHandler();

    /** 高爆机炮炮弹。 */
    public static final ExplosiveAutocannonPropertiesHandler HE_AUTOCANNON =
            new ExplosiveAutocannonPropertiesHandler();

    /**
     * 触发类加载。
     *
     * <p><b>1.20.1 → 1.21.1 的变化</b>：1.20.1 的 {@code PropertiesTypeHandler} 有
     * {@code init()}，由我们逐个调用；5.11.7 已经<b>删掉</b>了它，改为在实体注册时用
     * {@code MunitionPropertiesHandler.registerProjectileHandler(type, handler)}
     * 挂上去（见 {@link CBCModernWarfareEntityTypes} 的 {@code .onRegister(...)}）。
     * 所以这里只剩「保证静态字段初始化」这一个作用。
     */
    public static void init() {
        // 字段初始化器已完成全部工作，见方法注释。
    }

    /** 别名，保持各轮 init() 调用点的写法一致。 */
    public static void register() {
        init();
    }

    private CBCModernWarfareMunitionPropertiesHandlers() {
    }
}
