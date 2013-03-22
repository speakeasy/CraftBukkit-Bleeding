package org.bukkit.craftbukkit.scoreboard;

import net.minecraft.server.ScoreboardObjective;

import org.apache.commons.lang.Validate;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public final class CraftObjective implements Objective {

    private final Scoreboard scoreboard;
    private final ScoreboardObjective objective;
    private final Criteria criteria;

    CraftObjective(Scoreboard scoreboard, ScoreboardObjective objective) {
        this.scoreboard = scoreboard;
        this.objective = objective;
        this.criteria = Criteria.getCriteria(objective.getCriteria().getName());
    }

    public String getName() {
        return this.objective.getName();
    }

    public String getDisplayName() {
        return this.objective.getDisplayName();
    }

    public void setDisplayName(String displayName) {
        Validate.notNull(displayName, "Display name cannot be null");
        if (displayName.length() > 32) {
            throw new IllegalArgumentException("Display name cannot be longer than 32 characters");
        }
        this.objective.setDisplayName(displayName);
    }

    public Criteria getCriteria() {
        return this.criteria;
    }

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && (o instanceof CraftObjective)) {
            CraftObjective c = (CraftObjective) o;
            return this.scoreboard.equals(c.scoreboard) && this.objective.equals(c.objective);
        }
        return false;
    }

    ScoreboardObjective getHandle() {
        return this.objective;
    }
}
