package forestry.core.utils;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

/**
 * Minimal stand-in for the removed Forge FakePlayer.
 */
public class ForestryFakePlayer extends ServerPlayer {
	public static final GameProfile MINECRAFT_PROFILE = new GameProfile(
		UUID.nameUUIDFromBytes("ForestryFakePlayer".getBytes()),
		"[Minecraft]"
	);

	public ForestryFakePlayer(ServerLevel level, GameProfile profile) {
		super(level.getServer(), level, profile, ClientInformation.createDefault());
	}
}
