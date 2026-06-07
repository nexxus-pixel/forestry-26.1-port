package forestry.core.render;

import forestry.api.genetics.ILifeStage;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.geometry.QuadCollection;

import java.util.IdentityHashMap;
import java.util.Map;

public final class BeeModelBakeCache {
	private static final IdentityHashMap<ILifeStage, IdentityHashMap<forestry.api.apiculture.genetics.IBeeSpecies, SpeciesRenderData>> CACHE = new IdentityHashMap<>();

	private BeeModelBakeCache() {
	}

	public static void clear() {
		CACHE.clear();
	}

	public static void put(ILifeStage stage, IdentityHashMap<forestry.api.apiculture.genetics.IBeeSpecies, SpeciesRenderData> data) {
		CACHE.put(stage, data);
	}

	public static Map<forestry.api.apiculture.genetics.IBeeSpecies, SpeciesRenderData> get(ILifeStage stage) {
		return CACHE.getOrDefault(stage, new IdentityHashMap<>());
	}

	public record SpeciesRenderData(QuadCollection quads, ModelRenderProperties properties) {
	}
}
