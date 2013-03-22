package org.bukkit.craftbukkit.scoreboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.IScoreboardCriteria;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Packet206SetScoreboardObjective;
import net.minecraft.server.Packet209SetScoreboardTeam;
import net.minecraft.server.ScoreboardObjective;
import net.minecraft.server.ScoreboardScore;
import net.minecraft.server.ScoreboardServer;
import net.minecraft.server.ScoreboardTeam;

import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public final class CraftScoreboardManager implements ScoreboardManager {

    private final CraftScoreboard mainScoreboard;
    private final MinecraftServer server;
    private final Set<CraftScoreboard> scoreboards = new HashSet<CraftScoreboard>();
    private final Map<CraftPlayer, CraftScoreboard> playerBoards = new HashMap<CraftPlayer, CraftScoreboard>();

    public CraftScoreboardManager(MinecraftServer server, ScoreboardServer scoreboard) {
        this.mainScoreboard = new CraftScoreboard(scoreboard);
        this.server = server;
        scoreboard.setManager(this);
        this.scoreboards.add(this.mainScoreboard);
    }

    public CraftScoreboard getMainScoreboard() {
        return this.mainScoreboard;
    }

    public CraftScoreboard registerScoreboard() {
        CraftScoreboard scoreboard = new CraftScoreboard(new ScoreboardServer(this.server, this));
        this.scoreboards.add(scoreboard);
        return scoreboard;
    }

    public void registerScoreboard(Scoreboard scoreboard) {
        Validate.notNull(scoreboard, "Scoreboard may not be null");
        this.scoreboards.add((CraftScoreboard) scoreboard);
    }

    public void unregisterScoreboard(Scoreboard scoreboard) {
        Validate.notNull(scoreboard, "Scoreboard may not be null");
        if (scoreboard.equals(this.mainScoreboard)) {
            throw new IllegalArgumentException("You may not unregister the main scoreboard");
        }
        this.scoreboards.remove(scoreboard);
    }

    @SuppressWarnings("unchecked")
    public Collection<?> getObjectivesForCriteria(IScoreboardCriteria c) {
        Collection<Object> collection = new ArrayList<Object>();
        for (CraftScoreboard scoreboard : scoreboards) {
            collection.addAll(((CraftScoreboard) scoreboard).getHandle().getObjectivesForCriteria(c));
        }
        return collection;
    }

    public ScoreboardScore getPlayerScoreForObjective(String name, ScoreboardObjective objective) {
        return objective.getScoreboard().getPlayerScoreForObjective(name, objective);
    }

    public CraftScoreboard getPlayerBoard(CraftPlayer player) {
        CraftScoreboard board = this.playerBoards.get(player);
        return (CraftScoreboard) (board == null ? this.getMainScoreboard() : board);
    }

    public void setPlayerBoard(CraftPlayer player, Scoreboard scoreboard) {
        if (!this.scoreboards.contains(scoreboard)) {
            throw new IllegalArgumentException("Scoreboard is unregistered!");
        }
        ScoreboardServer oldboard = this.getPlayerBoard(player).getHandle();
        if (oldboard.equals(scoreboard)) {
            return;
        }
        if (scoreboard.equals(this.mainScoreboard)) {
            this.playerBoards.remove(player);
        } else {
            this.playerBoards.put(player, (CraftScoreboard) scoreboard);
        }
        ScoreboardServer newboard = ((CraftScoreboard) scoreboard).getHandle();

        EntityPlayer entityplayer = player.getHandle();
        HashSet<ScoreboardObjective> removed = new HashSet<ScoreboardObjective>();
        for (int i = 0; i < 3; ++i) {
            ScoreboardObjective scoreboardobjective = oldboard.getObjectiveForSlot(i);
            if (scoreboardobjective != null && !removed.contains(scoreboardobjective)) {
                entityplayer.playerConnection.sendPacket(new Packet206SetScoreboardObjective(scoreboardobjective, 1));
                removed.add(scoreboardobjective);
            }
        }
        Iterator<?> iterator = oldboard.getTeams().iterator();
        while (iterator.hasNext()) {
            ScoreboardTeam scoreboardteam = (ScoreboardTeam) iterator.next();
            entityplayer.playerConnection.sendPacket(new Packet209SetScoreboardTeam(scoreboardteam, 1));
        }
        // The above is the reverse of the below method.
        this.server.getPlayerList().a(newboard, player.getHandle());
    }

    public void removePlayer(Player player) {
        this.playerBoards.remove(player);
    }
}
