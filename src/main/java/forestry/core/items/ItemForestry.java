package forestry.core.items;

import forestry.core.utils.ItemTooltipUtil;
import forestry.core.utils.TooltipUtil;
import forestry.modules.features.RegistrationContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.crafting.RecipeType;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class ItemForestry extends Item {
	private final int burnTime;

	public ItemForestry() {
		this(new Properties());
	}

	public ItemForestry(Item.Properties properties) {
		super(RegistrationContext.withItemId(properties));

		if (properties instanceof ItemProperties props) {
			this.burnTime = props.burnTime;
		} else {
			this.burnTime = 0;
		}
	}

	public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
		return this.burnTime;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<net.minecraft.network.chat.Component> tooltipAdder, TooltipFlag advanced) {
		TooltipUtil.append(stack, context, advanced, ItemTooltipUtil::addInformation, tooltipAdder);
	}
}
