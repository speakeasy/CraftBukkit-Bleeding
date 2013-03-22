package org.bukkit.craftbukkit.scoreboard;

import net.minecraft.server.ScoreboardScore;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public final class CraftScore implements Score {

    private final Scoreboard scoreboard;
    private final ScoreboardScore score;

    CraftScore(Scoreboard scoreboard, ScoreboardScore score) {
        this.scoreboard = scoreboard;
        this.score = score;
    }

    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer(this.score.getPlayerName());
    }

    public Objective getObjective() {
        return new CraftObjective(this.scoreboard, this.score.getObjective());
    }

    public int getScore() {
        return this.score.getScore();
    }

    public void setScore(int score) {
        this.score.setScore(score);
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && (o instanceof CraftScore)) {
            CraftScore s = (CraftScore) o;
            return this.scoreboard.equals(s.scoreboard) && this.score.equals(s.score);
        }
        return false;
    }
}
