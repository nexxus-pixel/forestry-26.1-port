package forestry.core.render;

import forestry.core.fluids.ForestryFluids;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;

public final class FluidRenderHelper {
	private FluidRenderHelper() {
	}

	public static int getFluidColor(Fluid fluid) {
		FluidType attributes = fluid.getFluidType();
		int color = IClientFluidTypeExtensions.of(attributes).getTintColor();
		ForestryFluids definition = ForestryFluids.getFluidDefinition(fluid);
		if (color < 0) {
			color = 0x0000ff;
			if (definition != null) {
				color = definition.getParticleColor();
			}
		}
		return color;
	}
}
