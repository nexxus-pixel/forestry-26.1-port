package forestry.factory.recipes;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import forestry.api.recipes.ICarpenterRecipe;
import forestry.api.recipes.IForestryRecipe;
import forestry.factory.features.FactoryRecipeTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.Optional;

public class CarpenterRecipe implements ICarpenterRecipe {
	public static final MapCodec<CarpenterRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.INT.fieldOf("time").forGetter(CarpenterRecipe::getPackagingTime),
		RecipeSerializers.FLUID_STACK_CODEC.optionalFieldOf("liquid", FluidStack.EMPTY).forGetter(CarpenterRecipe::getInputFluid),
		Ingredient.CODEC.fieldOf("box").forGetter(CarpenterRecipe::getBox),
		RecipeSerializers.CRAFTING_RECIPE_CODEC.fieldOf("recipe").forGetter(CarpenterRecipe::getCraftingGridRecipe),
		ItemStack.CODEC.optionalFieldOf("result").forGetter(recipe -> Optional.ofNullable(recipe.result))
	).apply(instance, (time, liquid, box, internal, result) -> new CarpenterRecipe(
		RecipeSerializers.UNBOUND_ID, time, liquid, box, internal, result.orElse(null)
	)));

	public static final StreamCodec<RegistryFriendlyByteBuf, CarpenterRecipe> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT, CarpenterRecipe::getPackagingTime,
		RecipeSerializers.optionalFluidStreamCodec(), recipe -> recipe.liquid.isEmpty() ? Optional.empty() : Optional.of(recipe.liquid),
		Ingredient.CONTENTS_STREAM_CODEC, CarpenterRecipe::getBox,
		RecipeSerializers.CRAFTING_RECIPE_STREAM_CODEC, CarpenterRecipe::getCraftingGridRecipe,
		RecipeSerializers.optionalItemStreamCodec(), recipe -> Optional.ofNullable(recipe.result),
		(time, liquid, box, internal, result) -> new CarpenterRecipe(
			RecipeSerializers.UNBOUND_ID, time, liquid.orElse(FluidStack.EMPTY), box, internal, result.orElse(null)
		)
	);

	public static final RecipeSerializer<CarpenterRecipe> SERIALIZER = RecipeSerializers.of(CODEC, STREAM_CODEC);

	private final Identifier id;
	private final int packagingTime;
	private final FluidStack liquid;
	private final Ingredient box;
	private final CraftingRecipe recipe;
	@Nullable
	private final ItemStack result;

	public CarpenterRecipe(Identifier id, int packagingTime, FluidStack liquid, Ingredient box, CraftingRecipe recipe, @Nullable ItemStack result) {
		Preconditions.checkNotNull(id, "Recipe identifier cannot be null");
		Preconditions.checkNotNull(box);
		Preconditions.checkNotNull(recipe);

		this.id = id;
		this.packagingTime = packagingTime;
		this.liquid = liquid;
		this.box = box;
		this.recipe = recipe;
		this.result = result;
	}

	@Override
	public int getPackagingTime() {
		return this.packagingTime;
	}

	@Override
	public Ingredient getBox() {
		return this.box;
	}

	@Override
	public FluidStack getInputFluid() {
		return this.liquid;
	}

	@Override
	public CraftingRecipe getCraftingGridRecipe() {
		return this.recipe;
	}

	@Override
	public boolean matches(FluidStack fluid, ItemStack boxStack, Container craftingInventory, Level level) {
		FluidStack liquid = this.liquid;
		if (!liquid.isEmpty()) {
			if (fluid.isEmpty() || !fluid.containsFluid(liquid)) {
				return false;
			}
		}

		Ingredient box = this.box;
		if (!box.isEmpty() && !box.test(boxStack)) {
			return false;
		}

		return this.recipe.matches(FakeCraftingInventory.input(craftingInventory), level);
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	@Override
	public RecipeSerializer<? extends IForestryRecipe> getSerializer() {
		return FactoryRecipeTypes.CARPENTER.serializer();
	}

	@Override
	public RecipeType<? extends IForestryRecipe> getType() {
		return FactoryRecipeTypes.CARPENTER.type();
	}
}
