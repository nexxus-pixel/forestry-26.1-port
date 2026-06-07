package forestry.apiculture.blocks;

import forestry.api.ForestryConstants;
import forestry.api.apiculture.ForestryBeeSpecies;
import forestry.api.core.IBlockSubtype;
import net.minecraft.resources.Identifier;

import java.util.Locale;

public enum BlockHiveType implements IBlockSubtype {
	FOREST(ForestryBeeSpecies.FOREST),
	MEADOWS(ForestryBeeSpecies.MEADOWS),
	DESERT(ForestryBeeSpecies.MODEST),
	JUNGLE(ForestryBeeSpecies.TROPICAL),
	END(ForestryBeeSpecies.ENDED),
	SNOW(ForestryBeeSpecies.WINTRY),
	SWAMP(ForestryBeeSpecies.MARSHY),
	SAVANNA(ForestryBeeSpecies.SAVANNA),
	LUSH(ForestryBeeSpecies.LUSH),
	AQUATIC(ForestryBeeSpecies.AQUATIC),
	NETHER(ForestryBeeSpecies.EMBITTERED),
	SWARM(ForestryConstants.forestry("none"));

	private final Identifier speciesUid;

	BlockHiveType(Identifier speciesUid) {
		this.speciesUid = speciesUid;
	}

	public Identifier getSpeciesId() {
		return this.speciesUid;
	}

	@Override
	public String getSerializedName() {
		return name().toLowerCase(Locale.ENGLISH);
	}
}
