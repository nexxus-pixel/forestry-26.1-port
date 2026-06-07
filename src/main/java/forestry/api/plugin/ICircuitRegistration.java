package forestry.api.plugin;

import forestry.api.circuits.ICircuit;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Handles registration of all circuits-related data in Forestry.
 * Circuit socket types are not required to be registered, but should be kept as constants in a
 * separate class like in {@link forestry.api.circuits.ForestryCircuitSocketTypes}.
 */
public interface ICircuitRegistration {
	/**
	 * Registers the circuit for this item when in the given circuit layout.
	 *
	 * @param layoutId The unique ID of the circuit layout.
	 * @param stack    The item that should have this circuit (in base Forestry, always an electron tube).
	 * @param circuit  The circuit this item has in the layout.
	 */
	void registerCircuit(String layoutId, Item item, int count, ICircuit circuit);

	default void registerCircuit(String layoutId, Item item, ICircuit circuit) {
		registerCircuit(layoutId, item, 1, circuit);
	}

	default void registerCircuit(String layoutId, ItemStack stack, ICircuit circuit) {
		registerCircuit(layoutId, stack.getItem(), stack.getCount(), circuit);
	}

	/**
	 * Registers a new circuit layout.
	 *
	 * @param layoutId   The ID of the circuit layout.
	 * @param socketType The socket type of the layout, which determines which machines will accept this layout.
	 * @see forestry.api.circuits.ForestryCircuitSocketTypes
	 * @see forestry.api.circuits.ForestryCircuitLayouts
	 */
	void registerLayout(String layoutId, Identifier socketType);
}
