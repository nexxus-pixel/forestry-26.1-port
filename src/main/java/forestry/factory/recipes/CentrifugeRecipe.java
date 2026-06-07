package forestry.factory.recipes;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import forestry.api.core.Product;
import forestry.api.recipes.ICentrifugeRecipe;
import forestry.api.recipes.IForestryRecipe;
import forestry.factory.features.FactoryRecipeTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.List;

public class CentrifugeRecipe implements ICentrifugeRecipe {
	public static final MapCodec<CentrifugeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.INT.fieldOf("time").forGetter(CentrifugeRecipe::getProcessingTime),
		Ingredient.CODEC.fieldOf("input").forGetter(CentrifugeRecipe::getInput),
		Product.CODEC.listOf().fieldOf("products").forGetter(CentrifugeRecipe::getAllProducts)
	).apply(instance, RecipeSerializers.bindId(CentrifugeRecipe::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, CentrifugeRecipe> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT, CentrifugeRecipe::getProcessingTime,
		Ingredient.CONTENTS_STREAM_CODEC, CentrifugeRecipe::getInput,
		RecipeSerializers.PRODUCT_LIST_STREAM_CODEC, CentrifugeRecipe::getAllProducts,
		RecipeSerializers.bindId(CentrifugeRecipe::new)
	);

	public static final RecipeSerializer<CentrifugeRecipe> SERIALIZER = RecipeSerializers.of(CODEC, STREAM_CODEC);

	private final Identifier id;
	private final int processingTime;
	private final Ingredient input;
	private final List<Product> products;

	public CentrifugeRecipe(Identifier id, int processingTime, Ingredient input, List<Product> products) {
		Preconditions.checkNotNull(id, "Recipe identifier cannot be null");

		this.id = id;
		this.processingTime = processingTime;
		this.input = input;
		this.products = products;
	}

	@Override
	public Ingredient getInput() {
		return this.input;
	}

	@Override
	public int getProcessingTime() {
		return this.processingTime;
	}

	@Override
	public List<ItemStack> getProducts(RandomSource random, double outputMult) {
		ArrayList<ItemStack> products = new ArrayList<>();

		for (Product entry : this.products) {
			double probability = entry.chance() * outputMult;

			if (probability >= 1.0) {
				products.add(entry.createStack());
			} else if (random.nextFloat() < probability) {
				products.add(entry.createStack());
			}
		}

		return products;
	}

	@Override
	public List<Product> getAllProducts() {
		return this.products;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	@Override
	public RecipeSerializer<? extends IForestryRecipe> getSerializer() {
		return FactoryRecipeTypes.CENTRIFUGE.serializer();
	}

	@Override
	public RecipeType<? extends IForestryRecipe> getType() {
		return FactoryRecipeTypes.CENTRIFUGE.type();
	}
}
