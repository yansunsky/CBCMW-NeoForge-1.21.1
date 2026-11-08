package riftyboi.cbcmodernwarfare.index;

import rbasamoyai.createbigcannons.CreateBigCannons;
import riftyboi.cbcmodernwarfare.CBCModernWarfare;
import riftyboi.cbcmodernwarfare.cannons.rotarycannon.material.RotarycannonMaterial;
import riftyboi.cbcmodernwarfare.cannons.rotarycannon.material.RotarycannonMaterialProperties;

public class CBCModernWarfareRotarycannonMaterials {

	public static final RotarycannonMaterial
		BRONZE = RotarycannonMaterial.register(CBCModernWarfare.resource("bronze"),
			RotarycannonMaterialProperties.builder()
				.maxBarrelLength(4)
				.weight(1f)
				.baseSpread(4f)
				.spreadReductionPerBarrel(1.5f)
				.baseSpeed(3f)
				.speedIncreasePerBarrel(1.5f)
				.maxSpeedIncreases(2)
				.projectileLifetime(25)
				.baseRecoil(0.5f)
				.connectsInSurvival(false)
				.isWeldable(true)
				.weldDamage(1)
				.weldStressPenalty(0)
				.roundPenDebuff(0.5f)
				.build()),
		STEEL = RotarycannonMaterial.register(CBCModernWarfare.resource("steel"),
			RotarycannonMaterialProperties.builder()
				.maxBarrelLength(5)
				.weight(2.5f)
				.baseSpread(5f)
				.spreadReductionPerBarrel(1.25f)
				.baseSpeed(3f)
				.speedIncreasePerBarrel(1.5f)
				.maxSpeedIncreases(3)
				.projectileLifetime(60)
				.baseRecoil(0.5f)
				.connectsInSurvival(false)
				.isWeldable(true)
				.weldDamage(2)
				.weldStressPenalty(2)
				.roundPenDebuff(0.5f)
				.build()),
		NETHERSTEEL = RotarycannonMaterial.register(CBCModernWarfare.resource("steel"),
			RotarycannonMaterialProperties.builder()
				.maxBarrelLength(6)
				.weight(3.5f)
				.baseSpread(6f)
				.spreadReductionPerBarrel(1.25f)
				.baseSpeed(3f)
				.speedIncreasePerBarrel(1.5f)
				.maxSpeedIncreases(4)
				.projectileLifetime(85)
				.baseRecoil(0.75f)
				.connectsInSurvival(false)
				.isWeldable(false)
				.weldDamage(0)
				.weldStressPenalty(0)
				.roundPenDebuff(0.5f)
				.build());
}
