package forestry.core.genetics.alleles;

import forestry.api.genetics.alleles.IFloatAllele;
import forestry.api.genetics.alleles.IFloatChromosome;
import net.minecraft.resources.Identifier;

public class FloatChromosome extends AbstractChromosome<IFloatAllele> implements IFloatChromosome {
	public FloatChromosome(Identifier id) {
		super(id);
	}

	@Override
	public Class<?> valueClass() {
		return float.class;
	}
}
