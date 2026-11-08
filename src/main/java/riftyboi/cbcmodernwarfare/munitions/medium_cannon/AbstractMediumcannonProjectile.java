package riftyboi.cbcmodernwarfare.munitions.medium_cannon;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import com.mojang.math.Constants;

import rbasamoyai.createbigcannons.CBCClientCommon;
import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.block_armor_properties.BlockArmorPropertiesHandler;
import rbasamoyai.createbigcannons.block_armor_properties.BlockArmorPropertiesProvider;
import rbasamoyai.createbigcannons.config.CBCCfgMunitions;
import rbasamoyai.createbigcannons.config.CBCConfigs;
import rbasamoyai.createbigcannons.effects.particles.smoke.TrailSmokeParticleData;
import rbasamoyai.createbigcannons.index.CBCSoundEvents;
import rbasamoyai.createbigcannons.multiloader.EnvExecute;
import rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile;
import rbasamoyai.createbigcannons.munitions.ImpactExplosion;
import rbasamoyai.createbigcannons.munitions.ProjectileContext;
import rbasamoyai.createbigcannons.munitions.big_cannon.AbstractBigCannonProjectile;
import rbasamoyai.createbigcannons.munitions.config.components.BallisticPropertiesComponent;
import rbasamoyai.createbigcannons.network.ClientboundPlayBlockHitEffectPacket;
import rbasamoyai.createbigcannons.utils.CBCUtils;

/**
 * 中型炮炮弹的公共父类：穿甲、跳弹、破碎与拖尾。
 *
 * <p><b>1.20.1 → 1.21.1 的四处改动</b>（全部经 javap 与参考实现双向核对）：
 * <ol>
 *   <li>{@code ImpactExplosion} 的构造参数变了：1.20.1 是
 *       {@code (Level, Entity, DamageSource, x, y, z, float, Level.ExplosionInteraction)}，
 *       1.21.1 是 {@code (..., float, float, Explosion.BlockInteraction)}。
 *       {@code Level.ExplosionInteraction.NONE} 映射到 {@code Explosion.BlockInteraction.KEEP}。</li>
 *   <li>{@code SoundType} 的获取：{@code state.getSoundType()} 已 {@code @Deprecated}，
 *       改用 {@code state.getBlock().getSoundType(state, level, pos, null)}（R2 已记录，这里首次真正用上）。</li>
 *   <li>{@code this.level()} 仍可用（{@code Projectile} 的方法），未变。</li>
 *   <li>{@code abstract getBallisticProperties()} 与 {@code ID_FLAGS} 都在父类，
 *       签名未变，直接继承使用。</li>
 * </ol>
 *
 * <p><b>注意</b>：本类的 {@code calculateBlockPenetration} 是纯服务端逻辑，
 * 但 {@code tick()} 里有客户端分支（拖尾粒子与飞行音效）。
 * 客户端调用点是 {@code CBCClientCommon}，它本身是客户端类 —— 保持 1.20.1 的
 * {@code EnvExecute.executeOnClient} 隔离方式，不要改成直接引用。
 */
public abstract class AbstractMediumcannonProjectile extends AbstractCannonProjectile {

