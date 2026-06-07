package forestry.apiimpl.plugin;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.mojang.datafixers.util.Pair;
import forestry.Forestry;
import forestry.api.ForestryConstants;
import forestry.api.IForestryApi;
import forestry.api.apiculture.ForestryBeeSpecies;
import forestry.api.apiculture.genetics.BeeLifeStage;
import forestry.api.apiculture.genetics.IBeeSpecies;
import forestry.api.arboriculture.ITreeSpecies;
import forestry.api.arboriculture.ForestryTreeSpecies;
import forestry.api.client.arboriculture.ForestryLeafSprites;
import forestry.api.client.IForestryClientApi;
import forestry.api.client.arboriculture.ILeafSprite;
import forestry.api.client.arboriculture.ILeafTint;
import forestry.api.client.genetics.IAnalyzerPlugin;
import forestry.api.circuits.CircuitHolder;
import forestry.api.circuits.ICircuit;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.core.IError;
import forestry.api.genetics.ILifeStage;
import forestry.api.genetics.IMutationManager;
import forestry.api.genetics.ISpeciesType;
import forestry.api.genetics.ITaxon;
import forestry.api.genetics.pollen.IPollenType;
import forestry.api.lepidopterology.genetics.IButterflySpecies;
import forestry.api.plugin.IForestryPlugin;
import forestry.api.plugin.IPollenRegistration;
import forestry.apiimpl.ForestryApiImpl;
import forestry.apiimpl.client.BeeClientManager;
import forestry.apiimpl.client.ButterflyClientManager;
import forestry.apiimpl.client.ForestryClientApiImpl;
import forestry.apiimpl.client.TreeClientManager;
import forestry.apiimpl.client.genetics.GeneticClientManager;
import forestry.apiimpl.client.plugin.ClientRegistration;
import forestry.apiimpl.GeneticManager;
import forestry.core.circuits.CircuitLayout;
import forestry.core.circuits.CircuitManager;
import forestry.core.errors.ErrorManager;
import forestry.core.genetics.PollenManager;
import forestry.core.genetics.alleles.AlleleManager;
import forestry.core.utils.SpeciesUtil;
import forestry.farming.FarmingManager;
import forestry.plugin.DefaultForestryPlugin;
import forestry.sorting.FilterManager;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ShortOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import net.minecraft.resources.Identifier;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

import javax.annotation.Nullable;
import java.util.*;

public class PluginManager {
	private static final ArrayList<IForestryPlugin> LOADED_PLUGINS = new ArrayList<>();

	// Loads all plugins from the service loader.
	public static void loadPlugins() {
		ServiceLoader<IForestryPlugin> serviceLoader = ServiceLoader.load(IForestryPlugin.class);

		serviceLoader.stream().map(ServiceLoader.Provider::get).sorted(Comparator.comparing(IForestryPlugin::id)).forEachOrdered(plugin -> {
			if (plugin.shouldLoad()) {
				if (plugin.getClass() == DefaultForestryPlugin.class) {
					LOADED_PLUGINS.add(0, plugin);
				} else {
					LOADED_PLUGINS.add(plugin);
				}
				Forestry.LOGGER.debug("Registered IForestryPlugin {} with class {}", plugin.id(), plugin.getClass().getName());
			} else {
				Forestry.LOGGER.warn("Detected IForestryPlugin {} with class {} but did not load it because IForestryPlugin.shouldLoad returned false.", plugin.id(), plugin.getClass().getName());
			}
		});

		LOADED_PLUGINS.trimToSize();
	}

	public static void registerErrors() {
		ErrorRegistration registration = new ErrorRegistration();

		for (IForestryPlugin plugin : LOADED_PLUGINS) {
			plugin.registerErrors(registration);
		}

		ArrayList<IError> errors = registration.getErrors();
		int errorCount = errors.size();
		Short2ObjectOpenHashMap<IError> byNumericId = new Short2ObjectOpenHashMap<>(errorCount);
		Object2ShortOpenHashMap<IError> numericIdLookup = new Object2ShortOpenHashMap<>(errorCount);
		ImmutableMap.Builder<Identifier, IError> byId = ImmutableMap.builderWithExpectedSize(errorCount);

		for (int i = 0; i < errors.size(); i++) {
			IError error = errors.get(i);
			byNumericId.put((short) i, error);
			numericIdLookup.put(error, (short) i);
			byId.put(error.getId(), error);
		}

		((ForestryApiImpl) IForestryApi.INSTANCE).setErrorManager(new ErrorManager(byNumericId, numericIdLookup, byId.build()));
	}

