package com.calculusmaster.pokecord.game.player;

import com.calculusmaster.pokecord.game.player.leveltasks.AbstractLevelTask;
import com.calculusmaster.pokecord.game.player.leveltasks.ExperienceLevelTask;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerLevel
{
    public static final Map<Integer, LevelTaskHandler> LEVEL_REQUIREMENTS = new HashMap<>();

    public static void init()
    {
        registerNew(2)
                .setExp(100)
                .completeRegistry();

        registerNew(3)
                .setExp(250)
                .completeRegistry();
    }

    public static boolean canPlayerLevelUp(PlayerDataQuery p)
    {
        return LEVEL_REQUIREMENTS.get(p.getLevel() + 1).isPlayerReady(p);
    }

    private static LevelTaskHandler registerNew(int level)
    {
        return new LevelTaskHandler(level);
    }

    private static class LevelTaskHandler
    {
        private final List<AbstractLevelTask> tasks;
        private final int level;

        private LevelTaskHandler(int level)
        {
            this.tasks = new ArrayList<>();
            this.level = level;
        }

        private boolean isPlayerReady(PlayerDataQuery player)
        {
            return this.tasks.stream().allMatch(task -> task.isCompleted(player));
        }

        private LevelTaskHandler setExp(int exp)
        {
            return this.add(new ExperienceLevelTask(exp));
        }

        private LevelTaskHandler add(AbstractLevelTask task)
        {
            this.tasks.add(task);
            return this;
        }

        private void completeRegistry()
        {
            LEVEL_REQUIREMENTS.put(this.level, this);
        }
    }
}
