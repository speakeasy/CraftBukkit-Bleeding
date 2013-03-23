package org.bukkit.craftbukkit.scoreboard;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.server.IScoreboardCriteria;
import net.minecraft.server.ScoreboardObjective;
import net.minecraft.server.ScoreboardScore;
import net.minecraft.server.ScoreboardServer;
import net.minecraft.server.ScoreboardTeam;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Objective.Criteria;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.google.common.collect.ImmutableSet;

public final class CraftScoreboard implements Scoreboard {
    private final ScoreboardServer board;

    CraftScoreboard(ScoreboardServer board) {
        this.board = board;
    }

    public Objective registerObjective(String name, Criteria criteria) {
        Validate.notNull(name, "Objective name cannot be null");
        Validate.notNull(criteria, "Criteria cannot be null");
        Validate.isTrue(name.length() <= 16, "The name '" + name + "' is longer than the limit of 16 characters");
        Validate.isTrue(board.getObjective(name) == null, "An objective of name '" + name + "' already exists");
        return new CraftObjective(this, board.registerObjective(name, nmsCriteriaToBukkit(criteria)));
    }

    public Objective getObjective(String name) {
        Validate.notNull(name, "Objective name cannot be null");
        ScoreboardObjective objective = board.getObjective(name);
        return objective == null ? null : new CraftObjective(this, objective);
    }

    public Set<Objective> getObjectivesByCriteria(Criteria criteria) {
        Validate.notNull(criteria, "Criteria cannot be null");
        return nmsObjectivesToSet(board.getObjectivesForCriteria(nmsCriteriaToBukkit(criteria)));
    }

    public Set<Objective> getObjectives() {
        return nmsObjectivesToSet(board.getObjectives());
    }

    public void unregisterObjective(Objective objective) {
        Validate.notNull(objective, "Objective cannot be null");
        board.unregisterObjective(((CraftObjective) objective).getHandle());
    }

    public void setDisplaySlot(DisplaySlot slot, Objective objective) {
        Validate.notNull(slot, "Display slot cannot be null");
        board.setDisplaySlot(net.minecraft.server.Scoreboard.getSlotForName(slot.getCommandName()), objective == null ? null : ((CraftObjective) objective).getHandle());
    }

    public Objective getDisplaySlot(DisplaySlot slot) {
        Validate.notNull(slot, "Display slot cannot be null");
        ScoreboardObjective objective = board.getObjectiveForSlot(net.minecraft.server.Scoreboard.getSlotForName(slot.getCommandName()));
        return objective == null ? null : new CraftObjective(this, objective);
    }

    public Score getScore(Objective objective, OfflinePlayer player) {
        Validate.notNull(objective, "Objective cannot be null");
        Validate.notNull(player, "OfflinePlayer cannot be null");
        return new CraftScore(this, board.getPlayerScoreForObjective(player.getName(), ((CraftObjective) objective).getHandle()));
    }

    public Set<Score> getScores(OfflinePlayer player) {
        Validate.notNull(player, "OfflinePlayer cannot be null");
        Set<Score> scores = new HashSet<Score>();
        for (Object o : board.getPlayerObjectives(player.getName()).values()) {
            if (o != null && o instanceof ScoreboardScore) {
                scores.add(new CraftScore(this, (ScoreboardScore) o));
            }
        }
        return ImmutableSet.copyOf(scores);
    }

    public void resetScores(OfflinePlayer player) {
        Validate.notNull(player, "OfflinePlayer cannot be null");
        board.resetPlayerScores(player.getName());
    }

    public Team getPlayerTeam(OfflinePlayer player) {
        Validate.notNull(player, "OfflinePlayer cannot be null");
        ScoreboardTeam team = board.getTeam(player.getName());
        return team == null ? null : new CraftTeam(this, team);
    }

    public void setPlayerTeam(OfflinePlayer player, Team team) {
        Validate.notNull(player, "OfflinePlayer cannot be null");
        board.addPlayerToTeam(player.getName(), team == null ? null : ((CraftTeam) team).getHandle());
    }

    public Team getTeam(String teamName) {
        Validate.notNull(teamName, "Team name cannot be null");
        ScoreboardTeam team = board.getTeam(teamName);
        return team == null ? null : new CraftTeam(this, team);
    }

    public Set<Team> getTeams() {
        Set<Team> teams = new HashSet<Team>();
        for (Object o : board.getTeams()) {
            if (o != null && o instanceof ScoreboardTeam) {
                teams.add(new CraftTeam(this, (ScoreboardTeam) o));
            }
        }
        return ImmutableSet.copyOf(teams);
    }

    public Team registerTeam(String name) {
        Validate.notNull(name, "Team name cannot be null");
        Validate.isTrue(name.length() <= 16, "Team name '" + name + "' is longer than the limit of 16 characters");
        Validate.isTrue(board.getTeam(name) == null, "Team name '" + name + "' is already in use");
        return new CraftTeam(this, board.createTeam(name));
    }

    public void unregisterTeam(Team team) {
        Validate.notNull(team, "Team cannot be null");
        board.removeTeam(((CraftTeam) team).getHandle());
    }

    public Set<OfflinePlayer> getPlayers() {
        Set<OfflinePlayer> players = new HashSet<OfflinePlayer>();
        for (Object o : board.getPlayers()) {
            players.add(Bukkit.getOfflinePlayer(o.toString()));
        }
        return ImmutableSet.copyOf(players);
    }

    private IScoreboardCriteria nmsCriteriaToBukkit(Criteria criteria) {
        return (IScoreboardCriteria) IScoreboardCriteria.a.get(criteria.getCommandName());
    }

    private Set<Objective> nmsObjectivesToSet(Collection<?> collection) {
        Set<Objective> objectives = new HashSet<Objective>();
        for (Object o : collection) {
            if (o != null && o instanceof ScoreboardObjective) {
                objectives.add(new CraftObjective(this, (ScoreboardObjective) o));
            }
        }
        return ImmutableSet.copyOf(objectives);
    }

    public ScoreboardServer getHandle() {
        return board;
    }
}
