package forestry.apiculture.items;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.apiculture.hives.IHiveFrame;
import forestry.api.genetics.IGenome;
import forestry.core.items.ItemForestry;
import forestry.core.utils.TooltipUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.List;
import java.util.function.Consumer;

public class ItemHiveFrame extends ItemForestry implements IHiveFrame {
	private final HiveFrameBeeModifier beeModifier;

	public ItemHiveFrame(int maxDamage, float geneticDecay) {
		super(new Item.Properties().durability(maxDamage));

		this.beeModifier = new HiveFrameBeeModifier(geneticDecay);
	}

	public int getMaxStackSize(ItemStack stack) {
		return 64;
	}

	@Override
	public ItemStack frameUsed(IBeeHousing housing, ItemStack frame, IBee queen, int wear) {
		frame.hurtWithoutBreaking(wear, null);
		if (frame.isEmpty()) {
			return ItemStack.EMPTY;
		}
		return frame;
	}

	@Override
	public IBeeModifier getBeeModifier(ItemStack frame) {
		return this.beeModifier;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag advanced) {
		super.appendHoverText(stack, context, display, tooltipAdder, advanced);
		TooltipUtil.append(stack, context, advanced, (s, world, tooltip, flag) -> {
			this.beeModifier.addInformation(tooltip);
			if (!s.isDamaged()) {
				tooltip.add(Component.translatable("item.forestry.durability", s.getMaxDamage()));
			}
		}, tooltipAdder);
	}

	private static class HiveFrameBeeModifier implements IBeeModifier {
		private static final float production = 2f;
		private final float geneticDecay;

		public HiveFrameBeeModifier(float geneticDecay) {
			this.geneticDecay = geneticDecay;
		}

		@Override
		public float modifyProductionSpeed(IGenome genome, float currentSpeed) {
			return currentSpeed < 10f ? currentSpeed * production : 1f;
		}

		@Override
		public float modifyGeneticDecay(IGenome genome, float currentDecay) {
			return this.geneticDecay;
		}

		public void addInformation(List<Component> tooltip) {
			tooltip.add(Component.translatable("item.forestry.bee.modifier.production", production));
			tooltip.add(Component.translatable("item.forestry.bee.modifier.genetic.decay", this.geneticDecay));
		}
	}
}
