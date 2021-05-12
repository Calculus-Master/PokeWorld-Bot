package com.calculusmaster.pokecord.game.moves.grass;

import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.moves.Move;

public class LeechSeed extends Move
{
    public LeechSeed()
    {
        super("Leech Seed");
        this.setCustom();
    }

    @Override
    public String logic(Pokemon user, Pokemon opponent)
    {
        if(!opponent.getType()[0].equals(Type.GRASS) && !opponent.getType()[1].equals(Type.GRASS))
        {
            int hpGain = opponent.getStat(Stat.HP) / 8;
            user.changeHealth(hpGain);
            opponent.changeHealth(hpGain * -1);
            return this.getMoveResults(user, opponent, hpGain) + user.getName() + " gained " + hpGain + " HP!";
        }
        else return this.getName() + " doesn't affect " + opponent.getName() + " !"; //TODO: Other methods for move results
    }
}
