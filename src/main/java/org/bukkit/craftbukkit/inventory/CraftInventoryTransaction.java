package org.bukkit.craftbukkit.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryTransaction;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.Item;
import net.minecraft.server.ItemAxe;
import net.minecraft.server.ItemHoe;
import net.minecraft.server.ItemPickaxe;
import net.minecraft.server.ItemSpade;
import net.minecraft.server.ItemSword;

public class CraftInventoryTransaction implements InventoryTransaction, InventoryTransaction.Matcher {
    private Inventory inventory;
    private Materials materialRule;
    private Data dataRule;
    private Amount amountRule;
    private Enchantments enchantRule;
    private int stackSize;
    private Matcher itemMatcher;

    CraftInventoryTransaction(Inventory inven) {
        inventory = inven;
        material(Materials.EXACT);
        data(Data.AUTO);
        amount(Amount.IGNORE);
        enchant(Enchantments.IGNORE);
        stacks(Stacks.AUTO);
        matcher(this);
    }

    public InventoryTransaction material(Materials match) {
        materialRule = match;
        return this;
    }

    public Materials material() {
        return materialRule;
    }

    public InventoryTransaction data(Data match) {
        dataRule = match;
        return this;
    }

    public Data data() {
        return dataRule;
    }

    public InventoryTransaction amount(Amount match) {
        amountRule = match;
        return this;
    }

    public Amount amount() {
        return amountRule;
    }

    public InventoryTransaction enchant(Enchantments match) {
        enchantRule = match;
        return this;
    }

    public Enchantments enchant() {
        return enchantRule;
    }

    public InventoryTransaction stacks(Stacks rule) {
        switch (rule) {
            case AUTO:
                stackSize = 0;
                break;
            case MAXIMUM:
                stackSize = 64;
                break;
            case OVERFILL:
                stackSize = Short.MAX_VALUE;
                break;
            case SPECIFIC: // Set stack size only if it looks like it hasn't already been set
                if (stackSize == 0 || stackSize == Short.MAX_VALUE) {
                    stackSize = 64;
                }
                break;
        }
        return this;
    }

    public InventoryTransaction stacks(int size) {
        stackSize = size;
        return this;
    }

    public Map.Entry<Stacks, Integer> stacks() {
        return new Map.Entry<Stacks, Integer>() {
            public Stacks getKey() {
                switch (stackSize) {
                    case 0:
                        return Stacks.AUTO;
                    case 64:
                        return Stacks.MAXIMUM;
                    case Short.MAX_VALUE:
                        return Stacks.OVERFILL;
                    default:
                        return Stacks.SPECIFIC;
                }
            }

            public Integer getValue() {
                return stackSize;
            }

            public Integer setValue(Integer value) {
                int old = getValue();
                stackSize = value == null ? 0 : value;
                return old;
            }
        };
    }

    public InventoryTransaction matcher(Matcher match) {
        itemMatcher = match;
        return this;
    }

    public Matcher matcher() {
        return itemMatcher;
    }

    private int getStackSize(ItemStack item) {
        return stackSize == 0 ? item.getMaxStackSize() : stackSize;
    }

    public List<ItemStack> add(ItemStack... items) {
        // Two-step process. First check for partial stacks that can be filled, then fill empty slots.
        List<ItemStack> leftover = new ArrayList<ItemStack>(items.length);
        for (ItemStack item : items) {
            leftover.add(item.clone());
        }
        ListIterator<ItemStack> needles = leftover.listIterator();
    NEEDLES:
        while (needles.hasNext()) {
            ItemStack needle = needles.next();
            ListIterator<ItemStack> haystack = inventory.iterator();
            while (haystack.hasNext()) {
                ItemStack item = haystack.next();
                if (item != null && item.getAmount() < getStackSize(item) && itemMatcher.match(item, needle)) {
                    int maxTransfer = getStackSize(item) - item.getAmount();
                    int transfer = Math.min(maxTransfer, needle.getAmount());
                    item.setAmount(item.getAmount() + transfer);
                    needle.setAmount(needle.getAmount() - transfer);
                    if (needle.getAmount() == 0) {
                        needles.remove();
                        continue NEEDLES;
                    }
                }
            }
            int slot = inventory.firstEmpty();
            if (slot >= 0) {
                inventory.setItem(slot, needle);
                needles.remove();
            }
        }
        return leftover;
    }

    public Map<Integer, ItemStack> remove(ItemStack... items) {
        Map<Integer, ItemStack> removed = new HashMap<Integer, ItemStack>();
        List<ItemStack> leftover = new ArrayList<ItemStack>(items.length);
        for (ItemStack item : items) {
            leftover.add(item.clone());
        }
        ListIterator<ItemStack> needles = leftover.listIterator();
        while (needles.hasNext()) {
            ItemStack needle = needles.next();
            ListIterator<ItemStack> iter = inventory.iterator();
            while (iter.hasNext()) {
                ItemStack item = iter.next();
                if (item != null && itemMatcher.match(item, needle)) {
                    ItemStack removedItem;
                    if (removed.containsKey(iter.previousIndex())) {
                        removedItem = removed.get(iter.previousIndex());
                    } else {
                        removedItem = item.clone();
                        removedItem.setAmount(0);
                    }
                    int removedAmount = Math.min(item.getAmount(), needle.getAmount());
                    removedItem.setAmount(removedItem.getAmount() + removedAmount);
                    item.setAmount(item.getAmount() - removedAmount);
                    if (item.getAmount() == 0) {
                        iter.remove();
                    }
                    needle.setAmount(needle.getAmount() - removedAmount);
                    if (needle.getAmount() == 0) {
                        needles.remove();
                        break;
                    }
                }
            }
        }
        return removed;

    }

