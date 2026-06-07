package forestry.core.genetics.alleles;

import forestry.api.genetics.alleles.IValueAllele;
import net.minecraft.resources.Identifier;

public record ValueAllele<V>(Identifier alleleId, V value, boolean dominant) implements IValueAllele<V> {
}
