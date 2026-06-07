package forestry.core.owner;

import com.mojang.authlib.GameProfile;
import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;
import forestry.core.network.IStreamable;
import forestry.core.utils.CompoundTagUtil;
import forestry.core.utils.GameProfileUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nullable;
import java.util.UUID;

public class OwnerHandler implements IOwnerHandler, IStreamable, INbtWritable, INbtReadable {
	@Nullable
	private GameProfile owner = null;

	@Override
	@Nullable
	public GameProfile getOwner() {
		return this.owner;
	}

	@Override
	public void setOwner(GameProfile owner) {
		this.owner = owner;
	}

	@Override
	public void writeData(FriendlyByteBuf data) {
		GameProfileUtil.writeToNetwork(data, this.owner);
	}

	@Override
	public void readData(FriendlyByteBuf data) {
		GameProfile owner = GameProfileUtil.readFromNetwork(data);
		if (owner != null) {
			setOwner(owner);
		}
	}

	@Override
	public void read(CompoundTag data) {
		if (data.contains("owner")) {
			GameProfile owner = GameProfileUtil.read(CompoundTagUtil.getCompound(data, "owner"));
			if (owner != null) {
				setOwner(owner);
			}
		}
	}

	@Override
	public CompoundTag write(CompoundTag data) {
		if (this.owner != null) {
			CompoundTag nbt = new CompoundTag();
			GameProfileUtil.write(nbt, this.owner);
			data.put("owner", nbt);
		}
		return data;
	}
}
