package forestry.api.core;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public interface IItemProvider<I extends Item> {
	I item();

	default ItemStack stack() {
		return stack(1);
	}

	default ItemStack stack(int amount) {
		Item item = item();
		Holder.Reference<Item> holder = item.builtInRegistryHolder();
		if (holder.areComponentsBound()) {
			return new ItemStack(holder, amount);
		}
		ResourceKey<Item> key = holder.unwrapKey().orElseThrow();
		Item resolved = ForgeRegistries.ITEMS.getValue(key.identifier());
		return new ItemStack(resolved, amount);
	}

	default boolean itemEqual(ItemStack stack) {
		return !stack.isEmpty() && itemEqual(stack.getItem());
	}

	default boolean itemEqual(Item item) {
		return item() == item;
	}
}
