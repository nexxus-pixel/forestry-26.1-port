package forestry.core;

import forestry.api.IForestryApi;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.ISpeciesType;
import forestry.apiculture.features.ApicultureEffects;
import forestry.core.worldgen.VillagerJigsaw;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;

public final class EventHandlerCore {
	private EventHandlerCore() {
	}

	public static void register() {
		PlayerEvent.PlayerLoggedInEvent.BUS.addListener(EventHandlerCore::handlePlayerLoggedIn);
		PlayerEvent.PlayerChangedDimensionEvent.BUS.addListener(EventHandlerCore::handlePlayerChangedDimension);
		LivingAttackEvent.BUS.addListener(EventHandlerCore::doHakunaDamageReduction);
		ServerAboutToStartEvent.BUS.addListener(EventHandlerCore::serverAboutToStart);
	}

	private static void handlePlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		syncBreedingTrackers(event.getEntity());
	}

	private static void handlePlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		syncBreedingTrackers(event.getEntity());
	}

	private static void syncBreedingTrackers(Player player) {
		for (ISpeciesType<?, ?> type : IForestryApi.INSTANCE.getGeneticManager().getSpeciesTypes()) {
			IBreedingTracker breedingTracker = type.getBreedingTracker(player.level(), player.getGameProfile());
			breedingTracker.syncToPlayer(player);
		}
	}

	private static boolean doHakunaDamageReduction(LivingAttackEvent event) {
		if (event.getEntity().hasEffect(ApicultureEffects.HAKUNA_MATATA.getHolder().orElseThrow())) {
			if (event.getAmount() > 5) {
				event.getEntity().removeEffect(ApicultureEffects.HAKUNA_MATATA.getHolder().orElseThrow());
				event.getEntity().addEffect(new MobEffectInstance(ApicultureEffects.MATATA.getHolder().orElseThrow(), (int) (300 * event.getAmount())));
				event.getEntity().playSound(SoundEvents.WITHER_BREAK_BLOCK);
				if (event.getSource().getEntity() instanceof LivingEntity attacker) {
					if (attacker.hasEffect(ApicultureEffects.HAKUNA_MATATA.getHolder().orElseThrow())) {
						attacker.removeEffect(ApicultureEffects.HAKUNA_MATATA.getHolder().orElseThrow());
						attacker.addEffect(new MobEffectInstance(ApicultureEffects.MATATA.getHolder().orElseThrow(), (int) (300 * event.getAmount())));
						attacker.playSound(SoundEvents.WITHER_BREAK_BLOCK);
					}
				}
			}
			return true;
		}
		return false;
	}

	private static void serverAboutToStart(ServerAboutToStartEvent event) {
		Registry<StructureTemplatePool> pools = event.getServer().registryAccess().lookupOrThrow(Registries.TEMPLATE_POOL);
		Registry<StructureProcessorList> processors = event.getServer().registryAccess().lookupOrThrow(Registries.PROCESSOR_LIST);

		VillagerJigsaw.init(pools, processors);
	}
}
