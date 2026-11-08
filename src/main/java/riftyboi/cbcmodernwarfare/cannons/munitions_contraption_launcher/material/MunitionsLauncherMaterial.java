package riftyboi.cbcmodernwarfare.cannons.munitions_contraption_launcher.material;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public record MunitionsLauncherMaterial(ResourceLocation name, MunitionsLauncherMaterialProperties defaultProperties) {

	public MunitionsLauncherMaterialProperties properties() {
		MunitionsLauncherMaterialProperties custom = MunitionsLauncherMaterialPropertiesHandler.getMaterial(this);
		return custom == null ? this.defaultProperties : custom;
	}

	private static final Map<ResourceLocation, MunitionsLauncherMaterial> CANNON_MATERIALS = new HashMap<>();

	public static MunitionsLauncherMaterial register(ResourceLocation loc, MunitionsLauncherMaterialProperties defaultProperties) {
		MunitionsLauncherMaterial material = new MunitionsLauncherMaterial(loc, defaultProperties);
		CANNON_MATERIALS.put(material.name(), material);
		return material;
	}

	public static MunitionsLauncherMaterial fromName(ResourceLocation loc) {
		if (!CANNON_MATERIALS.containsKey(loc)) throw new IllegalArgumentException("No big cannon material '" + loc + "' registered");
		return CANNON_MATERIALS.get(loc);
	}

	public static MunitionsLauncherMaterial fromNameOrNull(ResourceLocation loc) { return CANNON_MATERIALS.get(loc); }

}
