package riftyboi.cbcmodernwarfare.cannon_control.contraption;

import static rbasamoyai.createbigcannons.cannons.big_cannons.BigCannonBlock.writeAndSyncSingleBlockData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.StructureTransform;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;

import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.cannon_control.ControlPitchContraption;
import rbasamoyai.createbigcannons.cannon_control.cannon_types.ICannonContraptionType;
import rbasamoyai.createbigcannons.cannon_control.contraption.AbstractMountedCannonContraption;
import rbasamoyai.createbigcannons.cannon_control.contraption.ItemCannon;
import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;
import rbasamoyai.createbigcannons.cannons.ItemCannonBehavior;
import rbasamoyai.createbigcannons.cannons.autocannon.breech.AbstractAutocannonBreechBlockEntity;
import rbasamoyai.createbigcannons.config.CBCConfigs;
import rbasamoyai.createbigcannons.effects.particles.plumes.AutocannonPlumeParticleData;
import rbasamoyai.createbigcannons.index.CBCEntityTypes;
import rbasamoyai.createbigcannons.index.CBCSoundEvents;
import rbasamoyai.createbigcannons.multiloader.NetworkPlatform;
import rbasamoyai.createbigcannons.munitions.autocannon.AbstractAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonAmmoItem;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonCartridgeItem;
import rbasamoyai.createbigcannons.munitions.autocannon.config.AutocannonProjectilePropertiesComponent;
import rbasamoyai.createbigcannons.network.ClientboundAnimateCannonContraptionPacket;
import rbasamoyai.createbigcannons.remix.GetItemStorage;
import rbasamoyai.createbigcannons.utils.CBCUtils;
import rbasamoyai.ritchiesprojectilelib.RitchiesProjectileLib;

import riftyboi.cbcmodernwarfare.cannon_control.cannon_types.CBCMWCannonContraptionTypes;
import riftyboi.cbcmodernwarfare.cannons.rotarycannon.IRotarycannonBlockEntity;
import riftyboi.cbcmodernwarfare.cannons.rotarycannon.RotarycannonBarrelBlock;
import riftyboi.cbcmodernwarfare.cannons.rotarycannon.RotarycannonBlock;
import riftyboi.cbcmodernwarfare.cannons.rotarycannon.bearing.MovesWithRotaryCannonBearing;
import riftyboi.cbcmodernwarfare.cannons.rotarycannon.bearing.RotarycannonBearingBlock;
import riftyboi.cbcmodernwarfare.cannons.rotarycannon.bearing.RotarycannonBearingBlockEntity;
import riftyboi.cbcmodernwarfare.cannons.rotarycannon.breech.AbstractRotarycannonBreechBlockEntity;
import riftyboi.cbcmodernwarfare.cannons.rotarycannon.breech.RotarycannonBreechBlockEntity;
import riftyboi.cbcmodernwarfare.cannons.rotarycannon.material.RotarycannonMaterial;
import riftyboi.cbcmodernwarfare.cannons.rotarycannon.material.RotarycannonMaterialProperties;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareContraptionTypes;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareRotarycannonMaterials;
import riftyboi.cbcmodernwarfare.munitions.autocannon.AmmoItemMixinInerface;
import riftyboi.cbcmodernwarfare.munitions.autocannon.AutocannonProjectileMixinInterface;
import riftyboi.cbcmodernwarfare.munitions.autocannon.canister.CanisterAutocannonProjectile;
import riftyboi.cbcmodernwarfare.munitions.autocannon.he.ExplosiveAutocannonProjectile;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonFuzes;

