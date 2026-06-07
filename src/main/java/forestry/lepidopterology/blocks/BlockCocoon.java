package forestry.lepidopterology.blocks;

import forestry.modules.features.RegistrationContext;

import forestry.core.utils.ItemStackUtil;

import forestry.api.lepidopterology.genetics.ButterflyLifeStage;
import forestry.api.lepidopterology.genetics.IButterfly;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.SpeciesUtil;
import forestry.lepidopterology.items.ItemButterflyGE;
import forestry.lepidopterology.tiles.TileCocoon;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockCocoon extends Block implements EntityBlock {
	public static final VoxelShape BOUNDING_BOX = Block.box(5f, 5f, 5f, 11f, 16F, 11f);
	public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 2);

	public BlockCocoon() {
		super(RegistrationContext.of(p -> p.randomTicks().sound(SoundType.GRAVEL)));
		registerDefaultState(getStateDefinition().any().setValue(AGE, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}

	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
		TileCocoon tileCocoon = TileUtil.getTile(world, pos, TileCocoon.class);
		if (tileCocoon == null || tileCocoon.isRemoved()) {
			return;
		}

		tileCocoon.onBlockTick();
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileCocoon(pos, state, false);
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos currentPos, Direction facing, BlockPos facingPos, BlockState facingState, RandomSource random) {
		if (facing != Direction.UP || !facingState.isAir()) {
			return state;
		}
		return Blocks.AIR.defaultBlockState();
	}

	@Override
	protected ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean includeData) {
		TileCocoon tile = TileUtil.getTile(world, pos, TileCocoon.class);
		if (tile == null) {
			return ItemStack.EMPTY;
		}

		IButterfly caterpillar = tile.getCaterpillar();
		int age = state.getValue(AGE);

		ItemStack stack = SpeciesUtil.BUTTERFLY_TYPE.get().createStack(caterpillar, ButterflyLifeStage.COCOON);
		if (!stack.isEmpty() && ItemStackUtil.getTag(stack) != null) {
			ItemStackUtil.getTag(stack).putInt(ItemButterflyGE.NBT_AGE, age);
		}
		return stack;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return BOUNDING_BOX;
	}
}
