package forestry.core.genetics.alleles;

import forestry.api.genetics.alleles.IIntegerAllele;
import forestry.api.genetics.alleles.IIntegerChromosome;
import net.minecraft.resources.Identifier;

public class IntegerChromosome extends AbstractChromosome<IIntegerAllele> implements IIntegerChromosome {
	public IntegerChromosome(Identifier id) {
		super(id);
	}

	@Override
	public Class<?> valueClass() {
		return int.class;
	}
}
