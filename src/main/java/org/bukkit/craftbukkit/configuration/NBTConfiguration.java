package org.bukkit.craftbukkit.configuration;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.Configuration;

import net.minecraft.server.NBTTagCompound;

public class NBTConfiguration extends NBTSection implements Configuration {
    private Configuration defaults;
    private NBTConfigurationOptions options;

    public NBTConfiguration(NBTTagCompound tag) {
        super(tag);
        options = new NBTConfigurationOptions(this);
    }

    public NBTConfiguration(NBTTagCompound tag, Configuration dflt) {
        this(tag);
        defaults = dflt;
    }

    public void addDefaults(Map<String, Object> dflt) {
        for(Entry<String, Object> entry : dflt.entrySet()) {
            defaults.set(entry.getKey(), entry.getValue());
        }
    }

    public void addDefaults(Configuration dflt) {
        if (this == dflt) {
            return;
        }
        for(String key : dflt.getKeys(true)) {
            defaults.set(key, dflt.get(key));
        }
    }

    public void setDefaults(Configuration dflt) {
        defaults = dflt;
    }

    public Configuration getDefaults() {
        return defaults;
    }

    public NBTConfigurationOptions options() {
        return options;
    }
}
