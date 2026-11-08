package riftyboi.cbcmodernwarfare.cannons.medium_cannon.material;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;

import rbasamoyai.createbigcannons.multiloader.NetworkPlatform;
import rbasamoyai.createbigcannons.network.RootPacket;

/**
 * Datapack-driven {@code mediumcannon_materials/} reload listener.
 *
 * <p><b>1.20.1 → 1.21.1 (two changes, both verified against CBC 5.11.7 + the reference port):</b>
 * <ol>
 *   <li>{@link RootPacket} lost {@code rootEncode(FriendlyByteBuf)} — 1.21.1 encodes through
 *       {@code CBCRootNetwork.PACKET_STREAM_CODEC} instead. Only {@code handle(...)} remains.
 *       The 1.20.1 packet captured a copy of the buffer; on 1.21.1 the payload has to be read
 *       from the packet's own fields, so the "buffer snapshot" trick is kept but the class is a
 *       {@code record} with an explicit {@code copyOf} (same shape the reference port uses).</li>
 *   <li>{@code handle}'s third parameter is {@link Player}, not {@code ServerPlayer}
 *       ({@code RootPacket#handle(Executor, PacketListener, Player)} — confirmed by javap on
 *       CBC 5.11.7).</li>
 * </ol>
 */
public class MediumcannonMaterialPropertiesHandler {

    public static final Map<MediumcannonMaterial, MediumcannonMaterialProperties> PROPERTIES = new HashMap<>();

    public static class ReloadListener extends SimpleJsonResourceReloadListener {
        private static final Gson GSON = new Gson();
        public static final ReloadListener INSTANCE = new ReloadListener();

        public ReloadListener() {
            super(GSON, "mediumcannon_materials");
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler) {
            PROPERTIES.clear();
            for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
                JsonElement el = entry.getValue();
                if (!el.isJsonObject()) {
                    continue;
                }
                try {
                    MediumcannonMaterial material = MediumcannonMaterial.fromName(entry.getKey());
                    PROPERTIES.put(material, MediumcannonMaterialProperties.fromJson(el.getAsJsonObject()));
                } catch (Exception ignored) {
                    // 1.20.1 swallowed malformed entries here on purpose: a bad datapack file must
                    // not take the whole reload down. Kept identical.
                }
            }
        }
    }

    public static MediumcannonMaterialProperties getMaterial(MediumcannonMaterial material) {
        return PROPERTIES.get(material);
    }

    public static void writeBuf(FriendlyByteBuf buf) {
        buf.writeVarInt(PROPERTIES.size());
        for (Map.Entry<MediumcannonMaterial, MediumcannonMaterialProperties> entry : PROPERTIES.entrySet()) {
            buf.writeUtf(entry.getKey().name().toString());
            entry.getValue().writeBuf(buf);
        }
    }

    public static void readBuf(FriendlyByteBuf buf) {
        PROPERTIES.clear();
        int sz = buf.readVarInt();
        for (int i = 0; i < sz; ++i) {
            PROPERTIES.put(MediumcannonMaterial.fromName(ResourceLocation.parse(buf.readUtf())),
                    MediumcannonMaterialProperties.fromBuf(buf));
        }
    }

    public static void syncTo(ServerPlayer player) {
        NetworkPlatform.sendToClientPlayer(new ClientboundMediumcannonMaterialPropertiesPacket(), player);
    }

    public static void syncToAll(MinecraftServer server) {
        NetworkPlatform.sendToClientAll(new ClientboundMediumcannonMaterialPropertiesPacket(), server);
    }

    /**
     * Syncs the whole material table to the client.
     *
     * <p>TODO(R9): the packet needs its {@code StreamCodec} registered with
     * {@code CBCRootNetwork} — that happens in {@code index/CBCModernWarfarePackets}, which is
     * still an empty enum (R9). Until then this record is only referenced by
     * {@link #syncTo}/{@link #syncToAll}, which nothing calls yet.
     */
    public record ClientboundMediumcannonMaterialPropertiesPacket(@Nullable FriendlyByteBuf buf) implements RootPacket {

        public ClientboundMediumcannonMaterialPropertiesPacket() {
            this(null);
        }

        public static ClientboundMediumcannonMaterialPropertiesPacket copyOf(FriendlyByteBuf buf) {
            return new ClientboundMediumcannonMaterialPropertiesPacket(new FriendlyByteBuf(buf.copy()));
        }

        @Override
        public void handle(Executor exec, PacketListener listener, @Nullable Player sender) {
            if (this.buf != null) {
                readBuf(this.buf);
            }
        }
    }
}
