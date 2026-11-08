package riftyboi.cbcmodernwarfare.index;

import static riftyboi.cbcmodernwarfare.CBCModernWarfare.REGISTRATE;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.AllSoundEvents.SoundEntry;
import com.simibubi.create.AllSoundEvents.SoundEntryBuilder;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import riftyboi.cbcmodernwarfare.CBCModernWarfare;

/**
 * Sound events of this mod.
 *
 * <p><b>1.20.1 → 1.21.1</b>
 *
 * <ul>
 *   <li>1.20.1 extended {@code SoundEntryBuilder} through a subclass
 *       ({@code CBCModernWarfareSoundEntryBuilder}) whose only job was to put the built entry into
 *       the {@code ALL} map. That still works — {@code AllSoundEvents.SoundEntryBuilder} is a plain
 *       public nested class with a public {@code (ResourceLocation)} constructor, verified by
 *       {@code javap} on Create 6.0.10. But {@code AllSoundEvents#create(ResourceLocation)} is now
 *       {@code public static} and already does exactly that (adds to {@code AllSoundEvents.ALL}),
 *       so the subclass is no longer needed: we keep our own map and register from it.</li>
 *   <li>{@code SoundEntry#prepare()} / {@code #register(RegisterHelper)} / {@code #write(JsonObject)}
 *       / {@code #getSubtitleKey()} / {@code #hasSubtitle()} all survived unchanged.</li>
 *   <li>{@code SoundEntryProvider} used {@code net.minecraft.data.HashCache}; 1.21.1 uses
 *       {@code CachedOutput}. The signature of {@code DataProvider#run} already changed in 1.20.5,
 *       and {@code DataProvider.saveStable(CachedOutput, JsonObject, Path)} is unchanged.</li>
 * </ul>
 */
public class CBCModernWarfareSoundEvents {

    public static final Map<ResourceLocation, SoundEntry> ALL = new HashMap<>();

    public static final SoundEntry
            FIRE_MEDIUMCANNON = create("fire_mediumcannon")
                    .subtitle("Mediumcannon fires")
                    .category(SoundSource.BLOCKS)
                    .build(),
            ROCKET_FLYING = create("rocket_flying")
                    .subtitle("Rocket Flying")
                    .category(SoundSource.BLOCKS)
                    .build();

    private static SoundEntryBuilder create(String id) {
        return new TrackingSoundEntryBuilder(CBCModernWarfare.resource(id));
    }

    /** Registers every sound event with the mod's {@code SOUND_EVENT} registry helper. */
    public static void prepare() {
        for (SoundEntry entry : ALL.values()) {
            entry.prepare();
        }
    }

    /**
     * Called from the {@code RegisterEvent} listener in the mod constructor.
     *
     * <p>1.20.1 wrote {@code event.register(Registries.SOUND_EVENT, helper -> ...)}; NeoForge 21.1
     * keeps that shape.
     */
    public static void register(java.util.function.Consumer<SoundEntry> cons) {
        for (SoundEntry entry : ALL.values()) {
            cons.accept(entry);
        }
    }

    public static void registerLangEntries() {
        for (SoundEntry entry : ALL.values()) {
            if (entry.hasSubtitle()) {
                REGISTRATE.addRawLang(entry.getSubtitleKey(), entry.getSubtitle());
            }
        }
    }

    /** Builder that also files the entry into {@link #ALL}, replacing the 1.20.1 subclass. */
    private static class TrackingSoundEntryBuilder extends SoundEntryBuilder {
        TrackingSoundEntryBuilder(ResourceLocation id) {
            super(id);
        }

        @Override
        public SoundEntry build() {
            SoundEntry entry = super.build();
            ALL.put(entry.getId(), entry);
            return entry;
        }
    }

    public static SoundEntryProvider provider(PackOutput output) {
        return new SoundEntryProvider(output);
    }

    /**
     * Writes {@code assets/cbcmodernwarfare/sounds.json}.
     *
     * <p>NOTE(1.21.1): 1.20.1 resolved the folder with {@code CreateBigCannons.MOD_ID} while the
     * datagen output went into this mod's namespace — that only worked because the 1.20.1 tree
     * happened to ship both. Fixed to {@link CBCModernWarfare#MOD_ID} here, which is where the
     * sound files actually live.
     */
    public static class SoundEntryProvider implements DataProvider {
        private final PackOutput output;

        public SoundEntryProvider(PackOutput output) {
            this.output = output;
        }

        @Override
        public CompletableFuture<?> run(CachedOutput cache) {
            java.nio.file.Path path = this.output.getOutputFolder()
                    .resolve("assets/" + CBCModernWarfare.MOD_ID);
            com.google.gson.JsonObject json = new com.google.gson.JsonObject();
            ALL.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> entry.getValue().write(json));
            return DataProvider.saveStable(cache, json, path.resolve("sounds.json"));
        }

        @Override
        public String getName() {
            return "CBC Modern Warfare custom sounds";
        }
    }
}
