package forestry.core.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import javax.annotation.Nullable;

/**
 * Helpers for CompoundTag accessors that return Optional in Minecraft 26.1.
 */
public final class CompoundTagUtil {
	private CompoundTagUtil() {
	}

	public static int getInt(CompoundTag tag, String key) {
		return tag.getInt(key).orElse(0);
	}

	public static int getInt(CompoundTag tag, String key, int defaultValue) {
		return tag.getInt(key).orElse(defaultValue);
	}

	public static boolean getBoolean(CompoundTag tag, String key) {
		return tag.getBoolean(key).orElse(false);
	}

	public static boolean getBoolean(CompoundTag tag, String key, boolean defaultValue) {
		return tag.getBoolean(key).orElse(defaultValue);
	}

	public static float getFloat(CompoundTag tag, String key) {
		return tag.getFloat(key).orElse(0f);
	}

	public static double getDouble(CompoundTag tag, String key) {
		return tag.getDouble(key).orElse(0d);
	}

	public static String getString(CompoundTag tag, String key) {
		return tag.getString(key).orElse("");
	}

	public static CompoundTag getCompound(CompoundTag tag, String key) {
		return tag.getCompound(key).orElseGet(CompoundTag::new);
	}

	@Nullable
	public static CompoundTag getCompoundOrNull(CompoundTag tag, String key) {
		return tag.getCompound(key).orElse(null);
	}

	public static ListTag getList(CompoundTag tag, String key) {
		return tag.getList(key).orElseGet(ListTag::new);
	}

	public static int[] getIntArray(CompoundTag tag, String key) {
		return tag.getIntArray(key).orElse(new int[0]);
	}

	public static byte getByte(CompoundTag tag, String key) {
		return tag.getByte(key).orElse((byte) 0);
	}

	public static long getLong(CompoundTag tag, String key) {
		return tag.getLong(key).orElse(0L);
	}

	public static short getShort(CompoundTag tag, String key) {
		return tag.getShort(key).orElse((short) 0);
	}

	public static short getShort(CompoundTag tag, String key, short defaultValue) {
		return tag.getShortOr(key, defaultValue);
	}

	public static CompoundTag getCompound(ListTag list, int index) {
		return list.getCompoundOrEmpty(index);
	}

	public static String getString(ListTag list, int index) {
		return list.getString(index).orElse("");
	}
}
