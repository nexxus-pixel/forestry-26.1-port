package forestry.core.genetics.alleles;

import forestry.api.genetics.alleles.IRegistryAllele;
import forestry.api.genetics.alleles.IRegistryAlleleValue;
import forestry.api.genetics.alleles.IRegistryChromosome;
import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;

public class RegistryAllele<V extends IRegistryAlleleValue> implements IRegistryAllele<V> {
	private final Identifier id;
	private final IRegistryChromosome<V> chromosome;
	@Nullable
	private V value;

	// Do not call directly, use IAlleleManager.registryAllele
	RegistryAllele(Identifier id, IRegistryChromosome<V> chromosome) {
		this.id = id;
		this.chromosome = chromosome;
		((RegistryChromosome<V>) chromosome).add(id, this);
	}

	@Override
	public Identifier alleleId() {
		return this.id;
	}

	@Override
	public boolean dominant() {
		return value().isDominant();
	}

	@Override
	public V value() {
		if (this.value == null) {
			this.value = this.chromosome.get(this.id);
		}

		return this.value;
	}

	@Override
	public IRegistryChromosome<V> chromosome() {
		return this.chromosome;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + '[' + this.id + ']';
	}
}
