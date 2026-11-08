package riftyboi.cbcmodernwarfare.index;

/**
 * contraption types of this mod.
 *
 * <p><b>R3 status: skeleton only.</b> 1.20.1 registered 4 contraption types here; every one needs
 * classes from cannon_control/contraption/ 与 munitions/contraptions/ that have not been ported yet, so the entries are held back per round.
 * Registering an entry whose factory references a missing class does not compile, which is exactly
 * what the phase plan forbids.
 *
 * <p>What belongs here (round assignments, from the 1.20.1 source):
 * <ul>
 *   <li>R7: MountedMediumcannonContraption / MountedMunitionsLauncherContraption /</li>
 *   <li>MountedRotarycannonContraption / MunitionsPhysicsContraption.</li>
 * </ul>
 */
public class CBCModernWarfareContraptionTypes {

    /** Forces class init; entries register from their field initialisers once they exist. */
    public static void register() {
        // Intentionally empty — see the class Javadoc.
    }

    private CBCModernWarfareContraptionTypes() {}
}
