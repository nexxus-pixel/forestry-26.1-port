package forestry.api.recipes;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public interface IForestryRecipe extends Recipe<ForestryContainerInput> {
	Identifier getId();

	@Override
	RecipeSerializer<? extends IForestryRecipe> getSerializer();

	@Override
	RecipeType<? extends IForestryRecipe> getType();

	@Override
	default boolean matches(ForestryContainerInput inv, Level level) {
		return false;
	}

	@Override
	default ItemStack assemble(ForestryContainerInput inv) {
		return ItemStack.EMPTY;
	}

	@Override
	default boolean isSpecial() {
		return true;
	}

	@Override
	default boolean showNotification() {
		return false;
	}

	@Override
	default String group() {
		return "forestry";
	}

	@Override
	default PlacementInfo placementInfo() {
		return PlacementInfo.NOT_PLACEABLE;
	}

	@Override
	default RecipeBookCategory recipeBookCategory() {
		return new RecipeBookCategory();
	}
}
