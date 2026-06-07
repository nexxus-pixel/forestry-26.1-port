package forestry.apiculture.models;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import forestry.api.ForestryConstants;
import forestry.api.apiculture.genetics.BeeLifeStage;
import forestry.api.apiculture.genetics.IBeeSpecies;
import forestry.api.client.IForestryClientApi;
import forestry.api.client.apiculture.IBeeClientManager;
import forestry.api.genetics.ILifeStage;
import forestry.core.render.BeeModelBakeCache;
import forestry.core.utils.SpeciesUtil;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.geometry.UnbakedGeometry;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.StandaloneGeometryBakingContext;

import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;

public class ModelBee implements UnbakedGeometry {
	private final ILifeStage stage;

	public ModelBee(ILifeStage stage) {
		this.stage = stage;
	}

	@Override
	public QuadCollection bake(TextureSlots textureSlots, ModelBaker baker, net.minecraft.client.renderer.block.dispatch.ModelState modelState, ModelDebugName debugName) {
		return bake(textureSlots, baker, modelState, debugName, StandaloneGeometryBakingContext.INSTANCE);
	}

	@Override
	public QuadCollection bake(TextureSlots textureSlots, ModelBaker baker, net.minecraft.client.renderer.block.dispatch.ModelState modelState, ModelDebugName debugName, IGeometryBakingContext context) {
		try {
			IBeeClientManager manager = IForestryClientApi.INSTANCE.getBeeManager();
			Map<IBeeSpecies, net.minecraft.resources.Identifier> models = manager.getBeeModels(this.stage);
			IdentityHashMap<IBeeSpecies, BeeModelBakeCache.SpeciesRenderData> speciesData = new IdentityHashMap<>();
			QuadCollection fallback = QuadCollection.EMPTY;

			for (IBeeSpecies species : SpeciesUtil.getAllBeeSpecies()) {
				net.minecraft.resources.Identifier location = models.get(species);
				BeeModelBakeCache.SpeciesRenderData data = bakeSpeciesModel(baker, location);
				speciesData.put(species, data);

				if (fallback == QuadCollection.EMPTY) {
					fallback = data.quads();
				}
			}

			BeeModelBakeCache.put(this.stage, speciesData);
			return fallback;
		} catch (IllegalStateException e) {
			return bakeSpeciesModel(baker, defaultModelFor(this.stage)).quads();
		}
	}

	private static BeeModelBakeCache.SpeciesRenderData bakeSpeciesModel(ModelBaker baker, net.minecraft.resources.Identifier location) {
		ResolvedModel resolved = baker.getModel(location);
		TextureSlots slots = resolved.getTopTextureSlots();
		QuadCollection quads = resolved.bakeTopGeometry(slots, baker, BlockModelRotation.IDENTITY);
		ModelRenderProperties properties = ModelRenderProperties.fromResolvedModel(baker, resolved, slots);
		return new BeeModelBakeCache.SpeciesRenderData(quads, properties);
	}

	private static net.minecraft.resources.Identifier defaultModelFor(ILifeStage stage) {
		if (stage == BeeLifeStage.DRONE) {
			return ForestryConstants.forestry("item/bee_drone_default");
		}
		if (stage == BeeLifeStage.PRINCESS) {
			return ForestryConstants.forestry("item/bee_princess_default");
		}
		if (stage == BeeLifeStage.QUEEN) {
			return ForestryConstants.forestry("item/bee_queen_default");
		}
		if (stage == BeeLifeStage.LARVAE) {
			return ForestryConstants.forestry("item/bee_larvae_default");
		}
		return ForestryConstants.forestry("item/bee_drone_default");
	}

	public static class Loader implements IGeometryLoader {
		private final ModelBee[] models = new ModelBee[BeeLifeStage.values().length];

		@Override
		public UnbakedGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
			String stageName = GsonHelper.getAsString(jsonObject, "stage");
			BeeLifeStage stage = BeeLifeStage.valueOf(stageName.toUpperCase(Locale.ENGLISH));
			int ordinal = stage.ordinal();

			if (this.models[ordinal] == null) {
				this.models[ordinal] = new ModelBee(stage);
			}

			return this.models[ordinal];
		}
	}
}
