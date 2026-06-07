package forestry.core.utils;

import forestry.core.network.IStreamable;
import io.netty.buffer.Unpooled;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public abstract class NBTUtilForestry {

	public static CompoundTag writeStreamableToNbt(IStreamable streamable, CompoundTag nbt, RegistryAccess registryAccess) {
		RegistryFriendlyByteBuf data = new RegistryFriendlyByteBuf(Unpooled.buffer(), registryAccess);
		streamable.writeData(data);

		byte[] bytes = new byte[data.readableBytes()];
		data.getBytes(0, bytes);
		nbt.putByteArray("dataBytes", bytes);
		return nbt;
	}

	public static void readStreamableFromNbt(IStreamable streamable, CompoundTag nbt, RegistryAccess registryAccess) {
		if (nbt.contains("dataBytes")) {
			byte[] bytes = nbt.getByteArray("dataBytes").orElse(new byte[0]);
			RegistryFriendlyByteBuf data = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(bytes), registryAccess);
			streamable.readData(data);
		}
	}
}
