package forestry.core.utils;

import com.google.common.base.Preconditions;

import forestry.api.ForestryConstants;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import forestry.modules.ModuleUtil;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class ModUtil {
	public static boolean isModLoaded(String modname) {
		return ModList.isLoaded(modname);
	}

	public static boolean isModLoaded(String modname, @Nullable String versionRangeString) {
		if (!isModLoaded(modname)) {
			return false;
		}

		if (versionRangeString != null) {
			Optional<? extends ModContainer> cont = ModList.getModContainerById(modname);
			if (cont.isPresent()) {
				ModContainer modContainer = cont.get();

				ArtifactVersion modVersion = modContainer.getModInfo().getVersion();

				VersionRange range = VersionRange.createFromVersion(versionRangeString);
				DefaultArtifactVersion requiredVersion = new DefaultArtifactVersion(versionRangeString);    //TODO - check

				return requiredVersion.compareTo(modVersion) > 0; //TODO - this comparison is incorrect
			}
		}

		return true;
	}

	public static Identifier getRegistryName(Fluid o) {
		return ForgeRegistries.FLUIDS.getKey(o);
	}

	public static Identifier getRegistryName(Block o) {
		return ForgeRegistries.BLOCKS.getKey(o);
	}

	public static Identifier getRegistryName(Item o) {
		return ForgeRegistries.ITEMS.getKey(o);
	}

	public static Identifier getRegistryName(ParticleType<?> o) {
		return ForgeRegistries.PARTICLE_TYPES.getKey(o);
	}

	// todo use in more parts of the mod
	public static void checkNotEmpty(@Nullable Item item) {
		Preconditions.checkArgument(item != null && item != Items.AIR);
	}

	// todo use in more parts of the mod
	public static void checkNotEmpty(@Nullable Block block) {
		Preconditions.checkArgument(block != null && block != Blocks.AIR);
	}

	public static Identifier withSuffix(Identifier id, String suffix) {
		return Identifier.fromNamespaceAndPath(id.getNamespace(), id.getPath() + suffix);
	}

	// Run code after registry registration completes (not applicable for data-driven registries)
	public static void addRegistryListener(ResourceKey<? extends Registry<?>> type, Runnable listener) {
		RegisterEvent.getBus(ModuleUtil.getModBusGroup(ForestryConstants.MOD_ID)).addListener(event -> {
			if (event.getRegistryKey().equals(type)) {
				listener.run();
			}
		});
	}
}
