package forestry.apiculture.items;

import forestry.modules.features.RegistrationContext;
import net.minecraft.world.item.HoneycombItem;

// Reuse the behavior from HoneycombItem
public class ItemBeesWax extends HoneycombItem {
	public ItemBeesWax() {
		super(RegistrationContext.itemProperties());
	}
}
