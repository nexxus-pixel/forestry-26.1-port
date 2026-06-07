package forestry.arboriculture.items;

import forestry.api.ForestryTags;
import forestry.api.arboriculture.IToolGrafter;
import forestry.core.items.ItemForestry;
import forestry.core.utils.TooltipUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Consumer;

public class ItemGrafter extends ItemForestry implements IToolGrafter {
	public ItemGrafter(int maxDamage) {
		super(new Item.Properties().durability(maxDamage));
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag advanced) {
		super.appendHoverText(stack, context, display, tooltipAdder, advanced);
		TooltipUtil.append(stack, context, advanced, (s, world, tooltip, flag) -> {
			if (!s.isDamaged()) {
				tooltip.add(Component.translatable("item.forestry.uses", s.getMaxDamage() + 1).withStyle(ChatFormatting.GRAY));
			}
		}, tooltipAdder);
	}

	@Override
	public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
		return state.getBlock() instanceof LeavesBlock || state.is(BlockTags.LEAVES) || super.isCorrectToolForDrops(stack, state);
	}

	@Override
	public float getDestroySpeed(ItemStack itemstack, BlockState state) {
		if (state.is(ForestryTags.Blocks.MINEABLE_GRAFTER)) {
			return 4.0f;
		} else {
			return 1.0f;
		}
	}

	@Override
	public boolean mineBlock(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity entity) {
		if (!world.isClientSide() && !state.is(BlockTags.FIRE)) {
			stack.hurtAndBreak(1, entity, InteractionHand.MAIN_HAND);
		}
		return state.is(BlockTags.LEAVES);
	}

	@Override
	public float getSaplingModifier(ItemStack stack, Level world, Player player, BlockPos pos) {
		return 100f;
	}
}
