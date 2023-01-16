package com.calculusmaster.pokecord.game.duel.players;

import com.calculusmaster.pokecord.util.helpers.IDHelper;

public abstract class AIPlayer extends Player
{
    public AIPlayer()
    {
        super("BOT–" + IDHelper.numeric(6));
    }

    @Override
    public String getName()
    {
        return "";
    }
}
