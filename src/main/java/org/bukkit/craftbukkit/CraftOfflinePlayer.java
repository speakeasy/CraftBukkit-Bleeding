package org.bukkit.craftbukkit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.server.BanEntry;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.NBTCompressedStreamTools;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagDouble;
import net.minecraft.server.NBTTagFloat;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.WorldNBTStorage;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.craftbukkit.inventory.CraftInventoryPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

@SerializableAs("Player")
public class CraftOfflinePlayer implements OfflinePlayer, ConfigurationSerializable {
    private final String name;
    private final CraftServer server;
    private final WorldNBTStorage storage;
    private CraftInventoryPlayer inventory;

    protected CraftOfflinePlayer(CraftServer server, String name) {
        this.server = server;
        this.name = name;
        this.storage = (WorldNBTStorage) (server.console.worlds.get(0).getDataManager());
    }

    public boolean isOnline() {
        return getPlayer() != null;
    }

    public String getName() {
        return name;
    }

    public Server getServer() {
        return server;
    }

    public boolean isOp() {
        return server.getHandle().isOp(getName().toLowerCase());
    }

    public void setOp(boolean value) {
        if (value == isOp()) return;

        if (value) {
            server.getHandle().addOp(getName().toLowerCase());
        } else {
            server.getHandle().removeOp(getName().toLowerCase());
        }
    }

    public boolean isBanned() {
        return server.getHandle().getNameBans().isBanned(name.toLowerCase());
    }

    public void setBanned(boolean value) {
        if (value) {
            BanEntry entry = new BanEntry(name.toLowerCase());
            server.getHandle().getNameBans().add(entry);
        } else {
            server.getHandle().getNameBans().remove(name.toLowerCase());
        }

        server.getHandle().getNameBans().save();
    }

    public boolean isWhitelisted() {
        return server.getHandle().getWhitelisted().contains(name.toLowerCase());
    }

    public void setWhitelisted(boolean value) {
        if (value) {
            server.getHandle().addWhitelist(name.toLowerCase());
        } else {
            server.getHandle().removeWhitelist(name.toLowerCase());
        }
    }

    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();

        result.put("name", name);

