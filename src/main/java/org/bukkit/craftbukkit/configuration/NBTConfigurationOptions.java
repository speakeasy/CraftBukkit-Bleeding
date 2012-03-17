package org.bukkit.craftbukkit.configuration;

import org.bukkit.configuration.ConfigurationOptions;

public class NBTConfigurationOptions extends ConfigurationOptions {
    protected NBTConfigurationOptions(NBTConfiguration configuration) {
        super(configuration);
    }

    @Override
    public NBTConfiguration configuration() {
        return (NBTConfiguration) super.configuration();
    }

    @Override
    public NBTConfigurationOptions copyDefaults(boolean value) {
        super.copyDefaults(value);
        return this;
    }

    @Override
    public NBTConfigurationOptions pathSeparator(char value) {
        super.pathSeparator(value);
        return this;
    }
}