	// Runs after all items are registered so that electron tubes and circuit boards are available.
	public static void registerCircuits() {
		CircuitRegistration registration = new CircuitRegistration();

		for (IForestryPlugin plugin : LOADED_PLUGINS) {
			// TODO remove in 1.20 when FMLCommonSetupEvent throws
			// rethrow swallowed exception
			try {
				plugin.registerCircuits(registration);
			} catch (Throwable e) {
				asyncThrown = new RuntimeException("An error was thrown by plugin " + plugin.id() + " during IForestryPlugin.registerCircuits", e);
				Forestry.LOGGER.fatal(asyncThrown);
			}
		}

		ArrayList<CircuitLayout> layouts = registration.getLayouts();
		ImmutableMap.Builder<String, ICircuitLayout> layoutsByIdBuilder = ImmutableMap.builderWithExpectedSize(layouts.size());

		for (CircuitLayout layout : layouts) {
			// Layouts by ID
			layoutsByIdBuilder.put(layout.getId(), layout);
		}

		ImmutableMap<String, ICircuitLayout> layoutsById = layoutsByIdBuilder.build();

		ArrayList<CircuitHolder> circuits = registration.getCircuits();
		ImmutableMultimap.Builder<ICircuitLayout, CircuitHolder> circuitHoldersBuilder = new ImmutableMultimap.Builder<>();
		ImmutableMap.Builder<String, ICircuit> circuitsBuilder = ImmutableMap.builderWithExpectedSize(circuits.size());

		for (CircuitHolder holder : circuits) {
			ICircuitLayout layout = layoutsById.get(holder.layoutId());

			if (layout == null) {
				throw new IllegalStateException("Attempted to register a CircuitHolder but no layout was registered with its layout ID: " + holder);
			}

			// Circuit holders by layout
			circuitHoldersBuilder.put(layout, holder);
			// Circuits by ID
			ICircuit circuit = holder.circuit();
			circuitsBuilder.put(circuit.getId(), circuit);
		}

		try {
			((ForestryApiImpl) IForestryApi.INSTANCE).setCircuitManager(new CircuitManager(circuitHoldersBuilder.build(), layoutsById, circuitsBuilder.buildOrThrow()));
		} catch (IllegalArgumentException exception) {
			Forestry.LOGGER.fatal("Failed to register circuits: two circuits were registered with the same ID");
			throw exception;
		}
	}

	public static void registerGenetics() {
		GeneticRegistration registration = new GeneticRegistration();

		// Register SPECIES TYPES, karyotypes, filter rules and set up taxonomy
		for (IForestryPlugin plugin : LOADED_PLUGINS) {
			plugin.registerGenetics(registration);
		}

		ImmutableMap<Identifier, ISpeciesType<?, ?>> speciesTypes = registration.buildSpeciesTypes();
		ImmutableMap<String, ITaxon> taxa = registration.buildTaxa();

		Forestry.LOGGER.debug("Registered {} species types: {}", speciesTypes.size(), Arrays.toString(speciesTypes.keySet().toArray(new Identifier[0])));

		ForestryApiImpl api = (ForestryApiImpl) IForestryApi.INSTANCE;
		AlleleManager alleleManager = ((AlleleManager) api.getAlleleManager());
		GeneticManager geneticManager = new GeneticManager(taxa, speciesTypes);
		api.setGeneticManager(geneticManager);
		api.setFilterManager(new FilterManager(registration.getFilterRuleTypes()));

		// block registration of new chromosomes
		alleleManager.setRegistrationState(AlleleManager.REGISTRATION_CHROMOSOMES_COMPLETE);

		// Register SPECIES for each type
		LinkedHashMap<ISpeciesType<?, ?>, ImmutableMap<Identifier, ?>> allSpecies = new LinkedHashMap<>(speciesTypes.size());
		IdentityHashMap<ISpeciesType<?, ?>, IMutationManager<?>> allMutations = new IdentityHashMap<>(speciesTypes.size());

		// go through species builders and build each species
		for (ISpeciesType<?, ?> speciesType : speciesTypes.values()) {
			// species and mutations
			Pair<? extends ImmutableMap<Identifier, ?>, ? extends IMutationManager<?>> pair = speciesType.handleSpeciesRegistration(LOADED_PLUGINS);
			ImmutableMap<Identifier, ?> species = pair.getFirst();
			IMutationManager<?> mutations = pair.getSecond();

			allSpecies.put(speciesType, species);
			allMutations.put(speciesType, mutations);

			Forestry.LOGGER.debug("Registered {} species for species type {}", species.size(), speciesType.id());
			Forestry.LOGGER.debug("Registered {} mutations for species type {}", mutations.getAllMutations().size(), speciesType.id());
		}

		// block registration of new alleles and verify all registry alleles have values
		alleleManager.setRegistrationState(AlleleManager.REGISTRATION_ALLELES_COMPLETE);

		for (Map.Entry<ISpeciesType<?, ?>, ImmutableMap<Identifier, ?>> entry : allSpecies.entrySet()) {
			ISpeciesType<?, ?> speciesType = entry.getKey();

			speciesType.onSpeciesRegistered((ImmutableMap) entry.getValue(), (IMutationManager) allMutations.get(speciesType));

			if (speciesType.getAllSpecies().isEmpty()) {
				throw new IllegalStateException("Failed to register species for type " + speciesType.id());
			}
			// this will throw an exception if mutations aren't populated
			speciesType.getMutations();
		}

		geneticManager.setMutations(ImmutableMap.copyOf(allMutations));
	}

