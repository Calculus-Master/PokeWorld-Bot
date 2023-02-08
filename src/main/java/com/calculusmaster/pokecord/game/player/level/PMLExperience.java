package com.calculusmaster.pokecord.game.player.level;

public enum PMLExperience
{
    LEVEL_POKEMON(10),
    ACHIEVEMENT(15),

    //TODO: Determine what actually should be giving PML Exp (maybe Pokemon defeats, not duel completions?)
    DUEL_PVP(25),
    DUEL_RAID_MVP(150),
    DUEL_RAID_PARTICIPANT(50),
    DUEL_ELITE(100),
    DUEL_TRAINER_DAILY_COMPLETE(150),

    ;

    public int experience;

    PMLExperience(int experience)
    {
        this.experience = experience;
    }
}
