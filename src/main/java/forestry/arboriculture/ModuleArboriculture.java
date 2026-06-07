package forestry.arboriculture;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.ITreeSpeciesType;
import forestry.api.arboriculture.genetics.TreeLifeStage;
import forestry.api.genetics.IIndividual;
import forestry.api.modules.ForestryModule;
import forestry.api.modules.ForestryModuleIds;
import forestry.api.modules.IPacketRegistry;
import forestry.arboriculture.commands.CommandTree;
import forestry.arboriculture.features.ArboricultureItems;
import forestry.arboriculture.network.PacketRipeningUpdate;
import forestry.core.genetics.capability.IndividualHandlerItem;
import forestry.core.network.PacketIdClient;
import forestry.core.utils.LootTableUtil;
import forestry.core.utils.SpeciesUtil;
import forestry.modules.BlankForestryModule;
import forestry.api.client.IClientModuleHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.function.Consumer;

@ForestryModule
public class ModuleArboriculture extends BlankForestryModule {
	@Override
	public Identifier getId() {
		return ForestryModuleIds.ARBORICULTURE;
	}

	@Override
	public void registerEvents(BusGroup modBusGroup) {
		FMLCommonSetupEvent.getBus(modBusGroup).addListener(ModuleArboriculture::commonSetup);
		AttachCapabilitiesEvent.ItemStacks.BUS.addListener(ModuleArboriculture::attachCapabilities);
		LootTableLoadEvent.BUS.addListener(ModuleArboriculture::modifySnifferLoot);
	}

	@Override
	public void registerClientHandler(Consumer<IClientModuleHandler> registrar) {
		registrar.accept(new ArboricultureSignClientSetup());
	}

	private static void attachCapabilities(AttachCapabilitiesEvent.ItemStacks event) {
		// Add genetics capabilities to vanilla saplings
		if (!event.getCapabilities().containsKey(IIndividual.CAPABILITY_ID)) {
			ItemStack stack = event.getObject();

			ITreeSpeciesType type = SpeciesUtil.TREE_TYPE.get();
			ITree individual = type.getVanillaIndividual(stack.getItem());

			if (individual != null) {
				event.addCapability(IIndividual.CAPABILITY_ID, new IndividualHandlerItem(type, stack, individual, TreeLifeStage.SAPLING));
			}
		}
	}

	private static void modifySnifferLoot(LootTableLoadEvent event) {
		if (event.getName().equals(BuiltInLootTables.SNIFFER_DIGGING)) {
			LootTableUtil.addSupplementaryPool(event.getTable(), "forestry_amber_sapling", LootItem.lootTableItem(ArboricultureItems.AMBER_SAPLING));
		}
	}

	@Override
	public void setupApi() {
		TreeManager.woodAccess = WoodAccess.INSTANCE;
	}

	@Override
	public void addToRootCommand(LiteralArgumentBuilder<CommandSourceStack> command) {
		command.then(CommandTree.register());
	}

	private static void commonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			for (ForestryWoodType type : ForestryWoodType.VALUES) {
				WoodType.register(type.getWoodType());
			}
		});
	}

	@Override
	public void registerPackets(IPacketRegistry registry) {
		registry.clientbound(PacketIdClient.RIPENING_UPDATE, PacketRipeningUpdate.class, PacketRipeningUpdate::decode, PacketRipeningUpdate::handle);
	}

}
