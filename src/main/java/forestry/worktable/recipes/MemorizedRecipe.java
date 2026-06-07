package forestry.worktable.recipes;

import forestry.core.utils.CompoundTagUtil;

import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;
import forestry.core.network.IStreamable;
import forestry.core.utils.InventoryUtil;
import forestry.core.utils.NetworkUtil;
import forestry.core.utils.RecipeUtils;
import forestry.worktable.inventory.WorktableCraftingContainer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MemorizedRecipe implements INbtWritable, INbtReadable, IStreamable {
	private WorktableCraftingContainer craftMatrix = new WorktableCraftingContainer();
	private List<CraftingRecipe> recipes = new ArrayList<>();
	private final List<Identifier> recipeIds = new ArrayList<>();
	private int selectedRecipe;
	private long lastUsed;
	private boolean locked;

	public MemorizedRecipe(FriendlyByteBuf buffer) {
		readData(buffer);
	}

	public MemorizedRecipe(CompoundTag nbt) {
		read(nbt);
	}

	public MemorizedRecipe(CraftingContainer craftMatrix, List<CraftingRecipe> recipes) {
		this(craftMatrix, recipes, List.of());
	}

	public MemorizedRecipe(CraftingContainer craftMatrix, List<CraftingRecipe> recipes, List<Identifier> ids) {
		InventoryUtil.deepCopyInventoryContents(craftMatrix, this.craftMatrix);
		this.recipes = recipes;
		if (!ids.isEmpty()) {
			this.recipeIds.addAll(ids);
		}
	}

	public MemorizedRecipe(CraftingContainer craftMatrix, List<RecipeHolder<CraftingRecipe>> recipeHolders, boolean useHolders) {
		InventoryUtil.deepCopyInventoryContents(craftMatrix, this.craftMatrix);
		for (RecipeHolder<CraftingRecipe> holder : recipeHolders) {
			this.recipes.add(holder.value());
			this.recipeIds.add(holder.id().identifier());
		}
	}

	public WorktableCraftingContainer getCraftMatrix() {
		return this.craftMatrix;
	}

	public void setCraftMatrix(WorktableCraftingContainer usedMatrix) {
		this.craftMatrix = usedMatrix;
	}

	public void incrementRecipe() {
        this.selectedRecipe++;
		if (this.selectedRecipe >= this.recipes.size()) {
            this.selectedRecipe = 0;
		}
	}

	public void decrementRecipe() {
        this.selectedRecipe--;
		if (this.selectedRecipe < 0) {
            this.selectedRecipe = this.recipes.size() - 1;
		}
	}

	public boolean hasRecipeConflict() {
		return this.recipes.size() > 1;
	}

	public void removeRecipeConflicts() {
		CraftingRecipe recipe = getSelectedRecipe();
        this.recipes.clear();
        this.recipes.add(recipe);
        this.selectedRecipe = 0;
	}

	public ItemStack getOutputIcon(Level level) {
		CraftingRecipe selectedRecipe = getSelectedRecipe();
		if (selectedRecipe != null) {
			ItemStack recipeOutput = selectedRecipe.assemble(CraftingInput.of(this.craftMatrix.getWidth(), this.craftMatrix.getHeight(), this.craftMatrix.getItems()));
			if (!recipeOutput.isEmpty()) {
				return recipeOutput;
			}
		}
		return ItemStack.EMPTY;
	}

	public ItemStack getCraftingResult(CraftingContainer inventory, Level level) {
		CraftingRecipe selectedRecipe = getSelectedRecipe();
		if (selectedRecipe != null && selectedRecipe.matches(CraftingInput.of(inventory.getWidth(), inventory.getHeight(), inventory.getItems()), level)) {
			ItemStack recipeOutput = selectedRecipe.assemble(CraftingInput.of(inventory.getWidth(), inventory.getHeight(), inventory.getItems()));
			if (!recipeOutput.isEmpty()) {
				return recipeOutput;
			}
		}
		return ItemStack.EMPTY;
	}

	public boolean hasRecipes() {
		return (!this.recipes.isEmpty() || !this.recipeIds.isEmpty());
	}

	public boolean hasSelectedRecipe() {
		return hasRecipes() && this.selectedRecipe >= 0 && this.recipeIds.size() > this.selectedRecipe && this.recipeIds.get(this.selectedRecipe) != null;
	}

	public List<CraftingRecipe> getRecipes() {
		if (this.recipes.isEmpty() && !this.recipeIds.isEmpty()) {
			for (Identifier key : this.recipeIds) {
				CraftingRecipe recipe = RecipeUtils.getRecipe(RecipeType.CRAFTING, key);
				if (recipe != null) {
                    this.recipes.add(recipe);
				}
			}
			if (this.selectedRecipe > this.recipes.size()) {
                this.selectedRecipe = 0;
			}
		}
		return this.recipes;
	}

	@Nullable
	public CraftingRecipe getSelectedRecipe() {
		List<CraftingRecipe> recipes = getRecipes();
		if (recipes.isEmpty()) {
			return null;
		} else {
			return recipes.get(this.selectedRecipe);
		}
	}

	public boolean hasRecipe(@Nullable CraftingRecipe recipe) {
		return getRecipes().contains(recipe);
	}

	public void updateLastUse(long lastUsed) {
		this.lastUsed = lastUsed;
	}

	public long getLastUsed() {
		return this.lastUsed;
	}

	public void toggleLock() {
        this.locked = !this.locked;
	}

	public boolean isLocked() {
		return this.locked;
	}

	@Override
	public final void read(CompoundTag compoundNBT) {
		InventoryUtil.readFromNBT(this.craftMatrix, "inventory", compoundNBT);
        this.lastUsed = CompoundTagUtil.getLong(compoundNBT, "LastUsed");
        this.locked = CompoundTagUtil.getBoolean(compoundNBT, "Locked");

		if (compoundNBT.contains("SelectedRecipe")) {
            this.selectedRecipe = CompoundTagUtil.getInt(compoundNBT, "SelectedRecipe");
		}

        this.recipes.clear();
        this.recipeIds.clear();
		ListTag recipesNbt = CompoundTagUtil.getList(compoundNBT, "Recipes");
		for (int i = 0; i < recipesNbt.size(); i++) {
			String recipeKey = CompoundTagUtil.getString(recipesNbt, i);
            this.recipeIds.add(Identifier.parse(recipeKey));
		}

		if (this.selectedRecipe > this.recipeIds.size()) {
            this.selectedRecipe = 0;
		}
	}

	@Override
	public CompoundTag write(CompoundTag compoundNBT) {
		InventoryUtil.writeToNBT(this.craftMatrix, "inventory", compoundNBT);
		compoundNBT.putLong("LastUsed", this.lastUsed);
		compoundNBT.putBoolean("Locked", this.locked);
		compoundNBT.putInt("SelectedRecipe", this.selectedRecipe);

		ListTag recipesNbt = new ListTag();
		for (Identifier recipeName : this.recipeIds) {
			recipesNbt.add(StringTag.valueOf(recipeName.toString()));
		}
		compoundNBT.put("Recipes", recipesNbt);

		return compoundNBT;
	}

	@Override
	public void writeData(FriendlyByteBuf data) {
		NetworkUtil.writeInventory(data, this.craftMatrix);
		data.writeBoolean(this.locked);
		data.writeVarInt(this.selectedRecipe);

		data.writeVarInt(this.recipeIds.size());
		for (Identifier recipeName : this.recipeIds) {
			data.writeIdentifier(recipeName);
		}
	}

	@Override
	public void readData(FriendlyByteBuf data) {
		NetworkUtil.readInventory(data, this.craftMatrix);
        this.locked = data.readBoolean();
        this.selectedRecipe = data.readVarInt();

        this.recipes.clear();
        this.recipeIds.clear();
		int recipeCount = data.readVarInt();
		for (int i = 0; i < recipeCount; i++) {
			Identifier recipeId = data.readIdentifier();
            this.recipeIds.add(recipeId);
		}
	}
}
