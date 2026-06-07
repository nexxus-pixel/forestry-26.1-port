package forestry.factory.recipes;

import com.google.common.base.Preconditions;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import forestry.api.recipes.IFabricatorRecipe;
import forestry.api.recipes.IForestryRecipe;
import forestry.factory.features.FactoryRecipeTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

public class FabricatorRecipe implements IFabricatorRecipe {
	public static final MapCodec<FabricatorRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Ingredient.CODEC.fieldOf("plan").forGetter(FabricatorRecipe::getPlan),
		RecipeSerializers.FLUID_STACK_CODEC.fieldOf("molten").forGetter(FabricatorRecipe::getResultFluid),
		RecipeSerializers.SHAPED_RECIPE_CODEC.fieldOf("recipe").forGetter(FabricatorRecipe::getCraftingGridRecipe)
	).apply(instance, RecipeSerializers.bindId(FabricatorRecipe::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, FabricatorRecipe> STREAM_CODEC = StreamCodec.composite(
		Ingredient.CONTENTS_STREAM_CODEC, FabricatorRecipe::getPlan,
		RecipeSerializers.FLUID_STACK_STREAM_CODEC, FabricatorRecipe::getResultFluid,
		RecipeSerializers.SHAPED_RECIPE_STREAM_CODEC, FabricatorRecipe::getCraftingGridRecipe,
		RecipeSerializers.bindId(FabricatorRecipe::new)
	);

	public static final RecipeSerializer<FabricatorRecipe> SERIALIZER = RecipeSerializers.of(CODEC, STREAM_CODEC);

	private final Identifier id;
	private final Ingredient plan;
	private final FluidStack resultFluid;
	private final ShapedRecipe recipe;

	public FabricatorRecipe(Identifier id, Ingredient plan, FluidStack resultFluid, ShapedRecipe recipe) {
		Preconditions.checkNotNull(id, "Recipe identifier cannot be null");
		Preconditions.checkNotNull(plan);
		Preconditions.checkNotNull(resultFluid);

		this.id = id;
		this.plan = plan;
		this.resultFluid = resultFluid;
		this.recipe = recipe;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	@Override
	public Ingredient getPlan() {
		return this.plan;
	}

	@Override
	public FluidStack getResultFluid() {
		return this.resultFluid;
	}

	@Override
	public ShapedRecipe getCraftingGridRecipe() {
		return this.recipe;
	}

	@Override
	public boolean matches(Level level, FluidStack liquid, ItemStack stack, Container inventory) {
		return liquid.containsFluid(this.resultFluid) && this.plan.test(stack) && this.recipe.matches(FakeCraftingInventory.input(inventory), level);
	}

	@Override
	public RecipeSerializer<? extends IForestryRecipe> getSerializer() {
		return FactoryRecipeTypes.FABRICATOR.serializer();
	}

	@Override
	public RecipeType<? extends IForestryRecipe> getType() {
		return FactoryRecipeTypes.FABRICATOR.type();
	}
}
