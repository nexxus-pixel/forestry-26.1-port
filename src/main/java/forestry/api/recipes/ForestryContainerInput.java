package forestry.api.recipes;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * Recipe input adapter for Forestry machine inventories backed by {@link Container}.
 */
public record ForestryContainerInput(Container container) implements RecipeInput {
	@Override
	public ItemStack getItem(int slot) {
		return this.container.getItem(slot);
	}

	@Override
	public int size() {
		return this.container.getContainerSize();
	}
}
