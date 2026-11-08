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
    public static final RegistryEntry<CreativeModeTab, CreativeModeTab> GROUP =
            REGISTRATE.defaultCreativeTab("base", builder -> builder
                    .title(Component.translatable("itemGroup." + CBCModernWarfare.MOD_ID))
                    .withTabsBefore(com.simibubi.create.Create.asResource("palettes"))
                    .displayItems((params, output) -> {
                        // TODO(R3): the full creative listing is restored together with
                        //  index/CBCModernWarfareBlocks and CBCModernWarfareItem. The 1.20.1
                        //  1.20.1 entry order is preserved in docs/plan/02-迁移计划书.md §R3.
                    }))
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
