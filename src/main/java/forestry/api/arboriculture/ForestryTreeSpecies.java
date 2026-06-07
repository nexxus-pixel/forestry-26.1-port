package forestry.api.arboriculture;

import net.minecraft.resources.Identifier;

import static forestry.api.ForestryConstants.forestry;

/**
 * IDs of all tree species registered by base Forestry.
 * The "tree_" prefix avoids ID conflicts because all types of species share the same registry (alleles).
 */
public class ForestryTreeSpecies {
	public static final Identifier OAK = forestry("tree_oak");
	public static final Identifier DARK_OAK = forestry("tree_dark_oak");
	public static final Identifier BIRCH = forestry("tree_birch");
	/**
	 * The species for the Acacia trees added by Vanilla Minecraft.
	 */
	public static final Identifier ACACIA_VANILLA = forestry("tree_acacia");
	public static final Identifier SPRUCE = forestry("tree_spruce");
	public static final Identifier JUNGLE = forestry("tree_jungle");
	/**
	 * The species for the Cherry Blossom trees added by Vanilla Minecraft.
	 */
	public static final Identifier CHERRY_VANILLA = forestry("tree_cherry");
	public static final Identifier LIME = forestry("tree_lime");
	public static final Identifier WALNUT = forestry("tree_walnut");
	public static final Identifier CHESTNUT = forestry("tree_chestnut");
	public static final Identifier SOUR_CHERRY = forestry("tree_hill_cherry"); //TODO: Rename in 1.21
	public static final Identifier LEMON = forestry("tree_lemon");
	public static final Identifier PLUM = forestry("tree_plum");
	public static final Identifier MAPLE = forestry("tree_maple");
	public static final Identifier LARCH = forestry("tree_larch");
	public static final Identifier PINE = forestry("tree_pine");
	public static final Identifier SEQUOIA = forestry("tree_sequoia");
	public static final Identifier GIANT_SEQUOIA = forestry("tree_giant_sequoia");
	public static final Identifier TEAK = forestry("tree_teak");
	public static final Identifier IPE = forestry("tree_ipe");
	public static final Identifier KAPOK = forestry("tree_kapok");
	public static final Identifier EBONY = forestry("tree_ebony");
	public static final Identifier ZEBRANO = forestry("tree_zebrawood"); //TODO: Rename in 1.21
	public static final Identifier MAHOGANY = forestry("tree_mahogany");
	public static final Identifier CAMELTHORN = forestry("tree_desert_acacia"); //TODO: Rename in 1.21
	public static final Identifier PADAUK = forestry("tree_padauk");
	public static final Identifier BALSA = forestry("tree_balsa");
	public static final Identifier COCOBOLO = forestry("tree_cocobolo");
	public static final Identifier WENGE = forestry("tree_wenge");
	public static final Identifier BAOBAB = forestry("tree_baobab");
	public static final Identifier MAHOE = forestry("tree_mahoe");
	public static final Identifier WILLOW = forestry("tree_willow");
	public static final Identifier GREENHEART = forestry("tree_sipiri"); //TODO: Rename in 1.21
	public static final Identifier PAPAYA = forestry("tree_papaya");
	public static final Identifier DATE = forestry("tree_date");
	public static final Identifier POPLAR = forestry("tree_poplar");
	public static final Identifier ELM = forestry("tree_elm");
	public static final Identifier FIR = forestry("tree_fir");
	public static final Identifier COCONUT = forestry("tree_coconut");
	public static final Identifier BEECH = forestry("tree_beech");
	public static final Identifier FEIJOA = forestry("tree_feijoa");
	public static final Identifier DOGWOOD = forestry("tree_dogwood");
	public static final Identifier GINKGO = forestry("tree_ginkgo");
	public static final Identifier JACARANDA = forestry("tree_jacaranda");
	public static final Identifier PEWEN = forestry("tree_pewen");
	public static final Identifier MACROCARPA = forestry("tree_macrocarpa");
	public static final Identifier OLIVE = forestry("tree_olive");
	public static final Identifier ORANGE = forestry("tree_orange");
	public static final Identifier PEAR = forestry("tree_pear");
	public static final Identifier KAURI = forestry("tree_kauri");
}
