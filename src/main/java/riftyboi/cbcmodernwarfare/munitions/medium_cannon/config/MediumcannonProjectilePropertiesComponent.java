package riftyboi.cbcmodernwarfare.munitions.medium_cannon.config;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

/**
 * 中型炮炮弹的附加属性：后坐增量与是否可能「哑火」。
 *
 * <p><b>1.20.1 → 1.21.1</b>：{@code FriendlyByteBuf} 未变（这个 payload 不带组件），
 * record 与三个静态方法签名全部一致，逐字搬入。
 *
 * <p>注意 {@code added_recoil} 的默认值在 1.20.1 是 <b>1</b>（不是 0）——
 * 原代码写的是 {@code getAsDouble(obj, "added_recoil", 1)}，保持原值。
 */
public record MediumcannonProjectilePropertiesComponent(double addedRecoil, boolean canSquib) {

    public static final MediumcannonProjectilePropertiesComponent DEFAULT =
            new MediumcannonProjectilePropertiesComponent(0, false);

    public static MediumcannonProjectilePropertiesComponent fromJson(String id, JsonObject obj) {
        double addedRecoil = Math.max(0, GsonHelper.getAsDouble(obj, "added_recoil", 1));
        boolean canSquib = GsonHelper.getAsBoolean(obj, "can_squib", true);
        return new MediumcannonProjectilePropertiesComponent(addedRecoil, canSquib);
    }

    public static MediumcannonProjectilePropertiesComponent fromNetwork(FriendlyByteBuf buf) {
        return new MediumcannonProjectilePropertiesComponent(buf.readDouble(), buf.readBoolean());
    }

    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeDouble(this.addedRecoil)
                .writeBoolean(this.canSquib);
    }
}
