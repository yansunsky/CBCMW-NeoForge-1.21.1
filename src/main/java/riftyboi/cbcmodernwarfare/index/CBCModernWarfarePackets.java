package riftyboi.cbcmodernwarfare.index;

/**
 * Network packet registration.
 *
 * <p><b>1.20.1 → 1.21.1</b>: this was an empty enum in 1.20.1 too (88 bytes), kept as the
 * designated home for the packet codecs. CBC's {@code RootPacket} lost
 * {@code rootEncode(FriendlyByteBuf)}; 1.21.1 encodes through
 * {@code CBCRootNetwork.PACKET_STREAM_CODEC}, so every packet now needs a
 * {@code StreamCodec} registered here.
 *
 * <p>TODO(R8): fill in once {@code network/} is ported. The six packets are
 * {@code CameraMovePacket}, {@code DismountCameraPacket}, {@code SetCameraViewPacket},
 * {@code RecoilingCannonUpdatePacket}, {@code ServerboundSetSmokeDischargerValuePacket} and
 * {@code CBCModernWarfareClientHandlers}. All of them currently implement only
 * {@code handle(Executor, PacketListener, Player)} — see
 * {@code docs/analysis/03-垂直切片验证结论.md} §5.
 */
public enum CBCModernWarfarePackets {

}
