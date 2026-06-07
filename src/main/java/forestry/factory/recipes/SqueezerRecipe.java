package forestry.factory.recipes;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import forestry.api.recipes.ISqueezerRecipe;
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

import java.util.ArrayList;
import java.util.List;

public class SqueezerRecipe implements ISqueezerRecipe {
	public static final MapCodec<SqueezerRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.INT.fieldOf("time").forGetter(SqueezerRecipe::getProcessingTime),
		Ingredient.CODEC.listOf().fieldOf("resources").forGetter(SqueezerRecipe::getInputs),
		RecipeSerializers.FLUID_STACK_CODEC.fieldOf("output").forGetter(SqueezerRecipe::getFluidOutput),
		ItemStack.CODEC.fieldOf("remnant").forGetter(SqueezerRecipe::getRemnants),
		Codec.FLOAT.fieldOf("chance").forGetter(SqueezerRecipe::getRemnantsChance)
	).apply(instance, RecipeSerializers.bindId(SqueezerRecipe::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, SqueezerRecipe> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT, SqueezerRecipe::getProcessingTime,
		RecipeSerializers.INGREDIENT_LIST_STREAM_CODEC, SqueezerRecipe::getInputs,
		RecipeSerializers.FLUID_STACK_STREAM_CODEC, SqueezerRecipe::getFluidOutput,
		ItemStack.STREAM_CODEC, SqueezerRecipe::getRemnants,
		ByteBufCodecs.FLOAT, SqueezerRecipe::getRemnantsChance,
		RecipeSerializers.bindId(SqueezerRecipe::new)
	);

	public static final RecipeSerializer<SqueezerRecipe> SERIALIZER = RecipeSerializers.of(CODEC, STREAM_CODEC);

	private final Identifier id;
	private final int processingTime;
	private final List<Ingredient> resources;
	private final FluidStack fluidOutput;
	private final ItemStack remnants;
	private final float remnantsChance;

	public SqueezerRecipe(Identifier id, int processingTime, List<Ingredient> resources, FluidStack fluidOutput, ItemStack remnants, float remnantsChance) {
		Preconditions.checkNotNull(id, "Recipe identifier cannot be null");
		Preconditions.checkNotNull(resources);
		Preconditions.checkArgument(!resources.isEmpty());
		Preconditions.checkNotNull(fluidOutput);
		Preconditions.checkNotNull(remnants);

		this.id = id;
		this.processingTime = processingTime;
		this.resources = resources;
		this.fluidOutput = fluidOutput;
		this.remnants = remnants;
		this.remnantsChance = remnantsChance;
	}

	@Override
	public List<Ingredient> getInputs() {
		return this.resources;
	}

	@Override
	public ItemStack getRemnants() {
		return this.remnants;
	}

	@Override
	public float getRemnantsChance() {
		return this.remnantsChance;
	}

	@Override
	public FluidStack getFluidOutput() {
		return this.fluidOutput;
	}

	@Override
	public int getProcessingTime() {
		return this.processingTime;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	@Override
	public RecipeSerializer<? extends IForestryRecipe> getSerializer() {
		return FactoryRecipeTypes.SQUEEZER.serializer();
	}

	@Override
	public RecipeType<? extends IForestryRecipe> getType() {
		return FactoryRecipeTypes.SQUEEZER.type();
	}
}
