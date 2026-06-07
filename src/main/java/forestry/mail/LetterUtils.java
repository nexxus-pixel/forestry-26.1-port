package forestry.mail;

import forestry.core.utils.ItemStackUtil;

import forestry.api.mail.ILetter;
import forestry.api.mail.IMailAddress;
import forestry.mail.features.MailItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class LetterUtils {
	public static ILetter createLetter(IMailAddress sender, IMailAddress recipient) {
		return new Letter(sender, recipient);
	}

	public static ItemStack createLetterStack(ILetter letter) {
		CompoundTag compoundNBT = new CompoundTag();
		letter.write(compoundNBT);

		ItemStack letterStack = LetterProperties.createStampedLetterStack(letter);
		ItemStackUtil.setTag(letterStack, compoundNBT);

		return letterStack;
	}

	@Nullable
	public static ILetter getLetter(ItemStack itemstack) {
		if (itemstack.isEmpty()) {
			return null;
		}

		if (!LetterUtils.isLetter(itemstack)) {
			return null;
		}

		if (ItemStackUtil.getTag(itemstack) == null) {
			return null;
		}

		return new Letter(ItemStackUtil.getTag(itemstack));
	}

	public static boolean isLetter(ItemStack itemstack) {
		return MailItems.LETTERS.itemEqual(itemstack);
	}
}
