package riftyboi.cbcmodernwarfare.munitions.big_cannon.heap_shell;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

import rbasamoyai.createbigcannons.block_hit_effects.BlockImpactTransformationHandler;
import rbasamoyai.createbigcannons.config.CBCConfigs;
import rbasamoyai.createbigcannons.effects.particles.explosions.ShellBlastWaveEffectParticleData;
import rbasamoyai.createbigcannons.effects.particles.explosions.ShellExplosionCloudParticleData;
import rbasamoyai.createbigcannons.index.CBCSoundEvents;
import rbasamoyai.createbigcannons.multiloader.NetworkPlatform;
import rbasamoyai.createbigcannons.network.ClientboundCBCExplodePacket;
import rbasamoyai.createbigcannons.remix.CustomExplosion;

/**
 * HEAP（破甲弹）的爆炸：金属射流 + 地形改造。
 *
 * <p><b>1.20.1 → 1.21.1</b>：{@code Level.ExplosionInteraction} 换成
 * {@code Explosion.BlockInteraction}，且 {@code CustomExplosion.Impl} 的构造多了一个
 * {@code entitySize}（float）。映射：{@code NONE → KEEP}。
 *
 * <p>本类原本属于 {@code munitions/big_cannon/heap_shell} 包（大口径炮弹那一批，R6/R7），
 * 但中型炮的 HEAP 弹头复用了它，所以提前到 R5 一起迁。
 */
public class HEAPExplosion extends CustomExplosion.Impl {

    private final Set<BlockPos> changedBlocks = new HashSet<>();
    private final boolean isPlume;
    private final boolean noEffects;

    public HEAPExplosion(Level level, @Nullable Entity source, @Nullable DamageSource damageSource,
            double toBlowX, double toBlowY, double toBlowZ, float blockPower, float entityPower, boolean fire,
            Explosion.BlockInteraction interaction, boolean noEffects) {
        super(level, source, damageSource, null, toBlowX, toBlowY, toBlowZ, blockPower, entityPower, fire, interaction);
        BlockPos pos = BlockPos.containing(this.x, this.y, this.z);
        this.isPlume = this.level.getBlockState(pos.above()).isAir() && !this.level.getBlockState(pos.below()).isAir();
        this.noEffects = noEffects;
    }

    /** 与 CBC 的 ShellExplosion 同形的便捷构造（blockPower + entityPower）。 */
    public HEAPExplosion(Level level, @Nullable Entity source, @Nullable DamageSource damageSource,
            double toBlowX, double toBlowY, double toBlowZ, float blockPower, float entityPower, boolean fire,
            Explosion.BlockInteraction interaction) {
        this(level, source, damageSource, toBlowX, toBlowY, toBlowZ, blockPower, entityPower, fire, interaction, false);
    }

    public HEAPExplosion(Level level, ClientboundCBCExplodePacket packet) {
        super(level, packet);
        BlockPos pos = BlockPos.containing(this.x, this.y, this.z);
        this.isPlume = this.level.getBlockState(pos.above()).isAir() && !this.level.getBlockState(pos.below()).isAir();
        this.noEffects = packet.explosionType() == ClientboundCBCExplodePacket.ExplosionType.SHELL_NO_EFFECTS;
    }

    @Override
    protected void spawnParticles() {
        if (this.noEffects) {
            return;
        }
        ShellBlastWaveEffectParticleData blastWave = new ShellBlastWaveEffectParticleData(this.blockSize * 12,
                BuiltInRegistries.SOUND_EVENT.wrapAsHolder(CBCSoundEvents.SHELL_EXPLOSION.getMainEvent()),
                SoundSource.BLOCKS, Math.max(this.blockSize * 2, 16),
                0.8f + this.level.random.nextFloat() * 0.4f, 2f, this.blockSize / 4);
        ShellExplosionCloudParticleData explosionCloud = new ShellExplosionCloudParticleData(this.blockSize, this.isPlume);
        this.level.addParticle(blastWave, true, this.x, this.y, this.z, 0, 0, 0);
        this.level.addParticle(explosionCloud, true, this.x, this.y, this.z, 0, 0, 0);
    }

    @Override
    public void editBlock(Level level, BlockPos pos, BlockState blockState, FluidState fluidState, float power) {
        if (this.noEffects || !CBCConfigs.server().munitions.projectilesChangeSurroundings.get()
                || this.changedBlocks.contains(pos)) {
            return;
        }
        BlockState transformed = BlockImpactTransformationHandler.transformBlock(blockState);
        level.setBlock(pos, transformed, 11);
        this.changedBlocks.add(pos);
    }

    @Override
    public void sendExplosionToClient(ServerPlayer player) {
        double distSqr = player.distanceToSqr(this.x, this.y, this.z);
        if (distSqr < 263000d) {
            Vec3 knockback = this.getHitPlayers().getOrDefault(player, Vec3.ZERO);
            ClientboundCBCExplodePacket.ExplosionType type = this.noEffects
                    ? ClientboundCBCExplodePacket.ExplosionType.SHELL_NO_EFFECTS
                    : ClientboundCBCExplodePacket.ExplosionType.SHELL;
            // 1.21.1：blockPower 与 entityPower 拆成两个 float（1.20.1 只有一个 size）
            NetworkPlatform.sendToClientPlayer(new ClientboundCBCExplodePacket(this.x, this.y, this.z,
                    this.blockSize, this.entitySize, this.getToBlow(),
                    (float) knockback.x, (float) knockback.y, (float) knockback.z, type), player);
        }
    }
}