    public Map<Integer, ItemStack> find(ItemStack... items) {
        Map<Integer, ItemStack> found = new HashMap<Integer, ItemStack>();
        for (ItemStack needle : items) {
            ListIterator<ItemStack> iter = inventory.iterator();
            while (iter.hasNext()) {
                ItemStack item = iter.next();
                if (!found.containsKey(iter.previousIndex()) && item != null && itemMatcher.match(item, needle)) {
                    found.put(iter.previousIndex(), item);
                }
            }
        }
        return found;
    }

    public boolean contains(ItemStack item) {
        item = item.clone();
        for (ItemStack check : inventory) {
            if (check != null && itemMatcher.match(check, item)) {
                item.setAmount(item.getAmount() - check.getAmount());
                if (item.getAmount() <= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public int first(ItemStack item) {
        ListIterator<ItemStack> iter = inventory.iterator();
        while (iter.hasNext()) {
            ItemStack check = iter.next();
            if (check != null && itemMatcher.match(check, item)) {
                return iter.previousIndex();
            }
        }
        return -1;
    }

    public boolean match(ItemStack inInventory, ItemStack matchingAgainst) {
        // This is a fail-fast matcher based on the various enums defined in InventoryTransaction
        // By fail-fast, I mean that the first test that fails will result in false being returned.
        // First check the material.
        if (!matchMaterial(inInventory.getTypeId(), matchingAgainst.getTypeId())) {
            return false;
        }
        // Then the data.
        if (!matchData(inInventory, matchingAgainst)) {
            return false;
        }
        // Next check the amount.
        if (!matchAmount(inInventory, matchingAgainst)) {
            return false;
        }
        // And finally match enchantments.
        if (!matchMagic(inInventory.getEnchantments(), matchingAgainst.getEnchantments())) {
            return false;
        }
        // If we got this far, we have a match.
        return true;
    }

    private boolean matchMaterial(int inInventory, int matchingAgainst) {
        switch (materialRule) {
            case EXACT:
                if (inInventory != matchingAgainst) {
                    return false;
                }
                break;
            case PARTIAL:
                if (inInventory != matchingAgainst) {
                    Item invenItem = Item.byId[inInventory];
                    Item matchItem = Item.byId[matchingAgainst];
                    if (invenItem instanceof ItemSword) {
                        return matchItem instanceof ItemSword;
                    } else if (invenItem instanceof ItemAxe) {
                        return matchItem instanceof ItemAxe;
                    } else if (invenItem instanceof ItemPickaxe) {
                        return matchItem instanceof ItemPickaxe;
                    } else if (invenItem instanceof ItemHoe) {
                        return matchItem instanceof ItemHoe;
                    } else if (invenItem instanceof ItemSpade) {
                        return matchItem instanceof ItemSpade;
                    } else {
                        return false;
                    }
                }
                break;
        }
        return true;
    }

    private boolean matchData(ItemStack inInventory, ItemStack matchingAgainst) {
        switch (dataRule) {
            case AUTO:
                Item item = Item.byId[inInventory.getTypeId()];
                return item.getMaxDurability() > 0 || item.e();
            case CHECK:
                return inInventory.getDurability() == matchingAgainst.getDurability();
            case IGNORE:
                return true;
        }
        return false;
    }

    private boolean matchAmount(ItemStack inInventory, ItemStack matchingAgainst) {
        switch (amountRule) {
            case EXACT:
                return inInventory.getAmount() == matchingAgainst.getAmount();
            case IGNORE:
                return true;
            case MINIMUM:
                return inInventory.getAmount() >= matchingAgainst.getAmount();
        }
        return false;
    }

    private boolean matchMagic(Map<Enchantment, Integer> inInventory, Map<Enchantment, Integer> matchingAgainst) {
        boolean checkLevel = true, allowLeftover = false;
        switch (enchantRule) {
            case EXACT:
                checkLevel = true;
                allowLeftover = false;
                break;
            case IGNORE:
                return true;
            case PARTIAL:
                checkLevel = true;
                allowLeftover = true;
                break;
            case TYPE_EXACT:
                checkLevel = false;
                allowLeftover = false;
                break;
            case TYPE_PARTIAL:
                checkLevel = false;
                allowLeftover = true;
                break;
        }
        // Clone it because we're going to be destroying it.
        Map<Enchantment, Integer> leftover = new HashMap<Enchantment, Integer>(inInventory);
        for (Enchantment mustHave : matchingAgainst.keySet()) {
            if (!leftover.containsKey(mustHave)) {
                return false;
            }
            if (checkLevel && leftover.get(mustHave) != matchingAgainst.get(mustHave)) {
                return false;
            }
            leftover.remove(mustHave);
        }
        // If we got this far, all requested enchantments are present, at the requested level if desired
        return allowLeftover ? true : leftover.isEmpty();
    }
}
