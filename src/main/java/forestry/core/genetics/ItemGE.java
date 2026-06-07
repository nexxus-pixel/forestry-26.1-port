package forestry.core.genetics;

import forestry.core.utils.ItemStackUtil;

import forestry.Forestry;
import forestry.api.ForestryCapabilities;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ILifeStage;
import forestry.api.genetics.ISpecies;
import forestry.api.genetics.ISpeciesType;
import forestry.api.genetics.capability.IIndividualHandlerItem;
import forestry.core.config.ForestryConfig;
import forestry.core.genetics.capability.SerializableIndividualHandlerItem;
import forestry.core.items.ItemForestry;
import forestry.core.utils.CompoundTagUtil;
import forestry.core.utils.GeneticsUtil;
import forestry.core.utils.SpeciesUtil;
import forestry.core.utils.TooltipUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.apache.commons.lang3.mutable.MutableBoolean;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public abstract class ItemGE extends ItemForestry {
	protected final ILifeStage stage;

	protected ItemGE(Item.Properties properties, ILifeStage stage) {
		super(properties);

		this.stage = stage;
	}

	protected abstract ISpecies<?> getSpecies(ItemStack stack);

	protected abstract ISpeciesType<?, ?> getType();

	public ICapabilityProvider createCapabilityProvider(ItemStack stack, @Nullable CompoundTag nbt) {
		Tag parent;

		if (nbt != null && nbt.contains("Parent")) {
			parent = nbt.get("Parent");
		} else {
			CompoundTag stackTag = ItemStackUtil.getTag(stack);
			if (stackTag != null && stackTag.contains("ForgeCaps")) {
				parent = CompoundTagUtil.getCompound(stackTag, "ForgeCaps").get("Parent");
			} else {
				parent = null;
			}
		}

		if (parent == null) {
			return new SerializableIndividualHandlerItem(getType(), stack, getType().getDefaultSpecies().createIndividual(), this.stage);
		}

		return new SerializableIndividualHandlerItem(getType(), stack, SpeciesUtil.deserializeIndividual(getType(), parent), this.stage);
	}

	@Override
	public Component getName(ItemStack stack) {
		return stack.getCapability(ForestryCapabilities.INDIVIDUAL_HANDLER_ITEM)
			.map(handler -> GeneticsUtil.getItemName(handler.getStage(), handler.getIndividual().getSpecies()))
			.orElseGet(() -> super.getName(stack));
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		if (!ItemStackUtil.hasTag(stack)) { // villager trade wildcard bees
			return false;
		}
		ISpecies<?> species = getSpecies(stack);
		return species.hasGlint() && ForestryConfig.CLIENT.enableGlints.get();
	}

	public static void appendGeneticsTooltip(ItemStack stack, List<Component> tooltip, Player player) {
		if (!ItemStackUtil.hasTag(stack)) {
			return;
		}

		MutableBoolean analyzed = new MutableBoolean();
		IIndividualHandlerItem.ifPresent(stack, individual -> {
			if (individual.isAnalyzed()) {
				if (player != null && player.isShiftKeyDown()) {
					((ISpecies<IIndividual>) individual.getSpecies()).addTooltip(individual, tooltip);
				} else {
					tooltip.add(Component.translatable("for.gui.tooltip.tmi", "< %s >").withStyle(style -> style.withColor(ChatFormatting.GRAY).withItalic(true)));
				}

				analyzed.setTrue();
			}
		});
		if (analyzed.isFalse()) {
			tooltip.add(Component.translatable("for.gui.unknown", "< %s >").withStyle(ChatFormatting.GRAY));
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
		TooltipUtil.append(stack, context, flag, (s, level, tooltip, f) -> {
			Player player = net.minecraft.client.Minecraft.getInstance().player;
			appendGeneticsTooltip(s, tooltip, player);
		}, tooltipAdder);
	}

	@Override
	public String getCreatorModId(ItemStack stack) {
		ISpecies<?> species = getSpecies(stack);
		return species.id().getNamespace();
	}

	public static <S extends ISpecies<I>, I extends IIndividual> void addCreativeItems(ILifeStage stage, List<ItemStack> subItems, boolean hideSecrets, ISpeciesType<S, I> type) {
		for (S species : type.getAllSpecies()) {
			// Don't show secrets unless ordered to.
			if (hideSecrets && species.isSecret() && !Forestry.DEBUG) {
				continue;
			}

			subItems.add(species.createStack(species.createIndividual(), stage));
		}
	}
}
