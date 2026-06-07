package forestry.lepidopterology.render;

import com.mojang.blaze3d.vertex.PoseStack;
import forestry.core.render.ForestryModelLayers;
import forestry.lepidopterology.entities.EntityButterfly;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class ButterflyEntityRenderer extends MobRenderer<EntityButterfly, ButterflyRenderState, ButterflyModel> {
	public ButterflyEntityRenderer(EntityRendererProvider.Context context) {
		super(context, new ButterflyModel(context.bakeLayer(ForestryModelLayers.BUTTERFLY_LAYER)), 0.25f);
	}

	@Override
	public ButterflyRenderState createRenderState() {
		return new ButterflyRenderState();
	}

	@Override
	public void extractRenderState(EntityButterfly entity, ButterflyRenderState state, float partialTick) {
		super.extractRenderState(entity, state, partialTick);
		state.butterflySize = entity.getSize();
		state.texture = entity.getTexture();
		state.renderable = entity.isRenderable();
		if (!state.renderable) {
			state.isInvisible = true;
		}
	}

	@Override
	public Identifier getTextureLocation(ButterflyRenderState state) {
		return state.texture;
	}

	@Override
	protected void scale(ButterflyRenderState state, PoseStack stack) {
		stack.translate(0, 0.2, 0);
		float size = state.butterflySize;
		stack.scale(size, size, size);
		stack.translate(0, 1.45f / size, 0);
	}
}
