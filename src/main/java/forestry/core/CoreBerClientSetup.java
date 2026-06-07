package forestry.core;

import forestry.api.client.IClientModuleHandler;
import forestry.apiculture.features.ApicultureBlocks;
import forestry.arboriculture.features.ArboricultureBlocks;
import forestry.core.blocks.IColoredBlock;
import forestry.core.config.Constants;
import forestry.core.features.CoreTiles;
import forestry.core.render.*;
import forestry.energy.features.EnergyTiles;
import forestry.factory.features.FactoryTiles;
import forestry.lepidopterology.features.LepidopterologyEntities;
import forestry.lepidopterology.render.ButterflyEntityRenderer;
import forestry.lepidopterology.render.ButterflyModel;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.List;

public final class CoreBerClientSetup implements IClientModuleHandler {
	private static List<BlockTintSource> forestryBlockTints() {
		return List.of(forestryBlockTint(0), forestryBlockTint(1), forestryBlockTint(2));
	}

	private static BlockTintSource forestryBlockTint(int tintIndex) {
		return new BlockTintSource() {
			@Override
			public int color(BlockState state) {
				return 0xffffff;
			}

			@Override
			public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
				Block block = state.getBlock();
				if (block instanceof IColoredBlock coloredBlock) {
					return coloredBlock.colorMultiplier(state, level, pos, tintIndex);
				}
				return 0xffffff;
			}
		};
	}

	@Override
	public void registerEvents(BusGroup modBusGroup) {
		FMLClientSetupEvent.getBus(modBusGroup).addListener(CoreBerClientSetup::onClientSetup);
		ModelEvent.RegisterGeometryLoaders.BUS.addListener(BeeModelSetup::registerGeometryLoaders);
		ModelEvent.ModifyBakingResult.BUS.addListener(BeeModelSetup::onModifyBakingResult);
		EntityRenderersEvent.RegisterLayerDefinitions.BUS.addListener(CoreBerClientSetup::registerLayerDefinitions);
		EntityRenderersEvent.RegisterRenderers.BUS.addListener(CoreBerClientSetup::registerRenderers);
		RegisterColorHandlersEvent.Block.BUS.addListener(CoreBerClientSetup::registerBlockColors);
	}

	private static void onClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(ForestryColoredItemTintSource::register);
	}

	private static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(ForestryModelLayers.ANALYZER_LAYER, RenderAnalyzer::createBodyLayer);
		event.registerLayerDefinition(ForestryModelLayers.MACHINE_LAYER, RenderMachine::createBodyLayer);
		event.registerLayerDefinition(ForestryModelLayers.NATURALIST_CHEST_LAYER, RenderNaturalistChest::createBodyLayer);
		event.registerLayerDefinition(ForestryModelLayers.ESCRITOIRE_LAYER, RenderEscritoire::createBodyLayer);
		event.registerLayerDefinition(ForestryModelLayers.MILL_LAYER, RenderMill::createBodyLayer);
		event.registerLayerDefinition(ForestryModelLayers.ENGINE_LAYER, RenderEngine::createBodyLayer);
		event.registerLayerDefinition(ForestryModelLayers.BUTTERFLY_LAYER, ButterflyModel::createLayer);
	}

	private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(CoreTiles.ANALYZER.tileType(), RenderAnalyzer::new);
		event.registerBlockEntityRenderer(CoreTiles.ESCRITOIRE.tileType(), RenderEscritoire::new);
		event.registerBlockEntityRenderer(CoreTiles.APIARIST_CHEST.tileType(), ctx -> new RenderNaturalistChest(ctx, "apiaristchest"));
		event.registerBlockEntityRenderer(CoreTiles.ARBORIST_CHEST.tileType(), ctx -> new RenderNaturalistChest(ctx, "arbchest"));
		event.registerBlockEntityRenderer(CoreTiles.LEPIDOPTERIST_CHEST.tileType(), ctx -> new RenderNaturalistChest(ctx, "lepichest"));

		event.registerBlockEntityRenderer(EnergyTiles.CLOCKWORK_ENGINE.tileType(), ctx -> new RenderEngine(ctx, Constants.TEXTURE_PATH_BLOCK + "/engine_clock_"));
		event.registerBlockEntityRenderer(EnergyTiles.BIOGAS_ENGINE.tileType(), ctx -> new RenderEngine(ctx, Constants.TEXTURE_PATH_BLOCK + "/engine_bronze_"));
		event.registerBlockEntityRenderer(EnergyTiles.PEAT_ENGINE.tileType(), ctx -> new RenderEngine(ctx, Constants.TEXTURE_PATH_BLOCK + "/engine_copper_"));

		event.registerBlockEntityRenderer(FactoryTiles.BOTTLER.tileType(), ctx -> new RenderMachine(ctx, Constants.TEXTURE_PATH_BLOCK + "/bottler_"));
		event.registerBlockEntityRenderer(FactoryTiles.CARPENTER.tileType(), ctx -> new RenderMachine(ctx, Constants.TEXTURE_PATH_BLOCK + "/carpenter_"));
		event.registerBlockEntityRenderer(FactoryTiles.CENTRIFUGE.tileType(), ctx -> new RenderMachine(ctx, Constants.TEXTURE_PATH_BLOCK + "/centrifuge_"));
		event.registerBlockEntityRenderer(FactoryTiles.FERMENTER.tileType(), ctx -> new RenderMachine(ctx, Constants.TEXTURE_PATH_BLOCK + "/fermenter_"));
		event.registerBlockEntityRenderer(FactoryTiles.MOISTENER.tileType(), ctx -> new RenderMachine(ctx, Constants.TEXTURE_PATH_BLOCK + "/moistener_"));
		event.registerBlockEntityRenderer(FactoryTiles.SQUEEZER.tileType(), ctx -> new RenderMachine(ctx, Constants.TEXTURE_PATH_BLOCK + "/squeezer_"));
		event.registerBlockEntityRenderer(FactoryTiles.STILL.tileType(), ctx -> new RenderMachine(ctx, Constants.TEXTURE_PATH_BLOCK + "/still_"));
		event.registerBlockEntityRenderer(FactoryTiles.RAINMAKER.tileType(), ctx -> new RenderMill(ctx, Constants.TEXTURE_PATH_BLOCK + "/rainmaker_"));

		event.registerEntityRenderer(LepidopterologyEntities.BUTTERFLY.entityType(), ButterflyEntityRenderer::new);
	}

	private static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
		List<BlockTintSource> tints = forestryBlockTints();
		event.register(tints, ApicultureBlocks.BEE_COMB.blockArray());
		event.register(tints, ArboricultureBlocks.LEAVES.block());
		event.register(tints, ArboricultureBlocks.LEAVES_DEFAULT.blockArray());
		event.register(tints, ArboricultureBlocks.LEAVES_DEFAULT_FRUIT.blockArray());
		event.register(tints, ArboricultureBlocks.LEAVES_DECORATIVE.blockArray());
	}
}
