package forestry.core.inventory;

import forestry.core.utils.CompoundTagUtil;

import forestry.core.utils.ItemStackUtil;

import com.google.common.base.Preconditions;
import forestry.core.tiles.IFilterSlotDelegate;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;
import java.util.Random;

public abstract class ItemInventory implements Container, IFilterSlotDelegate, ICapabilityProvider {
	private static final String KEY_SLOTS = "Slots";
	private static final String KEY_UID = "UID";
	private static final Random rand = new Random();

	private final IItemHandler itemHandler = new InvWrapper(this);

	protected final Player player;
	private ItemStack parent;    //TODO not final any more. Is this a problem
	private final NonNullList<ItemStack> inventoryStacks;

	public ItemInventory(Player player, int size, ItemStack parent) {
		Preconditions.checkArgument(!parent.isEmpty(), "Parent cannot be empty.");

		this.player = player;
		this.parent = parent;
		this.inventoryStacks = NonNullList.withSize(size, ItemStack.EMPTY);

		CompoundTag nbt = ItemStackUtil.getTag(parent);
		if (nbt == null) {
			nbt = new CompoundTag();
			ItemStackUtil.setTag(parent, nbt);
		}
		setUID(nbt); // Set a uid to identify the itemStack on SMP

		CompoundTag nbtSlots = CompoundTagUtil.getCompound(nbt, KEY_SLOTS);
		for (int i = 0; i < this.inventoryStacks.size(); i++) {
			String slotKey = getSlotNBTKey(i);
			if (nbtSlots.contains(slotKey)) {
				CompoundTag itemNbt = CompoundTagUtil.getCompound(nbtSlots, slotKey);
				ItemStack itemStack = ItemStackUtil.loadFromTag(itemNbt);
                this.inventoryStacks.set(i, itemStack);
			} else {
                this.inventoryStacks.set(i, ItemStack.EMPTY);
			}
		}
	}

	public static int getOccupiedSlotCount(ItemStack itemStack) {
		CompoundTag nbt = ItemStackUtil.getTag(itemStack);
		if (nbt == null) {
			return 0;
		}

		CompoundTag slotNbt = CompoundTagUtil.getCompound(nbt, KEY_SLOTS);
		return slotNbt.size();
	}

	private void setUID(CompoundTag nbt) {
		if (!nbt.contains(KEY_UID)) {
			nbt.putInt(KEY_UID, rand.nextInt());
		}
	}

	public boolean isParentItemInventory(ItemStack itemStack) {
		ItemStack parent = getParent();
		return isSameItemInventory(parent, itemStack);
	}

	protected ItemStack getParent() {
		for (InteractionHand hand : InteractionHand.values()) {
			ItemStack held = this.player.getItemInHand(hand);
			if (isSameItemInventory(held, this.parent)) {
				return held;
			}
		}
		return this.parent;
	}

	protected void setParent(ItemStack parent) {
		this.parent = parent;
	}

	@Nullable
	protected InteractionHand getHand() {
		for (InteractionHand hand : InteractionHand.values()) {
			ItemStack held = this.player.getItemInHand(hand);
			if (isSameItemInventory(held, this.parent)) {
				return hand;
			}
		}
		return null;
	}

	private static boolean isSameItemInventory(ItemStack base, ItemStack comparison) {
		if (base.isEmpty() || comparison.isEmpty()) {
			return false;
		}

		if (base.getItem() != comparison.getItem()) {
			return false;
		}

		CompoundTag baseTagCompound = ItemStackUtil.getTag(base);
		CompoundTag comparisonTagCompound = ItemStackUtil.getTag(comparison);
		if (baseTagCompound == null || comparisonTagCompound == null) {
			return false;
		}

		if (!baseTagCompound.contains(KEY_UID) || !comparisonTagCompound.contains(KEY_UID)) {
			return false;
		}

		int baseUID = CompoundTagUtil.getInt(baseTagCompound, KEY_UID);
		int comparisonUID = CompoundTagUtil.getInt(comparisonTagCompound, KEY_UID);
		return baseUID == comparisonUID;
	}

