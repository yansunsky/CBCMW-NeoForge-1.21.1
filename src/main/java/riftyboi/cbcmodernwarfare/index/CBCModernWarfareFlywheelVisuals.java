package riftyboi.cbcmodernwarfare.index;

/**
 * Flywheel visuals of this mod.
 *
 * <p><b>R3 status: skeleton only.</b> 1.20.1 registered 8 Flywheel visuals here; every one needs
 * classes from cannon_control/ 与 cannons/ that have not been ported yet, so the entries are held back per round.
 * Registering an entry whose factory references a missing class does not compile, which is exactly
 * what the phase plan forbids.
 *
 * <p>What belongs here (round assignments, from the 1.20.1 source):
 * <ul>
 *   <li>R4/R8: one visual per animated block entity.</li>
 *   <li>RISK: Flywheel 1.0.5 -> 1.0.6 changes how VisualizationContext and Instancer are obtained</li>
 *   <li>(85 call sites / 20 files, plan R-04). Do these together, not one at a time.</li>
 * </ul>
 */
public class CBCModernWarfareFlywheelVisuals {

    /** Forces class init; entries register from their field initialisers once they exist. */
    public static void register() {
        // Intentionally empty — see the class Javadoc.
    }

    private CBCModernWarfareFlywheelVisuals() {}
}
