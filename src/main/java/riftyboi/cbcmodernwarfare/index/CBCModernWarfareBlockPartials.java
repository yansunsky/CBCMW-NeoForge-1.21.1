package riftyboi.cbcmodernwarfare.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;

import net.minecraft.resources.ResourceLocation;

import rbasamoyai.createbigcannons.crafting.casting.CannonCastShape;

import riftyboi.cbcmodernwarfare.CBCModernWarfare;
import riftyboi.cbcmodernwarfare.cannons.medium_cannon.material.MediumcannonMaterial;

/**
 * 渲染用的「部分模型」句柄。
 *
 * <p><b>1.20.1 → 1.21.1</b>：{@code PartialModel.of(ResourceLocation)} 签名未变
 * （Flywheel 1.0.6 javap 核对）。{@code resolveDeferredModels()} 的存在理由也一样：
 * 有些 {@code CannonCastShape} 在静态初始化时还没注册完，所以延迟到客户端 setup 再解析。
 *
 * <p><b>本轮只落中型炮需要的两个工厂</b>（`mediumcannonEjectorFor` 与它依赖的三张
 * `*_FALLING_BREECH` 常量）。炮架、烟雾发射器等其它 partial 随各自轮次补。
 */
public class CBCModernWarfareBlockPartials {

    private static final Collection<Runnable> DEFERRED_MODEL_CALLBACKS = new ArrayList<>();
    private static final Map<CannonCastShape, PartialModel> CANNON_CAST_BY_SIZE = new HashMap<>();
    private static final Map<MediumcannonMaterial, PartialModel> MEDIUMCANNON_EJECTOR_BY_MATERIAL = new HashMap<>();

    /** 落闩（退壳时从炮闩里飞出来的那个模型）。按材质分四份。 */
    public static final PartialModel CAST_IRON_MEDIUMCANNON_FALLING_BREECH =
            mediumcannonEjectorPartial(CBCModernWarfareMediumcannonMaterials.CAST_IRON, "cast_iron");
    public static final PartialModel BRONZE_MEDIUMCANNON_FALLING_BREECH =
            mediumcannonEjectorPartial(CBCModernWarfareMediumcannonMaterials.BRONZE, "bronze");
    public static final PartialModel STEEL_MEDIUMCANNON_FALLING_BREECH =
            mediumcannonEjectorPartial(CBCModernWarfareMediumcannonMaterials.STEEL, "steel");
    public static final PartialModel NETHERSTEEL_MEDIUMCANNON_FALLING_BREECH =
            mediumcannonEjectorPartial(CBCModernWarfareMediumcannonMaterials.NETHERSTEEL, "nethersteel");

    private static PartialModel block(String path) {
        return PartialModel.of(CBCModernWarfare.resource("block/" + path));
    }

    /** 按材质取落闩模型；未知材质回落到铸铁。 */
    public static PartialModel mediumcannonEjectorFor(MediumcannonMaterial material) {
        return MEDIUMCANNON_EJECTOR_BY_MATERIAL.getOrDefault(material, CAST_IRON_MEDIUMCANNON_FALLING_BREECH);
    }

    private static PartialModel mediumcannonEjectorPartial(MediumcannonMaterial material, String path) {
        return mediumcannonEjectorPartial(material,
                CBCModernWarfare.resource("item/" + path + "_mediumcannon_falling_breech"));
    }

    public static PartialModel mediumcannonEjectorPartial(MediumcannonMaterial material, ResourceLocation loc) {
        PartialModel model = PartialModel.of(loc);
        MEDIUMCANNON_EJECTOR_BY_MATERIAL.put(material, model);
        return model;
    }

    @SuppressWarnings("unused")
    private static PartialModel cannonCastPartial(java.util.function.Supplier<CannonCastShape> size, String path) {
        PartialModel model = block(path);
        DEFERRED_MODEL_CALLBACKS.add(() -> CANNON_CAST_BY_SIZE.put(size.get(), model));
        return model;
    }

    /** 强制类初始化。 */
    public static void init() {
        // 故意留空 —— 静态字段初始化即完成注册。
    }

    /** 客户端 setup 后调用：解析那些依赖注册表顺序的 partial。 */
    public static void resolveDeferredModels() {
        for (Runnable run : DEFERRED_MODEL_CALLBACKS) {
            run.run();
        }
    }

    private CBCModernWarfareBlockPartials() {}
}
