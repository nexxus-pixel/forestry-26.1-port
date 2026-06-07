package forestry.arboriculture.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import forestry.api.arboriculture.IToolGrafter;
import forestry.api.arboriculture.genetics.IFruit;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.ITreeSpeciesType;
import forestry.api.arboriculture.genetics.TreeLifeStage;
import forestry.api.genetics.IFruitBearer;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.alleles.TreeChromosomes;
import forestry.arboriculture.blocks.BlockDefaultLeavesFruit;
import forestry.core.utils.SpeciesUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class GrafterLootModifier extends LootModifier {
	public static final MapCodec<GrafterLootModifier> CODEC = RecordCodecBuilder.mapCodec(inst -> codecStart(inst).apply(inst, GrafterLootModifier::new));

	public GrafterLootModifier(LootItemCondition[] conditionsIn) {
		super(conditionsIn);
	}

	@Override
	protected @NotNull ObjectArrayList<ItemStack> doApply(LootTable lootTable, ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
		BlockState state = context.getOptionalParameter(LootContextParams.BLOCK_STATE);
		if (state == null || !state.is(BlockTags.LEAVES)) {
			return generatedLoot;
		}
		ItemInstance harvestingTool = context.getOptionalParameter(LootContextParams.TOOL);
		if (!(harvestingTool instanceof ItemStack toolStack) || toolStack.isEmpty()) {
			return generatedLoot;
		}
		Entity entity = context.getOptionalParameter(LootContextParams.THIS_ENTITY);
		if (!(entity instanceof Player player)) {
			return generatedLoot;
		}
		if (generatedLoot.stream().noneMatch((stack) -> stack.is(ItemTags.SAPLINGS))) {
			handleLoot(generatedLoot, player, harvestingTool, state, context);
		}
		toolStack.hurtAndBreak(1, player, InteractionHand.MAIN_HAND);
		if (toolStack.isEmpty()) {
			ForgeEventFactory.onPlayerDestroyItem(player, toolStack, InteractionHand.MAIN_HAND);
		}
		return generatedLoot;
	}

	public void handleLoot(List<ItemStack> generatedLoot, Player player, ItemInstance harvestingTool, BlockState state, LootContext context) {
		Level world = player.level();
		BlockEntity tileEntity = context.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		ITree tree = getTree(state, tileEntity);
		if (tree == null) {
			return;
		}
		Vec3 origin = context.getOptionalParameter(LootContextParams.ORIGIN);
		if (origin == null) {
			return;
		}
		BlockPos pos = BlockPos.containing(origin);
		ItemStack toolStack = harvestingTool instanceof ItemStack stack ? stack : ItemStack.EMPTY;
		Item item = toolStack.getItem();
		float saplingModifier = 1.0f;
		if (item instanceof IToolGrafter) {
			saplingModifier = ((IToolGrafter) item).getSaplingModifier(toolStack, world, player, pos);
		}
		List<ITree> saplings = tree.getSaplings(world, pos, player.getGameProfile(), saplingModifier);
		for (ITree sapling : saplings) {
			if (sapling != null) {
				generatedLoot.add(sapling.createStack(TreeLifeStage.SAPLING));
			}
		}
		if (tileEntity instanceof IFruitBearer bearer) {
			generatedLoot.addAll(bearer.pickFruit(toolStack));
		}
		if (state.getBlock() instanceof BlockDefaultLeavesFruit) {
			IGenome genome = tree.getGenome();
			IFruit fruitProvider = genome.getActiveValue(TreeChromosomes.FRUIT);
			if (fruitProvider.isFruitLeaf()) {
				generatedLoot.addAll(tree.produceStacks(world, pos, Integer.MAX_VALUE));
			}
		}
	}

	@Nullable
	private ITree getTree(BlockState state, @Nullable BlockEntity entity) {
		ITreeSpeciesType type = SpeciesUtil.TREE_TYPE.get();
		ITree tree = type.getVanillaIndividual(state);
		if (tree != null || entity == null) {
			return tree;
		} else {
			return type.getTree(entity);
		}
	}

	@Override
	public MapCodec<? extends IGlobalLootModifier> codec() {
		return CODEC;
	}
}
