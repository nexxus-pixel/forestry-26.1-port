package forestry.factory.recipes;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import forestry.api.recipes.IFabricatorSmeltingRecipe;
import forestry.api.recipes.IForestryRecipe;
import forestry.factory.features.FactoryRecipeTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;

public class FabricatorSmeltingRecipe implements IFabricatorSmeltingRecipe {
	public static final MapCodec<FabricatorSmeltingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Ingredient.CODEC.fieldOf("resource").forGetter(FabricatorSmeltingRecipe::getInput),
		RecipeSerializers.FLUID_STACK_CODEC.fieldOf("product").forGetter(FabricatorSmeltingRecipe::getResultFluid),
		Codec.INT.fieldOf("melting").forGetter(FabricatorSmeltingRecipe::getMeltingPoint)
	).apply(instance, RecipeSerializers.bindId(FabricatorSmeltingRecipe::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, FabricatorSmeltingRecipe> STREAM_CODEC = StreamCodec.composite(
		Ingredient.CONTENTS_STREAM_CODEC, FabricatorSmeltingRecipe::getInput,
		RecipeSerializers.FLUID_STACK_STREAM_CODEC, FabricatorSmeltingRecipe::getResultFluid,
		ByteBufCodecs.VAR_INT, FabricatorSmeltingRecipe::getMeltingPoint,
		RecipeSerializers.bindId(FabricatorSmeltingRecipe::new)
	);

	public static final RecipeSerializer<FabricatorSmeltingRecipe> SERIALIZER = RecipeSerializers.of(CODEC, STREAM_CODEC);

	private final Identifier id;
	private final Ingredient resource;
	private final FluidStack product;
	private final int meltingPoint;

	public FabricatorSmeltingRecipe(Identifier id, Ingredient resource, FluidStack molten, int meltingPoint) {
		Preconditions.checkNotNull(id, "Recipe identifier cannot be null");
		Preconditions.checkNotNull(resource);
		Preconditions.checkArgument(!resource.isEmpty());
		Preconditions.checkNotNull(molten);

		this.id = id;
		this.resource = resource;
		this.product = molten;
		this.meltingPoint = meltingPoint;
	}

	@Override
	public Ingredient getInput() {
		return this.resource;
	}

	@Override
	public FluidStack getResultFluid() {
		return this.product;
	}

	@Override
	public int getMeltingPoint() {
		return this.meltingPoint;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	@Override
	public RecipeSerializer<? extends IForestryRecipe> getSerializer() {
		return FactoryRecipeTypes.FABRICATOR_SMELTING.serializer();
	}

	@Override
	public RecipeType<? extends IForestryRecipe> getType() {
		return FactoryRecipeTypes.FABRICATOR_SMELTING.type();
	}
}
