package com.calculusmaster.pokecord.game.moves.fighting;

import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.moves.Move;

public class BulkUp extends Move
{
    public BulkUp()
    {
        super("Bulk Up");
    }

    @Override
    public String logic(Pokemon user, Pokemon opponent)
    {
        user.changeIV(Stat.ATK, this.stageIV);
        user.changeIV(Stat.DEF, this.stageIV);

        return this.getMoveUseResults(user);
    }
}
