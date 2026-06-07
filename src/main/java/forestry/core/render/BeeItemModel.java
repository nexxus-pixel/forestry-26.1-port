package forestry.core.render;

import forestry.api.apiculture.genetics.IBeeSpecies;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ILifeStage;
import forestry.api.genetics.capability.IIndividualHandlerItem;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class BeeItemModel implements ItemModel {
	private final Map<IBeeSpecies, BeeModelBakeCache.SpeciesRenderData> speciesModels;
	private final BeeModelBakeCache.SpeciesRenderData fallback;
	private final Matrix4f transformation = new Matrix4f();

	public BeeItemModel(ILifeStage stage) {
		this.speciesModels = BeeModelBakeCache.get(stage);
		this.fallback = this.speciesModels.isEmpty() ? null : this.speciesModels.values().iterator().next();
	}

	@Override
	public void update(ItemStackRenderState renderState, ItemStack stack, ItemModelResolver resolver, ItemDisplayContext displayContext, ClientLevel level, ItemOwner owner, int seed) {
		BeeModelBakeCache.SpeciesRenderData data = resolve(stack);
		if (data == null) {
			return;
		}

		applyLayer(renderState, stack, displayContext, level, owner, data, List.of());
	}

	private BeeModelBakeCache.SpeciesRenderData resolve(ItemStack stack) {
		IIndividual individual = IIndividualHandlerItem.getIndividual(stack);
		if (individual != null) {
			BeeModelBakeCache.SpeciesRenderData data = this.speciesModels.get(individual.getSpecies());
			if (data != null) {
				return data;
			}
		}
		return this.fallback;
	}

	private static void applyLayer(ItemStackRenderState renderState, ItemStack stack, ItemDisplayContext displayContext, ClientLevel level, ItemOwner owner, BeeModelBakeCache.SpeciesRenderData data, List<ItemTintSource> tints) {
		ItemStackRenderState.LayerRenderState layer = renderState.newLayer();
		layer.setExtents(() -> ForestryCuboidItemModel.computeExtents(data.quads().getAll()));

		for (ItemTintSource tint : tints) {
			LivingEntity entity = owner != null ? owner.asLivingEntity() : null;
			int color = tint.calculate(stack, level, entity);
			layer.tintLayers().add(color);
			renderState.appendModelIdentityElement(color);
		}

		layer.setLocalTransform(new Matrix4f());
		data.properties().applyToLayer(layer, displayContext);
		layer.prepareQuadList().addAll(data.quads().getAll());
		renderState.appendModelIdentityElement(data);
	}
}
