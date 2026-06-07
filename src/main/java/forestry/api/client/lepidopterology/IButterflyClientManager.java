package forestry.api.client.lepidopterology;

import com.mojang.datafixers.util.Pair;
import forestry.api.lepidopterology.genetics.IButterflySpecies;
import net.minecraft.resources.Identifier;

public interface IButterflyClientManager {
	/**
	 * @return The butterfly item and entity textures, respectively.
	 */
	Pair<Identifier, Identifier> getTextures(IButterflySpecies species);
}
