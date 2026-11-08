package riftyboi.cbcmodernwarfare.cannon_control.contraption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.StructureTransform;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.phys.Vec3;

import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.cannon_control.ControlPitchContraption;
import rbasamoyai.createbigcannons.cannon_control.contraption.AbstractMountedCannonContraption;
import rbasamoyai.createbigcannons.cannon_control.contraption.ItemCannon;
import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;
import rbasamoyai.createbigcannons.cannons.ItemCannonBehavior;
import rbasamoyai.createbigcannons.config.CBCConfigs;
import rbasamoyai.createbigcannons.effects.particles.explosions.CannonBlastWaveEffectParticleData;
import rbasamoyai.createbigcannons.effects.particles.plumes.BigCannonPlumeParticleData;
import rbasamoyai.createbigcannons.index.CBCEntityTypes;
import rbasamoyai.createbigcannons.multiloader.NetworkPlatform;
import rbasamoyai.createbigcannons.network.ClientboundAnimateCannonContraptionPacket;

import riftyboi.cbcmodernwarfare.cannon_control.cannon_types.CBCMWCannonContraptionTypes;
import riftyboi.cbcmodernwarfare.cannons.medium_cannon.IMediumcannonBlockEntity;
import riftyboi.cbcmodernwarfare.cannons.medium_cannon.MediumcannonBarrelBlock;
import riftyboi.cbcmodernwarfare.cannons.medium_cannon.MediumcannonBlock;
import riftyboi.cbcmodernwarfare.cannons.medium_cannon.MovesWithMediumcannonRecoilBarrel;
import riftyboi.cbcmodernwarfare.cannons.medium_cannon.breech.MediumcannonBreechBlock;
import riftyboi.cbcmodernwarfare.cannons.medium_cannon.breech.MediumcannonBreechBlockEntity;
import riftyboi.cbcmodernwarfare.cannons.medium_cannon.material.MediumcannonMaterial;
import riftyboi.cbcmodernwarfare.cannons.medium_cannon.material.MediumcannonMaterialProperties;
import riftyboi.cbcmodernwarfare.cannons.medium_cannon.recoil_barrel.MediumcannonRecoilBarrelBlock;
import riftyboi.cbcmodernwarfare.cannons.medium_cannon.recoil_barrel.MediumcannonRecoilBarrelBlockEntity;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareContraptionTypes;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareMediumcannonMaterials;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareSoundEvents;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.AbstractMediumcannonProjectile;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonAmmoItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.config.MediumcannonProjectilePropertiesComponent;

/**
 * 中型炮的装配体（contraption）：把炮管、炮闩、后坐炮管当作一个整体移动与射击。
 *
 * <p><b>1.20.1 → 1.21.1 的改动</b>：
 * <ul>
 *   <li>{@code writeNBT}/{@code readNBT} 增加 {@link HolderLookup.Provider} 参数
 *       （{@code Contraption} 的新签名）。</li>
 *   <li>{@code NbtUtils.readBlockPos(CompoundTag)} → {@code readBlockPos(tag, key)}（返回 Optional）。</li>
 *   <li>{@code new ResourceLocation(String)} → {@code ResourceLocation.parse(String)}。</li>
 *   <li>{@code StructureBlockInfo} 是 record，取字段写 {@code info.state()} / {@code info.nbt()}。</li>
 *   <li>{@code level.explode(...)} 的最后一个参数在 1.21 仍是 {@code Level.ExplosionInteraction}
 *       （只有 {@code ImpactExplosion} 那个自定义爆炸换成了 {@code Explosion.BlockInteraction}）。</li>
 *   <li>{@code getType()} 现在返回注册表里的 {@code ContraptionType}，从
 *       {@code CBCModernWarfareContraptionTypes.MOUNTED_MEDIUMCANNON.get()} 取。</li>
 * </ul>
 *
 * <p><b>抽象方法实现清单</b>（{@code AbstractMountedCannonContraption} 的 abstract）：
 * {@code onRedstoneUpdate} / {@code fireShot} / {@code getWeightForStress} /
 * {@code getInteractionVec} / {@code getCannonType}；另加 {@code Contraption} 的
 * {@code assemble} / {@code getType}。
 */
