package forestry.core;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import forestry.api.ForestryConstants;
import forestry.api.IForestryApi;
import forestry.api.client.IClientModuleHandler;
import forestry.api.modules.ForestryModule;
import forestry.api.modules.ForestryModuleIds;
import forestry.api.modules.IForestryModule;
import forestry.api.modules.IPacketRegistry;
import forestry.apiculture.features.ApicultureItems;
import forestry.apiculture.items.ItemPollenCluster;
import forestry.apiimpl.plugin.PluginManager;
import forestry.arboriculture.features.ArboricultureBlocks;
import forestry.arboriculture.features.ArboricultureItems;
import forestry.arboriculture.loot.GrafterLootModifier;
import forestry.core.blocks.TileStreamUpdateTracker;
import forestry.core.climate.ForestryClimateManager;
import forestry.core.EventHandlerCore;
import forestry.core.multiblock.MultiblockServerTickHandler;
import forestry.core.commands.DiagnosticsCommand;
import forestry.core.commands.DumpCommand;
import forestry.core.features.CoreItems;
import forestry.core.items.definitions.EnumCraftingMaterial;
import forestry.core.network.PacketIdClient;
import forestry.core.network.PacketIdServer;
import forestry.core.network.packets.*;
import forestry.core.owner.GameProfileDataSerializer;
import forestry.core.recipes.RecipeManagers;
import forestry.core.utils.ModUtil;
import forestry.core.utils.NetworkUtil;
import forestry.lepidopterology.features.LepidopterologyItems;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleManager;
import forestry.modules.ModuleUtil;
import forestry.modules.features.FeatureItem;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.common.util.Result;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.util.List;
import java.util.function.Consumer;

@ForestryModule
public class ModuleCore extends BlankForestryModule {
	@Override
	public Identifier getId() {
		return ForestryModuleIds.CORE;
	}

	@Override
	public void registerEvents(BusGroup modBusGroup) {
		FMLCommonSetupEvent.getBus(modBusGroup).addListener(ModuleCore::onCommonSetup);
		RegisterEvent.getBus(modBusGroup).addListener(ModuleCore::registerGlobalLootModifiers);
		ModuleUtil.loadFeatureProviders();
		EntityItemPickupEvent.BUS.addListener(ModuleCore::onItemPickup);
		TickEvent.LevelTickEvent.Post.BUS.addListener(ModuleCore::onLevelTick);
		TagsUpdatedEvent.BUS.addListener(ModuleCore::onTagsUpdated);
		AddReloadListenerEvent.BUS.addListener(ModuleCore::registerReloadListeners);
		RegisterCommandsEvent.BUS.addListener(ModuleCore::registerCommands);
		MultiblockServerTickHandler.register();
		EventHandlerCore.register();

		PluginManager.registerAsyncException(modBusGroup);
	}

	@Override
	public void registerClientHandler(Consumer<IClientModuleHandler> registrar) {
		registrar.accept(new CoreBerClientSetup());
		registrar.accept(new ForestryMenuClientSetup());
	}

