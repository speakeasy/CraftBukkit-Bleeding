package org.bukkit;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.Iterator;
import java.util.List;

import net.minecraft.server.StatisticList;

import org.bukkit.support.Util;
import org.junit.Test;

import com.google.common.collect.Lists;

public class StatisticTest {
    @Test
    @SuppressWarnings("unchecked")
    public void verifyMapping() throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
        List<Statistic> statistics = Lists.newArrayList(Statistic.values());
        List<Statistic> subStatistic = Lists.newArrayList();

        final int subStatisticId = org.bukkit.Statistic.MINE_BLOCK.getId();
        final int maxSubstatisticId = 0xFFFF;

        // Specialcase the statistics
        for (Iterator<Statistic> iter = statistics.iterator(); iter.hasNext();) {
            Statistic statistic = iter.next();
            if (statistic.isSubstatistic()) {
                iter.remove();
                subStatistic.add(statistic);
            }
        }

        for (net.minecraft.server.Statistic statistic : (List<net.minecraft.server.Statistic>) StatisticList.b) {
            int id = statistic.e;

            if ((id & Achievement.STATISTIC_OFFSET) == Achievement.STATISTIC_OFFSET) continue;

            // See if its substatistic -- mask out block/item id
            if ((id & subStatisticId) == subStatisticId) {
                id &= ~maxSubstatisticId;
            }

            Statistic subject = org.bukkit.Statistic.getById(id);

            String name = Util.getInternalState(net.minecraft.server.Statistic.class, statistic, "a");
            String message = String.format("org.bukkit.Statistic is missing id: %d [%s] named: '%s'", id, Integer.toHexString(id).toUpperCase(), name);

            assertNotNull(message, subject);

            statistics.remove(subject);
        }

        assertThat("org.bukkit.Statistic has too many statistics", statistics, hasSize(0));
    }
}
