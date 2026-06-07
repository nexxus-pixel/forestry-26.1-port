package forestry.core.genetics.alleles;

import forestry.api.genetics.alleles.IValueAllele;
import forestry.api.genetics.alleles.IValueChromosome;
import net.minecraft.resources.Identifier;

public class ValueChromosome<V> extends AbstractChromosome<IValueAllele<V>> implements IValueChromosome<V> {
	protected final Class<V> valueClass;

	public ValueChromosome(Identifier id, Class<V> valueClass) {
		super(id);
		this.valueClass = valueClass;
	}

	@Override
	public Class<?> valueClass() {
		return this.valueClass;
	}
}
