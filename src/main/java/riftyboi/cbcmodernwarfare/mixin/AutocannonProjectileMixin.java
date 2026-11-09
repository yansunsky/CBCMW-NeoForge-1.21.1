package riftyboi.cbcmodernwarfare.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile;
import rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile.ImpactResult;
import rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile.ImpactResult.KinematicOutcome;
import rbasamoyai.createbigcannons.munitions.ProjectileContext;
import rbasamoyai.createbigcannons.munitions.autocannon.AbstractAutocannonProjectile;

import riftyboi.cbcmodernwarfare.munitions.autocannon.AutocannonProjectileMixinInterface;

/**
 * 给 CBC 的所有机炮炮弹（{@code AbstractAutocannonProjectile}）加燃烧剂状态与命中起火。
 *
 * <p>这是 4 个 mixin 里最重的一个。喷射火焰的逻辑本身来自 1.20.1 原项目，
 * 之所以要 mixin，是因为状态必须挂在 CBC 的实体类上而 CBC 的类不可改。
 *
 * <p><b>1.20.1 → 1.21.1 的四组结构性改动</b>（全部经 javap 核实）：
 * <ol>
 *   <li>{@code ImpactResult} 由顶层类变成
 *       {@code AbstractCannonProjectile} 的<b>嵌套 record</b>
 *       （字段 {@code kinematics()} / {@code shouldRemove()}），
 *       其枚举是 {@code ImpactResult.KinematicOutcome}。</li>
 *   <li>{@code defineSynchedData} 现在收 {@link SynchedEntityData.Builder}，
 *       所以是 {@code builder.define(...)} 而不是 {@code this.entityData.define(...)}；
 *       并且仍要调 {@code super.defineSynchedData(builder)} 保住父类的字段。</li>
 *   <li>{@code onImpact} / {@code onHitEntity} 都定义在
 *       {@code AbstractCannonProjectile}（不是 {@code AbstractAutocannonProjectile}），
 *       本类继承 {@code AbstractCannonProjectile} 才能覆写它们。</li>
 *   <li>{@code Level.explode(Entity, DamageSource, ExplosionDamageCalculator, double, double,
 *       double, float, boolean, Level.ExplosionInteraction)} 重载未变，仍可用
 *       {@code Level.ExplosionInteraction.NONE}。注意这与 R2 记录的 CBC {@code FlakExplosion}
 *       不同 —— 那边走的是原版 {@code Explosion.BlockInteraction}，两个枚举别混。</li>
 * </ol>
 */
@Mixin(AbstractAutocannonProjectile.class)
public abstract class AutocannonProjectileMixin extends AbstractCannonProjectile
		implements AutocannonProjectileMixinInterface {

	/** 燃烧剂标记位。位运算写法照搬 1.20.1，语义详见 {@link #setIncendiary}。 */
	private static final EntityDataAccessor<Byte> ID_FIRE =
			SynchedEntityData.defineId(AutocannonProjectileMixin.class, EntityDataSerializers.BYTE);

	protected AutocannonProjectileMixin(EntityType<? extends AbstractAutocannonProjectile> type, Level level) {
		super(type, level);
	}

	@Override
	public boolean isIncendiary() {
		return (this.entityData.get(ID_FIRE) & 3) != 0;
	}

	/**
	 * 设置燃烧剂标记。
	 *
	 * <p><b>⚠ 这里修掉了 1.20.1 的一个真实缺陷（R6a，用户 2026-11-09 明确授权修正）</b>。
	 *
	 * <p>1.20.1 原文：
	 * <pre>
	 * isIncendiary()       → (data &amp; 3) != 0          // 判低两位
	 * setIncendiary(true)  → data |= 3                // 置低两位 = 3
	 * setIncendiary(false) → data &amp;= 0b11111101      // 只清 bit1！
	 * </pre>
	 * 于是 {@code set(false)} 之后 {@code data} 从 3 变成 1，而 {@code 1 &amp; 3 = 1 ≠ 0}，
	 * <b>仍然读成「是燃烧弹」—— 清除从未生效</b>。这是判读掩码（{@code &3}）与清除掩码
	 * （{@code &0xFD}）不对称造成的，原项目 1.20.1 同样如此。
	 *
	 * <p>危害是实际的：R6c-1 的 {@code IncendiaryRemovalRecipie} 会调用
	 * {@code setIncendiary(copy, false)} 来移除燃烧剂，照原样迁移则「移除燃烧剂」功能
	 * 完全无效（玩家永远拆不掉）。因此本轮修正为与判读掩码对称的 {@code &amp; ~3}。
	 *
	 * <p>行为影响仅限「清除」这一侧；{@code set(true)} 仍是 {@code |= 3}，语义不变。
	 * 详见 {@code docs/log/14-Phase2-R6a.md} 与 R6 计划书 §2.1 的修订说明。
	 */
	@Override
	public void setIncendiary(boolean incendiary) {
		if (incendiary) {
			this.entityData.set(ID_FIRE, (byte) (this.entityData.get(ID_FIRE) | 3));
		} else {
			// 与 isIncendiary() 的 & 3 对称：清掉低两位，才是真正的「清除」。
			this.entityData.set(ID_FIRE, (byte) (this.entityData.get(ID_FIRE) & ~3));
		}
	}

	@Inject(method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("HEAD"))
	public void saveDataInject(CompoundTag tag, CallbackInfo ci) {
		tag.putBoolean("Incendiary", this.isIncendiary());
	}

	@Inject(method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("HEAD"))
	public void readDataInject(CompoundTag tag, CallbackInfo ci) {
		this.setIncendiary(tag.getBoolean("Incendiary"));
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(ID_FIRE, (byte) 0);
	}

	@Override
	protected boolean onImpact(HitResult hitResult, ImpactResult impactResult, ProjectileContext projectileContext) {
		if (!this.level().isClientSide && this.isIncendiary()) {
			this.level().explode(this, this.indirectArtilleryFire(this.getDamageProperties().ignoresEntityArmor()),
					null, hitResult.getLocation().x, hitResult.getLocation().y, hitResult.getLocation().z,
					1, true, Level.ExplosionInteraction.NONE);
			this.setIncendiary(false);
			return true;
		}
		return false;
	}

	@Override
	protected boolean onHitEntity(Entity entity, ProjectileContext projectileContext) {
		super.onHitEntity(entity, projectileContext);
		if (!this.level().isClientSide && this.isIncendiary()) {
			this.level().explode(this, this.indirectArtilleryFire(this.getDamageProperties().ignoresEntityArmor()),
					null, entity.blockPosition().getX(), entity.blockPosition().getY(), entity.blockPosition().getZ(),
					1, true, Level.ExplosionInteraction.NONE);
			entity.lavaHurt();
			this.setIncendiary(false);
		}
		return this.onImpact(new EntityHitResult(entity),
				new ImpactResult(KinematicOutcome.PENETRATE, false), projectileContext);
	}
}
