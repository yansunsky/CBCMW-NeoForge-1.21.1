package riftyboi.cbcmodernwarfare.index;

import static riftyboi.cbcmodernwarfare.CBCModernWarfare.REGISTRATE;

import com.tterrag.registrate.util.entry.BlockEntityEntry;

/**
 * Block entity types of this mod.
 *
 * <p><b>R3 status: skeleton only.</b> 1.20.1 registered 21 block entities here. Every one of them
 * needs its {@code BlockEntity} class from {@code cannon_control/}, {@code cannons/},
 * {@code content/}, {@code crafting/} or {@code munitions/} — none of which exist yet. Registering
 * a {@code BlockEntityType} whose factory references a missing class would not even compile, so the
 * entries are held back per round.
 *
 * <p><b>What R3 did verify about this file</b> (the R3 unknown, now resolved):
 * <ul>
 *   <li>{@code BlockEntityType.Builder#build()} changed: 1.20.1 took no arguments, 1.21.1 takes a
 *       {@code com.mojang.datafixers.types.Type<?>}. Verified by
 *       {@code javap net.minecraft.world.level.block.entity.BlockEntityType$Builder}:
 *       {@code public BlockEntityType<T> build(com.mojang.datafixers.types.Type<?>)}.</li>
 *   <li>Registrate's {@code BlockEntityBuilder} absorbed that: its own {@code createEntry()} builds
 *       the type, so <em>the mod-facing chain API did not change</em> —
 *       {@code blockEntity(name, factory)} → {@code validBlock(s)} → {@code renderer(...)} →
 *       {@code register()} all still exist with the same shapes. Verified by
 *       {@code javap com.tterrag.registrate.builders.BlockEntityBuilder}.</li>
 *   <li>{@code .visual(...)} — commented out in the 1.20.1 source already — no longer exists: the
 *       1.21 Registrate block-entity builder has {@code validBlock} / {@code validBlocks} /
 *       {@code renderer} / {@code registerCapability} only. Flywheel visuals moved to
 *       {@code CBCModernWarfareFlywheelVisuals}.</li>
 * </ul>
 *
 * <p><b>Ids must be preserved</b> — a block entity's registry id is baked into saved chunks, so
 * the strings below are the 1.20.1 ones, not re-derived.
 */

// TODO(R4): uncomment together with cannons/medium_cannon; needs MediumcannonBlockEntity etc.
// public static final BlockEntityEntry<MediumcannonBlockEntity> MEDIUMCANNON = REGISTRATE
//         .blockEntity("mediumcannon", MediumcannonBlockEntity::new)
//         .validBlocks(CBCModernWarfareBlocks.CAST_IRON_MEDIUMCANNON_BARREL,
//                 CBCModernWarfareBlocks.BRONZE_MEDIUMCANNON_BARREL,
//                 CBCModernWarfareBlocks.STEEL_MEDIUMCANNON_BARREL,
//                 CBCModernWarfareBlocks.NETHERSTEEL_MEDIUMCANNON_BARREL)
//         .register();

// TODO(R4): rotarycannon / rotarycannon_breech / rotarycannon_bearing /
//   incomplete_rotarycannoncannon  (spelling is the 1.20.1 registry id — keep it).
//   NOTE: `rotarycannon_breech` uses IndexPlatform::makeRotarycannonBreech in 1.20.1; that method
//   is still an Object stub (R1 stub, R4 target), so it must be inlined here rather than called.

// TODO(R4): mediumcannon_breech / mediumcannon_recoil_barrel /
//   incomplete_mediumcannon.

// TODO(R5): fuzed_block (HEF_SHELL / HEAP_SHELL).

// TODO(R6): autocannon_ammo_drum.

// TODO(R7): launcher_sliding_breech / launcher_end / launcher_quickfiring_breech /
//   incomplete_cannon / munitions_launcher.

// TODO(R8): compact_mount (needs cannon_control/compact_mount, 10 files),
//   optical_guidance / manual_guidance / infrared_guidance, smoke_discharger.

public class CBCModernWarfareBlockEntities {

    /** Forces class init; the entries above will register from their field initialisers. */
    public static void register() {
        // Intentionally empty — see the class Javadoc.
    }

    private CBCModernWarfareBlockEntities() {}
}
