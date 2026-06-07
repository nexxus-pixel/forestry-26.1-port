package forestry.core.genetics.alleles;

import forestry.api.genetics.alleles.IBooleanAllele;
import forestry.api.genetics.alleles.IBooleanChromosome;
import net.minecraft.util.Util;
import net.minecraft.resources.Identifier;

public record BooleanChromosome(Identifier id, String translationKey) implements IBooleanChromosome {
	public static BooleanChromosome create(Identifier id) {
		return new BooleanChromosome(id, Util.makeDescriptionId("chromosome", id));
	}

	@Override
	public String getTranslationKey(IBooleanAllele allele) {
		return allele.value() ? "allele.forestry.true" : "allele.forestry.false";
	}

	@Override
	public String getChromosomeTranslationKey() {
		return this.translationKey;
	}

	@Override
	public Class<?> valueClass() {
		return boolean.class;
	}
}
