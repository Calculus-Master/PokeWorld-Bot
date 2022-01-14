package com.calculusmaster.pokecord.game.moves.builder;

import com.calculusmaster.pokecord.game.enums.elements.Type;

public class OHKODamageEffect extends FixedDamageEffect
{
    @Override
    public String get()
    {
        boolean immuneLevel = this.user.getLevel() < this.opponent.getLevel();

        boolean immuneFissure = this.opponent.isType(Type.FLYING);
        boolean immuneSheerCold = this.opponent.isType(Type.ICE);
        boolean immuneHornDrillGuillotine = this.opponent.isType(Type.GHOST);

        if(immuneLevel || immuneFissure || immuneSheerCold || immuneHornDrillGuillotine) return this.move.getNoEffectResult(this.opponent);
        else
        {
            this.set(this.opponent.getHealth());
            return "It's a One-Hit Knock Out!";
        }
    }
}
