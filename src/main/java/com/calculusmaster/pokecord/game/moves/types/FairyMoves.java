package com.calculusmaster.pokecord.game.moves.types;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.elements.Terrain;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.builder.MoveEffectBuilder;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

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

    public String AromaticMist(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.SPDEF, 1, 100, true)
                .execute();
    }

    public String FloralHealing(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFractionHealEffect(duel.terrain.equals(Terrain.GRASSY_TERRAIN) ? 2 / 3D : 1 / 2D)
                .execute();
    }

    public String CraftyShield(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(user.getUUID()).craftyShieldUsed = true;

        return user.getName() + " is protected from Status moves!";
    }

    public String FlowerShield(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        MoveEffectBuilder builder = MoveEffectBuilder.make(user, opponent, duel, move);

        if(user.isType(Type.GRASS))
            builder.addStatChangeEffect(Stat.DEF, 1, 100, true);

        if(opponent.isType(Type.GRASS))
            builder.addStatChangeEffect(Stat.DEF, 1, 100, false);

        if(!user.isType(Type.GRASS) && !opponent.isType(Type.GRASS))
            return move.getNothingResult();

        return builder.execute();
    }
}
