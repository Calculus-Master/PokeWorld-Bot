package com.calculusmaster.pokecord.game.moves.normal;

import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.moves.Move;

public class Growth extends Move
{
    public Growth()
    {
        super("Growth");
    }

    @Override
    public String logic(Pokemon user, Pokemon opponent)
    {
        user.changeIV(Stat.ATK, this.stageIV);
        user.changeIV(Stat.SPATK, this.stageIV);

        return this.getMoveUseResults(user);
    }
}
