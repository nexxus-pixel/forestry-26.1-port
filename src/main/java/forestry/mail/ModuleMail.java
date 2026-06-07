package forestry.mail;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import forestry.api.mail.IMailAddress;
import forestry.api.modules.ForestryModule;
import forestry.api.modules.ForestryModuleIds;
import forestry.api.modules.IPacketRegistry;
import forestry.core.network.PacketIdClient;
import forestry.core.network.PacketIdServer;
import forestry.core.utils.NetworkUtil;
import forestry.mail.carriers.PostalCarriers;
import forestry.mail.carriers.players.POBox;
import forestry.mail.carriers.players.POBoxRegistry;
import forestry.mail.commands.CommandMail;
import forestry.mail.network.packets.*;
import forestry.modules.BlankForestryModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;


@ForestryModule
public class ModuleMail extends BlankForestryModule {
	@Override
	public Identifier getId() {
		return ForestryModuleIds.MAIL;
	}

	@Override
	public void registerEvents(BusGroup modBusGroup) {
		PlayerEvent.PlayerLoggedInEvent.BUS.addListener(ModuleMail::handlePlayerLoggedIn);
		PostalCarriers.register(modBusGroup);
	}

	public static void handlePlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		Player player = event.getEntity();
		if (player.level().isClientSide()) {
			return;
		}

		IMailAddress address = new MailAddress(player.getGameProfile());
		POBox pobox = POBoxRegistry.getOrCreate((ServerLevel) player.level()).getOrCreatePOBox(address);
		NetworkUtil.sendToPlayer(new PacketPOBoxInfo(pobox), (ServerPlayer) player);
	}

	@Override
	public void addToRootCommand(LiteralArgumentBuilder<CommandSourceStack> command) {
		command.then(CommandMail.register());
	}

	@Override
	public void registerPackets(IPacketRegistry registry) {
		registry.serverbound(PacketIdServer.MAILBOX_REQUEST, PacketMailboxRequest.class, PacketMailboxRequest::decode, PacketMailboxRequest::handle);
		registry.serverbound(PacketIdServer.MAILBOX_MOVE, PacketMailboxMove.class, PacketMailboxMove::decode, PacketMailboxMove::handle);
		registry.serverbound(PacketIdServer.MAILBOX_RESPONSE, PacketMailboxResponse.class, PacketMailboxResponse::decode, PacketMailboxResponse::handle);
		registry.serverbound(PacketIdServer.TRADING_ADDRESS_REQUEST, PacketTradingAddressRequest.class, PacketTradingAddressRequest::decode, PacketTradingAddressRequest::handle);

		registry.clientbound(PacketIdClient.MAILBOX_INFO, PacketMailboxInfo.class, PacketMailboxInfo::decode, PacketMailboxInfo::handle);
		registry.clientbound(PacketIdClient.MAILBOX_POBOX_INFO, PacketPOBoxInfo.class, PacketPOBoxInfo::decode, PacketPOBoxInfo::handle);
		registry.clientbound(PacketIdClient.MAILBOX_RESPONSE, PacketMailboxResponse.class, PacketMailboxResponse::decode, PacketMailboxResponse::handle);
		registry.clientbound(PacketIdClient.TRADING_ADDRESS_RESPONSE, PacketTradingAddressResponse.class, PacketTradingAddressResponse::decode, PacketTradingAddressResponse::handle);
	}

}
