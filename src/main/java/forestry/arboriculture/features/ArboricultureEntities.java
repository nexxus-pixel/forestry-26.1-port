package forestry.arboriculture.features;

import forestry.api.modules.ForestryModuleIds;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

@FeatureProvider
public class ArboricultureEntities {
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ForestryModuleIds.ARBORICULTURE);

	// ForestryBoat/ForestryChestBoat pending boat entity/renderer migration (Phase 4)
}
