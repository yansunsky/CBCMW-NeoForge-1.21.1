package riftyboi.cbcmodernwarfare.index;


import riftyboi.cbcmodernwarfare.CBCModernWarfare;
import riftyboi.cbcmodernwarfare.cannons.medium_cannon.material.MediumcannonMaterial;
import riftyboi.cbcmodernwarfare.cannons.medium_cannon.material.MediumcannonMaterialProperties;
import rbasamoyai.createbigcannons.index.CBCAutocannonMaterials;
import rbasamoyai.createbigcannons.index.CBCBigCannonMaterials;
public class CBCModernWarfareMediumcannonMaterials {

	public static final MediumcannonMaterial
		CAST_IRON = MediumcannonMaterial.register(CBCModernWarfare.resource("cast_iron"),
			MediumcannonMaterialProperties.builder()
				.maxBarrelLength(5)
				.weight(2.5f)
				.baseSpread(1.5f)
				.spreadReductionPerBarrel(2f)
				.baseSpeed(3f)
				.speedIncreasePerBarrel(1f)
				.maxSpeedIncreases(5)
				.baseRecoil(2f)
				.connectsInSurvival(false)
				.isWeldable(true)
				.weldDamage(1)
				.weldStressPenalty(1)
				.build()),
		BRONZE = MediumcannonMaterial.register(CBCModernWarfare.resource("bronze"),
			MediumcannonMaterialProperties.builder()
				.maxBarrelLength(8)
				.weight(2f)
				.baseSpread(2.0f)
				.spreadReductionPerBarrel(2.0f)
				.baseSpeed(3f)
				.speedIncreasePerBarrel(1f)
				.maxSpeedIncreases(8)
				.baseRecoil(3f)
				.connectsInSurvival(false)
				.isWeldable(true)
				.weldDamage(1)
				.weldStressPenalty(0)
				.build()),
		STEEL = MediumcannonMaterial.register(CBCModernWarfare.resource("steel"),
			MediumcannonMaterialProperties.builder()
				.maxBarrelLength(11)
				.weight(3.5f)
				.baseSpread(2.5f)
				.spreadReductionPerBarrel(2f)
				.baseSpeed(3f)
				.speedIncreasePerBarrel(1f)
				.maxSpeedIncreases(11)
				.baseRecoil(3f)
				.connectsInSurvival(false)
				.isWeldable(true)
				.weldDamage(2)
				.weldStressPenalty(2)
				.build()),
		NETHERSTEEL = MediumcannonMaterial.register(CBCModernWarfare.resource("nethersteel"),
		MediumcannonMaterialProperties.builder()
				.maxBarrelLength(14)
				.weight(4.5f)
				.baseSpread(3.0f)
				.spreadReductionPerBarrel(2f)
				.baseSpeed(3f)
				.speedIncreasePerBarrel(1f)
				.maxSpeedIncreases(14)
				.baseRecoil(5f)
				.connectsInSurvival(false)
				.isWeldable(false)
				.weldDamage(0)
				.weldStressPenalty(0)
				.build());
}
