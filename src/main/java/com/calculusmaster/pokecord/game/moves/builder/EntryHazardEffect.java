package com.calculusmaster.pokecord.game.moves.builder;

import com.calculusmaster.pokecord.game.duel.DuelHelper;

public class EntryHazardEffect extends MoveEffect
{
    private DuelHelper.EntryHazard hazard;

    public EntryHazardEffect(DuelHelper.EntryHazard hazard)
    {
        this.hazard = hazard;
    }

    @Override
    public String get()
    {
        this.duel.hazardData(this.opponent.getUUID()).addHazard(this.hazard);

        return this.user.getName() + " laid a " + this.move.getName() + " trap!";
    }
}
