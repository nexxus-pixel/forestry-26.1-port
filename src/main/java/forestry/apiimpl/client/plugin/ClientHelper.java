package forestry.apiimpl.client.plugin;

import forestry.api.client.arboriculture.ILeafSprite;
import forestry.api.client.arboriculture.ILeafTint;
import forestry.api.client.plugin.IClientHelper;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * Stub until arboriculture client tint/sprite code is re-enabled.
 */
public class ClientHelper implements IClientHelper {
	@Override
	public ILeafTint createNoneTint() {
		return (level, pos) -> -1;
	}

	@Override
	public ILeafTint createFixedTint(Color color) {
		return (level, pos) -> color.getRGB();
	}

	@Override
	public ILeafTint createBiomeTint() {
		return (level, pos) -> -1;
	}

	@Override
	public ILeafTint createBiomeTint(Int2IntFunction mapper) {
		return (level, pos) -> mapper.applyAsInt(-1);
	}

	@Override
	public ILeafSprite createLeafSprite(Identifier id) {
		return new ILeafSprite() {
			@Override
			public Identifier get(boolean pollinated, boolean fancy) {
				return id;
			}

			@Override
			public Identifier getParticle() {
				return id;
			}
		};
	}
}
