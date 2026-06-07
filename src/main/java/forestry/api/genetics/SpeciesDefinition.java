package forestry.api.genetics;

import net.minecraft.resources.Identifier;

public final class SpeciesDefinition {
	private final Identifier id;

	public SpeciesDefinition(Identifier id) {
		this.id = id;
	}

	public Identifier getId() {
		return this.id;
	}
}
