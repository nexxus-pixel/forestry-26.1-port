package forestry.apiimpl.client;

import forestry.api.apiculture.genetics.IBeeSpecies;
import forestry.api.client.apiculture.IBeeClientManager;
import forestry.api.genetics.ILifeStage;
import forestry.apiimpl.client.plugin.ClientRegistration;
import forestry.core.utils.SpeciesUtil;
import net.minecraft.resources.Identifier;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

public class BeeClientManager implements IBeeClientManager {
	private final ClientRegistration registration;
	private final IdentityHashMap<ILifeStage, Map<IBeeSpecies, Identifier>> resolvedModels = new IdentityHashMap<>();

	public BeeClientManager(ClientRegistration registration) {
		this.registration = registration;
	}

	@Override
	public Map<IBeeSpecies, Identifier> getBeeModels(ILifeStage stage) {
		return this.resolvedModels.computeIfAbsent(stage, this::resolveModels);
	}

	private Map<IBeeSpecies, Identifier> resolveModels(ILifeStage stage) {
		Map<Identifier, Identifier> locationsByStage = this.registration.getBeeModels().getOrDefault(stage, Map.of());
		Map<IBeeSpecies, Identifier> modelsByStage = new IdentityHashMap<>();

		for (IBeeSpecies species : SpeciesUtil.getAllBeeSpecies()) {
			Identifier modelLocation = locationsByStage.get(species.id());
			if (modelLocation == null) {
				modelLocation = Objects.requireNonNull(
					this.registration.getDefaultBeeModel(stage),
					"IClientRegistration.setDefaultBeeModel has not been called for life stage " + stage.getSerializedName()
				);
			}
			modelsByStage.put(species, modelLocation);
		}

		return modelsByStage;
	}
}
