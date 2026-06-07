package forestry.core.genetics.alleles;

import forestry.api.genetics.alleles.IAllele;
import forestry.api.genetics.alleles.IChromosome;
import forestry.core.utils.GeneticsUtil;
import net.minecraft.util.Util;
import net.minecraft.resources.Identifier;

public abstract class AbstractChromosome<A extends IAllele> implements IChromosome<A> {
	protected final Identifier id;
	private final String translationKey;

	protected AbstractChromosome(Identifier id) {
		this.id = id;
		this.translationKey = Util.makeDescriptionId("chromosome", this.id);
	}

	@Override
	public Identifier id() {
		return this.id;
	}

	@Override
	public String getTranslationKey(A allele) {
		return GeneticsUtil.createTranslationKey("allele", this.id, allele.alleleId());
	}

	@Override
	public String getChromosomeTranslationKey() {
		return this.translationKey;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + '[' + this.id + ']';
	}
}
