package riftyboi.cbcmodernwarfare.content.reactive;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakExplosion;

/** Reactive armour, stair form. Same port notes as {@link ERABlock}. */
public class ERAStairBlock extends StairBlock {

    public ERAStairBlock(BlockState baseState, Properties properties) {
        super(baseState, properties);
    }

    @Override
    protected void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        super.onProjectileHit(level, state, hit, projectile);
        if (level.isClientSide || !isReactiveTrigger(projectile)) {
            return;
        }

        BlockPos pos = hit.getBlockPos();
        projectile.discard();
        level.destroyBlock(pos.immutable(), false);

        FlakExplosion explosion = new FlakExplosion(level, null, null,
                pos.getX(), pos.getY(), pos.getZ(), 4.0F, 4.0F,
                net.minecraft.world.level.Explosion.BlockInteraction.KEEP);
        CreateBigCannons.handleCustomExplosion(level, explosion);

        // See ERABlock for why the deprecated getSoundType() was replaced.
        SoundType type = state.getBlock().getSoundType(state, level, pos, null);
        level.playSound(null, pos, type.getBreakSound(), SoundSource.NEUTRAL,
                type.getVolume() * 0.25F, type.getPitch());
    }

    /** TODO(R5): restore the 1.20.1 three-type predicate. See {@link ERABlock}. */
    protected boolean isReactiveTrigger(Projectile projectile) {
        return false;
    }
}
