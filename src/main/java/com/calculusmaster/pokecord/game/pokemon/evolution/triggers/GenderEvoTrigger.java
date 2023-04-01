package com.calculusmaster.pokecord.game.pokemon.evolution.triggers;

import com.calculusmaster.pokecord.game.enums.elements.Gender;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.util.Global;

public class GenderEvoTrigger implements EvolutionTrigger
{
    private final Gender gender;

    public GenderEvoTrigger(Gender gender)
    {
        this.gender = gender;
    }

    @Override
    public boolean canEvolve(Pokemon p, String serverID)
    {
        return p.getGender().equals(this.gender);
    }

    @Override
    public String getDescription()
    {
        return "Has a " + Global.normalize(this.gender.toString()) + " gender";
    }
}