	public static void registerFarming() {
		FarmingRegistration registration = new FarmingRegistration();

		for (IForestryPlugin plugin : LOADED_PLUGINS) {
			try {
				plugin.registerFarming(registration);
			} catch (Throwable t) {
				throw new RuntimeException("An error was thrown by plugin " + plugin.id() + " during IForestryPlugin.registerFarming", t);
			}
		}

		// Defensive copy of fertilizers
		FarmingManager manager = new FarmingManager(new Object2IntOpenHashMap<>(registration.getFertilizers()), registration.buildFarmTypes());

		((ForestryApiImpl) IForestryApi.INSTANCE).setFarmingManager(manager);
	}

	public static void registerPollen() {
		HashMap<Identifier, IPollenType<?>> pollenTypes = new HashMap<>();
		IPollenRegistration registration = pollen -> {
			Identifier id = pollen.id();
			if (pollenTypes.containsKey(id)) {
				throw new IllegalStateException("A pollen type was already registered with ID " + pollen + ": " + pollenTypes.get(id));
			} else {
				pollenTypes.put(id, pollen);
			}
		};

		for (IForestryPlugin plugin : LOADED_PLUGINS) {
			plugin.registerPollen(registration);
		}

		((ForestryApiImpl) IForestryApi.INSTANCE).setPollenManager(new PollenManager(ImmutableMap.copyOf(pollenTypes)));
	}

	// Todo remove in 1.20 when FMLCommonSetupEvent throws exceptions again
	@Nullable
	@Deprecated
	private static RuntimeException asyncThrown = null;

	@Deprecated
	public static void registerAsyncException(BusGroup modBusGroup) {
		FMLLoadCompleteEvent.getBus(modBusGroup).addListener((FMLLoadCompleteEvent event) -> {
			if (asyncThrown != null) {
				throw asyncThrown;
			}
		});
	}

	public static void registerClient() {
		ClientRegistration registration = new ClientRegistration();
		registerDefaultClientData(registration);

		for (IForestryPlugin plugin : LOADED_PLUGINS) {
			plugin.registerClient(consumer -> consumer.accept(registration));
		}

		ForestryClientApiImpl clientApi = (ForestryClientApiImpl) IForestryClientApi.INSTANCE;
		clientApi.setBeeManager(new BeeClientManager(registration));

		try {
			registerTreeAndButterflyClients(registration, clientApi);
		} catch (IllegalStateException e) {
			Forestry.LOGGER.debug("Deferring tree/butterfly client registration until genetics are initialized");
		}
	}

