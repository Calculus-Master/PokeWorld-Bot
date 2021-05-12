package com.calculusmaster.pokecord.game.moves.psychic;

import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.moves.Move;

public class CalmMind extends Move
{
    public CalmMind()
    {
        super("Calm Mind");
    }

    @Override
    public String logic(Pokemon user, Pokemon opponent)
    {
        user.changeIV(Stat.SPATK, this.stageIV);
        user.changeIV(Stat.SPDEF, this.stageIV);

        return this.getMoveUseResults(user);
    }
}
