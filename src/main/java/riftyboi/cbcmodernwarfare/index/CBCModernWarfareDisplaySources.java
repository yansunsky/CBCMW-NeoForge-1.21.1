package riftyboi.cbcmodernwarfare.index;

/**
 * display sources of this mod.
 *
 * <p><b>R3 status: skeleton only.</b> 1.20.1 registered 1 display sources here; every one needs
 * classes from cannon_control/compact_mount/ that have not been ported yet, so the entries are held back per round.
 * Registering an entry whose factory references a missing class does not compile, which is exactly
 * what the phase plan forbids.
 *
 * <p>What belongs here (round assignments, from the 1.20.1 source):
 * <ul>
 *   <li>R8: CompactCannonMountDisplaySource.</li>
 *   <li>NOTE(1.21.1): register via REGISTRATE.displaySource(name, Supplier) — verified present on</li>
 *   <li>CreateRegistrate. The 1.20.1 code used the same CreateRegistrate API.</li>
 * </ul>
 */
public class CBCModernWarfareDisplaySources {

    /** Forces class init; entries register from their field initialisers once they exist. */
    public static void register() {
        // Intentionally empty — see the class Javadoc.
    }

    private CBCModernWarfareDisplaySources() {}
}