	private static void registerTreeAndButterflyClients(ClientRegistration registration, ForestryClientApiImpl clientApi) {
		HashMap<Identifier, ILeafSprite> spritesById = registration.getLeafSprites();
		HashMap<Identifier, ILeafTint> tintsById = registration.getTints();
		HashMap<Identifier, Pair<Identifier, Identifier>> modelsById = registration.getSaplingModels();
		List<ITreeSpecies> treeSpecies = SpeciesUtil.getAllTreeSpecies();
		IdentityHashMap<ITreeSpecies, ILeafSprite> sprites = new IdentityHashMap<>(treeSpecies.size());
		IdentityHashMap<ITreeSpecies, ILeafTint> tints = new IdentityHashMap<>(treeSpecies.size());
		IdentityHashMap<ITreeSpecies, Pair<Identifier, Identifier>> models = new IdentityHashMap<>(treeSpecies.size());

		for (ITreeSpecies species : treeSpecies) {
			Identifier id = species.id();

			ILeafSprite sprite = Objects.requireNonNull(spritesById.get(id), "No leaf sprite registered for tree species " + id + ", did you call IClientRegistration.setLeafSprite ?");
			ILeafTint tint = tintsById.getOrDefault(id, (level, pos) -> species.getEscritoireColor());
			Pair<Identifier, Identifier> modelPair = modelsById.get(id);

			sprites.put(species, sprite);
			tints.put(species, tint);

			if (modelPair != null) {
				models.put(species, modelPair);
			} else {
				String path = id.getPath().replace("tree_", "");
				models.put(species, Pair.of(
					Identifier.fromNamespaceAndPath(id.getNamespace(), "block/" + path + "_sapling"),
					Identifier.fromNamespaceAndPath(id.getNamespace(), "item/" + path + "_sapling")
				));
			}
		}

		clientApi.setTreeManager(new TreeClientManager(sprites, tints, models));

		HashMap<Identifier, Pair<Identifier, Identifier>> butterflyTexturesById = registration.getButterflyTextures();
		List<IButterflySpecies> butterflySpecies = SpeciesUtil.BUTTERFLY_TYPE.get().getAllSpecies();
		IdentityHashMap<IButterflySpecies, Pair<Identifier, Identifier>> butterflyTextures = new IdentityHashMap<>(butterflySpecies.size());

		for (IButterflySpecies species : butterflySpecies) {
			Identifier id = species.id();
			Pair<Identifier, Identifier> texturePair = butterflyTexturesById.get(id);

			if (texturePair != null) {
				butterflyTextures.put(species, texturePair);
			} else {
				String path = id.getPath().replace("butterfly_", "");
				butterflyTextures.put(species, Pair.of(
					Identifier.fromNamespaceAndPath(id.getNamespace(), "item/butterfly/" + path),
					Identifier.fromNamespaceAndPath(id.getNamespace(), "textures/entity/butterfly/" + path + ".png")
				));
			}
		}
		clientApi.setButterflyManager(new ButterflyClientManager(butterflyTextures));

		HashMap<Identifier, IAnalyzerPlugin<?, ?>> analyzerPluginsById = registration.getAnalyzerPlugins();
		IdentityHashMap<ISpeciesType<?, ?>, IAnalyzerPlugin<?, ?>> analyzerPlugins = new IdentityHashMap<>(analyzerPluginsById.size());
		for (ISpeciesType<?, ?> type : IForestryApi.INSTANCE.getGeneticManager().getSpeciesTypes()) {
			IAnalyzerPlugin<?, ?> plugin = analyzerPluginsById.get(type.id());
			if (plugin == null) {
				Forestry.LOGGER.warn("No IAnalyzerPlugin registered for species type {}", type.id());
			} else {
				analyzerPlugins.put(type, plugin);
			}
		}
		clientApi.setGeneticsManager(new GeneticClientManager(analyzerPlugins));
	}

	private static void registerDefaultClientData(ClientRegistration client) {
		client.setDefaultBeeModel(BeeLifeStage.DRONE, ForestryConstants.forestry("item/bee_drone_default"));
		client.setDefaultBeeModel(BeeLifeStage.PRINCESS, ForestryConstants.forestry("item/bee_princess_default"));
		client.setDefaultBeeModel(BeeLifeStage.QUEEN, ForestryConstants.forestry("item/bee_queen_default"));
		client.setDefaultBeeModel(BeeLifeStage.LARVAE, ForestryConstants.forestry("item/bee_larvae_default"));
		client.setCustomBeeModel(ForestryBeeSpecies.VANILLA, BeeLifeStage.DRONE, ForestryConstants.forestry("item/bee_drone_cube"));
		client.setCustomBeeModel(ForestryBeeSpecies.VANILLA, BeeLifeStage.PRINCESS, ForestryConstants.forestry("item/bee_princess_cube"));
		client.setCustomBeeModel(ForestryBeeSpecies.VANILLA, BeeLifeStage.QUEEN, ForestryConstants.forestry("item/bee_queen_cube"));

		client.setLeafSprite(ForestryTreeSpecies.OAK, ForestryLeafSprites.OAK);
		client.setLeafSprite(ForestryTreeSpecies.DARK_OAK, ForestryLeafSprites.OAK);
		client.setLeafSprite(ForestryTreeSpecies.BIRCH, ForestryLeafSprites.BIRCH);
		client.setLeafSprite(ForestryTreeSpecies.ACACIA_VANILLA, ForestryLeafSprites.ACACIA);
		client.setLeafSprite(ForestryTreeSpecies.SPRUCE, ForestryLeafSprites.SPRUCE);
		client.setLeafSprite(ForestryTreeSpecies.JUNGLE, ForestryLeafSprites.JUNGLE);
		client.setLeafSprite(ForestryTreeSpecies.CHERRY_VANILLA, ForestryLeafSprites.CHERRY);
		client.setLeafTint(ForestryTreeSpecies.OAK, ILeafTint.DEFAULT);
		client.setLeafTint(ForestryTreeSpecies.DARK_OAK, ILeafTint.DEFAULT);
		client.setLeafTint(ForestryTreeSpecies.JUNGLE, ILeafTint.DEFAULT);
		client.setLeafTint(ForestryTreeSpecies.ACACIA_VANILLA, ILeafTint.DEFAULT);
		client.setLeafTint(ForestryTreeSpecies.CHERRY_VANILLA, (level, pos) -> 0xffffff);
	}
}
