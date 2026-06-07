package forestry.core.utils;

import forestry.core.utils.ItemStackUtil;
import forestry.api.recipes.*;
import forestry.core.ClientsideCode;
import forestry.core.fluids.FluidHelper;
import forestry.factory.features.FactoryRecipeTypes;
import forestry.modules.features.FeatureRecipeType;
import forestry.worktable.inventory.WorktableCraftingContainer;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecipeUtils {
	/**
	 * @return The global registry manager. {@code null} on server when there is no server, or when there is no world (on client).
	 */
	@Nullable
	public static RecipeManager getRecipeManager() {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		return server == null ? (FMLEnvironment.dist == Dist.CLIENT ? ClientsideCode.getRecipeManager() : null) : server.getRecipeManager();
	}

	public static RegistryAccess getRegistryAccess() {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		return server == null ? (FMLEnvironment.dist == Dist.CLIENT ? ClientsideCode.getRegistryAccess() : null) : server.registryAccess();
	}

	public static Registry<Fluid> getFluidRegistry() {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		return server == null ? (FMLEnvironment.dist == Dist.CLIENT ? ClientsideCode.getFluidRegistry() : null) : server.registryAccess().lookupOrThrow(Registries.FLUID);
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public static <T extends Recipe<?>> T getRecipe(RecipeType<T> recipeType, Identifier name) {
		RecipeManager manager = getRecipeManager();
		if (manager == null) {
			return null;
		}
		return manager.byKey(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.RECIPE, name))
			.map(holder -> (T) holder.value())
			.orElse(null);
	}

	public static <I extends RecipeInput, T extends Recipe<I>> List<T> getRecipes(RecipeType<T> recipeType, I inventory, @Nullable Level world) {
		RecipeManager manager = getRecipeManager();
		if (manager == null || world == null) {
			return Collections.emptyList();
		}
		return manager.getRecipes().stream()
			.filter(holder -> holder.value().getType() == recipeType)
			.map(holder -> (T) holder.value())
			.filter(recipe -> recipe.matches(inventory, world))
			.toList();
	}

	public static List<CraftingRecipe> findMatchingRecipes(CraftingContainer inventory, Level level) {
		return findMatchingRecipeHolders(inventory, level).stream().map(RecipeHolder::value).toList();
	}

	public static List<RecipeHolder<CraftingRecipe>> findMatchingRecipeHolders(CraftingContainer inventory, Level level) {
		RecipeManager manager = LevelRecipeUtil.getRecipeManager(level);
		if (manager == null) {
			return Collections.emptyList();
		}
		CraftingInput input = CraftingInput.of(inventory.getWidth(), inventory.getHeight(), inventory.getItems());
		return manager.getRecipes().stream()
			.filter(holder -> holder.value().getType() == RecipeType.CRAFTING)
			.map(holder -> (RecipeHolder<CraftingRecipe>) holder)
			.filter(holder -> holder.value().matches(input, level))
			.toList();
	}

	public static List<RecipeHolder<CraftingRecipe>> getRecipeHolders(RecipeType<CraftingRecipe> recipeType, CraftingContainer inventory, @Nullable Level world) {
		RecipeManager manager = getRecipeManager();
		if (manager == null || world == null) {
			return Collections.emptyList();
		}
		CraftingInput input = CraftingInput.of(inventory.getWidth(), inventory.getHeight(), inventory.getItems());
		return manager.getRecipes().stream()
			.filter(holder -> holder.value().getType() == recipeType)
			.map(holder -> (RecipeHolder<CraftingRecipe>) holder)
			.filter(holder -> holder.value().matches(input, world))
			.toList();
	}

	// Returns a crafting matrix for a certain recipe using available items
	@Nullable
	public static WorktableCraftingContainer getUsedMatrix(WorktableCraftingContainer originalMatrix, NonNullList<ItemStack> availableItems, Level level, CraftingRecipe recipe) {
		CraftingInput originalInput = toCraftingInput(originalMatrix);
		if (!recipe.matches(originalInput, level)) {
			return null;
		}

		ItemStack expectedOutput = recipe.assemble(originalInput);
		if (expectedOutput.isEmpty()) {
			return null;
		}

		WorktableCraftingContainer usedMatrix = new WorktableCraftingContainer();
		List<ItemStack> stockCopy = ItemStackUtil.condenseStacks(availableItems);

		for (int slot = 0; slot < originalMatrix.getContainerSize(); slot++) {
			ItemStack stack = originalMatrix.getItem(slot);

			if (!stack.isEmpty()) {
				ItemStack equivalent = getCraftingEquivalent(stockCopy, originalMatrix, slot, level, recipe, expectedOutput);
				if (equivalent.isEmpty()) {
					return null;
				} else {
					usedMatrix.setItem(slot, equivalent);
				}
			}
		}

		if (recipe.matches(toCraftingInput(usedMatrix), level)) {
			ItemStack output = recipe.assemble(toCraftingInput(usedMatrix));
			if (ItemStack.matches(output, expectedOutput)) {
				return usedMatrix;
			}
		}

		return null;
	}

	private static ItemStack getCraftingEquivalent(List<ItemStack> stockCopy, WorktableCraftingContainer originalMatrix, int slot, Level level, CraftingRecipe recipe, ItemStack expectedOutput) {
		ItemStack originalStack = originalMatrix.getItem(slot);
		for (ItemStack stockStack : stockCopy) {
			if (!stockStack.isEmpty()) {
				ItemStack singleStockStack = stockStack.copy();
				singleStockStack.setCount(1);
				originalMatrix.setItem(slot, singleStockStack);

				if (recipe.matches(toCraftingInput(originalMatrix), level)) {
					ItemStack output = recipe.assemble(toCraftingInput(originalMatrix));
					if (ItemStack.matches(output, expectedOutput)) {
						originalMatrix.setItem(slot, originalStack);
						return stockStack.split(1);
					}
				}
			}
		}
		originalMatrix.setItem(slot, originalStack);
		return ItemStack.EMPTY;
	}

	@Nullable
	public static IHygroregulatorRecipe getHygroRegulatorRecipe(RecipeManager manager, FluidStack input) {
		return getMatchingRecipe(manager, FactoryRecipeTypes.HYGROREGULATOR, recipe -> recipe.getInputFluid().isFluidEqual(input));
	}

	@Nullable
	public static IFermenterRecipe getFermenterRecipe(RecipeManager manager, ItemStack inputItem, FluidStack inputFluid) {
		return getMatchingRecipe(manager, FactoryRecipeTypes.FERMENTER, recipe -> recipe.matches(inputItem, inputFluid));
	}

	public static boolean isFermenterInput(RecipeManager manager, ItemStack stack) {
		return getRecipes(manager, FactoryRecipeTypes.FERMENTER)
			.anyMatch(recipe -> recipe.getInputItem().test(stack));
	}

	@Nullable
	public static ICarpenterRecipe getCarpenterRecipe(RecipeManager manager, FluidStack fluid, ItemStack boxStack, Container craftingInventory, Level level) {
		return getMatchingRecipe(manager, FactoryRecipeTypes.CARPENTER, recipe -> recipe.matches(fluid, boxStack, craftingInventory, level));
	}

	public static boolean isCarpenterBox(RecipeManager manager, ItemStack stack) {
		return getMatchingRecipe(manager, FactoryRecipeTypes.CARPENTER, recipe -> recipe.getBox().test(stack)) != null;
	}

	// Returns true if the item is part of any squeezer recipe.
	public static boolean isSqueezerIngredient(RecipeManager manager, ItemStack stack) {
		return getRecipes(manager, FactoryRecipeTypes.SQUEEZER).anyMatch(recipe -> {
			for (Ingredient ingredient : recipe.getInputs()) {
				if (ingredient.test(stack)) {
					return true;
				}
			}
			return false;
		});
	}

	@Nullable
	public static ISqueezerContainerRecipe getSqueezerContainerRecipe(RecipeManager manager, ItemStack stack) {
		if (!FluidHelper.isDrainableFilledContainer(stack)) {
			return null;
		}
		return getMatchingRecipe(manager, FactoryRecipeTypes.SQUEEZER_CONTAINER, recipe -> ItemStack.isSameItem(recipe.getEmptyContainer(), stack));
	}

	@Nullable
	public static ICentrifugeRecipe getCentrifugeRecipe(RecipeManager manager, ItemStack stack) {
		return getMatchingRecipe(manager, FactoryRecipeTypes.CENTRIFUGE, recipe -> recipe.getInput().test(stack));
	}

	@Nullable
	public static IFabricatorSmeltingRecipe getFabricatorMeltingRecipe(RecipeManager manager, ItemStack stack) {
		return getMatchingRecipe(manager, FactoryRecipeTypes.FABRICATOR_SMELTING, recipe -> recipe.getInput().test(stack));
	}

	@Nullable
	public static IFabricatorRecipe getFabricatorRecipe(RecipeManager manager, Level level, FluidStack liquid, ItemStack stack, Container inventory) {
		return getMatchingRecipe(manager, FactoryRecipeTypes.FABRICATOR, recipe -> recipe.matches(level, liquid, stack, inventory));
	}

	public static boolean isFabricatorPlan(RecipeManager manager, ItemStack stack) {
		return getMatchingRecipe(manager, FactoryRecipeTypes.FABRICATOR, recipe -> recipe.getPlan().test(stack)) != null;
	}

	@Nullable
	public static IMoistenerRecipe getMoistenerRecipe(RecipeManager manager, ItemStack stack) {
		return getMatchingRecipe(manager, FactoryRecipeTypes.MOISTENER, recipe -> recipe.getInput().test(stack));
	}

	@Nullable
	public static ISqueezerRecipe getSqueezerRecipe(RecipeManager manager, List<ItemStack> inputs) {
		return getMatchingRecipe(manager, FactoryRecipeTypes.SQUEEZER, recipe -> ItemStackUtil.createConsume(recipe.getInputs(), inputs.size(), inputs::get, false).length > 0);
	}

	@Nullable
	public static IStillRecipe getStillRecipe(RecipeManager manager, FluidStack input) {
		return getMatchingRecipe(manager, FactoryRecipeTypes.STILL, recipe -> recipe.matches(input));
	}

	@Nullable
	private static <R extends Recipe<?>> R getMatchingRecipe(RecipeManager manager, FeatureRecipeType<R> type, Predicate<R> matcher) {
		return getRecipes(manager, type)
			.filter(matcher)
			.findFirst()
			.orElse(null);
	}

	public static <R extends Recipe<?>> Stream<R> getRecipes(RecipeManager manager, FeatureRecipeType<R> type) {
		return manager.getRecipes().stream()
			.filter(holder -> holder.value().getType() == type.type())
			.map(holder -> (R) holder.value());
	}

	public static <R extends Recipe<?>> Set<Identifier> getTargetFluidsFromStacks(RecipeManager manager, RecipeType<R> type, Function<R, FluidStack> targetFluid) {
		return getTargetFluids(manager, type, recipe -> targetFluid.apply(recipe).getFluid());
	}

	public static <R extends Recipe<?>> Set<Identifier> getTargetFluids(RecipeManager manager, RecipeType<R> type, Function<R, Fluid> targetFluid) {
		return manager.getRecipes().stream()
			.filter(holder -> holder.value().getType() == type)
			.map(holder -> (R) holder.value())
			.map(value -> ModUtil.getRegistryName(targetFluid.apply(value)))
			.collect(Collectors.toSet());
	}

	public static <R extends Recipe<?>> R getRecipeByOutput(FeatureRecipeType<R> recipeType, RegistryAccess registryAccess, ItemStack output) {
		return getRecipes(getRecipeManager(), recipeType)
			.filter(recipe -> ItemStack.isSameItem(getRecipeResult(recipe), output))
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("Couldn't find a recipe with output: " + output));
	}

	private static CraftingInput toCraftingInput(CraftingContainer container) {
		return CraftingInput.of(container.getWidth(), container.getHeight(), container.getItems());
	}

	public static ItemStack getCraftingResult(CraftingRecipe recipe) {
		return recipe.assemble(CraftingInput.EMPTY);
	}

	public static List<Ingredient> getRecipeIngredients(Recipe<?> recipe) {
		return recipe.placementInfo().ingredients();
	}

	private static ItemStack getRecipeResult(Recipe<?> recipe) {
		if (recipe instanceof CraftingRecipe craftingRecipe) {
			return getCraftingResult(craftingRecipe);
		}
		if (recipe instanceof IForestryRecipe forestryRecipe) {
			return forestryRecipe.assemble(new ForestryContainerInput(new net.minecraft.world.SimpleContainer(9)));
		}
		return ItemStack.EMPTY;
	}
}