public class MountedMediumcannonContraption extends AbstractMountedCannonContraption implements ItemCannon {

    private MediumcannonMaterial cannonMaterial;
    public boolean hasFired = false;
    private final Set<BlockPos> recoilBarrelPositions = new LinkedHashSet<>();

    @Override
    public boolean assemble(Level level, BlockPos pos) throws AssemblyException {
        if (!this.collectCannonBlocks(level, pos)) {
            return false;
        }
        this.bounds = this.createBoundsFromExtensionLengths();
        return !this.blocks.isEmpty();
    }

    /**
     * 从炮闩一侧出发，向两端收集同材质的连续炮管。
     *
     * <p>1.20.1 逐字搬入，改动点见类注释。注意 {@code structureInfo.pos()} 是 record 访问器。
     */
    private boolean collectCannonBlocks(Level level, BlockPos pos) throws AssemblyException {
        BlockState startState = level.getBlockState(pos);

        if (!(startState.getBlock() instanceof MediumcannonBlock startCannon)) {
            return false;
        }
        if (!startCannon.isComplete(startState)) {
            throw hasIncompleteCannonBlocks(pos);
        }

        MediumcannonMaterial material = startCannon.getMediumcannonMaterial();
        boolean isStartBreech = startCannon.isBreechMechanism(startState);

        List<StructureBlockInfo> cannonBlocks = new ArrayList<>();
        cannonBlocks.add(new StructureBlockInfo(pos, startState, this.getBlockEntityNBT(level, pos)));

        int cannonLength = 1;

        Direction cannonFacing = startCannon.getFacing(startState);

        Direction positive = Direction.get(Direction.AxisDirection.POSITIVE, cannonFacing.getAxis());
        Direction negative = positive.getOpposite();

        BlockPos start = pos;
        BlockState nextState = level.getBlockState(pos.relative(positive));
        boolean positiveBreech = false;

        while (nextState.getBlock() instanceof MediumcannonBlock cBlock
                && this.isConnectedToCannon(level, nextState, start.relative(positive), positive, material)) {
            start = start.relative(positive);
            if (!cBlock.isComplete(nextState)) {
                throw hasIncompleteCannonBlocks(start);
            }
            cannonBlocks.add(new StructureBlockInfo(start, nextState, this.getBlockEntityNBT(level, start)));
            this.frontExtensionLength++;
            cannonLength++;
            positiveBreech = cBlock.isBreechMechanism(nextState);
            if (positiveBreech && isStartBreech) {
                throw invalidCannon();
            }
            if (positiveBreech && cBlock.getFacing(nextState) != negative) {
                throw incorrectBreechDirection(start);
            }
            nextState = level.getBlockState(start.relative(positive));
            if (cannonLength > getMaxCannonLength()) {
                throw cannonTooLarge();
            }
            if (positiveBreech) {
                break;
            }
        }
        BlockPos positiveEndPos = positiveBreech ? start : start.relative(negative);

        start = pos;
        nextState = level.getBlockState(pos.relative(negative));

        boolean negativeBreech = false;
        while (nextState.getBlock() instanceof MediumcannonBlock cBlock
                && this.isConnectedToCannon(level, nextState, start.relative(negative), negative, material)) {
            start = start.relative(negative);
            if (!cBlock.isComplete(nextState)) {
                throw hasIncompleteCannonBlocks(start);
            }
            cannonBlocks.add(new StructureBlockInfo(start, nextState, this.getBlockEntityNBT(level, start)));
            this.backExtensionLength++;
            cannonLength++;
            negativeBreech = cBlock.isBreechMechanism(nextState);
            if (negativeBreech && isStartBreech) {
                throw invalidCannon();
            }
            if (negativeBreech && cBlock.getFacing(nextState) != positive) {
                throw incorrectBreechDirection(start);
            }
            nextState = level.getBlockState(start.relative(negative));
            if (cannonLength > getMaxCannonLength()) {
                throw cannonTooLarge();
            }
            if (negativeBreech) {
                break;
            }
        }
        BlockPos negativeEndPos = negativeBreech ? start : start.relative(positive);

        if (cannonLength < 2 || positiveBreech && negativeBreech) {
            throw invalidCannon();
        }

        this.startPos = !positiveBreech && !negativeBreech ? pos : negativeBreech ? negativeEndPos : positiveEndPos;
        BlockState breechState = level.getBlockState(this.startPos);
        if (!(breechState.getBlock() instanceof MediumcannonBreechBlock)) {
            throw invalidCannon();
        }
        this.initialOrientation = breechState.getValue(BlockStateProperties.FACING);

        this.anchor = pos;

        this.startPos = this.startPos.subtract(pos);

        for (StructureBlockInfo blockInfo : cannonBlocks) {
            BlockPos localPos = blockInfo.pos().subtract(pos);
            StructureBlockInfo localBlockInfo = new StructureBlockInfo(localPos, blockInfo.state(), blockInfo.nbt());
            this.blocks.put(localPos, localBlockInfo);

            if (blockInfo.nbt() == null) {
                continue;
            }
            BlockEntity be = BlockEntity.loadStatic(localPos, blockInfo.state(), blockInfo.nbt(), level.registryAccess());
            this.presentBlockEntities.put(localPos, be);
            if (blockInfo.state().getBlock() instanceof MediumcannonRecoilBarrelBlock) {
                this.recoilBarrelPositions.add(localPos);
            }
        }

        StructureBlockInfo startInfo = this.blocks.get(this.startPos);
        if (startInfo == null || !(startInfo.state().getBlock() instanceof MediumcannonBreechBlock)) {
            throw noAutocannonBreech();
        }

        // 后坐炮管在主弹簧前方时，把它自己和它后面的部件登记为「随后坐移动」。
        StructureBlockInfo possibleSpring = this.blocks.get(this.startPos.relative(this.initialOrientation));
        if (possibleSpring != null
                && possibleSpring.state().getBlock() instanceof MediumcannonRecoilBarrelBlock springBlock
                && springBlock.getFacing(possibleSpring.state()) == this.initialOrientation) {
            BlockPos mainRecoilSpringPos = this.startPos.relative(this.initialOrientation);
            if (this.presentBlockEntities.get(mainRecoilSpringPos) instanceof MediumcannonRecoilBarrelBlockEntity springBE) {
                boolean disconnected = false;
                for (int i = 1; i < cannonLength; ++i) {
                    BlockPos pos1 = this.startPos.relative(this.initialOrientation, i);
                    StructureBlockInfo blockInfo = this.blocks.get(pos1);
                    if (blockInfo == null || !(blockInfo.state().getBlock() instanceof MovesWithMediumcannonRecoilBarrel springed)) {
                        continue;
                    }
                    if (springed instanceof MediumcannonBarrelBlock) {
                        disconnected = true;
                    }
                    if (springed instanceof MediumcannonRecoilBarrelBlock recoilBarrelBlock && disconnected) {
                        springBE.toAnimate.put(pos1.subtract(mainRecoilSpringPos),
                                recoilBarrelBlock.getStationaryState(blockInfo.state()));
                        this.blocks.put(pos1, new StructureBlockInfo(pos1,
                                recoilBarrelBlock.getDisconnectedState(blockInfo.state()), blockInfo.nbt()));
                    } else {
                        springBE.toAnimate.put(pos1.subtract(mainRecoilSpringPos),
                                springed.getMovingState(blockInfo.state()));
                        this.blocks.put(pos1, new StructureBlockInfo(pos1,
                                springed.getStationaryState(blockInfo.state()), blockInfo.nbt()));
                    }
                }
                if (breechState.getBlock() instanceof MovesWithMediumcannonRecoilBarrel springed) {
                    StructureBlockInfo breechInfo = this.blocks.get(this.startPos);
                    springBE.toAnimate.put(this.startPos.subtract(mainRecoilSpringPos),
                            springed.getMovingState(breechInfo.state()));
                    this.blocks.put(this.startPos, new StructureBlockInfo(this.startPos,
                            springed.getStationaryState(breechInfo.state()), breechInfo.nbt()));
                }
                // 1.21.1: saveWithFullMetadata 需要 HolderLookup.Provider。
                CompoundTag newTag = springBE.saveWithFullMetadata(level.registryAccess());
                newTag.remove("x");
                newTag.remove("y");
                newTag.remove("z");
                this.blocks.put(mainRecoilSpringPos,
                        new StructureBlockInfo(mainRecoilSpringPos, possibleSpring.state(), newTag));
            }
        }

        this.cannonMaterial = material;

        return true;
    }

