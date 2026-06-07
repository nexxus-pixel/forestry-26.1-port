package forestry.core.render;

import forestry.api.ForestryConstants;
import forestry.api.apiculture.genetics.BeeLifeStage;
import forestry.apiculture.features.ApicultureItems;
import forestry.arboriculture.features.ArboricultureItems;
import forestry.lepidopterology.features.LepidopterologyItems;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.resources.Identifier;
import net.minecraftforge.client.event.ModelEvent;

import java.util.Map;

public final class BeeModelSetup {
	private BeeModelSetup() {
	}

	public static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
		ForestryColoredItemTintSource.register();
		forestry.apiimpl.plugin.PluginManager.registerClient();
		BeeModelBakeCache.clear();
		ButterflyModelBakeCache.clear();
		SaplingModelBakeCache.clear();
		event.register(ForestryConstants.forestry("bee_ge"), new forestry.apiculture.models.ModelBee.Loader());
		event.register(ForestryConstants.forestry("butterfly_ge"), new ModelButterfly.Loader());
		event.register(ForestryConstants.forestry("sapling_ge"), new ModelSapling.Loader());
	}

	public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
		Map<Identifier, ItemModel> itemModels = event.getResults().itemStackModels();

		replaceBeeModel(itemModels, ApicultureItems.BEE_DRONE.id(), BeeLifeStage.DRONE);
		replaceBeeModel(itemModels, ApicultureItems.BEE_PRINCESS.id(), BeeLifeStage.PRINCESS);
		replaceBeeModel(itemModels, ApicultureItems.BEE_QUEEN.id(), BeeLifeStage.QUEEN);
		replaceBeeModel(itemModels, ApicultureItems.BEE_LARVAE.id(), BeeLifeStage.LARVAE);

		if (!ButterflyModelBakeCache.get().isEmpty()) {
			itemModels.put(LepidopterologyItems.BUTTERFLY_GE.id(), new ButterflyItemModel());
		}

		if (!SaplingModelBakeCache.getItems().isEmpty()) {
			itemModels.put(ArboricultureItems.SAPLING.id(), new SaplingItemModel());
		}
	}

	private static void replaceBeeModel(Map<Identifier, ItemModel> itemModels, Identifier itemId, BeeLifeStage stage) {
		if (!BeeModelBakeCache.get(stage).isEmpty()) {
			itemModels.put(itemId, new BeeItemModel(stage));
		}
	}
}
