package riftyboi.cbcmodernwarfare.munitions.autocannon;

/**
 * 实体侧的燃烧剂（incendiary）状态读写。
 *
 * <p>由 {@code AutocannonProjectileMixin}（注入到 CBC 的 {@code AbstractAutocannonProjectile}）
 * 实现，状态存在同步实体数据（{@code SynchedEntityData}）里，服务端与客户端都能读到。
 *
 * <p>消费点：R6c-1 的燃烧剂配方与转管炮发射链路
 * （{@code MountedRotarycannonContraption}，见 R6 计划书 §1 的补记）。
 */
public interface AutocannonProjectileMixinInterface {

	boolean isIncendiary();

	void setIncendiary(boolean incendiary);
}
