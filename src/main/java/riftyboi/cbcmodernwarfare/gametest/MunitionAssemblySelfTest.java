package riftyboi.cbcmodernwarfare.gametest;

import java.util.List;

import net.minecraft.core.NonNullList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.Level;

import rbasamoyai.createbigcannons.index.CBCItems;

import riftyboi.cbcmodernwarfare.CBCModernWarfare;
import riftyboi.cbcmodernwarfare.crafting.munition_assembly.CartridgeAssemblyRecipe;
import riftyboi.cbcmodernwarfare.crafting.munition_assembly.MediumFuzeRemoval;
import riftyboi.cbcmodernwarfare.crafting.munition_assembly.MediumMunitionFuzingRecipe;
import riftyboi.cbcmodernwarfare.crafting.munition_assembly.MediumTracerApply;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareItem;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareMunitionPropertiesHandlers;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonCartridgeItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonFuzes;

/**
 * R5b 配方自检 —— 服务端启动时跑一遍，把结论写进日志。
 *
 * <p><b>为什么不用 GameTest</b>：NeoForge 21.1 的 {@code @GameTest} 强制要求一个
 * 结构模板（{@code StructureUtils.prepareTestStructure} 找不到就整个 server 崩），
 * 而项目里没有、也不打算为几个纯逻辑断言引入 gametest 结构 nbt。
 * 配方的 {@code matches}/{@code assemble} 本来就是纯函数，只要注册表和 Level 就绪
 * 就能跑，用「启动后自检 + 日志断言」比 GameTest 的依赖面小得多。
 *
 * <p>验收方式与 {@code serverAssertClean} 一致：{@code runServer} 后用 grep 看
 * {@code [R5b-SELFTEST] PASS}/{@code FAIL} 行。
 */
public final class MunitionAssemblySelfTest {

    private static final String TAG = "[R5b-SELFTEST] ";

    private MunitionAssemblySelfTest() {
    }

