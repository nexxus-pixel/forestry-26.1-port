package forestry.arboriculture;

import forestry.api.client.IClientModuleHandler;
import forestry.arboriculture.features.ArboricultureTiles;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.StandingSignRenderer;
import net.minecraft.world.level.block.HangingSignBlock;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;

public final class ArboricultureSignClientSetup implements IClientModuleHandler {
	@Override
	public void registerEvents(BusGroup modBusGroup) {
		EntityRenderersEvent.RegisterLayerDefinitions.BUS.addListener(ArboricultureSignClientSetup::registerSignLayers);
		EntityRenderersEvent.RegisterRenderers.BUS.addListener(ArboricultureSignClientSetup::registerSignRenderers);
		RegisterClientReloadListenersEvent.BUS.addListener(ArboricultureSignClientSetup::registerWoodTypes);
	}

	private static void registerWoodTypes(RegisterClientReloadListenersEvent event) {
		for (ForestryWoodType type : ForestryWoodType.VALUES) {
			Sheets.addWoodType(type.getWoodType());
		}
	}

	private static void registerSignLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
		for (ForestryWoodType type : ForestryWoodType.VALUES) {
			WoodType woodType = type.getWoodType();
			event.registerLayerDefinition(ModelLayers.createStandingSignModelName(woodType), () -> StandingSignRenderer.createSignLayer(true));
			event.registerLayerDefinition(ModelLayers.createWallSignModelName(woodType), () -> StandingSignRenderer.createSignLayer(false));
			for (HangingSignBlock.Attachment attachment : HangingSignBlock.Attachment.values()) {
				event.registerLayerDefinition(ModelLayers.createHangingSignModelName(woodType, attachment), () -> HangingSignRenderer.createHangingSignLayer(attachment));
			}
		}
	}

	private static void registerSignRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(ArboricultureTiles.SIGN.tileType(), StandingSignRenderer::new);
		event.registerBlockEntityRenderer(ArboricultureTiles.HANGING_SIGN.tileType(), HangingSignRenderer::new);
	}
}
