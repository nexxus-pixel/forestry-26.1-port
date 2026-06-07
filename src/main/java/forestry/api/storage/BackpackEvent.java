package forestry.api.storage;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.event.MutableEvent;

public abstract class BackpackEvent extends MutableEvent {

	public final Player player;
	public final IBackpackDefinition backpackDefinition;
	public final Container backpackInventory;

	public BackpackEvent(Player player, IBackpackDefinition backpackDefinition, Container backpackInventory) {
		this.player = player;
		this.backpackDefinition = backpackDefinition;
		this.backpackInventory = backpackInventory;
	}
}
