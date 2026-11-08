package riftyboi.cbcmodernwarfare.cannons;

import net.minecraft.world.phys.Vec3;

import riftyboi.cbcmodernwarfare.network.RecoilingCannonUpdatePacket;

/**
 * 会「后坐」的火炮。
 *
 * <p>1.20.1 → 1.21.1 零变化：两个方法签名都没动（参考实现逐字保留）。
 * 唯一要注意的是 {@link RecoilingCannonUpdatePacket} 在 1.21.1 改为走
 * {@code CBCRootNetwork.PACKET_STREAM_CODEC}，但那属于网络层的实现细节，
 * 这个接口本身不受影响。
 */
public interface RecoilingCannon {

    void recoil(Vec3 v);

    void handlePacket(RecoilingCannonUpdatePacket p);
}
