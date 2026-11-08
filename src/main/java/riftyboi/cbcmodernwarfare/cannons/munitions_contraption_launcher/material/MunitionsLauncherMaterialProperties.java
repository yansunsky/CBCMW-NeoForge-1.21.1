package riftyboi.cbcmodernwarfare.cannons.munitions_contraption_launcher.material;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StringRepresentable;

public record MunitionsLauncherMaterialProperties(double minimumVelocityPerBarrel, float weight, int maxLength, FailureMode failureMode, int maxSafePropellantStress,
												  boolean connectsInSurvival, boolean isWeldable, int weldDamage, int weldStressPenalty,
												  float minimumSpread, float spreadReduction, boolean isGunLauncher,
												  float initialSpeed) {

	public boolean mayGetStuck(float chargesUsed, int barrelTravelled) {
		if (this.minimumVelocityPerBarrel < 0) return true;
		if (barrelTravelled < 1) return false;
		return this.minimumVelocityPerBarrel > chargesUsed / barrelTravelled;
	}


	public static MunitionsLauncherMaterialProperties fromJson(ResourceLocation id, JsonObject obj) {

		double minimumVelocityPerBarrel = GsonHelper.getAsDouble(obj, "minimum_velocity_per_barrel", 1);

		float weight = Math.max(0, GsonHelper.getAsFloat(obj, "weight", 2));

		int maxLength = GsonHelper.getAsInt(obj, "maxLength", 2);
		maxLength = Math.max(0, maxLength);

		FailureMode failureMode = FailureMode.byId(GsonHelper.getAsString(obj, "failure_mode"));

		int maxSafeBaseCharges = GsonHelper.getAsInt(obj, "max_safe_propellant_stress", 2);

		boolean connectsInSurvival = GsonHelper.getAsBoolean(obj, "connects_in_survival", false);
		boolean isWeldable = GsonHelper.getAsBoolean(obj, "is_weldable", false);
		int weldDamage = Math.max(GsonHelper.getAsInt(obj, "weld_damage", 0), 0);
		int weldStressPenalty = Math.max(GsonHelper.getAsInt(obj, "weld_stress_penalty", 0), 0);
		float minimumSpread = Math.max(GsonHelper.getAsFloat(obj, "minimum_spread", 0.05f), 0);
		float spreadReductionPerBarrel = Math.max(GsonHelper.getAsFloat(obj, "spread_reduction", 1), 0);
		boolean isGunLauncher = GsonHelper.getAsBoolean(obj, "is_gun_launcher", false);
		float initialSpeed = Math.max(GsonHelper.getAsFloat(obj, "inital_speed", 1), 0);
		return new MunitionsLauncherMaterialProperties(minimumVelocityPerBarrel, weight, maxLength, failureMode, maxSafeBaseCharges,
			connectsInSurvival, isWeldable, weldDamage, weldStressPenalty, minimumSpread, spreadReductionPerBarrel, isGunLauncher, initialSpeed);
	}

	public JsonObject serialize() {
		JsonObject obj = new JsonObject();
		obj.addProperty("minimum_velocity_per_barrel", this.minimumVelocityPerBarrel);
		obj.addProperty("weight", this.weight);
		obj.addProperty("maxLength", this.maxLength);
		obj.addProperty("failure_mode", this.failureMode.toString());
		obj.addProperty("max_safe_propellant_stress", this.maxSafePropellantStress);
		obj.addProperty("connects_in_survival", this.connectsInSurvival);
		obj.addProperty("is_weldable", this.isWeldable);
		obj.addProperty("weld_damage", this.weldDamage);
		obj.addProperty("weld_stress_penalty", this.weldStressPenalty);
		obj.addProperty("minimum_spread", this.minimumSpread);
		obj.addProperty("spread_reduction", this.spreadReduction);
		obj.addProperty("isGunLauncher", this.isGunLauncher);
		obj.addProperty("initial_speed", this.initialSpeed);
		return obj;
	}

	public void writeBuf(FriendlyByteBuf buf) {
		buf.writeDouble(this.minimumVelocityPerBarrel);
		buf.writeFloat(this.weight)
			.writeInt(this.maxSafePropellantStress);
			buf.writeVarInt(this.maxLength)
			.writeVarInt(this.failureMode.ordinal())
			.writeBoolean(this.connectsInSurvival)
			.writeBoolean(this.isWeldable);
		buf.writeVarInt(this.weldDamage)
			.writeVarInt(this.weldStressPenalty)
			.writeFloat(this.minimumSpread)
			.writeFloat(this.spreadReduction)
			.writeBoolean(this.isGunLauncher)
			.writeFloat(this.initialSpeed);
	}

	public static MunitionsLauncherMaterialProperties fromBuf(FriendlyByteBuf buf) {
		double minimumVelocityPerBarrel = buf.readDouble();
		float weight = buf.readFloat();
		int maxSafeBaseCharges = buf.readVarInt();
		FailureMode mode = FailureMode.values()[buf.readVarInt()];
		boolean connectsInSurvival = buf.readBoolean();
		boolean isWeldable = buf.readBoolean();
		int weldDamage = buf.readVarInt();
		int weldStressPenalty = buf.readVarInt();
		float minimumSpread = buf.readFloat();
		float spreadReduction = buf.readFloat();
		boolean isGunLauncher = buf.readBoolean();
		float initialSpeed = buf.readFloat();
		return new MunitionsLauncherMaterialProperties(minimumVelocityPerBarrel, weight, maxSafeBaseCharges, mode, maxSafeBaseCharges, connectsInSurvival,
			isWeldable, weldDamage, weldStressPenalty, minimumSpread, spreadReduction, isGunLauncher, initialSpeed);
	}

	public enum FailureMode implements StringRepresentable {
		RUPTURE,
		FRAGMENT;
		private static final Map<String, FailureMode> BY_ID = Arrays.stream(values())
			.collect(Collectors.toMap(StringRepresentable::getSerializedName, Function.identity()));

		private final String name;

		FailureMode() {
			this.name = this.name().toLowerCase(Locale.ROOT);
		}

		@Override public String getSerializedName() { return this.name; }
		public static FailureMode byId(String id) { return BY_ID.getOrDefault(id, RUPTURE); }
	}

}