/**
 * 转管炮装配成 contraption 后的实体逻辑（装填、开火、后坐、轴承联动）。
 *
 * <p><b>这是 R4b-2 的遗留</b>：R6 收尾时发现它从未被迁移，
 * {@code RotarycannonBlock#getCannonContraption()} 一直抛
 * {@code UnsupportedOperationException}，导致转管炮（以及 R6a/R6c-1 做的
 * 燃烧剂）在游戏里完全用不了。本轮补上。
 *
 * <p><b>1.20.1 → 1.21.1 的改动</b>（全部 javap 核实）：
 * <ol>
 *   <li>{@code writeNBT(boolean)} → {@link #writeNBT(Provider, boolean)}（多一个 Provider）。</li>
 *   <li>{@code NbtUtils.readBlockPos(CompoundTag)} → {@code readBlockPos(tag, key)}（返回 Optional）；
 *       {@code writeBlockPos} 未变。</li>
 *   <li>{@code new ResourceLocation(String)} → {@link ResourceLocation#parse(String)}。</li>
 *   <li>{@code StructureBlockInfo} 是 record，取字段写 {@code info.state()} / {@code info.nbt()}。</li>
 *   <li>{@code getType()} 返回注册表里的 {@link ContraptionType}，从
 *       {@code CBCModernWarfareContraptionTypes.MOUNTED_ROTARYCANNON.get()} 取。</li>
 *   <li>{@code BlockState#getSoundType()} 无参版本已 @Deprecated →
 *       {@code state.getBlock().getSoundType(state, level, pos, entity)}。</li>
 * </ol>
 *
 * <p><b>与参考实现（refdec）的差异 —— 按项目判例以 1.20.1 为准</b>：
 * refdec 那版是 812 行，比 1.20.1 多出约 275 行，其中
 * <b>Sable 兼容层</b>（{@code projectOutOfSableSubLevel} / {@code getSableVelocity} /
 * {@code initSableCompat} 等 7 个方法）与<b>引信反射 fallback</b>
 * （{@code applyFuzeFallback} / {@code findSetFuzeMethod}）都是**新增功能**，
 * 不是 API 适配。本项目不在迁移期加新功能（HANDOFF §8.4），故不引入。
 */