    private boolean isConnectedToCannon(LevelAccessor level, BlockState state, BlockPos pos, Direction connection,
            MediumcannonMaterial material) {
        MediumcannonBlock cBlock = (MediumcannonBlock) state.getBlock();
        if (cBlock.getMediumcannonMaterialInLevel(level, state, pos) != material) {
            return false;
        }
        return level.getBlockEntity(pos) instanceof IMediumcannonBlockEntity cbe
                && level.getBlockEntity(pos.relative(connection.getOpposite())) instanceof IMediumcannonBlockEntity cbe1
                && cbe.cannonBehavior().isConnectedTo(connection.getOpposite())
                && cbe1.cannonBehavior().isConnectedTo(connection);
    }

    public static AssemblyException noAutocannonBreech() {
        return new AssemblyException(
                Component.translatable("exception." + CreateBigCannons.MOD_ID + ".cannon_mount.noAutocannonBreech"));
    }

    /**
     * 放回世界时清掉「装配态」标记与运行期 NBT。
     *
     * <p>1.20.1 里这个方法的 {@code modified} 初值是 {@code true}，导致**每个方块都会被替换**。
     * 这是原代码的行为（不是笔误的效果就是全量替换），迁移期保持等价，不改。
     */
    @Override
    public void addBlocksToWorld(Level world, StructureTransform transform) {
        Map<BlockPos, StructureBlockInfo> modifiedBlocks = new HashMap<>();
        for (Map.Entry<BlockPos, StructureBlockInfo> entry : this.blocks.entrySet()) {
            StructureBlockInfo info = entry.getValue();
            BlockState newState = info.state();
            // 原代码这里是 true；保留原行为。
            boolean modified = true;

            if (newState.hasProperty(MediumcannonBarrelBlock.ASSEMBLED)
                    && newState.getValue(MediumcannonBarrelBlock.ASSEMBLED)) {
                newState = newState.setValue(MediumcannonBarrelBlock.ASSEMBLED, false);
                modified = true;
            }

            CompoundTag infoNbt = info.nbt();
            if (infoNbt != null) {
                if (infoNbt.contains("AnimateTicks")) {
                    infoNbt.remove("AnimateTicks");
                    modified = true;
                }
                if (infoNbt.contains("RenderedBlocks")) {
                    infoNbt.remove("RenderedBlocks");
                    modified = true;
                }
            }

            if (modified) {
                modifiedBlocks.put(info.pos(), new StructureBlockInfo(info.pos(), newState, infoNbt));
            }
        }
        this.blocks.putAll(modifiedBlocks);
        super.addBlocksToWorld(world, transform);
    }

