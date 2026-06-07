package forestry.core.render;

import forestry.api.arboriculture.ITreeSpecies;

import java.util.IdentityHashMap;
import java.util.Map;

public final class SaplingModelBakeCache {
	private static IdentityHashMap<ITreeSpecies, GeneticsRenderData> itemCache = new IdentityHashMap<>();
	private static IdentityHashMap<ITreeSpecies, GeneticsRenderData> blockCache = new IdentityHashMap<>();

	private SaplingModelBakeCache() {
	}

	public static void clear() {
		itemCache = new IdentityHashMap<>();
		blockCache = new IdentityHashMap<>();
	}

	public static void put(IdentityHashMap<ITreeSpecies, GeneticsRenderData> items, IdentityHashMap<ITreeSpecies, GeneticsRenderData> blocks) {
		itemCache = items;
		blockCache = blocks;
	}

	public static Map<ITreeSpecies, GeneticsRenderData> getItems() {
		return itemCache;
	}

	public static Map<ITreeSpecies, GeneticsRenderData> getBlocks() {
		return blockCache;
	}
}
