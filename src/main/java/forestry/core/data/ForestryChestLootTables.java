package forestry.core.data;

import forestry.api.ForestryConstants;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.BiConsumer;

public class ForestryChestLootTables implements LootTableSubProvider {
	@Override
	public void generate(BiConsumer<Identifier, LootTable.Builder> consumer) {
		consumer.accept(ForestryConstants.forestry("chests/village_naturalist"), LootTable.lootTable());
		for (LootTableHelper.Entry entry : LootTableHelper.getInstance().entries.values()) {
			consumer.accept(entry.getLocation(), entry.builder);
		}
	}
}
