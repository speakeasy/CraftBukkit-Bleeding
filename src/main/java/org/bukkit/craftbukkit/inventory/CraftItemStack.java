package org.bukkit.craftbukkit.inventory;

import java.util.Map;

import net.minecraft.server.EnchantmentManager;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;

import org.apache.commons.lang.Validate;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import com.google.common.collect.ImmutableMap;

@DelegateDeserialization(ItemStack.class)
public final class CraftItemStack extends ItemStack {
    net.minecraft.server.ItemStack item;

    private CraftItemStack(net.minecraft.server.ItemStack item) {
        super(
            item != null ? item.id: 0,
            item != null ? item.count : 0,
            (short)(item != null ? item.getData() : 0)
        );
        this.item = item;
    }

    private CraftItemStack(ItemStack item) {
        this(item.getTypeId(), item.getAmount(), item.getDurability());
        addUnsafeEnchantments(item.getEnchantments());
        setItemMeta(item.getItemMeta());
    }

    /* 'Overwritten' constructors from ItemStack, yay for Java sucking */
    private CraftItemStack(final int type) {
        this(type, 1);
    }

    private CraftItemStack(final Material type) {
        this(type, 1);
    }

    private CraftItemStack(final int type, final int amount) {
        this(type, amount, (byte) 0);
    }

    private CraftItemStack(final Material type, final int amount) {
        this(type.getId(), amount);
    }

    private CraftItemStack(final int type, final int amount, final short damage) {
        this(type, amount, damage, null);
    }

    private CraftItemStack(final Material type, final int amount, final short damage) {
        this(type.getId(), amount, damage);
    }

    private CraftItemStack(final Material type, final int amount, final short damage, final Byte data) {
        this(type.getId(), amount, damage, data);
    }

    private CraftItemStack(int type, int amount, short damage, Byte data) {
        this(new net.minecraft.server.ItemStack(type, amount, data != null ? data : damage));
    }

    /*
     * Unsure if we have to sync before each of these calls the values in 'item'
     * are all public.
     */

    @Override
    public int getTypeId() {
        return item != null ? item.id : 0;
    }

    @Override
    public void setTypeId(int type) {
        if (getTypeId() == type) {
            return;
        } else if (type == 0) {
            item = null;
        } else if (item == null) {
            item = new net.minecraft.server.ItemStack(type, 1, 0);
        } else {
            item.id = type;
        }
        setData(null);
    }

    @Override
    public int getAmount() {
        return item != null ? item.count : 0;
    }

    @Override
    public void setAmount(int amount) {
        if (item == null) {
            return;
        }
        if (amount == 0) {
            item = null;
        } else {
            item.count = amount;
        }
    }

    @Override
    public void setDurability(final short durability) {
        // Ignore damage if item is null
        if (item != null) {
            item.setData(durability);
        }
    }

    @Override
    public short getDurability() {
        if (item != null) {
            return (short) item.getData();
        } else {
            return -1;
        }
    }

    @Override
    public int getMaxStackSize() { // TODO: Needed?
        return (item == null) ? 0 : item.getItem().getMaxStackSize();
    }

    @Override
    public void addUnsafeEnchantment(Enchantment ench, int level) {
        Validate.notNull(ench, "Cannot add null enchantment");

        if (!makeTag(item)) {
            return;
        }
        NBTTagList list = getEnchantmentList(item), listCopy;
        if (list == null) {
            list = new NBTTagList("ench");
            item.tag.set("ench", list);
        }
        int size = list.size();

        for (int i = 0; i < size; i++) {
            NBTTagCompound tag = (NBTTagCompound) list.get(i);
            short id = tag.getShort("id");
            if (id == ench.getId()) {
                tag.setShort("lvl", (short) level);
                return;
            }
        }
        NBTTagCompound tag = new NBTTagCompound();
        tag.setShort("id", (short) ench.getId());
        tag.setShort("lvl", (short) level);
        list.add(tag);
    }

    static boolean makeTag(net.minecraft.server.ItemStack item) {
        if (item == null) {
            return false;
        }
        if (item.tag != null) {
            return true;
        }
        item.tag = new NBTTagCompound();
        return true;
    }

    @Override
    public boolean containsEnchantment(Enchantment ench) {
        return getEnchantmentLevel(ench) > 0;
    }

    @Override
    public int getEnchantmentLevel(Enchantment ench) {
        Validate.notNull(ench, "Cannot find null enchantment");
        if (item == null) {
            return 0;
        }
        return EnchantmentManager.getEnchantmentLevel(ench.getId(), item);
    }

    @Override
    public int removeEnchantment(Enchantment ench) {
        Validate.notNull(ench, "Cannot remove null enchantment");

        NBTTagList list = getEnchantmentList(item), listCopy;
        if (list == null) {
            return 0;
        }
        int index = Integer.MIN_VALUE, size = list.size(), level;

        for (int i = 0; i < size; i++) {
            short id = ((NBTTagCompound) list.get(i)).getShort("id");
            if (id == ench.getId()) {
                index = i;
                break;
            }
        }

        if (index == Integer.MIN_VALUE) {
            return 0;
        }
        if (index == 0 && size == 0) {
            item.tag.remove("ench");
            if (item.tag.d()) {
                item.tag = null;
            }
        }

        listCopy = new NBTTagList("ench");
        level = Integer.MAX_VALUE;
        for (int i = 0; i < size; i++) {
            if (i == index) {
                level = ((NBTTagCompound) list.get(i)).getShort("id");
                continue;
            }
            listCopy.add(list.get(i));
        }
        item.tag.set("ench", listCopy);
        return level;
    }

