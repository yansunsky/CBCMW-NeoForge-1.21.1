package riftyboi.cbcmodernwarfare.index;

/**
 * recipe serializers and types of this mod.
 *
 * <p><b>R3 status: skeleton only.</b> 1.20.1 registered 29 recipe serializers and types here; every one needs
 * classes from crafting/ that have not been ported yet, so the entries are held back per round.
 * Registering an entry whose factory references a missing class does not compile, which is exactly
 * what the phase plan forbids.
 *
 * <p>What belongs here (round assignments, from the 1.20.1 source):
 * <ul>
 *   <li>R9: the whole crafting/ package. 1.21 recipes need MapCodec + StreamCodec.</li>
 *   <li>NOTE: IndexPlatform#registerRecipeSerializer / #registerRecipeType already exist and are</li>
 *   <li>wired to the two DeferredRegisters there — reuse them, do not add a third registry.</li>
 * </ul>
 */
public class CBCModernWarfareRecipeTypes {

    /** Forces class init; entries register from their field initialisers once they exist. */
    public static void register() {
        // Intentionally empty — see the class Javadoc.
    }

    private CBCModernWarfareRecipeTypes() {}
}
