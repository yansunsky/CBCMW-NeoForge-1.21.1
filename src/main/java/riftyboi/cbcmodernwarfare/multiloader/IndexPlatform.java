package riftyboi.cbcmodernwarfare.multiloader;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.tterrag.registrate.util.nullness.NonNullSupplier;

import riftyboi.cbcmodernwarfare.CBCModernWarfare;

/**
 * The last remnants of the multi-loader shim.
 *
 * <p>In 1.20.1 every method here was an Architectury {@code @ExpectPlatform} stub with a
 * Forge implementation in {@code multiloader/forge/IndexPlatformImpl}. There is only one
 * platform now, so the platform split is <b>deleted</b> rather than kept as an indirection:
 * the methods stay because their call sites (recipes, sights, rotary cannon breech, chunk
 * events) are still being ported and keeping the signatures stable avoids touching those
 * call sites twice.
 */
public final class IndexPlatform {

    /** Recipe serializers of this mod. Registered on the mod event bus. */
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, CBCModernWarfare.MOD_ID);

    /** Recipe types of this mod. Registered on the mod event bus. */
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, CBCModernWarfare.MOD_ID);

    private IndexPlatform() {}

    /** Attaches the deferred registries held here to the mod event bus. */
    public static void register(IEventBus modEventBus) {
        RECIPE_SERIALIZERS.register(modEventBus);
        RECIPE_TYPES.register(modEventBus);
    }

    /**
     * Creates the rotary cannon breech block entity.
     *
     * <p>TODO(R4): the concrete class lived in {@code forge/cannons/} and used
     * {@code net.minecraftforge.items.IItemHandler}. Port it to
     * {@code net.neoforged.neoforge.items.IItemHandler} and implement this for real.
     */
    public static Object makeRotarycannonBreech(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        throw new UnsupportedOperationException("TODO(R4): rotary cannon breech block entity is not ported yet");
    }

    public static Supplier<RecipeSerializer<?>> registerRecipeSerializer(ResourceLocation id,
            NonNullSupplier<RecipeSerializer<?>> sup) {
        return RECIPE_SERIALIZERS.register(id.getPath(), sup);
    }

    public static void registerRecipeType(ResourceLocation id, Supplier<RecipeType<?>> type) {
        RECIPE_TYPES.register(id.getPath(), type);
    }

    /**
     * Creates the server-side stand-in player that "holds" a sight for camera purposes.
     *
     * <p>TODO(R8): ported together with {@code content/sights}. The 1.20.1 implementation
     * ({@code forge/SightFakePlayerForge}) extends the Forge {@code FakePlayer}; on NeoForge
     * the equivalent base is {@code net.neoforged.neoforge.common.util.FakePlayer}.
     */
    public static ServerPlayer createSightFakePlayer(ServerLevel level, Object conductor) {
        throw new UnsupportedOperationException("TODO(R8): sight fake player is not ported yet");
    }

    /** Re-posts a vanilla-Forge chunk load/unload event on the NeoForge event bus. */
    public static void postChunkEventClient(LevelChunk chunk, boolean load) {
        if (load) {
            NeoForge.EVENT_BUS.post(new ChunkEvent.Load(chunk, false));
        } else {
            NeoForge.EVENT_BUS.post(new ChunkEvent.Unload(chunk));
        }
    }
}
