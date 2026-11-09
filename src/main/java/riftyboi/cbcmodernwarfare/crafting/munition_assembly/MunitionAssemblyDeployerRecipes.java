package riftyboi.cbcmodernwarfare.crafting.munition_assembly;

import java.util.Optional;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import com.simibubi.create.content.kinetics.deployer.DeployerRecipeSearchEvent;

import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

import riftyboi.cbcmodernwarfare.index.CBCModernWarfareRecipeTypes;

/**
 * 机械手（Create deployer）配方的接入点。
 *
 * <p><b>为什么需要这个类</b>：机械手不像工作台那样从 {@code RecipeManager} 里按类型查配方。
 * Create 6.0.10 的 {@code DeployerBlockEntity#getRecipe} 只查它自己的
 * {@code DEPLOYING} / {@code ITEM_APPLICATION} 两类，然后 post 一个
 * {@link DeployerRecipeSearchEvent}，让附属模组按优先级插入自己的配方：
 *
 * <pre>
 * ItemStack 0 = 机械手要作用的物品（传送带/置物台上那一个）
 * ItemStack 1 = 机械手手上拿的物品
 * </pre>
 *
 * <p>CBC 5.11.7 走的是同一条路（{@code CBCCommonNeoForgeEvents#onDeployerRecipeSearch}
 * → {@code CBCCommonEvents#onAddDeployerRecipes}），优先级 25。这里保持一致：
 * 现场 new 一个「值对象配方」，{@code matches} 成立才登记，配方 id 用配方类型 id。
 *
 * <p>{@code MapCodec.unit} 那套序列化器对这两个配方只是摆设（它们不是数据驱动的），
 * 但配方类型必须存在，因为 {@code Recipe#getType()} 要返回它。
 */
public final class MunitionAssemblyDeployerRecipes {

    /**
     * 与 CBC 自己的机械手配方同级。
     *
     * <p>Create 内置：顺序装配 100、{@code DEPLOYING}/{@code ITEM_APPLICATION} 各 50。
     * 数值越大越优先；同优先级时先登记者保留。
     */
    private static final int PRIORITY = 25;

    private MunitionAssemblyDeployerRecipes() {
    }

    public static void onDeployerRecipeSearch(DeployerRecipeSearchEvent event) {
        RecipeWrapper inventory = event.getInventory();
        ItemStack munition = inventory.getItem(0);
        ItemStack held = inventory.getItem(1);
        if (munition.isEmpty() || held.isEmpty()) {
            return;
        }

        MediumFuzingDeployerRecipe fuzing = new MediumFuzingDeployerRecipe(munition, held);
        if (fuzing.matches(inventory, event.getBlockEntity().getLevel())) {
            event.addRecipe(() -> Optional.of(new RecipeHolder<>(
                    CBCModernWarfareRecipeTypes.MEDIUM_DEPLOYER_MUNITION_FUZING.getId(), fuzing)), PRIORITY);
        }

        MediumTracerDeployerRecipe tracer = new MediumTracerDeployerRecipe(munition, held);
        if (tracer.matches(inventory, event.getBlockEntity().getLevel())) {
            event.addRecipe(() -> Optional.of(new RecipeHolder<>(
                    CBCModernWarfareRecipeTypes.MEDIUM_TRACER_DEPLOYER_RECIPE.getId(), tracer)), PRIORITY);
        }

        // R6c-1：机械手涂燃烧剂（手持燃烧头，作用于机炮弹药）
        IncendiaryApplicationDeployerRecipe incendiary = new IncendiaryApplicationDeployerRecipe(munition, held);
        if (incendiary.matches(inventory, event.getBlockEntity().getLevel())) {
            event.addRecipe(() -> Optional.of(new RecipeHolder<>(
                    CBCModernWarfareRecipeTypes.INCENDIARY_DEPLOYER_APPLICATION.getId(), incendiary)),
                    PRIORITY);
        }
    }
}