    @Override
    public void fireShot(ServerLevel level, PitchOrientedContraptionEntity entity) {
        if (this.startPos == null
                || this.cannonMaterial == null
                || !(this.presentBlockEntities.get(this.startPos) instanceof MediumcannonBreechBlockEntity breech)
                || !breech.canFire()) {
            return;
        }

        ItemStack foundProjectile = breech.getInputBuffer();
        if (!(foundProjectile.getItem() instanceof MediumcannonAmmoItem round)) {
            return;
        }
        ControlPitchContraption controller = entity.getController();

        Vec3 centerPos = entity.toGlobalVector(Vec3.atCenterOf(BlockPos.ZERO), 0);
        ItemStack ejectStack = round.getSpentItem(foundProjectile);

        MediumcannonMaterialProperties properties = this.cannonMaterial.properties();
        MediumcannonProjectilePropertiesComponent roundProperties = round.getMediumcannonProperties(foundProjectile);

        boolean canFail = !CBCConfigs.server().failure.disableAllFailure.get();

        float speed = properties.baseSpeed();
        float spread = properties.baseSpread();
        boolean canSquib = roundProperties == null || roundProperties.canSquib();
        canSquib &= canFail;

        BlockPos currentPos = this.startPos.relative(this.initialOrientation);
        int barrelTravelled = 0;
        boolean squib = false;

        while (this.presentBlockEntities.get(currentPos) instanceof IMediumcannonBlockEntity mediumcannon) {
            ItemCannonBehavior behavior = mediumcannon.cannonBehavior();

            if (behavior.canLoadItem(foundProjectile)) {
                ++barrelTravelled;
                if (barrelTravelled <= properties.maxSpeedIncreases()) {
                    speed += properties.speedIncreasePerBarrel();
                }
                spread -= properties.spreadReductionPerBarrel();
                spread = Math.max(spread, 0);
                if (canSquib && barrelTravelled > properties.maxBarrelLength()) {
                    StructureBlockInfo oldInfo = this.blocks.get(currentPos);
                    if (oldInfo == null) {
                        return;
                    }
                    behavior.tryLoadingItem(foundProjectile);
                    CompoundTag tag = this.presentBlockEntities.get(currentPos).saveWithFullMetadata(level.registryAccess());
                    tag.remove("x");
                    tag.remove("y");
                    tag.remove("z");
                    StructureBlockInfo squibInfo = new StructureBlockInfo(currentPos, oldInfo.state(), tag);
                    this.blocks.put(currentPos, squibInfo);
                    Vec3 squibPos = entity.toGlobalVector(Vec3.atCenterOf(currentPos), 0);
                    level.playSound(null, squibPos.x, squibPos.y, squibPos.z,
                            oldInfo.state().getSoundType().getBreakSound(), SoundSource.BLOCKS, 10.0f, 0.0f);
                    squib = true;
                    break;
                }
                currentPos = currentPos.relative(this.initialOrientation);
            } else {
                behavior.removeItem();
                if (canFail) {
                    Vec3 failurePoint = entity.toGlobalVector(Vec3.atCenterOf(currentPos), 0);
                    level.explode(null, failurePoint.x, failurePoint.y, failurePoint.z,
                            2, Level.ExplosionInteraction.NONE);
                    for (int i = 0; i < 10; ++i) {
                        BlockPos pos = currentPos.relative(this.initialOrientation, i);
                        this.blocks.remove(pos);
                    }
                    if (controller != null) {
                        controller.disassemble();
                    }
                    return;
                }
            }
        }

        if (squib) {
            return;
        }
        if (!this.recoilBarrelPositions.isEmpty()) {
            breech.setPower(barrelTravelled);
            breech.handleFiring();
            for (BlockPos pos : this.recoilBarrelPositions) {
                if (this.presentBlockEntities.get(pos) instanceof MediumcannonRecoilBarrelBlockEntity spring) {
                    spring.handleFiring();
                }
            }
        }
        NetworkPlatform.sendToClientTracking(new ClientboundAnimateCannonContraptionPacket(entity.getId()), entity);

        Vec3 spawnPos = entity.toGlobalVector(Vec3.atCenterOf(currentPos.relative(this.initialOrientation)), 0);
        Vec3 vec1 = spawnPos.subtract(centerPos).normalize();
        spawnPos = spawnPos.subtract(vec1.scale(1.5));

        float recoilMagnitude = properties.baseRecoil();

        boolean isTracer = CBCConfigs.server().munitions.allAutocannonProjectilesAreTracers.get()
                || round.isTracer(foundProjectile);

        AbstractMediumcannonProjectile projectile = round.getMediumcannonProjectile(foundProjectile, level);
        if (projectile != null) {
            projectile.setPos(spawnPos);
            projectile.setChargePower(barrelTravelled);
            projectile.setTracer(isTracer);
            projectile.shoot(vec1.x, vec1.y, vec1.z, speed, spread);
            projectile.xRotO = projectile.getXRot();
            projectile.yRotO = projectile.getYRot();
            level.addFreshEntity(projectile);

            projectile.addUntouchableEntity(entity, 1);
            Entity vehicle = entity.getVehicle();
            if (vehicle != null && CBCEntityTypes.CANNON_CARRIAGE.is(vehicle)) {
                projectile.addUntouchableEntity(vehicle, 1);
            }

            if (roundProperties != null) {
                recoilMagnitude += roundProperties.addedRecoil();
            }
        }

        recoilMagnitude += properties.baseRecoil();
        recoilMagnitude *= CBCConfigs.server().cannons.bigCannonRecoilScale.getF();
        if (controller != null) {
            // 1.21.1: onRecoil 的签名是 (Vec3 反冲方向, Vec3 位移, AbstractContraptionEntity)
            controller.onRecoil(vec1.scale(-recoilMagnitude), Vec3.ZERO, entity);
        }

        breech.clearInputBuffer();
        breech.setInputBuffer(ejectStack);

        float size = barrelTravelled / 1.5f;
        float power = barrelTravelled / 1.5f;
        float soundPower = Mth.clamp(barrelTravelled / 16f, 0, 1);
        float tone = 2 + soundPower * -8 + level.random.nextFloat() * 4f - 2f;
        float pitch = (float) Mth.clamp(Math.pow(2, tone / 12f), 1, 2);
        double shakeDistance = barrelTravelled * CBCConfigs.server().cannons.bigCannonBlastDistanceMultiplier.getF();
        float volume = 10 + soundPower * 30;
        Vec3 plumePos = spawnPos.subtract(vec1);

        BigCannonPlumeParticleData plumeParticle = new BigCannonPlumeParticleData(size, power, 10);
        CannonBlastWaveEffectParticleData blastEffect = new CannonBlastWaveEffectParticleData(shakeDistance,
                BuiltInRegistries.SOUND_EVENT.wrapAsHolder(CBCModernWarfareSoundEvents.FIRE_MEDIUMCANNON.getMainEvent()),
                SoundSource.BLOCKS, volume, pitch, 2, power);
        Packet<?> blastWavePacket = new ClientboundLevelParticlesPacket(blastEffect, true,
                plumePos.x, plumePos.y, plumePos.z, 0, 0, 0, 1, 0);

        double blastDistSqr = volume * volume * 256 * 1.21;
        for (ServerPlayer player : level.players()) {
            level.sendParticles(player, plumeParticle, true, plumePos.x, plumePos.y, plumePos.z,
                    0, vec1.x, vec1.y, vec1.z, 1.0f);
            if (player.distanceToSqr(plumePos.x, plumePos.y, plumePos.z) < blastDistSqr) {
                player.connection.send(blastWavePacket);
            }
        }
    }