    protected AbstractMediumcannonProjectile(EntityType<? extends AbstractMediumcannonProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        ChunkPos cpos = new ChunkPos(this.blockPosition());
        if (this.level().isClientSide || this.level().hasChunk(cpos.x, cpos.z)) {
            Vec3 oldPos = this.position();
            super.tick();
            if (!this.isInGround()) {
                AbstractBigCannonProjectile.TrailType trailType = CBCConfigs.server().munitions.bigCannonTrailType.get();
                if (trailType != AbstractBigCannonProjectile.TrailType.NONE) {
                    int lifetime = trailType == AbstractBigCannonProjectile.TrailType.SHORT
                            ? 100
                            : 280 + this.level().random.nextInt(50);
                    for (int i = 0; i < 10; ++i) {
                        double partial = i * 0.1f;
                        double dx = Mth.lerp(partial, this.xOld, this.getX());
                        double dy = Mth.lerp(partial, this.yOld, this.getY());
                        double dz = Mth.lerp(partial, this.zOld, this.getZ());
                        double sx = this.level().random.nextDouble() * 0.002d - 0.002d;
                        double sy = this.level().random.nextDouble() * 0.002d - 0.002d;
                        double sz = this.level().random.nextDouble() * 0.002d - 0.002d;
                        this.level().addAlwaysVisibleParticle(new TrailSmokeParticleData(lifetime), true,
                                dx, dy, dz, sx, sy, sz);
                    }
                }
                Vec3 newPos = this.position();
                if (this.level().isClientSide && this.localSoundCooldown == 0) {
                    Vec3 displacement = newPos.subtract(oldPos);
                    double dispLen = displacement.length();
                    Vec3 originPos = newPos.subtract(displacement.scale(0.5));
                    double radius = Math.min(200, dispLen * 30);
                    EnvExecute.executeOnClient(() -> () -> CBCClientCommon.playShellFlyingSoundOnClient(this,
                            CBCSoundEvents.SHELL_FLYING.getMainEvent(), player -> {
                                if (!CBCConfigs.client().enableBigCannonFlybySounds.get()) {
                                    return false;
                                }
                                if (player.distanceToSqr(originPos) > radius * radius) {
                                    return false;
                                }
                                Vec3 diff = player.position().subtract(originPos);
                                return displacement.normalize().dot(diff.normalize()) >= 0;
                            }, radius));
                }
            }
        }
    }

