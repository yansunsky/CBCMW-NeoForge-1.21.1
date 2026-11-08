package riftyboi.cbcmodernwarfare;

import java.util.Arrays;
import java.util.List;

import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider;

import net.minecraft.data.tags.TagsProvider.TagAppender;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import rbasamoyai.createbigcannons.utils.CBCRegistryUtils;
import rbasamoyai.createbigcannons.utils.CBCUtils;

import static riftyboi.cbcmodernwarfare.CBCModernWarfare.REGISTRATE;

/**
 * Item tags of this mod.
 *
 * <p><b>What changed for 1.21.1</b>
 *
 * <ul>
 *   <li>1.20.1 reached into Registrate's provider with a raw cast
 *       ({@code ((TagsProvider<Item>) prov).tag(tag)}) and needed CBC's mixin
 *       {@code TagAppenderAccessor} plus {@code ForcedTagEntry} to append a <i>mandatory</i>
 *       tag reference. Registrate 1.21 exposes
 *       {@link RegistrateTagsProvider#addTag(TagKey)} directly and the vanilla
 *       {@link TagAppender#addTag(TagKey)} does the same job — so <b>both the accessor mixin
 *       and {@code ForcedTagEntry} are gone</b>, and no CBC mixin has to apply for tags.</li>
 *   <li>No access transformer for {@code TagsProvider#tag} is needed any more. The disabled
 *       entry in {@code accesstransformer.cfg} stays disabled (documented there).</li>
 *   <li>{@code CBCRegistryUtils.getItemRegistry()} still exists in CBC 5.11.7, so the item
 *       lookups read as they did in 1.20.1.</li>
 *   <li>The mod-loader marker tags ({@code forge:} / {@code c:} namespaced optionals) are
 *       kept verbatim: they are plain resource locations and still resolve on 1.21.</li>
 *   <li>The upstream TODO about "fabric convention tags in 1.21" is satisfied by the {@code c}
 *       namespace entries below, which is what both loaders read on 1.21 (item #3 in the list).</li>
 * </ul>
 */
public class CBCModernWarfareTags {

    public static class CBCModernWarfareItemTags {

        public static final TagKey<Item>
                NUGGET_CARBON_STEEL = commonTag("nugget_carbon_steel", "nuggets/carbon_steel", "carbon_steel_nuggets"),
                INGOT_CARBON_STEEL = commonTag("ingot_carbon_steel", "ingots/carbon_steel", "carbon_steel_ingots"),
                BLOCK_CARBON_STEEL = commonTag("block_carbon_steel", "storage_blocks/carbon_steel", "carbon_steel_blocks"),
                NUGGET_SILICON = commonTag("nugget_silicon", "nuggets/silicon", "silicon_nuggets"),
                INGOT_SILICON = commonTag("ingot_silicon", "ingots/silicon", "silicon_ingots");

        // Crafting tags
        public static final TagKey<Item>
                SPENT_MEDIUMCANNON_CASINGS = makeTag("spent_mediumcannon_casings"),
                MEDIUMCANNON_CARTRIDGES = makeTag("mediumcannon_cartridges"),
                MEDIUMCANNON_ROUNDS = makeTag("mediumcannon_rounds");

        public static TagKey<Item> makeTag(String loc) {
            TagKey<Item> tag = CBCRegistryUtils.createItemTag(CBCModernWarfare.resource(loc));
            REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS,
                    prov -> ((RegistrateTagsProvider<Item>) prov).addTag(tag));
            return tag;
        }

        public static TagKey<Item> commonTag(String mainPath, String forgePath, String fabricPath) {
            TagKey<Item> mainTag = makeTag(mainPath);
            addOptionalTagsToItemTag(mainTag, Arrays.asList(
                    CBCUtils.location("forge", forgePath),
                    CBCUtils.location("c", forgePath), // For forge -> fabric ports, e.g. Create
                    CBCUtils.location("c", fabricPath)));
            return mainTag;
        }

        public static void addItemsToItemTag(TagKey<Item> tag, Item... items) {
            REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, prov -> {
                TagAppender<Item> app = ((RegistrateTagsProvider<Item>) prov).addTag(tag);
                for (Item item : items) {
                    CBCRegistryUtils.getItemRegistry().getResourceKey(item).ifPresent(app::add);
                }
            });
        }

        public static void addItemsToItemTag(TagKey<Item> tag, ItemLike... items) {
            REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, prov -> {
                TagAppender<Item> app = ((RegistrateTagsProvider<Item>) prov).addTag(tag);
                for (ItemLike bp : items) {
                    CBCRegistryUtils.getItemRegistry().getResourceKey(bp.asItem()).ifPresent(app::add);
                }
            });
        }

        @SafeVarargs
        public static void addTagsToItemTag(TagKey<Item> tag, TagKey<Item>... tags) {
            REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, prov -> {
                TagAppender<Item> app = ((RegistrateTagsProvider<Item>) prov).addTag(tag);
                for (TagKey<Item> t : tags) {
                    app.addTag(t);
                }
            });
        }

        public static void addIdsToItemTag(TagKey<Item> tag, ResourceLocation... ids) {
            REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, prov -> {
                TagAppender<Item> app = ((RegistrateTagsProvider<Item>) prov).addTag(tag);
                for (ResourceLocation id : ids) {
                    app.addOptional(id);
                }
            });
        }

        public static void addOptionalTagsToItemTag(TagKey<Item> tag, List<ResourceLocation> ops) {
            REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, prov -> {
                TagAppender<Item> app = ((RegistrateTagsProvider<Item>) prov).addTag(tag);
                ops.forEach(app::addOptionalTag);
            });
        }
    }
}
