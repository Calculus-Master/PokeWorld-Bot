package com.calculusmaster.pokecord.game.moves.builder;

import com.calculusmaster.pokecord.game.duel.component.GMaxDoTType;

public class GMaxDoTEffect extends MoveEffect
{
    private GMaxDoTType type;

    public GMaxDoTEffect(GMaxDoTType type)
    {
        this.type = type;
    }

    @Override
    public String get()
    {
        this.duel.gmaxDoT[this.duel.playerIndexFromUUID(this.opponent.getUUID())].addDoT(this.type);

        return "";
    }
}
