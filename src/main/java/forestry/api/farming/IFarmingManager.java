package forestry.api.farming;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

public interface IFarmingManager {
	default List<IFarmable> getFarmables(Identifier farmTypeId) {
		IFarmType farmType = getFarmType(farmTypeId);
		return farmType == null ? List.of() : farmType.getFarmables();
	}

	/**
	 * @return The value of the fertilizer when used in a farm.
	 */
	int getFertilizeValue(ItemStack stack);

	@Nullable
	IFarmType getFarmType(Identifier id);
}
