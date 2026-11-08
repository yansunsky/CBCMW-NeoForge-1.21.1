package riftyboi.cbcmodernwarfare.index;

/**
 * menus of this mod.
 *
 * <p><b>R3 status: skeleton only.</b> 1.20.1 registered 1 menus here; every one needs
 * classes from munitions/autocannon/ammo_drum/ that have not been ported yet, so the entries are held back per round.
 * Registering an entry whose factory references a missing class does not compile, which is exactly
 * what the phase plan forbids.
 *
 * <p>What belongs here (round assignments, from the 1.20.1 source):
 * <ul>
 *   <li>R6: AutocannonAmmoDrumMenu + AutocannonAmmoDrumScreen.</li>
 *   <li>NOTE(1.21.1): Registering a CUSTOM MenuType makes clients that have NeoForge but NOT this mod</li>
 *   <li>get disconnected during registry sync (RegistryManager#applySnapshot -> missing -></li>
 *   <li>ClientPayloadHandler#handle(FrozenRegistrySyncCompletedPayload) -> disconnect, no exemption).</li>
 *   <li>hasChannel cannot guard this: the kick happens during handshake, before any menu packet.</li>
 *   <li>=> Prefer vanilla MenuType.GENERIC_9x6 and send extra state over an optional() S2C channel.</li>
 * </ul>
 */
public class CBCModernWarfareMenuTypes {

    /** Forces class init; entries register from their field initialisers once they exist. */
    public static void register() {
        // Intentionally empty — see the class Javadoc.
    }

    private CBCModernWarfareMenuTypes() {}
}
