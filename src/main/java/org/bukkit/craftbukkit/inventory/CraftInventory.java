package org.bukkit.craftbukkit.inventory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import net.minecraft.server.ContainerAnvilInventory;
import net.minecraft.server.ContainerEnchantTableInventory;
import net.minecraft.server.IHopper;
import net.minecraft.server.IInventory;
import net.minecraft.server.InventoryCrafting;
import net.minecraft.server.InventoryEnderChest;
import net.minecraft.server.InventoryMerchant;
import net.minecraft.server.PlayerInventory;
import net.minecraft.server.TileEntityBeacon;
import net.minecraft.server.TileEntityBrewingStand;
import net.minecraft.server.TileEntityDispenser;
import net.minecraft.server.TileEntityDropper;
import net.minecraft.server.TileEntityFurnace;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

public class CraftInventory implements Inventory {
    protected final IInventory primaryInventory;
    private final IInventory[] inventories;
    private final int size;

    public CraftInventory(int primary, IInventory... inventories) {
        this.inventories = inventories;
        primaryInventory = inventories[primary];
        int size = 0;

        for (IInventory inventory : getInventories()) {
            size += inventory.getSize();
        }
        this.size = size;
    }

    public CraftInventory(IInventory... inventories) {
        this(0, inventories);
    }

    public IInventory[] getInventories() {
        return inventories;
    }

    public IInventory getPrimaryInventory() {
        return primaryInventory;
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return getPrimaryInventory().getName();
    }

    public ItemStack getItem(int index) {
        Validate.isTrue(index >= 0, "Slot must be greater than or equal to 0.");
        Validate.isTrue(index < getSize(), "Slot must be less than the size of this inventory(" + getSize() + ").");

        for (IInventory inventory : getInventories()) {
            if (index >= inventory.getSize()) {
                index -= inventory.getSize();
            } else {
                net.minecraft.server.ItemStack item = inventory.getItem(index);
                return item == null ? null : CraftItemStack.asCraftMirror(inventory.getItem(index));
            }
        }
        throw new IllegalStateException("Unable to retrieve an ItemStack for slot " + index);
    }

    public ItemStack[] getContents() {
        ItemStack[] contents = new ItemStack[getSize()];
        int index = 0;

        for (IInventory inventory : getInventories()) {
            for (net.minecraft.server.ItemStack item : inventory.getContents()) {
                contents[index++] = item == null ? null : CraftItemStack.asCraftMirror(item);
            }
        }

        return contents;
    }

    public void setContents(ItemStack[] items) {
        if (items == null) {
            items = new ItemStack[0];
        }
        Validate.isTrue(items.length <= getSize(), "Invalid inventory size; expected " + getSize() + " or less.");

        int inventoryOffset = 0;
        for (IInventory inventory : getInventories()) {
            for (int index = 0; index < inventory.getSize(); index++) {
                int arrayIndex = inventoryOffset + index;
                ItemStack arrayItem = arrayIndex >= items.length ? null : items[arrayIndex];
                net.minecraft.server.ItemStack item = ((arrayItem == null || arrayItem.getTypeId() == 0) ? null : CraftItemStack.asNMSCopy(arrayItem));

                inventory.getContents()[index] = item;
            }
            inventoryOffset += inventory.getSize();
        }
    }

    public void setItem(int index, ItemStack item) {
        Validate.isTrue(index >= 0, "Slot must be greater than or equal to 0.");
        Validate.isTrue(index < getSize(), "Slot must be less than the size of this inventory(" + getSize() + ").");

        for (IInventory inventory : getInventories()) {
            if (index >= inventory.getSize()) {
                index -= inventory.getSize();
            } else {
                inventory.setItem(index, ((item == null || item.getTypeId() == 0) ? null : CraftItemStack.asNMSCopy(item)));
                return;
            }
        }
        throw new IllegalStateException("Unable to set an ItemStack at slot " + index);
    }

