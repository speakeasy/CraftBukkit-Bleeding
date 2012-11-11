package org.bukkit.craftbukkit.inventory;

import com.google.common.collect.ImmutableList;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import org.bukkit.Material;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class CraftPotionMeta extends CraftItemMeta implements PotionMeta {
    private List<PotionEffect> customEffects = new ArrayList<PotionEffect>();

    CraftPotionMeta(CraftItemMeta meta) {
        super(meta);
        if (!(meta instanceof CraftPotionMeta)) {
            return;
        }
        CraftPotionMeta potionMeta = (CraftPotionMeta) meta;
        this.customEffects = new ArrayList<PotionEffect>(potionMeta.customEffects);
    }

    CraftPotionMeta(NBTTagCompound tag) {
        super(tag);

        if (tag.hasKey("CustomPotionEffects")) {
            NBTTagList list = tag.getList("CustomPotionEffects");
            int length = list.size();
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

    CraftPotionMeta(Map<String, Object> map) {
        super(map);
        // TODO Auto-generated constructor stub
    }

    @Override
    void applyToItem(NBTTagCompound tag) {
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
            tag.remove("CustomPotionEffects");
        }
    }

    @Override
    boolean isEmpty() {
        return !hasCustomEffects() && super.isEmpty();
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
        clone.customEffects = new ArrayList<PotionEffect>(customEffects);
        return clone;
    }

    public boolean hasCustomEffects() {
        return !customEffects.isEmpty();
    }

    public List<PotionEffect> getCustomEffects() {
        return ImmutableList.copyOf(customEffects);
    }

    public boolean addCustomEffect(PotionEffect effect, boolean overwrite) {
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
            customEffects.add(effect);
            return true;
        }
    }

    public boolean removeCustomEffect(PotionEffectType type) {
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
        return indexOfEffect(type) == -1;
    }

    public boolean setMainEffect(PotionEffectType type) {
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
        for (int i = 0; i < customEffects.size(); i++) {
            if (customEffects.get(i).getType().equals(type)) {
                return i;
            }
        }
        return -1;
    }

    public boolean clearCustomEffects() {
        boolean changed = hasCustomEffects();
        customEffects.clear();
        return changed;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 73 * hash + (customEffects != null ? customEffects.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof CraftPotionMeta)) {
            return false;
        }
        CraftPotionMeta other = (CraftPotionMeta) object;

        if (!this.customEffects.equals(other.customEffects)) {
            return false;
        }

        return super.equals(object);
    }
}
