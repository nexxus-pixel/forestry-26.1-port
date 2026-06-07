package forestry.core.network.packets;

import forestry.core.utils.CompoundTagUtil;

import forestry.api.IForestryApi;
import forestry.api.core.ForestryEvent;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.ISpeciesType;
import forestry.api.modules.IForestryPacketClient;
import forestry.core.genetics.BreedingTracker;
import forestry.core.network.PacketIdClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import javax.annotation.Nullable;

public record PacketGenomeTrackerSync(@Nullable CompoundTag nbt) implements IForestryPacketClient {
	@Override
	public Identifier id() {
		return PacketIdClient.GENOME_TRACKER_UPDATE;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeNbt(this.nbt);
	}

	public static PacketGenomeTrackerSync decode(FriendlyByteBuf buffer) {
		return new PacketGenomeTrackerSync(buffer.readNbt());
	}

	public static void handle(PacketGenomeTrackerSync msg, Player player) {
		if (msg.nbt != null) {
			String type = CompoundTagUtil.getString(msg.nbt, BreedingTracker.TYPE_KEY);
			ISpeciesType<?, ?> root = IForestryApi.INSTANCE.getGeneticManager().getSpeciesTypeSafe(Identifier.parse(type));

			if (root != null) {
				IBreedingTracker tracker = root.getBreedingTracker(player.level(), player.getGameProfile());
				tracker.readFromNbt(msg.nbt);
				ForestryEvent.SyncedBreedingTracker.BUS.post(new ForestryEvent.SyncedBreedingTracker(tracker, player));
			}
		}
	}
}
