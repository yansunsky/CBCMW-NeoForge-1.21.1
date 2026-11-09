package riftyboi.cbcmodernwarfare.munitions.smoke_discharger;

import java.util.function.Predicate;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import rbasamoyai.createbigcannons.CBCClientCommon;
import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.block_armor_properties.BlockArmorPropertiesHandler;
import rbasamoyai.createbigcannons.block_armor_properties.BlockArmorPropertiesProvider;
import rbasamoyai.createbigcannons.config.CBCConfigs;
import rbasamoyai.createbigcannons.config.CBCCfgMunitions.GriefState;
import rbasamoyai.createbigcannons.effects.particles.smoke.TrailSmokeParticleData;
import rbasamoyai.createbigcannons.index.CBCEntityTypes;
import rbasamoyai.createbigcannons.index.CBCSoundEvents;
import rbasamoyai.createbigcannons.multiloader.EnvExecute;
import rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile;
import rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile.ImpactResult;
import rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile.ImpactResult.KinematicOutcome;
import rbasamoyai.createbigcannons.munitions.ImpactExplosion;
import rbasamoyai.createbigcannons.munitions.ProjectileContext;
import rbasamoyai.createbigcannons.munitions.big_cannon.AbstractBigCannonProjectile.TrailType;
import rbasamoyai.createbigcannons.munitions.big_cannon.smoke_shell.SmokeEmitterEntity;
import rbasamoyai.createbigcannons.munitions.big_cannon.smoke_shell.SmokeExplosion;
import rbasamoyai.createbigcannons.munitions.config.components.BallisticPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.EntityDamagePropertiesComponent;
import rbasamoyai.createbigcannons.munitions.fuzes.FuzeItem;
import rbasamoyai.createbigcannons.network.ClientboundPlayBlockHitEffectPacket;
import rbasamoyai.createbigcannons.utils.CBCUtils;

import riftyboi.cbcmodernwarfare.index.CBCModernWarfareItem;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareMunitionPropertiesHandlers;

/**
 * 烟雾排放器炮弹：撞到东西或引信触发时炸出一团烟。
 *
 * <p><b>为什么直接继承 {@link AbstractCannonProjectile}</b>（而不是 CBC 的
 * {@code AbstractBigCannonProjectile}）：1.20.1 就是这么写的 —— 本项目需要
 * 自己控制穿甲与爆炸，所以把 CBC 的穿甲算法整段搬了过来
 * （{@link #calculateBlockPenetration}，与 CBC 的 {@code AbstractBigCannonProjectile}
 * 逐行等价，已与 1.20.1 源归一化 diff 核对，只差数值字面量写法）。
 *
 * <p><b>1.20.1 → 1.21.1</b>：
 * <ul>
 *   <li>{@code ImpactResult} 变成 {@code AbstractCannonProjectile} 的嵌套 record。</li>
 *   <li>{@code ItemStack.save/parseOptional} 都要传 {@code Provider}。</li>
 *   <li>{@code AbstractCannonProjectile#defineSynchedData} 收 {@code Builder}（本类不覆写，无影响）。</li>
 *   <li>其余（{@code SmokeExplosion} / {@code ImpactExplosion} / {@code SmokeEmitterEntity} /
 *       {@code TrailSmokeParticleData} / {@code CBCUtils}）签名均已 javap 核实未变。</li>
 * </ul>
 */
public class SmokeDischargerProjectile extends AbstractCannonProjectile {

	protected ItemStack fuze = ItemStack.EMPTY;

	public SmokeDischargerProjectile(EntityType<? extends AbstractCannonProjectile> type, Level level) {
		super(type, level);
	}

	public void setFuze(ItemStack fuze) {
		this.fuze = fuze;
	}

