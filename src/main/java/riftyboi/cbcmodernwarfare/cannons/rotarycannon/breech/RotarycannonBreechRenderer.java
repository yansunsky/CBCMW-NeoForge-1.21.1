package riftyboi.cbcmodernwarfare.cannons.rotarycannon.breech;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;

import dev.engine_room.flywheel.api.visualization.VisualizationManager;

import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import rbasamoyai.createbigcannons.munitions.autocannon.ammo_container.AutocannonAmmoContainerBlock;
import rbasamoyai.createbigcannons.munitions.autocannon.ammo_container.AutocannonAmmoContainerItem;

/**
 * 转管炮炮闩的原版渲染回退：Flywheel 不可用时画弹匣。
 *
 * <p><b>1.20.1 → 1.21.1</b>：{@code CachedBuffers.block(...).translate(...).rotateCentered(...)}
 * 链式 API 未变。{@code AutocannonAmmoContainerBlock.State.getFromFilled} 也在。
 *
 * <p><b>与参考实现的差异</b>：参考实现里有个
 * {@code SableRenderCompat.isInSubLevel(breech)} 判断 —— 那是它为了兼容「子维度」模组
 * 自己加的（1.20.1 源里没有，本项目也没有那个依赖）。这里不引入，条件只剩
 * 「Flywheel 不支持可视化时才走原版渲染」。
 */
public class RotarycannonBreechRenderer extends SmartBlockEntityRenderer<AbstractRotarycannonBreechBlockEntity> {

    public RotarycannonBreechRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(AbstractRotarycannonBreechBlockEntity breech, float partialTicks, PoseStack ms,
            MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(breech, partialTicks, ms, buffer, light, overlay);

        if (VisualizationManager.supportsVisualization(breech.getLevel())) {
            return;
        }

        BlockState state = breech.getBlockState();
        Direction facing = state.getValue(RotarycannonBreechBlock.FACING);

        ms.pushPose();
        Vector3f normal = facing.step();
        normal.mul(breech.getAnimateOffset(partialTicks) * -0.5f);

        ItemStack container = breech.getMagazine();
        if (container.getItem() instanceof AutocannonAmmoContainerItem) {
            boolean vertical = facing.getAxis().isVertical();
            Quaternionf q1;
            if (vertical) {
                float f = facing == Direction.UP ? 90.0f : -90.0f;
                q1 = Axis.ZP.rotationDegrees(f);
                q1.mul(Axis.XP.rotationDegrees(f));
            } else {
                q1 = Axis.YP.rotationDegrees(-90.0f - facing.toYRot());
            }

            Direction offset = vertical
                    ? facing.getCounterClockWise(Direction.Axis.Z)
                    : facing.getClockWise(Direction.Axis.Y);
            Vector3f normal1 = facing == Direction.UP ? offset.getOpposite().step() : offset.step();
            normal1.mul(0.625f);

            SuperByteBuffer ammoModel = CachedBuffers.block(getAmmoContainerModel(container))
                    .translate(normal1)
                    .rotateCentered(q1);
            ammoModel.light(light).renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));
        }

        ms.popPose();
    }

    private static BlockState getAmmoContainerModel(ItemStack stack) {
        BlockState state = ((BlockItem) stack.getItem()).getBlock().defaultBlockState();
        if (state.hasProperty(AutocannonAmmoContainerBlock.CONTAINER_STATE)) {
            state = state.setValue(AutocannonAmmoContainerBlock.CONTAINER_STATE,
                    AutocannonAmmoContainerBlock.State.getFromFilled(
                            AutocannonAmmoContainerItem.getTotalAmmoCount(stack) > 0));
        }
        return state;
    }
}
