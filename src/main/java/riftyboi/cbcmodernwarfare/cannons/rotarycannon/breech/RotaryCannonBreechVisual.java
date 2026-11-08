package riftyboi.cbcmodernwarfare.cannons.rotarycannon.breech;

import java.util.function.Consumer;

import com.mojang.math.Axis;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.instance.Instancer;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;

import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import rbasamoyai.createbigcannons.cannons.autocannon.AutocannonBlock;
import rbasamoyai.createbigcannons.index.CBCBlockPartials;
import rbasamoyai.createbigcannons.munitions.autocannon.ammo_container.AutocannonAmmoContainerBlock;
import rbasamoyai.createbigcannons.munitions.autocannon.ammo_container.AutocannonAmmoContainerItem;

/**
 * 转管炮炮闩的 Flywheel 视觉：画出插在侧面/顶面的弹匣。
 *
 * <p><b>1.20.1 → 1.21.1</b>：{@code Instancer} / {@code OrientedInstance} /
 * {@code Models.block(BlockState)} / {@code setVisible} / {@code stealInstance} 全部未变
 * （Flywheel 1.0.6 javap 核对）。{@code AutocannonAmmoContainerBlock.State.getFromFilled}
 * 与 {@code AutocannonAmmoContainerItem.getTotalAmmoCount} 也在（CBC 5.11.7）。
 *
 * <p>本类的 {@code getPartialModelForState()} 与 {@code getAmmoContainerModel()} 是从参考实现
 * 搬来的，但注意它们其实<b>没有被用到</b>（原代码里也没有调用点，是死代码）。
 * 保留是为了与参考实现保持同构、便于后续排查；不影响行为。
 */
public class RotaryCannonBreechVisual extends AbstractBlockEntityVisual<AbstractRotarycannonBreechBlockEntity>
        implements SimpleDynamicVisual {

    private final OrientedInstance ammoContainer;
    private final Direction facing;
    private boolean isFilled = false;
    private Item magazineItem = null;

    public RotaryCannonBreechVisual(VisualizationContext ctx, AbstractRotarycannonBreechBlockEntity blockEntity,
            float partialTick) {
        super(ctx, blockEntity, partialTick);
        this.facing = this.blockState.getValue(BlockStateProperties.FACING);
        Quaternionf q = Axis.YP.rotationDegrees(this.facing.getAxis().isVertical() ? 180.0f : 0.0f);
        this.ammoContainer = this.containerInstancer().createInstance();
        boolean vertical = this.facing.getAxis().isVertical();
        Quaternionf q1;
        if (vertical) {
            float f = this.facing == Direction.UP ? 90.0f : -90.0f;
            q1 = Axis.ZP.rotationDegrees(f);
            q1.mul(Axis.XP.rotationDegrees(f));
        } else {
            q1 = Axis.YP.rotationDegrees(-90.0f - this.facing.toYRot());
        }

        Direction offset = vertical
                ? this.facing.getCounterClockWise(Direction.Axis.Z)
                : this.facing.getClockWise(Direction.Axis.Y);
        Vector3f normal = this.facing == Direction.UP ? offset.getOpposite().step() : offset.step();
        normal.mul(0.625f);
        this.ammoContainer.rotation(q1)
                .position(this.getVisualPosition())
                .translatePosition(normal.x(), normal.y(), normal.z());
        this.isFilled = this.isFilled();
        this.magazineItem = this.getMagazineItem();
        this.updateTransforms(partialTick);
    }

    /** 弹匣内容变了要换模型，换模型要重新 steal 一个实例。 */
    protected void refreshContainer() {
        this.isFilled = this.isFilled();
        this.magazineItem = this.getMagazineItem();
        this.containerInstancer().stealInstance(this.ammoContainer);
    }

    protected Instancer<OrientedInstance> containerInstancer() {
        return this.instancerProvider().instancer(InstanceTypes.ORIENTED, Models.block(this.getAmmoContainerModel()));
    }

    @Override
    public void beginFrame(DynamicVisual.Context ctx) {
        this.updateTransforms(ctx.partialTick());
    }

    private void updateTransforms(float partialTick) {
        this.ammoContainer.setVisible(this.getMagazineItem() instanceof AutocannonAmmoContainerItem);
        if (this.isFilled != this.isFilled() || this.magazineItem != this.getMagazineItem()) {
            this.refreshContainer();
        }
        this.ammoContainer.setChanged();
    }

    @Override
    public void updateLight(float partialTick) {
        this.relight(this.pos, new FlatLit[] { this.ammoContainer });
    }

    @Override
    protected void _delete() {
        this.ammoContainer.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept(this.ammoContainer);
    }

    // ---- 以下两个方法在参考实现里也是死代码（无调用点），保留以保持同构 ----

    @SuppressWarnings("unused")
    private PartialModel getPartialModelForState() {
        return this.blockState.getBlock() instanceof AutocannonBlock cBlock
                ? CBCBlockPartials.autocannonEjectorFor(cBlock.getAutocannonMaterial())
                : CBCBlockPartials.CAST_IRON_AUTOCANNON_EJECTOR;
    }

    private BlockState getAmmoContainerModel() {
        ItemStack item = this.blockEntity.getMagazine();
        if (item != null && !item.isEmpty() && item.getItem() instanceof AutocannonAmmoContainerItem blockItem) {
            BlockState state = blockItem.getBlock().defaultBlockState();
            if (state.hasProperty(AutocannonAmmoContainerBlock.CONTAINER_STATE)) {
                state = state.setValue(AutocannonAmmoContainerBlock.CONTAINER_STATE,
                        AutocannonAmmoContainerBlock.State.getFromFilled(
                                AutocannonAmmoContainerItem.getTotalAmmoCount(item) > 0));
            }
            return state;
        }
        return Blocks.AIR.defaultBlockState();
    }

    private boolean isFilled() {
        return AutocannonAmmoContainerItem.getTotalAmmoCount(this.blockEntity.getMagazine()) > 0;
    }

    private Item getMagazineItem() {
        ItemStack stack = this.blockEntity.getMagazine();
        return stack != null && !stack.isEmpty() ? stack.getItem() : null;
    }
}
