package forestry.apiculture;

import forestry.api.genetics.alleles.IAllele;
import forestry.api.genetics.alleles.IChromosome;
import net.minecraft.resources.Identifier;

import java.util.Map;

public record VillageHive(Identifier speciesId, Map<IChromosome<?>, IAllele> alleles) {
}
