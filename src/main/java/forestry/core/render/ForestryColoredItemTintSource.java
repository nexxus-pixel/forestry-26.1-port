package forestry.core.render;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import forestry.api.ForestryConstants;
import forestry.core.items.definitions.IColoredItem;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record ForestryColoredItemTintSource(int index) implements ItemTintSource {
	public static final MapCodec<ForestryColoredItemTintSource> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.INT.fieldOf("index").forGetter(ForestryColoredItemTintSource::index)
	).apply(instance, ForestryColoredItemTintSource::new));

	public static void register() {
		net.minecraft.client.color.item.ItemTintSources.ID_MAPPER.put(
			ForestryConstants.forestry("colored_item"),
			MAP_CODEC
		);
	}

	@Override
	public int calculate(ItemStack stack, ClientLevel level, LivingEntity entity) {
		Item item = stack.getItem();
		if (item instanceof IColoredItem coloredItem) {
			return coloredItem.getColorFromItemStack(stack, this.index);
		}
		return 0xffffff;
	}

	@Override
	public MapCodec<? extends ItemTintSource> type() {
		return MAP_CODEC;
	}
}
