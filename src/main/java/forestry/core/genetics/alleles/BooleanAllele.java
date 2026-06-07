package forestry.core.genetics.alleles;

import forestry.api.ForestryConstants;
import forestry.api.genetics.alleles.IBooleanAllele;
import net.minecraft.resources.Identifier;

public record BooleanAllele(Identifier alleleId, boolean value, boolean dominant) implements IBooleanAllele {
	BooleanAllele(boolean value, boolean dominant) {
		this(createId(value, dominant), value, dominant);
	}

	private static Identifier createId(boolean value, boolean dominant) {
		return ForestryConstants.forestry(dominant ? Boolean.toString(value) + 'd' : Boolean.toString(value));
	}
}
