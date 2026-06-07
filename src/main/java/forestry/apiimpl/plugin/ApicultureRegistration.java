package forestry.apiimpl.plugin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import forestry.api.apiculture.IActivityType;
import forestry.api.apiculture.IFlowerType;
import forestry.api.apiculture.genetics.IBeeEffect;
import forestry.api.apiculture.genetics.IBeeSpecies;
import forestry.api.apiculture.hives.IHiveDefinition;
import forestry.api.genetics.ISpeciesType;
import forestry.api.genetics.alleles.IAllele;
import forestry.api.genetics.alleles.IChromosome;
import forestry.api.plugin.IApicultureRegistration;
import forestry.api.plugin.IBeeSpeciesBuilder;
import forestry.api.plugin.IHiveBuilder;
import forestry.apiculture.VillageHive;
import forestry.apiculture.hives.HiveManager;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;

public class ApicultureRegistration extends SpeciesRegistration<IBeeSpeciesBuilder, IBeeSpecies, BeeSpeciesBuilder> implements IApicultureRegistration {
	private final ModifiableRegistrar<Identifier, IHiveBuilder, HiveBuilder> hives = new ModifiableRegistrar<>(IHiveBuilder.class);
	private final Registrar<Identifier, IFlowerType, IFlowerType> flowerTypes = new Registrar<>(IFlowerType.class);
	private final Registrar<Identifier, IBeeEffect, IBeeEffect> beeEffects = new Registrar<>(IBeeEffect.class);
	private final Registrar<Identifier, IActivityType, IActivityType> activityTypes = new Registrar<>(IActivityType.class);
	private final ArrayList<VillageHive> commonVillageHives = new ArrayList<>();
	private final ArrayList<VillageHive> rareVillageHives = new ArrayList<>();
	private final Object2FloatOpenHashMap<Item> swarmerMaterials = new Object2FloatOpenHashMap<>();

	public ApicultureRegistration(ISpeciesType<IBeeSpecies, ?> type) {
		super(type);
	}

	@Override
	protected BeeSpeciesBuilder createSpeciesBuilder(Identifier id, String genus, String species, MutationsRegistration mutations) {
		return new BeeSpeciesBuilder(id, genus, species, mutations);
	}

	@Override
	public IBeeSpeciesBuilder registerSpecies(Identifier id, String genus, String species, boolean dominant, TextColor outline) {
		return register(id, genus, species)
			.setDominant(dominant)
			.setOutline(outline);
	}

	@Override
	public void addVillageBee(Identifier speciesId, boolean rare, Map<IChromosome<?>, IAllele> alleles) {
		(rare ? this.rareVillageHives : this.commonVillageHives).add(new VillageHive(speciesId, alleles));
	}

	@Override
	public void registerFlowerType(Identifier id, IFlowerType type) {
		this.flowerTypes.create(id, type);
	}

	public ImmutableMap<Identifier, IFlowerType> getFlowerTypes() {
		return this.flowerTypes.build();
	}

	@Override
	public void registerBeeEffect(Identifier id, IBeeEffect effect) {
		this.beeEffects.create(id, effect);
	}

	public ImmutableMap<Identifier, IBeeEffect> getBeeEffects() {
		return this.beeEffects.build();
	}

	@Override
	public void registerActivityType(Identifier id, IActivityType type) {
		this.activityTypes.create(id, type);
	}

	public ImmutableMap<Identifier, IActivityType> getActivityTypes() {
		return this.activityTypes.build();
	}

	@Override
	public IHiveBuilder registerHive(Identifier id, IHiveDefinition definition) {
		return this.hives.create(id, new HiveBuilder(definition));
	}

	@Override
	public void modifyHive(Identifier id, Consumer<IHiveBuilder> builder) {
		this.hives.modify(id, builder);
	}

	@Override
	public void registerSwarmerMaterial(Item swarmItem, float swarmChance) {
		this.swarmerMaterials.put(swarmItem, swarmChance);
	}

	public HiveManager buildHiveManager() {
		// todo validate IDs of the village species OR use the species directly
		return new HiveManager(this.hives.build(HiveBuilder::build), ImmutableList.copyOf(this.commonVillageHives), ImmutableList.copyOf(this.rareVillageHives), new Object2FloatOpenHashMap<>(this.swarmerMaterials));
	}
}
