package com.calculusmaster.pokecord.game.player.level.pmltasks;

import com.calculusmaster.pokecord.mongo.PlayerData;
import com.calculusmaster.pokecord.util.enums.StatisticType;

public class TrainerPMLTask extends AbstractPMLTask
{
    private final int amount;

    public TrainerPMLTask(int amount)
    {
        super(LevelTaskType.TRAINER_DUELS);
        this.amount = amount;
    }

    @Override
    public boolean isCompleted(PlayerData p)
    {
        return p.getStatistics().get(StatisticType.TRAINER_DUELS_WON) >= this.amount;
    }

    @Override
    public String getProgressOverview(PlayerData p)
    {
        return p.getStatistics().get(StatisticType.TRAINER_DUELS_WON) + " / " + this.amount + " Trainers defeated";
    }
}
