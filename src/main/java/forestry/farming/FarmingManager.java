package forestry.farming;

import forestry.core.utils.CompoundTagUtil;

import com.google.common.collect.ImmutableMap;
import forestry.api.farming.IFarmType;
import forestry.api.farming.IFarmingManager;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class FarmingManager implements IFarmingManager {
	private final Object2IntOpenHashMap<Item> fertilizers;
	private final ImmutableMap<Identifier, IFarmType> farmTypes;

	public FarmingManager(Object2IntOpenHashMap<Item> fertilizers, ImmutableMap<Identifier, IFarmType> farmTypes) {
		this.farmTypes = farmTypes;
		this.fertilizers = fertilizers;
	}

	@Override
	public int getFertilizeValue(ItemStack stack) {
		return this.fertilizers.getInt(stack.getItem());
	}

	@Nullable
	@Override
	public IFarmType getFarmType(Identifier id) {
		return this.farmTypes.get(id);
	}
}