    @Override
    public void animate() {
        super.animate();
        if (this.presentBlockEntities.get(this.startPos) instanceof MediumcannonBreechBlockEntity breech) {
            breech.handleFiring();
        }
        for (BlockPos pos : this.recoilBarrelPositions) {
            if (this.presentBlockEntities.get(pos) instanceof MediumcannonRecoilBarrelBlockEntity spring) {
                spring.handleFiring();
            }
        }
        if (this.getBlockEntityClientSide(this.startPos) instanceof MediumcannonBreechBlockEntity breech) {
            breech.handleFiring();
        }
        for (BlockPos pos : this.recoilBarrelPositions) {
            if (this.getBlockEntityClientSide(pos) instanceof MediumcannonRecoilBarrelBlockEntity spring) {
                spring.handleFiring();
            }
        }
    }

    @Override
    public void tick(Level level, PitchOrientedContraptionEntity entity) {
        super.tick(level, entity);
        for (Map.Entry<BlockPos, BlockEntity> entry : this.presentBlockEntities.entrySet()) {
            if (entry.getValue() instanceof IMediumcannonBlockEntity medcan) {
                medcan.tickFromContraption(level, entity, entry.getKey());
            }
        }
    }

    @Override
    public boolean canBeTurnedByController(ControlPitchContraption control) {
        return true;
    }

