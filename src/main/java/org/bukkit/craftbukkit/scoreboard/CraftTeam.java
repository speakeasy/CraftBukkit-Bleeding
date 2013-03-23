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
        return team.getName();
    }

    public String getDisplayName() {
        return team.getDisplayName();
    }

    public void setDisplayName(String displayName) {
        Validate.notNull(displayName, "Display name cannot be null");
        Validate.isTrue(displayName.length() <= 32, "Display name '" + displayName + "' is longer than the limit of 32 characters");
        team.setDisplayName(displayName);
    }

    public String getPrefix() {
        return team.getPrefix();
    }

    public void setPrefix(String prefix) {
        Validate.notNull(prefix, "Prefix cannot be null");
        Validate.isTrue(prefix.length() <= 32, "Prefix '" + prefix + "' is longer than the limit of 32 characters");
        team.setPrefix(prefix);
    }

    public String getSuffix() {
        return team.getSuffix();
    }

    public void setSuffix(String suffix) {
        Validate.notNull(suffix, "Suffix cannot be null");
        Validate.isTrue(suffix.length() <= 32, "Suffix '" + suffix + "' is longer than the limit of 32 characters");
        team.setSuffix(suffix);
    }

    public boolean allowFriendlyFire() {
        return team.allowFriendlyFire();
    }

    public void setAllowFriendlyFire(boolean enabled) {
        team.setAllowFriendlyFire(enabled);
    }

    public boolean canSeeFriendlyInvisibles() {
        return team.canSeeFriendlyInvisibles();
    }

    public void setCanSeeFriendlyInvisibles(boolean enabled) {
        team.setCanSeeFriendlyInvisibles(enabled);
    }

    public Set<OfflinePlayer> getPlayers() {
        Set<OfflinePlayer> players = new HashSet<OfflinePlayer>();
        for (Object o : team.getPlayerNameSet()) {
            players.add(Bukkit.getOfflinePlayer(o.toString()));
        }
        return ImmutableSet.copyOf(players);
    }

    public int getSize() {
        return team.getPlayerNameSet().size();
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    ScoreboardTeam getHandle() {
        return team;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CraftTeam) {
            CraftTeam t = (CraftTeam) o;
            return scoreboard.equals(t.scoreboard) && team.equals(t.team);
        }
        return false;
    }
}
