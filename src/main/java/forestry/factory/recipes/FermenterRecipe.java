package forestry.factory.recipes;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import forestry.api.recipes.IFermenterRecipe;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class FermenterRecipe implements IFermenterRecipe {
	public static final MapCodec<FermenterRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Ingredient.CODEC.fieldOf("resource").forGetter(FermenterRecipe::getInputItem),
		Codec.INT.fieldOf("fermentationValue").forGetter(FermenterRecipe::getFermentationValue),
		Codec.FLOAT.fieldOf("modifier").forGetter(FermenterRecipe::getModifier),
		RecipeSerializers.FLUID_CODEC.fieldOf("output").forGetter(FermenterRecipe::getOutput),
		RecipeSerializers.FLUID_STACK_CODEC.fieldOf("fluidResource").forGetter(FermenterRecipe::getInputFluid)
	).apply(instance, RecipeSerializers.bindId((id, resource, fermentationValue, modifier, output, fluidResource) ->
		new FermenterRecipe(id, resource, fermentationValue, modifier, output, fluidResource))));

	public static final StreamCodec<RegistryFriendlyByteBuf, FermenterRecipe> STREAM_CODEC = StreamCodec.composite(
		Ingredient.CONTENTS_STREAM_CODEC, FermenterRecipe::getInputItem,
		ByteBufCodecs.VAR_INT, FermenterRecipe::getFermentationValue,
		ByteBufCodecs.FLOAT, FermenterRecipe::getModifier,
		RecipeSerializers.FLUID_STREAM_CODEC, FermenterRecipe::getOutput,
		RecipeSerializers.FLUID_STACK_STREAM_CODEC, FermenterRecipe::getInputFluid,
		RecipeSerializers.bindId((id, resource, fermentationValue, modifier, output, fluidResource) ->
			new FermenterRecipe(id, resource, fermentationValue, modifier, output, fluidResource))
	);

	public static final RecipeSerializer<FermenterRecipe> SERIALIZER = RecipeSerializers.of(CODEC, STREAM_CODEC);

	private final Identifier id;
	private final Ingredient resource;
	private final int fermentationValue;
	private final float modifier;
	private final Fluid output;
	private final FluidStack fluidResource;

	public FermenterRecipe(Identifier id, Ingredient resource, int fermentationValue, float modifier, Fluid output, FluidStack fluidResource) {
		Preconditions.checkNotNull(id, "Recipe identifier cannot be null");
		Preconditions.checkNotNull(resource, "Fermenter Resource cannot be null!");
		Preconditions.checkArgument(!resource.isEmpty(), "Fermenter Resource item cannot be empty!");
		Preconditions.checkNotNull(output, "Fermenter Output cannot be null!");
		Preconditions.checkNotNull(fluidResource, "Fermenter Liquid cannot be null!");

		this.id = id;
		this.resource = resource;
		this.fermentationValue = fermentationValue;
		this.modifier = modifier;
		this.output = output;
		this.fluidResource = fluidResource;
	}

	public FermenterRecipe(Identifier id, int fermentationValue, float modifier, Fluid output, FluidStack fluidResource) {
		Preconditions.checkNotNull(id, "Recipe identifier cannot be null");
		Preconditions.checkNotNull(output, "Fermenter output cannot be null!");
		Preconditions.checkNotNull(fluidResource, "Fermenter liquid cannot be null!");

		this.id = id;
		this.resource = Ingredient.of(java.util.stream.Stream.empty());
		this.fermentationValue = fermentationValue;
		this.modifier = modifier;
		this.output = output;
		this.fluidResource = fluidResource;
	}

	@Override
	public Ingredient getInputItem() {
		return this.resource;
	}

	@Override
	public FluidStack getInputFluid() {
		return this.fluidResource;
	}

	@Override
	public int getFermentationValue() {
		return this.fermentationValue;
	}

	@Override
	public float getModifier() {
		return this.modifier;
	}

	@Override
	public Fluid getOutput() {
		return this.output;
	}

	@Override
	public boolean matches(ItemStack inputItem, FluidStack inputFluid) {
		return this.resource.test(inputItem) && this.fluidResource.isFluidEqual(inputFluid);
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	@Override
	public RecipeSerializer<? extends IForestryRecipe> getSerializer() {
		return FactoryRecipeTypes.FERMENTER.serializer();
	}

	@Override
	public RecipeType<? extends IForestryRecipe> getType() {
		return FactoryRecipeTypes.FERMENTER.type();
	}
}
