package riftyboi.cbcmodernwarfare.index;

/**
 * entity types of this mod.
 *
 * <p><b>R3 status: skeleton only.</b> 1.20.1 registered 25 entity types here; every one needs
 * classes from munitions/ 与 cannon_control/ that have not been ported yet, so the entries are held back per round.
 * Registering an entry whose factory references a missing class does not compile, which is exactly
 * what the phase plan forbids.
 *
 * <p>What belongs here (round assignments, from the 1.20.1 source):
 * <ul>
 *   <li>R5: the big-cannon projectiles (HEAP/HEF/APDS) and their projectile-burst entities.</li>
 *   <li>R6: the autocannon projectiles and the smoke discharger projectile.</li>
 *   <li>R7: MunitionsPhysicsContraptionEntity (the 49 KB class).</li>
 *   <li>R8: AbstractSightEntity and its four subclasses.</li>
 * </ul>
 */
public class CBCModernWarfareEntityTypes {

    /** Forces class init; entries register from their field initialisers once they exist. */
    public static void register() {
        // Intentionally empty — see the class Javadoc.
    }

    private CBCModernWarfareEntityTypes() {}
}
