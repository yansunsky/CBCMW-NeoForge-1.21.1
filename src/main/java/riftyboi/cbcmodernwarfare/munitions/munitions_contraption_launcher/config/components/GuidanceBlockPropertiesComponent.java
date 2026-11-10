package riftyboi.cbcmodernwarfare.munitions.munitions_contraption_launcher.config.components;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

/**
 * 制导组件（guidance block）的属性 —— 转向速率、最大速度、附加重力与散布。
 *
 * <p>1.20.1 原样迁移，零 API 调整。
 *
 * <p>JSON 键名是数据包契约，不要改名。
 */
public record GuidanceBlockPropertiesComponent(float turnRate, float maxSpeed, float addedGravity,
                                               float addedSpread) {

    public static final GuidanceBlockPropertiesComponent DEFAULT =
            new GuidanceBlockPropertiesComponent(0, 0, 0, 0);

    public static GuidanceBlockPropertiesComponent fromJson(String id, JsonObject obj) {
        float turnRate = Math.max(0, GsonHelper.getAsFloat(obj, "turn_rate", 1));
        float maxSpeed = Math.max(0, GsonHelper.getAsFloat(obj, "max_speed", 1));
        float gravity = Math.max(0, GsonHelper.getAsFloat(obj, "added_gravity", 1));
        float spread = Math.max(0, GsonHelper.getAsFloat(obj, "added_spread", 1));
        return new GuidanceBlockPropertiesComponent(turnRate, maxSpeed, gravity, spread);
    }

    public static GuidanceBlockPropertiesComponent fromNetwork(FriendlyByteBuf buf) {
        return new GuidanceBlockPropertiesComponent(buf.readFloat(), buf.readFloat(),
                buf.readFloat(), buf.readFloat());
    }

    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeFloat(this.turnRate)
                .writeFloat(this.maxSpeed)
                .writeFloat(this.addedGravity)
                .writeFloat(this.addedSpread);
    }
}
