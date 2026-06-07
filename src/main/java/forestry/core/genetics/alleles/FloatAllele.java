package forestry.core.genetics.alleles;

import forestry.api.ForestryConstants;
import forestry.api.genetics.alleles.IFloatAllele;
import net.minecraft.resources.Identifier;

record FloatAllele(Identifier alleleId, float value, boolean dominant) implements IFloatAllele {
	FloatAllele(float value, boolean dominant) {
		this(createId(value, dominant), value, dominant);
	}

	private static Identifier createId(float value, boolean dominant) {
		return ForestryConstants.forestry(value + (dominant ? "fd" : "f"));
	}
}
