package forestry.lepidopterology;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import forestry.api.modules.ForestryModule;
import forestry.api.modules.ForestryModuleIds;
import forestry.lepidopterology.commands.CommandButterfly;
import forestry.lepidopterology.entities.EntityButterfly;
import forestry.lepidopterology.features.LepidopterologyEntities;
import forestry.modules.BlankForestryModule;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.Identifier;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;

import java.util.List;

@ForestryModule
public class ModuleLepidopterology extends BlankForestryModule {
	public static int maxDistance = 64;
	private static final boolean allowPollination = true;
	public static final Object2FloatOpenHashMap<String> spawnRarities = new Object2FloatOpenHashMap<>();
	public static boolean spawnButterflysFromLeaves = true;
	private static final boolean generateCocoons = false;
	private static final float generateCocoonsAmount = 1.0f;
	private static final float serumChance = 0.55f;
	private static final float secondSerumChance = 0;

	@Override
	public void registerEvents(BusGroup modBusGroup) {
		EntityTravelToDimensionEvent.BUS.addListener(ModuleLepidopterology::onEntityTravelToDimension);
		EntityAttributeCreationEvent.BUS.addListener(ModuleLepidopterology::onAttributeCreate);
	}

	public static boolean onEntityTravelToDimension(EntityTravelToDimensionEvent event) {
		return event.getEntity() instanceof EntityButterfly;
	}

	public static void onAttributeCreate(EntityAttributeCreationEvent event) {
		event.put(LepidopterologyEntities.BUTTERFLY.entityType(), LepidopterologyEntities.BUTTERFLY.createAttributes().build());
	}

	@Override
	public Identifier getId() {
		return ForestryModuleIds.LEPIDOPTEROLOGY;
	}

	@Override
	public void addToRootCommand(LiteralArgumentBuilder<CommandSourceStack> command) {
		command.then(CommandButterfly.register());
	}

	@Override
	public List<Identifier> getModuleDependencies() {
		return List.of(ForestryModuleIds.CORE);
	}

	public static boolean isAllowPollination() {
		return allowPollination;
	}

	public static boolean isGenerateCocoons() {
		return generateCocoons;
	}

	public static float getGenerateCocoonsAmount() {
		return generateCocoonsAmount;
	}

	public static float getSerumChance() {
		return serumChance;
	}

	public static float getSecondSerumChance() {
		return secondSerumChance;
	}
}
