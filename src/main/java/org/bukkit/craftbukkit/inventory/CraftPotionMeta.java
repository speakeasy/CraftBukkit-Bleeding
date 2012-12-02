package org.bukkit.craftbukkit.inventory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.craftbukkit.inventory.CraftItemMeta.SerializableMeta;

import com.google.common.collect.ImmutableList;

@DelegateDeserialization(SerializableMeta.class)
final class CraftPotionMeta extends CraftItemMeta implements PotionMeta {
    // TODO: ItemMetaKey effects
    private List<PotionEffect> customEffects;

    CraftPotionMeta(CraftItemMeta meta) {
        super(meta);
        if (!(meta instanceof CraftPotionMeta)) {
            return;
        }
        CraftPotionMeta potionMeta = (CraftPotionMeta) meta;
        if (potionMeta.hasCustomEffects()) {
            this.customEffects = new ArrayList<PotionEffect>(potionMeta.customEffects);
        }
    }

    CraftPotionMeta(NBTTagCompound tag) {
        super(tag);

        if (tag.hasKey("CustomPotionEffects")) {
            NBTTagList list = tag.getList("CustomPotionEffects");
            int length = list.size();
            if (length > 0) {
                customEffects = new ArrayList<PotionEffect>(length);

                for (int i = 0; i < length; i++) {
                    NBTTagCompound effect = (NBTTagCompound) list.get(i);
                    PotionEffectType type = PotionEffectType.getById(effect.getByte("Id"));
                    int amp = effect.getByte("Amplifier");
                    int duration = effect.getInt("Duration");
                    boolean ambient = effect.getBoolean("Ambient");
                    customEffects.add(new PotionEffect(type, amp, duration, ambient));
                }
            }
        }
    }

    CraftPotionMeta(Map<String, Object> map) {
        super(map);
        // TODO Auto-generated constructor stub
    }

    @Override
    void applyToItem(NBTTagCompound tag) {
        super.applyToItem(tag);
        if (hasCustomEffects()) {
            NBTTagList effectList = new NBTTagList();
            tag.set("CustomPotionEffects", effectList);

            for (PotionEffect effect : customEffects) {
                NBTTagCompound effectData = new NBTTagCompound();
                effectData.setByte("Id", (byte) effect.getType().getId());
                effectData.setByte("Amplifier", (byte) effect.getAmplifier());
                effectData.setInt("Duration", effect.getDuration());
                effectData.setBoolean("Ambient", effect.isAmbient());
                effectList.add(effectData);
            }
        } else {
            tag.o("CustomPotionEffects");
        }
    }

    @Override
    boolean hasUncommon() {
        return hasCustomEffects();
    }

    @Override
    boolean applicableTo(Material type) {
        switch(type) {
            case POTION:
                return true;
            default:
                return false;
        }
    }

    @Override
    public CraftPotionMeta clone() {
        CraftPotionMeta clone = (CraftPotionMeta) super.clone();
        if (hasCustomEffects()) {
            clone.customEffects = new ArrayList<PotionEffect>(customEffects);
        }
        return clone;
    }

    public boolean hasCustomEffects() {
        return !(customEffects == null || customEffects.isEmpty());
    }

    public List<PotionEffect> getCustomEffects() {
        if (hasCustomEffects()) {
            return ImmutableList.copyOf(customEffects);
        } else {
            return ImmutableList.of();
        }
    }

    public boolean addCustomEffect(PotionEffect effect, boolean overwrite) {
        Validate.notNull(effect, "Potion effect must not be null");

        int index = indexOfEffect(effect.getType());
        if (index != -1) {
            if (overwrite) {
                PotionEffect old = customEffects.get(index);
                if (old.getAmplifier() == effect.getAmplifier() && old.getDuration() == effect.getDuration() && old.isAmbient() == effect.isAmbient()) {
                    return false;
                }
                customEffects.set(index, effect);
                return true;
            } else {
                return false;
            }
        } else {
            if (customEffects == null) {
                customEffects = new ArrayList<PotionEffect>();
            }
            customEffects.add(effect);
            return true;
        }
    }

    public boolean removeCustomEffect(PotionEffectType type) {
        Validate.notNull(type, "Potion effect type must not be null");

        if (!hasCustomEffects()) {
            return false;
        }

        boolean changed = false;
        Iterator<PotionEffect> iterator = customEffects.iterator();
        while (iterator.hasNext()) {
            PotionEffect effect = iterator.next();
            if (effect.getType() == type) {
                iterator.remove();
                changed = true;
            }
        }
        return changed;
    }

    public boolean hasCustomEffect(PotionEffectType type) {
        Validate.notNull(type, "Potion effect type must not be null");
        return indexOfEffect(type) != -1;
    }

    public boolean setMainEffect(PotionEffectType type) {
        Validate.notNull(type, "Potion effect type must not be null");
        int index = indexOfEffect(type);
        if (index == -1 || index == 0) {
            return false;
        }

        PotionEffect old = customEffects.get(0);
        customEffects.set(0, customEffects.get(index));
        customEffects.set(index, old);
        return true;
    }

    private int indexOfEffect(PotionEffectType type) {
        if (!hasCustomEffects()) {
            return -1;
        }

        for (int i = 0; i < customEffects.size(); i++) {
            if (customEffects.get(i).getType().equals(type)) {
                return i;
            }
        }
        return -1;
    }

    public boolean clearCustomEffects() {
        boolean changed = hasCustomEffects();
        customEffects = null;
        return changed;
    }

    @Override
    int applyHash(final int original) {
        int hash = original;
        if (hasCustomEffects()) {
            hash = 73 * hash + customEffects.hashCode();
        }
        return original != hash ? CraftPotionMeta.class.hashCode() ^ hash : hash;
    }

    @Override
    public boolean equalsCommon(CraftItemMeta meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof CraftPotionMeta) {
            CraftPotionMeta that = (CraftPotionMeta) meta;

            return (this.hasCustomEffects() ? that.hasCustomEffects() && this.customEffects.equals(that.customEffects) : !that.hasCustomEffects());
        }
        return true;
    }

    @Override
    boolean notUncommon(CraftItemMeta meta) {
        return meta instanceof CraftPotionMeta || super.notUncommon(meta);
    }
}