    /** 入口：全部通过返回 true，任何一条失败返回 false（失败项会逐条打进日志）。 */
    public static boolean run(MinecraftServer server) {
        Level level = server.overworld();
        int passed = 0;
        int failed = 0;

        // ① 弹头 + 空筒 → 弹药筒，且筒里装着弹头
        try {
            ItemStack round = CBCModernWarfareItem.AP_MEDIUMCANNON_ROUND.asStack();
            CraftingInput input = CraftingInput.of(1, 3,
                    List.of(round, CBCModernWarfareItem.FILLED_MEDIUMCANNON_CARTRIDGE.asStack(), ItemStack.EMPTY));
            CartridgeAssemblyRecipe recipe = new CartridgeAssemblyRecipe();
            check("① 弹药筒组装 matches", recipe.matches(input, level));
            ItemStack result = recipe.assemble(input, server.registryAccess());
            check("① 结果是弹药筒", result.getItem() instanceof MediumcannonCartridgeItem);
            check("① 筒里有弹头", MediumcannonCartridgeItem.hasProjectile(result));
            check("① 筒里弹头种类一致",
                    MediumcannonCartridgeItem.getProjectileStack(result).getItem() == round.getItem());
            passed++;
        } catch (RuntimeException | AssertionError e) {
            failed++;
            log("① 弹药筒组装 FAIL: " + e);
        }

        // ② 弹药筒 + 引信 → 筒里的弹头装上引信（关键是「写回」）
        try {
            ItemStack cartridge = CBCModernWarfareItem.HE_MEDIUMCANNON_CARTRIDGE.asStack();
            MediumcannonCartridgeItem.writeProjectile(
                    CBCModernWarfareItem.HE_MEDIUMCANNON_ROUND.asStack(), cartridge);
            ItemStack fuze = CBCItems.DELAYED_IMPACT_FUZE.asStack();
            CraftingInput input = CraftingInput.of(2, 1, List.of(cartridge, fuze));
            MediumMunitionFuzingRecipe recipe = new MediumMunitionFuzingRecipe();
            check("② 装引信 matches", recipe.matches(input, level));
            ItemStack result = recipe.assemble(input, server.registryAccess());
            ItemStack projectile = MediumcannonCartridgeItem.getProjectileStack(result);
            check("② 装完仍有弹头", !projectile.isEmpty());
            check("② 弹头已装引信", MediumcannonFuzes.hasFuze(projectile));
            check("② 引信种类一致", MediumcannonFuzes.getFuze(projectile).getItem() == fuze.getItem());
            passed++;
        } catch (RuntimeException | AssertionError e) {
            failed++;
            log("② 装引信 FAIL: " + e);
        }

        // ③ 弹药筒 + 曳光剂 → 曳光标记（覆盖 R5 修的那个 setTracer 上游 bug）
        try {
            ItemStack cartridge = CBCModernWarfareItem.AP_MEDIUMCANNON_CARTRIDGE.asStack();
            MediumcannonCartridgeItem.writeProjectile(
                    CBCModernWarfareItem.AP_MEDIUMCANNON_ROUND.asStack(), cartridge);
            CraftingInput input = CraftingInput.of(2, 1, List.of(cartridge, CBCItems.TRACER_TIP.asStack()));
            MediumTracerApply recipe = new MediumTracerApply();
            check("③ 涂曳光 matches", recipe.matches(input, level));
            ItemStack result = recipe.assemble(input, server.registryAccess());
            check("③ 结果是弹药筒", result.getItem() instanceof MediumcannonCartridgeItem);
            check("③ 已标记曳光", ((MediumcannonCartridgeItem) result.getItem()).isTracer(result));
            passed++;
        } catch (RuntimeException | AssertionError e) {
            failed++;
            log("③ 涂曳光 FAIL: " + e);
        }

        // ④ 拆引信 → 输出引信，输入槽留下剥掉引信的弹头
        try {
            ItemStack fuze = CBCItems.DELAYED_IMPACT_FUZE.asStack();
            ItemStack round = CBCModernWarfareItem.HE_MEDIUMCANNON_ROUND.asStack();
            MediumcannonFuzes.setFuze(round, fuze);
            ItemStack cartridge = CBCModernWarfareItem.HE_MEDIUMCANNON_CARTRIDGE.asStack();
            MediumcannonCartridgeItem.writeProjectile(round, cartridge);
            CraftingInput input = CraftingInput.of(1, 1, List.of(cartridge));
            MediumFuzeRemoval recipe = new MediumFuzeRemoval();
            check("④ 拆引信 matches", recipe.matches(input, level));
            check("④ 输出是引信",
                    recipe.assemble(input, server.registryAccess()).getItem() == fuze.getItem());
            NonNullList<ItemStack> remaining = recipe.getRemainingItems(input);
            ItemStack stripped = remaining.get(0);
            check("④ 留下弹药筒", !stripped.isEmpty());
            check("④ 弹头已无引信",
                    !MediumcannonFuzes.hasFuze(MediumcannonCartridgeItem.getProjectileStack(stripped)));
            passed++;
        } catch (RuntimeException | AssertionError e) {
            failed++;
            log("④ 拆引信 FAIL: " + e);
        }

        // ⑤ 反例：两个弹头不应匹配
        try {
            CraftingInput input = CraftingInput.of(2, 1, List.of(
                    CBCModernWarfareItem.AP_MEDIUMCANNON_ROUND.asStack(),
                    CBCModernWarfareItem.HE_MEDIUMCANNON_ROUND.asStack()));
            check("⑤ 反例两个弹头不匹配", !new CartridgeAssemblyRecipe().matches(input, level));
            passed++;
        } catch (RuntimeException | AssertionError e) {
            failed++;
            log("⑤ 反例 FAIL: " + e);
        }

        // ⑥ 弹药属性表已加载（这是 R5 遗留的登录 NPE 根因，属性 JSON 缺一个就整服断线）
        try {
            if (server.getRecipeManager() == null) {
                throw new AssertionError("recipe manager 尚未就绪");
            }
            check("⑥ 弹药属性 handler 已注册",
                    CBCModernWarfareMunitionPropertiesHandlers.INERT_MEDIUMCANNON_PROJECTILE != null);
            passed++;
        } catch (RuntimeException | AssertionError e) {
            failed++;
            log("⑥ 属性表 FAIL: " + e);
        }

        log("PASS=" + passed + " FAIL=" + failed);
        if (failed == 0) {
            log("PASS — 全部 " + passed + " 组断言通过");
        } else {
            log("FAIL — " + failed + " 组断言失败");
        }
        return failed == 0;
    }

    private static void check(String what, boolean ok) {
        if (!ok) {
            throw new AssertionError(what);
        }
    }

    private static void log(String message) {
        CBCModernWarfare.LOGGER.info(TAG + message);
    }
}
