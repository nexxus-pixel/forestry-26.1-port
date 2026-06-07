package forestry.core.features;

import forestry.api.modules.ForestryModuleIds;
import forestry.modules.features.FeatureItem;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;
import net.minecraft.world.item.Item;

@FeatureProvider
public class BackpackItems {
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ForestryModuleIds.STORAGE);

	public static final FeatureItem<Item> APIARIST_BACKPACK = REGISTRY.item("apiarist_bag");
	public static final FeatureItem<Item> ARBORIST_BACKPACK = REGISTRY.item("arborist_bag");
	public static final FeatureItem<Item> LEPIDOPTERIST_BACKPACK = REGISTRY.item("lepidopterist_bag");
	public static final FeatureItem<Item> MINER_BACKPACK = REGISTRY.item("miner_bag");
	public static final FeatureItem<Item> MINER_BACKPACK_T_2 = REGISTRY.item("miner_bag_woven");
	public static final FeatureItem<Item> DIGGER_BACKPACK = REGISTRY.item("digger_bag");
	public static final FeatureItem<Item> DIGGER_BACKPACK_T_2 = REGISTRY.item("digger_bag_woven");
	public static final FeatureItem<Item> FORESTER_BACKPACK = REGISTRY.item("forester_bag");
	public static final FeatureItem<Item> FORESTER_BACKPACK_T_2 = REGISTRY.item("forester_bag_woven");
	public static final FeatureItem<Item> HUNTER_BACKPACK = REGISTRY.item("hunter_bag");
	public static final FeatureItem<Item> HUNTER_BACKPACK_T_2 = REGISTRY.item("hunter_bag_woven");
	public static final FeatureItem<Item> ADVENTURER_BACKPACK = REGISTRY.item("adventurer_bag");
	public static final FeatureItem<Item> ADVENTURER_BACKPACK_T_2 = REGISTRY.item("adventurer_bag_woven");
	public static final FeatureItem<Item> BUILDER_BACKPACK = REGISTRY.item("builder_bag");
	public static final FeatureItem<Item> BUILDER_BACKPACK_T_2 = REGISTRY.item("builder_bag_woven");
}
