package forestry.core.utils;

import net.minecraft.world.level.Level;

/**
 * Time-of-day helpers for Minecraft 26.1 (Level.isDaytime() removed).
 */
public final class LevelTimeUtil {
	private LevelTimeUtil() {
	}

	public static boolean isDaytime(Level level) {
		return level.isBrightOutside();
	}
}
