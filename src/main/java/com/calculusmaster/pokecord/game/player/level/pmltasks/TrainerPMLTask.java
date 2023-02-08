package com.calculusmaster.pokecord.game.player.level.pmltasks;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;

public class TrainerPMLTask extends AbstractPMLTask
{
    private final int amount;

    public TrainerPMLTask(int amount)
    {
        super(LevelTaskType.TRAINER_DUELS);
        this.amount = amount;
    }

    @Override
    public boolean isCompleted(PlayerDataQuery p)
    {
        return p.getStatistics().get(PlayerStatistic.TRAINER_DUELS_WON) >= this.amount;
    }

    @Override
    public String getProgressOverview(PlayerDataQuery p)
    {
        return p.getStatistics().get(PlayerStatistic.TRAINER_DUELS_WON) + " / " + this.amount + " Trainers defeated";
    }
}
