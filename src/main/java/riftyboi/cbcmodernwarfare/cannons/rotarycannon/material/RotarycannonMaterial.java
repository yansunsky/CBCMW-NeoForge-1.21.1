package riftyboi.cbcmodernwarfare.cannons.rotarycannon.material;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public record RotarycannonMaterial(ResourceLocation name, RotarycannonMaterialProperties defaultProperties) {

	public RotarycannonMaterialProperties properties() {
		RotarycannonMaterialProperties handlerProperties = RotarycannonMaterialPropertiesHandler.getMaterial(this);
		return handlerProperties == null ? this.defaultProperties : handlerProperties;
	}

    private static final Map<ResourceLocation, RotarycannonMaterial> CANNON_MATERIALS = new HashMap<>();

    public static RotarycannonMaterial register(ResourceLocation loc, RotarycannonMaterialProperties defaultProperties) {
        RotarycannonMaterial material = new RotarycannonMaterial(loc, defaultProperties);
        CANNON_MATERIALS.put(material.name(), material);
        return material;
    }

    public static RotarycannonMaterial fromName(ResourceLocation loc) {
		if (!CANNON_MATERIALS.containsKey(loc)) throw new IllegalArgumentException("No autocannon material '" + loc + "' registered");
        return CANNON_MATERIALS.get(loc);
    }

	public static RotarycannonMaterial fromNameOrNull(ResourceLocation loc) { return CANNON_MATERIALS.get(loc); }

}
