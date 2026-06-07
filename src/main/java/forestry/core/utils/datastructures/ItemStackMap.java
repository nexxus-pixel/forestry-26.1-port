package forestry.core.utils.datastructures;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.io.Serial;
import java.util.IdentityHashMap;

// todo  remove
public class ItemStackMap<T> extends IdentityHashMap<Item, T> {
	@Serial
	private static final long serialVersionUID = -8511966739130702305L;

	@Override
	public boolean containsKey(Object key) {
		Item item = asItem(key);
		return item != null && super.containsKey(item);
	}

	@Override
	public T get(Object key) {
		Item item = asItem(key);
		return item != null ? super.get(item) : null;
	}

	private static Item asItem(Object key) {
		if (key instanceof Item item) {
			return item;
		}
		if (key instanceof ItemStack stack && !stack.isEmpty()) {
			return stack.getItem();
		}
		return null;
	}
}
