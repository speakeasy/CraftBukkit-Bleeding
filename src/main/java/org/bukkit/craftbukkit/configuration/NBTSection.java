package org.bukkit.craftbukkit.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import net.minecraft.server.NBTBase;
import net.minecraft.server.NBTTagByte;
import net.minecraft.server.NBTTagByteArray;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagDouble;
import net.minecraft.server.NBTTagFloat;
import net.minecraft.server.NBTTagInt;
import net.minecraft.server.NBTTagIntArray;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.NBTTagLong;
import net.minecraft.server.NBTTagShort;
import net.minecraft.server.NBTTagString;

public class NBTSection extends MemorySection {
    private NBTTagCompound tag;

    public NBTSection(NBTTagCompound nbt) {
        tag = nbt;
    }

    public NBTSection(NBTTagCompound nbt, ConfigurationSection parent, String path) {
        super(parent, path);
        tag = nbt;
    }

    @Override
    public ConfigurationSection createSection(String path) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        } else if (path.length() == 0) {
            throw new IllegalArgumentException("Cannot create section at empty path");
        }

        String[] split = path.split(Pattern.quote(Character.toString(getRoot().options().pathSeparator())));
        ConfigurationSection section = this;

        for (int i = 0; i < split.length - 1; i++) {
            ConfigurationSection last = section;

            section = getConfigurationSection(split[i]);

            if (section == null) {
                section = last.createSection(split[i]);
            }
        }

        String key = split[split.length - 1];

        if (section == this) {
            NBTTagCompound newTag = new NBTTagCompound();
            ConfigurationSection result = new NBTSection(newTag, this, key);
            tag.setCompound(key, newTag);
            return result;
        } else {
            return section.createSection(key);
        }
    }

    @Override
    public Object get(String path, Object def) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        } else if (path.length() == 0) {
            return this;
        }

        String[] split = path.split(Pattern.quote(Character.toString(getRoot().options().pathSeparator())));
        ConfigurationSection section = this;

        for (int i = 0; i < split.length - 1; i++) {
            section = section.getConfigurationSection(split[i]);

            if (section == null) {
                return def;
            }
        }

        String key = split[split.length - 1];

        if (section == this) {
            NBTBase result = tag.get(key);
            return dataFromNBT(def, key, result);
        }
        return section.get(key, def);
    }

    private Object dataFromNBT(Object def, String key, NBTBase nbttag) {
        if (nbttag == null) {
            return def;
        } else if (nbttag instanceof NBTTagByte) {
            return ((NBTTagByte) nbttag).data;
        } else if (nbttag instanceof NBTTagShort) {
            return ((NBTTagShort) nbttag).data;
        } else if (nbttag instanceof NBTTagInt) {
            return ((NBTTagInt) nbttag).data;
        } else if (nbttag instanceof NBTTagLong) {
            return ((NBTTagLong) nbttag).data;
        } else if (nbttag instanceof NBTTagFloat) {
            return ((NBTTagFloat) nbttag).data;
        } else if (nbttag instanceof NBTTagDouble) {
            return ((NBTTagDouble) nbttag).data;
        } else if (nbttag instanceof NBTTagString) {
            return ((NBTTagString) nbttag).data;
        } else if (nbttag instanceof NBTTagByteArray) {
            return ((NBTTagByteArray) nbttag).data;
        } else if (nbttag instanceof NBTTagIntArray) {
            return ((NBTTagIntArray) nbttag).data;
        } else if (nbttag instanceof NBTTagCompound) {
            if (key != null) {
                return createSection(key);
            } else {
                Map<String,Object> map = new HashMap<String,Object>();
                NBTTagCompound nbt = (NBTTagCompound) nbttag;
                for (Object nbtkey : nbt.keys()) {
                    map.put(String.valueOf(nbtkey), dataFromNBT(null, null, nbt.get((String) nbtkey)));
                }
                ConfigurationSerializable object = ConfigurationSerialization.deserializeObject(map);
                if (object != null) {
                    return object;
                } else {
                    return map;
                }
            }
        } else if (nbttag instanceof NBTTagList) {
            List<Object> list = new ArrayList<Object>();
            NBTTagList nbt = (NBTTagList) nbttag;
            for (int i = 0; i < nbt.size(); i++) {
                list.add(dataFromNBT(null, null, nbt.get(i)));
            }
            return list;
        } else {
            return null;
        }
    }

    @Override
    public void set(String path, Object value) {
        String[] split = path.split(Pattern.quote(Character.toString(getRoot().options().pathSeparator())));
        ConfigurationSection section = this;

        if (path.length() == 0) {
            throw new IllegalArgumentException("Cannot set to an empty path");
        }

        for (int i = 0; i < split.length - 1; i++) {
            ConfigurationSection last = section;

            section = last.getConfigurationSection(split[i]);

            if (section == null) {
                section = last.createSection(split[i]);
            }
        }

        String key = split[split.length - 1];

        if (section == this) {
            if (value == null) {
                tag.remove(key);
            } else {
                tag.set(key, dataToNBT(key, value));
            }
        } else {
            section.set(key, value);
        }
    }

    private NBTBase dataToNBT(String key, Object value) {
        if (value instanceof Byte) {
            return new NBTTagByte(key, (Byte) value);
        } else if (value instanceof Short) {
            return new NBTTagShort(key, (Short) value);
        } else if (value instanceof Integer) {
            return new NBTTagInt(key, (Integer) value);
        } else if (value instanceof Long) {
            return new NBTTagLong(key, (Long) value);
        } else if (value instanceof Boolean) {
            return new NBTTagByte(key, (Byte) value);
        } else if (value instanceof Float) {
            return new NBTTagFloat(key, (Float) value);
        } else if (value instanceof Double) {
            return new NBTTagDouble(key, (Double) value);
        } else if (value instanceof String) {
            return new NBTTagString(key, (String) value);
        } else if (value instanceof byte[]) {
            return new NBTTagByteArray(key, (byte[]) value);
        } else if (value instanceof int[]) {
            return new NBTTagIntArray(key, (int[]) value);
        } else if (value instanceof List) {
            NBTTagList nbt = new NBTTagList(key);
            List<?> list = (List<?>) value;
            for (Object o : list) {
                nbt.add(dataToNBT(key, o));
            }
            return nbt;
        } else if (value instanceof Map) {
            NBTTagCompound nbt = new NBTTagCompound(key);
            Map<?,?> map = (Map<?,?>) value;
            for (Entry<?,?> entry : map.entrySet()) {
                nbt.set(String.valueOf(entry.getKey()), dataToNBT(key, entry.getValue()));
            }
            return nbt;
        } else if (value instanceof ConfigurationSerializable) {
            return dataToNBT(key, ((ConfigurationSerializable) value).serialize());
        }
        return null;
    }
}
