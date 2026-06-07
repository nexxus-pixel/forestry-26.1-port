package forestry.core.render;

import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.geometry.QuadCollection;

public record GeneticsRenderData(QuadCollection quads, ModelRenderProperties properties) {
}