	@Override
	public void tick() {
		ChunkPos cpos = new ChunkPos(this.blockPosition());
		if (this.level().isClientSide || this.level().hasChunk(cpos.x, cpos.z)) {
			Vec3 oldPos = this.position();
			super.tick();

			if (!this.isInGround()) {
				// 烟迹：与 CBC 的大口径炮弹同一套配置
				TrailType trailType = CBCConfigs.server().munitions.bigCannonTrailType.get();
				if (trailType != TrailType.NONE) {
					int lifetime = trailType == TrailType.SHORT ? 100 : 280 + this.level().random.nextInt(50);
					for (int i = 0; i < 10; i++) {
						double partial = (float) i * 0.1F;
						double dx = Mth.lerp(partial, this.xOld, this.getX());
						double dy = Mth.lerp(partial, this.yOld, this.getY());
						double dz = Mth.lerp(partial, this.zOld, this.getZ());
						double sx = this.level().random.nextDouble() * 0.002 - 0.002;
						double sy = this.level().random.nextDouble() * 0.002 - 0.002;
						double sz = this.level().random.nextDouble() * 0.002 - 0.002;
						this.level().addAlwaysVisibleParticle(new TrailSmokeParticleData(lifetime), true, dx, dy, dz,
								sx, sy, sz);
					}
				}

				// 掠过音效（仅客户端）
				Vec3 newPos = this.position();
				if (this.level().isClientSide && this.localSoundCooldown == 0) {
					Vec3 displacement = newPos.subtract(oldPos);
					double dispLen = displacement.length();
					Vec3 originPos = newPos.subtract(displacement.scale(0.5));
					double radius = Math.min(200.0, dispLen * 30.0);
					EnvExecute.executeOnClient(() -> () -> CBCClientCommon.playShellFlyingSoundOnClient(this,
							CBCSoundEvents.SHELL_FLYING.getMainEvent(), player -> {
								if (!CBCConfigs.client().enableBigCannonFlybySounds.get()) {
									return false;
								}
								if (player.distanceToSqr(originPos) > radius * radius) {
									return false;
								}
								Vec3 diff = player.position().subtract(originPos);
								return displacement.normalize().dot(diff.normalize()) >= 0.0;
							}, radius));
				}
			}
		}

		if (this.canDetonate(fz -> fz.onProjectileTick(this.fuze, this))) {
			this.detonate(this.position());
			this.removeNextTick = true;
		}
	}

	/** 炸出一团烟：烟雾爆炸 + 一个烟雾发射器实体（按属性里的 scale/duration）。 */
	protected void detonate(Position position) {
		SmokeDischargerProjectileProperties properties = this.getAllProperties();
		SmokeExplosion explosion = new SmokeExplosion(this.level(), null, position.x(), position.y(), position.z(),
				2.0F, 2.0F, BlockInteraction.KEEP);
		CreateBigCannons.handleCustomExplosion(this.level(), explosion);

		SmokeEmitterEntity smoke = CBCEntityTypes.SMOKE_EMITTER.create(this.level());
		smoke.setPos(new Vec3(position.x(), position.y(), position.z()));
		smoke.setDuration(properties.smokeDuration());
		smoke.setSize(properties.smokeScale());
		this.level().addFreshEntity(smoke);
	}

