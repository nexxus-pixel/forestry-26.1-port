package forestry.core.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class TooltipUtil {
	@FunctionalInterface
	public interface LegacyAppender {
		void append(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag);
	}

	private TooltipUtil() {
	}

	public static void append(ItemStack stack, Item.TooltipContext context, TooltipFlag flag, LegacyAppender appender, Consumer<Component> tooltipAdder) {
		List<Component> tooltip = new ArrayList<>();
		appender.append(stack, context.level(), tooltip, flag);
		tooltip.forEach(tooltipAdder);
	}
}
