package forestry.lepidopterology.render;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class ButterflyRenderState extends LivingEntityRenderState {
	public float butterflySize = 1f;
	public Identifier texture = Identifier.withDefaultNamespace("missingno");
	public boolean renderable = true;
}
