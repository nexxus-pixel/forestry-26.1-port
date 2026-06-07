package forestry.farming.items;

import forestry.core.TranslationKeys;
import forestry.core.items.ItemBlockForestry;
import forestry.core.utils.TooltipUtil;
import forestry.farming.blocks.FarmBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.List;
import java.util.function.Consumer;

public class ItemBlockFarm extends ItemBlockForestry<FarmBlock> {
	public ItemBlockFarm(FarmBlock block) {
		super(block, new Item.Properties().overrideDescription("block.forestry.farm_" + block.getType().getSerializedName()));
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
		TooltipUtil.append(stack, context, flag, (s, world, tooltip, f) -> {
			if (f.isAdvanced()) {
				tooltip.add(Component.translatable("block.forestry.farm.tooltip").withStyle(ChatFormatting.GRAY));
			} else {
				tooltip.add(Component.translatable(TranslationKeys.HOLD_SHIFT_FOR_DETAILS).withStyle(ChatFormatting.GRAY));
			}
		}, tooltipAdder);
	}

}
