package forestry.api.storage;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;

/**
 * Use @SubscribeEvent on a method taking this event as an argument. Will fire whenever a backpack tries to resupply to a player inventory. Processing will stop
 * if the event is canceled.
 */
public class BackpackResupplyEvent extends BackpackEvent implements Cancellable {
	public static final CancellableEventBus<BackpackResupplyEvent> BUS = CancellableEventBus.create(BackpackResupplyEvent.class);

	public BackpackResupplyEvent(Player player, IBackpackDefinition backpackDefinition, Container backpackInventory) {
		super(player, backpackDefinition, backpackInventory);
	}
}
