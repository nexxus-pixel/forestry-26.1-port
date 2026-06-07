package forestry.core.render;

import forestry.api.arboriculture.ITreeSpecies;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.capability.IIndividualHandlerItem;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

import java.util.Map;

public class SaplingItemModel implements ItemModel {
	private final Map<ITreeSpecies, GeneticsRenderData> speciesModels;
	private final GeneticsRenderData fallback;

	public SaplingItemModel() {
		this.speciesModels = SaplingModelBakeCache.getItems();
		this.fallback = this.speciesModels.isEmpty() ? null : this.speciesModels.values().iterator().next();
	}

	@Override
	public void update(ItemStackRenderState renderState, ItemStack stack, ItemModelResolver resolver, ItemDisplayContext displayContext, ClientLevel level, ItemOwner owner, int seed) {
		GeneticsRenderData data = resolve(stack);
		if (data == null) {
			return;
		}

		ItemStackRenderState.LayerRenderState layer = renderState.newLayer();
		layer.setExtents(() -> ForestryCuboidItemModel.computeExtents(data.quads().getAll()));
		layer.setLocalTransform(new Matrix4f());
		data.properties().applyToLayer(layer, displayContext);
		layer.prepareQuadList().addAll(data.quads().getAll());
		renderState.appendModelIdentityElement(data);
	}

	private GeneticsRenderData resolve(ItemStack stack) {
		IIndividual individual = IIndividualHandlerItem.getIndividual(stack);
		if (individual != null) {
			GeneticsRenderData data = this.speciesModels.get(individual.getSpecies());
			if (data != null) {
				return data;
			}
		}
		return this.fallback;
	}
}
