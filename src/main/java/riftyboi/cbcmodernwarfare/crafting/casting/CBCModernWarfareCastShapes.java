package riftyboi.cbcmodernwarfare.crafting.casting;

import java.util.function.Supplier;

import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.base.CBCRegistries;
import rbasamoyai.createbigcannons.base.PropertySetter;
import rbasamoyai.createbigcannons.crafting.casting.CannonCastShape;

import riftyboi.cbcmodernwarfare.index.CBCModernWarfareBlocks;

/**
 * 本模组注册的铸型（{@link CannonCastShape}）。
 *
 * <p><b>1.20.1 → 1.21.1 的变化</b>：
 * 1.20.1 用 {@code MappedRegistry} + {@code Registry.register(...)} 的原始写法，
 * 1.21.1 的 {@code Registry.register} 仍可用，但铸型注册表本身由 CBC 的
 * {@link CBCRegistries#cannonCastShapes()} 持有（javap 确认该方法是
 * {@code public static Registry<CannonCastShape> cannonCastShapes()}，未变）。
 *
 * <p><b>本轮的取舍</b>：{@code CannonCastShape} 的构造在 1.20.1 与 1.21.1 一致
 * （两个重载都在：{@code (int fluidSize, int diameter, NonNullSupplier<Block>, PropertySetter...)}
 * 与带 {@code isLarge/texturesCanConnect} 的六参版本）。这里先只注册中型炮的四种铸型；
 * 转管炮的四种（{@code ROTARYCANNON_*}）随 R4b 一起加，因为它们依赖还没迁移的铸型方块。
 */
public class CBCModernWarfareCastShapes {

    /** CBC 的 {@code CannonCastShape} 用「一个锭 = 90 mB」作为体积单位。 */
    private static final int INGOT_SIZE_MB = 90;

    public static final CannonCastShape
            MEDIUMCANNON_BARREL = register("mediumcannon_barrel", new CannonCastShape(3 * INGOT_SIZE_MB, 8,
                    CBCModernWarfareBlocks.MEDIUMCANNON_BARREL_CAST_MOULD, false, false,
                    PropertySetter.of(BlockStateProperties.FACING, Direction.UP))),

            MEDIUMCANNON_BARREL_FLANGED = register("mediumcannon_barrel_flanged",
                    new CannonCastShape(3 * INGOT_SIZE_MB, 8, () -> Blocks.AIR, false, false)),

            MEDIUMCANNON_BREECH = register("mediumcannon_breech", new CannonCastShape(4 * INGOT_SIZE_MB, 16,
                    CBCModernWarfareBlocks.MEDIUMCANNON_BREECH_CAST_MOULD, false, false,
                    PropertySetter.of(BlockStateProperties.FACING, Direction.UP))),

            MEDIUMCANNON_RECOIL_BARREL = register("mediumcannon_recoil_barrel",
                    new CannonCastShape(4 * INGOT_SIZE_MB, 10,
                            CBCModernWarfareBlocks.MEDIUMCANNON_RECOIL_BARREL_CAST_MOULD, false, false,
                            PropertySetter.of(BlockStateProperties.FACING, Direction.UP)));

    // TODO(R4b): ROTARYCANNON_BARREL / _FLANGED / _BREECH / _BEARING —— 需要先迁移
    //   ROTARYCANNON_*_CAST_MOULD 方块，属于转管炮那一批。

    public static void register() {
        // 静态字段初始化即注册；本方法只用于强制类加载。
    }

    private static CannonCastShape register(String id, CannonCastShape shape) {
        return Registry.register(CBCRegistries.cannonCastShapes(), CreateBigCannons.resource(id), shape);
    }

    /** 供铸型方块引用：把「形状」包装成 supplier，避免静态初始化顺序问题。 */
    public static Supplier<CannonCastShape> barrelShape() {
        return () -> MEDIUMCANNON_BARREL;
    }

    /** 未使用的占位，保持与 1.20.1 相同的类形状。 */
    static BlockState applyTo(CannonCastShape shape, BlockState state) {
        return shape.applyTo(state);
    }

    private CBCModernWarfareCastShapes() {}
}
