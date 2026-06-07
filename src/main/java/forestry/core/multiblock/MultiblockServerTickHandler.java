package forestry.core.multiblock;

import net.minecraftforge.event.TickEvent;

/**
 * This is a generic multiblock tick handler. If you are using this code on your own,
 * you will need to register this with the Forge TickRegistry on both the
 * client AND server sides.
 * Note that different types of ticks run on different parts of the system.
 * CLIENT ticks only run on the client, at the start/end of each game loop.
 * SERVER and WORLD ticks only run on the server.
 * WORLDLOAD ticks run only on the server, and only when worlds are loaded.
 */
public final class MultiblockServerTickHandler {
	private MultiblockServerTickHandler() {
	}

	public static void register() {
		TickEvent.LevelTickEvent.Pre.BUS.addListener(MultiblockServerTickHandler::onWorldTick);
	}

	private static void onWorldTick(TickEvent.LevelTickEvent.Pre event) {
		MultiblockRegistry.tickStart(event.level());
	}
}