public class MountedRotarycannonContraption extends AbstractMountedCannonContraption
		implements ItemCannon, GetItemStorage {

	private RotarycannonMaterial cannonMaterial;
	private final Set<BlockPos> rotaryBearingPositions = new LinkedHashSet<>();

	@Override
	public boolean assemble(Level level, BlockPos pos) throws AssemblyException {
		if (!this.collectCannonBlocks(level, pos)) {
			return false;
		}
		this.bounds = this.createBoundsFromExtensionLengths();
		return !this.blocks.isEmpty();
	}

	private boolean collectCannonBlocks(Level level, BlockPos pos) throws AssemblyException {
		BlockState startState = level.getBlockState(pos);

		if (!(startState.getBlock() instanceof RotarycannonBlock startCannon)) {
			return false;
		}
		if (!startCannon.isComplete(startState)) {
			throw hasIncompleteCannonBlocks(pos);
		}

		RotarycannonMaterial material = startCannon.getRotarycannonMaterial();
		boolean isStartBreech = startCannon.isBreechMechanism(startState);

		List<StructureBlockInfo> cannonBlocks = new ArrayList<>();
		cannonBlocks.add(new StructureBlockInfo(pos, startState, this.getBlockEntityNBT(level, pos)));

		int cannonLength = 1;
		Direction cannonFacing = startCannon.getFacing(startState);
		Direction positive = Direction.get(Direction.AxisDirection.POSITIVE, cannonFacing.getAxis());
		Direction negative = positive.getOpposite();

		// ---- 正向（positive）扫描 ----
		BlockPos start = pos;
		BlockState nextState = level.getBlockState(pos.relative(positive));
		boolean positiveBreech = false;

		while (nextState.getBlock() instanceof RotarycannonBlock cBlock
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

		// ---- 反向（negative）扫描 ----
		start = pos;
		nextState = level.getBlockState(pos.relative(negative));
		boolean negativeBreech = false;

		while (nextState.getBlock() instanceof RotarycannonBlock cBlock
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
		if (!(breechState.getBlock() instanceof RotarycannonBlock)) {
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
			if (blockInfo.state().getBlock() instanceof RotarycannonBearingBlock) {
				this.rotaryBearingPositions.add(localPos);
			}
		}

		StructureBlockInfo startInfo = this.blocks.get(this.startPos);
		if (startInfo == null || !(startInfo.state().getBlock() instanceof RotarycannonBlock)) {
			throw noRotarycannonBreech();
		}

		// 主复进弹簧：把炮管段的方块换成「已上膛」状态，并把位移表塞给弹簧 BE
		StructureBlockInfo possibleSpring = this.blocks.get(this.startPos.relative(this.initialOrientation));
		if (possibleSpring != null
				&& possibleSpring.state().getBlock() instanceof RotarycannonBearingBlock springBlock
				&& springBlock.getFacing(possibleSpring.state()) == this.initialOrientation) {
			BlockPos mainRecoilSpringPos = this.startPos.relative(this.initialOrientation).immutable();
			if (this.presentBlockEntities.get(mainRecoilSpringPos) instanceof RotarycannonBearingBlockEntity springBE) {
				for (int i = 1; i < cannonLength; ++i) {
					BlockPos pos1 = this.startPos.relative(this.initialOrientation, i);
					StructureBlockInfo blockInfo = this.blocks.get(pos1);
					if (blockInfo == null
							|| !(blockInfo.state().getBlock() instanceof MovesWithRotaryCannonBearing springed)) {
						continue;
					}
					springBE.toAnimate.put(pos1.subtract(mainRecoilSpringPos),
							springed.getMovingState(blockInfo.state()));
					this.blocks.put(pos1, new StructureBlockInfo(pos1, springed.getStationaryState(blockInfo.state()),
							blockInfo.nbt()));
				}
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
			RotarycannonMaterial material) {
		RotarycannonBlock cBlock = (RotarycannonBlock) state.getBlock();
		if (cBlock.getRotarycannonMaterialInLevel(level, state, pos) != material) {
			return false;
		}
		return level.getBlockEntity(pos) instanceof IRotarycannonBlockEntity cbe
				&& level.getBlockEntity(pos.relative(connection.getOpposite())) instanceof IRotarycannonBlockEntity cbe1
				&& cbe.cannonBehavior().isConnectedTo(connection.getOpposite())
				&& cbe1.cannonBehavior().isConnectedTo(connection);
	}

	public static AssemblyException noRotarycannonBreech() {
		return new AssemblyException(Component.translatable(
				"exception." + CreateBigCannons.MOD_ID + ".cannon_mount.noRotarycannonBreech"));
	}

	@Override
	public void addBlocksToWorld(Level world, StructureTransform transform) {
		Map<BlockPos, StructureBlockInfo> modifiedBlocks = new HashMap<>();
		for (Map.Entry<BlockPos, StructureBlockInfo> entry : this.blocks.entrySet()) {
			StructureBlockInfo info = entry.getValue();
			BlockState newState = info.state();
			boolean modified = false;

			// 放回世界时炮管要回到「未装配」状态
			if (newState.hasProperty(RotarycannonBarrelBlock.ASSEMBLED)
					&& newState.getValue(RotarycannonBarrelBlock.ASSEMBLED)) {
				newState = newState.setValue(RotarycannonBarrelBlock.ASSEMBLED, false);
				modified = true;
			}

			if (info.nbt() != null && info.nbt().contains("RenderedBlocks")) {
				info.nbt().remove("RenderedBlocks");
				modified = true;
			}

			if (modified) {
				modifiedBlocks.put(info.pos(), new StructureBlockInfo(info.pos(), newState, info.nbt()));
			}
		}
		this.blocks.putAll(modifiedBlocks);
		super.addBlocksToWorld(world, transform);
	}

	@Override
	public void fireShot(ServerLevel level, PitchOrientedContraptionEntity entity) {
		if (this.startPos == null
				|| this.cannonMaterial == null
				|| !(this.presentBlockEntities.get(this.startPos) instanceof AbstractRotarycannonBreechBlockEntity breech)
				|| !breech.canFire()) {
			return;
		}

		ItemStack foundProjectile = breech.extractNextInput();
		if (!(foundProjectile.getItem() instanceof AutocannonAmmoItem round)) {
			return;
		}
		ControlPitchContraption controller = entity.getController();

		// ---- Sable 子维度：先算出「原始世界坐标」，再判断要不要投影出子维度 ----
		// 参考点统一用 contraption 的中心，与 CBC 的 SableCompat.transformFromShip 语义一致。
		Vec3 rawCenterPos = entity.toGlobalVector(Vec3.atCenterOf(BlockPos.ZERO), 0f);
		boolean inSubLevel = RotarycannonSableCompat.isInSubLevel(level, rawCenterPos);

		// 退壳：把打空的弹壳从炮尾后方抛出去
		Vec3 rawEjectPos = entity.toGlobalVector(
				Vec3.atCenterOf(this.startPos.relative(this.initialOrientation.getOpposite())), 0f);
		Vec3 ejectPos = RotarycannonSableCompat.projectOutOfSubLevel(level, rawEjectPos, rawCenterPos);
		Vec3 centerPos = RotarycannonSableCompat.projectOutOfSubLevel(level, rawCenterPos, rawCenterPos);
		ItemStack ejectStack = round.getSpentItem(foundProjectile);
		if (!ejectStack.isEmpty()) {
			ItemEntity ejectEntity = new ItemEntity(level, ejectPos.x, ejectPos.y, ejectPos.z, ejectStack);
			Vec3 eject = ejectPos.subtract(centerPos).normalize();
			ejectEntity.setDeltaMovement(eject.scale(0.5));
			ejectEntity.setPickUpDelay(20);
			level.addFreshEntity(ejectEntity);
		}

		RotarycannonMaterialProperties properties = this.cannonMaterial.properties();
		AutocannonProjectilePropertiesComponent roundProperties = round.getAutocannonProperties(foundProjectile);

		boolean canFail = !CBCConfigs.server().failure.disableAllFailure.get();
		float speed = properties.baseSpeed();
		float spread = properties.baseSpread();
		boolean canSquib = roundProperties == null || roundProperties.canSquib();
		canSquib &= canFail;

		// 沿炮管逐格走：能装弹就加速/收束，装不下就是「炸膛」（squib）
		BlockPos currentPos = this.startPos.relative(this.initialOrientation);
		int barrelTravelled = 0;
		boolean squib = false;

		while (this.presentBlockEntities.get(currentPos) instanceof IRotarycannonBlockEntity autocannon) {
			ItemCannonBehavior behavior = autocannon.cannonBehavior();

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
					CompoundTag tag = this.presentBlockEntities.get(currentPos)
							.saveWithFullMetadata(level.registryAccess());
					tag.remove("x");
					tag.remove("y");
					tag.remove("z");
					StructureBlockInfo squibInfo = new StructureBlockInfo(currentPos, oldInfo.state(), tag);
					this.blocks.put(currentPos, squibInfo);
					Vec3 squibPos = RotarycannonSableCompat.projectOutOfSubLevel(level,
							entity.toGlobalVector(Vec3.atCenterOf(currentPos), 0f), rawCenterPos);
					// 1.21.1：无参 getSoundType() 已 @Deprecated，改用带参版本
					level.playSound(null, squibPos.x, squibPos.y, squibPos.z,
							oldInfo.state().getBlock().getSoundType(oldInfo.state(), level, currentPos, null)
									.getBreakSound(),
							SoundSource.BLOCKS, 10.0f, 0.0f);
					squib = true;
					break;
				}
				currentPos = currentPos.relative(this.initialOrientation);
			} else {
				behavior.removeItem();
				if (canFail) {
					Vec3 failurePoint = RotarycannonSableCompat.projectOutOfSubLevel(level,
							entity.toGlobalVector(Vec3.atCenterOf(currentPos), 0f), rawCenterPos);
					level.explode(null, failurePoint.x, failurePoint.y, failurePoint.z, 2,
							Level.ExplosionInteraction.NONE);
					for (int i = 0; i < 10; ++i) {
						this.blocks.remove(currentPos.relative(this.initialOrientation, i));
					}
					if (controller != null) {
						controller.disassemble();
					}
					return;
				}
			}
		}
		breech.handleFiring();
		if (squib) {
			return;
		}

		// 1.21.1：这个包现在是 record，只收实体 id（1.20.1 收实体本身）
		NetworkPlatform.sendToClientTracking(new ClientboundAnimateCannonContraptionPacket(entity.getId()), entity);

		// 出膛位置同样要投影出子维度；但**方向仍在原始坐标里算** ——
		// 子维度变换含旋转/平移，直接拿变换后的两点相减会把载具位移混进炮口方向。
		Vec3 rawSpawnPos = entity.toGlobalVector(
				Vec3.atCenterOf(currentPos.relative(this.initialOrientation)), 0f);
		Vec3 spawnPos = RotarycannonSableCompat.projectOutOfSubLevel(level, rawSpawnPos, rawCenterPos);
		Vec3 vec1 = rawSpawnPos.subtract(rawCenterPos).normalize();
		if (vec1.lengthSqr() < 1.0E-7) {
			vec1 = Vec3.atLowerCornerOf(this.initialOrientation.getNormal());
		}
		Vec3 particlePos = spawnPos.subtract(vec1.scale(1.5));

		float recoilMagnitude = properties.baseRecoil();
		boolean isTracer = CBCConfigs.server().munitions.allAutocannonProjectilesAreTracers.get()
				|| round.isTracer(foundProjectile);

		AbstractAutocannonProjectile projectile = round.getAutocannonProjectile(foundProjectile, level);
		if (projectile != null) {
			// 引信可能挂在弹药筒上而不是弹头上 —— 补到弹体
			this.applyFuzeFromCartridge(foundProjectile, projectile);

			projectile.setPos(spawnPos);
			projectile.setChargePower(barrelTravelled);
			projectile.setTracer(isTracer);
			projectile.setProjectileMass(projectile.getProjectileMass() * properties.roundPenDebuff());
			projectile.setLifetime(properties.projectileLifetime());

			// R6a/R6c-1：把弹药上的燃烧剂状态传给弹体。
			// 两个接口分别由 AutocannonCartridgeItemMixin（物品侧）
			// 与 AutocannonProjectileMixin（实体侧）实现 —— 缺任一都不会进来，
			// 这是「mixin 是否真的生效」的运行期判据之一。
			if (round instanceof AmmoItemMixinInerface roundItemInterface
					&& projectile instanceof AutocannonProjectileMixinInterface autoProjectileInterface) {
				boolean isIncendiary = roundItemInterface.isIncendiary(foundProjectile);
				autoProjectileInterface.setIncendiary(isIncendiary);
			}

			projectile.shoot(vec1.x, vec1.y, vec1.z, speed, spread);

			// Sable：弹体要继承载具（子维度）的速度，否则在高速载具上开炮
			// 弹丸会「原地掉落」—— 它的初速只有炮口速度，没有载具的动量。
			// 0.05 是 refdec 用的换算系数（Sable 的 velocity → 每 tick 位移）。
			if (inSubLevel) {
				Vec3 inherited = RotarycannonSableCompat.getVelocity(level, rawSpawnPos).scale(0.05);
				projectile.setDeltaMovement(projectile.getDeltaMovement().add(inherited));
			}

			projectile.xRotO = projectile.getXRot();
			projectile.yRotO = projectile.getYRot();

			projectile.addUntouchableEntity(entity, 1);
			Entity vehicle = entity.getVehicle();
			if (vehicle != null && CBCEntityTypes.CANNON_CARRIAGE.is(vehicle)) {
				projectile.addUntouchableEntity(vehicle, 1);
			}

			level.addFreshEntity(projectile);
			if (roundProperties != null) {
				recoilMagnitude += roundProperties.addedRecoil();
			}
		}

		recoilMagnitude *= CBCConfigs.server().cannons.autocannonRecoilScale.getF();
		if (controller != null) {
			controller.onRecoil(vec1.scale(-recoilMagnitude), Vec3.ZERO, entity);
		}

		Vec3 particleVel = vec1.scale(1.25);
		for (ServerPlayer player : level.players()) {
			if (entity.getControllingPassenger() == player) {
				continue;
			}
			level.sendParticles(player, new AutocannonPlumeParticleData(1f), true, particlePos.x, particlePos.y,
					particlePos.z, 0, particleVel.x, particleVel.y, particleVel.z, 1.0f);
		}
		CBCUtils.playBlastLikeSoundOnServer(level, spawnPos.x, spawnPos.y, spawnPos.z,
				CBCSoundEvents.FIRE_AUTOCANNON.getMainEvent(), SoundSource.BLOCKS, 12, 1, 5f);

		if (CBCConfigs.server().munitions.projectilesCanChunkload.get()) {
			ChunkPos cpos1 = new ChunkPos(BlockPos.containing(projectile.position()));
			RitchiesProjectileLib.queueForceLoad(level, cpos1.x, cpos1.z);
		}
	}

	@Override
	public void animate() {
		super.animate();
		if (this.presentBlockEntities.get(this.startPos) instanceof AbstractAutocannonBreechBlockEntity breech) {
			breech.handleFiring();
		}
	}

	/**
	 * 把「挂在弹药筒上的引信」补到弹体上。
	 *
	 * <h2>为什么需要这一步</h2>
	 * 开火时 {@code breech.extractNextInput()} 取出来的可能是**弹药筒**
	 * （{@code AutocannonCartridgeItem}）而不是弹头。此时
	 * {@code round.getAutocannonProjectile(cartridgeStack)} 内部读的是
	 * 「筒里那发弹头」的栈，而引信是挂在**筒**上的组件 —— 于是弹体拿不到引信，
	 * 打出去就不会按引信起爆。
	 *
	 * <p>{@code ExplosiveAutocannonRoundItem#getAutocannonProjectile} 那一层
	 * 已经处理了「弹头自己带引信」的情形，这里补的正是「引信在筒上」的那条路径。
	 *
	 * <h2>为什么不用反射</h2>
	 * 参考实现用了 {@code findSetFuzeMethod} 反射（沿继承链找 {@code setFuze}）。
	 * 那是为了在「不知道具体是哪个子类」时兜底。但**这些炮弹类是我们自己写的**，
	 * 只有 2 个实现带 {@code setFuze}（HE / Canister），直接 {@code instanceof} 派发即可 ——
	 * 类型安全、没有反射开销、也不会因为改类名而静默失效。
	 */
	private void applyFuzeFromCartridge(ItemStack firedStack, AbstractAutocannonProjectile projectile) {
		if (!(firedStack.getItem() instanceof AutocannonCartridgeItem)) {
			return;
		}
		// 引信在筒上
		ItemStack fuze = MediumcannonFuzes.getFuze(firedStack);
		if (fuze.isEmpty()) {
			return;
		}
		// 但筒里那发弹头可能自己已经有引信了 —— 有就不覆盖
		ItemStack projectileStack = AutocannonCartridgeItem.getProjectileStack(firedStack);
		if (projectileStack.isEmpty() || !MediumcannonFuzes.getFuze(projectileStack).isEmpty()) {
			return;
		}

		ItemStack fuzeCopy = fuze.copyWithCount(1);
		if (projectile instanceof ExplosiveAutocannonProjectile explosive) {
			explosive.setFuze(fuzeCopy);
		} else if (projectile instanceof CanisterAutocannonProjectile canister) {
			canister.setFuze(fuzeCopy);
		}
		// 其余弹种（HVAP / APDS）不吃引信，无需处理。
	}

	@Override
	public void tick(Level level, PitchOrientedContraptionEntity entity) {
		super.tick(level, entity);

		Entity controller = entity.getControllingPassenger();
		if (CBCEntityTypes.CANNON_CARRIAGE.is(entity.getVehicle())) {
			controller = entity.getVehicle().getControllingPassenger();
		}
		// 炮架上的射速提示（actionbar）
		if (!level.isClientSide && controller instanceof Player player) {
			String key = "";
			ControlPitchContraption controllerBlock = entity.getController();
			if (controllerBlock != null) {
				ResourceLocation loc = controllerBlock.getTypeId();
				if (loc != null) {
					key = "." + loc.getNamespace() + "." + loc.getPath();
				}
			}
			player.displayClientMessage(Component.translatable(
					"block." + CreateBigCannons.MOD_ID + ".cannon_carriage.hotbar.fireRate" + key,
					this.getReferencedFireRate()), true);
		}

		if (level instanceof ServerLevel slevel && this.canBeFiredOnController(entity.getController())) {
			this.fireShot(slevel, entity);
		}

		// 轴承转速跟随射速
		for (BlockPos pos : this.rotaryBearingPositions) {
			if (this.presentBlockEntities.get(pos) instanceof RotarycannonBearingBlockEntity spring
					&& this.presentBlockEntities
							.get(this.startPos) instanceof AbstractRotarycannonBreechBlockEntity breech) {
				spring.updateRotation(breech.getActualFireRate(), breech.getFireRate());
			}
		}

		for (Map.Entry<BlockPos, BlockEntity> entry : this.presentBlockEntities.entrySet()) {
			if (entry.getValue() instanceof IRotarycannonBlockEntity autocannon) {
				autocannon.tickFromContraption(level, entity, entry.getKey());
			}
		}
	}

	@Override
	public BlockPos getSeatPos(Entity entity) {
		return entity == this.entity.getControllingPassenger()
				? this.startPos.relative(this.initialOrientation.getOpposite())
				: super.getSeatPos(entity);
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
		if (this.presentBlockEntities.get(this.startPos) instanceof AbstractRotarycannonBreechBlockEntity breech) {
			breech.setFireRate(firePower);
			writeAndSyncSingleBlockData(breech, this.blocks.get(this.startPos), entity, this);
		}
	}

	public void trySettingFireRateCarriage(int fireRateAdjustment) {
		if (this.presentBlockEntities.get(this.startPos) instanceof AbstractRotarycannonBreechBlockEntity breech
				&& (fireRateAdjustment > 0 || breech.getFireRate() > 1)) {
			// > 0 因为炮架上的机炮不能关掉
			breech.setFireRate(breech.getFireRate() + fireRateAdjustment);
			writeAndSyncSingleBlockData(breech, this.blocks.get(this.startPos), entity, this);
		}
	}

	public int getReferencedFireRate() {
		return this.presentBlockEntities
				.get(this.startPos) instanceof AbstractRotarycannonBreechBlockEntity breech
						? breech.getActualFireRate()
						: 0;
	}

	@Override
	public float getWeightForStress() {
		return this.cannonMaterial == null
				? this.blocks.size()
				: this.blocks.size() * this.cannonMaterial.properties().weight();
	}

	@Override
	public Vec3 getInteractionVec(PitchOrientedContraptionEntity poce) {
		return poce.toGlobalVector(Vec3.atCenterOf(this.startPos), 0f);
	}

	@Override
	public ICannonContraptionType getCannonType() {
		return CBCMWCannonContraptionTypes.ROTARYCANNON;
	}

	@Override
	public CompoundTag writeNBT(Provider registries, boolean clientData) {
		CompoundTag tag = super.writeNBT(registries, clientData);
		tag.putString("RotarycannonMaterial",
				this.cannonMaterial == null
						? CBCModernWarfareRotarycannonMaterials.BRONZE.name().toString()
						: this.cannonMaterial.name().toString());
		if (this.startPos != null) {
			tag.put("StartPos", NbtUtils.writeBlockPos(this.startPos));
		}
		if (!this.rotaryBearingPositions.isEmpty()) {
			ListTag positionsTag = new ListTag();
			for (BlockPos pos : this.rotaryBearingPositions) {
				positionsTag.add(NbtUtils.writeBlockPos(pos));
			}
			tag.put("RotaryBarrelPositions", positionsTag);
		}
		return tag;
	}

	@Override
	public void readNBT(Level level, CompoundTag tag, boolean clientData) {
		super.readNBT(level, tag, clientData);
		this.cannonMaterial = RotarycannonMaterial
				.fromNameOrNull(ResourceLocation.parse(tag.getString("RotarycannonMaterial")));
		this.rotaryBearingPositions.clear();
		if (tag.contains("RotaryBarrelPositions")) {
			ListTag positionTags = tag.getList("RotaryBarrelPositions", Tag.TAG_COMPOUND);
			int sz = positionTags.size();
			for (int i = 0; i < sz; ++i) {
				NbtUtils.readBlockPos(positionTags.getCompound(i), "").ifPresent(this.rotaryBearingPositions::add);
			}
		}
		if (this.cannonMaterial == null) {
			this.cannonMaterial = CBCModernWarfareRotarycannonMaterials.BRONZE;
		}
		this.startPos = tag.contains("StartPos")
				? NbtUtils.readBlockPos(tag.getCompound("StartPos"), "").orElse(null)
				: null;
	}

	@Override
	public ContraptionType getType() {
		return CBCModernWarfareContraptionTypes.MOUNTED_ROTARYCANNON.get();
	}

	/**
	 * 把弹药塞进炮闩的输入队列。
	 *
	 * <p><b>这里曾经是桩实现</b>（直接 {@code return stack;}），后果是炮车
	 * （{@code CannonCarriageEntity}）与机械臂装填静默失效 —— 它们会先判
	 * {@code instanceof ItemCannon} 再调本方法，拿到原样的栈就当作「塞不进去」。
	 * 1.20.1 是靠 forge 侧的 {@code RotaryCannonContraptionMixin} override 这两个方法
	 * 走 {@code getItemStorage()}，迁移时漏了。
	 */
	@Override
	public ItemStack insertItemIntoCannon(ItemStack stack, boolean simulate) {
		IItemHandler handler = this.getItemStorage();
		return handler == null ? stack : handler.insertItem(1, stack, simulate);
	}

	@Override
	public ItemStack extractItemFromCannon(boolean simulate) {
		IItemHandler handler = this.getItemStorage();
		return handler == null ? ItemStack.EMPTY : handler.extractItem(0, 1, simulate);
	}

	/**
	 * 炮闩的弹药 handler。
	 *
	 * <p>1.21.1 的 {@code rbasamoyai.createbigcannons.remix.GetItemStorage} 返回裸
	 * {@code IItemHandler}（可为 null），不再是 1.20.1 的
	 * {@code LazyOptional<IItemHandler>} —— 与 CBC 自己的
	 * {@code MountedAutocannonContraption#getItemStorage()} 形态一致。
	 */
	@Override
	public IItemHandler getItemStorage() {
		return this.presentBlockEntities.get(this.startPos) instanceof RotarycannonBreechBlockEntity breech
				? breech.createItemHandler()
				: null;
	}
}
