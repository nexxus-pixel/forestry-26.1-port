package forestry.core.features;

import forestry.api.ForestryConstants;
import forestry.api.modules.ForestryModuleIds;
import net.minecraft.resources.Identifier;

/**
 * Forestry painting variants are registered via datapack JSON in {@code data/forestry/painting_variant/}.
 */
public final class CorePaintings {
	public static final Identifier MOUSETREE = id("mousetree");
	public static final Identifier WASPHOL = id("wasphol");
	public static final Identifier CAGE = id("cage");
	public static final Identifier LEWIS = id("lewis");
	public static final Identifier SITEBEE = id("site_bee");
	public static final Identifier ALEXBLOOME = id("alex_bloome");
	public static final Identifier SUSPICIOUS_LOOKING_TREE = id("suspicious_looking_tree");
	public static final Identifier WISDOM = id("wisdom");
	public static final Identifier MYSTICAL_TREE = id("mystical_tree");
	public static final Identifier DEKU = id("deku");

	private CorePaintings() {
	}

	private static Identifier id(String name) {
		return ForestryConstants.forestry(name);
	}
}
