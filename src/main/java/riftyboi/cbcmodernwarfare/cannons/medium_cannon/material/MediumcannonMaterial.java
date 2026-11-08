package riftyboi.cbcmodernwarfare.cannons.medium_cannon.material;

import com.google.gson.JsonObject;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Ballistics profile of a medium-cannon material.
 *
 * <p><b>1.20.1 → 1.21.1</b>: {@code writeBuf}/{@code fromBuf} used {@code FriendlyByteBuf};
 * 1.21.1 network code moves component-aware payloads to {@link RegistryFriendlyByteBuf}. This class
 * is only ever serialised from datapack JSON and synced as part of the material reload, so the
 * buffer type is widened to the registry-aware one.
 *
 * <p>NOTE: the 1.20.1 file also carried a {@code CANNON_MATERIALS} map keyed by
 * {@link ResourceLocation} — kept verbatim.
 */
public record MediumcannonMaterial(ResourceLocation name, MediumcannonMaterialProperties defaultProperties) {

    private static final Map<ResourceLocation, MediumcannonMaterial> CANNON_MATERIALS = new HashMap<>();

    public MediumcannonMaterialProperties properties() {
        MediumcannonMaterialProperties handlerProperties = MediumcannonMaterialPropertiesHandler.getMaterial(this);
        return handlerProperties == null ? this.defaultProperties : handlerProperties;
    }

    public static MediumcannonMaterial register(ResourceLocation loc, MediumcannonMaterialProperties defaultProperties) {
        MediumcannonMaterial material = new MediumcannonMaterial(loc, defaultProperties);
        CANNON_MATERIALS.put(material.name(), material);
        return material;
    }

    public static MediumcannonMaterial fromName(ResourceLocation loc) {
        if (!CANNON_MATERIALS.containsKey(loc)) {
            throw new IllegalArgumentException("No mediumcannon material '" + loc + "' registered");
        }
        return CANNON_MATERIALS.get(loc);
    }

    public static MediumcannonMaterial fromNameOrNull(ResourceLocation loc) {
        return CANNON_MATERIALS.get(loc);
    }

    /** Unused but kept so the shape matches the 1.20.1 record's companion helpers. */
    static JsonObject emptyJson() {
        return GsonHelper.parse("{}");
    }
}
