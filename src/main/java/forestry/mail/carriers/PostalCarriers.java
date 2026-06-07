package forestry.mail.carriers;

import forestry.api.ForestryConstants;
import forestry.api.mail.IPostalCarrier;
import forestry.mail.carriers.players.CarrierPlayer;
import forestry.mail.carriers.trading.CarrierTrader;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class PostalCarriers {
	private static final ResourceKey<Registry<IPostalCarrier>> REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(ForestryConstants.MOD_ID, "postal_carriers"));
	private static final DeferredRegister<IPostalCarrier> POSTAL_CARRIERS = DeferredRegister.create(REGISTRY_KEY, ForestryConstants.MOD_ID);
	public static final Supplier<IForgeRegistry<IPostalCarrier>> REGISTRY = POSTAL_CARRIERS.makeRegistry(() -> new RegistryBuilder<IPostalCarrier>().disableSaving());

    public static final RegistryObject<IPostalCarrier> PLAYER = POSTAL_CARRIERS.register("player", CarrierPlayer::new);
	public static final RegistryObject<IPostalCarrier> TRADER = POSTAL_CARRIERS.register("trader", CarrierTrader::new);

	public static void register(BusGroup modBusGroup) {
		POSTAL_CARRIERS.register(modBusGroup);
	}
}
