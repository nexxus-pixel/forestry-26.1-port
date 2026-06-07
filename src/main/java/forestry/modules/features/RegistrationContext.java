package forestry.modules.features;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.function.Supplier;

/**
 * Thread-local registration context so block/item constructors can call {@link #blockProperties()}
 * or receive {@link #withItemId} during deferred registration.
 * Uses a stack so nested block construction (e.g. slab from plank) preserves the outer block id.
 */
public final class RegistrationContext {
	private static final ThreadLocal<ArrayDeque<ResourceKey<Block>>> BLOCK_KEYS = ThreadLocal.withInitial(ArrayDeque::new);
	private static final ThreadLocal<ArrayDeque<ResourceKey<Item>>> ITEM_KEYS = ThreadLocal.withInitial(ArrayDeque::new);

	private RegistrationContext() {
	}

	public static BlockBehaviour.Properties blockProperties() {
		return withBlockId(BlockBehaviour.Properties.of());
	}

	public static BlockBehaviour.Properties of(java.util.function.UnaryOperator<BlockBehaviour.Properties> config) {
		return withBlockId(config.apply(BlockBehaviour.Properties.of()));
	}

	public static Item.Properties itemProperties() {
		Item.Properties properties = new Item.Properties();
		ResourceKey<Item> key = currentItemKey();
		return key != null ? properties.setId(key) : properties;
	}

	public static Item.Properties withItemId(Item.Properties properties) {
		ResourceKey<Item> key = currentItemKey();
		return key != null ? properties.setId(key) : properties;
	}

	public static BlockBehaviour.Properties withBlockId(BlockBehaviour.Properties properties) {
		ResourceKey<Block> key = currentBlockKey();
		return key != null ? properties.setId(key) : properties;
	}

	public static <T> T withBlock(ResourceKey<Block> key, Supplier<T> action) {
		BLOCK_KEYS.get().push(key);
		try {
			return action.get();
		} finally {
			BLOCK_KEYS.get().pop();
		}
	}

	public static <T> T withItem(ResourceKey<Item> key, Supplier<T> action) {
		ITEM_KEYS.get().push(key);
		try {
			return action.get();
		} finally {
			ITEM_KEYS.get().pop();
		}
	}

	@Nullable
	private static ResourceKey<Block> currentBlockKey() {
		ArrayDeque<ResourceKey<Block>> stack = BLOCK_KEYS.get();
		return stack.isEmpty() ? null : stack.peek();
	}

	@Nullable
	private static ResourceKey<Item> currentItemKey() {
		ArrayDeque<ResourceKey<Item>> stack = ITEM_KEYS.get();
		return stack.isEmpty() ? null : stack.peek();
	}

	public static ResourceKey<Item> itemKey(Identifier id) {
		return ResourceKey.create(Registries.ITEM, id);
	}

	public static ResourceKey<Block> blockKey(Identifier id) {
		return ResourceKey.create(Registries.BLOCK, id);
	}
}