    @Override
    public boolean canBeTurnedByPassenger(Entity entity) {
        return false;
    }

    @Override
    public boolean canBeFiredOnController(ControlPitchContraption control) {
        return this.entity.getVehicle() != control;
    }

    @Override
    public void onRedstoneUpdate(ServerLevel level, PitchOrientedContraptionEntity entity, boolean togglePower,
            int firePower, ControlPitchContraption controller) {
        if (!togglePower || firePower <= 0) {
            return;
        }
        this.fireShot(level, entity);
    }

    @Override
    public float getWeightForStress() {
        return this.cannonMaterial == null
                ? this.blocks.size()
                : this.blocks.size() * this.cannonMaterial.properties().weight();
    }

    @Override
    public Vec3 getInteractionVec(PitchOrientedContraptionEntity poce) {
        return poce.toGlobalVector(Vec3.atCenterOf(this.startPos), 0);
    }

    @Override
    public CBCMWCannonContraptionTypes getCannonType() {
        return CBCMWCannonContraptionTypes.MEDIUMCANNON;
    }

    @Override
    public ContraptionType getType() {
        return CBCModernWarfareContraptionTypes.MOUNTED_MEDIUMCANNON.get();
    }

    @Override
    public CompoundTag writeNBT(HolderLookup.Provider registries, boolean clientData) {
        CompoundTag tag = super.writeNBT(registries, clientData);
        tag.putString("MediumcannonMaterial", this.cannonMaterial == null
                ? CBCModernWarfareMediumcannonMaterials.CAST_IRON.name().toString()
                : this.cannonMaterial.name().toString());
        if (this.startPos != null) {
            tag.put("StartPos", NbtUtils.writeBlockPos(this.startPos));
        }
        if (!this.recoilBarrelPositions.isEmpty()) {
            ListTag positionsTag = new ListTag();
            for (BlockPos pos : this.recoilBarrelPositions) {
                positionsTag.add(NbtUtils.writeBlockPos(pos));
            }
            tag.put("RecoilBarrelPositions", positionsTag);
        }
        return tag;
    }

