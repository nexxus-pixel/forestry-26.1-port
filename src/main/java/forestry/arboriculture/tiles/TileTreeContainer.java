package forestry.arboriculture.tiles;

import net.minecraft.world.level.storage.ValueInput;

import net.minecraft.core.HolderLookup;

import forestry.api.arboriculture.genetics.ITree;
import forestry.core.ClientsideCode;
import forestry.core.network.IStreamable;
import forestry.core.tiles.LegacyBlockEntity;
import forestry.core.utils.NBTUtilForestry;
import forestry.core.utils.ValueIoForestry;
import forestry.core.utils.SpeciesUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * This is the base TE class for any block that needs to contain tree genome information.
 */
public abstract class TileTreeContainer extends LegacyBlockEntity implements IStreamable {
	@Nullable
	private ITree containedTree;

	public TileTreeContainer(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	/* SAVING & LOADING */
	@Override
	protected void writeLegacyData(CompoundTag nbt) {
		super.writeLegacyData(nbt);

		if (this.containedTree != null) {
			Tag serialized = SpeciesUtil.serializeIndividual(this.containedTree);
			if (serialized != null) {
				nbt.put("ContainedTree", serialized);
			}
		}
	}

	@Override
	protected void readLegacyData(CompoundTag nbt) {
		super.readLegacyData(nbt);

		Tag treeNbt = nbt.get("ContainedTree");

		if (treeNbt != null) {
			this.containedTree = SpeciesUtil.deserializeIndividual(SpeciesUtil.TREE_TYPE.get(), treeNbt);
		}
	}

	@Override
	public void writeData(FriendlyByteBuf data) {
		ITree tree = getTree();
		if (tree != null) {
			data.writeBoolean(true);
			Identifier speciesId = tree.getSpecies().id();
			data.writeIdentifier(speciesId);
		} else {
			data.writeBoolean(false);
		}
	}

	@Override
	public void readData(FriendlyByteBuf data) {
		if (data.readBoolean()) {
			Identifier speciesId = data.readIdentifier();
			ITree tree = SpeciesUtil.getTreeSpecies(speciesId).createIndividual();
			setTree(tree);
		}
	}

	/* CONTAINED TREE */
	public void setTree(ITree tree) {
		this.containedTree = tree;

		if (this.level != null && this.level.isClientSide()) {
			ClientsideCode.markForUpdate(this.worldPosition);
        }
	}

	@Nullable
	public ITree getTree() {
		return this.containedTree;
	}

	/* UPDATING */

	/**
	 * Leaves and saplings will implement their logic here.
	 */
	public abstract void onBlockTick(Level worldIn, BlockPos pos, BlockState state, RandomSource rand);

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag tag = saveWithoutMetadata(registries);
		return NBTUtilForestry.writeStreamableToNbt(this, tag, level.registryAccess());
	}

	@Override
	public void handleUpdateTag(ValueInput input, HolderLookup.Provider registries) {
		super.handleUpdateTag(input, registries);
		ValueIoForestry.readLegacy(input, tag -> NBTUtilForestry.readStreamableFromNbt(this, tag, level.registryAccess()));
	}
}
