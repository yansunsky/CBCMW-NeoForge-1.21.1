package riftyboi.cbcmodernwarfare;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

import com.tterrag.registrate.util.entry.RegistryEntry;

import static riftyboi.cbcmodernwarfare.CBCModernWarfare.REGISTRATE;

/**
 * The mod's creative inventory tab.
 *
 * <p>1.20.1 used Architectury's {@code @ExpectPlatform} to delegate tab creation to
 * {@code forge/ModGroupImpl} (Forge {@code DeferredRegister} plus
 * {@code CreateRegistrate#setCreativeTab}). On NeoForge 1.21.1 Registrate can do the whole
 * job itself: {@code defaultCreativeTab(name, builder -> ...)} returns an owned entry and
 * automatically routes every content entry registered after {@code useModTab()} into it.
 * The platform split is therefore gone, not stubbed.
 */
public class ModGroup {

    public static final ResourceKey<CreativeModeTab> MAIN_TAB_KEY = makeKey("base");

    /**
     * Registered through Registrate so that {@link #useModTab()} can point the shared
     * registrate instance at it before content entries are declared.
     */
    /**
     * 模组的创造模式标签页。
     *
     * <p><b>不要在这里手动 {@code output.accept()} 任何本模组的条目。</b>
     * Create 的 {@code CreateRegistrate} 会在每次注册条目时把
     * {@code TAB_LOOKUP.put(entry, currentTab)} 记下来，并在
     * {@code BuildCreativeModeTabContentsEvent} 里自动把它们加进这个 tab。
     * 如果同时又在 {@code displayItems} 里手动 accept，NeoForge 会抛：
     *
     * <pre>
     * IllegalArgumentException: Itemstack 1 cbcmodernwarfare:era_block
     *     already exists in the tab's list
     * </pre>
     *
     * <p>这个崩溃在 R4b 之后首次进客户端时出现（打开创造栏即崩）——
     * 1.20.1 的手动列表在单平台 + 自动收集下变成了重复来源。
     * 现在 tab 内容完全由 {@link #useModTab()} 的自动收集决定。
     */
    public static final RegistryEntry<CreativeModeTab, CreativeModeTab> GROUP =
            REGISTRATE.defaultCreativeTab("base", builder -> builder
                    .title(Component.translatable("itemGroup." + CBCModernWarfare.MOD_ID))
                    .withTabsBefore(com.simibubi.create.Create.asResource("palettes"))
                    .icon(() -> riftyboi.cbcmodernwarfare.index.CBCModernWarfareBlocks.ERA_BLOCK.asStack()))
                    .register();

    /** Sends every subsequently registered Registrate entry into this mod's tab. */
    public static void useModTab() {
        REGISTRATE.setCreativeTab(GROUP);
    }

    public static ResourceKey<CreativeModeTab> makeKey(String id) {
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB, CBCModernWarfare.resource(id));
    }

    public static void register() {
        REGISTRATE.addRawLang("itemGroup." + CBCModernWarfare.MOD_ID, "CBC Modern Warfare");
    }
}
