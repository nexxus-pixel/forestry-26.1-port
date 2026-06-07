package forestry.apiimpl.client;

import com.mojang.datafixers.util.Pair;
import forestry.api.client.lepidopterology.IButterflyClientManager;
import forestry.api.lepidopterology.genetics.IButterflySpecies;
import net.minecraft.resources.Identifier;

import java.util.IdentityHashMap;

public class ButterflyClientManager implements IButterflyClientManager {
	private final IdentityHashMap<IButterflySpecies, Pair<Identifier, Identifier>> textures;

	public ButterflyClientManager(IdentityHashMap<IButterflySpecies, Pair<Identifier, Identifier>> textures) {
		this.textures = textures;
	}

	@Override
	public Pair<Identifier, Identifier> getTextures(IButterflySpecies species) {
		return this.textures.get(species);
	}
}
