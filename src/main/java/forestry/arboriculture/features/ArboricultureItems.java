package forestry.arboriculture.features;

import forestry.api.modules.ForestryModuleIds;
import forestry.arboriculture.ForestryWoodType;
import forestry.arboriculture.items.ItemGrafter;
import forestry.core.items.ItemForestry;
import forestry.modules.features.*;
import net.minecraft.world.item.Item;

@FeatureProvider
public class ArboricultureItems {
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ForestryModuleIds.ARBORICULTURE);

	// ItemGermlingGE pending genetics item API migration
	public static final FeatureItem<Item> SAPLING = REGISTRY.item("sapling");
	public static final FeatureItem<Item> POLLEN_FERTILE = REGISTRY.item("pollen_fertile");
	public static final FeatureItem<ItemGrafter> GRAFTER = REGISTRY.item(() -> new ItemGrafter(9), "grafter");
	public static final FeatureItem<ItemGrafter> GRAFTER_PROVEN = REGISTRY.item(() -> new ItemGrafter(149), "grafter_proven");
	// ItemForestryBoat pending boat entity/renderer migration
	public static final FeatureItemGroup<Item, ForestryWoodType> BOAT = REGISTRY.itemGroup(type -> new Item(RegistrationContext.itemProperties()), ForestryWoodType.VALUES).identifier("boat", FeatureGroup.IdentifierType.SUFFIX).create();
	public static final FeatureItemGroup<Item, ForestryWoodType> CHEST_BOAT = REGISTRY.itemGroup(type -> new Item(RegistrationContext.itemProperties()), ForestryWoodType.VALUES).identifier("chest_boat", FeatureGroup.IdentifierType.SUFFIX).create();

	// MISC
	public static final FeatureItem<ItemForestry> AMBER_SAPLING = REGISTRY.item(ItemForestry::new, "amber_sapling");
}
