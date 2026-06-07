package forestry.arboriculture.blocks;

import com.mojang.serialization.MapCodec;
import forestry.arboriculture.worldgen.FeatureArboriculture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public abstract class BlockExtendedLeaves extends LeavesBlock {
	private static final ThreadLocal<byte[]> SURROUNDINGS = ThreadLocal.withInitial(() -> new byte[32768]);
	private static final byte SUPPORTED_LEAVES = 0;
	private static final byte NO_LEAVES = -1;
	private static final byte UNSUPPORTED_LEAVES = -2;

	// true when a leaves block with distance 6 or below is nearby (extends range to ~10)
	public static final BooleanProperty SUPPORTED = BooleanProperty.create("supported");

	protected BlockExtendedLeaves(Properties properties) {
		super(0.2f, properties);
		registerDefaultState(defaultBlockState().setValue(SUPPORTED, false));
	}

	public abstract MapCodec<? extends LeavesBlock> codec();

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(SUPPORTED);
	}

	@Override
	protected void spawnFallingLeavesParticle(Level level, BlockPos pos, RandomSource random) {
	}

	@Override
	protected boolean isRandomlyTicking(BlockState state) {
		return super.isRandomlyTicking(state) && !state.getValue(SUPPORTED);
	}

	@Override
	protected boolean decaying(BlockState state) {
		return super.decaying(state) && !state.getValue(SUPPORTED);
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos currentPos, Direction facing, BlockPos facingPos, BlockState facingState, RandomSource random) {
		BlockState newState = super.updateShape(state, level, tickAccess, currentPos, facing, facingPos, facingState, random);

		boolean oldSupported = newState.getValue(SUPPORTED);
		boolean newSupported = hasSupport(newState, level, currentPos);

		if (oldSupported != newSupported) {
			newState = newState.setValue(SUPPORTED, newSupported);
			tickAccess.scheduleTick(currentPos, this, 1);
		}

		return newState;
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		super.tick(state, level, pos, random);

		BlockState newState = level.getBlockState(pos);
		boolean shouldBeSupported = hasSupport(newState, level, pos);
		newState = newState.setValue(SUPPORTED, shouldBeSupported);

		if (newState != state) {
			level.setBlock(pos, newState, 3);

			if (shouldBeSupported != state.getValue(SUPPORTED)) {
				for (Direction direction : Direction.values()) {
					BlockPos neighborPos = pos.relative(direction);
					BlockState neighborState = level.getBlockState(neighborPos);

					if (neighborState.getBlock() instanceof BlockExtendedLeaves) {
						level.scheduleTick(neighborPos, neighborState.getBlock(), 1);
					}
				}
			}
		}
	}

	private static boolean hasSupport(BlockState state, LevelReader level, BlockPos pos) {
		if (state.getValue(DISTANCE) <= 6) {
			return true;
		}

		return FeatureArboriculture.SKIP_EXTENDED_CHECKS.get() || hasExtendedSupport(level, pos);
	}

	private static boolean hasExtendedSupport(LevelReader level, BlockPos pos) {
		byte[] surroundings = BlockExtendedLeaves.SURROUNDINGS.get();

		BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		for (int xOffset = -4; xOffset <= 4; xOffset++) {
			cursor.setX(x + xOffset);
			for (int yOffset = -4; yOffset <= 4; yOffset++) {
				cursor.setY(y + yOffset);
				for (int zOffset = -4; zOffset <= 4; zOffset++) {
					cursor.setZ(z + zOffset);

					BlockState state = level.getBlockState(cursor);
					int index = ((xOffset + 16) << 10) + ((yOffset + 16) << 5) + (zOffset + 16);

					if (state.hasProperty(DISTANCE)) {
						if (state.getValue(DISTANCE) <= 6) {
							surroundings[index] = SUPPORTED_LEAVES;
						} else {
							surroundings[index] = UNSUPPORTED_LEAVES;
						}
					} else {
						surroundings[index] = NO_LEAVES;
					}
				}
			}
		}

		for (byte sustainedValue = 1; sustainedValue <= 4; ++sustainedValue) {
			for (int xOffset = -4; xOffset <= 4; ++xOffset) {
				for (int yOffset = -4; yOffset <= 4; ++yOffset) {
					for (int zOffset = -4; zOffset <= 4; ++zOffset) {
						int index = ((xOffset + 16) << 10) + ((yOffset + 16) << 5) + (zOffset + 16);

						if (surroundings[index] == sustainedValue - 1) {
							checkAndSetSupport(surroundings, xOffset - 1, yOffset, zOffset, sustainedValue);
							checkAndSetSupport(surroundings, xOffset + 1, yOffset, zOffset, sustainedValue);
							checkAndSetSupport(surroundings, xOffset, yOffset - 1, zOffset, sustainedValue);
							checkAndSetSupport(surroundings, xOffset, yOffset + 1, zOffset, sustainedValue);
							checkAndSetSupport(surroundings, xOffset, yOffset, zOffset - 1, sustainedValue);
							checkAndSetSupport(surroundings, xOffset, yOffset, zOffset + 1, sustainedValue);
						}
					}
				}
			}
		}

		return surroundings[16912] >= 0;
	}

	private static void checkAndSetSupport(byte[] array, int x, int y, int z, byte supportValue) {
		if (x < -16 || x > 16 || y < -16 || y > 16 || z < -16 || z > 16) {
			return;
		}

		int arrayIndex = ((x + 16) << 10) + ((y + 16) << 5) + (z + 16);

		if (array[arrayIndex] == UNSUPPORTED_LEAVES) {
			array[arrayIndex] = supportValue;
		}
	}
}
