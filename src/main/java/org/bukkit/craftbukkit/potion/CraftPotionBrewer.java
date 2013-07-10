package org.bukkit.craftbukkit.potion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.minecraft.server.MobEffect;

import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionBrewer;
import org.bukkit.potion.PotionEffect;

import com.google.common.collect.Maps;

public class CraftPotionBrewer implements PotionBrewer {
    private static final Map<Integer, Collection<PotionEffect>> cache = Maps.newHashMap();

    public Collection<PotionEffect> getEffectsFromDamage(int damage) {
        if (cache.containsKey(damage))
            return cache.get(damage);

        List<?> mcEffects = net.minecraft.server.PotionBrewer.getEffects(damage, false);
        List<PotionEffect> effects = new ArrayList<PotionEffect>();
        if (mcEffects == null)
            return effects;

        for (Object raw : mcEffects) {
            if (raw == null || !(raw instanceof MobEffect))
                continue;
            MobEffect mcEffect = (MobEffect) raw;
            PotionEffect effect = new PotionEffect(PotionEffectType.getById(mcEffect.getEffectId()),
                    mcEffect.getDuration(), mcEffect.getAmplifier());
            // Minecraft PotionBrewer applies duration modifiers automatically.
            effects.add(effect);
        }

        cache.put(damage, effects);

        return effects;
    }

    public PotionEffect createEffect(PotionEffectType potion, int duration, int amplifier) {
        return new PotionEffect(potion, potion.isInstant() ? 1 : (int) (duration * potion.getDurationModifier()),
                amplifier);
    }

    public static PotionEffect nmsToBukkitEffect(MobEffect handle) {
        return new PotionEffect(PotionEffectType.getById(handle.getEffectId()), handle.getDuration(), handle.getAmplifier(), handle.isAmbient());
    }

    public static List<PotionEffect> nmsToBukkitEffects(Collection<MobEffect> coll) {
        List<PotionEffect> list = new ArrayList<PotionEffect>();
        if (coll != null) {
            for (Object o : coll) {
                if (o instanceof MobEffect) {
                    MobEffect handle = (MobEffect) o;
                    list.add(nmsToBukkitEffect(handle));
                }
            }
        }
        return list;
    }

    public static MobEffect bukkitToNmsEffect(PotionEffect effect) {
        return new MobEffect(effect.getType().getId(), effect.getDuration(), effect.getAmplifier(), effect.isAmbient());
    }

    public static List<MobEffect> bukkitToNmsEffects(Collection<PotionEffect> coll) {
        List<MobEffect> list = new ArrayList<MobEffect>();
        if (coll != null) {
            for (Object o : coll) {
                if (o instanceof PotionEffect) {
                    PotionEffect effect = (PotionEffect) o;
                    list.add(bukkitToNmsEffect(effect));
                }
            }
        }
        return list;
    }
}