	/**
	 * 穿甲计算。
	 *
	 * <p>与 CBC 的 {@code AbstractBigCannonProjectile#calculateBlockPenetration} 等价
	 * （1.20.1 原项目就是这么整段搬的）。这段逻辑负责：动量/韧性/硬度判定、
	 * 跳弹概率、穿透后掉质量、未穿透则碎甲并触发 {@link ImpactExplosion}。
	 */
	@Override
	protected ImpactResult calculateBlockPenetration(ProjectileContext projectileContext, BlockState state,
			BlockHitResult blockHitResult) {
		BlockPos pos = blockHitResult.getBlockPos();
		Vec3 hitLoc = blockHitResult.getLocation();
		BallisticPropertiesComponent ballistics = this.getBallisticProperties();
		BlockArmorPropertiesProvider blockArmor = BlockArmorPropertiesHandler.getProperties(state);
		boolean unbreakable = projectileContext.griefState() == GriefState.NO_DAMAGE
				|| state.getDestroySpeed(this.level(), pos) == -1.0F;
		Vec3 accel = this.getForces(this.position(), this.getDeltaMovement());
		Vec3 curVel = this.getDeltaMovement().add(accel);
		Vec3 normal = CBCUtils.getSurfaceNormalVector(this.level(), blockHitResult);
		double incidence = Math.max(0.0, curVel.normalize().dot(normal.reverse()));
		double velMag = curVel.length();
		double mass = this.getProjectileMass();
		double bonusMomentum = 1.0 + Math.max(0.0,
				(velMag - CBCConfigs.server().munitions.minVelocityForPenetrationBonus.getF())
						* CBCConfigs.server().munitions.penetrationBonusScale.getF());
		double incidentVel = velMag * incidence;
		double momentum = mass * incidentVel * bonusMomentum;
		double toughness = blockArmor.toughness(this.level(), state, pos, true);
		double toughnessPenalty = toughness - momentum;
		double hardnessPenalty = blockArmor.hardness(this.level(), state, pos, true) - ballistics.penetration();
		double bounceBonus = Math.max(1.0 - hardnessPenalty, 0.0);
		double projectileDeflection = ballistics.deflection();
		double baseChance = CBCConfigs.server().munitions.baseProjectileBounceChance.getF();
		double bounceChance = projectileDeflection < 1.0E-2 || incidence > projectileDeflection
				? 0.0
				: Math.max(baseChance, 1.0 - incidence / projectileDeflection) * bounceBonus;
		boolean surfaceImpact = this.canHitSurface();
		boolean canBounce = CBCConfigs.server().munitions.projectilesCanBounce.get();
		boolean blockBroken = toughnessPenalty < 0.01 && !unbreakable;

		KinematicOutcome outcome;
		if (surfaceImpact && canBounce && this.level().getRandom().nextDouble() < bounceChance) {
			outcome = KinematicOutcome.BOUNCE;
		} else if (blockBroken) {
			outcome = KinematicOutcome.PENETRATE;
		} else {
			outcome = KinematicOutcome.STOP;
		}

		boolean shatter = surfaceImpact && outcome != KinematicOutcome.BOUNCE
				&& hardnessPenalty > ballistics.toughness();
		float durabilityPenalty = ((float) Math.max(0.0, hardnessPenalty) + 1.0F) * (float) toughness
				/ (float) incidentVel;

		state.onProjectileHit(this.level(), state, blockHitResult, this);

		if (!this.level().isClientSide) {
			boolean bounced = outcome == KinematicOutcome.BOUNCE;
			Vec3 effectNormal;
			if (bounced) {
				double elasticity = 1.7F;
				effectNormal = curVel.subtract(normal.scale(normal.dot(curVel) * elasticity));
			} else {
				effectNormal = curVel.reverse();
			}
			for (BlockState contained : blockArmor.containedBlockStates(this.level(), state, pos.immutable(), true)) {
				projectileContext.addPlayedEffect(new ClientboundPlayBlockHitEffectPacket(contained, this.getType(),
						bounced, true, hitLoc.x, hitLoc.y, hitLoc.z, (float) effectNormal.x, (float) effectNormal.y,
						(float) effectNormal.z));
			}
		}

		if (blockBroken) {
			this.setProjectileMass(incidentVel < 1.0E-4
					? 0.0F
					: Math.max(this.getProjectileMass() - durabilityPenalty, 0.0F));
			this.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
			if (surfaceImpact) {
				float f = (float) toughness / (float) momentum;
				float overPenetrationPower = f < 0.15F ? 2.0F - 2.0F * f : 0.0F;
				if (overPenetrationPower > 0.0F && outcome == KinematicOutcome.PENETRATE) {
					projectileContext.queueExplosion(pos, overPenetrationPower);
				}
			}
		} else {
			if (outcome == KinematicOutcome.STOP) {
				this.setProjectileMass(0.0F);
			} else {
				this.setProjectileMass(incidentVel < 1.0E-4
						? 0.0F
						: Math.max(this.getProjectileMass() - durabilityPenalty / 2.0F, 0.0F));
			}

			Vec3 spallLoc = hitLoc.add(curVel.normalize().scale(2.0));
			if (!this.level().isClientSide) {
				ImpactExplosion explosion = new ImpactExplosion(this.level(), this,
						this.indirectArtilleryFire(false), spallLoc.x, spallLoc.y, spallLoc.z, 2.0F, 2.0F,
						BlockInteraction.KEEP);
				CreateBigCannons.handleCustomExplosion(this.level(), explosion);
			}

			// 注意：1.21.1 的 BlockState#getSoundType() 已 @Deprecated，
			// 但这里是无参版本（R2 记忆里那条是带参的 IBlockExtension 版本）。
			// 为免警告，改用 state.getBlock().getSoundType(state, level, pos, null)。
			SoundType sound = state.getBlock().getSoundType(state, this.level(), pos, this);
			if (!this.level().isClientSide) {
				this.level().playSound(null, spallLoc.x, spallLoc.y, spallLoc.z, sound.getBreakSound(),
						SoundSource.BLOCKS, sound.getVolume(), sound.getPitch());
			}
		}

		shatter |= this.onImpact(blockHitResult, new ImpactResult(outcome, shatter), projectileContext);
		return new ImpactResult(outcome, shatter);
	}

