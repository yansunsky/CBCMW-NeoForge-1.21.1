package riftyboi.cbcmodernwarfare.munitions.munitions_contraption_launcher.config.components;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

/**
 * 推进器（thruster）的属性组件 —— 在燃料箱的基础上多了「燃烧速率」和「推力」。
 *
 * <p>1.20.1 原样迁移，零 API 调整。
 *
 * <p>JSON 键名是数据包契约，不要改名。注意 {@code burnRate} / {@code thrust}
 * 用的是驼峰，而 {@code added_gravity} / {@code added_spread} 用的是下划线 ——
 * 这是 1.20.1 原项目的写法（不统一），**保持原样**，
 * 因为已发布的数据包 JSON 依赖它。
 */
public record ThrusterPropertiesComponent(int storedFuel, float addedGravity, float addedSpread,
                                         float burnRate, float thrust) {

    public static final ThrusterPropertiesComponent DEFAULT = new ThrusterPropertiesComponent(0, 0, 0, 0, 0);

    public static ThrusterPropertiesComponent fromJson(String id, JsonObject obj) {
        int fuel = Math.max(0, GsonHelper.getAsInt(obj, "storedFuel", 1));
        float gravity = Math.max(0, GsonHelper.getAsFloat(obj, "added_gravity", 1));
        float spread = Math.max(0, GsonHelper.getAsFloat(obj, "added_spread", 1));
        float rate = Math.max(0, GsonHelper.getAsFloat(obj, "burnRate", 1));
        float thrust = Math.max(0, GsonHelper.getAsFloat(obj, "thrust", 1));
        return new ThrusterPropertiesComponent(fuel, gravity, spread, rate, thrust);
    }

    public static ThrusterPropertiesComponent fromNetwork(FriendlyByteBuf buf) {
        return new ThrusterPropertiesComponent(buf.readInt(), buf.readFloat(), buf.readFloat(),
                buf.readFloat(), buf.readFloat());
    }

    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeInt(this.storedFuel)
                .writeFloat(this.addedGravity)
                .writeFloat(this.addedSpread)
                .writeFloat(this.burnRate)
                .writeFloat(this.thrust);
    }
}
