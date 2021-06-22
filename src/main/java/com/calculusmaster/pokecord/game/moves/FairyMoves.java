package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.moves.builder.MoveEffectBuilder;

public class FairyMoves
{
    public String Moonlight(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFractionHealEffect(switch(duel.weather) {
                    case CLEAR -> 1 / 2D;
                    case HARSH_SUNLIGHT -> 2 / 3D;
                    default -> 1 / 4D;
                })
                .execute();
    }

    public String Moonblast(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statChangeDamageMove(user, opponent, duel, move, Stat.SPATK, -1, 30, false);
    }

    public String DisarmingVoice(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String NaturesMadness(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFixedDamageEffect(opponent.getHealth() / 2)
                .execute();
    }

    public String StrangeSteam(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.CONFUSED, 20);
    }

    public String DrainingKiss(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addDamageHealEffect(3 / 4D)
                .execute();
    }
}
