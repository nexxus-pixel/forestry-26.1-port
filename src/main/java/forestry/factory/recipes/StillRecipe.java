package forestry.factory.recipes;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import forestry.api.recipes.IStillRecipe;
import forestry.api.recipes.IForestryRecipe;
import forestry.factory.features.FactoryRecipeTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;

public class StillRecipe implements IStillRecipe {
	public static final MapCodec<StillRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.INT.fieldOf("time").forGetter(StillRecipe::getCyclesPerUnit),
		RecipeSerializers.FLUID_STACK_CODEC.fieldOf("input").forGetter(StillRecipe::getInput),
		RecipeSerializers.FLUID_STACK_CODEC.fieldOf("output").forGetter(StillRecipe::getOutput)
	).apply(instance, RecipeSerializers.bindId(StillRecipe::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, StillRecipe> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT, StillRecipe::getCyclesPerUnit,
		RecipeSerializers.FLUID_STACK_STREAM_CODEC, StillRecipe::getInput,
		RecipeSerializers.FLUID_STACK_STREAM_CODEC, StillRecipe::getOutput,
		RecipeSerializers.bindId(StillRecipe::new)
	);

	public static final RecipeSerializer<StillRecipe> SERIALIZER = RecipeSerializers.of(CODEC, STREAM_CODEC);

	private final Identifier id;
	private final int timePerUnit;
	private final FluidStack input;
	private final FluidStack output;

	public StillRecipe(Identifier id, int timePerUnit, FluidStack input, FluidStack output) {
		Preconditions.checkNotNull(id, "Recipe identifier cannot be null");
		Preconditions.checkNotNull(input, "Still recipes need an input. Input was null.");
		Preconditions.checkNotNull(output, "Still recipes need an output. Output was null.");

		this.id = id;
		this.timePerUnit = timePerUnit;
		this.input = input;
		this.output = output;
	}

	@Override
	public int getCyclesPerUnit() {
		return this.timePerUnit;
	}

	@Override
	public FluidStack getInput() {
		return this.input;
	}

	@Override
	public FluidStack getOutput() {
		return this.output;
	}

	@Override
	public boolean matches(FluidStack input) {
		return input.containsFluid(this.input);
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	@Override
	public RecipeSerializer<? extends IForestryRecipe> getSerializer() {
		return FactoryRecipeTypes.STILL.serializer();
	}

	@Override
	public RecipeType<? extends IForestryRecipe> getType() {
		return FactoryRecipeTypes.STILL.type();
	}
}
