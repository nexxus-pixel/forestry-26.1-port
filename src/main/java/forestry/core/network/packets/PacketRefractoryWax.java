package forestry.core.network.packets;

import forestry.api.modules.IForestryPacketClient;
import forestry.api.ForestryConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

/**
 * Stub until particle packet handler is migrated.
 */
public record PacketRefractoryWax(double x, double y, double z) implements IForestryPacketClient {
	public static PacketRefractoryWax decode(FriendlyByteBuf buffer) {
		return new PacketRefractoryWax(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
	}

	public static void handle(PacketRefractoryWax packet, Player player) {
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeDouble(this.x);
		buffer.writeDouble(this.y);
		buffer.writeDouble(this.z);
	}

	@Override
	public Identifier id() {
		return ForestryConstants.forestry("refractory_wax_on");
	}
}
