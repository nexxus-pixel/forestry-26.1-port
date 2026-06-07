package forestry.core.items;

import forestry.core.utils.ItemTooltipUtil;
import forestry.core.utils.TooltipUtil;
import forestry.modules.features.RegistrationContext;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class ItemBlockForestry<B extends Block> extends BlockItem {
	private final int burnTime;

	public ItemBlockForestry(B block, Item.Properties builder) {
		super(block, RegistrationContext.withItemId(builder.useBlockDescriptionPrefix()));

		if (builder instanceof ItemProperties properties) {
			this.burnTime = properties.burnTime;
		} else {
            this.burnTime = -1;
		}
	}

	public ItemBlockForestry(B block) {
		this(block, RegistrationContext.itemProperties());
	}

	@Override
	public B getBlock() {
		//noinspection unchecked
		return (B) super.getBlock();
	}

	public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
		return this.burnTime;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<net.minecraft.network.chat.Component> tooltipAdder, TooltipFlag advanced) {
		super.appendHoverText(stack, context, display, tooltipAdder, advanced);
		TooltipUtil.append(stack, context, advanced, ItemTooltipUtil::addInformation, tooltipAdder);
	}
}