    @Override
    public void readNBT(Level level, CompoundTag tag, boolean clientData) {
        super.readNBT(level, tag, clientData);
        // 1.21.1: new ResourceLocation(String) 已弃用，用 ResourceLocation.parse。
        this.cannonMaterial = MediumcannonMaterial.fromNameOrNull(
                ResourceLocation.parse(tag.getString("MediumcannonMaterial")));
        if (this.cannonMaterial == null) {
            this.cannonMaterial = CBCModernWarfareMediumcannonMaterials.CAST_IRON;
        }
        this.startPos = tag.contains("StartPos")
                ? NbtUtils.readBlockPos(tag, "StartPos").orElse(null)
                : null;
        this.recoilBarrelPositions.clear();
        if (tag.contains("RecoilBarrelPositions")) {
            ListTag positionTags = tag.getList("RecoilBarrelPositions", Tag.TAG_COMPOUND);
            for (int i = 0; i < positionTags.size(); ++i) {
                NbtUtils.readBlockPos(positionTags.getCompound(i), "Pos")
                        .ifPresent(this.recoilBarrelPositions::add);
            }
        }
    }

    @Override
    public ItemStack insertItemIntoCannon(ItemStack stack, boolean simulate) {
        return stack;
    }

    @Override
    public ItemStack extractItemFromCannon(boolean simulate) {
        return ItemStack.EMPTY;
    }
}
