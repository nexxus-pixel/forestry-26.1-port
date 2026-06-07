package forestry.core.utils;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.function.Consumer;

/**
 * Bridge between legacy CompoundTag tile-entity IO and Minecraft 26.1 ValueInput/ValueOutput.
 */
public final class ValueIoForestry {
	public static final String LEGACY_KEY = "forestry";

	private ValueIoForestry() {
	}

	public static void readLegacy(ValueInput input, Consumer<CompoundTag> reader) {
		input.read(LEGACY_KEY, CompoundTag.CODEC).ifPresent(reader);
	}

	public static void writeLegacy(ValueOutput output, CompoundTag tag) {
		if (!tag.isEmpty()) {
			output.store(LEGACY_KEY, CompoundTag.CODEC, tag);
		}
	}

	public static void readChild(ValueInput input, String key, Codec<CompoundTag> codec, Consumer<CompoundTag> reader) {
		input.read(key, codec).ifPresent(reader);
	}

	public static void writeChild(ValueOutput output, String key, Codec<CompoundTag> codec, CompoundTag tag) {
		if (!tag.isEmpty()) {
			output.store(key, codec, tag);
		}
	}
}
