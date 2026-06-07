package forestry.energy;

import forestry.api.fuels.EngineBronzeFuel;
import forestry.api.fuels.EngineCopperFuel;
import forestry.api.fuels.FuelManager;
import forestry.api.modules.ForestryModule;
import forestry.api.modules.ForestryModuleIds;
import forestry.core.config.Constants;
import forestry.core.features.CoreItems;
import forestry.core.fluids.ForestryFluids;
import forestry.core.utils.datastructures.FluidMap;
import forestry.core.utils.datastructures.ItemStackMap;
import forestry.modules.BlankForestryModule;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.ForgeMod;


@ForestryModule
public class ModuleEnergy extends BlankForestryModule {
	@Override
	public Identifier getId() {
		return ForestryModuleIds.ENERGY;
	}

	@Override
	public void setupApi() {
		FuelManager.biogasEngineFuel = new FluidMap<>();
		FuelManager.peatEngineFuel = new ItemStackMap<>();

		// Biogas Engine
		Fluid biomass = ForestryFluids.BIOMASS.getFluid();
		FuelManager.biogasEngineFuel.put(biomass, new EngineBronzeFuel(biomass,
			Constants.ENGINE_FUEL_VALUE_BIOMASS, Constants.ENGINE_CYCLE_DURATION_BIOMASS, 1));

		FuelManager.biogasEngineFuel.put(Fluids.WATER, new EngineBronzeFuel(Fluids.WATER,
			Constants.ENGINE_FUEL_VALUE_WATER, Constants.ENGINE_CYCLE_DURATION_WATER, 3));

		Fluid milk = ForgeMod.MILK.get();
		FuelManager.biogasEngineFuel.put(milk, new EngineBronzeFuel(milk,
			Constants.ENGINE_FUEL_VALUE_MILK, Constants.ENGINE_CYCLE_DURATION_MILK, 3));

		Fluid seedOil = ForestryFluids.SEED_OIL.getFluid();
		FuelManager.biogasEngineFuel.put(seedOil, new EngineBronzeFuel(seedOil,
			Constants.ENGINE_FUEL_VALUE_SEED_OIL, Constants.ENGINE_CYCLE_DURATION_SEED_OIL, 1));

		Fluid honey = ForestryFluids.HONEY.getFluid();
		FuelManager.biogasEngineFuel.put(honey, new EngineBronzeFuel(honey,
			Constants.ENGINE_FUEL_VALUE_HONEY, Constants.ENGINE_CYCLE_DURATION_HONEY, 1));

		Fluid juice = ForestryFluids.JUICE.getFluid();
		FuelManager.biogasEngineFuel.put(juice, new EngineBronzeFuel(juice,
			Constants.ENGINE_FUEL_VALUE_JUICE, Constants.ENGINE_CYCLE_DURATION_JUICE, 1));

		// Peat Engine
		FuelManager.peatEngineFuel.put(CoreItems.PEAT.item(), new EngineCopperFuel(CoreItems.PEAT.item(), Constants.ENGINE_COPPER_FUEL_VALUE_PEAT, Constants.ENGINE_COPPER_CYCLE_DURATION_PEAT));
		FuelManager.peatEngineFuel.put(CoreItems.BITUMINOUS_PEAT.item(), new EngineCopperFuel(CoreItems.BITUMINOUS_PEAT.item(), Constants.ENGINE_COPPER_FUEL_VALUE_BITUMINOUS_PEAT, Constants.ENGINE_COPPER_CYCLE_DURATION_BITUMINOUS_PEAT));
	}

}
