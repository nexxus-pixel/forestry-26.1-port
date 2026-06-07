package forestry.core.utils.datastructures;


import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.Serial;

public class FluidMap<T> extends StackMap<Fluid, T> {
	@Serial
	private static final long serialVersionUID = 15891293315299994L;

	@Override
	protected boolean areEqual(Fluid a, Fluid b) {
		return a.equals(b);
	}

	@Override
	protected boolean isValidKey(Object key) {
		return key instanceof FluidStack || key instanceof Fluid || key instanceof String || key instanceof Identifier;
	}

	@Override
	protected Fluid getStack(Object key) {
		if (key instanceof FluidStack) {
			return ((FluidStack) key).getFluid();
		}
		if (key instanceof Fluid) {
			return (Fluid) key;
		}
		if (key instanceof String) {
			return ForgeRegistries.FLUIDS.getValue(Identifier.parse((String) key));
		}
		if (key instanceof Identifier) {
			return ForgeRegistries.FLUIDS.getValue((Identifier) key);
		}
		return null;
	}
}
