package forestry.core.render;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IGenome;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import java.util.List;

/**
 * Stub until particle system is migrated to MC 26.1 Codec-based ParticleOptions.
 */
public final class ParticleRender {
	private ParticleRender() {
	}

	public static boolean shouldSpawnParticle(Level world) {
		return false;
	}

	public static void addBeeHiveFX(IBeeHousing housing, IGenome genome, List<BlockPos> flowerPositions) {
	}

	public static void addEntityHoneyDustFX(LevelAccessor world, double x, double y, double z) {
	}

	public static void addEntityExplodeFX(Level world, double x, double y, double z) {
	}

	public static void addEntitySmokeFX(Level world, double x, double y, double z) {
	}

	public static void addPortalFx(Level world, BlockPos pos, net.minecraft.util.RandomSource rand) {
	}

	public static void addEntityIgnitionFX(Level world, double x, double y, double z) {
	}

	public static void addEntityPotionFX(Level world, double x, double y, double z, int color) {
	}

	public static void addEntitySnowFX(Level world, double x, double y, double z) {
	}
}
