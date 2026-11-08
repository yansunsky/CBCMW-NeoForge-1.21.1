package riftyboi.cbcmodernwarfare.munitions.big_cannon.heap_shell;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.base.PartialBlockDamageManager;
import rbasamoyai.createbigcannons.block_armor_properties.BlockArmorPropertiesHandler;
import rbasamoyai.createbigcannons.index.CBCDamageTypes;
import rbasamoyai.createbigcannons.munitions.CannonDamageSource;
import rbasamoyai.createbigcannons.munitions.big_cannon.shrapnel.ShrapnelBurst;
import rbasamoyai.createbigcannons.munitions.config.components.EntityDamagePropertiesComponent;
import rbasamoyai.createbigcannons.munitions.fragment_burst.CBCProjectileBurst;
import rbasamoyai.createbigcannons.utils.CBCUtils;
import rbasamoyai.ritchiesprojectilelib.projectile_burst.ProjectileBurst;
import riftyboi.cbcmodernwarfare.CBCModernWarfare;

import java.util.Iterator;

public class HEAPBurst extends CBCProjectileBurst {

	public HEAPBurst(EntityType<? extends HEAPBurst> entityType, Level level) { super(entityType, level); }

	@Override
	public void tick() {
		super.tick();
		if (this.level().isClientSide) {
			ParticleOptions trailParticle = this.getTrailParticle();
			if (trailParticle != null) {
				for (SubProjectile subProjectile : this.subProjectiles) {
					this.level().addParticle(trailParticle, this.getX() + subProjectile.displacement()[0],
						this.getY() + subProjectile.displacement()[1], this.getZ() + subProjectile.displacement()[2], 0, 0, 0);
				}
			}
		}
	}

	protected void onSubProjectileHitEntity(EntityHitResult result, ProjectileBurst.SubProjectile subProjectile) {
		EntityDamagePropertiesComponent properties = this.getProperties().damage();
		Entity entity = result.getEntity();
		if (properties == null || properties.ignoresInvulnerability()) {
			entity.invulnerableTime = 0;
		}

		float damage = properties == null ? 0.0F : properties.entityDamage();
		entity.hurt(this.getDamageSource(), damage);
		if (properties == null || !properties.rendersInvulnerable()) {
			entity.invulnerableTime = 0;
		}

	}


	@Override
	protected void onSubProjectileHitBlock(BlockHitResult result, ProjectileBurst.SubProjectile subProjectile) {
		super.onSubProjectileHitBlock(result, subProjectile);
		BlockPos pos = result.getBlockPos();
		BlockState state = this.level().getChunk(pos).getBlockState(pos);
		if (!this.level().isClientSide && state.getDestroySpeed(this.level(), pos) != -1.0F && this.canDestroyBlock(state)) {
			Vec3 curVel = new Vec3(subProjectile.velocity()[0], subProjectile.velocity()[1], subProjectile.velocity()[2]);
			Vec3 impactDir = CBCUtils.getSurfaceNormalVector(level, result);
			double angleImpact = Math.abs(curVel.normalize().dot(impactDir));
			double curPom = (this.getProperties().ballistics().durabilityMass() * curVel.length()) * angleImpact;
			double speed = state.getDestroySpeed(level,pos);
			double toughness = BlockArmorPropertiesHandler.getProperties(state).toughness(this.level(), state, pos, true);
			double impactedToughness = toughness;
		//	impactedToughness = ((speed * speed) / 5 ) + toughness;
			BlockPos pos1 = pos.immutable();
			CreateBigCannons.BLOCK_DAMAGE.damageBlock(pos1, (int)Math.min(curPom, impactedToughness), state, this.level(), PartialBlockDamageManager::voidBlock);
		}

		Level var13 = this.level();
		if (var13 instanceof ServerLevel slevel) {
			ParticleOptions options = new BlockParticleOption(ParticleTypes.BLOCK, state);
			Iterator var7 = slevel.players().iterator();

			while(var7.hasNext()) {
				ServerPlayer player = (ServerPlayer)var7.next();
				if (player.distanceToSqr((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()) < 1024.0) {
					slevel.sendParticles(player, options, true, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 20, 0.4, 2.0, 0.4, 1.0);
				}
			}
		}

		SoundType type = state.getSoundType();
		this.level().playLocalSound((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), type.getBreakSound(), SoundSource.NEUTRAL, type.getVolume() * 2.0F, type.getPitch(), false);
	}

	protected DamageSource getDamageSource() {
		return new CannonDamageSource(CannonDamageSource.getDamageRegistry(this.level()).getHolderOrThrow(CBCDamageTypes.MOLTEN_METAL),
			this.getProperties().damage().ignoresEntityArmor());
	}

	@Nullable public ParticleOptions getTrailParticle() { return ParticleTypes.FLAME; }

	protected boolean canDestroyBlock(BlockState state) {
		return true;
	}

	public double getSubProjectileWidth() {
		return 0.8;
	}

	public double getSubProjectileHeight() {
		return 0.8;
	}


}
