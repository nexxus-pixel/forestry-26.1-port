package forestry.api.multiblock;

import forestry.core.utils.ValueIoForestry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Base logic class for Multiblock-connected tile entities.
 * Most multiblock components should derive from this.
 * Supply it an IMultiblockLogic from MultiblockManager.logicFactory
 */
public abstract class MultiblockTileEntityBase<T extends IMultiblockLogic> extends BlockEntity implements IMultiblockComponent {
	private final T multiblockLogic;

	public MultiblockTileEntityBase(BlockEntityType<?> tileEntityType, BlockPos pos, BlockState state, T multiblockLogic) {
		super(tileEntityType, pos, state);
		this.multiblockLogic = multiblockLogic;
	}

	@Override
	public BlockPos getCoordinates() {
		return getBlockPos();
	}

	@Override
	public T getMultiblockLogic() {
		return this.multiblockLogic;
	}

	@Override
	public abstract void onMachineAssembled(IMultiblockController multiblockController, BlockPos minCoord, BlockPos maxCoord);

	@Override
	public abstract void onMachineBroken();

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		ValueIoForestry.readLegacy(input, this::readLegacyData);
	}

	protected void readLegacyData(CompoundTag data) {
		this.multiblockLogic.readFromNBT(data);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		CompoundTag data = new CompoundTag();
		writeLegacyData(data);
		ValueIoForestry.writeLegacy(output, data);
	}

	protected void writeLegacyData(CompoundTag data) {
		this.multiblockLogic.write(data);
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		this.multiblockLogic.invalidate(this.level, this);
	}

	@Override
	public void onChunkUnloaded() {
		super.onChunkUnloaded();
		this.multiblockLogic.onChunkUnload(this.level, this);
	}

	@Override
	public void onLoad() {
		super.onLoad();
		this.multiblockLogic.validate(this.level, this);
	}

	/* Network Communication */

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag updateTag = saveWithoutMetadata(registries);
		this.multiblockLogic.encodeDescriptionPacket(updateTag);
		encodeDescriptionPacket(updateTag);
		return updateTag;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void onDataPacket(Connection network, ValueInput input, HolderLookup.Provider registries) {
		super.onDataPacket(network, input, registries);
		ValueIoForestry.readLegacy(input, nbtData -> {
			this.multiblockLogic.decodeDescriptionPacket(nbtData);
			decodeDescriptionPacket(nbtData);
		});
	}

	@Override
	public void handleUpdateTag(ValueInput input, HolderLookup.Provider registries) {
		super.handleUpdateTag(input, registries);
		ValueIoForestry.readLegacy(input, tag -> {
			this.multiblockLogic.decodeDescriptionPacket(tag);
			decodeDescriptionPacket(tag);
		});
	}

	/**
	 * Used to write tileEntity-specific data to the descriptionPacket
	 */
	protected void encodeDescriptionPacket(CompoundTag packetData) {

	}

	/**
	 * Used to read tileEntity-specific data from the descriptionPacket (onDataPacket)
	 */
	protected void decodeDescriptionPacket(CompoundTag packetData) {

	}
}
