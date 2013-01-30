package org.bukkit.craftbukkit;

import java.util.Date;
import java.util.Set;

import net.minecraft.server.BanEntry;
import net.minecraft.server.BanList;

import org.apache.commons.lang.Validate;

import com.google.common.collect.ImmutableSet;

public final class CraftBanList implements org.bukkit.BanList {
    private final BanList list;

    public CraftBanList(BanList banList) {
        this.list = banList;
    }

    public org.bukkit.BanEntry getBanEntry(String name) {
        return list.getEntries().containsKey(name) ? new CraftBanEntry((BanEntry) list.getEntries().get(name), list) : null;
    }

    public org.bukkit.BanEntry addBan(String name, String source, Date expires, String reason) {
        Validate.notNull(name, "Banned entry name may not be null");
        BanEntry entry = new BanEntry(name);
        entry.setSource(source == null ? entry.getSource() : source);
        entry.setExpires(expires);
        entry.setReason(reason == null ? entry.getReason() : reason);

        list.add(entry);
        list.save();
        return new CraftBanEntry(entry, list);
    }

    @SuppressWarnings("unchecked")
    public Set<org.bukkit.BanEntry> getBanEntries() {
        ImmutableSet.Builder<org.bukkit.BanEntry> builder = ImmutableSet.builder();
        for (BanEntry entry : (Set<BanEntry>) list.getEntries().values()) {
            builder.add(new CraftBanEntry(entry, list));
        }
        return builder.build();
    }

    public void unban(String name) {
        list.remove(name);
    }

    public boolean isBanned(String name) {
        return this.list.isBanned(name);
    }

}
