package forestry.sorting;

import forestry.api.modules.ForestryModule;
import forestry.api.modules.ForestryModuleIds;
import forestry.api.modules.IPacketRegistry;
import forestry.core.network.PacketIdClient;
import forestry.core.network.PacketIdServer;
import forestry.modules.BlankForestryModule;
import forestry.sorting.network.packets.PacketFilterChangeGenome;
import forestry.sorting.network.packets.PacketFilterChangeRule;
import forestry.sorting.network.packets.PacketGuiFilterUpdate;
import net.minecraft.resources.Identifier;
import net.minecraftforge.eventbus.api.bus.BusGroup;


@ForestryModule
public class ModuleSorting extends BlankForestryModule {
	@Override
	public Identifier getId() {
		return ForestryModuleIds.SORTING;
	}

	@Override
	public void registerEvents(BusGroup modBusGroup) {
	}

	@Override
	public void registerPackets(IPacketRegistry registry) {
		registry.serverbound(PacketIdServer.FILTER_CHANGE_RULE, PacketFilterChangeRule.class, PacketFilterChangeRule::decode, PacketFilterChangeRule::handle);
		registry.serverbound(PacketIdServer.FILTER_CHANGE_GENOME, PacketFilterChangeGenome.class, PacketFilterChangeGenome::decode, PacketFilterChangeGenome::handle);

		registry.clientbound(PacketIdClient.GUI_UPDATE_FILTER, PacketGuiFilterUpdate.class, PacketGuiFilterUpdate::decode, PacketGuiFilterUpdate::handle);
	}

}
