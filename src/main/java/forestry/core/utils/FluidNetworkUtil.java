package forestry.core.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fluids.FluidStack;

public final class FluidNetworkUtil {
	private FluidNetworkUtil() {
	}

	public static void write(FriendlyByteBuf buf, FluidStack stack) {
		if (stack == null || stack.isEmpty()) {
			buf.writeBoolean(false);
			return;
		}
		buf.writeBoolean(true);
		buf.writeNbt(stack.writeToNBT(new CompoundTag()));
	}

	public static FluidStack read(FriendlyByteBuf buf) {
		if (!buf.readBoolean()) {
			return FluidStack.EMPTY;
		}
		CompoundTag tag = buf.readNbt();
		return tag == null ? FluidStack.EMPTY : FluidStack.loadFluidStackFromNBT(tag);
	}
}
