package forestry.factory.tiles;

import forestry.core.utils.CompoundTagUtil;

import forestry.api.fuels.FuelManager;
import forestry.api.fuels.RainSubstrate;
import forestry.core.render.ParticleRender;
import forestry.core.tiles.TileMill;
import forestry.factory.features.FactoryTiles;
import forestry.factory.inventory.InventoryRainmaker;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ServerLevelData;

import javax.annotation.Nullable;

public class TileMillRainmaker extends TileMill {
	private int duration;
	private boolean reverse;

	public TileMillRainmaker(BlockPos pos, BlockState state) {
		super(FactoryTiles.RAINMAKER.tileType(), pos, state);
        this.speed = 0.01f;
		setInternalInventory(new InventoryRainmaker(this));
	}

	@Override
	public void openGui(ServerPlayer player, InteractionHand hand, BlockPos pos) {
		if (!player.level().isClientSide()) {
			ItemStack heldItem = player.getItemInHand(hand);

			// We don't have a gui, but we can be activated
			if (FuelManager.rainSubstrate.containsKey(heldItem) && this.charge == 0) {
				RainSubstrate substrate = FuelManager.rainSubstrate.get(heldItem);
				if (heldItem.is(substrate.item())) {
					addCharge(substrate);
					if (!player.isCreative()) {
						heldItem.shrink(1);
					}
				}
			}
			sendNetworkUpdate();
		}
	}

	@Override
	protected void readLegacyData(CompoundTag compoundNBT) {
		super.readLegacyData(compoundNBT);

        this.charge = CompoundTagUtil.getInt(compoundNBT, "Charge");
        this.progress = CompoundTagUtil.getFloat(compoundNBT, "Progress");
        this.stage = CompoundTagUtil.getInt(compoundNBT, "Stage");
        this.duration = CompoundTagUtil.getInt(compoundNBT, "Duration");
        this.reverse = CompoundTagUtil.getBoolean(compoundNBT, "Reverse");
	}


	@Override
	protected void writeLegacyData(CompoundTag compoundNBT) {
		super.writeLegacyData(compoundNBT);

		compoundNBT.putInt("Charge", this.charge);
		compoundNBT.putFloat("Progress", this.progress);
		compoundNBT.putInt("Stage", this.stage);
		compoundNBT.putInt("Duration", this.duration);
		compoundNBT.putBoolean("Reverse", this.reverse);
	}

	public void addCharge(RainSubstrate substrate) {
        this.charge = 1;
        this.speed = substrate.speed();
        this.duration = substrate.duration();
        this.reverse = substrate.reverse();
		sendNetworkUpdate();
	}

	@Override
	public void activate(Level level, BlockPos pos) {
		if (level.isClientSide()) {
			level.playSound(null, pos, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 10000.0F, 0.8F + level.getRandom().nextFloat() * 0.2F);

			float f = pos.getX() + 0.5F;
			float f1 = pos.getY() + level.getRandom().nextFloat() * 6F / 16F;
			float f2 = pos.getZ() + 0.5F;
			float f3 = 0.52F;
			float f4 = level.getRandom().nextFloat() * 0.6F - 0.3F;

			ParticleRender.addEntityExplodeFX(level, f - f3, f1, f2 + f4);
			ParticleRender.addEntityExplodeFX(level, f + f3, f1, f2 + f4);
			ParticleRender.addEntityExplodeFX(level, f + f4, f1, f2 - f3);
			ParticleRender.addEntityExplodeFX(level, f + f4, f1, f2 + f3);
		} else {
			if (level instanceof ServerLevel serverLevel) {
				var weather = serverLevel.getWeatherData();
				if (this.reverse) {
					weather.setRaining(false);
				} else {
					weather.setRaining(true);
					weather.setRainTime(this.duration);
				}
			}
            this.charge = 0;
            this.duration = 0;
            this.reverse = false;
			sendNetworkUpdate();
		}
	}

	@Override
	@Nullable
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return null;
	}

	@Override
	protected boolean hasGui() {
		return false;
	}
}
