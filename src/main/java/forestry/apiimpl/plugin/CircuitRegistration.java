package forestry.apiimpl.plugin;

import forestry.api.circuits.CircuitHolder;
import forestry.api.circuits.ICircuit;
import forestry.api.plugin.ICircuitRegistration;
import forestry.core.circuits.CircuitLayout;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

// should circuit board items be registered too? not sure if there's a use for any besides the four Forestry ones
public class CircuitRegistration implements ICircuitRegistration {
	private final ArrayList<CircuitLayout> layouts = new ArrayList<>();
	private final ArrayList<CircuitHolder> circuits = new ArrayList<>();

	@Override
	public void registerCircuit(String layoutId, Item item, int count, ICircuit circuit) {
		this.circuits.add(new CircuitHolder(layoutId, item, count, circuit));
	}

	@Override
	public void registerLayout(String layoutId, Identifier socketType) {
		this.layouts.add(new CircuitLayout(layoutId, socketType));
	}

	public ArrayList<CircuitHolder> getCircuits() {
		return this.circuits;
	}

	public ArrayList<CircuitLayout> getLayouts() {
		return this.layouts;
	}
}
