package forestry.apiimpl.plugin;

import com.google.common.collect.ImmutableMap;
import forestry.api.arboriculture.ICharcoalManager;
import forestry.api.arboriculture.ITreeSpecies;
import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.genetics.IFruit;
import forestry.api.arboriculture.genetics.ITreeEffect;
import forestry.api.genetics.ISpeciesType;
import forestry.api.plugin.IArboricultureRegistration;
import forestry.api.plugin.ITreeSpeciesBuilder;
import forestry.arboriculture.TreeManager;
import forestry.arboriculture.charcoal.CharcoalManager;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ArboricultureRegistration extends SpeciesRegistration<ITreeSpeciesBuilder, ITreeSpecies, TreeSpeciesBuilder> implements IArboricultureRegistration {
	private final Registrar<Identifier, IFruit, IFruit> fruits = new Registrar<>(IFruit.class);
	private final Registrar<Identifier, ITreeEffect, ITreeEffect> effects = new Registrar<>(ITreeEffect.class);
	private final ImmutableMap.Builder<Block, Block> refractoryWaxables = ImmutableMap.builder();
	private final ICharcoalManager charcoalPitWalls = new CharcoalManager();

	public ArboricultureRegistration(ISpeciesType<ITreeSpecies, ?> type) {
		super(type);
	}

	@Override
	protected TreeSpeciesBuilder createSpeciesBuilder(Identifier id, String genus, String species, MutationsRegistration mutations) {
		return new TreeSpeciesBuilder(id, genus, species, mutations);
	}

	@Override
	public ITreeSpeciesBuilder registerSpecies(Identifier id, String genus, String species, boolean dominant, TextColor escritoireColor, IWoodType woodType) {
		return register(id, genus, species)
			.setDominant(dominant)
			.setEscritoireColor(escritoireColor)
			.setWoodType(woodType);
	}

	@Override
	public void registerFruit(Identifier id, IFruit fruit) {
		this.fruits.create(id, fruit);
	}

	@Override
	public void registerTreeEffect(Identifier id, ITreeEffect effect) {
		this.effects.create(id, effect);
	}

	@Override
	public void registerRefractoryWaxable(Block block, Block waxedForm) {
		this.refractoryWaxables.put(block, waxedForm);
	}

	@Override
	public void registerCharcoalPitWall(BlockState state, int charcoal) {
		this.charcoalPitWalls.registerWall(state, charcoal);
	}

	public ImmutableMap<Identifier, IFruit> getFruits() {
		return this.fruits.build();
	}

	public ImmutableMap<Identifier, ITreeEffect> getEffects() {
		return this.effects.build();
	}

	public TreeManager buildTreeManager() {
		return new TreeManager(this.refractoryWaxables.build(), new CharcoalManager());
	}
}
