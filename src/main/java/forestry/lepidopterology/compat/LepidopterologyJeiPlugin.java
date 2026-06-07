package forestry.lepidopterology.compat;

import forestry.api.genetics.alleles.ButterflyChromosomes;
import forestry.api.modules.ForestryModuleIds;
import forestry.core.utils.JeiUtil;
import forestry.core.utils.SpeciesUtil;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.Identifier;

@JeiPlugin
public class LepidopterologyJeiPlugin implements IModPlugin {
	@Override
	public Identifier getPluginUid() {
		return ForestryModuleIds.LEPIDOPTEROLOGY;
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registry) {
		JeiUtil.registerItemSubtypes(registry, ButterflyChromosomes.SPECIES, SpeciesUtil.BUTTERFLY_TYPE.get());
	}
}
