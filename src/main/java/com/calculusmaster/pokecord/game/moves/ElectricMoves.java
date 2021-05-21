package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;

import java.util.Random;

public class ElectricMoves
{
    public String ElectricTerrain(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.electricTerrainActive = true;
        duel.electricTerrainTurns = 5;

        return user.getName() + " generated an Electric Field!";
    }

    public String ThunderShock(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        boolean paralyzed = new Random().nextInt(100) < 10;

        if(paralyzed) opponent.setStatusCondition(StatusCondition.PARALYZED);

        return Move.simpleDamageMove(user, opponent, duel, move) + (paralyzed ? " " + opponent.getName() + " is paralyzed!" : "");
    }

    public String ThunderWave(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        opponent.setStatusCondition(StatusCondition.PARALYZED);

        return opponent.getName() + " is paralyzed!";
    }
}
