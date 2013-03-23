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
        criteria = Criteria.getCriteria(objective.getCriteria().getName());
    }

    public String getName() {
        return this.objective.getName();
    }

    public String getDisplayName() {
        return objective.getDisplayName();
    }

    public void setDisplayName(String displayName) {
        Validate.notNull(displayName, "Display name cannot be null");
        Validate.isTrue(displayName.length() <= 32, "Display name '" + displayName + "' is longer than the limit of 32 characters");
        objective.setDisplayName(displayName);
    }

    public Criteria getCriteria() {
        return criteria;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CraftObjective) {
            CraftObjective c = (CraftObjective) o;
            return scoreboard.equals(c.scoreboard) && objective.equals(c.objective);
        }
        return false;
    }

    ScoreboardObjective getHandle() {
        return objective;
    }
}
