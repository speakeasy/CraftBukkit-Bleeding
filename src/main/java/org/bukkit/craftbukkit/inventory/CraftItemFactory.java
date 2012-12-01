package org.bukkit.craftbukkit.inventory;

import java.util.Map;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class CraftItemFactory implements ItemFactory {
    @SerializableAs("ItemMeta")
    static class SerializableMeta implements ConfigurationSerializable {
        static final String TYPE_FIELD = "meta-type";

        enum Deserializers {
            BOOK {
                @Override
                CraftBookMeta deserialize(Map<String, Object> map) {
                    return new CraftBookMeta(map);
                }
            },
            SKULL {
                @Override
                CraftSkullMeta deserialize(Map<String, Object> map) {
                    return new CraftSkullMeta(map);
                }
            },
            LEATHER_ARMOR {
                @Override
                CraftLeatherArmorMeta deserialize(Map<String, Object> map) {
                    return new CraftLeatherArmorMeta(map);
                }
            },
            MAP {
                @Override
                ItemMeta deserialize(Map<String, Object> map) {
                    // TODO
                    throw new UnsupportedOperationException(this.name());
                }
            },
            POTION {
                @Override
                ItemMeta deserialize(Map<String, Object> map) {
                    return new CraftPotionMeta(map);
                }
            },
            UNSPECIFIC {
                @Override
                CraftItemMeta deserialize(Map<String, Object> map) {
                    return new CraftItemMeta(map);
                }
            };

            abstract ItemMeta deserialize(Map<String, Object> map);
        }

        private SerializableMeta() {
        }

        public static ItemMeta deserialize(Map<String, Object> map) {
            Validate.notNull(map, "Cannot deserialize null map");

            String type = getString(map, TYPE_FIELD, false);
            Deserializers deserializer = Deserializers.valueOf(type);

            if (deserializer == null) {
                throw new IllegalArgumentException(type + " is not a valid " + TYPE_FIELD);
            }

            return deserializer.deserialize(map);
        }

        public Map<String, Object> serialize() {
            throw new AssertionError();
        }

        static String getString(Map<String, Object> map, String field, boolean nullable) {
            return getObject(String.class, map, field, nullable);
        }

        static <T> T getObject(Class<T> clazz, Map<String, Object> map, String field, boolean nullable) {
            final Object object = map.get(field);

            if (clazz.isInstance(object)) {
                return clazz.cast(object);
            }
            if (object == null) {
                if (!nullable) {
                    throw new IllegalArgumentException(field + " cannot be null");
                }
                return null;
            }
            throw new IllegalArgumentException(field + '(' + object + ") is not a valid " + clazz);
        }
    }
    private static final CraftItemFactory instance;

    static {
        instance = new CraftItemFactory();
        ConfigurationSerialization.registerClass(SerializableMeta.class);
    }

    private CraftItemFactory() {
    }

    public boolean isApplicable(ItemMeta meta, ItemStack itemstack) {
        if (itemstack == null) {
            return false;
        }
        return isApplicable(meta, itemstack.getType());
    }

    public boolean isApplicable(ItemMeta meta, Material type) {
        if (type == null || meta == null) {
            return false;
        }
        if (!(meta instanceof CraftItemMeta)) {
            throw new IllegalArgumentException("Meta of " + meta.getClass().toString() + " not created by " + CraftItemFactory.class.getName());
        }

        return ((CraftItemMeta) meta).applicableTo(type);
    }

    public ItemMeta getItemMeta(Material material) {
        Validate.notNull(material, "Material cannot be null");
        return getItemMeta(material, null);
    }

    private ItemMeta getItemMeta(Material material, CraftItemMeta meta) {
        switch (material) {
        case AIR:
            return null;
        case WRITTEN_BOOK:
        case BOOK_AND_QUILL:
            return meta instanceof CraftBookMeta ? meta : new CraftBookMeta(meta);
        case SKULL_ITEM:
            return meta instanceof CraftSkullMeta ? meta : new CraftSkullMeta(meta);
        case LEATHER_HELMET:
        case LEATHER_CHESTPLATE:
        case LEATHER_LEGGINGS:
        case LEATHER_BOOTS:
            return meta instanceof CraftLeatherArmorMeta ? meta : new CraftLeatherArmorMeta(meta);
        case POTION:
            return meta instanceof CraftPotionMeta ? meta : new CraftPotionMeta(meta);
        default:
            return new CraftItemMeta(meta);
        }
    }

    public boolean equals(ItemMeta meta1, ItemMeta meta2) {
        if (meta1 == meta2) {
            return true;
        }
        if (meta1 != null && !(meta1 instanceof CraftItemMeta)) {
            throw new IllegalArgumentException("First meta of " + meta1.getClass().getName() + " does not belong to " + CraftItemFactory.class.getName());
        }
        if (meta2 != null && !(meta2 instanceof CraftItemMeta)) {
            throw new IllegalArgumentException("Second meta " + meta2.getClass().getName() + " does not belong to " + CraftItemFactory.class.getName());
        }
        if (meta1 == null) {
            return ((CraftItemMeta) meta2).isEmpty();
        }
        if (meta2 == null) {
            return ((CraftItemMeta) meta1).isEmpty();
        }

        return equals((CraftItemMeta) meta1, (CraftItemMeta) meta2);
    }

    boolean equals(CraftItemMeta meta1, CraftItemMeta meta2) {
        /*
         * This couldn't be done inside of the objects themselves, else force recursion.
         * This is a fairly clean way of implementing it, by dividing the methods into purposes and letting each method perform its own function.
         *
         * The common and uncommon were split, as both could have variables not applicable to the other, like a skull and book.
         * Each object needs its chance to say "hey wait a minute, we're not equal," but without the redundancy of using the 1.equals(2) && 2.equals(1) checking the 'commons' twice.
         *
         * Doing it this way fills all conditions of the .equals() method.
         */
        return meta1.equalsCommon(meta2) && meta1.notUncommon(meta2) && meta2.notUncommon(meta1);
    }

    public static CraftItemFactory instance() {
        return instance;
    }

    public ItemMeta asMetaFor(ItemMeta meta, ItemStack stack) {
        Validate.notNull(stack, "Stack cannot be null");
        return asMetaFor(meta, stack.getType());
    }

    public ItemMeta asMetaFor(ItemMeta meta, Material material) {
        Validate.notNull(material, "Material cannot be null");
        if (!(meta instanceof CraftItemMeta)) {
            throw new IllegalArgumentException("Meta of " + (meta != null ? meta.getClass().toString() : "null") + " not created by " + CraftItemFactory.class.getName());
        }
        return getItemMeta(material, (CraftItemMeta) meta);
    }
}
