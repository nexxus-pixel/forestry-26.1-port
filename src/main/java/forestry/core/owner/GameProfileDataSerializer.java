package forestry.core.owner;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;

import java.util.Optional;
import java.util.UUID;

public enum GameProfileDataSerializer implements EntityDataSerializer<Optional<GameProfile>> {
	INSTANCE;

	private static final StreamCodec<RegistryFriendlyByteBuf, Optional<GameProfile>> CODEC = StreamCodec.of(
		(buf, value) -> {
			if (value.isEmpty()) {
				buf.writeBoolean(false);
			} else {
				buf.writeBoolean(true);
				GameProfile gameProfile = value.get();
				buf.writeUUID(gameProfile.id());
				buf.writeUtf(gameProfile.name());
			}
		},
		buf -> {
			if (!buf.readBoolean()) {
				return Optional.empty();
			}
			UUID uuid = buf.readUUID();
			String name = buf.readUtf(1024);
			return Optional.of(new GameProfile(uuid, name));
		}
	);

	@Override
	public StreamCodec<? super RegistryFriendlyByteBuf, Optional<GameProfile>> codec() {
		return CODEC;
	}

	@Override
	public Optional<GameProfile> copy(Optional<GameProfile> value) {
		return value;
	}
}
