package forestry.farming;

import forestry.api.modules.ForestryModule;
import forestry.api.modules.ForestryModuleIds;
import forestry.modules.BlankForestryModule;
import net.minecraft.resources.Identifier;

@ForestryModule
public class ModuleFarming extends BlankForestryModule {
	@Override
	public Identifier getId() {
		return ForestryModuleIds.FARMING;
	}
}
