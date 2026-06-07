package forestry.core;

import forestry.api.ForestryConstants;
import forestry.api.client.IClientModuleHandler;
import forestry.api.client.IForestryClientApi;
import forestry.apiimpl.client.ForestryClientApiImpl;
import forestry.apiculture.features.ApicultureMenuTypes;
import forestry.core.config.Constants;
import forestry.core.features.CoreMenuTypes;
import forestry.core.gui.minimal.ForestryMachineScreen;
import forestry.cultivation.features.CultivationMenuTypes;
import forestry.energy.features.EnergyMenus;
import forestry.factory.features.FactoryMenuTypes;
import forestry.farming.features.FarmingMenuTypes;
import forestry.sorting.features.SortingMenuTypes;
import forestry.worktable.features.WorktableMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.Identifier;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public final class ForestryMenuClientSetup implements IClientModuleHandler {
	@Override
	public void registerEvents(BusGroup modBusGroup) {
		FMLClientSetupEvent.getBus(modBusGroup).addListener(ForestryMenuClientSetup::onClientSetup);
		RegisterClientReloadListenersEvent.BUS.addListener(((ForestryClientApiImpl) IForestryClientApi.INSTANCE)::initializeTextureManager);
	}

	private static void onClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			MenuScreens.register(CoreMenuTypes.ANALYZER.menuType(), screen(gui("alyzer"), 176, 166));
			MenuScreens.register(CoreMenuTypes.ESCRITOIRE.menuType(), screen(gui("escritoire"), 176, 200));
			MenuScreens.register(CoreMenuTypes.NATURALIST_INVENTORY.menuType(), screen(gui("apiaristinventory"), 176, 200));

			MenuScreens.register(EnergyMenus.ENGINE_PEAT.menuType(), screen(gui("peatengine"), 176, 166));
			MenuScreens.register(EnergyMenus.ENGINE_BIOGAS.menuType(), screen(gui("bioengine"), 176, 166));

			MenuScreens.register(FactoryMenuTypes.BOTTLER.menuType(), screen(gui("bottler"), 176, 166));
			MenuScreens.register(FactoryMenuTypes.CARPENTER.menuType(), screen(gui("carpenter"), 176, 218));
			MenuScreens.register(FactoryMenuTypes.CENTRIFUGE.menuType(), screen(gui("centrifugesocket2"), 176, 166));
			MenuScreens.register(FactoryMenuTypes.FABRICATOR.menuType(), screen(gui("fabricator"), 176, 200));
			MenuScreens.register(FactoryMenuTypes.FERMENTER.menuType(), screen(gui("fermenter"), 176, 166));
			MenuScreens.register(FactoryMenuTypes.MOISTENER.menuType(), screen(gui("moistener"), 176, 166));
			MenuScreens.register(FactoryMenuTypes.RAINTANK.menuType(), screen(gui("raintank"), 176, 166));
			MenuScreens.register(FactoryMenuTypes.SQUEEZER.menuType(), screen(gui("squeezersocket"), 176, 166));
			MenuScreens.register(FactoryMenuTypes.STILL.menuType(), screen(gui("still"), 176, 166));

			MenuScreens.register(ApicultureMenuTypes.ALVEARY.menuType(), screen(gui("alveary"), 176, 190));
			MenuScreens.register(ApicultureMenuTypes.ALVEARY_HYGROREGULATOR.menuType(), screen(gui("hygroregulator"), 176, 166));
			MenuScreens.register(ApicultureMenuTypes.ALVEARY_SIEVE.menuType(), screen(gui("sieve"), 176, 166));
			MenuScreens.register(ApicultureMenuTypes.ALVEARY_SWARMER.menuType(), screen(gui("swarmer"), 176, 166));
			MenuScreens.register(ApicultureMenuTypes.BEE_HOUSING.menuType(), screen(gui("apiary"), 176, 166));

			MenuScreens.register(WorktableMenus.WORKTABLE.menuType(), screen(gui("worktable2"), 176, 218));

			MenuScreens.register(FarmingMenuTypes.FARM.menuType(), screen(gui("mfarm"), 176, 200));
			MenuScreens.register(CultivationMenuTypes.PLANTER.menuType(), screen(gui("planter"), 176, 200));
			MenuScreens.register(SortingMenuTypes.GENETIC_FILTER.menuType(), screen(gui("filter"), 176, 200));
		});
	}

	private static Identifier gui(String name) {
		return ForestryConstants.forestry(Constants.TEXTURE_PATH_GUI + "/" + name);
	}

	private static <M extends net.minecraft.world.inventory.AbstractContainerMenu> MenuScreens.ScreenConstructor<M, ForestryMachineScreen<M>> screen(Identifier texture, int width, int height) {
		return (menu, inv, title) -> new ForestryMachineScreen<>(menu, inv, title, texture, width, height);
	}
}
