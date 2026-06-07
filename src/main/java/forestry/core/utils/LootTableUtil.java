package forestry.core.utils;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;

/**
 * Helpers for modifying loot tables at runtime (LootTableLoadEvent).
 * LootPool entries are no longer mutable in 26.1; extra drops are added via a supplementary pool.
 */
public final class LootTableUtil {
	private LootTableUtil() {
	}

	public static void addSupplementaryPool(LootTable table, String poolName, LootPoolEntryContainer.Builder<?> entry) {
		table.addPool(LootPool.lootPool()
			.name(poolName)
			.add(entry)
			.build());
	}
}
