package forestry.worktable;

import forestry.api.modules.ForestryModule;
import forestry.api.modules.ForestryModuleIds;
import forestry.api.modules.IPacketRegistry;
import forestry.core.network.PacketIdClient;
import forestry.core.network.PacketIdServer;
import forestry.modules.BlankForestryModule;
import forestry.worktable.network.packets.PacketWorktableMemoryUpdate;
import forestry.worktable.network.packets.PacketWorktableRecipeRequest;
import forestry.worktable.network.packets.PacketWorktableRecipeUpdate;
import net.minecraft.resources.Identifier;

@ForestryModule
public class ModuleWorktable extends BlankForestryModule {
	@Override
	public Identifier getId() {
		return ForestryModuleIds.WORKTABLE;
	}

	@Override
	public void registerPackets(IPacketRegistry registry) {
		registry.serverbound(PacketIdServer.WORKTABLE_RECIPE_REQUEST, PacketWorktableRecipeRequest.class, PacketWorktableRecipeRequest::decode, PacketWorktableRecipeRequest::handle);

		registry.clientbound(PacketIdClient.WORKTABLE_MEMORY_UPDATE, PacketWorktableMemoryUpdate.class, PacketWorktableMemoryUpdate::decode, PacketWorktableMemoryUpdate::handle);
		registry.clientbound(PacketIdClient.WORKTABLE_CRAFTING_UPDATE, PacketWorktableRecipeUpdate.class, PacketWorktableRecipeUpdate::decode, PacketWorktableRecipeUpdate::handle);
	}
}
