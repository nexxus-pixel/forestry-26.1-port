package forestry.core.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;

import java.util.function.Function;

public final class BerRenderHelper {
	private static final Function<Identifier, RenderType> BLOCK_CUTOUT = RenderTypes::entityCutout;
	private static final Function<Identifier, RenderType> BLOCK_TRANSLUCENT = RenderTypes::entityTranslucent;

	private BerRenderHelper() {
	}

	public static Identifier atlasTexture(Identifier texture) {
		String path = texture.getPath();
		if (path.startsWith("textures/")) {
			path = path.substring("textures/".length());
		}
		if (path.endsWith(".png")) {
			path = path.substring(0, path.length() - 4);
		}
		return Identifier.fromNamespaceAndPath(texture.getNamespace(), path);
	}

	public static SpriteId spriteId(Identifier texture) {
		return new SpriteId(TextureAtlas.LOCATION_BLOCKS, atlasTexture(texture));
	}

	public static void submitPart(SubmitNodeCollector collector, ModelPart part, PoseStack stack, SpriteGetter sprites, Identifier texture, int light, int overlay) {
		SpriteId id = spriteId(texture);
		collector.submitModelPart(part, stack, id.renderType(BLOCK_CUTOUT), light, overlay, sprites.get(id));
	}

	public static void submitTintedPart(SubmitNodeCollector collector, ModelPart part, PoseStack stack, SpriteGetter sprites, Identifier texture, int light, int overlay, int rgb) {
		SpriteId id = spriteId(texture);
		collector.submitModelPart(part, stack, id.renderType(BLOCK_TRANSLUCENT), light, overlay, sprites.get(id), false, false, rgb | 0xFF000000, null, -1);
	}

	public static void rotateByHorizontalDirection(PoseStack stack, Direction facing) {
		if (facing != Direction.SOUTH) {
			stack.translate(0.5, 0.5, 0.5);
			stack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
			stack.translate(-0.5, -0.5, -0.5);
		}
	}
}
