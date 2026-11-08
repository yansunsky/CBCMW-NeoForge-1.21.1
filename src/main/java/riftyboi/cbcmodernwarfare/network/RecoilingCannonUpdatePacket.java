package riftyboi.cbcmodernwarfare.network;

import java.util.concurrent.Executor;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketListener;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import rbasamoyai.createbigcannons.network.RootPacket;

import riftyboi.cbcmodernwarfare.cannons.RecoilingCannon;

/**
 * 服务端 → 客户端：同步某门炮的后坐状态（当前位移 + 目标位移）。
 *
 * <p><b>1.20.1 → 1.21.1 的三处改动</b>（{@code javap} 于 CBC 5.11.7 核对）：
 * <ol>
 *   <li>{@link RootPacket} 不再有 {@code rootEncode(FriendlyByteBuf)} ——
 *       1.21.1 的编码统一交给 {@code CBCRootNetwork.PACKET_STREAM_CODEC}，
 *       所以这里改为提供一个静态 {@link StreamCodec}。</li>
 *   <li>{@code handle} 的第三个参数从 {@code ServerPlayer} 变成 {@link Player}
 *       （{@code RootPacket#handle(Executor, PacketListener, Player)}）。</li>
 *   <li>{@code RegistryFriendlyByteBuf} 取代 {@code FriendlyByteBuf} ——
 *       凡是走 CBC 网络栈的包都必须是 registry-aware 的缓冲区。</li>
 * </ol>
 *
 * <p>注意：{@code Minecraft} 是客户端专有类。本类的 {@code handle} 只会在客户端被调用
 * （服务端根本不派发 {@code Clientbound*} 包），所以与 1.20.1 一样直接引用，
 * 不做 dist 隔离 —— 但这也意味着本类不能被服务端在类加载阶段主动触碰。
 *
 * <p><b>TODO(R8)</b>：本包尚未登记进 {@code CBCRootNetwork}（那是
 * {@code CBCModernWarfarePackets} 的事）。登记之前它只会被
 * {@code MountedMediumcannonContraption}（R4b）引用而不会真的收发。
 */
public class RecoilingCannonUpdatePacket implements RootPacket {

    /** 1.21.1 的编解码器：按字段顺序写读，与 1.20.1 的 rootEncode 等价。 */
    public static final StreamCodec<RegistryFriendlyByteBuf, RecoilingCannonUpdatePacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, p -> p.entityID,
                    ByteBufCodecs.FLOAT, p -> (float) p.motion,
                    ByteBufCodecs.FLOAT, p -> (float) p.target,
                    RecoilingCannonUpdatePacket::new);

    public int entityID;
    public double motion;
    public double target;

    public RecoilingCannonUpdatePacket() {}

    public RecoilingCannonUpdatePacket(int entityID, double motion, double sequenceLimit) {
        this.entityID = entityID;
        this.motion = motion;
        this.target = sequenceLimit;
    }

    /** 供 {@link #STREAM_CODEC} 反序列化用。 */
    public RecoilingCannonUpdatePacket(int entityID, float motion, float target) {
        this.entityID = entityID;
        this.motion = motion;
        this.target = target;
    }

    @Override
    public void handle(Executor executor, PacketListener listener, @Nullable Player player) {
        Entity entity = Minecraft.getInstance().level.getEntity(this.entityID);
        if (!(entity instanceof RecoilingCannon rce)) {
            return;
        }
        rce.handlePacket(this);
    }
}
