package forestry.apiculture.items;

import forestry.core.utils.ItemStackUtil;

import forestry.api.apiculture.genetics.BeeLifeStage;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.apiculture.genetics.IBeeSpecies;
import forestry.api.genetics.ISpeciesType;
import forestry.api.genetics.capability.IIndividualHandlerItem;
import forestry.core.genetics.ItemGE;
import forestry.core.items.definitions.IColoredItem;
import forestry.core.utils.SpeciesUtil;
import forestry.core.utils.TooltipUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.List;
import java.util.function.Consumer;

public class ItemBeeGE extends ItemGE implements IColoredItem {
	public ItemBeeGE(BeeLifeStage type) {
		super(type != BeeLifeStage.DRONE ? new Item.Properties().stacksTo(1) : new Item.Properties(), type);
	}

	@Override
	protected ISpeciesType<?, ?> getType() {
		return SpeciesUtil.BEE_TYPE.get();
	}

	@Override
	protected IBeeSpecies getSpecies(ItemStack stack) {
		return IIndividualHandlerItem.getSpecies(stack, SpeciesUtil.BEE_TYPE.get());
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
		TooltipUtil.append(stack, context, flag, (s, level, list, f) -> {
			if (!ItemStackUtil.hasTag(s)) {
				return;
			}
			if (this.stage != BeeLifeStage.DRONE) {
				IIndividualHandlerItem.ifPresent(s, individual -> {
					if (((IBee) individual).isPristine()) {
						list.add(Component.translatable("for.bees.stock.pristine").withStyle(ChatFormatting.YELLOW, ChatFormatting.ITALIC));
					} else {
						list.add(Component.translatable("for.bees.stock.ignoble").withStyle(ChatFormatting.YELLOW));
					}
				});
			}
			Player player = net.minecraft.client.Minecraft.getInstance().player;
			appendGeneticsTooltip(s, list, player);
		}, tooltipAdder);
	}

	@Override
	public int getColorFromItemStack(ItemStack stack, int tintIndex) {
		if (!ItemStackUtil.hasTag(stack)) {
			if (tintIndex == 1) {
				// 1 = body
				return 0xffdc16;
			} else if (tintIndex == 2) {
				// 2 = stripes
				return 0;
			} else {
				// 0 = outline
				return 0xffffff;
			}
		} else {
			IBeeSpecies species = getSpecies(stack);

			return switch (tintIndex) {
				case 2 -> species.getStripes();
				case 1 -> species.getBody();
				case 0 -> species.getOutline();
				default -> 0xffffff;
			};
		}
	}

	public final BeeLifeStage getStage() {
		return (BeeLifeStage) this.stage;
	}
}
