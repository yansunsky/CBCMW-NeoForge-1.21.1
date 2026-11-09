package riftyboi.cbcmodernwarfare.cannon_control.contraption;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import dev.ryanhcode.sable.Sable;
import rbasamoyai.createbigcannons.CBCModsNeoForge;
import rbasamoyai.createbigcannons.compat.sable.SableCompat;

/**
 * 转管炮的 Sable（子维度 / 载具物理）兼容门面。
 *
 * <h2>为什么需要这一层</h2>
 * CBC 5.11.7 在 1.21.1 上把「炮装在 Sable 子维度载具上」当成它自己该支持的能力：
 * {@code compat/sable/} 下有 8 个类、{@code mixin/compat/sable/} 下有 4 个 mixin，
 * 并且 {@code mods.toml} 里声明 {@code modId="sable", type="optional", versionRange="[2.0.0,)"}。
 * 我们是它的附属，跟着做同一套兼容才是「贴合前置」。
 *
 * <h2>接入范式：跟 CBC，不跟参考实现</h2>
 * CBC 自己的写法（{@code AbstractCannonProjectile#onImpact}）是：
 * <pre>{@code
 * if (CBCModsNeoForge.SABLE.isLoaded()) {
 *     SableCompat.impactContraption(level(), hitResult, impactResult, projectileContext);
 * }
 * }</pre>
 * 即「<b>编译期静态引用 + 调用点 isLoaded 守卫</b>」。守卫为假时 Sable 的类根本不会被加载，
 * 所以没装 Sable 的玩家不会 {@code NoClassDefFoundError}。
 *
 * <p>参考实现（refdec）改用了 {@code Class.forName} + {@code Method.invoke} 的纯反射方案，
 * 那是因为它保留了 Architectury 平台层、不想把 Sable 写进构建。我们是单平台 NeoForge，
 * 条件与 CBC 相同，因此**选择 CBC 的方案**：类型安全、可读、且与前置行为一致。
 * 反射方案不采用。
 *
 * <h2>位移变换的语义</h2>
 * {@code toGlobalVector} 给出的是「世界坐标下的位置」，但当炮装在 Sable 子维度里时，
 * 弹体、抛壳、粒子的真实落点必须再经过子维度的 pose 变换。
 * {@link SableCompat#transformFromShip(Level, Vec3, Vec3)} 就是干这个的：
 * 第三个参数是「用来判断在不在子维度里的参考点」，返回变换后的位置；不在子维度时原样返回。
 *
 * <p>本类所有方法在 Sable 未安装时都是**恒等**的（返回传入值 / 零），调用方不需要额外判空。
 */
public final class RotarycannonSableCompat {

	private RotarycannonSableCompat() {}

	/** Sable 是否已加载。 */
	public static boolean isLoaded() {
		return CBCModsNeoForge.SABLE.isLoaded();
	}

	/**
	 * 把一个「在世界坐标里算出来的位置」投影出 Sable 子维度。
	 *
	 * <p>参考点用 {@code root}：一般传 contraption 的中心位置。
	 * 不在子维度里时原样返回 {@code pos}。
	 */
	public static Vec3 projectOutOfSubLevel(Level level, Vec3 pos, Vec3 root) {
		if (!isLoaded()) {
			return pos;
		}
		return SableCompat.transformFromShip(level, pos, root);
	}

	/**
	 * 这个位置是不是在 Sable 子维度里。
	 *
	 * <p>用 {@code Sable.HELPER.getContaining} 判断 —— 与 CBC 的
	 * {@code SableCompat.groundProjectile} 内部判据一致。
	 */
	public static boolean isInSubLevel(Level level, Vec3 pos) {
		if (!isLoaded()) {
			return false;
		}
		return Sable.HELPER.getContaining(level, pos) != null;
	}

	/**
	 * 取该位置处子维度的速度（世界坐标系），用于让弹体继承载具速度。
	 *
	 * <p>不在子维度里、或 Sable 未安装时返回 {@link Vec3#ZERO}。
	 */
	public static Vec3 getVelocity(Level level, Vec3 pos) {
		if (!isLoaded()) {
			return Vec3.ZERO;
		}
		Vec3 velocity = Sable.HELPER.getVelocity(level, pos);
		return velocity == null ? Vec3.ZERO : velocity;
	}
}
