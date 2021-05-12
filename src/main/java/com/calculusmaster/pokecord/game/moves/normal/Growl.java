package com.calculusmaster.pokecord.game.moves.normal;

import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.moves.Move;

public class Growl extends Move
{
    public Growl()
    {
        super("Growl");
    }

    @Override
    public String logic(Pokemon user, Pokemon opponent)
    {
        opponent.changeIV(Stat.ATK, -1 * this.stageIV);
        return this.getMoveUseResults(user) + " It lowered " + opponent.getName() + "'s Attack stat!";
    }
}
