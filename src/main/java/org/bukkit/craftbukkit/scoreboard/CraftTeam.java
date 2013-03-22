package org.bukkit.craftbukkit.scoreboard;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.google.common.collect.ImmutableSet;

import net.minecraft.server.ScoreboardTeam;

public final class CraftTeam implements Team {

    private final Scoreboard scoreboard;
    private final ScoreboardTeam team;

    CraftTeam(Scoreboard scoreboard, ScoreboardTeam team) {
        this.scoreboard = scoreboard;
        this.team = team;
    }

    public String getName() {
        return this.team.getName();
    }

    public String getDisplayName() {
        return this.team.getDisplayName();
    }

    public void setDisplayName(String displayName) {
        Validate.notNull(displayName, "Display name cannot be null");
        if (displayName.length() > 32) {
            throw new IllegalArgumentException("Display name cannot be longer than 32 characters");
        }
        this.team.setDisplayName(displayName);
    }

    public String getPrefix() {
        return this.team.getPrefix();
    }

    public void setPrefix(String prefix) {
        Validate.notNull(prefix, "Prefix cannot be null");
        if (prefix.length() > 16) {
            throw new IllegalArgumentException("Prefix cannot be longer than 16 characters");
        }
        this.team.setPrefix(prefix);
    }

    public String getSuffix() {
        return this.team.getSuffix();
    }

    public void setSuffix(String suffix) {
        Validate.notNull(suffix, "Suffix cannot be null");
        if (suffix.length() > 16) {
            throw new IllegalArgumentException("Suffix cannot be longer than 16 characters");
        }
        this.team.setSuffix(suffix);
    }

    public boolean allowFriendlyFire() {
        return this.team.allowFriendlyFire();
    }

    public void setAllowFriendlyFire(boolean enabled) {
        this.team.setAllowFriendlyFire(enabled);
    }

    public boolean canSeeFriendlyInvisibles() {
        return this.team.canSeeFriendlyInvisibles();
    }

    public void setCanSeeFriendlyInvisibles(boolean enabled) {
        this.team.setCanSeeFriendlyInvisibles(enabled);
    }

    public Set<OfflinePlayer> getPlayers() {
        Set<OfflinePlayer> players = new HashSet<OfflinePlayer>();
        for (Object o : this.team.getPlayerNameSet()) {
            players.add(Bukkit.getOfflinePlayer(o.toString()));
        }
        return ImmutableSet.copyOf(players);
    }

    public int getSize() {
        return this.team.getPlayerNameSet().size();
    }

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    ScoreboardTeam getHandle() {
        return this.team;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && (o instanceof CraftTeam)) {
            CraftTeam t = (CraftTeam) o;
            return this.scoreboard.equals(t.scoreboard) && this.team.equals(t.team);
        }
        return false;
    }
}
