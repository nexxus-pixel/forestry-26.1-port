package forestry.core.features;

import forestry.api.modules.ForestryModuleIds;
import forestry.modules.features.*;
import net.minecraft.world.item.Item;

@FeatureProvider
public class MailItems {
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ForestryModuleIds.MAIL);

	public static final FeatureItem<Item> STAMP = REGISTRY.item("stamp");
	public static final FeatureItem<Item> LETTER = REGISTRY.item("letter");
	public static final FeatureItem<Item> CATALOGUE = REGISTRY.item("catalogue");
}
