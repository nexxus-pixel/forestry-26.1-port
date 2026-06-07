package forestry.core.items;

import forestry.api.IForestryApi;
import forestry.api.circuits.ICircuit;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.circuits.ICircuitManager;
import forestry.api.core.ItemGroups;
import forestry.core.utils.TooltipUtil;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ItemElectronTube extends ItemOverlay {
	public ItemElectronTube(ItemOverlay.IOverlayInfo type) {
		super(ItemGroups.tabForestry, type);
	}

	//TODO: Make it so total speed, efficiency, and fortune multipliers are shown?
	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, display, tooltipAdder, flag);
		TooltipUtil.append(itemstack, context, flag, (stack, world, list, f) -> {
			ArrayList<Pair<ICircuitLayout, ICircuit>> circuits = getCircuits(stack);
			if (!circuits.isEmpty()) {
				for (var entry : circuits) {
					list.add(entry.left().getUsage().withStyle(ChatFormatting.WHITE, ChatFormatting.UNDERLINE));
					entry.right().addTooltip(list);
				}
			} else {
				list.add(Component.literal("<")
					.append(Component.translatable("for.gui.noeffect")
						.append(">").withStyle(ChatFormatting.GRAY)));
			}
		}, tooltipAdder);
	}

	private static ArrayList<Pair<ICircuitLayout, ICircuit>> getCircuits(ItemStack stack) {
		ArrayList<Pair<ICircuitLayout, ICircuit>> circuits = new ArrayList<>();
		ICircuitManager manager = IForestryApi.INSTANCE.getCircuitManager();

		for (ICircuitLayout layout : manager.getLayouts()) {
			ICircuit circuit = manager.getCircuit(layout, stack);
			if (circuit != null) {
				circuits.add(Pair.of(layout, circuit));
			}
		}

		return circuits;
	}
}
