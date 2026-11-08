package riftyboi.cbcmodernwarfare.index;

import static riftyboi.cbcmodernwarfare.CBCModernWarfare.REGISTRATE;

import com.tterrag.registrate.util.entry.ItemEntry;

/**
 * Items of this mod that are not block items.
 *
 * <p><b>R3 status: skeleton only.</b> 1.20.1 registered 25 {@code ItemEntry} values here. They all
 * need classes from {@code munitions/} ({@code MediumcannonCartridgeItem},
 * {@code MediumcannonRoundItem}, the nine {@code *MediumcannonCartridgeItem} variants, the four
 * {@code *AutocannonRoundItem} variants, {@code SmokeDischargerRoundItem}) — R5/R6 material.
 *
 * <p><b>What R3 verified about this file:</b> the 1.20.1 chain is
 * {@code REGISTRATE.item(id, Item::new).transform(someItemHelper).register()}, and
 * {@code ItemBuilder} is unchanged on 1.21.1 (same {@code model} / {@code lang} / {@code tag} /
 * {@code properties} / {@code register} shapes as {@code BlockBuilder}). The helpers it calls live
 * in {@code datagen/assets/CBCModernWarfareBuilderTransformers}, which currently only has 2 of its
 * 37 methods (R2) — so nothing here can be written until R5 ports the item helpers.
 *
 * <p><b>Ids must be preserved</b> — an item id is what existing worlds and recipes reference.
 */

// TODO(R5): the 4 "falling breech" items + 3 rotarycannon breech extractors.
// TODO(R5): filled/empty mediumcannon cartridge, cartridge sheet.
// TODO(R5): the nine mediumcannon cartridge + round pairs
//   (AP, APHE, APDS, APFSDS, Canister, HE, HEF, HEAP, Smoke).
// TODO(R6): the four autocannon rounds (HVAP, APDS, Canister, HE).
// TODO(R6): incendiary tip, heavy recoil spring, inert explosives,
//   carbon steel ingot/nugget, silicon ingot/nugget.
// TODO(R6): smoke discharger round.

public class CBCModernWarfareItem {

    /** Forces class init; the entries above will register from their field initialisers. */
    public static void register() {
        // Intentionally empty — see the class Javadoc.
    }

    private CBCModernWarfareItem() {}
}
