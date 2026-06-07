package forestry.core.utils;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.ItemInstance;

public final class EnchantmentUtil {
	private EnchantmentUtil() {
	}

	public static Holder<Enchantment> holder(HolderLookup.Provider registries, ResourceKey<Enchantment> key) {
		return registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key);
	}

	public static int getLevel(HolderLookup.Provider registries, ResourceKey<Enchantment> key, ItemInstance item) {
		return EnchantmentHelper.getItemEnchantmentLevel(holder(registries, key), item);
	}
}
