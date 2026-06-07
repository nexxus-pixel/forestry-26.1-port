package forestry.factory.recipes;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import forestry.api.recipes.ISqueezerContainerRecipe;
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

import java.util.List;

public class SqueezerContainerRecipe implements ISqueezerContainerRecipe {
	public static final MapCodec<SqueezerContainerRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ItemStack.CODEC.fieldOf("container").forGetter(SqueezerContainerRecipe::getEmptyContainer),
		Codec.INT.fieldOf("time").forGetter(SqueezerContainerRecipe::getProcessingTime),
		ItemStack.CODEC.fieldOf("remnants").forGetter(SqueezerContainerRecipe::getRemnants),
		Codec.FLOAT.fieldOf("remnantsChance").forGetter(SqueezerContainerRecipe::getRemnantsChance)
	).apply(instance, (container, time, remnants, chance) -> new SqueezerContainerRecipe(RecipeSerializers.UNBOUND_ID, container, time, remnants, chance)));

	public static final StreamCodec<RegistryFriendlyByteBuf, SqueezerContainerRecipe> STREAM_CODEC = StreamCodec.composite(
		ItemStack.STREAM_CODEC, SqueezerContainerRecipe::getEmptyContainer,
		ByteBufCodecs.VAR_INT, SqueezerContainerRecipe::getProcessingTime,
		ItemStack.STREAM_CODEC, SqueezerContainerRecipe::getRemnants,
		ByteBufCodecs.FLOAT, SqueezerContainerRecipe::getRemnantsChance,
		(container, time, remnants, chance) -> new SqueezerContainerRecipe(RecipeSerializers.UNBOUND_ID, container, time, remnants, chance)
	);

	public static final RecipeSerializer<SqueezerContainerRecipe> SERIALIZER = RecipeSerializers.of(CODEC, STREAM_CODEC);

	private final Identifier id;
	private final ItemStack emptyContainer;
	private final int processingTime;
	private final ItemStack remnants;
	private final float remnantsChance;

	public SqueezerContainerRecipe(Identifier id, ItemStack emptyContainer, int processingTime, ItemStack remnants, float remnantsChance) {
		this.id = id;
		Preconditions.checkNotNull(emptyContainer);
		Preconditions.checkArgument(!emptyContainer.isEmpty());
		Preconditions.checkNotNull(remnants);

		this.emptyContainer = emptyContainer;
		this.processingTime = processingTime;
		this.remnants = remnants;
		this.remnantsChance = remnantsChance;
	}

	@Override
	public ItemStack getEmptyContainer() {
		return this.emptyContainer;
	}

	@Override
	public List<Ingredient> getInputs() {
		return List.of();
	}

	@Override
	public int getProcessingTime() {
		return this.processingTime;
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
		return FluidStack.EMPTY;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	@Override
	public RecipeSerializer<? extends IForestryRecipe> getSerializer() {
		return FactoryRecipeTypes.SQUEEZER_CONTAINER.serializer();
	}

	@Override
	public RecipeType<? extends IForestryRecipe> getType() {
		return FactoryRecipeTypes.SQUEEZER_CONTAINER.type();
	}
}