    @Override
    public Map<Enchantment, Integer> getEnchantments() {
        return getEnchantments(item);
    }

    static Map<Enchantment, Integer> getEnchantments(net.minecraft.server.ItemStack item) {
        ImmutableMap.Builder<Enchantment, Integer> result = ImmutableMap.builder();
        NBTTagList list = (item == null) ? null : item.getEnchantments();

        if (list == null) {
            return result.build();
        }

        for (int i = 0; i < list.size(); i++) {
            short id = ((NBTTagCompound) list.get(i)).getShort("id");
            short level = ((NBTTagCompound) list.get(i)).getShort("lvl");

            result.put(Enchantment.getById(id), (int) level);
        }

        return result.build();
    }

    static NBTTagList getEnchantmentList(net.minecraft.server.ItemStack item) {
        return item == null ? null : item.getEnchantments();
    }

    @Deprecated
    private void rebuildEnchantments(Map<Enchantment, Integer> enchantments) {
        if (item == null) return;

        NBTTagCompound tag = item.tag;
        NBTTagList list = new NBTTagList("ench");

        if (tag == null) {
            tag = item.tag = new NBTTagCompound();
        }

        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            NBTTagCompound subtag = new NBTTagCompound();

            subtag.setShort("id", (short) entry.getKey().getId());
            subtag.setShort("lvl", (short) (int) entry.getValue());

            list.add(subtag);
        }

        if (enchantments.isEmpty()) {
            tag.remove("ench");
        } else {
            tag.set("ench", list);
        }
    }

    public net.minecraft.server.ItemStack getHandle() {
        return item;
    }

    @Override
    public CraftItemStack clone() {
        CraftItemStack itemStack = (CraftItemStack) super.clone();
        if (this.item != null) {
            itemStack.item = this.item.cloneItemStack();
        }
        return itemStack;
    }

    @Override
    public ItemMeta getItemMeta() {
        if (!hasItemMeta()) {
            return CraftItemFactory.instance().getItemMeta(getType());
        }
        switch (getType()) {
            case WRITTEN_BOOK:
            case BOOK_AND_QUILL:
                return new CraftBookMeta(item.tag);
            case SKULL_ITEM:
                return new CraftSkullMeta(item.tag);
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
                return new CraftLeatherArmorMeta(item.tag);
            default:
                return new CraftItemMeta(item.tag);
        }
    }

    @Override
    public boolean setItemMeta(ItemMeta itemMeta) {
        if (!CraftItemFactory.instance().isValidMeta(itemMeta, this)) {
            return false;
        }

        NBTTagCompound tag = getHandle().getTag();
        if (tag == null) {
            tag = new NBTTagCompound();
            getHandle().setTag(tag);
        }

        ((CraftItemMeta) itemMeta).applyToItem(tag);
        return true;
    }

    @Override
    public MaterialData getData() {
        return super.getData();
    }

    @Override
    public void setData(MaterialData data) {
        super.setData(data); // TODO: reset on set type
    }

    @Override
    public String toString() {
        if (item == null) {
            return "ItemStack{AIR}";
        }
        StringBuilder toString = new StringBuilder("ItemStack{").append(getType().name()).append(" x ").append(getAmount());
        if (item.tag != null && !item.tag.d()) {
            toString.append(", ").append(getItemMeta());
        }
        return toString.append('}').toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof CraftItemStack) {
            CraftItemStack that = (CraftItemStack) obj;
            if (item == that.item) {
                return true;
            }
            if (item == null || that.item == null) {
                return false;
            }
            return (!hasItemMeta()) ? (!that.hasItemMeta()) : (item.tag != null && item.tag.equals(that.item.tag));
        }
        return obj.getClass() == ItemStack.class && obj.equals(this);
    }

    @Override
    public boolean hasItemMeta() {
        return item != null && item.tag != null && item.tag.d();
    }

    public static net.minecraft.server.ItemStack asNMSCopy(ItemStack original) {
        if (original == null || original.getTypeId() <= 0) {
            return null;
        } else if (original instanceof CraftItemStack) {
            return ((CraftItemStack) original).getHandle(); // TODO, use actual copy
        }
        return new CraftItemStack(original).getHandle();
    }

    public static net.minecraft.server.ItemStack copyNMSStack(net.minecraft.server.ItemStack original, int amount) {
        throw new UnsupportedOperationException();
    }

    /**
     * Copies the NMS stack to return as a strictly-Bukkit stack
     */
    public static ItemStack asBukkitCopy(net.minecraft.server.ItemStack original) {
        ItemStack stack = new ItemStack(original.id, original.count, (short) original.getData());
        stack.addUnsafeEnchantments(getEnchantments(original));
        return stack;
    }

    public static CraftItemStack asCraftMirror(net.minecraft.server.ItemStack original) {
        throw new UnsupportedOperationException();
    }

    public static CraftItemStack asCraftCopy(ItemStack original) {
        throw new UnsupportedOperationException();
    }

    public static CraftItemStack asNewCraftStack(net.minecraft.server.Item item) {
        throw new UnsupportedOperationException();
    }

    public static CraftItemStack asNewCraftStack(net.minecraft.server.Item item, int amount) {
        throw new UnsupportedOperationException();
    }
}
