package forestry.core.render;

import net.minecraft.client.resources.model.geometry.BakedQuad;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.List;

public final class ForestryCuboidItemModel {
	private ForestryCuboidItemModel() {
	}

	public static Vector3fc[] computeExtents(List<BakedQuad> quads) {
		if (quads.isEmpty()) {
			return new Vector3fc[0];
		}

		float minX = Float.POSITIVE_INFINITY;
		float minY = Float.POSITIVE_INFINITY;
		float minZ = Float.POSITIVE_INFINITY;
		float maxX = Float.NEGATIVE_INFINITY;
		float maxY = Float.NEGATIVE_INFINITY;
		float maxZ = Float.NEGATIVE_INFINITY;

		for (BakedQuad quad : quads) {
			for (int i = 0; i < 4; i++) {
				var pos = quad.position(i);
				minX = Math.min(minX, pos.x());
				minY = Math.min(minY, pos.y());
				minZ = Math.min(minZ, pos.z());
				maxX = Math.max(maxX, pos.x());
				maxY = Math.max(maxY, pos.y());
				maxZ = Math.max(maxZ, pos.z());
			}
		}

		return new Vector3fc[]{
			new Vector3f(minX, minY, minZ),
			new Vector3f(maxX, maxY, maxZ)
		};
	}
}