	private void writeToParentNBT() {
		ItemStack parent = getParent();

		CompoundTag nbt = ItemStackUtil.getTag(parent);
		if (nbt == null) {
			nbt = new CompoundTag();
			ItemStackUtil.setTag(parent, nbt);
		}

		CompoundTag slotsNbt = new CompoundTag();
		for (int i = 0; i < getContainerSize(); i++) {
			ItemStack itemStack = getItem(i);
			if (!itemStack.isEmpty()) {
				String slotKey = getSlotNBTKey(i);
				CompoundTag itemNbt = new CompoundTag();
				ItemStackUtil.saveToTag(itemStack, itemNbt);
				slotsNbt.put(slotKey, itemNbt);
			}
		}

		nbt.put(KEY_SLOTS, slotsNbt);
		onWriteNBT(nbt);
	}

	private static String getSlotNBTKey(int i) {
		return Integer.toString(i, Character.MAX_RADIX);
	}

	protected void onWriteNBT(CompoundTag nbt) {
	}

	public void onSlotClick(int slotIndex, Player player) {
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : this.inventoryStacks) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}

		return true;
	}


	@Override
	public ItemStack removeItem(int index, int count) {
		ItemStack itemstack = ContainerHelper.removeItem(this.inventoryStacks, index, count);

		if (!itemstack.isEmpty()) {
			this.setChanged();
		}

		return itemstack;
	}

	@Override
	public void setItem(int index, ItemStack itemstack) {
        this.inventoryStacks.set(index, itemstack);

		ItemStack parent = getParent();

		CompoundTag nbt = ItemStackUtil.getTag(parent);
		if (nbt == null) {
			nbt = new CompoundTag();
			ItemStackUtil.setTag(parent, nbt);
		}

		CompoundTag slotNbt;
		if (!nbt.contains(KEY_SLOTS)) {
			slotNbt = new CompoundTag();
			nbt.put(KEY_SLOTS, slotNbt);
		} else {
			slotNbt = CompoundTagUtil.getCompound(nbt, KEY_SLOTS);
		}

		String slotKey = getSlotNBTKey(index);

		if (itemstack.isEmpty()) {
			slotNbt.remove(slotKey);
		} else {
			CompoundTag itemNbt = new CompoundTag();
			ItemStackUtil.saveToTag(itemstack, itemNbt);

			slotNbt.put(slotKey, itemNbt);
		}
	}

	@Override
	public ItemStack getItem(int i) {
		return this.inventoryStacks.get(i);
	}

	@Override
	public int getContainerSize() {
		return this.inventoryStacks.size();
	}

	@Override
	public int getMaxStackSize() {
		return 64;
	}

	@Override
	public final void setChanged() {
		writeToParentNBT();
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public boolean canPlaceItem(int slotIndex, ItemStack itemStack) {
		return canSlotAccept(slotIndex, itemStack);
	}

	@Override
	public void startOpen(ContainerUser user) {
	}

	@Override
	public void stopOpen(ContainerUser user) {
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		ItemStack toReturn = getItem(slot);

		if (!toReturn.isEmpty()) {
			setItem(slot, ItemStack.EMPTY);
		}

		return toReturn;
	}

	/* IFilterSlotDelegate */
	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack stack) {
		return true;
	}

	@Override
	public boolean isLocked(int slotIndex) {
		return false;
	}

	/* Fields */

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		if (capability == ForgeCapabilities.ITEM_HANDLER) {
			return LazyOptional.of(() -> this.itemHandler).cast();
		}
		return LazyOptional.empty();
	}

	public IItemHandler getItemHandler() {
		return this.itemHandler;
	}

	@Override
	public void clearContent() {
		this.inventoryStacks.clear();
	}
}
