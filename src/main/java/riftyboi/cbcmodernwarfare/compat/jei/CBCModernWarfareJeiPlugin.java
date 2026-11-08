package riftyboi.cbcmodernwarfare.compat.jei;

import net.minecraft.resources.ResourceLocation;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeRegistration;

import riftyboi.cbcmodernwarfare.CBCModernWarfare;

/**
 * JEI 插件入口。
 *
 * <p><b>为什么需要它</b>：本模组的 4 个弹药装配配方继承 {@code CustomRecipe}，
 * 其 {@code isSpecial()} 恒 {@code true}；JEI 的 {@code CategoryRecipeValidator}
 * 遇到这类配方直接 `return true`（跳过），所以 JEI 里永远看不到它们。
 * 只能像 CBC 那样自己造展示配方（见 {@link MunitionAssemblyJeiRecipes}）。
 *
 * <p><b>软依赖</b>：JEI 未安装时 {@code ForgePluginFinder} 根本不会运行，
 * 这个类不会被加载 —— 不需要额外守卫。但也意味着**这里的一切都只在客户端**，
 * 不要引用任何仅服务端存在的东西。
 *
 * <p><b>无参构造器是硬要求</b>：JEI 用反射 new 出插件实例，缺了会静默加载失败。
 */
@JeiPlugin
public class CBCModernWarfareJeiPlugin implements IModPlugin {

    private static final ResourceLocation UID =
            CBCModernWarfare.resource("jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        MunitionAssemblyJeiRecipes.register(registration);
    }
}
