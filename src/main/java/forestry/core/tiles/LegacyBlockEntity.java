package forestry.core.tiles;

import forestry.core.utils.ValueIoForestry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * BlockEntity base with legacy CompoundTag IO bridged through ValueInput/ValueOutput.
 */
public abstract class LegacyBlockEntity extends BlockEntity {
	protected LegacyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		ValueIoForestry.readLegacy(input, this::readLegacyData);
	}

	protected void readLegacyData(CompoundTag data) {
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		CompoundTag data = new CompoundTag();
		writeLegacyData(data);
		ValueIoForestry.writeLegacy(output, data);
	}

	protected void writeLegacyData(CompoundTag data) {
	}

	/** Reads legacy tile data from a plain CompoundTag (e.g. item stack NBT). */
	public void load(CompoundTag data) {
		readLegacyData(data);
	}

	/** Writes legacy tile data into a plain CompoundTag. */
	public void save(CompoundTag data) {
		writeLegacyData(data);
	}
}
