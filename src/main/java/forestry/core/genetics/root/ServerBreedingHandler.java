package forestry.core.genetics.root;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import forestry.api.ForestryConstants;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.ISpeciesType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerBreedingHandler implements BreedingTrackerManager.SidedHandler {
	private static final Map<String, SavedDataType<SavedData>> TRACKER_TYPES = new ConcurrentHashMap<>();

	@Override
	@SuppressWarnings("unchecked")
	public <T extends IBreedingTracker> T getTracker(ISpeciesType<?, ?> type, LevelAccessor level, @Nullable GameProfile profile) {
		String filename = type.getBreedingTrackerFile(profile);
		ServerLevel overworld = level.getServer().getLevel(Level.OVERWORLD);
		SavedDataType<SavedData> dataType = getSavedDataType(type, filename);
		T tracker = (T) overworld.getDataStorage().computeIfAbsent(dataType);
		type.initializeBreedingTracker(tracker, overworld, profile);
		return tracker;
	}

	private static SavedDataType<SavedData> getSavedDataType(ISpeciesType<?, ?> type, String filename) {
		return TRACKER_TYPES.computeIfAbsent(type.id() + "|" + filename, key -> {
			Identifier id = Identifier.fromNamespaceAndPath(ForestryConstants.MOD_ID, sanitizeFilename(filename));
			return new SavedDataType<>(id, () -> (SavedData) type.createBreedingTracker(), createCodec(type), DataFixTypes.SAVED_DATA_MAP_DATA);
		});
	}

	private static Codec<SavedData> createCodec(ISpeciesType<?, ?> type) {
		return CompoundTag.CODEC.xmap(
			tag -> (SavedData) type.createBreedingTracker(tag),
			saved -> {
				CompoundTag tag = new CompoundTag();
				((IBreedingTracker) saved).writeToNbt(tag);
				return tag;
			}
		);
	}

	private static String sanitizeFilename(String filename) {
		return filename.toLowerCase().replaceAll("[^a-z0-9/._-]", "_");
	}
}
