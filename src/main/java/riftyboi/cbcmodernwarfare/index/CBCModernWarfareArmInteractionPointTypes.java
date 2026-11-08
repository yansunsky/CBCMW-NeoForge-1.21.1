package riftyboi.cbcmodernwarfare.index;

/**
 * arm interaction point types of this mod.
 *
 * <p><b>R3 status: skeleton only.</b> 1.20.1 registered 1 arm interaction point types here; every one needs
 * classes from cannon_control/compact_mount/ that have not been ported yet, so the entries are held back per round.
 * Registering an entry whose factory references a missing class does not compile, which is exactly
 * what the phase plan forbids.
 *
 * <p>What belongs here (round assignments, from the 1.20.1 source):
 * <ul>
 *   <li>R8: CannonMountType.</li>
 *   <li>NOTE(1.21.1): register against CreateBuiltInRegistries.ARM_INTERACTION_POINT_TYPE — verified</li>
 *   <li>present. The class itself (canCreatePoint / createPoint / getPriority) is unchanged.</li>
 * </ul>
 */
public class CBCModernWarfareArmInteractionPointTypes {

    /** Forces class init; entries register from their field initialisers once they exist. */
    public static void register() {
        // Intentionally empty — see the class Javadoc.
    }

    private CBCModernWarfareArmInteractionPointTypes() {}
}
