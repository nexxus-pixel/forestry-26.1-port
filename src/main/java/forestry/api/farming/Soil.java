package forestry.api.farming;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public record Soil(Item resource, BlockState soilState) {
	public ItemStack resourceStack() {
		return new ItemStack(this.resource);
	}
}
