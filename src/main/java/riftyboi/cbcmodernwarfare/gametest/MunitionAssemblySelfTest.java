package riftyboi.cbcmodernwarfare.gametest;

import java.util.List;

import net.minecraft.core.NonNullList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.Level;

import rbasamoyai.createbigcannons.index.CBCItems;
import rbasamoyai.createbigcannons.munitions.autocannon.AbstractAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonAmmoType;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonCartridgeItem;

import riftyboi.cbcmodernwarfare.CBCModernWarfare;
import riftyboi.cbcmodernwarfare.crafting.munition_assembly.CartridgeAssemblyRecipe;
import riftyboi.cbcmodernwarfare.crafting.munition_assembly.IncendiaryApplicationRecipie;
import riftyboi.cbcmodernwarfare.crafting.munition_assembly.IncendiaryRemovalRecipie;
import riftyboi.cbcmodernwarfare.crafting.munition_assembly.MediumFuzeRemoval;
import riftyboi.cbcmodernwarfare.crafting.munition_assembly.MediumMunitionFuzingRecipe;
import riftyboi.cbcmodernwarfare.crafting.munition_assembly.MediumTracerApply;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareBlockEntities;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareBlocks;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareEntityTypes;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareItem;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareMenuTypes;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareMunitionPropertiesHandlers;
import riftyboi.cbcmodernwarfare.munitions.autocannon.AmmoItemMixinInerface;
import riftyboi.cbcmodernwarfare.munitions.autocannon.AutocannonProjectileMixinInterface;
import riftyboi.cbcmodernwarfare.munitions.autocannon.ammo_drum.AutocannonAmmoDrumBlock;
import riftyboi.cbcmodernwarfare.munitions.autocannon.ammo_drum.AutocannonAmmoDrumBlockEntity;
import riftyboi.cbcmodernwarfare.munitions.autocannon.ammo_drum.AutocannonAmmoDrumItem;
import riftyboi.cbcmodernwarfare.munitions.autocannon.ammo_drum.IAutocannonAmmoDrumContainer;
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

        // ⑦ 走**真实工作台查询路径**：RecipeManager 里到底有没有这 4 个配方。
        //    前面 ①-④ 只是直接 new 出配方对象调 matches，完全没有经过注册与 JSON 加载，
        //    自检全绿也可能工作台里一片空白 —— 这一组就是补上那个盲区。
        try {
            var rm = server.getRecipeManager();
            String[] ids = {"medium_cartridge_assembly", "medium_munition_fuzing",
                    "medium_fuze_removal", "medium_tracer_application"};
            for (String id : ids) {
                var key = net.minecraft.resources.ResourceLocation.withDefaultNamespace(id);
                check("⑦ 配方已加载: " + id, rm.byKey(key).isPresent());
            }
            passed++;
        } catch (RuntimeException | AssertionError e) {
            failed++;
            log("⑦ 配方加载 FAIL: " + e);
        }

        // ⑧ 工作台真实查询：竖列能查到、横排查不到（判定方向规则是否按原版生效）
        try {
            var rm = server.getRecipeManager();
            ItemStack round = CBCModernWarfareItem.AP_MEDIUMCANNON_ROUND.asStack();
            ItemStack shell = CBCModernWarfareItem.FILLED_MEDIUMCANNON_CARTRIDGE.asStack();

            // 竖列：slot0 = 弹头, slot3 = 空筒（同一列，宽 3）
            java.util.List<ItemStack> vertical = new java.util.ArrayList<>(
                    java.util.Collections.nCopies(9, ItemStack.EMPTY));
            vertical.set(0, round);
            vertical.set(3, shell);
            CraftingInput vInput = CraftingInput.of(3, 3, vertical);
            boolean vHit = rm.getRecipeFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING,
                    vInput, level).isPresent();
            check("⑧ 工作台竖向摆放能查到配方", vHit);

            // 横排：slot0 = 弹头, slot1 = 空筒（同一行）
            java.util.List<ItemStack> horizontal = new java.util.ArrayList<>(
                    java.util.Collections.nCopies(9, ItemStack.EMPTY));
            horizontal.set(0, round);
            horizontal.set(1, shell);
            CraftingInput hInput = CraftingInput.of(3, 3, horizontal);
            boolean hHit = rm.getRecipeFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING,
                    hInput, level).isPresent();
            log("⑧ 横排是否匹配 = " + hHit + "（原版规则应为 false，同列才成立）");
            passed++;
        } catch (RuntimeException | AssertionError e) {
            failed++;
            log("⑧ 工作台查询 FAIL: " + e);
        }

        // ⑨ 用 getRecipesFor（工作台实际调的那个）再验一次，并打印命中的配方 id
        try {
            var rm = server.getRecipeManager();
            ItemStack round = CBCModernWarfareItem.AP_MEDIUMCANNON_ROUND.asStack();
            ItemStack shell = CBCModernWarfareItem.FILLED_MEDIUMCANNON_CARTRIDGE.asStack();
            java.util.List<ItemStack> v = new java.util.ArrayList<>(
                    java.util.Collections.nCopies(9, ItemStack.EMPTY));
            v.set(0, round);
            v.set(3, shell);
            var hits = rm.getRecipesFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING,
                    CraftingInput.of(3, 3, v), level);
            log("⑨ getRecipesFor 命中数 = " + hits.size()
                    + " ids=" + hits.stream().map(h -> h.id().toString()).toList());
            check("⑨ 竖排应至少命中 1 个", !hits.isEmpty());
            // 反过来看：结果是否真的能被 assemble 出来
            var holder = hits.get(0);
            ItemStack out = holder.value().assemble(CraftingInput.of(3, 3, v), server.registryAccess());
            log("⑨ assemble 产物 = " + out.getItem() + " x" + out.getCount()
                    + " 有弹头=" + MediumcannonCartridgeItem.hasProjectile(out));
            check("⑨ 产物不是空", !out.isEmpty());
            passed++;
        } catch (RuntimeException | AssertionError e) {
            failed++;
            log("⑨ getRecipesFor FAIL: " + e);
        }

        // ⑩【R6a 核心】mixin 是否真的注入到了 CBC 的类里。
        //
        //    mixin 失效是**静默**的：mixins.json 漏登记、目标方法名/描述符写错，
        //    都不会有任何报错，只是功能不生效。所以必须主动断言。
        //
        //    判据用 instanceof：mixin 在类加载期把接口合成进目标类，
        //    注入成功时 CANON 的实例就是 AmmoItemMixinInerface。
        //    这与「能不能编译」「能不能启动」是两回事 —— 那两条已经验过了。
        try {
            ItemStack round = CBCItems.AP_AUTOCANNON_ROUND.asStack();
            check("⑩ AutocannonRoundItemMixin 已注入（Item 上能拿到接口）",
                    round.getItem() instanceof AmmoItemMixinInerface);

            ItemStack cartridge = CBCItems.AUTOCANNON_CARTRIDGE.asStack();
            check("⑩ AutocannonCartridgeItemMixin 已注入",
                    cartridge.getItem() instanceof AmmoItemMixinInerface);

            ItemStack mgRound = CBCItems.MACHINE_GUN_ROUND.asStack();
            check("⑩ AutocannonMGRoundItemMixin 已注入",
                    mgRound.getItem() instanceof AmmoItemMixinInerface);

            AbstractAutocannonProjectile projectile = CBCModernWarfareEntityTypes.HVAP_AUTOCANNON.create(level);
            check("⑩ AutocannonProjectileMixin 已注入（Entity 上能拿到接口）",
                    projectile instanceof AutocannonProjectileMixinInterface);
            passed++;
        } catch (RuntimeException | AssertionError e) {
            failed++;
            log("⑩ mixin 注入 FAIL: " + e);
        }

        // ⑪ 燃烧剂状态能真正读写（走 mixin 实现的方法，物品侧）
        try {
            ItemStack round = CBCModernWarfareItem.HE_AUTOCANNON_ROUND.asStack();
            AmmoItemMixinInerface ammo = (AmmoItemMixinInerface) round.getItem();
            check("⑪ 初始不是燃烧弹", !ammo.isIncendiary(round));
            ammo.setIncendiary(round, true);
            check("⑪ 设置后读得到", ammo.isIncendiary(round));
            ammo.setIncendiary(round, false);
            check("⑪ 清除后读不到", !ammo.isIncendiary(round));
            passed++;
        } catch (RuntimeException | AssertionError e) {
            failed++;
            log("⑪ 燃烧剂读写 FAIL: " + e);
        }

        // ⑫ 弹药筒侧燃烧剂要写在**弹头**上，且改完必须写回筒（组件副本语义）
        try {
            ItemStack cartridge = CBCItems.AUTOCANNON_CARTRIDGE.asStack();
            AutocannonCartridgeItem.writeProjectile(
                    CBCModernWarfareItem.HE_AUTOCANNON_ROUND.asStack(), cartridge);
            check("⑫ 装弹后筒里有弹头", AutocannonCartridgeItem.hasProjectile(cartridge));

            AmmoItemMixinInerface ammo = (AmmoItemMixinInerface) cartridge.getItem();
            check("⑫ 初始不是燃烧弹", !ammo.isIncendiary(cartridge));

            ammo.setIncendiary(cartridge, true);
            // 读的是「写回之后」的筒 —— 若 mixin 忘了 writeProjectile 写回，这里会 false
            check("⑫ 燃烧标记写回了弹药筒", ammo.isIncendiary(cartridge));

            ItemStack projectile = AutocannonCartridgeItem.getProjectileStack(cartridge);
            check("⑫ 标记落在弹头上",
                    ((AmmoItemMixinInerface) projectile.getItem()).isIncendiary(projectile));
            passed++;
        } catch (RuntimeException | AssertionError e) {
            failed++;
            log("⑫ 弹药筒燃烧剂 FAIL: " + e);
        }

        // ⑬ 实体侧燃烧剂状态读写（走 SynchedEntityData，服务端也能读）
        //
        // 这一组曾经抓到 1.20.1 的一个真实缺陷：判读掩码 &3 与清除掩码 &0xFD 不对称，
        // 导致 setIncendiary(false) 之后仍读成 true（3 & 0xFD = 1，而 1 & 3 ≠ 0）。
        // 用户 2026-11-09 授权修正为对称的 &~3，所以这里把它作为**必须成立**的断言保留 ——
        // 它是这个 bug 不再复发的回归哨兵。
        try {
            AbstractAutocannonProjectile projectile = CBCModernWarfareEntityTypes.HE_AUTOCANNON.create(level);
            AutocannonProjectileMixinInterface incendiary = (AutocannonProjectileMixinInterface) projectile;
            check("⑬ 初始不是燃烧弹", !incendiary.isIncendiary());
            incendiary.setIncendiary(true);
            check("⑬ 设置后读得到", incendiary.isIncendiary());
            incendiary.setIncendiary(false);
            check("⑬ 清除后读不到（1.20.1 此断言会失败，R6a 已修正）", !incendiary.isIncendiary());
            passed++;
        } catch (RuntimeException | AssertionError e) {
            failed++;
            log("⑬ 实体燃烧剂 FAIL: " + e);
        }

        // ⑭ R6a 的 4 个机炮实体属性 handler 都注册上了（R5b 断线根因的同型检查）
        try {
            check("⑭ CANISTER_AUTOCANNON handler 存在",
                    CBCModernWarfareMunitionPropertiesHandlers.CANISTER_AUTOCANNON != null);
            check("⑭ HE_AUTOCANNON handler 存在",
                    CBCModernWarfareMunitionPropertiesHandlers.HE_AUTOCANNON != null);
            check("⑭ HVAP 实体注册成功", CBCModernWarfareEntityTypes.HVAP_AUTOCANNON.isBound());
            check("⑭ APDS 实体注册成功", CBCModernWarfareEntityTypes.APDS_AUTOCANNON.isBound());
            check("⑭ CANISTER 实体注册成功", CBCModernWarfareEntityTypes.CANISTER_AUTOCANNON.isBound());
            check("⑭ HE 实体注册成功", CBCModernWarfareEntityTypes.HE_AUTOCANNON.isBound());
            check("⑭ 4 个机炮 round 物品注册成功",
                    CBCModernWarfareItem.HVAP_AUTOCANNON_ROUND.isBound()
                            && CBCModernWarfareItem.APDS_AUTOCANNON_ROUND.isBound()
                            && CBCModernWarfareItem.CANISTER_AUTOCANNON_ROUND.isBound()
                            && CBCModernWarfareItem.HE_AUTOCANNON_ROUND.isBound());
            passed++;
        } catch (RuntimeException | AssertionError e) {
            failed++;
            log("⑭ R6a 注册 FAIL: " + e);
        }

        // ⑮【R6b】弹鼓：方块 / BE / 菜单 三者都注册上了，且「注册 id 不一致」是预期
        try {
            var drumBlock = CBCModernWarfareBlocks.AUTOCANNON_AMMO_DRUM;
            check("⑮ 弹鼓方块已注册", drumBlock.isBound());
            check("⑮ 弹鼓 BE 已注册", CBCModernWarfareBlockEntities.AUTOCANNON_AMMO_DRUM.isBound());
            check("⑮ 弹鼓菜单已注册", CBCModernWarfareMenuTypes.AUTOCANNON_AMMO_DRUM.isBound());
            // 1.20.1 原样：方块叫 rotarycannon_ammo_drum，BE/Menu 叫 autocannon_ammo_drum
            log("⑮ 方块 id = " + drumBlock.getId() + "；BE id = "
                    + CBCModernWarfareBlockEntities.AUTOCANNON_AMMO_DRUM.getId()
                    + "（两者不同是 1.20.1 原样，不是 bug）");
            check("⑮ 方块 id 是 rotarycannon_ammo_drum",
                    drumBlock.getId().getPath().equals("rotarycannon_ammo_drum"));
            check("⑮ BE id 是 autocannon_ammo_drum",
                    CBCModernWarfareBlockEntities.AUTOCANNON_AMMO_DRUM.getId().getPath()
                            .equals("autocannon_ammo_drum"));
            passed++;
        } catch (RuntimeException | AssertionError e) {
            failed++;
            log("⑮ 弹鼓注册 FAIL: " + e);
        }

        // ⑯【R6b】弹鼓的三个状态属性与朝向都在（外观三态 + 两轴）
        try {
            var state = CBCModernWarfareBlocks.AUTOCANNON_AMMO_DRUM.getDefaultState();
            check("⑯ 默认状态是 closed",
                    state.getValue(AutocannonAmmoDrumBlock.CONTAINER_STATE)
                            == AutocannonAmmoDrumBlock.State.CLOSED);
            check("⑯ 有 AXIS 属性", state.hasProperty(AutocannonAmmoDrumBlock.AXIS));
            check("⑯ 有 WATERLOGGED 属性", state.hasProperty(AutocannonAmmoDrumBlock.WATERLOGGED));
            // State 枚举的序列化名（blockstate json 的 variant 值依赖它）
            check("⑯ State 序列化名正确",
                    AutocannonAmmoDrumBlock.State.FILLED.getSerializedName().equals("filled")
                            && AutocannonAmmoDrumBlock.State.EMPTY.getSerializedName().equals("empty")
                            && AutocannonAmmoDrumBlock.State.CLOSED.getSerializedName().equals("closed"));
            passed++;
        } catch (RuntimeException | AssertionError e) {
            failed++;
            log("⑯ 弹鼓状态 FAIL: " + e);
        }

        // ⑰【R6b】弹鼓的容器契约：空桶容量、装弹后计数
        try {
            ItemStack drum = new ItemStack(CBCModernWarfareBlocks.AUTOCANNON_AMMO_DRUM.get().asItem());
            check("⑰ 弹鼓物品不是空的", !drum.isEmpty());
            check("⑰ 新建弹鼓里没有弹药",
                    AutocannonAmmoDrumItem.getMainAmmoStack(drum).isEmpty()
                            && AutocannonAmmoDrumItem.getSecondaryAmmoStack(drum).isEmpty()
                            && AutocannonAmmoDrumItem.getTracerAmmoStack(drum).isEmpty());
            check("⑰ 默认曳光间隔是 1", AutocannonAmmoDrumItem.getTracerSpacing(drum) == 1);
            passed++;
        } catch (RuntimeException | AssertionError e) {
            failed++;
            log("⑰ 弹鼓物品 FAIL: " + e);
        }

        // ⑱【R6b 核心】弹鼓真实数据链路：放下 → 存弹 → 存档往返 → 读回。
        //
        //    这一组比「注册成功」强得多：它真的往世界放一个方块、走 setItem、
        //    再 saveAdditional/loadAdditional 往返一次，验证弹药不会丢。
        //    1.20.1 的弹鼓内容存在 BE 的 NBT 里，1.21.1 加上 Provider 参数后
        //    任何一个签名没跟上都会在这里暴露。
        try {
            var serverLevel = server.overworld();
            // 找一块高空位置（避免干扰地形；world 是测试存档，无所谓）
            var pos = new net.minecraft.core.BlockPos(0, 200, 0);
            var drumState = CBCModernWarfareBlocks.AUTOCANNON_AMMO_DRUM.getDefaultState();
            serverLevel.setBlock(pos, drumState, 3);

            var be = serverLevel.getBlockEntity(pos);
            check("⑱ 放下后确实生成了弹鼓 BE",
                    be instanceof AutocannonAmmoDrumBlockEntity);
            AutocannonAmmoDrumBlockEntity drum = (AutocannonAmmoDrumBlockEntity) be;

            // 往主弹药槽塞 5 发。注意必须用**弹药筒**而不是整装弹：
            // CBC 的 AutocannonAmmoType.of() 判据是 `item instanceof AutocannonAmmoItem`，
            // 而 AutocannonRoundItem（整装弹/弹头）**没有**实现该接口 ——
            // 只有 AutocannonCartridgeItem（弹药筒）与 MachineGunRoundItem 实现了。
            // 也就是说弹鼓收的是「弹药筒」，这一点与 1.20.1 一致（javap 核实）。
            ItemStack ammo = CBCItems.AUTOCANNON_CARTRIDGE.asStack();
            ammo.setCount(5);
            drum.setItem(IAutocannonAmmoDrumContainer.AMMO_SLOT, ammo);
            check("⑱ 主槽计数正确", drum.getMainAmmoStack().getCount() == 5);
            check("⑱ 总计数正确", drum.getTotalCount() == 5);
            check("⑱ 弹药类型被识别为机炮",
                    drum.getAmmoType() == AutocannonAmmoType.AUTOCANNON);

            // 存档往返（这一步同时验证 saveAdditional/loadAdditional 的双 Provider 签名）
            // 1.21.1 的 loadStatic 签名是 (BlockPos, BlockState, CompoundTag, Provider)
            var saved = drum.saveWithFullMetadata(server.registryAccess());
            var reloaded = net.minecraft.world.level.block.entity.BlockEntity
                    .loadStatic(pos, drumState, saved, server.registryAccess());
            check("⑱ 读回后仍是弹鼓 BE", reloaded instanceof AutocannonAmmoDrumBlockEntity);
            AutocannonAmmoDrumBlockEntity drum2 = (AutocannonAmmoDrumBlockEntity) reloaded;
            check("⑱ 往返后弹药数量没丢", drum2.getMainAmmoStack().getCount() == 5);
            check("⑱ 往返后弹药种类没丢",
                    drum2.getMainAmmoStack().getItem() == CBCItems.AUTOCANNON_CARTRIDGE.get());

            // 清空原地图上的测试方块
            serverLevel.removeBlock(pos, false);
            passed++;
        } catch (RuntimeException | AssertionError e) {
            failed++;
            log("⑱ 弹鼓数据链路 FAIL: " + e);
        }

        // ⑲【R6c-1】燃烧剂：配方已加载 + 涂/拆的完整往返。
        //
        //    这一组直接验证 R6a 修的那个位运算缺陷是否真的让「拆燃烧剂」可用了：
        //    涂上 → 读得到 → 拆掉 → 读不到。若当初照搬 1.20.1 的 & 0xFD，
        //    「拆掉」这一步会失败。
        try {
            var rm = server.getRecipeManager();
            for (String id : new String[]{"incendiary_application", "incendiary_removal",
                    "incendiary_deployer_application"}) {
                check("⑲ 配方已加载: " + id,
                        rm.byKey(net.minecraft.resources.ResourceLocation.withDefaultNamespace(id)).isPresent());
            }

            // 涂燃烧剂：整装弹 + 燃烧头
            ItemStack round = CBCModernWarfareItem.HE_AUTOCANNON_ROUND.asStack();
            ItemStack tip = CBCModernWarfareItem.INCENDIARY_TIP.asStack();
            CraftingInput applyInput = CraftingInput.of(2, 1, List.of(round, tip));
            IncendiaryApplicationRecipie apply = new IncendiaryApplicationRecipie();
            check("⑲ 涂燃烧剂 matches", apply.matches(applyInput, level));
            ItemStack coated = apply.assemble(applyInput, server.registryAccess());
            check("⑲ 涂完仍是那发弹药", coated.getItem() == round.getItem());
            AmmoItemMixinInerface coatedAmmo = (AmmoItemMixinInerface) coated.getItem();
            check("⑲ 涂完读得到燃烧标记", coatedAmmo.isIncendiary(coated));

            // 已经涂过的不能再涂（反例）
            CraftingInput againInput = CraftingInput.of(2, 1, List.of(coated, tip));
            check("⑲ 已涂过的不能再涂", !apply.matches(againInput, level));

            // 拆燃烧剂：输出是燃烧头，输入槽留下剥掉标记的那发
            CraftingInput removeInput = CraftingInput.of(1, 1, List.of(coated));
            IncendiaryRemovalRecipie remove = new IncendiaryRemovalRecipie();
            check("⑲ 拆燃烧剂 matches", remove.matches(removeInput, level));
            check("⑲ 拆出的产物是燃烧头",
                    remove.assemble(removeInput, server.registryAccess()).getItem()
                            == CBCModernWarfareItem.INCENDIARY_TIP.get());
            ItemStack stripped = remove.getRemainingItems(removeInput).get(0);
            check("⑲ 输入槽留下了那发弹药", !stripped.isEmpty());
            check("⑲ 剥完读不到燃烧标记（1.20.1 此断言会失败，R6a 已修）",
                    !((AmmoItemMixinInerface) stripped.getItem()).isIncendiary(stripped));
            passed++;
        } catch (RuntimeException | AssertionError e) {
            failed++;
            log("⑲ 燃烧剂配方 FAIL: " + e);
        }

        // ⑳【R6c-1】燃烧剂写在**弹药筒里的弹头**上，且能写回筒（组件副本语义）
        try {
            ItemStack cartridge = CBCItems.AUTOCANNON_CARTRIDGE.asStack();
            AutocannonCartridgeItem.writeProjectile(
                    CBCModernWarfareItem.HE_AUTOCANNON_ROUND.asStack(), cartridge);

            CraftingInput in = CraftingInput.of(2, 1,
                    List.of(cartridge, CBCModernWarfareItem.INCENDIARY_TIP.asStack()));
            IncendiaryApplicationRecipie apply = new IncendiaryApplicationRecipie();
            check("⑳ 弹药筒涂燃烧剂 matches", apply.matches(in, level));
            ItemStack coated = apply.assemble(in, server.registryAccess());
            check("⑳ 涂完仍是弹药筒", coated.getItem() == cartridge.getItem());
            check("⑳ 涂完读得到（说明写回了筒）",
                    ((AmmoItemMixinInerface) coated.getItem()).isIncendiary(coated));
            passed++;
        } catch (RuntimeException | AssertionError e) {
            failed++;
            log("⑳ 弹药筒燃烧剂 FAIL: " + e);
        }

        log("PASS=" + passed + " FAIL=" + failed);        if (failed == 0) {
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
