package forestry.api.genetics.alyzer;

import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ILifeStage;
import forestry.apiculture.genetics.IGeneticTooltipProvider;
import net.minecraft.resources.Identifier;

import java.util.function.Predicate;

public interface IAlleleDisplayHelper {
	void addTooltip(IGeneticTooltipProvider<? extends IIndividual> provider, Identifier id, int orderingInfo);

	void addTooltip(IGeneticTooltipProvider<? extends IIndividual> provider, Identifier id, int orderingInfo, Predicate<ILifeStage> typeFilter);

	void addAlyzer(IGeneticTooltipProvider<?> provider, Identifier id, int orderingInfo);

}
