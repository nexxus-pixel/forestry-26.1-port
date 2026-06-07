package forestry.api.modules;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;

public interface IForestryPacket {
	// Useless until 1.21
	Identifier id();

	void write(FriendlyByteBuf buffer);
}
