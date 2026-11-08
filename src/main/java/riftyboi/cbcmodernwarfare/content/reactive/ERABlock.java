package riftyboi.cbcmodernwarfare.content.reactive;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakExplosion;

/**
 * Reactive (explosive) armour block.
 *
 * <p><b>Port note — 1.20.1 → 1.21.1.</b> The 1.20.1 class detonated whenever a projectile of one
 * of three hard-coded types hit it. Those three types all live in
 * {@code munitions/}, which is ported in R5/R6 and does not exist yet. Importing them here
 * would drag the whole munitions package into R2, which is exactly what the phase plan forbids.
 *
 * <p>The 1.20.1 behaviour is therefore split in two:
 * <ul>
 *   <li>the <i>reactive</i> behaviour (the flak detonation + block removal + break sound) is
 *       ported now, verbatim;</li>
 *   <li>the <i>trigger predicate</i> (which projectiles count) is delegated to
 *       {@link #isReactiveTrigger(Projectile)}, which currently returns {@code false}.</li>
 * </ul>
 * TODO(R5): flip {@link #isReactiveTrigger(Projectile)} to the original three-type check once
 * {@code HEAPBurst}, {@code HEAPShellProjectile} and {@code HEAPMediumcannonProjectile} exist.
 * Keeping the detonation code live now means R5 only touches one method.
 */
public class ERABlock extends Block {

    public ERABlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        // NOTE(1.21.1): onProjectileHit became `protected` on BlockBehaviour (it is public in
        // 1.20.1). Overriding with the same visibility as the superclass is required.
        super.onProjectileHit(level, state, hit, projectile);
        if (level.isClientSide || !isReactiveTrigger(projectile)) {
            return;
        }

        BlockPos pos = hit.getBlockPos();
        projectile.discard();
        level.destroyBlock(pos.immutable(), false);

        // NOTE(1.21.1): 1.20.1 passed `Level.ExplosionInteraction.NONE` and a `float` entity
        // size in one constructor. CBC 5.11.7's FlakExplosion now takes
        // (level, entity, damageSource, x, y, z, blockSize, entitySize, Explosion.BlockInteraction)
        // — a *vanilla* Explosion.BlockInteraction, two floats, and no explosionFire flag.
        // Level.ExplosionInteraction.NONE maps to Explosion.BlockInteraction.KEEP
        // (verified in Level#explode, the switch at the top of the 9-arg overload).
        FlakExplosion explosion = new FlakExplosion(level, null, null,
                pos.getX(), pos.getY(), pos.getZ(), 4.0F, 4.0F,
                net.minecraft.world.level.Explosion.BlockInteraction.KEEP);
        CreateBigCannons.handleCustomExplosion(level, explosion);

        // NOTE(1.21.1): BlockState#getSoundType() is deprecated — it cannot see the position or
        // the entity. The supported replacement is the NeoForge extension
        // IBlockExtension#getSoundType(BlockState, LevelReader, BlockPos, Entity), which Block
        // already implements. Behaviour kept identical to 1.20.1: same sound, same volume
        // multiplier, same pitch.
        SoundType type = state.getBlock().getSoundType(state, level, pos, null);
        level.playSound(null, pos, type.getBreakSound(), SoundSource.NEUTRAL,
                type.getVolume() * 0.25F, type.getPitch());
    }

    /**
     * Whether the given projectile should detonate reactive armour.
     *
     * <p>TODO(R5): restore the 1.20.1 predicate:
     * {@code projectile instanceof HEAPBurst || projectile instanceof HEAPShellProjectile
     * || projectile instanceof HEAPMediumcannonProjectile}.
     */
    protected boolean isReactiveTrigger(Projectile projectile) {
        return false;
    }
}