	private static void onCommonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			postItemRegistry();
			((ForestryModuleManager) IForestryApi.INSTANCE.getModuleManager()).setupApi();
			PluginManager.registerCircuits();
			EntityDataSerializers.registerSerializer(GameProfileDataSerializer.INSTANCE);
			registerComposts();
		});
	}

	private static void registerComposts() {
		// cast avoids stupid typos (IItemLike can be different than Item, then composter will not work)
		@SuppressWarnings({"unchecked", "rawtypes"})
		Object2FloatMap<Item> composts = ((Object2FloatMap) ComposterBlock.COMPOSTABLES);

		for (FeatureItem<?> fruit : CoreItems.FRUITS.getFeatures()) {
			composts.put(fruit.item(), 0.65f);
		}
		composts.put(CoreItems.MOULDY_WHEAT.item(), 0.65f);
		composts.put(CoreItems.DECAYING_WHEAT.item(), 0.65f);
		composts.put(CoreItems.MULCH.item(), 0.65f);
		composts.put(CoreItems.ASH.item(), 0.65f);
		composts.put(CoreItems.CRAFTING_MATERIALS.item(EnumCraftingMaterial.WOOD_PULP), 0.65f);
		composts.put(CoreItems.PEAT.item(), 0.75f);
		composts.put(CoreItems.COMPOST.item(), 1f);
		for (ItemPollenCluster pollen : ApicultureItems.POLLEN_CLUSTER.getItems()) {
			composts.put(pollen, 0.3f);
		}
		composts.put(ArboricultureItems.SAPLING.item(), 0.3f);
		composts.put(ArboricultureItems.POLLEN_FERTILE.item(), 0.3f);
		for (BlockItem leaves : ArboricultureBlocks.LEAVES_DECORATIVE.getItems()) {
			composts.put(leaves, 0.3f);
		}
		composts.put(LepidopterologyItems.COCOON_GE.item(), 0.3f);
	}

	private static void registerGlobalLootModifiers(RegisterEvent event) {
		event.register(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, helper -> {
			helper.register(ForestryConstants.forestry("grafter_modifier"), GrafterLootModifier.CODEC);
		});
	}

	private static void postItemRegistry() {
		PluginManager.registerGenetics();
		PluginManager.registerFarming();
		PluginManager.registerPollen();
	}

	private static void onItemPickup(EntityItemPickupEvent event) {
		if (event.getResult() == Result.ALLOW) {
			return;
		}
		PickupHandlerCore.onItemPickup(event.getEntity(), event.getItem());
	}

	private static void onLevelTick(TickEvent.LevelTickEvent.Post event) {
		TileStreamUpdateTracker.syncVisualUpdates();
	}

	private static void onTagsUpdated(TagsUpdatedEvent event) {
		if (event.shouldUpdateStaticData()) {
			event.getRegistryAccess().lookup(Registries.BIOME).ifPresent(registry -> ((ForestryClimateManager) IForestryApi.INSTANCE.getClimateManager()).onBiomesReloaded(registry));
		}
	}

	private static void registerReloadListeners(AddReloadListenerEvent event) {
		event.addListener((state, backgroundExecutor, prepBarrier, gameExecutor) -> prepBarrier.wait(null).thenRunAsync(() -> {
			RecipeManagers.invalidateCaches();
			NetworkUtil.sendToAllPlayers(new RecipeCachePacket());
		}, gameExecutor));
	}

	private static void registerCommands(RegisterCommandsEvent event) {
		LiteralArgumentBuilder<CommandSourceStack> forestryCommand = LiteralArgumentBuilder.literal("forestry");

		forestryCommand.then(DiagnosticsCommand.register());
		forestryCommand.then(DumpCommand.register());

		for (IForestryModule module : IForestryApi.INSTANCE.getModuleManager().getModulesForMod(ForestryConstants.MOD_ID)) {
			if (module instanceof BlankForestryModule forestryModule) {
				forestryModule.addToRootCommand(forestryCommand);
			}
		}

		event.getDispatcher().register(forestryCommand);
	}

	@Override
	public boolean isCore() {
		return true;
	}

	@Override
	public List<Identifier> getModuleDependencies() {
		return List.of();
	}

	@Override
	public void registerPackets(IPacketRegistry registry) {
		registry.serverbound(PacketIdServer.GUI_SELECTION_REQUEST, PacketGuiSelectRequest.class, PacketGuiSelectRequest::decode, PacketGuiSelectRequest::handle);
		registry.serverbound(PacketIdServer.PIPETTE_CLICK, PacketPipetteClick.class, PacketPipetteClick::decode, PacketPipetteClick::handle);
		registry.serverbound(PacketIdServer.CHIPSET_CLICK, PacketChipsetClick.class, PacketChipsetClick::decode, PacketChipsetClick::handle);
		registry.serverbound(PacketIdServer.SOLDERING_IRON_CLICK, PacketSolderingIronClick.class, PacketSolderingIronClick::decode, PacketSolderingIronClick::handle);

		registry.clientbound(PacketIdClient.ERROR_UPDATE, PacketErrorUpdate.class, PacketErrorUpdate::decode, PacketErrorUpdate::handle);
		registry.clientbound(PacketIdClient.GUI_UPDATE, PacketGuiStream.class, PacketGuiStream::decode, PacketGuiStream::handle);
		registry.clientbound(PacketIdClient.GUI_LAYOUT_SELECT, PacketGuiLayoutSelect.class, PacketGuiLayoutSelect::decode, PacketGuiLayoutSelect::handle);
		registry.clientbound(PacketIdClient.GUI_ENERGY, PacketGuiEnergy.class, PacketGuiEnergy::decode, PacketGuiEnergy::handle);
		registry.clientbound(PacketIdClient.SOCKET_UPDATE, PacketSocketUpdate.class, PacketSocketUpdate::decode, PacketSocketUpdate::handle);
		registry.clientbound(PacketIdClient.TILE_FORESTRY_UPDATE, PacketTileStream.class, PacketTileStream::decode, PacketTileStream::handle);
		registry.clientbound(PacketIdClient.TILE_FORESTRY_ACTIVE, PacketActiveUpdate.class, PacketActiveUpdate::decode, PacketActiveUpdate::handle);
		registry.clientbound(PacketIdClient.ITEMSTACK_DISPLAY, PacketItemStackDisplay.class, PacketItemStackDisplay::decode, PacketItemStackDisplay::handle);
		registry.clientbound(PacketIdClient.GENOME_TRACKER_UPDATE, PacketTankLevelUpdate.class, PacketTankLevelUpdate::decode, PacketTankLevelUpdate::handle);
		registry.clientbound(PacketIdClient.TANK_LEVEL_UPDATE, PacketGenomeTrackerSync.class, PacketGenomeTrackerSync::decode, PacketGenomeTrackerSync::handle);
		registry.clientbound(PacketIdClient.RECIPE_CACHE, RecipeCachePacket.class, RecipeCachePacket::decode, RecipeCachePacket::handle);
		registry.clientbound(PacketIdClient.REFRACTORY_WAX_ON, PacketRefractoryWax.class, PacketRefractoryWax::decode, PacketRefractoryWax::handle);
	}

}
