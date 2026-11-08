package riftyboi.cbcmodernwarfare.cannons.medium_cannon.material;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

/**
 * Ballistics profile of a medium-cannon material.
 *
 * <p><b>1.20.1 → 1.21.1: zero API change.</b> Verified against the reference port — it kept
 * {@link FriendlyByteBuf} (not the registry-aware variant, because this payload carries no
 * components) and the same {@code fromJson}/{@code serialize}/{@code writeBuf}/{@code fromBuf}
 * quartet. Copied verbatim.
 */
public record MediumcannonMaterialProperties(int maxBarrelLength, float weight, float baseSpread,
        float spreadReductionPerBarrel, float baseSpeed, float speedIncreasePerBarrel,
        int maxSpeedIncreases, float baseRecoil, boolean connectsInSurvival,
        boolean isWeldable, int weldDamage, int weldStressPenalty) {

    public static MediumcannonMaterialProperties fromJson(JsonObject obj) {
        int maxBarrelLength = Math.max(1, GsonHelper.getAsInt(obj, "maximum_barrel_length"));
        float weight = Math.max(0, GsonHelper.getAsFloat(obj, "weight", 2));
        float baseSpread = Math.max(0.01f, GsonHelper.getAsFloat(obj, "base_spread", 3));
        float spreadReductionPerBarrel = Math.max(0, GsonHelper.getAsFloat(obj, "spread_reduction_per_barrel", 0.5f));
        float baseSpeed = Math.max(0.1f, GsonHelper.getAsFloat(obj, "base_speed", 1));
        float speedIncreasePerBarrel = Math.max(0, GsonHelper.getAsFloat(obj, "speed_increase_per_barrel", 0.5f));
        int maxSpeedIncreases = Math.max(0, GsonHelper.getAsInt(obj, "max_speed_increases", 2));
        float baseRecoil = Math.max(0, GsonHelper.getAsFloat(obj, "base_recoil", 1));
        boolean connectsInSurvival = GsonHelper.getAsBoolean(obj, "connects_in_survival", false);
        boolean isWeldable = GsonHelper.getAsBoolean(obj, "is_weldable", false);
        int weldDamage = Math.max(GsonHelper.getAsInt(obj, "weld_damage", 0), 0);
        int weldStressPenalty = Math.max(GsonHelper.getAsInt(obj, "weld_stress_penalty", 0), 0);
        return new MediumcannonMaterialProperties(maxBarrelLength, weight, baseSpread, spreadReductionPerBarrel,
                baseSpeed, speedIncreasePerBarrel, maxSpeedIncreases, baseRecoil, connectsInSurvival,
                isWeldable, weldDamage, weldStressPenalty);
    }

    public JsonObject serialize() {
        JsonObject obj = new JsonObject();
        obj.addProperty("maximum_barrel_length", this.maxBarrelLength);
        obj.addProperty("weight", this.weight);
        obj.addProperty("base_spread", this.baseSpread);
        obj.addProperty("spread_reduction_per_barrel", this.spreadReductionPerBarrel);
        obj.addProperty("base_speed", this.baseSpeed);
        obj.addProperty("speed_increase_per_barrel", this.speedIncreasePerBarrel);
        obj.addProperty("max_speed_increases", this.maxSpeedIncreases);
        obj.addProperty("base_recoil", this.baseRecoil);
        obj.addProperty("connects_in_survival", this.connectsInSurvival);
        obj.addProperty("is_weldable", this.isWeldable);
        obj.addProperty("weld_damage", this.weldDamage);
        obj.addProperty("weld_stress_penalty", this.weldStressPenalty);
        return obj;
    }

    public void writeBuf(FriendlyByteBuf buf) {
        buf.writeVarInt(this.maxBarrelLength);
        buf.writeFloat(this.weight)
                .writeFloat(this.baseSpread)
                .writeFloat(this.spreadReductionPerBarrel)
                .writeFloat(this.baseSpeed)
                .writeFloat(this.speedIncreasePerBarrel);
        buf.writeVarInt(this.maxSpeedIncreases)
                .writeFloat(this.baseRecoil)
                .writeBoolean(this.connectsInSurvival)
                .writeBoolean(this.isWeldable);
        buf.writeVarInt(this.weldDamage)
                .writeVarInt(this.weldStressPenalty);
    }

    public static MediumcannonMaterialProperties fromBuf(FriendlyByteBuf buf) {
        int maxBarrelLength = buf.readVarInt();
        float weight = buf.readFloat();
        float baseSpread = buf.readFloat();
        float spreadReductionPerBarrel = buf.readFloat();
        float baseSpeed = buf.readFloat();
        float speedIncreasePerBarrel = buf.readFloat();
        int maxSpeedIncreases = buf.readVarInt();
        float baseRecoil = buf.readFloat();
        boolean connectsInSurvival = buf.readBoolean();
        boolean isWeldable = buf.readBoolean();
        int weldDamage = buf.readVarInt();
        int weldStressPenalty = buf.readVarInt();
        return new MediumcannonMaterialProperties(maxBarrelLength, weight, baseSpread, spreadReductionPerBarrel,
                baseSpeed, speedIncreasePerBarrel, maxSpeedIncreases, baseRecoil, connectsInSurvival,
                isWeldable, weldDamage, weldStressPenalty);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private int maxBarrelLength;
        private float weight;
        private float baseSpread;
        private float spreadReductionPerBarrel;
        private float baseSpeed;
        private float speedIncreasePerBarrel;
        private int maxSpeedIncreases;
        private float baseRecoil;
        private boolean connectsInSurvival;
        private boolean isWeldable;
        private int weldDamage;
        private int weldStressPenalty;

        private Builder() {}

        public Builder maxBarrelLength(int v) { this.maxBarrelLength = v; return this; }
        public Builder weight(float v) { this.weight = v; return this; }
        public Builder baseSpread(float v) { this.baseSpread = v; return this; }
        public Builder spreadReductionPerBarrel(float v) { this.spreadReductionPerBarrel = v; return this; }
        public Builder baseSpeed(float v) { this.baseSpeed = v; return this; }
        public Builder speedIncreasePerBarrel(float v) { this.speedIncreasePerBarrel = v; return this; }
        public Builder maxSpeedIncreases(int v) { this.maxSpeedIncreases = v; return this; }
        public Builder baseRecoil(float v) { this.baseRecoil = v; return this; }
        public Builder connectsInSurvival(boolean v) { this.connectsInSurvival = v; return this; }
        public Builder isWeldable(boolean v) { this.isWeldable = v; return this; }
        public Builder weldDamage(int v) { this.weldDamage = v; return this; }
        public Builder weldStressPenalty(int v) { this.weldStressPenalty = v; return this; }

        public MediumcannonMaterialProperties build() {
            return new MediumcannonMaterialProperties(this.maxBarrelLength, this.weight, this.baseSpread,
                    this.spreadReductionPerBarrel, this.baseSpeed, this.speedIncreasePerBarrel,
                    this.maxSpeedIncreases, this.baseRecoil, this.connectsInSurvival,
                    this.isWeldable, this.weldDamage, this.weldStressPenalty);
        }
    }
}
