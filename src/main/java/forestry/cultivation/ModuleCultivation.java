package forestry.cultivation;

import forestry.api.modules.ForestryModule;
import forestry.api.modules.ForestryModuleIds;
import forestry.modules.BlankForestryModule;
import net.minecraft.resources.Identifier;

import java.util.List;

@ForestryModule
public class ModuleCultivation extends BlankForestryModule {
	@Override
	public Identifier getId() {
		return ForestryModuleIds.CULTIVATION;
	}

	@Override
	public List<Identifier> getModuleDependencies() {
		return List.of(ForestryModuleIds.CORE, ForestryModuleIds.FARMING);
	}
}
