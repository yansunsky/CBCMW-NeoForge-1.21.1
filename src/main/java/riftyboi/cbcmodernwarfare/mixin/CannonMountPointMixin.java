package riftyboi.cbcmodernwarfare.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import rbasamoyai.createbigcannons.cannon_control.contraption.AbstractMountedCannonContraption;
import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;
import rbasamoyai.createbigcannons.cannons.big_cannons.breeches.quickfiring_breech.CannonMountPoint;

import riftyboi.cbcmodernwarfare.cannon_control.contraption.MountedMediumcannonContraption;

/**
 * 让机械臂（Create 的 mechanical arm）能把中口径炮弹药筒塞进「火炮底座」。
 *
 * <p><b>为什么必须 mixin</b>：机械臂装填**不走物品能力**，走的是 Create 的
 * {@code ArmInteractionPoint}。CBC 的 {@code CannonMountType} 会给
 * {@code createbigcannons:cannon_mount} 造一个 {@code CannonMountPoint}，而它的
 * {@code getInsertedResultAndDoSomething} 只认两种 contraption：
 * <pre>
 *   contraption instanceof MountedBigCannonContraption  → bigCannonInsert
 *   contraption instanceof MountedAutocannonContraption → autocannonInsert
 *   else → 原样返回（静默什么都不做）
 * </pre>
 * 我们的 {@code MountedMediumcannonContraption} 两者都不是，于是「机械臂对着底座怎么都塞不进去」。
 * CBC 的 {@code bigCannonInsert} 还额外要求物品是 {@code BlockItem} + {@code BigCannonMunitionBlock}，
 * 而中口径弹药筒是普通 Item —— 所以也不能靠「伪装成大炮」绕过去。
 *
 * <p>这里在 HEAD 拦一刀，只接管我们自己的 contraption，其余情况原样放行给 CBC。
 */
@Mixin(CannonMountPoint.class)
public abstract class CannonMountPointMixin {

    @Inject(method = "getInsertedResultAndDoSomething", at = @At("HEAD"), cancellable = true)
    private void cbcmodernwarfare$insertMediumcannonAmmo(ItemStack stack, boolean simulate,
            AbstractMountedCannonContraption contraption, PitchOrientedContraptionEntity entity,
            CallbackInfoReturnable<ItemStack> cir) {
        if (!(contraption instanceof MountedMediumcannonContraption medium)) {
            return;
        }
        IItemHandler handler = medium.getItemStorage();
        cir.setReturnValue(handler == null ? stack : handler.insertItem(1, stack, simulate));
    }
}
