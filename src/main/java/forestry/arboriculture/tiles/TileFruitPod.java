package forestry.arboriculture.tiles;

import forestry.core.utils.CompoundTagUtil;

import net.minecraft.world.level.storage.ValueInput;

import net.minecraft.core.HolderLookup;

import forestry.api.IForestryApi;
import forestry.api.arboriculture.genetics.IFruit;
import forestry.api.core.IProduct;
import forestry.api.genetics.IFruitBearer;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.alleles.ForestryAlleles;
import forestry.api.genetics.alleles.IValueAllele;
import forestry.api.genetics.alleles.TreeChromosomes;
import forestry.arboriculture.features.ArboricultureTiles;
import forestry.core.ClientsideCode;
import forestry.core.network.IStreamable;
import forestry.core.tiles.LegacyBlockEntity;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.ValueIoForestry;
import forestry.core.utils.NBTUtilForestry;
import forestry.core.utils.SpeciesUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public class TileFruitPod extends LegacyBlockEntity implements IFruitBearer, IStreamable {
	private static final short MAX_MATURITY = 2;
	private static final IGenome defaultGenome = SpeciesUtil.TREE_TYPE.get().getDefaultSpecies().getDefaultGenome();

	public static final String NBT_MATURITY = "MT";
	public static final String NBT_YIELD = "SP";
	public static final String NBT_FRUIT = "UID";

	private IGenome genome = defaultGenome;
	@Nullable
	private IFruit fruit = null;
	private short maturity;
	private float yield;

	public TileFruitPod(BlockPos pos, BlockState state) {
		super(ArboricultureTiles.PODS.tileType(), pos, state);
	}

	public void setProperties(IGenome genome, IFruit allele, float yield) {
		this.genome = genome;
		this.fruit = allele;
		this.yield = yield;
		setChanged();
	}

	/* SAVING & LOADING */
	@Override
	public void writeData(FriendlyByteBuf data) {
		if (this.fruit != null) {
			data.writeBoolean(true);
			data.writeIdentifier(TreeChromosomes.FRUIT.getId(this.fruit));
		} else {
			data.writeBoolean(false);
		}
	}

	@Override
	public void readData(FriendlyByteBuf data) {
		if (data.readBoolean()) {
			IValueAllele<?> stored = IForestryApi.INSTANCE.getAlleleManager().getAllele(data.readIdentifier()).cast();

			if (stored.value() instanceof IFruit newFruit) {
				this.fruit = newFruit;
				ClientsideCode.markForUpdate(this.worldPosition);
			}
		}
	}

	@Override
	protected void writeLegacyData(CompoundTag compoundNBT) {
		super.writeLegacyData(compoundNBT);
		if (this.fruit != null) {
			compoundNBT.putString(NBT_FRUIT, TreeChromosomes.FRUIT.getId(this.fruit).toString());
		}
		compoundNBT.putShort(NBT_MATURITY, this.maturity);
		compoundNBT.putFloat(NBT_YIELD, this.yield);
	}

	@Override
	protected void readLegacyData(CompoundTag nbt) {
		super.readLegacyData(nbt);

		String fruitNbt = CompoundTagUtil.getString(nbt, NBT_FRUIT);
		if (!fruitNbt.isEmpty()) {
			this.fruit = TreeChromosomes.FRUIT.getSafe(Identifier.parse(fruitNbt));
		}
		if (this.fruit == null) {
			this.fruit = ForestryAlleles.FRUIT_COCOA.value();
		}

		this.maturity = CompoundTagUtil.getShort(nbt, NBT_MATURITY);
		this.yield = CompoundTagUtil.getFloat(nbt, NBT_YIELD);
	}

	/* UPDATING */
	public void onBlockTick(RandomSource rand) {
		if (canMature() && rand.nextFloat() <= this.yield) {
			addRipeness(0.5f);
		}
	}

	public boolean canMature() {
		return this.maturity < MAX_MATURITY;
	}

	public short getMaturity() {
		return this.maturity;
	}

	public ItemStack getPickBlock() {
		if (this.fruit == null) {
			return ItemStack.EMPTY;
		}
		List<IProduct> products = this.fruit.getProducts();

		ItemStack pickBlock = ItemStack.EMPTY;
		float maxChance = 0.0f;
		for (IProduct product : products) {
			if (maxChance < product.chance()) {
				maxChance = product.chance();
				pickBlock = product.createStack();
			}
		}

		pickBlock.setCount(1);
		return pickBlock;
	}

	public List<ItemStack> getDrops() {
		return this.fruit == null ? List.of() : this.fruit.getFruits(this.genome, this.level, this.maturity);
	}

	/* NETWORK */
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

	/* IFRUITBEARER */
	@Override
	public boolean hasFruit() {
		return true;
	}

	@Override
	public List<ItemStack> pickFruit(ItemStack tool) {
		List<ItemStack> fruits = getDrops();
        this.maturity = 0;

		BlockState oldState = getBlockState();
		BlockState newState = oldState.setValue(CocoaBlock.AGE, 0);
		BlockUtil.setBlockWithBreakSound(this.level, getBlockPos(), newState, oldState);

		return fruits;
	}

	@Override
	public float getRipeness() {
		return (float) this.maturity / MAX_MATURITY;
	}

	@Override
	public void addRipeness(float add) {
		int previousAge = this.maturity;

        this.maturity += MAX_MATURITY * add;
		if (this.maturity > MAX_MATURITY) {
            this.maturity = MAX_MATURITY;
		}

		int age = this.maturity;
		if (age - previousAge > 0) {
			BlockState state = getBlockState().setValue(CocoaBlock.AGE, age);
            this.level.setBlockAndUpdate(getBlockPos(), state);
		}
	}
}
