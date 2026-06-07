package forestry.core.genetics;

import forestry.core.utils.CompoundTagUtil;

import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ISpecies;
import forestry.api.genetics.ISpeciesType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;

public class AlleleUtil {
	@Nullable
	public static <S extends ISpecies<I>, I extends IIndividual> S getSpecies(ISpeciesType<S, I> speciesType, CompoundTag nbt, String key) {
		String idString = nbt.getString(key).orElse("");
		if (idString.isEmpty()) {
			return null;
		}
		Identifier id = Identifier.parse(idString);
		return speciesType.getSpecies(id);
	}
}
