package forestry.core.network;

import forestry.Forestry;
import forestry.api.ForestryConstants;
import forestry.api.IForestryApi;
import forestry.api.modules.IForestryPacket;
import forestry.api.modules.IForestryPacketClient;
import forestry.api.modules.IForestryPacketServer;
import forestry.api.modules.IPacketRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class NetworkHandler {
	public static final Identifier CHANNEL_ID = ForestryConstants.forestry("channel");
	public static final SimpleChannel CHANNEL = ChannelBuilder.named(CHANNEL_ID)
		.networkProtocolVersion(1)
		.acceptedVersions(Channel.VersionTest.exact(1))
		.simpleChannel();

	public static void register() {
		IPacketRegistry registry = new NetworkHandler.PacketRegistry(CHANNEL);

		IForestryApi.INSTANCE.getModuleManager().getLoadedModules().forEach(module -> module.registerPackets(registry));
		CHANNEL.build();
	}

	private static final class PacketRegistry implements IPacketRegistry {
		private final SimpleChannel channel;
		private int packetId;

		private PacketRegistry(SimpleChannel channel) {
			this.channel = channel;
		}

		@Override
		public <P extends IForestryPacketServer> void serverbound(Identifier id, Class<P> packetClass, Function<FriendlyByteBuf, P> decoder, BiConsumer<P, ServerPlayer> packetHandler) {
			this.channel.messageBuilder(packetClass, this.packetId++)
				.encoder(IForestryPacket::write)
				.decoder(decoder)
				.consumerMainThread((msg, ctx) -> handleServerbound(msg, ctx, packetHandler))
				.add();
		}

		@Override
		public <P extends IForestryPacketClient> void clientbound(Identifier id, Class<P> packetClass, Function<FriendlyByteBuf, P> decoder, BiConsumer<P, Player> packetHandler) {
			this.channel.messageBuilder(packetClass, this.packetId++)
				.encoder(IForestryPacket::write)
				.decoder(decoder)
				.consumerMainThread((msg, ctx) -> handleClientbound(msg, ctx, packetHandler))
				.add();
		}
	}

	private static <T extends IForestryPacket> void handleServerbound(T message, CustomPayloadEvent.Context ctx, BiConsumer<T, ServerPlayer> handler) {
		ServerPlayer sender = ctx.getSender();
		if (sender == null) {
			Forestry.LOGGER.warn("the player was null, message: {}", message);
		} else {
			handler.accept(message, sender);
		}
		ctx.setPacketHandled(true);
	}

	private static <T extends IForestryPacket> void handleClientbound(T message, CustomPayloadEvent.Context ctx, BiConsumer<T, Player> handler) {
		handler.accept(message, Minecraft.getInstance().player);
		ctx.setPacketHandled(true);
	}
}
