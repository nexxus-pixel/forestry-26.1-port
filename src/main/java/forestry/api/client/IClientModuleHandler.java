package forestry.api.client;

import net.minecraftforge.eventbus.api.bus.BusGroup;

/**
 * Used to separate client-only code and events from a {@link forestry.api.modules.IForestryModule}.
 */
public interface IClientModuleHandler {
	/**
	 * Called during mod construction after {@link forestry.api.modules.IForestryModule#registerEvents(IEventBus)}.
	 *
	 * @param modBus Your mod's mod-specific event bus, for things like deferred registers and IModBus events.
	 */
	default void registerEvents(BusGroup modBusGroup) {
	}
}
