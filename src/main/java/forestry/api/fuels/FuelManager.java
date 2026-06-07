package forestry.api.fuels;

import forestry.core.utils.datastructures.ItemStackMap;
import net.minecraft.world.level.material.Fluid;

import java.util.Map;

// todo get rid of the ItemStack maps
public class FuelManager {
	/**
	 * Add new fuels for the fermenter here (i.e. fertilizer).
	 */
	public static ItemStackMap<FermenterFuel> fermenterFuel;
	/**
	 * Add new resources for the moistener here (i.e. wheat)
	 */
	public static ItemStackMap<MoistenerFuel> moistenerResource;
	/**
	 * Add new substrates for the rainmaker here
	 */
	public static ItemStackMap<RainSubstrate> rainSubstrate;
	/**
	 * Add new fuels for EngineBronze (= biogas engine) here
	 */
	public static Map<Fluid, EngineBronzeFuel> biogasEngineFuel;
	/**
	 * Add new fuels for EngineCopper (= peat-fired engine) here
	 */
	public static ItemStackMap<EngineCopperFuel> peatEngineFuel;

}
