package riftyboi.cbcmodernwarfare.index;

/**
 * munition property handlers of this mod.
 *
 * <p><b>R3 status: skeleton only.</b> 1.20.1 had no direct registrations here; the calls are held back because
 * classes from munitions/<group>/config/ that have not been ported yet, so the entries are held back per round.
 * Registering an entry whose factory references a missing class does not compile, which is exactly
 * what the phase plan forbids.
 *
 * <p>What belongs here (round assignments, from the 1.20.1 source):
 * <ul>
 *   <li>R5: HEAP_SHELL / HEF_SHELL / APDS_SHOT handlers.</li>
 *   <li>R6: the four autocannon handlers.</li>
 *   <li>R7: block-propellant / fuel-tank / thruster / guidance handlers.</li>
 *   <li>init() is called first thing in CBCModernWarfare#init() in the 1.20.1 order.</li>
 * </ul>
 */
public class CBCModernWarfareMunitionPropertiesHandlers {

    /**
     * Registers every munition-property handler.
     *
     * <p>Called first in {@code CBCModernWarfare#init()} — the 1.20.1 order, because handlers must
     * exist before any projectile block or item looks itself up.
     */
    public static void init() {
        // TODO(R5/R6/R7): one call per munition family.
        //  1.20.1 called: HEAP_SHELL.init(); HEF_SHELL.init(); APDS_SHOT.init(); ... etc.
    }

    /** Alias kept so the round-by-round call sites in init() stay uniform. */
    public static void register() {
        init();
    }

    private CBCModernWarfareMunitionPropertiesHandlers() {}
}
