package forestry.core.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * GameProfile serialization helpers for Minecraft 26.1 (NbtUtils profile methods removed).
 */
public final class GameProfileUtil {
	private static final Codec<GameProfile> CODEC = ExtraCodecs.AUTHLIB_GAME_PROFILE;

	private GameProfileUtil() {
	}

	public static UUID id(GameProfile profile) {
		return profile.id();
	}

	public static String name(GameProfile profile) {
		return profile.name();
	}

	@Nullable
	public static GameProfile read(CompoundTag tag) {
		return CODEC.parse(NbtOps.INSTANCE, tag).result().orElse(null);
	}

	public static void write(CompoundTag tag, GameProfile profile) {
		CODEC.encodeStart(NbtOps.INSTANCE, profile).result().ifPresent(encoded -> {
			if (encoded instanceof CompoundTag compound) {
				tag.merge(compound);
			}
		});
	}

	public static void writeToNetwork(FriendlyByteBuf buf, @Nullable GameProfile profile) {
		if (profile == null) {
			buf.writeBoolean(false);
		} else {
			buf.writeBoolean(true);
			buf.writeUUID(profile.id());
			buf.writeUtf(profile.name());
		}
	}

	@Nullable
	public static GameProfile readFromNetwork(FriendlyByteBuf buf) {
		if (!buf.readBoolean()) {
			return null;
		}
		return new GameProfile(buf.readUUID(), buf.readUtf());
	}
}
