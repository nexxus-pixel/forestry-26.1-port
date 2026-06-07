package forestry.core.render;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import forestry.api.ForestryConstants;
import forestry.api.client.IForestryClientApi;
import forestry.api.client.lepidopterology.IButterflyClientManager;
import forestry.api.lepidopterology.genetics.IButterflySpecies;
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

public class ModelButterfly implements UnbakedGeometry {
	@Override
	public QuadCollection bake(TextureSlots textureSlots, ModelBaker baker, net.minecraft.client.renderer.block.dispatch.ModelState modelState, ModelDebugName debugName) {
		return bake(textureSlots, baker, modelState, debugName, StandaloneGeometryBakingContext.INSTANCE);
	}

	@Override
	public QuadCollection bake(TextureSlots textureSlots, ModelBaker baker, net.minecraft.client.renderer.block.dispatch.ModelState modelState, ModelDebugName debugName, IGeometryBakingContext context) {
		try {
			IButterflyClientManager manager = IForestryClientApi.INSTANCE.getButterflyManager();
			IdentityHashMap<IButterflySpecies, GeneticsRenderData> speciesData = new IdentityHashMap<>();
			QuadCollection fallback = QuadCollection.EMPTY;

			for (IButterflySpecies species : SpeciesUtil.getAllButterflySpecies()) {
				Identifier texture = manager.getTextures(species).getFirst();
				GeneticsRenderData data = bakeTexturedButterfly(baker, texture);
				speciesData.put(species, data);
				if (fallback == QuadCollection.EMPTY) {
					fallback = data.quads();
				}
			}

			ButterflyModelBakeCache.put(speciesData);
			return fallback;
		} catch (RuntimeException e) {
			return bakeModel(baker, ForestryConstants.forestry("item/butterfly")).quads();
		}
	}

	private static GeneticsRenderData bakeTexturedButterfly(ModelBaker baker, Identifier texture) {
		// Reuse the base butterfly shape; species texture is applied through the item atlas path.
		ResolvedModel resolved = baker.getModel(ForestryConstants.forestry("item/butterfly"));
		TextureSlots slots = resolved.getTopTextureSlots();
		QuadCollection quads = resolved.bakeTopGeometry(slots, baker, BlockModelRotation.IDENTITY);
		ModelRenderProperties properties = ModelRenderProperties.fromResolvedModel(baker, resolved, slots);
		return new GeneticsRenderData(quads, properties);
	}

	private static GeneticsRenderData bakeModel(ModelBaker baker, Identifier location) {
		ResolvedModel resolved = baker.getModel(location);
		TextureSlots slots = resolved.getTopTextureSlots();
		QuadCollection quads = resolved.bakeTopGeometry(slots, baker, BlockModelRotation.IDENTITY);
		ModelRenderProperties properties = ModelRenderProperties.fromResolvedModel(baker, resolved, slots);
		return new GeneticsRenderData(quads, properties);
	}

	public static class Loader implements IGeometryLoader {
		private static final ModelButterfly MODEL = new ModelButterfly();

		@Override
		public UnbakedGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
			return MODEL;
		}
	}
}
