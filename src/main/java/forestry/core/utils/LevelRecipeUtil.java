package forestry.core.utils;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public final class LevelRecipeUtil {
	private LevelRecipeUtil() {
	}

	@Nullable
	public static RecipeManager getRecipeManager(Level level) {
		if (level instanceof ServerLevel serverLevel) {
			return serverLevel.recipeAccess();
		}
		RecipeAccess access = level.recipeAccess();
		return access instanceof RecipeManager manager ? manager : null;
	}
}