    @Override
    protected ImpactResult calculateBlockPenetration(ProjectileContext projectileContext, BlockState state,
            BlockHitResult blockHitResult) {
        BlockPos pos = blockHitResult.getBlockPos();
        Vec3 hitLoc = blockHitResult.getLocation();

        BallisticPropertiesComponent ballistics = this.getBallisticProperties();
        BlockArmorPropertiesProvider blockArmor = BlockArmorPropertiesHandler.getProperties(state);
        boolean unbreakable = projectileContext.griefState() == CBCCfgMunitions.GriefState.NO_DAMAGE
                || state.getDestroySpeed(this.level(), pos) == -1;

        Vec3 accel = this.getForces(this.position(), this.getDeltaMovement());
        Vec3 curVel = this.getDeltaMovement().add(accel);

        Vec3 normal = CBCUtils.getSurfaceNormalVector(this.level(), blockHitResult);
        double incidence = Math.max(0, curVel.normalize().dot(normal.reverse()));
        double velMag = curVel.length();
        double mass = this.getProjectileMass();
        double bonusMomentum = 1 + Math.max(0, (velMag - CBCConfigs.server().munitions.minVelocityForPenetrationBonus.getF())
                * CBCConfigs.server().munitions.penetrationBonusScale.getF());
        double incidentVel = velMag * incidence;
        double momentum = mass * incidentVel * bonusMomentum;

        double toughness = blockArmor.toughness(this.level(), state, pos, true);
        double toughnessPenalty = toughness - momentum;
        double hardnessPenalty = blockArmor.hardness(this.level(), state, pos, true) - ballistics.penetration();
        double bounceBonus = Math.max(1 - hardnessPenalty, 0);

        double projectileDeflection = ballistics.deflection();
        double baseChance = CBCConfigs.server().munitions.baseProjectileBounceChance.getF();
        double bounceChance = projectileDeflection < 1e-2d || incidence > projectileDeflection
                ? 0
                : Math.max(baseChance, 1 - incidence / projectileDeflection) * bounceBonus;

        boolean surfaceImpact = this.canHitSurface();
        boolean canBounce = CBCConfigs.server().munitions.projectilesCanBounce.get();
        boolean blockBroken = toughnessPenalty < 1e-2d && !unbreakable;
        ImpactResult.KinematicOutcome outcome;
        if (surfaceImpact && canBounce && this.level().getRandom().nextDouble() < bounceChance) {
            outcome = ImpactResult.KinematicOutcome.BOUNCE;
        } else if (blockBroken) {
            outcome = ImpactResult.KinematicOutcome.PENETRATE;
        } else {
            outcome = ImpactResult.KinematicOutcome.STOP;
        }
        boolean shatter = surfaceImpact && outcome != ImpactResult.KinematicOutcome.BOUNCE
                && hardnessPenalty > ballistics.toughness();
        float durabilityPenalty = ((float) Math.max(0, hardnessPenalty) + 1) * (float) toughness / (float) incidentVel;

        // 1.21.1: onProjectileHit 从 public 变 protected（定义在 BlockBehaviour），
        // 这里是从子类上下文调用，不受影响。
        state.onProjectileHit(this.level(), state, blockHitResult, this);
        if (!this.level().isClientSide) {
            boolean bounced = outcome == ImpactResult.KinematicOutcome.BOUNCE;
            Vec3 effectNormal;
            if (bounced) {
                double elasticity = 1.7f;
                effectNormal = curVel.subtract(normal.scale(normal.dot(curVel) * elasticity));
            } else {
                effectNormal = curVel.reverse();
            }
            for (BlockState state1 : blockArmor.containedBlockStates(this.level(), state, pos.immutable(), true)) {
                projectileContext.addPlayedEffect(new ClientboundPlayBlockHitEffectPacket(state1, this.getType(), bounced, true,
                        hitLoc.x, hitLoc.y, hitLoc.z,
                        (float) effectNormal.x, (float) effectNormal.y, (float) effectNormal.z));
            }
        }
        if (blockBroken) {
            this.setProjectileMass(incidentVel < 1e-4d ? 0 : Math.max(this.getProjectileMass() - durabilityPenalty, 0));
            this.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            if (surfaceImpact) {
                float f = (float) toughness / (float) momentum;
                float overPenetrationPower = f < 0.15f ? 2 - 2 * f : 0;
                if (overPenetrationPower > 0 && outcome == ImpactResult.KinematicOutcome.PENETRATE) {
                    projectileContext.queueExplosion(pos, overPenetrationPower);
                }
            }
        } else {
            if (outcome == ImpactResult.KinematicOutcome.STOP) {
                this.setProjectileMass(0);
            } else {
                this.setProjectileMass(incidentVel < 1e-4d ? 0 : Math.max(this.getProjectileMass() - durabilityPenalty / 2f, 0));
            }
            Vec3 spallLoc = hitLoc.add(curVel.normalize().scale(2));
            if (!this.level().isClientSide) {
                // 1.21.1: ImpactExplosion 的两个 float + Explosion.BlockInteraction
                ImpactExplosion explosion = new ImpactExplosion(this.level(), this, this.indirectArtilleryFire(false),
                        spallLoc.x, spallLoc.y, spallLoc.z, 2, 2, Explosion.BlockInteraction.KEEP);
                CreateBigCannons.handleCustomExplosion(this.level(), explosion);
            }
            // 1.21.1: BlockState#getSoundType() 已 @Deprecated，改用 IBlockExtension 的版本。
            SoundType sound = state.getBlock().getSoundType(state, this.level(), pos, null);
            if (!this.level().isClientSide) {
                this.level().playSound(null, spallLoc.x, spallLoc.y, spallLoc.z,
                        sound.getBreakSound(), SoundSource.BLOCKS, sound.getVolume(), sound.getPitch());
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
            if (vel.lengthSqr() > 0.005d) {
                this.setYRot((float) (Mth.atan2(vel.x, vel.z) * (double) Constants.RAD_TO_DEG));
                this.setXRot((float) (Mth.atan2(vel.y, vel.horizontalDistance()) * (double) Constants.RAD_TO_DEG));
            }
            this.setYRot(lerpRotation(this.yRotO, this.getYRot()));
            this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
        }
    }

    /** 击发它的那一发弹药（用于退壳与掉落）。 */
    public abstract ItemStack getItem();

    public void setTracer(boolean tracer) {
        if (tracer) {
            this.entityData.set(ID_FLAGS, (byte) (this.entityData.get(ID_FLAGS) | 2));
        } else {
            this.entityData.set(ID_FLAGS, (byte) (this.entityData.get(ID_FLAGS) & 0b11111101));
        }
    }

    protected boolean onImpact(HitResult hitResult, ImpactResult impactResult, ProjectileContext projectileContext) {
        return false;
    }

    public boolean isTracer() {
        return (this.entityData.get(ID_FLAGS) & 2) != 0;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Tracer", this.isTracer());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setTracer(tag.getBoolean("Tracer"));
    }
}
