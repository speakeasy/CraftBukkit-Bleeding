package org.bukkit.craftbukkit;

import java.util.Date;

import net.minecraft.server.BanEntry;
import net.minecraft.server.BanList;

public final class CraftBanEntry implements org.bukkit.BanEntry {
    private final BanList list;
    private final String name;
    private Date created;
    private String source;
    private Date expires;
    private String reason;

    public CraftBanEntry(BanEntry entry, BanList list) {
        this.list = list;
        this.name = entry.getName();
        this.created = new Date(entry.getCreated().getTime());
        this.source = entry.getSource();
        this.expires = new Date(entry.getExpires().getTime());
        this.reason = entry.getReason();
    }

    public String getName() {
        return this.name;
    }

    public Date getCreated() {
        return (Date) this.created.clone();
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Date getExpires() {
        return (Date) this.expires.clone();
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void save() {
        BanEntry entry = new BanEntry(this.name);
        entry.setCreated(this.created);
        entry.setSource(this.source);
        entry.setExpires(this.expires);
        entry.setReason(this.reason);
        this.list.add(entry);
        this.list.save();
    }

}
