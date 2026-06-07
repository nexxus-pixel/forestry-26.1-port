package forestry.factory.recipes;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import forestry.api.recipes.IMoistenerRecipe;
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

public class MoistenerRecipe implements IMoistenerRecipe {
	public static final MapCodec<MoistenerRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.INT.fieldOf("time").forGetter(MoistenerRecipe::getTimePerItem),
		Ingredient.CODEC.fieldOf("resource").forGetter(MoistenerRecipe::getInput),
		ItemStack.CODEC.fieldOf("product").forGetter(MoistenerRecipe::getProduct)
	).apply(instance, (time, resource, product) -> new MoistenerRecipe(RecipeSerializers.UNBOUND_ID, resource, product, time)));

	public static final StreamCodec<RegistryFriendlyByteBuf, MoistenerRecipe> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT, MoistenerRecipe::getTimePerItem,
		Ingredient.CONTENTS_STREAM_CODEC, MoistenerRecipe::getInput,
		ItemStack.STREAM_CODEC, MoistenerRecipe::getProduct,
		(time, resource, product) -> new MoistenerRecipe(RecipeSerializers.UNBOUND_ID, resource, product, time)
	);

	public static final RecipeSerializer<MoistenerRecipe> SERIALIZER = RecipeSerializers.of(CODEC, STREAM_CODEC);

	private final Identifier id;
	private final int timePerItem;
	private final Ingredient resource;
	private final ItemStack product;

	public MoistenerRecipe(Identifier id, Ingredient resource, ItemStack product, int timePerItem) {
		Preconditions.checkNotNull(id, "Recipe identifier cannot be null");
		Preconditions.checkNotNull(resource, "Resource cannot be null");
		Preconditions.checkNotNull(product, "Product cannot be null");

		this.id = id;
		this.timePerItem = timePerItem;
		this.resource = resource;
		this.product = product;
	}

	@Override
	public int getTimePerItem() {
		return timePerItem;
	}

	@Override
	public Ingredient getInput() {
		return resource;
	}

	@Override
	public ItemStack getProduct() {
		return product;
	}

	@Override
	public Identifier getId() {
		return id;
	}

	@Override
	public RecipeSerializer<? extends IForestryRecipe> getSerializer() {
		return FactoryRecipeTypes.MOISTENER.serializer();
	}

	@Override
	public RecipeType<? extends IForestryRecipe> getType() {
		return FactoryRecipeTypes.MOISTENER.type();
	}
}
