package forestry.core.genetics.alleles;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import forestry.Forestry;
import forestry.api.genetics.alleles.IAllele;
import forestry.api.genetics.alleles.IRegistryAllele;
import forestry.api.genetics.alleles.IRegistryAlleleValue;
import forestry.api.genetics.alleles.IRegistryChromosome;
import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;
import java.util.*;

public class RegistryChromosome<V extends IRegistryAlleleValue> extends ValueChromosome<V> implements IRegistryChromosome<V> {
	private final HashMap<Identifier, IRegistryAllele<V>> alleles = new HashMap<>();
	@Nullable
	private ImmutableMap<Identifier, V> registry;
	@Nullable
	private IdentityHashMap<V, Identifier> reverseLookup;

	public RegistryChromosome(Identifier id, Class<V> valueClass) {
		super(id, valueClass);
	}

	@Override
	public boolean isValidAllele(IAllele allele) {
		Preconditions.checkState(this.registry != null, "Registry not yet populated");
		return this.registry.containsKey(allele.alleleId());
	}

	@Override
	public V get(Identifier id) {
		Preconditions.checkState(this.registry != null, "Registry not yet populated");
		V value = this.registry.get(id);
		if (value == null) {
			throw new RuntimeException("No allele registered for chromosome " + this.id + " with ID: " + id);
		}
		return value;
	}

	@Nullable
	@Override
	public V getSafe(Identifier id) {
		Preconditions.checkState(this.registry != null, "Registry not yet populated");
		return this.registry.get(id);
	}

	@Override
	public Collection<V> values() {
		Preconditions.checkState(this.registry != null, "Registry not yet populated");

		return this.registry.values();
	}

	@Override
	public Collection<IRegistryAllele<V>> alleles() {
		Preconditions.checkState(this.registry != null, "Registry not yet populated");

		return Collections.unmodifiableCollection(this.alleles.values());
	}

	@Override
	public Identifier getId(V value) {
		Preconditions.checkState(this.reverseLookup != null, "Registry not yet populated");

		return this.reverseLookup.get(value);
	}

	@Override
	public void populate(ImmutableMap<Identifier, V> registry) {
		Preconditions.checkState(this.registry == null, "Registry has already been populated");

		this.registry = registry;
		this.reverseLookup = new IdentityHashMap<>(registry.size());

		for (Map.Entry<Identifier, V> entry : registry.entrySet()) {
			this.reverseLookup.put(entry.getValue(), entry.getKey());
		}

		for (Identifier alleleId : this.alleles.keySet()) {
			if (!registry.containsKey(alleleId)) {
				Forestry.LOGGER.warn("No IRegistryAllele found for registered value {}, did you forget to create one?", alleleId);
			}
		}
	}

	@Override
	public boolean isPopulated() {
		return this.registry != null;
	}

	// called by RegistryAllele
	void add(Identifier id, RegistryAllele<V> allele) {
		this.alleles.put(id, allele);
	}
}