    public boolean contains(int materialId) {
        for (ItemStack item : getContents()) {
            if (item != null && item.getTypeId() == materialId) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(Material material) {
        Validate.notNull(material, "Material cannot be null");
        return contains(material.getId());
    }

    public boolean contains(ItemStack item) {
        if (item == null) {
            return false;
        }
        for (ItemStack i : getContents()) {
            if (item.equals(i)) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(int materialId, int amount) {
        if (amount <= 0) {
            return true;
        }
        for (ItemStack item : getContents()) {
            if (item != null && item.getTypeId() == materialId) {
                if ((amount -= item.getAmount()) <= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean contains(Material material, int amount) {
        Validate.notNull(material, "Material cannot be null");
        return contains(material.getId(), amount);
    }

    public boolean contains(ItemStack item, int amount) {
        if (item == null) {
            return false;
        }
        if (amount <= 0) {
            return true;
        }
        for (ItemStack i : getContents()) {
            if (item.equals(i) && --amount <= 0) {
                return true;
            }
        }
        return false;
    }

    public boolean containsAtLeast(ItemStack item, int amount) {
        if (item == null) {
            return false;
        }
        if (amount <= 0) {
            return true;
        }
        for (ItemStack i : getContents()) {
            if (item.isSimilar(i) && (amount -= i.getAmount()) <= 0) {
                return true;
            }
        }
        return false;
    }

    public HashMap<Integer, ItemStack> all(int materialId) {
        HashMap<Integer, ItemStack> slots = new HashMap<Integer, ItemStack>();

        ItemStack[] inventory = getContents();
        for (int i = 0; i < inventory.length; i++) {
            ItemStack item = inventory[i];
            if (item != null && item.getTypeId() == materialId) {
                slots.put(i, item);
            }
        }
        return slots;
    }

    public HashMap<Integer, ItemStack> all(Material material) {
        Validate.notNull(material, "Material cannot be null");
        return all(material.getId());
    }

    public HashMap<Integer, ItemStack> all(ItemStack item) {
        HashMap<Integer, ItemStack> slots = new HashMap<Integer, ItemStack>();
        if (item != null) {
            ItemStack[] inventory = getContents();
            for (int i = 0; i < inventory.length; i++) {
                if (item.equals(inventory[i])) {
                    slots.put(i, inventory[i]);
                }
            }
        }
        return slots;
    }

    public int first(int materialId) {
        ItemStack[] inventory = getContents();
        for (int i = 0; i < inventory.length; i++) {
            ItemStack item = inventory[i];
            if (item != null && item.getTypeId() == materialId) {
                return i;
            }
        }
        return -1;
    }

    public int first(Material material) {
        Validate.notNull(material, "Material cannot be null");
        return first(material.getId());
    }

    public int first(ItemStack item) {
        return first(item, true);
    }

    private int first(ItemStack item, boolean withAmount) {
        if (item == null) {
            return -1;
        }
        ItemStack[] inventory = getContents();
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == null) continue;

            if (withAmount ? item.equals(inventory[i]) : item.isSimilar(inventory[i])) {
                return i;
            }
        }
        return -1;
    }

    public int firstEmpty() {
        ItemStack[] inventory = getContents();
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public int firstPartial(int materialId) {
        ItemStack[] inventory = getContents();
        for (int i = 0; i < inventory.length; i++) {
            ItemStack item = inventory[i];
            if (item != null && item.getTypeId() == materialId && item.getAmount() < item.getMaxStackSize()) {
                return i;
            }
        }
        return -1;
    }

    public int firstPartial(Material material) {
        Validate.notNull(material, "Material cannot be null");
        return firstPartial(material.getId());
    }

    private int firstPartial(ItemStack item) {
        ItemStack[] inventory = getContents();
        ItemStack filteredItem = CraftItemStack.asCraftCopy(item);
        if (item == null) {
            return -1;
        }
        for (int i = 0; i < inventory.length; i++) {
            ItemStack cItem = inventory[i];
            if (cItem != null && cItem.getAmount() < cItem.getMaxStackSize() && cItem.isSimilar(filteredItem)) {
                return i;
            }
        }
        return -1;
    }

    public HashMap<Integer, ItemStack> addItem(ItemStack... items) {
        Validate.noNullElements(items, "Item cannot be null");
        HashMap<Integer, ItemStack> leftover = new HashMap<Integer, ItemStack>();

        /* TODO: some optimization
         *  - Create a 'firstPartial' with a 'fromIndex'
         *  - Record the lastPartial per Material
         *  - Cache firstEmpty result
         */

        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            while (true) {
                // Do we already have a stack of it?
                int firstPartial = firstPartial(item);

                // Drat! no partial stack
                if (firstPartial == -1) {
                    // Find a free spot!
                    int firstFree = firstEmpty();

                    if (firstFree == -1) {
                        // No space at all!
                        leftover.put(i, item);
                        break;
                    } else {
                        // More than a single stack!
                        if (item.getAmount() > getMaxItemStack()) {
                            CraftItemStack stack = CraftItemStack.asCraftCopy(item);
                            stack.setAmount(getMaxItemStack());
                            setItem(firstFree, stack);
                            item.setAmount(item.getAmount() - getMaxItemStack());
                        } else {
                            // Just store it
                            setItem(firstFree, item);
                            break;
                        }
                    }
                } else {
                    // So, apparently it might only partially fit, well lets do just that
                    ItemStack partialItem = getItem(firstPartial);

                    int amount = item.getAmount();
                    int partialAmount = partialItem.getAmount();
                    int maxAmount = partialItem.getMaxStackSize();

                    // Check if it fully fits
                    if (amount + partialAmount <= maxAmount) {
                        partialItem.setAmount(amount + partialAmount);
                        break;
                    }

                    // It fits partially
                    partialItem.setAmount(maxAmount);
                    item.setAmount(amount + partialAmount - maxAmount);
                }
            }
        }
        return leftover;
    }

    public HashMap<Integer, ItemStack> removeItem(ItemStack... items) {
        Validate.notNull(items, "Items cannot be null");
        HashMap<Integer, ItemStack> leftover = new HashMap<Integer, ItemStack>();

        // TODO: optimization

        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            int toDelete = item.getAmount();

            while (true) {
                int first = first(item, false);

                // Drat! we don't have this type in the inventory
                if (first == -1) {
                    item.setAmount(toDelete);
                    leftover.put(i, item);
                    break;
                } else {
                    ItemStack itemStack = getItem(first);
                    int amount = itemStack.getAmount();

                    if (amount <= toDelete) {
                        toDelete -= amount;
                        // clear the slot, all used up
                        clear(first);
                    } else {
                        // split the stack and store
                        itemStack.setAmount(amount - toDelete);
                        setItem(first, itemStack);
                        toDelete = 0;
                    }
                }

                // Bail when done
                if (toDelete <= 0) {
                    break;
                }
            }
        }
        return leftover;
    }

    private int getMaxItemStack() {
        return getPrimaryInventory().getMaxStackSize();
    }

    public void remove(int materialId) {
        ItemStack[] items = getContents();
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && items[i].getTypeId() == materialId) {
                clear(i);
            }
        }
    }

    public void remove(Material material) {
        Validate.notNull(material, "Material cannot be null");
        remove(material.getId());
    }

    public void remove(ItemStack item) {
        ItemStack[] items = getContents();
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && items[i].equals(item)) {
                clear(i);
            }
        }
    }

    public void clear(int index) {
        setItem(index, null);
    }

    public void clear() {
        for (int i = 0; i < getSize(); i++) {
            clear(i);
        }
    }

    public ListIterator<ItemStack> iterator() {
        return new InventoryIterator(this);
    }

    public ListIterator<ItemStack> iterator(int index) {
        if (index < 0) {
            index += getSize() + 1; // ie, with -1, previous() will return the last element
        }
        return new InventoryIterator(this, index);
    }

    public List<HumanEntity> getViewers() {
        return this.primaryInventory.getViewers();
    }

    public String getTitle() {
        return primaryInventory.getName();
    }

    public InventoryType getType() {
        // Thanks to Droppers extending Dispensers, order is important.
        if (primaryInventory instanceof InventoryCrafting) {
            return primaryInventory.getSize() >= 9 ? InventoryType.WORKBENCH : InventoryType.CRAFTING;
        } else if (primaryInventory instanceof PlayerInventory) {
            return InventoryType.PLAYER;
        } else if (primaryInventory instanceof TileEntityDropper) {
            return InventoryType.DROPPER;
        } else if (primaryInventory instanceof TileEntityDispenser) {
            return InventoryType.DISPENSER;
        } else if (primaryInventory instanceof TileEntityFurnace) {
            return InventoryType.FURNACE;
        } else if (primaryInventory instanceof ContainerEnchantTableInventory) {
            return InventoryType.ENCHANTING;
        } else if (primaryInventory instanceof TileEntityBrewingStand) {
            return InventoryType.BREWING;
        } else if (primaryInventory instanceof CraftInventoryCustom.MinecraftInventory) {
            return ((CraftInventoryCustom.MinecraftInventory) primaryInventory).getType();
        } else if (primaryInventory instanceof InventoryEnderChest) {
            return InventoryType.ENDER_CHEST;
        } else if (primaryInventory instanceof InventoryMerchant) {
            return InventoryType.MERCHANT;
        } else if (primaryInventory instanceof TileEntityBeacon) {
            return InventoryType.BEACON;
        } else if (primaryInventory instanceof ContainerAnvilInventory) {
            return InventoryType.ANVIL;
        } else if (primaryInventory instanceof IHopper) {
            return InventoryType.HOPPER;
        } else {
            return InventoryType.CHEST;
        }
    }

    public InventoryHolder getHolder() {
        return primaryInventory.getOwner();
    }

    public int getMaxStackSize() {
        return primaryInventory.getMaxStackSize();
    }

    public void setMaxStackSize(int size) {
        primaryInventory.setMaxStackSize(size);
    }

    @Override
    public int hashCode() {
        return getPrimaryInventory().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CraftInventory)) {
            return false;
        }
        CraftInventory other = (CraftInventory) obj;

        return Arrays.equals(this.inventories, other.inventories);
    }
}
