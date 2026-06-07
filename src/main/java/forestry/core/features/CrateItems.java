package forestry.core.features;

import forestry.api.modules.ForestryModuleIds;
import forestry.modules.features.FeatureItem;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;
import net.minecraft.world.item.Item;

import java.util.Collections;
import java.util.List;

@FeatureProvider
public class CrateItems {
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ForestryModuleIds.STORAGE);

	public static final FeatureItem<Item> CRATE = REGISTRY.item("crate");

	public static List<FeatureItem<Item>> getCrates() {
		return Collections.emptyList();
	}
}
