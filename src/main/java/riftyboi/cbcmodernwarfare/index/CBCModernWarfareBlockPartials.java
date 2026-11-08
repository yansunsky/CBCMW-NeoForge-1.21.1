package riftyboi.cbcmodernwarfare.index;

/**
 * partial models of this mod.
 *
 * <p><b>R3 status: skeleton only.</b> 1.20.1 had no direct registrations here; the values are held back because
 * classes from cannons/<material>/ 与 crafting/casting/ that have not been ported yet, so the entries are held back per round.
 * Registering an entry whose factory references a missing class does not compile, which is exactly
 * what the phase plan forbids.
 *
 * <p>What belongs here (round assignments, from the 1.20.1 source):
 * <ul>
 *   <li>R4: the cannon partials, keyed off MediumcannonMaterial / RotarycannonMaterial.</li>
 *   <li>R9: the cast-mould partials (needs CBCModernWarfareCastShapes).</li>
 * </ul>
 */
public class CBCModernWarfareBlockPartials {

    /** Forces class init; entries register from their field initialisers once they exist. */
    public static void register() {
        // Intentionally empty — see the class Javadoc.
    }

    private CBCModernWarfareBlockPartials() {}
}