        return result;
    }

    public static OfflinePlayer deserialize(Map<String, Object> args) {
        return Bukkit.getServer().getOfflinePlayer((String) args.get("name"));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[name=" + name + "]";
    }

    public Player getPlayer() {
        for (Object obj : server.getHandle().players) {
            EntityPlayer player = (EntityPlayer) obj;
            if (player.name.equalsIgnoreCase(getName())) {
                return (player.netServerHandler != null) ? player.netServerHandler.getPlayer() : null;
            }
        }

        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof OfflinePlayer)) {
            return false;
        }
        OfflinePlayer other = (OfflinePlayer) obj;
        if ((this.getName() == null) || (other.getName() == null)) {
            return false;
        }
        return this.getName().equalsIgnoreCase(other.getName());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.getName() != null ? this.getName().toLowerCase().hashCode() : 0);
        return hash;
    }

    private NBTTagCompound getData() {
        return storage.getPlayerData(getName());
    }

    private NBTTagCompound getBukkitData() {
        NBTTagCompound result = getData();

        if (result != null) {
            if (!result.hasKey("bukkit")) {
                result.setCompound("bukkit", new NBTTagCompound());
            }
            result = result.getCompound("bukkit");
        }

        return result;
    }

    private File getDataFile() {
        return new File(storage.getPlayerDir(), name + ".dat");
    }

    public long getFirstPlayed() {
        Player player = getPlayer();
        if (player != null) return player.getFirstPlayed();

        NBTTagCompound data = getBukkitData();

        if (data != null) {
            if (data.hasKey("firstPlayed")) {
                return data.getLong("firstPlayed");
            } else {
                File file = getDataFile();
                return file.lastModified();
            }
        } else {
            return 0;
        }
    }

    public long getLastPlayed() {
        Player player = getPlayer();
        if (player != null) return player.getLastPlayed();

        NBTTagCompound data = getBukkitData();

        if (data != null) {
            if (data.hasKey("lastPlayed")) {
                return data.getLong("lastPlayed");
            } else {
                File file = getDataFile();
                return file.lastModified();
            }
        } else {
            return 0;
        }
    }

    public boolean hasPlayedBefore() {
        return getData() != null;
    }

    public Location getBedSpawnLocation() {
        NBTTagCompound data = getData();
        if (data.hasKey("SpawnX") && data.hasKey("SpawnY") && data.hasKey("SpawnZ")) {
            String spawnWorld = data.getString("SpawnWorld");
            if (spawnWorld.equals("")) {
                spawnWorld = server.getWorlds().get(0).getName();
            }
            return new Location(server.getWorld(spawnWorld), data.getInt("SpawnX"), data.getInt("SpawnY"), data.getInt("SpawnZ"));
        }
        return null;
    }

    public void setBedSpawnLocation(Location location) {
        NBTTagCompound data = getData();
        data.setInt("SpawnX", location.getBlockX());
        data.setInt("SpawnY", location.getBlockY());
        data.setInt("SpawnZ", location.getBlockZ());
        data.setString("SpawnWorld", location.getWorld().getName());
        saveData();
    }

    public void setMetadata(String metadataKey, MetadataValue metadataValue) {
        server.getPlayerMetadata().setMetadata(this, metadataKey, metadataValue);
    }

    public List<MetadataValue> getMetadata(String metadataKey) {
        return server.getPlayerMetadata().getMetadata(this, metadataKey);
    }

    public boolean hasMetadata(String metadataKey) {
        return server.getPlayerMetadata().hasMetadata(this, metadataKey);
    }

    public void removeMetadata(String metadataKey, Plugin plugin) {
        server.getPlayerMetadata().removeMetadata(this, metadataKey, plugin);
    }

    public PlayerInventory getInventory() {
        if (inventory == null) {
            NBTTagList nbttaglist = getData().getList("Inventory");
            net.minecraft.server.PlayerInventory inv = new net.minecraft.server.PlayerInventory(null);
            inv.b(nbttaglist);
            inventory = new CraftInventoryPlayer(inv);
        }
        return inventory;
    }

    public void updateInventory() {
        getData().set("Inventory", inventory.getInventory().a(new NBTTagList()));
        saveData();
    }

    public Location getLocation() {
        NBTTagCompound data = getData();
        Location location = null;
        if (data.hasKey("Pos") && data.hasKey("Rotation")) {
            NBTTagList pos = data.getList("Pos");
            NBTTagList rot = data.getList("Rotation");
            double locX = ((NBTTagDouble) pos.get(0)).data;
            double locY = ((NBTTagDouble) pos.get(1)).data;
            double locZ = ((NBTTagDouble) pos.get(2)).data;
            float yaw = ((NBTTagFloat) rot.get(0)).data;
            float pitch = ((NBTTagFloat) rot.get(1)).data;
            World world = null;
            if (data.hasKey("WorldUUIDMost") && data.hasKey("WorldUUIDLeast")) {
                UUID uid = new UUID(data.getLong("WorldUUIDMost"), data.getLong("WorldUUIDLeast"));
                world = server.getWorld(uid);
            }
            location = new Location(world, locX, locY, locZ, yaw, pitch);
        }
        return location;
    }

    public void setLocation(Location location) {
        NBTTagCompound data = getData();

        NBTTagList pos = new NBTTagList();
        pos.add(new NBTTagDouble(null, location.getX()));
        pos.add(new NBTTagDouble(null, location.getY()));
        pos.add(new NBTTagDouble(null, location.getZ()));
        data.set("Pos", pos);

        NBTTagList rot = new NBTTagList();
        rot.add(new NBTTagDouble(null, location.getYaw()));
        rot.add(new NBTTagDouble(null, location.getPitch()));
        data.set("Rotation", rot);

        data.setLong("WorldUUIDLeast", location.getWorld().getUID().getLeastSignificantBits());
        data.setLong("WorldUUIDMost", location.getWorld().getUID().getMostSignificantBits());
        
        saveData();
    }

    private void saveData() {
        try {
            File file1 = new File(getDataFile().getParentFile(), name + ".dat~");
            File file2 = new File(getDataFile().getParentFile(), name + ".dat");

            NBTCompressedStreamTools.a(getData(), new FileOutputStream(file1));
            if (file2.exists()) {
                file2.delete();
            }

            file1.renameTo(file2);
        } catch (Exception exception) {
            Bukkit.getLogger().warning("Failed to save player data for " + name);
        }
    }
}
