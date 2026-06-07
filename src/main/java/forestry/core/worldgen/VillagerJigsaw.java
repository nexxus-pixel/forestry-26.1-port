package forestry.core.worldgen;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import forestry.api.ForestryConstants;
import forestry.Forestry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import java.util.ArrayList;
import java.util.List;

public class VillagerJigsaw {
	private static final ResourceKey<StructureProcessorList> EMPTY_PROCESSOR_LIST_KEY = ResourceKey.create(Registries.PROCESSOR_LIST, Identifier.fromNamespaceAndPath("minecraft", "empty"));

	public static void init(Registry<StructureTemplatePool> pools, Registry<StructureProcessorList> processors) {
		addVillagerHouse(pools, processors, "plains", 15);
		addVillagerHouse(pools, processors, "snowy", 15);
		addVillagerHouse(pools, processors, "savanna", 15);
		addVillagerHouse(pools, processors, "desert", 15);
		addVillagerHouse(pools, processors, "taiga", 15);
	}

	private static void addVillagerHouse(Registry<StructureTemplatePool> pools, Registry<StructureProcessorList> processors, String biome, int weight) {
		addToJigsawPattern(pools, Identifier.parse("village/" + biome + "/houses"), new ApiaristPoolElement(Either.left(ForestryConstants.forestry("village/apiarist_house_" + biome + "_1")), processors.getOrThrow(EMPTY_PROCESSOR_LIST_KEY)), weight);
	}

	public static void addToJigsawPattern(Registry<StructureTemplatePool> pools, Identifier pool, StructurePoolElement newPiece, int weight) {
		StructureTemplatePool oldPool = pools.getValue(pool);
		if (oldPool == null) {
			return;
		}

		List<Pair<StructurePoolElement, Integer>> templates = oldPool.getTemplates();
		if (!(templates instanceof ArrayList<Pair<StructurePoolElement, Integer>> mutableTemplates)) {
			Forestry.LOGGER.warn("Unable to modify jigsaw pool {} at runtime; migrate apiarist houses to datapacks", pool);
			return;
		}

		mutableTemplates.add(new Pair<>(newPiece, weight));
	}
}
