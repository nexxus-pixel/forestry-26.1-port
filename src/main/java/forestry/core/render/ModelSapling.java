package forestry.core.render;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import forestry.api.arboriculture.ForestryTreeSpecies;
import forestry.api.arboriculture.ITreeSpecies;
import forestry.api.client.IForestryClientApi;
import forestry.api.client.arboriculture.ITreeClientManager;
import forestry.core.utils.SpeciesUtil;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.geometry.UnbakedGeometry;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.StandaloneGeometryBakingContext;

import java.util.IdentityHashMap;
import java.util.Objects;

public class ModelSapling implements UnbakedGeometry {
	@Override
	public QuadCollection bake(TextureSlots textureSlots, ModelBaker baker, net.minecraft.client.renderer.block.dispatch.ModelState modelState, ModelDebugName debugName) {
		return bake(textureSlots, baker, modelState, debugName, StandaloneGeometryBakingContext.INSTANCE);
	}

	@Override
	public QuadCollection bake(TextureSlots textureSlots, ModelBaker baker, net.minecraft.client.renderer.block.dispatch.ModelState modelState, ModelDebugName debugName, IGeometryBakingContext context) {
		try {
			ITreeClientManager manager = IForestryClientApi.INSTANCE.getTreeManager();
			IdentityHashMap<ITreeSpecies, GeneticsRenderData> itemModels = new IdentityHashMap<>();
			IdentityHashMap<ITreeSpecies, GeneticsRenderData> blockModels = new IdentityHashMap<>();

			for (ITreeSpecies species : SpeciesUtil.getAllTreeSpecies()) {
				var pair = manager.getSaplingModels(species);
				itemModels.put(species, bakeModel(baker, pair.getSecond()));
				blockModels.put(species, bakeModel(baker, pair.getFirst()));
			}

			SaplingModelBakeCache.put(itemModels, blockModels);
			ITreeSpecies oak = SpeciesUtil.getTreeSpecies(ForestryTreeSpecies.OAK);
			return Objects.requireNonNull(blockModels.get(oak)).quads();
		} catch (RuntimeException e) {
			ResolvedModel resolved = baker.getModel(Identifier.fromNamespaceAndPath("minecraft", "block/oak_sapling"));
			return resolved.bakeTopGeometry(resolved.getTopTextureSlots(), baker, BlockModelRotation.IDENTITY);
		}
	}

	private static GeneticsRenderData bakeModel(ModelBaker baker, Identifier location) {
		ResolvedModel resolved = baker.getModel(location);
		TextureSlots slots = resolved.getTopTextureSlots();
		QuadCollection quads = resolved.bakeTopGeometry(slots, baker, BlockModelRotation.IDENTITY);
		ModelRenderProperties properties = ModelRenderProperties.fromResolvedModel(baker, resolved, slots);
		return new GeneticsRenderData(quads, properties);
	}

	public static class Loader implements IGeometryLoader {
		private static final ModelSapling MODEL = new ModelSapling();

		@Override
		public UnbakedGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
			return MODEL;
		}
	}
}
