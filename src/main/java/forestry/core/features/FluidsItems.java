package forestry.core.features;

import forestry.api.modules.ForestryModuleIds;
import forestry.core.items.definitions.EnumContainerType;
import forestry.modules.features.FeatureItemGroup;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;
import forestry.modules.features.RegistrationContext;
import net.minecraft.world.item.Item;

@FeatureProvider
public class FluidsItems {
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ForestryModuleIds.FLUIDS);

	// ItemFluidContainerForestry pending Phase 5 fluid item API migration
	public static final FeatureItemGroup<Item, EnumContainerType> CONTAINERS = REGISTRY.itemGroup(type -> new Item(RegistrationContext.itemProperties()), EnumContainerType.values()).create();
}
