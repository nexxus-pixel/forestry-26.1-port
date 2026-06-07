package forestry.core.genetics.alleles;

import forestry.api.ForestryConstants;
import forestry.api.genetics.alleles.IIntegerAllele;
import net.minecraft.resources.Identifier;

record IntegerAllele(Identifier alleleId, int value, boolean dominant) implements IIntegerAllele {
	IntegerAllele(int value, boolean dominant) {
		this(createId(value, dominant), value, dominant);
	}

	private static Identifier createId(int value, boolean dominant) {
		return ForestryConstants.forestry(value + (dominant ? "id" : "i"));
	}
}
