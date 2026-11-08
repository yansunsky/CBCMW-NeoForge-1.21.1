package riftyboi.cbcmodernwarfare.munitions.medium_cannon.canister;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.index.CBCDamageTypes;
import rbasamoyai.createbigcannons.munitions.CannonDamageSource;
import rbasamoyai.createbigcannons.munitions.big_cannon.grapeshot.GrapeshotBurst;

public class CanisterBurst extends GrapeshotBurst {

	public CanisterBurst(EntityType<? extends GrapeshotBurst> entityType, Level level) { super(entityType, level); }

	@Override
	protected DamageSource getDamageSource() {
		return new CannonDamageSource(CannonDamageSource.getDamageRegistry(this.level()).getHolderOrThrow(CBCDamageTypes.SHRAPNEL), this.getProperties().damage().ignoresEntityArmor());
	}
}
