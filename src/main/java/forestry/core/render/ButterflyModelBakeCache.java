package forestry.core.render;

import forestry.api.lepidopterology.genetics.IButterflySpecies;

import java.util.IdentityHashMap;
import java.util.Map;

public final class ButterflyModelBakeCache {
	private static IdentityHashMap<IButterflySpecies, GeneticsRenderData> cache = new IdentityHashMap<>();

	private ButterflyModelBakeCache() {
	}

	public static void clear() {
		cache = new IdentityHashMap<>();
	}

	public static void put(IdentityHashMap<IButterflySpecies, GeneticsRenderData> data) {
		cache = data;
	}

	public static Map<IButterflySpecies, GeneticsRenderData> get() {
		return cache;
	}
}
