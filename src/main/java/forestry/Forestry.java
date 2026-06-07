package forestry;

import forestry.api.ForestryConstants;
import forestry.api.IForestryApi;
import forestry.apiimpl.plugin.PluginManager;
import forestry.core.config.ForestryConfig;
import forestry.core.network.NetworkHandler;
import forestry.modules.ForestryModuleManager;
import forestry.modules.ModuleUtil;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Forestry Minecraft Mod
 *
 * @author SirSengir
 */
@Mod(ForestryConstants.MOD_ID)
public class Forestry {
	public static final boolean DEBUG = ModList.isLoaded("modkit");
	public static final Logger LOGGER = LogManager.getLogger(ForestryConstants.MOD_ID);

	public Forestry(FMLJavaModLoadingContext context) {
		ModuleUtil.registerModBusGroup(ForestryConstants.MOD_ID, context.getModBusGroup());

		ForestryModuleManager moduleManager = (ForestryModuleManager) IForestryApi.INSTANCE.getModuleManager();
		moduleManager.init();
		NetworkHandler.register();

		PluginManager.loadPlugins();
		PluginManager.registerErrors();

		ForestryConfig.register(context);

		ForgeMod.enableMilkFluid();
	}
}
