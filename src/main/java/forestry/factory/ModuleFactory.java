package forestry.factory;

import forestry.api.fuels.FermenterFuel;
import forestry.api.fuels.FuelManager;
import forestry.api.fuels.MoistenerFuel;
import forestry.api.fuels.RainSubstrate;
import forestry.api.modules.ForestryModule;
import forestry.api.modules.ForestryModuleIds;
import forestry.api.modules.IPacketRegistry;
import forestry.core.config.Preference;
import forestry.core.features.CoreItems;
import forestry.core.network.PacketIdClient;
import forestry.core.network.PacketIdServer;
import forestry.core.utils.datastructures.ItemStackMap;
import forestry.factory.network.packets.PacketRecipeTransferRequest;
import forestry.factory.network.packets.PacketRecipeTransferUpdate;
import forestry.modules.BlankForestryModule;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.bus.BusGroup;


@ForestryModule
public class ModuleFactory extends BlankForestryModule {
	@Override
	public Identifier getId() {
		return ForestryModuleIds.FACTORY;
	}

	@Override
	public void registerEvents(BusGroup modBusGroup) {
	}

	@Override
	public void setupApi() {
		FuelManager.fermenterFuel = new ItemStackMap<>();
		FuelManager.moistenerResource = new ItemStackMap<>();
		FuelManager.rainSubstrate = new ItemStackMap<>();

		// Set fuels and resources for the fermenter
		FuelManager.fermenterFuel.put(CoreItems.FERTILIZER_COMPOUND.item(), new FermenterFuel(CoreItems.FERTILIZER_COMPOUND.item(),
			Preference.FERMENTED_CYCLE_FERTILIZER, Preference.FERMENTATION_DURATION_FERTILIZER));

		int cyclesCompost = Preference.FERMENTATION_DURATION_COMPOST;
		int valueCompost = Preference.FERMENTED_CYCLE_COMPOST;
		FuelManager.fermenterFuel.put(CoreItems.COMPOST.item(), new FermenterFuel(CoreItems.COMPOST.item(), valueCompost, cyclesCompost));
		FuelManager.fermenterFuel.put(CoreItems.MULCH.item(), new FermenterFuel(CoreItems.MULCH.item(), valueCompost, cyclesCompost));

		// Add moistener resources
		FuelManager.moistenerResource.put(Items.WHEAT, new MoistenerFuel(Items.WHEAT, CoreItems.MOULDY_WHEAT.item(), 0, 300));
		FuelManager.moistenerResource.put(CoreItems.MOULDY_WHEAT.item(), new MoistenerFuel(CoreItems.MOULDY_WHEAT.item(), CoreItems.DECAYING_WHEAT.item(), 1, 600));
		FuelManager.moistenerResource.put(CoreItems.DECAYING_WHEAT.item(), new MoistenerFuel(CoreItems.DECAYING_WHEAT.item(), CoreItems.MULCH.item(), 2, 900));

		// Set rain substrates
		FuelManager.rainSubstrate.put(CoreItems.IODINE_CHARGE.item(), new RainSubstrate(CoreItems.IODINE_CHARGE.item(), 10000, 0.01f));
		FuelManager.rainSubstrate.put(CoreItems.DISSIPATION_CHARGE.item(), new RainSubstrate(CoreItems.DISSIPATION_CHARGE.item(), 0.075f));
	}

	@Override
	public void registerPackets(IPacketRegistry registry) {
		registry.serverbound(PacketIdServer.RECIPE_TRANSFER_REQUEST, PacketRecipeTransferRequest.class, PacketRecipeTransferRequest::decode, PacketRecipeTransferRequest::handle);
		registry.clientbound(PacketIdClient.RECIPE_TRANSFER_UPDATE, PacketRecipeTransferUpdate.class, PacketRecipeTransferUpdate::decode, PacketRecipeTransferUpdate::handle);
	}
}