	@Override
	protected void onTickRotate() {
		this.yRotO = this.getYRot();
		this.xRotO = this.getXRot();
		if (!this.isInGround()) {
			Vec3 vel = this.getDeltaMovement();
			if (vel.lengthSqr() > 0.005) {
				this.setYRot((float) (Mth.atan2(vel.x, vel.z) * 180.0F / (float) Math.PI));
				this.setXRot((float) (Mth.atan2(vel.y, vel.horizontalDistance()) * 180.0F / (float) Math.PI));
			}
			this.setYRot(lerpRotation(this.yRotO, this.getYRot()));
			this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
		}
	}

	public ItemStack getItem() {
		return CBCModernWarfareItem.SMOKE_DISCHARGER_ROUND.asStack();
	}

	protected final boolean canDetonate(Predicate<FuzeItem> cons) {
		return !this.level().isClientSide
				&& this.level().hasChunkAt(this.blockPosition())
				&& this.fuze.getItem() instanceof FuzeItem fuzeItem
				&& cons.test(fuzeItem);
	}

	@Override
	protected boolean onImpact(HitResult hitResult, ImpactResult impactResult, ProjectileContext projectileContext) {
		super.onImpact(hitResult, impactResult, projectileContext);
		if (this.canDetonate(fz -> fz.onProjectileImpact(this.fuze, this, hitResult, impactResult, false))) {
			this.detonate(hitResult.getLocation());
			return true;
		}
		return false;
	}

	@Override
	protected boolean onClip(ProjectileContext ctx, Vec3 start, Vec3 end) {
		if (super.onClip(ctx, start, end)) {
			return true;
		}
		if (this.canDetonate(fz -> fz.onProjectileClip(this.fuze, this, start, end, ctx, false))) {
			this.detonate(start);
			return true;
		}
		return false;
	}

	@Nonnull
	@Override
	public EntityDamagePropertiesComponent getDamageProperties() {
		return this.getAllProperties().damage();
	}

	@Nonnull
	@Override
	protected BallisticPropertiesComponent getBallisticProperties() {
		return this.getAllProperties().ballistics();
	}

	protected SmokeDischargerProjectileProperties getAllProperties() {
		return CBCModernWarfareMunitionPropertiesHandlers.SMOKE_GRENADE.getPropertiesOf(this);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		if (this.fuze != null && !this.fuze.isEmpty()) {
			tag.put("Fuze", this.fuze.save(this.registryAccess()));
		}
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.fuze = tag.contains("Fuze", Tag.TAG_COMPOUND)
				? ItemStack.parseOptional(this.registryAccess(), tag.getCompound("Fuze"))
				: ItemStack.EMPTY;
	}
}
