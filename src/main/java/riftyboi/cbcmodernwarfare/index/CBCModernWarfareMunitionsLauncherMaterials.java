package riftyboi.cbcmodernwarfare.index;

import rbasamoyai.createbigcannons.cannons.big_cannons.material.BigCannonMaterial;
import rbasamoyai.createbigcannons.cannons.big_cannons.material.BigCannonMaterialProperties;
import rbasamoyai.createbigcannons.index.CBCBigCannonMaterials;
import riftyboi.cbcmodernwarfare.CBCModernWarfare;
import riftyboi.cbcmodernwarfare.cannons.munitions_contraption_launcher.material.MunitionsLauncherMaterial;
import riftyboi.cbcmodernwarfare.cannons.munitions_contraption_launcher.material.MunitionsLauncherMaterialProperties;
import riftyboi.cbcmodernwarfare.cannons.munitions_contraption_launcher.material.MunitionsLauncherMaterialProperties.FailureMode;

public class CBCModernWarfareMunitionsLauncherMaterials {
	public static final MunitionsLauncherMaterial
		ROCKET_LAUNCHER = MunitionsLauncherMaterial.register(CBCModernWarfare.resource("rocket_launcher"), new MunitionsLauncherMaterialProperties( -1d,1f, 5, FailureMode.FRAGMENT, 0, true, false, 0, 0, 1.25f, 0.5f,  false, 3)),
		TORPEDO_LAUNCHER = MunitionsLauncherMaterial.register(CBCModernWarfare.resource("torpedo_launcher"), new MunitionsLauncherMaterialProperties( -1d,1.5f, 5, FailureMode.RUPTURE, 0,false, true, 1, 1, 1.25f, 1f,false,2)),
		GUN_LAUNCHER = MunitionsLauncherMaterial.register(CBCModernWarfare.resource("gun_launcher"), new MunitionsLauncherMaterialProperties( 1d,2.5f, 5, FailureMode.RUPTURE, 4,false, true, 1, 1, 0.5f, 1f, true,3));

}

